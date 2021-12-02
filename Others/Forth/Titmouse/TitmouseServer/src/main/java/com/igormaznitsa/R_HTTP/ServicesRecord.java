package com.igormaznitsa.R_HTTP;

import java.io.File;
import java.io.IOException;

public class ServicesRecord {
  public String Alias; // Наименование сервера
  public String HomeDir; // Домашний каталог
  public String DefaultPage; // Страница по умолчанию
  public int Port; // Номер порта
  public int MaxClCount; // Максимальное количество одновременных клиентов
  public R_HTTPServer server = null; // Указатель на связанный сервер
  public int Time_delay;
  public String StartScript;
  public String StopScript;

  public ServicesRecord(String alias, String homedir, String defaultpage, int port, int maxclcount, int tdelay, String startscr, String stopscr) throws IOException {
    File p_hdir = new File(homedir);
    if (p_hdir.exists() && p_hdir.isDirectory()) {
      Time_delay = tdelay;
      Alias = alias;
      HomeDir = p_hdir.getCanonicalPath();
      DefaultPage = defaultpage;
      Port = port;
      MaxClCount = maxclcount;
      StartScript = startscr;
      StopScript = stopscr;
    } else {
      throw new IOException("The server directory [" + homedir + " doesn't exist");
    }
  }
}
