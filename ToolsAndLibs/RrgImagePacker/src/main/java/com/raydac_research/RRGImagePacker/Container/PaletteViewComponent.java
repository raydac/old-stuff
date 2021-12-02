package com.raydac_research.RRGImagePacker.Container;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class PaletteViewComponent extends JPanel
{
    protected PaletteCnt p_palete;

    private static final int ITEM_WIDTH = 10;
    private static final int ITEM_HEIGHT = 10;

    private PalettePropertiesPanel p_propPanel;

    public void setPaletteContainer(PaletteCnt _cnt)
    {
        removeAll();

        for(int li=0;li<_cnt.getLength();li++)
        {
            BufferedImage p_img = new BufferedImage(ITEM_WIDTH,ITEM_HEIGHT,BufferedImage.TYPE_INT_RGB);
            Graphics p_gr = p_img.getGraphics();

            Color p_color = _cnt.getColorAsCOLOR(li);
            p_gr.setColor(p_color);
            p_gr.fillRect(0,0,ITEM_WIDTH,ITEM_HEIGHT);

            JLabel p_jlabel = new JLabel(new ImageIcon(p_img));

            p_jlabel.setToolTipText("Index:"+li+" R:"+p_color.getRed()+" G:"+p_color.getGreen()+" B:"+p_color.getBlue());

            add(p_jlabel);
        }

        for(int li=_cnt.getLength();li<256;li++)
        {
            add(new JLabel());
        }
    }

    public PaletteViewComponent(PalettePropertiesPanel _parent)
    {
        super();
        p_propPanel = _parent;
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setLayout(new GridLayout(16,16,5,5));
    }
}
