package ru.coldcore.PixelFontMaker;

import javax.swing.*;
import javax.accessibility.Accessible;
import java.awt.*;

public class PreviewPanel extends JComponent implements Accessible
{
    protected FontContainer p_FontContainer;
    protected int i_ScaleFactor = 1;

    public void setScale(int _factor)
    {
        if (_factor<=0) i_ScaleFactor = 1;
        else
        i_ScaleFactor = _factor;
    }

    public PreviewPanel()
    {
        super();
        setDoubleBuffered(true);
    }

    public PreviewPanel(FontContainer _container)
    {
        super();
        setDoubleBuffered(true);
        p_FontContainer = _container;
    }

    public void setFontContainer(FontContainer _container)
    {
        p_FontContainer = _container;
        repaint();
    }

    public void paint(Graphics _g)
    {
        final int H_OFFSET = 5;
        final int V_OFFSET = 5;


        Rectangle p_rect = getBounds();

        if (p_FontContainer == null)
        {
            _g.setColor(Color.red);
            _g.fillRect(0, 0, p_rect.width, p_rect.height);
            return;
        }


        // отрисовываем шахматку
        final int CELL_WIDTH = 15;
        final int CELL_HEIGHT = 15;

        final Color p_color1 = p_FontContainer.p_Background;
        final Color p_color2 = p_FontContainer.p_Background.darker();

        boolean lg_color1 = true;

        for(int ly=0;ly<p_rect.height;ly+=CELL_HEIGHT)
        {
            boolean lg_curcolor = lg_color1;

            for(int lx=0;lx<p_rect.width;lx+=CELL_WIDTH)
            {
                _g.setColor(lg_curcolor ? p_color1 : p_color2);
                _g.fillRect(lx,ly,CELL_WIDTH,CELL_HEIGHT);
                lg_curcolor = !lg_curcolor;
            }

            lg_color1 = !lg_color1;
        }

        // отрисовываем ограничивающий прямоугольник
        Image p_img = p_FontContainer.getFontImage();
        if (p_img != null)
        {
            int i_w = p_img.getWidth(null)*i_ScaleFactor;
            int i_h = p_img.getHeight(null)*i_ScaleFactor;

            Color p_borderColor = new Color(p_FontContainer.p_Background.getRGB() ^ 0xFFFFFF);
            _g.setColor(p_borderColor);

            int i_borderW = i_w + 1;
            int i_borderH = i_h + 1;

            _g.drawRect(H_OFFSET, V_OFFSET, i_borderW, i_borderH);

            if (i_ScaleFactor<=1)
            {
                _g.drawImage(p_img, H_OFFSET + 1, V_OFFSET + 1, null);
            }
            else
            {
                _g.drawImage(p_img, H_OFFSET + 1,V_OFFSET + 1,i_w,i_h, null);
            }
        }

    }
}
