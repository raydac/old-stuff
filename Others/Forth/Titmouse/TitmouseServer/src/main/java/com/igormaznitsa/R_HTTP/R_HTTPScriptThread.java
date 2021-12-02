package com.igormaznitsa.R_HTTP;

import java.util.Date;
import java.util.Vector;

public class R_HTTPScriptThread extends Thread {
  Vector ScriptVector;
  BusServerParams busp;

  public R_HTTPScriptThread(Vector sv, BusServerParams bsp) {
    busp = bsp;
    ScriptVector = sv;
    this.setPriority(Thread.NORM_PRIORITY);
    this.start();
  }

  public void run() {
    TimeScript ts = null;
    ScriptThread st;
    Date cur_date;
    int li;
    try {
      while (true) {
        this.sleep(1000);
        cur_date = new Date(System.currentTimeMillis());
        for (li = 0; li < ScriptVector.size(); li++) {
          ts = (TimeScript) ScriptVector.elementAt(li);
          ts.TimeCounter++;
          if (ts.TimeDelay > 0) {
            if (ts.TimeCounter > ts.TimeDelay) {
              ts.StartCounter++;
              ts.TimeCounter = 0;
              if (ts.script_thread != null)
                if (ts.script_thread.isAlive()) continue;
              st = new ScriptThread(ts, busp);
              ts.script_thread = st;
            }
          } else {
            if (ts.TimeCompare(cur_date)) {
              ts.StartCounter++;
              ts.TimeCounter = 0;
              if (ts.script_thread != null)
                if (ts.script_thread.isAlive()) continue;
              st = new ScriptThread(ts, busp);
              ts.script_thread = st;
            }
          }
        }
      }
    } catch (InterruptedException e) {
    }
  }
}
