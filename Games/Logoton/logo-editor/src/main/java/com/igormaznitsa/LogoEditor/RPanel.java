/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 02.04.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogoEditor;

import java.awt.*;

public class RPanel extends Panel
{
    public void update(Graphics g)
    {
    }


    public RPanel(LayoutManager mng)
    {
        super(mng);
    }


    public Insets getInsets()
    {
        return new Insets(2,4,2,4);
    }
}
