package com.igormaznitsa.LogoEditor;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.io.IOException;

public class TextWindow extends Panel implements ItemListener,TextListener,ActionListener
{
    public static final int FONT_NUMBER=5;

    public static final String ACTION_OK="TEXT_SELECTED";

    protected static final int IMAGE_WIDTH = 72;
    protected static final int IMAGE_HEIGHT = 14;

    protected int _current_font;
    protected int [] _current_int_image;
    protected int _current_image_width;
    protected RFont [] _fonts;
    protected List _font_list;
    protected TextField _text_field;
    protected Button _okbutton;
    protected Image _img;
    protected PhonePreview _prev;
    protected Applet _apl;

    protected ActionListener _lstnr = null;

    public void setActionListener(ActionListener lstnr)
    {
        _lstnr = lstnr;
    }

    public RFont getCurrentFont()
    {
        return _fonts[_current_font];
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("OK"))
        {
            if (_lstnr!=null) _lstnr.actionPerformed(new ActionEvent(this,0,ACTION_OK));
        }
    }

    public int getCurrentImageWidth()
    {
        return _current_image_width;
    }

    public int [] getCurrentIntImage()
    {
        return _current_int_image;
    }

    public void textValueChanged(TextEvent e)
    {
        updateImage();
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange()!=ItemEvent.SELECTED) return;
        _current_font = ((Integer)e.getItem()).intValue();
        updateImage();
    }

    public synchronized void updateImage()
    {
        String _str = _text_field.getText().toUpperCase();
        RFont _rf = getCurrentFont();
        _current_image_width = _rf.getStringWidth(_str);

        _current_int_image = new int [_current_image_width*IMAGE_HEIGHT];

        _rf.drawString(_current_int_image,_str,0,0,_current_image_width);

        if (_current_image_width<=0)
        {
            _img = common.createImage(_apl,2,IMAGE_HEIGHT);
            Graphics gg = _img.getGraphics();
            gg.setColor(PhonePreview.COLOR_BACKGROUND);
            gg.fillRect(0,0,3,IMAGE_HEIGHT);
            gg.dispose();
        }
        else
        {
            _img = common.createImage(_apl,_current_image_width,IMAGE_HEIGHT);
        Graphics g = _img.getGraphics();
        for(int ly=0;ly<IMAGE_HEIGHT;ly++)
        {
            for(int lx=0;lx<_current_image_width;lx++)
            {
                if (_current_int_image[lx+ly*_current_image_width]!=0x00)
                {
                    g.setColor(PhonePreview.COLOR_FOREGROUND);
                }
                else
                {
                    g.setColor(PhonePreview.COLOR_BACKGROUND);
                }
                g.drawLine(lx,ly,lx,ly);
            }
        }
        }

        _prev.setImage(_img);
    }

    public TextWindow(Applet apl,Color color) throws IOException
    {
        super();
        setBackground(color);
        _apl = apl;
        _img = null;
        _prev = new PhonePreview(apl,new Rectangle(28,56,101,64),true);
        setFont(new Font("Systen",Font.BOLD,12));
        setLayout(new BorderLayout(5,5));
        _fonts = new RFont [FONT_NUMBER];

        _fonts[0] = new RFont(apl,"Simple font","1.gif");
        _fonts[1] = new RFont(apl,"Bold font","2.gif");
        _fonts[2] = new RFont(apl,"Techno font","3.gif");
        _fonts[3] = new RFont(apl,"Small bold font","4.gif");
        _fonts[4] = new RFont(apl,"Micro font","5.gif");


        _font_list = new List();

        for(int li=0;li<FONT_NUMBER;li++)
        {
            _font_list.addItem(_fonts[li].getFontName());
        }

        _font_list.select(0);
        _current_font = 0;
        _font_list.addItemListener(this);

        Panel _botpanel = new Panel();
        _botpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        _okbutton = new Button("   Ok   ");
        _okbutton.setActionCommand("OK");
        _okbutton.addActionListener(this);
        _botpanel.add(_okbutton);

        Panel _toppanel = new Panel();
        _toppanel.setLayout(new BorderLayout(0,0));

        Label _txtlabel = new Label("Text");
        _text_field = new TextField();

        _text_field.addTextListener(this);
        _toppanel.add("Center",_text_field);
        _toppanel.add("North",_txtlabel);

        Panel _leftpanel = new Panel();
        _leftpanel.setLayout(new BorderLayout(0,0));
        _txtlabel = new Label("Font");
        _leftpanel.add("North",_txtlabel);
        _leftpanel.add("Center",_font_list);

        Panel _centerpanel = new Panel();
        _centerpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        _centerpanel.add(_prev);


        _toppanel.setBackground(color);
        _leftpanel.setBackground(color);
        _botpanel.setBackground(color);
        _centerpanel.setBackground(color);

        add("North",_toppanel);
        add("West",_leftpanel);
        add("South",_botpanel);
        add("Center",_centerpanel);
    }
}
