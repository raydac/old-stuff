package com.igormaznitsa;

import com.igormaznitsa.RImage.RImage;
import com.igormaznitsa.RImage.RImageFont;
import com.igormaznitsa.R_HTTP.*;

import java.io.*;
import java.net.UnknownServiceException;
import java.util.*;

public class TitmouseServer {
  static String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

  static String server_version = "1.5"; // The server version
  static String fsp_version = "2.0a"; // The FSP version

  static String scripts_patch; // The path to the script directory

  /**
   * Out the DB aliases list
   */
  static void dbaliases_list(Hashtable al) {
    Enumeration ee = al.elements();
    System.out.println("\n---------------List of Database aliases----------------------------------------");
    while (ee.hasMoreElements()) {
      DataBaseRecord dbr = (DataBaseRecord) ee.nextElement();
      System.out.print(dbr.getDataBaseAlias() + "\t");
      System.out.print(dbr.getDataBaseURL() + "\t");
      System.out.print(dbr.getMaxConnectionCount() + "(" + dbr.getConnectionCount() + ")\t");
      System.out.println(dbr.getConnectionTimeout());
    }
    System.out.println("-------------------------------------------------------------------------------");
  }

  /**
   * Output the console command list
   */
  static void help() {
    System.out.println("\n---------------List of commands----------------------------------------");
    System.out.println("  ABOUT           About this server");
    System.out.println("  HELP            This help");
    System.out.println("  MEM             View free memory area");
    System.out.println("  CLOSE           Down server");
    System.out.println("  TRACEON         Enable trace client connections");
    System.out.println("  TRACEOFF        Disable trace client connections");
    System.out.println("  SERVERS         Output list of current servers");
    System.out.println("  SCRIPTS         Output list of system scripts");
    System.out.println("  DBALIASES       Output list of database aliases");
    System.out.println("  RUN             Execute external FSP script");
    System.out.println("  SUSPEND         Suspends a server");
    System.out.println("  RESUME          Resumes a suspended server");
    System.out.println("-----------------------------------------------------------------------\n");
  }

  // Start the script
  static void StartScriptWithName(String scriptname, BusServerParams bus) {
    ScriptForth tsf = null;
    try {
      tsf = new ScriptForth(null, null, bus, null);
      OutputStream los = tsf.PlayScript(scripts_patch + scriptname);
      if (los instanceof ScriptForthBuffer) {
        System.out.print(((ScriptForthBuffer) los).GetString());
        los.close();
      }
      los = null;
      tsf.close();
      tsf = null;
    } catch (Exception e) {
      if (tsf != null)
        try {
          tsf.close();
        } finally {
          tsf = null;
        }
      System.out.println("Error in the script \'" + scriptname + "\' [" + e.getMessage() + "]");
    }
  }

  // Pause a server
  static void SuspendServer(R_HTTPServer server) {
    if (server != null) {
      server.ServerSuspend();
    }
  }

  // Resume a server
  static void ResumeServer(R_HTTPServer server) {
    if (server != null) {
      server.ServerResume();
    }
  }

  /**
   * Close all working services
   */
  static void CloseServices(Hashtable services_vector) {
    int li;
    ServicesRecord sr;
    ScriptForth tsf = null;
    Enumeration lenm = services_vector.elements();
    while (lenm.hasMoreElements()) {
      sr = (ServicesRecord) lenm.nextElement();
      if (sr.server != null) {
        if (sr.server.isAlive()) sr.server.CloseServer();
        if (sr.StopScript != null) {
          try {
            tsf = new ScriptForth(null, null, sr.server.busp, sr.server.spar);
            tsf.PlayScript(scripts_patch + sr.StopScript);
            tsf.close();
            tsf = null;
          } catch (Exception e) {
            if (tsf != null)
              try {
                tsf.close();
              } finally {
                tsf = null;
              }
            System.out.println("Error in stop script for \'" + sr.Alias + "\' server [" + e.getMessage() + "]");
          }
        }
        sr.server.stop();
        sr.server = null;
      }
    }
    lenm = null;
    services_vector.clear();
  }


  static void LoadInitServerFile(String filename, Hashtable mimetable, Hashtable services, Hashtable dbaliases, Vector ascripts, Hashtable aaliases, BusServerParams bus, Vector StartBat, Vector StopBat) throws Exception {
    FileInputStream fis = null;
    DataInputStream dis = null;
    String ltmpstr, ltstr;
    int curtype = -1;

    bus.BackMailAddress = "titmouse@back.adr";
    bus.MailServerAddress = "127.0.0.1";
    bus.MailServerPort = 25;

    try {
      fis = new FileInputStream(filename);
      dis = new DataInputStream(fis);


      while (true) {
        ltmpstr = dis.readLine();
        if (ltmpstr == null) break;
        ltstr = ltmpstr;
        ltmpstr = ltmpstr.trim().toUpperCase();

        if (ltmpstr.length() == 0) {
          continue;
        } else if (ltmpstr.charAt(0) == '#') {
          continue;
        }
        if (ltmpstr.compareTo("[MIME]") == 0) {
          curtype = 0;
          continue;
        } else if (ltmpstr.compareTo("[SERVERS]") == 0) {
          curtype = 1;
          continue;
        } else if (ltmpstr.compareTo("[DBALIASES]") == 0) {
          curtype = 2;
          continue;
        }
        if (ltmpstr.compareTo("[SCRIPTS]") == 0) {
          curtype = 3;
          continue;
        } else if (ltmpstr.compareTo("[MAIL]") == 0) {
          curtype = 4;
          continue;
        } else if (ltmpstr.compareTo("[ALIASES]") == 0) {
          curtype = 5;
          continue;
        } else if (ltmpstr.compareTo("[SERVER_START]") == 0) {
          curtype = 6;
          continue;
        } else if (ltmpstr.compareTo("[SERVER_STOP]") == 0) {
          curtype = 7;
          continue;
        }

        switch (curtype) {
          case 0: {
            AddMIMEType(ltstr, mimetable);
          }
          ;
          break;
          case 1: {
            AddService(ltstr, services);
          }
          ;
          break;
          case 2: {
            AddDataBaseAlias(ltstr, dbaliases);
          }
          ;
          break;
          case 3: {
            AddScript(ltstr, ascripts, scripts_patch);
          }
          ;
          break;
          case 4: {
            SetMail(ltstr, bus);
          }
          ;
          break;
          case 5: {
            AddAlias(ltstr, aaliases);
          }
          ;
          break;
          case 6: {
            StartBat.addElement(ltstr);
          }
          ;
          break;
          case 7: {
            StopBat.addElement(ltstr);
          }
          ;
          break;
        }
      }
      try {
        fis.close();
        fis = null;
        dis = null;
      } catch (Exception ee) {
      }
    } catch (Exception e) {
      if (dis != null) dis.close();
      dis = null;
      if (fis != null) fis.close();
      fis = null;
      switch (curtype) {
        case 0:
          throw new Exception("Error in MIME section [" + e.getMessage() + "]");
        case 1:
          throw new Exception("Error in SERVERS section [" + e.getMessage() + "]");
        case 2:
          throw new Exception("Error in DBALIASES section [" + e.getMessage() + "]");
        case 3:
          throw new Exception("Error in SCRIPTS section [" + e.getMessage() + "]");
        case 4:
          throw new Exception("Error in MAIL section [" + e.getMessage() + "]");
        case 5:
          throw new Exception("Error in ALIASES section [" + e.getMessage() + "]");
        default:
          throw new Exception("Error during  server.ini loading");
      }
    }
  }

  /* Add alias to the alias list */
  static void AddAlias(String str, Hashtable aali) throws Exception {
    StringTokenizer strt;
    strt = new StringTokenizer(str, ",");
    String a_alias = strt.nextToken().trim().toUpperCase(); // Алиас
    String a_string = strt.nextToken().trim(); // Строка

    if (!aali.containsKey(a_alias)) {
      aali.put(a_alias, a_string);
    } else {
      throw new Exception("Duplicate alias '" + a_alias + "'");
    }
  }

  /* Set the email SMTP server */
  static void SetMail(String str, BusServerParams bus) throws Exception {
    StringTokenizer strt;
    String a_serveraddr, a_serverport, a_backaddr;
    strt = new StringTokenizer(str, ",");
    a_serveraddr = strt.nextToken().trim(); // Адрес почтового сервера
    a_serverport = strt.nextToken().trim(); // Порт сервера
    a_backaddr = strt.nextToken().trim(); // обратный почтовый адрес

    bus.BackMailAddress = a_backaddr;
    bus.MailServerAddress = a_serveraddr;

    try {
      bus.MailServerPort = new Integer(a_serverport).intValue();
    } catch (NumberFormatException e) {
      throw new Exception("Error port of mail server");
    }

  }

  /* Add a MIME type to the MIME table */
  static void AddMIMEType(String str, Hashtable htable) throws Exception {
    StringTokenizer strt;
    String a_ext, a_mime;
    strt = new StringTokenizer(str, ",");
    a_ext = strt.nextToken().toUpperCase().trim(); // расширение
    a_mime = strt.nextToken().trim(); // тип MIME
    if (!htable.containsKey(a_ext)) {
      htable.put(a_ext, a_mime);
    } else {
      throw new Exception("Duplicate file extension in " + a_ext);
    }
  }


  /* Add DB alias*/
  static void AddDataBaseAlias(String str, Hashtable htable) throws Exception {
    DataBaseRecord dbr = null;
    StringTokenizer strt;
    String db_url, db_alias;
    int max_con;
    long time_out;
    strt = new StringTokenizer(str, ",");
    db_alias = strt.nextToken().toUpperCase().trim(); // алиас
    db_url = strt.nextToken().trim(); // URL к базе
    max_con = Integer.valueOf(strt.nextToken()).intValue(); // максимальное количество коннектов
    time_out = Long.valueOf(strt.nextToken()).longValue(); // максимальное время ожидания коннекта
    if (!htable.containsKey(db_alias)) {
      dbr = new DataBaseRecord(db_alias, db_url, max_con, time_out);
      htable.put(db_alias, dbr);
      dbr = null;
    } else {
      throw new Exception("Duplicate alias " + db_alias);
    }
  }

  /* Add server to the server list */
  static void AddService(String str, Hashtable svector) throws Exception {
    R_HTTPScriptThread script_hread;
    ServicesRecord sr = null;
    StringTokenizer strt;
    String a_ls, a_srv, a_defp, a_port, a_maxc, a_tdelay, a_startscript, a_stopscript;
    int aport, amaxc;
    strt = new StringTokenizer(str, ",");
    a_ls = strt.nextToken(); // псевдоним
    a_srv = strt.nextToken(); // каталог сервера
    a_defp = strt.nextToken();// страница по умолчанию
    a_port = strt.nextToken();// номер порта
    a_maxc = strt.nextToken();// макс-е кол-во клиентов
    a_tdelay = strt.nextToken(); // время ожидания данных
    a_startscript = strt.nextToken().trim(); // Скрипт исполняемый перед запуском сервера
    a_stopscript = strt.nextToken().trim(); // Скрипт исполняемый после остановки сервера
    if (a_startscript.equalsIgnoreCase("NOT")) a_startscript = null;
    if (a_stopscript.equalsIgnoreCase("NOT")) a_stopscript = null;
    aport = new Integer(a_port).intValue();
    amaxc = new Integer(a_maxc).intValue();
    int tdelay = new Integer(a_tdelay).intValue();
    sr = new ServicesRecord(a_ls, a_srv, a_defp, aport, amaxc, tdelay, a_startscript, a_stopscript);
    a_ls = a_ls.trim().toUpperCase();
    if (!svector.containsKey(a_ls)) {
      Enumeration tenm = svector.elements();
      while (tenm.hasMoreElements()) {
        ServicesRecord tsr = (ServicesRecord) tenm.nextElement();
        if (tsr.Port == sr.Port) throw new Exception("Conflict port number in server " + a_ls);
      }
      svector.put(a_ls.trim().toUpperCase(), sr);
    } else {
      throw new Exception("Duplicated server name " + a_ls);
    }
  }

  // Load the file contains the list of scripts
  static void AddScript(String str, Vector svector, String pth) throws Exception {
    R_HTTPScriptThread script_hread;
    TimeScript ts = null;
    StringTokenizer strt;
    String scr_name, file_name;
    int time_delay;
    strt = new StringTokenizer(str, ",");
    scr_name = strt.nextToken();
    file_name = strt.nextToken();
    String tdl = strt.nextToken();
    strt = null;

    try {
      time_delay = new Integer(tdl).intValue();
      if (time_delay <= 0) throw new UnknownServiceException("Bad delay time");
      ts = new TimeScript(scr_name, pth, file_name, time_delay);
    } catch (NumberFormatException el) {
      StringTokenizer lstrt = new StringTokenizer(tdl, " :/");
      String lshour = lstrt.nextToken().trim();// часы
      String lsminut = lstrt.nextToken().trim();// минуты
      String lsweekday = lstrt.nextToken().trim().toUpperCase();// день недели
      String lsday = lstrt.nextToken().trim();// день
      String lsmon = lstrt.nextToken().trim();// месяц
      String lsyear = lstrt.nextToken().trim();// год

      lstrt = null;
      int lihour = -1;
      int liminut = 0;
      int liweekday = -1;
      int liday = -1;
      int limon = -1;
      int liyear = -1;

      if (lshour.compareTo("*") != 0) lihour = new Integer(lshour).intValue();
      if (lsminut.compareTo("*") != 0) liminut = new Integer(lsminut).intValue();
      {
        if (lsweekday.compareTo("SUN") == 0)
          liweekday = 0;
        else if (lsweekday.compareTo("MON") == 0)
          liweekday = 1;
        else if (lsweekday.compareTo("TUE") == 0)
          liweekday = 2;
        else if (lsweekday.compareTo("WED") == 0)
          liweekday = 3;
        else if (lsweekday.compareTo("THU") == 0)
          liweekday = 4;
        else if (lsweekday.compareTo("FRI") == 0)
          liweekday = 5;
        else if (lsweekday.compareTo("SAT") == 0)
          liweekday = 6;
        else if (lsweekday.compareTo("ALL") == 0)
          liweekday = -1;
        else
          throw new UnknownServiceException("Unknown weekday");
      }
      if (lsday.compareTo("*") != 0) liday = new Integer(lsday).intValue();
      if (lsmon.compareTo("*") != 0) limon = new Integer(lsmon).intValue();
      if (lsyear.compareTo("*") != 0) liyear = new Integer(lsyear).intValue();

      ts = new TimeScript(scr_name, pth, file_name, lihour, liminut, liday, liweekday, limon, liyear);
    }
    svector.addElement(ts);
  }


  /**
   * Выводит список зарегистрированных временных скриптов
   */
  static void ViewScriptList(Vector scv) {
    System.out.println("\n List of system scripts");
    System.out.println("-----------------------------------------------------------------------");
    for (int li = 0; li < scv.size(); li++) {
      TimeScript tim_sc = (TimeScript) scv.elementAt(li);
      System.out.print(tim_sc.ScriptName + "\t" + tim_sc.SimpleFileName + "\t");
      if (tim_sc.TimeDelay > 0) {
        System.out.print(tim_sc.TimeDelay + " sec\t");
      } else {
        if (tim_sc.HourOfStart < 0) System.out.print("*:");
        else System.out.print(tim_sc.HourOfStart + ":");
        if (tim_sc.MinuteOfStart < 0) System.out.print("* ");
        else System.out.print(tim_sc.MinuteOfStart + " ");
        if (tim_sc.WeekDayOfStart < 0) System.out.print("All ");
        else System.out.print(weekdays[tim_sc.WeekDayOfStart] + " ");
        if (tim_sc.DayOfStart < 0) System.out.print("*/");
        else System.out.print(tim_sc.DayOfStart + "/");
        if (tim_sc.MonathOfStart < 0) System.out.print("*/");
        else System.out.print(tim_sc.MonathOfStart + "/");
        if (tim_sc.YearOfStart < 0) System.out.print("*/\t");
        else System.out.print(tim_sc.YearOfStart + "\t");
      }
      if (tim_sc.script_thread != null) {
        if (tim_sc.script_thread.isAlive())
          System.out.println(" Active");
        else
          System.out.println(" Passive");
      } else
        System.out.println("Passive");
    }
    System.out.println("-----------------------------------------------------------------------\n");
  }


  /**
   * Выводит на экран табличку "О программе"
   */
  static void about() {
    System.out.println("");
    System.out.println("==============================================================================");
    System.out.println("    Titmouse Internet Server v " + server_version + " (with support Forth Server Pages v" + fsp_version + ")");
    System.out.println("==============================================================================");
    System.out.println("       Author: Igor A. Maznitsa");
    System.out.println("       E-Mail: igor.maznitsa@igormaznitsa.com");
    System.out.println("    Home page: http://www.igormaznitsa.com");
    System.out.println("==============================================================================");
    System.out.println("");
  }

  static void start_image() {
    Calendar clndr = Calendar.getInstance();
    System.out.println(". . . . . . . . . . . . . . . . . . . . . . . .||");
    System.out.println(" .*****.*.*****.*. .*. *** .*. .*. *** .*****. ||      11     5555555 ");
    System.out.println(". . * . . . * . **.** * . * * . * * . . * . . .||    1111     55      ");
    System.out.println(" . .*. .*. .*. .*.*.*.*. .*.*. .*. *** .***. . ||      11     55      ");
    System.out.println(". . * . * . * . * * * * . *.* . * . . * * . . .||      11     555555  ");
    System.out.println(" . .*. .*. .*. .*. .*. *** . *** . *** .*****. ||      11           5 ");
    System.out.println(". . . . . . . . . . . . . . . . . . . . . . . .||      11           5 ");
    System.out.println(" /---------------------------------------------//    111111 O 555555  ");
    System.out.println("//---------------------------------------------/");
    System.out.println("||");
    System.out.println("||                   Titmouse Internet Server v" + server_version);
    System.out.println("||               (with support Forth Server Pages v" + fsp_version + ")");
    System.out.println("||");
    System.out.println("||");
    System.out.println("||  Author    : Igor A. Maznitsa");
    System.out.println("||  Home Page : http://www.igormaznitsa.com");
    System.out.println("||  E-Mail    : igor.maznitsa@igormaznitsa.com\r\n");
    System.out.println("Today " + clndr.getTime());
  }

  static void ExecuteBAT(Vector cur_bat, BusServerParams rhttpbusobject, R_HTTPScriptThread script_thread, Vector ScriptVector) {
    for (int li = 0; li < cur_bat.size(); li++) {
      TranslateCommandString((String) cur_bat.elementAt(li), rhttpbusobject, script_thread, ScriptVector);
    }
    cur_bat.removeAllElements();
  }

  static boolean TranslateCommandString(String kbd_string, BusServerParams rhttpbusobject, R_HTTPScriptThread script_thread, Vector ScriptVector) {
    ServicesRecord ser_rec = null;
    try {
      if (kbd_string.compareTo("servers") == 0) {
        System.out.println("\nList of current servers");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("\tAlias\tHome directory\tDefault page\tPort\tMax clients");
        System.out.println("--------------------------------------------------------------------------");
        {
          Enumeration lenm = rhttpbusobject.RHTTPServers.elements();
          while (lenm.hasMoreElements()) {
            ser_rec = (ServicesRecord) lenm.nextElement();
            if (ser_rec.server == null)
              System.out.print("P\t");
            else {
              if (ser_rec.server.IsSuspend())
                System.out.print("S\t");
              else
                System.out.print("A\t");
            }
            System.out.print(ser_rec.Alias + "\t");
            System.out.print(ser_rec.HomeDir + "\t");
            System.out.print(ser_rec.DefaultPage + "\t");
            System.out.print(ser_rec.Port + "\t");
            System.out.println(ser_rec.MaxClCount + "(" + (ser_rec.server.client_group.activeCount() >> 1) + ")");
          }
          lenm = null;
        }
        System.out.println("--------------------------------------------------------------------------\n");
      } else if (kbd_string.compareTo("close") == 0) {
        if (script_thread != null) {
          if (script_thread.isAlive()) script_thread.stop();
          System.out.println("All Scripts have been closed");
        }
        CloseServices(rhttpbusobject.RHTTPServers);
        System.out.println("All Servers have been closed");
        System.out.println("---===[Titmouse Internet Server v" + server_version + " has been closed]===---");
        return false;
      } else if (kbd_string.startsWith("run ")) {
        StringTokenizer ltmpstrt = new StringTokenizer(kbd_string, " ");
        ltmpstrt.nextToken();
        StartScriptWithName(ltmpstrt.nextToken() + ".fsp", rhttpbusobject);
        ltmpstrt = null;
      } else if (kbd_string.startsWith("resume ")) {
        StringTokenizer ltmpstrt = new StringTokenizer(kbd_string, " ");
        ltmpstrt.nextToken();
        String ltserv = ltmpstrt.nextToken().trim().toUpperCase();
        if (rhttpbusobject.RHTTPServers.containsKey(ltserv)) {
          ServicesRecord lobj = (ServicesRecord) rhttpbusobject.RHTTPServers.get(ltserv);
          ResumeServer(lobj.server);
          lobj = null;
        } else {
          System.out.println("Server " + ltserv + " not found");
        }
        ltserv = null;
        ltmpstrt = null;
      } else if (kbd_string.startsWith("suspend ")) {
        StringTokenizer ltmpstrt = new StringTokenizer(kbd_string, " ");
        ltmpstrt.nextToken();
        String ltserv = ltmpstrt.nextToken().trim().toUpperCase();
        if (rhttpbusobject.RHTTPServers.containsKey(ltserv)) {
          ServicesRecord lobj = (ServicesRecord) rhttpbusobject.RHTTPServers.get(ltserv);
          SuspendServer(lobj.server);
          lobj = null;
        } else {
          System.out.println("Server " + ltserv + " not found");
        }
        ltmpstrt = null;
      } else if (kbd_string.compareTo("scripts") == 0) {
        ViewScriptList(ScriptVector);
      } else if (kbd_string.compareTo("about") == 0) {
        about();
      } else if (kbd_string.compareTo("dbaliases") == 0) {
        dbaliases_list(rhttpbusobject.dbaliases);
      } else if (kbd_string.compareTo("traceon") == 0) {
        Enumeration lenm = rhttpbusobject.RHTTPServers.elements();
        while (lenm.hasMoreElements()) {
          ((ServicesRecord) lenm.nextElement()).server.trace_client = true;
        }
        lenm = null;
      } else if (kbd_string.compareTo("traceoff") == 0) {
        Enumeration lenm = rhttpbusobject.RHTTPServers.elements();
        while (lenm.hasMoreElements()) {
          ((ServicesRecord) lenm.nextElement()).server.trace_client = false;
        }
        lenm = null;
      } else if (kbd_string.compareTo("help") == 0) {
        help();
      } else if (kbd_string.compareTo("mem") == 0) {
        long rt_tm = Runtime.getRuntime().freeMemory();
        System.out.println("Free memory : " + Long.toString(rt_tm / 1024) + " kBt");
      } else {
        System.out.println("Unknown instruction! [" + kbd_string + "]");
      }
    } catch (Exception e) {
    }
    ;
    return true;
  }


  public static void main(String[] args) {
    start_image();
    int lj;
    String server_dir = System.getProperty("user.dir"); // Содержит имя каталога из которого запущен сервер
    String kbd_string;
    Vector Servers_v;

    // Загрузка шрифта по умолчанию
    RImageFont DefaultFont = null;
    try {
      DefaultFont = new RImageFont(server_dir + File.separatorChar + "DEFAULT.RF");
    } catch (Exception e) {
      System.out.println("Error of loading default font!!! Fatal error!");
      System.exit(1);
    }

    //Создание типов MIME
    Hashtable MIMETypes = new Hashtable(10);// Хэш таблица содержит соответствие
    //Создание алиасов баз данных
    Hashtable DBAliases = new Hashtable(5); // Хэш таблица содержит алиасы БД и их URL
    //Создание списка скриптов
    Vector ScriptVector = new Vector(10, 2); // Вектор содержит скрипты  определенные в системе
    //Создание списка сервисов
    Hashtable ServVector = new Hashtable(10); // Вектор содержит виртуальные сервера

    //Создаем список псевдонимов ресурсов
    Hashtable AliasesTable = new Hashtable(10);

    // Вектор со сценарием старта сервера
    Vector StartBAT = new Vector(10);
    StartBAT.removeAllElements();

    // Вектор со сценарием остановки сервера
    Vector StopBAT = new Vector(10);
    StopBAT.removeAllElements();

    // Создаем шинный объект
    BusServerParams rhttpbusobject = new BusServerParams();
    rhttpbusobject.ServerVersion = "Titmouse Internet Server " + server_version;
    rhttpbusobject.dbaliases = DBAliases;
    rhttpbusobject.mtable = MIMETypes;
    rhttpbusobject.dfont = DefaultFont;
    rhttpbusobject.aliases = AliasesTable;

    scripts_patch = server_dir + File.separatorChar + "scripts" + File.separatorChar;

    try {
      LoadInitServerFile(server_dir + File.separatorChar + "SERVER.INI", MIMETypes, ServVector, DBAliases, ScriptVector, AliasesTable, rhttpbusobject, StartBAT, StopBAT);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }

    rhttpbusobject.RHTTPServers = ServVector;

    BufferedReader kbd_rd = new BufferedReader(new InputStreamReader(System.in));
    try {
      Compiler.enable();

      Compiler.compileClass(ScriptForth.class);
      Compiler.compileClass(R_HTTPBinResponse.class);
      Compiler.compileClass(R_HTTPClientThread.class);
      Compiler.compileClass(R_HTTPScriptThread.class);
      Compiler.compileClass(ScriptThread.class);
      Compiler.compileClass(R_HTTPServer.class);
      Compiler.compileClass(RImage.class);

      R_HTTPServer VarServer = null;
      ServicesRecord ser_rec;
      server_par spar = null;
      {
        Enumeration lenm = ServVector.elements();
        while (lenm.hasMoreElements()) {
          ser_rec = (ServicesRecord) lenm.nextElement();
          spar = new server_par();
          spar.default_page = ser_rec.DefaultPage;
          spar.home_dir = ser_rec.HomeDir;
          spar.server_port = new Integer(ser_rec.Port);
          spar.max_client_con = new Integer(ser_rec.MaxClCount);
          spar.Time_delay = ser_rec.Time_delay;
          spar.ServerParams = new GlobalServerParams();
          try {
            VarServer = new R_HTTPServer(ser_rec.Port, spar, rhttpbusobject);
            ser_rec.server = VarServer;
          } catch (Exception e) {
            System.out.println("Server \"" + ser_rec.Alias + "\" problem \"" + e.getMessage() + "\"");
          }
        }
        lenm = null;
        lenm = ServVector.elements();
        ScriptForth tsf = null;
        while (lenm.hasMoreElements()) {
          ser_rec = (ServicesRecord) lenm.nextElement();
          if (ser_rec.server == null) continue;
          if (ser_rec.StartScript != null) {
            try {
              tsf = new ScriptForth(null, null, rhttpbusobject, ser_rec.server.spar);
              tsf.PlayScript(scripts_patch + ser_rec.StartScript);
              tsf.close();
              tsf = null;
            } catch (Exception e) {
              try {
                if (tsf != null) tsf.close();
              } finally {
                tsf = null;
              }
              System.out.println("Error in start script of \'" + ser_rec.Alias + "\' server [" + e.getMessage() + "]");
            }
          }
          tsf = null;
          ser_rec.server.start();
          System.out.println("Server \"" + ser_rec.Alias + "\" has been started");
        }
        lenm = null;
      }


      R_HTTPScriptThread script_thread = new R_HTTPScriptThread(ScriptVector, rhttpbusobject);

      ExecuteBAT(StartBAT, rhttpbusobject, script_thread, ScriptVector);

      while (true) {
        try {
          System.out.print(">");
          kbd_string = kbd_rd.readLine().trim().toLowerCase();
          if (kbd_string.length() == 0) continue;
          //	Блок обработки клавиатурных команд
          if (!TranslateCommandString(kbd_string, rhttpbusobject, script_thread, ScriptVector)) break;
        } catch (Exception ej) {
          System.out.println("SYSTEM ERROR : " + ej.getMessage());
        }
      }

      ExecuteBAT(StopBAT, rhttpbusobject, script_thread, ScriptVector);
    } catch (Exception e) {
    }
  }
}