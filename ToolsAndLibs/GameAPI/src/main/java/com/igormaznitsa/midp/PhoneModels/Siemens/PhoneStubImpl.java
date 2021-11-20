package com.igormaznitsa.midp.PhoneModels.Siemens;

import com.siemens.mp.game.*;
import com.igormaznitsa.midp.PhoneStub;
import com.igormaznitsa.midp.Melody.Melody;
import com.igormaznitsa.midp.Melody.MelodyEventListener;
import com.igormaznitsa.gameapi.Gamelet;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.IOException;

public class PhoneStubImpl extends Canvas implements PhoneStub
{
    public static final int KEY_MENU = -4;
    public static final int KEY_RIGHT = KEY_NUM6;
    public static final int KEY_LEFT = KEY_NUM4;
    public static final int KEY_UP = KEY_NUM2;
    public static final int KEY_DOWN = KEY_NUM8;
    public static final int KEY_FIRE = KEY_NUM5;
    public static final int KEY_CANCEL = -1;
    public static final int KEY_ACCEPT = -4;
    public static final int KEY_BACK = -12;

    private boolean lg_stopmelody;
    private Melody p_playedmelody;
    private Melody p_nextmelody;

    private MelodyEventListener p_melodyEventListener;
    private ExtendedImage p_doublebuffer;
    private Graphics p_doublebuffergraphics;
    private boolean lg_isShown;

    private int i_dbX;
    private int i_dbY;

    private Object p_semaphore = new Object();
    private AuxiliaryClass p_auxiliary;

    private class AuxiliaryClass
    {
        public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
        {
            boolean lg_ispackedimage = false;
            boolean lg_ismask = false;

            int i_cmndbyte = _inputstream.readUnsignedByte();

            if ((i_cmndbyte & 0x80) != 0) lg_ispackedimage = true;
            if ((i_cmndbyte & 0x20) != 0) lg_ismask = true;

            int i_width = _inputstream.readUnsignedByte();
            int i_height = _inputstream.readUnsignedByte();

            int i_calculatedlength = ((i_width + 7) >> 3) * i_height;

            if (lg_ismask) i_calculatedlength = ((i_width + 3) >> 2) * i_height;

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

            Image p_img = null;
            try
            {
                if (lg_ismask)
                {
                    p_img = com.siemens.mp.ui.Image.createTransparentImageFromBitmap(ab_imagearray, i_width, i_height);
                }
                else
                {
                    p_img = com.siemens.mp.ui.Image.createImageFromBitmap(ab_imagearray, i_width, i_height);
                }
            }
            catch (Exception ex)
            {
                throw new IOException(ex.getMessage());
            }

            return p_img;
        }

        public boolean convertMelody(Melody _melody)
        {
            try
            {
                byte[] arr = _melody.getMelodyArray();
                int i_delay = ((arr[0] & 0xFF) << 16) | ((arr[1] & 0xFF) << 8) | (arr[2] & 0xFF);
                int i_bpm = ((arr[3] & 0xFF) << 8) | (arr[4] & 0xFF);
                _melody.i_MelodyLength = i_delay;
                MelodyComposer p_melodycomposer = new MelodyComposer();
                p_melodycomposer.setBPM(i_bpm);
                for (int li = 5; li < arr.length;)
                {
                    int i_note = arr[li++] & 0xFF;
                    int i_duration = arr[li++] & 0xFF;
                    p_melodycomposer.appendNote(i_note, i_duration);
                }
                com.siemens.mp.game.Melody p_melody = p_melodycomposer.getMelody();
                _melody.p_NativeFormat = p_melody;
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
    }

    public PhoneStubImpl(MelodyEventListener _melodyEventListener)
    {
        p_melodyEventListener = _melodyEventListener;
        lg_isShown = true;
        new Thread(
                new Runnable()
                {
                    public void run()
                    {
                        while (true)
                        {
                            if (p_nextmelody != null)
                            {
                                if (lg_stopmelody)
                                {
                                    p_nextmelody = null;
                                    continue;
                                }
                                p_playedmelody = p_nextmelody;
                                p_nextmelody = null;
                            }
                            else
                            {
                                synchronized (p_semaphore)
                                {
                                    try
                                    {
                                        p_semaphore.wait();
                                        lg_stopmelody = false;
                                    }
                                    catch (InterruptedException e)
                                    {
                                        return;
                                    }
                                }
                                continue;
                            }

                            com.siemens.mp.game.Melody.stop();
                            lg_stopmelody = false;
                            int i_delay = p_playedmelody.i_MelodyLength;
                            com.siemens.mp.game.Melody p_melody = (com.siemens.mp.game.Melody) p_playedmelody.p_NativeFormat;
                            p_melody.play();
                            int i_finaltime = ((int) System.currentTimeMillis()) + i_delay;
                            while (i_finaltime > ((int) System.currentTimeMillis()) && !lg_stopmelody) Thread.yield();
                            if (lg_stopmelody) com.siemens.mp.game.Melody.stop();
                            if (!lg_stopmelody) if (p_melodyEventListener != null) p_melodyEventListener.melodyEnd(p_playedmelody.i_MelodyID);
                            p_playedmelody = null;
                        }
                    }
                }).start();

        p_auxiliary = new AuxiliaryClass();
    }

    public void releaseAuxiliaryModules()
    {
        p_auxiliary = null;
        Runtime.getRuntime().gc();
    }

    public void setScreenLight(boolean state)
    {
        if (state)
        {
            Light.setLightOn();
        }
        else
        {
            Light.setLightOff();
        }
    }

    public void activateVibrator(long duration)
    {
        Vibrator.triggerVibrator((int) duration);
    }

    public void playTone(int freq, int tone_time, boolean blocking)
    {
        Sound.playTone(freq, tone_time);
    }

    public void playMelody(Melody _melody, boolean _blocking)
    {
        if (_melody.getMelodyType() != Melody.MELODY_TYPE_OTT) return;
        int i_delay = _melody.i_MelodyLength;
        com.siemens.mp.game.Melody p_melody = (com.siemens.mp.game.Melody) _melody.p_NativeFormat;
        if (_blocking)
        {
            p_melody.play();
            int i_finaltime = ((int) System.currentTimeMillis()) + i_delay;
            lg_stopmelody = false;
            while (i_finaltime > ((int) System.currentTimeMillis()) && !lg_stopmelody) Thread.yield();
            if (!lg_stopmelody) if (p_melodyEventListener != null) p_melodyEventListener.melodyEnd(_melody.i_MelodyID);
        }
        else
        {
            lg_stopmelody = true;
            Thread.yield();
            p_nextmelody = _melody;

                synchronized (p_semaphore)
                {
                    p_semaphore.notify();
                }
        }
    }

    public void stopAllMelodies()
    {
        p_nextmelody = null;
        stopMelody(null);
    }

    public void stopMelody(Melody _melody)
    {
        lg_stopmelody = true;
    }

    public boolean convertMelody(Melody _melody)
    {
        return p_auxiliary.convertMelody(_melody);
    }

    public void initDoubleBuffer(int _width, int _height) throws IllegalArgumentException
    {
        Image p_dbf = Image.createImage(_width, _height);
        p_doublebuffer = new ExtendedImage(p_dbf);
        p_doublebuffergraphics = p_doublebuffer.getImage().getGraphics();
    }

    public Graphics getGraphicsForDoubleBuffer()
    {
        return p_doublebuffergraphics;
    }

    public boolean isShowEnabled()
    {
        return lg_isShown;
    }

    public void showNotify()
    {
        lg_isShown = true;
        paintDoubleBuffer(i_dbX,i_dbY);
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
            p_doublebuffer.blitToScreen(_x, _y);
        }
    }

    public final void paint(Graphics _graphics)
    {
        if (lg_isShown) p_doublebuffer.blitToScreen(i_dbX,i_dbY);
    }

    public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
    {
        return p_auxiliary.getImageFromInputStream(_inputstream);
    }
}
