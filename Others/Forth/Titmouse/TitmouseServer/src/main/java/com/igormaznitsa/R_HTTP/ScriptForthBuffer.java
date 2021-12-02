package com.igormaznitsa.R_HTTP;

import java.io.OutputStream;

public class ScriptForthBuffer extends OutputStream {
  public byte[] buffermem;
  public int len = 0;
  int incr = 0;

  public ScriptForthBuffer(int AStartSize, int increase) {
    buffermem = new byte[AStartSize];
    incr = increase;
    len = 0;
  }

  public int getLength() {
    return len;
  }

  public String GetString() {
    return new String(buffermem, 0, len);
  }

  public void clear() {
    len = 0;
  }

  public void close() {

  }

  public void flush() {

  }

  public void write(byte[] ABytes) {
    for (int li = 0; li < ABytes.length; li++) this.write(ABytes[li]);
  }

  public void write(byte[] ABytes, int offst, int lgt) {
    int lmax = offst + lgt;
    for (int li = offst; li < lmax; li++) this.write(ABytes[li]);
  }

  public void write(int AByte) {
    if (this.buffermem.length == len) {
      byte[] ltmp = new byte[this.buffermem.length + incr];
      System.arraycopy(this.buffermem, 0, ltmp, 0, len);
      this.buffermem = ltmp;
    }
    this.buffermem[len] = (byte) AByte;
    len++;
  }

}
