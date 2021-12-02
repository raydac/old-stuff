package com.igormaznitsa.RImage;

import java.io.FileInputStream;

public class RImagePalette {
  public byte[] Palette;

  public RImagePalette(String AFileName) throws Exception {
    Palette = new byte[768];
    FileInputStream fis = new FileInputStream(AFileName);
    if (fis.read(Palette, 0, 768) < 768) throw new Exception("Error length of the palette file!");
  }

  public RImagePalette() {
    Palette = new byte[768];
  }

  public void DefaultPalette() {
    //R				G				B
    Palette[0] = 0;
    Palette[1] = 0;
    Palette[2] = 0;            //	0
    Palette[3] = 127;
    Palette[4] = 0;
    Palette[5] = 0;            //	1
    Palette[6] = 0;
    Palette[7] = 127;
    Palette[8] = 0;            //	2
    Palette[9] = 127;
    Palette[10] = 127;
    Palette[11] = 0;        //	3
    Palette[12] = 0;
    Palette[13] = 0;
    Palette[14] = 127;        //	4
    Palette[15] = 127;
    Palette[16] = 0;
    Palette[17] = 127;    //	5
    Palette[18] = 0;
    Palette[19] = 127;
    Palette[20] = 127;    //	6
    Palette[21] = 127;
    Palette[22] = 127;
    Palette[23] = 127;    //	7
    Palette[24] = 0;
    Palette[25] = 0;
    Palette[26] = 0;        //	8
    Palette[27] = -1;
    Palette[28] = 0;
    Palette[29] = 0;        //	9
    Palette[30] = 0;
    Palette[31] = -1;
    Palette[32] = 0;        //	10
    Palette[33] = -1;
    Palette[34] = -1;
    Palette[35] = 0;        //	11
    Palette[36] = 0;
    Palette[37] = 0;
    Palette[38] = -1;        //	12
    Palette[39] = -1;
    Palette[40] = 0;
    Palette[41] = -1;        //	13
    Palette[42] = 0;
    Palette[43] = -1;
    Palette[44] = -1;        //	14
    Palette[45] = -1;
    Palette[46] = -1;
    Palette[47] = -1;        //	15
  }

  public int GetR(int Index) {
    return (int) Palette[Index * 3];
  }

  public int GetG(int Index) {
    return (int) Palette[Index * 3 + 1];
  }

  public int GetB(int Index) {
    return (int) Palette[Index * 3 + 2];
  }

  public void SetR(int Index, int Value) {
    Palette[Index * 3] = (byte) Value;
  }

  public void SetG(int Index, int Value) {
    Palette[Index * 3 + 1] = (byte) Value;
  }

  public void SetB(int Index, int Value) {
    Palette[Index * 3 + 2] = (byte) Value;
  }

  public void SetRGB(int Index, int ValueR, int ValueG, int ValueB) {
    //int int_1 = Index *3;
    Palette[Index++] = (byte) ValueR;
    Palette[Index++] = (byte) ValueG;
    Palette[Index] = (byte) ValueB;
  }

}
