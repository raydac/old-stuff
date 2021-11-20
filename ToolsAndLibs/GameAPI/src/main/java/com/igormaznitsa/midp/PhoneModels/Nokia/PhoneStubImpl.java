package com.igormaznitsa.midp.PhoneModels.Nokia;

import com.igormaznitsa.midp.Melody.Melody;
import com.igormaznitsa.midp.Melody.MelodyEventListener;
import com.igormaznitsa.midp.PhoneStub;
import com.igormaznitsa.gameapi.Gamelet;
import com.nokia.mid.sound.Sound;
import com.nokia.mid.sound.SoundListener;
import com.nokia.mid.ui.DeviceControl;
import com.nokia.mid.ui.FullCanvas;
import com.nokia.mid.ui.DirectUtils;
import com.nokia.mid.ui.DirectGraphics;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.DataInputStream;
import java.io.IOException;

public class PhoneStubImpl extends FullCanvas implements SoundListener/*-*/,PhoneStub
{
    public static final int KEY_MENU = -7;
    public static final int KEY_RIGHT = KEY_NUM6;
    public static final int KEY_LEFT = KEY_NUM4;
    public static final int KEY_UP = KEY_NUM2;
    public static final int KEY_DOWN = KEY_NUM8;
    public static final int KEY_FIRE = KEY_NUM5;
    public static final int KEY_CANCEL = -6;
    public static final int KEY_ACCEPT = -7;
    public static final int KEY_BACK = -2;

    protected Hashtable p_hashsound;
    protected MelodyEventListener p_melodyeventlistener;

    private Image p_doublebuffer;
    private Image p_background;
    private int i_dbX;
    private int i_dbY;
    private boolean lg_isShown;
    private Object p_unregistersemaphor = new Object();

    public PhoneStubImpl(MelodyEventListener _melodyEventListener)
    {
        p_hashsound = new Hashtable(5);
        p_melodyeventlistener = _melodyEventListener;
        lg_isShown = true;
    }

    public void initDoubleBuffer(int _width, int _height) throws IllegalArgumentException
    {
    }

    public void releaseAuxiliaryModules()
    {
    }

    public void setScreenLight(boolean state)
    {
        if (state)
        {
            DeviceControl.setLights(0, 100);
        }
        else
        {
            DeviceControl.setLights(0, 0);
        }
    }

    public void activateVibrator(long duration)
    {
        DeviceControl.startVibra(50, duration);
    }

    public void playTone(int freq, int tone_time, boolean blocking)
    {
        new Sound(freq, tone_time);
        if (blocking)
        {
            try
            {
                Thread.sleep(tone_time);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public boolean convertMelody(Melody _melody)
    {
        int i_type = 0;
        switch (_melody.getMelodyType())
        {
            case Melody.MELODY_TYPE_OTT:
                i_type = Sound.FORMAT_TONE;
                break;
            case Melody.MELODY_TYPE_WAV:
                i_type = Sound.FORMAT_WAV;
                break;
        }
        try
        {
            Sound p_sound = new Sound(_melody.getMelodyArray(), i_type);
            p_sound.setSoundListener(this);
            _melody.p_NativeFormat = p_sound;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally
        {
            Runtime.getRuntime().gc();
        }
        return true;
    }

    private synchronized void registerMelody(Melody _melody)
    {
        Sound p_sound = (Sound) _melody.p_NativeFormat;
        if (p_hashsound.containsKey(p_sound))
        {
            p_sound.stop();
        }
        else
        {
            p_hashsound.put(p_sound, _melody);
        }
    }

    private void unregisterMelody(Sound _sound)
    {
        p_hashsound.remove(_sound);
    }

    public synchronized void playMelody(Melody _melody, boolean _blocking)
    {
        if (_blocking)
        {
            registerMelody(_melody);
            Sound p_sound = (Sound) _melody.p_NativeFormat;

            if (p_sound.getState() == Sound.SOUND_PLAYING)
            {
                p_sound.stop();
            }

            p_sound.play(1);
            try
            {
                synchronized (_melody)
                {
                    _melody.wait();
                }
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
        else
        {
            Sound p_sound = (Sound) _melody.p_NativeFormat;
            if (p_sound.getState() == Sound.SOUND_PLAYING)
            {
                synchronized (p_unregistersemaphor)
                {
                    p_sound.stop();
                    try
                    {
                        p_unregistersemaphor.wait();
                    }
                    catch (InterruptedException e)
                    {
                        return;
                    }
                }
            }
            registerMelody(_melody);
            p_sound.play(1);
        }
    }

    public void stopMelody(Melody _melody)
    {
        ((Sound) _melody.p_NativeFormat).stop();
    }

    public synchronized void stopAllMelodies()
    {
        Enumeration p_elem = p_hashsound.elements();
        synchronized (p_hashsound)
        {
            while (p_elem.hasMoreElements())
            {
                Melody p_melody = (Melody) p_elem.nextElement();
                ((Sound) p_melody.p_NativeFormat).stop();
            }
        }
    }

    public void soundStateChanged(Sound sound, int event)
    {
        Melody p_melody = (Melody) p_hashsound.get(sound);
        switch (event)
        {
            case Sound.SOUND_STOPPED:
//            case Sound.SOUND_UNINITIALIZED:
                {
                    unregisterMelody(sound);
                    synchronized (p_melody)
                    {
                        p_melody.notify();
                    }
                    p_melody.lg_MelodyPlayed = false;
                    if (p_melodyeventlistener != null) p_melodyeventlistener.melodyEnd(p_melody.i_MelodyID);

                    synchronized (p_unregistersemaphor)
                    {
                        p_unregistersemaphor.notify();
                    }
                }
                ;
                break;
        }
    }

    public void setBackgroundImage(Image _background) throws IllegalArgumentException
    {
        if (_background == null)
        {
            p_background = null;
        }
        else
        {
            p_background = _background;
        }
    }

    public void initDoubleBuffer(int _width, int _height, Image _background) throws IllegalArgumentException
    {
        p_doublebuffer = Image.createImage(_width, _height);
        setBackgroundImage(_background);
    }

    public Graphics getGraphicsForDoubleBuffer()
    {
        if (p_doublebuffer == null)
            return null;
        else
            return p_doublebuffer.getGraphics();
    }

    public void showNotify()
    {
        lg_isShown = true;
        repaint();
    }

    public boolean isShowEnabled()
    {
        return lg_isShown;
    }

    public void hideNotify()
    {
        lg_isShown = false;
        stopAllMelodies();
    }

    public void paintDoubleBuffer(int _x, int _y)
    {
        if (lg_isShown)

        {
            i_dbX = _x;
            i_dbY = _y;
            repaint();
        }
    }

    public final void paint(Graphics _graphics)
    {
        if (p_background != null) _graphics.drawImage(p_background, 0, 0, 0);
        if (p_doublebuffer != null) _graphics.drawImage(p_doublebuffer, i_dbX, i_dbY, 0);
    }

    /*public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
    {
        // Reading length of image
        int len = _inputstream.readUnsignedByte();
        if ((len & 0x80) != 0)
        {
            len = (((len & 0x7F) << 8) | _inputstream.readUnsignedByte()) + 100;
        }
        else
            len += 100;

        byte[] narr = new byte[len + 8];
        if (_inputstream.read(narr, 8, len) != len) throw new IOException("Error image length");

        narr[0] = (byte) 0x89;
        narr[1] = 0x50;
        narr[2] = 0x4E;
        narr[3] = 0x47;
        narr[4] = 0x0D;
        narr[5] = 0x0A;
        narr[6] = 0x1A;
        narr[7] = 0x0A;

        Image p_newimage = Image.createImage(narr, 0, narr.length);
        return p_newimage;
    } */

    public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
    {
        boolean lg_ispackedimage = false;
        boolean lg_ismask = false;
        boolean lg_packedmask = false;

        int i_cmndbyte = _inputstream.readUnsignedByte();

        if ((i_cmndbyte & 0x80) != 0) lg_ispackedimage = true;
        if ((i_cmndbyte & 0x20) != 0) lg_ismask = true;
        if ((i_cmndbyte & 0x40) != 0) lg_packedmask = true;

        int i_width = _inputstream.readUnsignedByte();
        int i_height = _inputstream.readUnsignedByte();

        int i_calculatedlength = (i_width * i_height + 7) >> 3;

        int i_len = -1;

        byte[] ab_imagearray = null;

        if (lg_ispackedimage)
        {
            i_len = _inputstream.readUnsignedByte();
            if ((i_len & 0x80) != 0)
            {
                i_len = (((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte());
            }
            ab_imagearray = new byte[i_len];
            _inputstream.readFully(ab_imagearray);
            ab_imagearray = Gamelet.RLEdecompress(ab_imagearray, i_calculatedlength);
        }
        else
        {
            ab_imagearray = new byte[i_calculatedlength];
            _inputstream.readFully(ab_imagearray);
        }

        byte[] ab_maskarray = null;
        if (lg_ismask)
        {
            if (lg_packedmask)
            {
                i_len = _inputstream.readUnsignedByte();
                if ((i_len & 0x80) != 0)
                {
                    i_len = (((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte());
                }
                ab_maskarray = new byte[i_len];
                _inputstream.readFully(ab_maskarray);
                ab_maskarray = Gamelet.RLEdecompress(ab_maskarray, i_calculatedlength);
            }
            else
            {
                ab_maskarray = new byte[i_calculatedlength];
                _inputstream.readFully(ab_maskarray);
            }
        }

        Image p_img = DirectUtils.createImage(i_width, i_height, 0);
        try
        {
            DirectUtils.getDirectGraphics(p_img.getGraphics()).drawPixels(ab_imagearray, ab_maskarray, 0, i_width, 0, 0, i_width, i_height, 0, DirectGraphics.TYPE_BYTE_1_GRAY);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }

        return p_img;
    }

}
