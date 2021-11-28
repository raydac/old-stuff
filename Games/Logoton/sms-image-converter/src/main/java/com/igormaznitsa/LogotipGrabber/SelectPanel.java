/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ItemListener;

public class SelectPanel extends Panel
{
    protected MethodGroup _methodchooser;


    public SelectPanel(Applet apl, ItemListener lst)
    {
        super();
        setBackground(ConverterPanel._backgroundColor);
        setLayout(new BorderLayout());

        _methodchooser = new MethodGroup(lst);


        add("Center", _methodchooser);
    }
}
