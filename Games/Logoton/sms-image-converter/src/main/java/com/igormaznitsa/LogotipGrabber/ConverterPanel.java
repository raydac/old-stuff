/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

public class ConverterPanel extends Panel implements ActionListener, ItemListener
{
    protected static final int MAX_IMAGE_SIZE = 1500;
    protected static final int MIN_IMAGE_SIZE = 10;
    protected static final float ZOOM_STEP = 0.5f;

    protected RScroll _scrollpane;
    protected SelectPanel _selectpanel;

    protected static Color _backgroundColor = new Color(216, 232, 244);

    protected TopPanel _toppanel;
    protected PhonePanel _phonescreen;
    protected LogotipGrabber _apl;
    protected int _filtermethod = CONV_LINE_ART;
    protected float _increasing = 0;

    protected int _max_zoom = 0;
    protected int _min_zoom = 0;

    protected int[] grayarray = null;
    protected int _posterizevalue = 1;
    protected Image original_image = null;

    protected Color _backgroundphone = new Color(89, 200, 212);

    protected IconObject [] _icobjarray;

    public static final int CONV_LINE_ART = 0;
    public static final int CONV_HALFTONE = 1;
    public static final int CONV_DITHER = 2;
    public static final int CONV_DIFFUSE = 3;

    protected IconObject _logostate;
    protected IconObject _oldlogostate;


    public ConverterPanel(LogotipGrabber apl, Color backgroundcolor,IconObject [] _obj)
    {
        super();

        _icobjarray = _obj;

        _backgroundColor = backgroundcolor;

        _apl = apl;
        setBackground(_backgroundColor);

        BorderLayout brl = new BorderLayout(0, 0);
        setLayout(brl);
    }

    public void itemStateChanged(ItemEvent evt)
    {
        if (evt.getSource()instanceof Checkbox)
        {
        Checkbox box = (Checkbox) evt.getSource();
        if (box.getState())
        {
            String name = box.getName();
            if (name.equals("LINEART"))
                _filtermethod = CONV_LINE_ART;
            else if (name.equals("HALFTONE"))
                _filtermethod = CONV_HALFTONE;
            else if (name.equals("DITHER"))
                _filtermethod = CONV_DITHER;
            else if (name.equals("DIFFUSE")) _filtermethod = CONV_DIFFUSE;

            _changephoneimage();
        }
        }
        else
        if (evt.getSource() instanceof Choice)
        {
            if (evt.getStateChange()==ItemEvent.DESELECTED) return;
            String i = ((String)evt.getItem());

            IconObject _obj = null;

            for(int li=0;li<_icobjarray.length;li++ )
            {
                if (_icobjarray[li].getName().equals(i))
                {
                       _obj = _icobjarray [li];
                       break;
                }
            }
            _scrollpane.setMode(_obj);
            _oldlogostate = _logostate;
            _logostate = _obj;
        }
    }

    public void actionPerformed(ActionEvent evt)
    {
        String straction = evt.getActionCommand();

        if (straction.equals("imageselect"))
        {
            grayarray = Filters.ColorToGray(_scrollpane.getSelectImage());
            _changephoneimage();
            if (!_oldlogostate.equals(_logostate))
            {
                _phonescreen.fullRepaintPhoneScreen();
                _oldlogostate = _logostate;
            }
        }
        else if (straction.equals("CONTRAST"))
        {
            _posterizevalue = evt.getID();
            _changephoneimage();
        }
        else if (straction.equals("savepart"))
        {
            _apl.actionPerformed(new ActionEvent(this,0,"SAVE"));
        }
        else if (straction.equals("increase"))
        {
            grayarray = null;
            Image new_img = getZoomedImage(_increasing+ZOOM_STEP);
            if (new_img==null) return;
            _increasing+=ZOOM_STEP;
            if (_increasing>=_max_zoom) _toppanel.setZoomIncEnable(false);
            _scrollpane.setImage(new_img);
            _changephoneimage();
            if (_increasing>_min_zoom) _toppanel.setZoomDecEnable(true);
            _phonescreen.fullRepaintPhoneScreen();
        }
        else if (straction.equals("decrease"))
        {
            grayarray = null;
            Image new_img = getZoomedImage(_increasing-ZOOM_STEP);
            if (new_img==null) return;
            if (_increasing<=_min_zoom) _toppanel.setZoomDecEnable(false);
            _increasing-=ZOOM_STEP;
            _scrollpane.setImage(new_img);
            _changephoneimage();
            if (_increasing<_max_zoom) _toppanel.setZoomIncEnable(true);
            _phonescreen.fullRepaintPhoneScreen();
        }
    }

    public IconObject getCurrentState()
    {
        return _logostate;
    }

    protected  int[] convertImage(int [] arr)
    {
        int [] nimg = null;
        switch (_filtermethod)
        {
            case CONV_LINE_ART:
                nimg = Filters.filterLINEART(arr);
                break;
            case CONV_HALFTONE:
                nimg = Filters.filterHALFTONE(arr, _logostate.getWidth());
                break;
            case CONV_DITHER:
                nimg = Filters.filterDITHER(arr, _logostate.getWidth());
                break;
            case CONV_DIFFUSE:
                nimg = Filters.filterDIFFUSION(arr, _logostate.getWidth(), 128);
                break;
        }
        return nimg;
    }

    public int [] getConvertedImage()
    {
        if (grayarray == null) return null;
        int[] narr = Filters.contrastGray(grayarray, _posterizevalue);
        narr = convertImage(narr);
        return narr;
    }

    protected synchronized void _changephoneimage()
    {
        if ( getConvertedImage() == null)
        {
            _phonescreen.setPhoneImage(null);
            _phonescreen.repaintPhoneScreen();
        }
        else
        {
            Image nimg = Filters.convertBWArrayToImage(_apl, getConvertedImage(), _logostate.getWidth(), PhonePreview.COLOR_BACKGROUND, PhonePreview.COLOR_FOREGROUND);
            _phonescreen.setPhoneImage(nimg);
            _phonescreen.repaintPhoneScreen();
        }
    }

    public boolean action(Event evt, Object what)
    {
        return super.action(evt, what);
    }

    protected Image getZoomedImage(float factor)
    {
        float f = 1;
        if (factor>0) f = factor+1;
        else
        if (factor<0) f = 1f/(float)Math.abs(factor-1);
        int lw = (int)((float)original_image.getWidth(null)*f);
        int lh = (int)((float)original_image.getHeight(null)*f);

        Image newimg = original_image.getScaledInstance(lw,lh,Image.SCALE_SMOOTH);

        if (newimg!=null)
        {
            MediaTracker trckr = new MediaTracker(this);
            trckr.addImage(newimg,0);
            try
            {
                trckr.waitForID(0);
            }
            catch(InterruptedException ex)
            {
                return null;
            }
            trckr.removeImage(newimg);
            trckr = null;
        }

        return newimg;
    }

    public void init(LogotipGrabber apl, Image img)
    {
        original_image = img;

        int lwdt = img.getWidth(null);
        int lhgt = img.getHeight(null);

        _max_zoom = (Math.min(MAX_IMAGE_SIZE/lwdt,MAX_IMAGE_SIZE/lhgt)-1);
        _min_zoom = 0-(Math.min(lwdt/MIN_IMAGE_SIZE,lhgt/MIN_IMAGE_SIZE)-1);

        _increasing = 0;

        _scrollpane = new RScroll(apl, img,_icobjarray);

        _scrollpane.setBackground(_backgroundColor);
        _scrollpane.setForeground(_backgroundColor);

        _selectpanel = new SelectPanel(apl, this);

        try
        {
            _phonescreen = new PhonePanel(apl, this);
            _phonescreen.setBackground(getBackground());
            _toppanel = new TopPanel(apl, this,this,_icobjarray);
            _toppanel.setBackground(getBackground());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            apl.showStatus(ex.getMessage());
            return;
        }

        if (Math.max(lwdt,lhgt)>=MAX_IMAGE_SIZE) _toppanel.setZoomIncEnable(false);
        if (Math.min(lwdt,lhgt)<=MIN_IMAGE_SIZE) _toppanel.setZoomDecEnable(false);

        add("North", _toppanel);

        Panel editPanel = new Panel(new BorderLayout());
        editPanel.add("Center", _scrollpane);
        editPanel.add("South", _selectpanel);

        add("Center", editPanel);
        add("East", _phonescreen);

        _scrollpane.setActionListener(this);

        _scrollpane.setMode(_icobjarray[0]);
        _logostate = _icobjarray[0];
        _oldlogostate = _logostate;
    }

}
