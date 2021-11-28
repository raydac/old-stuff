/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import com.igormaznitsa.LogotipGrabber.ConverterPanel;
import com.igormaznitsa.LogotipGrabber.LoadingPanel;
import com.igormaznitsa.LogotipGrabber.REmpty;
import com.igormaznitsa.LogotipGrabber.SavingPanel;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class LogotipGrabber extends Applet implements Runnable,ActionListener
{
    protected static final int MODE_LOADING = 0;
    protected static final int MODE_EDITING = 1;
    protected static final int MODE_SAVING = 2;

    protected ConverterPanel _convpanel;
    protected LoadingPanel _loadingpanel;
    protected SavingPanel _savingpanel;

    protected String imageurl = null;
    protected int imagelength = -1;
    protected String _outurl=null;

    protected int _internal_state = -1;
    protected Color backclr = null;

    protected IconObject [] _mode_list = null;
    protected String _modestr;

    public void actionPerformed(ActionEvent e)
    {
        showStatus("");
        if (e.getActionCommand().equals("SAVE"))
        {
            final int [] image_array = _convpanel.getConvertedImage();
            if (image_array==null) return;

            invalidate();
            _savingpanel.init();
            remove(_convpanel);
            add("Center",_savingpanel);
            _internal_state = MODE_SAVING;
            validate();
            repaint();
            final Applet apl = this;
            new Thread(new Runnable(){
                public void run()
                {
                    _savingpanel.savePart(apl,image_array,_convpanel.getCurrentState(),_outurl);
                }
            }).start();
        }
        else
        if (e.getActionCommand().equals("EDIT"))
        {
            invalidate();
            remove(_savingpanel);
            add("Center",_convpanel);
            _internal_state = MODE_EDITING;
            validate();
            repaint();
        }
    }

    protected IconObject [] decodeModeList () throws IOException
    {

        try
        {
            if (_modestr==null) throw new IOException("The \"list\" parameter is not found");
            Vector vec = new Vector();
            StringTokenizer tkn = new StringTokenizer(_modestr,"-");
            while(tkn.hasMoreTokens())
            {
                String st = tkn.nextToken();
                vec.addElement(st);
            }
            tkn = null;
            IconObject [] _ioa = new IconObject [vec.size()];
            for(int li=0;li<vec.size();li++)
            {
                _ioa[li] = new IconObject((String) vec.elementAt(li));
            }

            return _ioa;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public void run()
    {
        REmpty br1 = new REmpty(2, 2);
        REmpty br2 = new REmpty(2, 2);
        REmpty br3 = new REmpty(2, 2);
        REmpty br4 = new REmpty(2, 2);

        add("North", br1);
        add("East", br2);
        add("South", br3);
        add("West", br4);

        add("Center", _loadingpanel);
        validate();

        if (_mode_list == null)
        {
            _loadingpanel.outErrorMessage("Error in the \"list\" parameter");
            return;
        }

        byte [] bte = _loadingpanel.loadUrl();

        if (bte!=null)
        {

            Image _nimg = null;

            _nimg = Toolkit.getDefaultToolkit().createImage(bte);

            MediaTracker trckr = new MediaTracker(this);
            trckr.addImage(_nimg,0);
            try
            {
                trckr.waitForID(0);
            }
            catch (InterruptedException e)
            {
                return;
            }

            if (_nimg.getWidth(null)<0)
            {
                _loadingpanel.outErrorMessage("Error file...\r\nThis is not GIF or JPEG format");
                return;
            }

            _convpanel.init(this, _nimg);

            remove(_loadingpanel);
            add("Center",_convpanel);

            _internal_state = MODE_EDITING;
            _loadingpanel = null;
            validate();
        }

        while(true)
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

    public void init()
    {
        String backcolor = this.getParameter("background");
        if (backcolor == null) backcolor = "FFFFFF";

        _modestr = this.getParameter("list");

        try
        {
            _mode_list  = decodeModeList();
        }
        catch(IOException exx)
        {
            exx.printStackTrace();
            _mode_list = null;
        }

        imageurl = this.getParameter("imageurl");
        String imglen = this.getParameter("imagelength");

        try
        {
            imagelength = Integer.parseInt(imglen);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            imagelength = -1;
        }

        _outurl = this.getParameter("outurl");

        Color backclr = stringToColor(backcolor);
        setBackground(backclr);
        setLayout(new BorderLayout(0, 0));

        _convpanel = new ConverterPanel(this, backclr,_mode_list);
        _loadingpanel = new LoadingPanel(this,imageurl,imagelength);
        _savingpanel = new SavingPanel(this,_outurl);

        _internal_state = MODE_SAVING;// LOADING;
        new Thread(this).start();
    }

    private Color stringToColor(String paramValue)
    {
        int red;
        int green;
        int blue;

        red = Integer.parseInt(paramValue.substring(0, 2),16);
        green = Integer.parseInt(paramValue.substring(2, 4),16);
        blue = Integer.parseInt(paramValue.substring(4, 6), 16);

        return new Color(red, green, blue);
    }

    public void paint(Graphics g)
    {
        g.setColor(Color.black);
        g.draw3DRect(0, 0, getSize().width - 1, getSize().height - 1, false);

        switch (_internal_state)
        {
            case MODE_EDITING:
                {
                    if (_convpanel != null) _convpanel.repaint();
                }
                ;
                break;
            case MODE_LOADING:
                {
                    if (_loadingpanel != null) _loadingpanel.repaint();
                }
                ;
                break;
            case MODE_SAVING:
                {
                    if (_savingpanel != null) _savingpanel.repaint();
                };break;
        }
    }
}
