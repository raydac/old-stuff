/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 29.03.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogoEditor;

import com.igormaznitsa.LogoEditor.graphics.BMPImage;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LogoEditor extends Applet implements Runnable, ActionListener
{
    protected Color _background_color = null;
    protected int _current_mode = MODE_NONE;

    protected static final String COMMAND_NEW_DOCUMENT = "#NEW_DOCUMENT#";
    protected static final int MODE_NONE = -1;
    protected static final int MODE_LOADING = 0;
    protected static final int MODE_EDITING = 1;
    protected static final int MODE_TEXT = 2;
    protected static final int MODE_SAVING = 3;

    protected REditorPanel _editor_panel;
    protected TextWindow _text_panel;
    protected LoadingPanel _load_panel;
    protected SavingPanel _save_panel;

    protected String _image_url = "";
    protected int _image_len = 0;
    protected String _send_command = "";
    protected String _ok_page = "";


    public void actionPerformed(ActionEvent e)
    {
        invalidate();
        String _s = e.getActionCommand();
        if (_s.equals(TextWindow.ACTION_OK))
        {
            _editor_panel._scroll_pane.setPattern(_text_panel.getCurrentIntImage(), _text_panel.getCurrentImageWidth(), 14);
            changeState(MODE_EDITING);
        }
        else if (_s.equals(REditorPanel.ACTION_TEXTSELECT))
        {
            changeState(MODE_TEXT);
            _text_panel.transferFocus();
        }
        else
        if (_s.equals(SavingPanel.ACTION_EDIT))
        {
            changeState(MODE_EDITING);
        }
        else
        if (_s.equals(REditorPanel.ACTION_SAVEIMAGE))
        {
            changeState(MODE_SAVING);
            _save_panel.savePart(this,_editor_panel.getImageArray(),_editor_panel.getImageWidth(),_editor_panel.getImageHeight(),_send_command,_ok_page);
        }
        validate();
        repaint();
    }

    public Insets getInsets()
    {
        return new Insets(2, 2, 2, 2);
    }

    public void init()
    {
        String backcolor = this.getParameter("background");
        if (backcolor == null) backcolor = "0xFFFFFF";
        _background_color = stringToColor(backcolor);
        setBackground(_background_color);

        _send_command = this.getParameter("sendcmnd");
        if (_send_command == null) _send_command = "";

        _ok_page = this.getParameter("okpage");
        if (_ok_page == null) _ok_page = "";

        _image_url = this.getParameter("image");
        if (_image_url == null) _image_url = "";

        String _tstr = this.getParameter("imagelen");
        if (_tstr == null) _tstr = "-1";
        try
        {
            _image_len = Integer.parseInt(_tstr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            _image_len = -1;
        }

        if (_image_url == null) _image_url = "";

        try
        {
            _editor_panel = new REditorPanel(this,_background_color);
            _text_panel = new TextWindow(this,_background_color);
            _load_panel = new LoadingPanel(this, _image_url, _image_len,_background_color);
            _save_panel = new SavingPanel(this, _send_command,_background_color);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            this.showStatus(ex.getMessage());
            return;
        }

        _editor_panel.setActionListener(this);
        _text_panel.setActionListener(this);
        _save_panel.setActionListener(this);

        setLayout(new BorderLayout());
        new Thread(this).start();
    }

    protected void changeState(int new_state)
    {
        removeAll();
        switch (new_state)
        {
            case MODE_LOADING:
                {
                    add("Center", _load_panel);
                }
                ;
                break;
            case MODE_EDITING:
                {
                    add("Center", _editor_panel);
                    _editor_panel._scroll_pane.setScrollPosition(0,0);
                }
                ;
                break;
            case MODE_SAVING:
                {
                    _save_panel.init();
                    add("Center", _save_panel);
                }
                ;
                break;
            case MODE_TEXT:
                {
                    add("Center", _text_panel);
                }
                ;
                break;
        }
        validate();
        repaint();
    }

    public void run()
    {
        int _wi = -1;
        int _hi = -1;

        changeState(MODE_LOADING);

        if (_image_url.length() > 0)
        {
            if (_image_url.startsWith(COMMAND_NEW_DOCUMENT))
            {
                String lstr = _image_url.substring(COMMAND_NEW_DOCUMENT.length(),COMMAND_NEW_DOCUMENT.length()+4);
                try
                {
                    _wi = Integer.parseInt(lstr);
                    lstr = _image_url.substring(COMMAND_NEW_DOCUMENT.length()+4,COMMAND_NEW_DOCUMENT.length()+8);
                    _hi = Integer.parseInt(lstr);
                }
                catch (NumberFormatException e)
                {
                    _load_panel.outErrorMessage("New document size error ["+_image_url+"]");
                }

                if ((_wi<=0) || (_hi<=0))
                {
                    _load_panel.outErrorMessage("New document size error ["+_wi+","+_hi+"]");
                    return;
                }

                _editor_panel.setImageSize(_wi,_hi);
            }
            else
            {
                byte[] _bte = _load_panel.loadUrl();
                if (_bte != null)
                {
                    BMPImage _bmpimg = new BMPImage();
                    try
                    {
                        _bmpimg.decode(_bte);
                    }
                    catch (IOException ex)
                    {
                        _load_panel.outErrorMessage("Error file...\r\n May be it is not a BMP file...");
                        return;
                    }
                    _editor_panel.loadImageFromArray(_bmpimg.biWidth, _bmpimg.biHeight, _bmpimg.image_array);
                }
                else
                {
                    return;
                }
            }
        }
        else
        {
            _load_panel.outErrorMessage("Image url is required but not provided!");
            return;
        }

        changeState(MODE_EDITING);

        while (true)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
    }

    private Color stringToColor(String paramValue)
    {
        int red;
        int green;
        int blue;

        red = (Integer.decode("0x" + paramValue.substring(0, 2))).intValue();
        green = (Integer.decode("0x" + paramValue.substring(2, 4))).intValue();
        blue = (Integer.decode("0x" + paramValue.substring(4, 6))).intValue();

        return new Color(red, green, blue);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        g.setColor(Color.black);
        g.draw3DRect(0, 0, getSize().width - 1, getSize().height - 1, false);

/*        switch (_current_mode)
        {
            case MODE_LOADING:
                _load_panel.repaint();
                break;
            case MODE_EDITING:
                _editor_panel.repaint();
                break;
            case MODE_TEXT:
                _text_panel.repaint();
                break;
            case MODE_SAVING:
                _save_panel.repaint();
                ;
                break;
        }*/

    }

}
