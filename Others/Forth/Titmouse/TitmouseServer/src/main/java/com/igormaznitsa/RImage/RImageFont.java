package com.igormaznitsa.RImage;

import java.io.FileInputStream;

public class RImageFont {
  int FontHeight; // Высота шрифта
  byte[] SymbolData; // Описание символов
  int[] SymbolOffset; // Смещение символов
  byte[] SymbolWidth; // Ширина символов
  int BytesPerLine; // Количество байт на одну линию символа

  public RImageFont(String FontFileName) throws Exception {
    SymbolOffset = new int[256];
    SymbolWidth = new byte[256];
    LoadFromFile(FontFileName);
  }

  protected int ReadInteger(FileInputStream dis) throws Exception {
    int lb0 = dis.read();
    int lb1 = dis.read();
    int lb2 = dis.read();
    int lb3 = dis.read();
    return ((lb3 << 24) | (lb2 << 16) | (lb1 << 8) | lb0);
  }

  public synchronized int getSymbolPixelWidth(int sym_code) // Получить ширину символа в пикселях
  {
    return (int) SymbolWidth[sym_code];
  }

  public int getSymbolOffset(int ch) {
    return SymbolOffset[ch];
  }

  public synchronized int getFontHeight() {
    return FontHeight;
  }

  public int getCharSizeInBytes() {
    return BytesPerLine * FontHeight;
  }

  public void DrawStringOnImage(RImage img, int X, int Y, int bcgrnd, String str) {
    byte[] strb = str.getBytes();
    int ch;
    for (int lcd = 0; lcd < strb.length; lcd++) {
      ch = strb[lcd];
      if (ch < 0) {
        ch = (Math.abs(ch) ^ 127) + 129;
      }
      int lpx = getSymbolPixelWidth(ch);
      int lchoffst = getSymbolOffset(ch);
      for (int ly = 0; ly < FontHeight; ly++) {
        for (int lx = 0; lx < BytesPerLine; lx++) {
          short lb = (short) SymbolData[lchoffst];
          int lmsk = 128;
          int ltx1 = lx << 3;
          int ltx = ltx1 + X;
          int lty = ly + Y;


          for (int lii = 0; lii < 8; lii++) {
            if ((ltx1 + lii) >= lpx) break;
            if ((lb & lmsk) != 0)
              img.MainSetPoint(lii + ltx, lty);
            else {
              if (bcgrnd >= 0) img.MainSetPointC(lii + ltx, lty, bcgrnd);
            }
            lmsk = lmsk >> 1;
          }
          lchoffst++;
        }
      }
      X += lpx;
      if (X >= img.GetImageWidth()) break;
    }
  }

  public void LoadFromFile(String AFileName) throws Exception {
    FileInputStream fis = new FileInputStream(AFileName);
    int fontfilesize = ReadInteger(fis); // сигнатуру

    if (fontfilesize != 0x544E4652) {
      fis.close();
      fis = null;
      throw new Exception("Unknown font format");
    }

    fontfilesize = ReadInteger(fis) - 1296;
    SymbolData = new byte[fontfilesize];
    BytesPerLine = ReadInteger(fis); // количество байт на одну строку
    FontHeight = ReadInteger(fis);
    for (int li = 0; li < 256; li++) // Считываем смещение символов
    {
      SymbolOffset[li] = ReadInteger(fis) - 1296;
    }

    for (int li = 0; li < 256; li++) {
      SymbolWidth[li] = (byte) fis.read();
    }

    for (int li = 0; li < SymbolData.length; li++) {
      SymbolData[li] = (byte) fis.read();
    }
    fis.close();
    fis = null;
  }

}
