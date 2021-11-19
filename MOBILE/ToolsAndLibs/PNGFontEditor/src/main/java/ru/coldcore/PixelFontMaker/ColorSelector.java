package ru.coldcore.PixelFontMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class ColorSelector extends JButton implements ActionListener
{
    protected Color p_Color;
    protected BufferedImage p_Image;

    public void actionPerformed(ActionEvent e)
    {
        Color p_newColor = JColorChooser.showDialog(this, "Select color", p_Color);
        if (p_newColor!=null)
        {
            setColor(p_newColor);
        }
    }

    public Color getColor()
    {
        return p_Color;
    }

    public void setColor(Color _color)
    {
        p_Color = _color;
        Graphics p_g = p_Image.getGraphics();
        p_g.setColor(_color);
        p_g.fill3DRect(0,0,p_Image.getWidth(),p_Image.getHeight(),true);
        repaint();
    }

    public ColorSelector()
    {
        super("");
        p_Image = new BufferedImage(40,10,BufferedImage.TYPE_INT_RGB);
        ImageIcon p_Icon = new ImageIcon(p_Image);
        setIcon(p_Icon);
        setColor(Color.red);
        addActionListener(this);
    }
}
