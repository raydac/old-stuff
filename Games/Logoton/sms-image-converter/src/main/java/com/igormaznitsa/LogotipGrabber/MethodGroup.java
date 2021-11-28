/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.awt.*;
import java.awt.event.ItemListener;

public class MethodGroup extends Panel
{
    protected CheckboxGroup _group;
    protected Scrollbar bar;

    public MethodGroup(ItemListener listener)
    {
        super();
        setLayout(new GridLayout(2, 2));
        _group = new CheckboxGroup();

        Checkbox chk = new Checkbox("Line Art", _group, true);
        chk.setName("LINEART");
        chk.addItemListener(listener);
        add(chk);

        chk = new Checkbox("Halftone", _group, false);
        chk.setName("HALFTONE");
        chk.addItemListener(listener);
        add(chk);

        chk = new Checkbox("Dither", _group, false);
        chk.setName("DITHER");
        chk.addItemListener(listener);
        add(chk);

        chk = new Checkbox("Diffuse", _group, false);
        chk.setName("DIFFUSE");
        chk.addItemListener(listener);
        add(chk);
    }

}
