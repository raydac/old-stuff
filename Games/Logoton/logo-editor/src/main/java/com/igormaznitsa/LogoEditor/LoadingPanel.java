/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class LoadingPanel extends Panel implements ActionListener
{
    protected Label _lbl;
    protected RStatusBar _bar;
    protected TextArea _text;
    protected Applet _apl;
    protected String _imageurl;
    protected int _imagesize;

    public byte[] getURLResourceAsByteArray(ActionListener lstnr, int actionlevel, Applet cls, String name, int maxsize) throws IOException
    {
        if (name==null) throw new IOException("Error the image URL");
        if (maxsize<=0) throw new IOException("Error length of the image");

        URL url = new URL(cls.getDocumentBase(), name);

        URLConnection con = url.openConnection();
        InputStream istr = con.getInputStream();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(maxsize);

        int lvlcount = 0;
        int byte_counter = 0;

        while (true)
        {
            int li = istr.read();
            if (li>=0)
            {
                bos.write(li);
                byte_counter++;
            }

            if(byte_counter==maxsize) break;

            if (li < 0)
            {
                if (byte_counter != maxsize) throw new IOException("Error file length [" + byte_counter + "]");

                if (lstnr != null)
                {
                    lstnr.actionPerformed(new ActionEvent(this, byte_counter, "END_IN_STREAM"));
                }
                break;
            }
            else
            {
                lvlcount++;
                if (lvlcount >= actionlevel)
                {
                    lstnr.actionPerformed(new ActionEvent(this, byte_counter, "IN_STREAM"));
                    lvlcount = 0;

                    /*try
                    {
                        Thread.sleep(500);

                    }
                    catch(InterruptedException ex) { return null;}*/

                }
            }
        }

        try
        {
            if (istr!=null) istr.close();
        }
        catch(IOException ex){}
        istr = null;

        return bos.toByteArray();
    }

    public void outErrorMessage(String text)
    {
        _bar.setForegroundColor(Color.red);
        _bar.setBackgroundColor(Color.black);
        _bar.repaint();
        _text.setText(text);
        _text.setVisible(true);
        validate();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("END_IN_STREAM"))
        {
            int val = e.getID();
            _bar.setForegroundColor(Color.green);
            _bar.setValue(val);
        }
        else
        if (e.getActionCommand().equals("IN_STREAM"))
        {
            int val = e.getID();
            _bar.setValue(val);
        }
    }

    public byte[] loadUrl()
    {
        byte [] hhh = null;
        try
        {
            hhh = getURLResourceAsByteArray(this, _imagesize / 10, _apl, _imageurl, _imagesize);
        }
        catch (IOException excpt)
        {
            outErrorMessage("I can't load your image :( \r\n" + excpt.getMessage());
            return null;
        }
        return hhh;
    }

    public LoadingPanel(Applet apl, String urlimage, int imagesize,Color backcolor)
    {
        super();
        setBackground(backcolor);
        _imageurl = urlimage;
        _imagesize = imagesize;
        _apl = apl;
        _lbl = new Label("The image is loading...");
        _lbl.setFont(new Font("System", Font.BOLD, 12));
        _lbl.setForeground(Color.black);

        _bar = new RStatusBar(Color.yellow, Color.white, Color.black, 0, imagesize - 1, 0, 20);

        _bar.setBackground(Color.yellow);

        _text = new TextArea();
        _text.setVisible(false);

        setLayout(new BorderLayout(3, 3));

        add("Center", _bar);
        add("North", _lbl);
        add("South", _text);
    }

}
