package com.igormaznitsa.R_HTTP;

public class DataBaseRecord {
  protected int ConnectionCount = 0;
  protected String DataBaseURL = "";
  protected String DataBaseAlias = "";
  protected int MaxConnectionCount = 0;
  protected long ConnectionTimeout = 0;

  public DataBaseRecord(String alias, String url, int max_connection_count, long connection_timeout) {
    ConnectionCount = 0;
    DataBaseURL = url;
    DataBaseAlias = alias;
    MaxConnectionCount = max_connection_count;
    ConnectionTimeout = connection_timeout;
  }

  public synchronized long getConnectionTimeout() {
    return ConnectionTimeout;
  }

  public synchronized String getDataBaseAlias() {
    return DataBaseAlias;
  }

  public synchronized String getDataBaseURL() {
    return DataBaseURL;
  }

  public synchronized int getMaxConnectionCount() {
    return MaxConnectionCount;
  }

  public synchronized int getConnectionCount() {
    return ConnectionCount;
  }

  public synchronized void AddConnection() {
    ConnectionCount++;
  }

  public synchronized void SubConnection() {
    if (ConnectionCount > 0) ConnectionCount--;
  }

  public synchronized boolean AccessDenied() {
    if (getMaxConnectionCount() == 0) return false;
    if (getConnectionCount() >= getMaxConnectionCount()) return true;
    else return false;
  }
}
