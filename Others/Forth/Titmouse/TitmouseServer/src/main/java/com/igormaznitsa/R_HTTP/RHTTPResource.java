package com.igormaznitsa.R_HTTP;

public class RHTTPResource {
  public long date;
  public String resource_name;

  public RHTTPResource(String res_name) {
    resource_name = res_name;
    date = System.currentTimeMillis();
  }

  public synchronized void SetTime() {
    date = System.currentTimeMillis();
  }
}