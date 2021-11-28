/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import com.igormaznitsa.LogotipGrabber.graphics.ImageConverter;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SavingPanel extends Panel implements ActionListener
{
    protected Panel _toppanel;
    protected Panel _botpanel;
    protected Button _cancelbutton;
    protected Label _lbl;
    protected RStatusBar _bar;
    protected TextArea _text;
    protected LogotipGrabber _apl;
    protected String _outurl;
    protected boolean savingflag;

    public void setErrorColorToBar()
    {
        _bar.setForegroundColor(Color.red);
        _bar.setBackgroundColor(Color.black);
        _bar.repaint();
    }

    protected void addTextLine(String txt)
    {
        _text.setText(_text.getText() + txt + "\r\n");
        _text.repaint();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("CANCEL"))
        {
            addTextLine("Operation canceled...");
            endSavingProcess(false);
        }
        else if (e.getActionCommand().equals("OK"))
        {
            _apl.actionPerformed(new ActionEvent(this, 0, "EDIT"));
        }
        else if (e.getActionCommand().equals("SAVEPROGRESS"))
        {
            _bar.setValue(e.getID());
        }
    }

    public void init()
    {
        _cancelbutton.setActionCommand("CANCEL");
        _cancelbutton.setLabel("Cancel");
        _text.setText("");
        _bar.setForegroundColor(Color.yellow);
        _bar.setBackgroundColor(Color.white);
        _bar.setValue(0);
    }

    public void savePart(Applet apl, int[] image, IconObject info, String outurl)
    {
        savingflag = true;

        int ltype = info.getType() ;
        int height = info.getHeight();
        int width = info.getWidth();

        try
        {
            if (outurl==null) throw new IOException("Output URL is not defined...");
            byte[] _part = createImageArray(apl, outurl, image, width, height);

            String elstr = "";
            if (outurl != null)
            {
                try
                {
                    StringTokenizer stk = new StringTokenizer(outurl, "?");
                    stk.nextToken();
                    elstr = stk.nextToken();
                }
                catch (NoSuchElementException exx)
                {
                    elstr = outurl;
                }
            }

            elstr=elstr+"&type="+ltype;

            sendURLByteArray(apl, "upl.asp?" + elstr, _part, this);
        }
        catch (IOException ex)
        {
            addTextLine("I can't save your picture");
            //addTextLine(ex.getMessage());
            endSavingProcess(false);
            return;
        }
        endSavingProcess(true);
    }

    public byte[] createImageArray(Applet apl, String savename, int[] image, int width, int height) throws IOException
    {
        byte[] imgBMP = ImageConverter.encodeBMPImage(image, width, height);
        byte[] imgGIF = ImageConverter.encodeGIFImage(image, width, height, false, null);
        byte[] imgOTB = ImageConverter.encodeOTBImage(image, width, height);

        String rzd = "\r\n";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);

        String uid_msg = "-----------------------------" + String.valueOf(System.currentTimeMillis());

        daos.writeBytes(uid_msg + rzd);
        daos.writeBytes("Content-Disposition: form-data; name=\"img1\"; filename=\"z:\\image.bmp\"\r\n");
        daos.writeBytes("Content-Type: image/bmp\r\n\r\n");
        daos.write(imgBMP);

        daos.writeBytes("\r\n" + uid_msg + "\r\n");
        daos.writeBytes("Content-Disposition: form-data; name=\"img2\"; filename=\"z:\\image.otb\"\r\n");
        daos.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
        daos.write(imgOTB);

        daos.writeBytes(rzd + uid_msg + rzd);
        daos.writeBytes("Content-Disposition: form-data; name=\"img3\"; filename=\"z:\\image.gif\"\r\n");
        daos.writeBytes("Content-Type: image/gif\r\n\r\n");
        daos.write(imgGIF);

        daos.writeBytes(rzd + uid_msg + "--\r\n");

        daos.flush();

        byte[] arr = baos.toByteArray();

        return arr;
    }

    protected void endSavingProcess(boolean success)
    {
        if (success)
        {
           _bar.setForeground(Color.green);
           addTextLine("You have saved your picture...All ok...");
        }
        else
        {
            setErrorColorToBar();
        }
        _cancelbutton.setLabel("Ok");
        _cancelbutton.setActionCommand("OK");
    }

    public void sendURLByteArray(Applet cls, String cmnd, byte[] arr, ActionListener lst) throws IOException
    {
        int stepaction = arr.length / 10;
        try
        {
            URL url = new URL(cls.getDocumentBase(), cmnd);

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Transfer-Encoding", "8BIT");
            connection.setRequestProperty("MIME-Version", "1.0");
            connection.setRequestProperty("Content-Type", "multipart/mixed");
            connection.setRequestProperty("Content-length", String.valueOf(arr.length));
            OutputStream ostr = connection.getOutputStream();

            int cll = 0;
            int byte_count = 0;
            for (int li = 0; li < arr.length; li++)
            {
                if (!savingflag)
                {
                    try
                    {
                        ostr.close();
                    }
                    catch(IOException ex){}
                    return;
                }
                ostr.write(arr[li]);
                byte_count++;

                cll++;
                if (cll >= stepaction)
                {
                    if (lst != null) lst.actionPerformed(new ActionEvent(this, byte_count, "SAVEPROGRESS"));
                    cll = 0;
                }
            }

            DataInputStream inStream = new DataInputStream(connection.getInputStream());

            while (inStream.readLine() != null)
            {
                /* System.out.println(inputLine); */
            }
            ostr.close();
            inStream.close();
        }
        catch (MalformedURLException me)
        {
            throw new IOException("Error URL for saving");
        }
    }

    public SavingPanel(LogotipGrabber apl, String outurl)
    {
        super();
        _outurl = outurl;
        _apl = apl;
        _lbl = new Label("The image is saving...");
        _lbl.setFont(new Font("System", Font.BOLD, 12));
        _lbl.setForeground(Color.black);

        _bar = new RStatusBar(Color.yellow, Color.white, Color.black, 0, 10, 0, 10);

        _bar.setBackground(Color.yellow);

        _text = new TextArea();

        setLayout(new BorderLayout(3, 3));

        _toppanel = new Panel();
        _toppanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        _toppanel.add(_lbl);
        _toppanel.add(_bar);
        add("North", _toppanel);

        _cancelbutton = new Button("Cancel");
        _cancelbutton.addActionListener(this);

        _botpanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        _botpanel.add(_cancelbutton);

        init();

        add("South", _botpanel);
        add("Center", _text);
    }
}
