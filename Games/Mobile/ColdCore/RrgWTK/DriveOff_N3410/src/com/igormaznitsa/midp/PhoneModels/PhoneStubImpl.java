package com.igormaznitsa.midp.PhoneModels;

import com.igormaznitsa.midp.Melody.Melody;
//import com.igormaznitsa.midp.Melody.MelodyEventListener;
//import com.igormaznitsa.midp.PhoneStub;
import com.nokia.mid.sound.Sound;
import com.nokia.mid.sound.SoundListener;
import com.nokia.mid.ui.DeviceControl;
import com.nokia.mid.ui.FullCanvas;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public abstract class PhoneStubImpl extends FullCanvas implements /*PhoneStub,*/ SoundListener
{
    public static final int KEY_MENU = -7;
    public static final int KEY_RIGHT = KEY_NUM6;
    public static final int KEY_LEFT = KEY_NUM4;
    public static final int KEY_UP = KEY_NUM2;
    public static final int KEY_DOWN = KEY_NUM8;
    public static final int KEY_FIRE = KEY_NUM5;
    public static final int KEY_CANCEL = -6;
    public static final int KEY_ACCEPT = -7;
    public static final int KEY_BACK = -10;

//    private MelodyEventListener p_melodyeventlistener;
    private Melody p_playedmelody;
    private boolean lg_blockingmelody;

    private boolean lg_isShown;

    public PhoneStubImpl(Object obj/*MelodyEventListener _melodyEventListener*/)
    {
//        p_melodyeventlistener = _melodyEventListener;
        lg_isShown = false;
    }


    public void setScreenLight(boolean state)
    {
            DeviceControl.setLights(0, state?100:0);
    }

    public void activateVibrator(long duration)
    {
        //DeviceControl.startVibra(50, duration);
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
        i_type = Sound.FORMAT_TONE;
        try
        {
            Sound p_sound = new Sound(_melody.getMelodyArray(), i_type);
            p_sound.setSoundListener(this);
            _melody.setNativeObject(p_sound);
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

    public void playMelody(Melody _melody, boolean _blocking)
    {
        if (_blocking)
        {
            stopAllMelodies();
            Sound p_sound = (Sound) _melody.getNativeObject();
            p_playedmelody = _melody;
            lg_blockingmelody = true;
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
            p_playedmelody = null;
        }
        else
        {
            if (p_playedmelody != null)
            {
                stopAllMelodies();
            }
            p_playedmelody = _melody;
            lg_blockingmelody = false;
            Sound p_sound = (Sound) _melody.getNativeObject();
            p_sound.play(1);
        }
    }

    public void stopMelody(Melody _melody)
    {
        if (p_playedmelody.equals(_melody)) p_playedmelody = null;
        ((Sound) _melody.getNativeObject()).stop();
    }

    public void stopAllMelodies()
    {
        if (p_playedmelody != null)
        {
            ((com.nokia.mid.sound.Sound) p_playedmelody.getNativeObject()).stop();
            p_playedmelody = null;
        }
    }

    public void soundStateChanged(Sound sound, int event)
    {
        Melody p_melody = p_playedmelody;
        if (p_melody == null) return;

        if (event == com.nokia.mid.sound.Sound.SOUND_STOPPED)
          {
                    if (lg_blockingmelody)
                    {
                        synchronized (p_melody)
                        {
                            p_melody.notify();
                        }
                    }
                  //  if (!lg_blockingmelody && p_melodyeventlistener != null) p_melodyeventlistener.melodyEnd(p_melody.getMelodyID());
                    p_melody = null;
          }
    }


    public void showNotify()
    {
        lg_isShown = true;
//	repaint();
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

}
