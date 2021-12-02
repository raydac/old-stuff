package com.igormaznitsa.R_HTTP;

// Класс содержит таблицу глобальных параметров для каждого сервера

import java.util.Hashtable;

public class GlobalServerParams {
  Hashtable ParamsContainer; // Контейнер параметров

  public GlobalServerParams() {
    ParamsContainer = new Hashtable(5);
  }

  public synchronized boolean ContainsParameter(String parname) {
    if (ParamsContainer.containsKey(parname.trim().toUpperCase())) return true;
    else return false;
  }

  public synchronized void SetStringParameter(String parname, String parvalue) throws Exception {
    String lstr = parname.trim().toUpperCase();
    if (ContainsParameter(parname)) {
      if (ParamsContainer.get(lstr) instanceof Long) throw new Exception("Type mismatch in parameter \'" + lstr + "\'");
    }
    ParamsContainer.put(lstr, new Long(parvalue));
  }

  public synchronized String GetStringParameter(String parname) throws Exception {
    Object lobj = this.ParamsContainer.get(parname.trim().toUpperCase());
    if (lobj == null) throw new Exception("Server variable \'" + parname + "\' not found");
    if (lobj instanceof java.lang.String)
      return (String) lobj;
    else
      throw new Exception("Server variable \'" + parname + "\' is string type");
  }

  public synchronized void SetLongParameter(String parname, long parvalue) throws Exception {
    String lstr = parname.trim().toUpperCase();
    if (ContainsParameter(lstr)) {
      if (ParamsContainer.get(lstr) instanceof String)
        throw new Exception("Type mismatch in parameter \'" + lstr + "\'");
    }
    ParamsContainer.put(lstr, new Long(parvalue));
  }

  public synchronized long GetLongParameter(String parname) throws Exception {
    Object lobj = this.ParamsContainer.get(parname.trim().toUpperCase());
    if (lobj == null) throw new Exception("Server variable \'" + parname + "\' not found");
    if (lobj instanceof java.lang.Long)
      return ((Long) lobj).longValue();
    else
      throw new Exception("Server variable \'" + parname + "\' is number type");
  }

  public void finalize() {
    if (ParamsContainer != null) {
      ParamsContainer.clear();
    }
    ParamsContainer = null;
  }
}
