package com.igormaznitsa.midp.PhoneModels.Nokia;

import com.igormaznitsa.midp.Melody.Melody;
import com.igormaznitsa.midp.Melody.MelodyEventListener;
import com.igormaznitsa.midp.PhoneStub;
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

public abstract class PhoneStubImpl extends FullCanvas implements PhoneStub, SoundListener
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

    protected MelodyEventListener p_melodyeventlistener;
    protected Melody p_playedmelody;
    protected boolean lg_blockingmelody;

    private boolean lg_isShown;

    public PhoneStubImpl(MelodyEventListener _melodyEventListener)
    {
        p_melodyeventlistener = _melodyEventListener;
        lg_isShown = false;
    }

    public int getPhoneSupportFlags()
    {
        return SUPPORT_LIGHT | SUPPORT_SOUND;// | SUPPORT_VIBRATION;
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
        //DeviceControl.startVibra(50, duration);
    }

    public void playTone(int freq, int tone_time, boolean blocking)
    {
        new Sound(freq, tone_time);
        if (blocking)
        {
            try
            {
                sleep(tone_time);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public void sleep(int duration) throws InterruptedException
    {
        Thread.sleep(duration);
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
                    if (!lg_blockingmelody && p_melodyeventlistener != null) p_melodyeventlistener.melodyEnd(p_melody.getMelodyID());
                    p_melody = null;
          }
    }

    public void setBackgroundImage(Image _background) throws IllegalArgumentException
    {
    }

    public void initDoubleBuffer(int _width, int _height, Image _background) throws IllegalArgumentException
    {
    }

    public Graphics getGraphicsForDoubleBuffer()
    {
        return null;
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

    public void paintDoubleBuffer(int _x, int _y)
    {
    }
/*
    public void paint(Graphics _graphics)
    {
    }
*/
    private int crc32(int crc, byte b)
    {
        final int CRC_POLY = 0xEDB88320;
        int i_crc = (crc ^ b) & 0xFF;

        for (int lj = 0; lj < 8; lj++)
        {
            if ((i_crc & 1) == 0)
            {
                i_crc >>>= 1;
            }
            else
            {
                i_crc = (i_crc >>> 1) ^ CRC_POLY;
            }

        }
        crc = i_crc ^ (crc >>> 8);

        return crc;
    }

    private int crc32(int crc, int b)
    {
        crc = crc32(crc, (byte) (b >>> 24));
        crc = crc32(crc, (byte) (b >>> 16));
        crc = crc32(crc, (byte) (b >>> 8));
        crc = crc32(crc, (byte) b);
        return crc;
    }

    public Image getImageFromInputStream(DataInputStream _inputstream) throws IOException
    {
        final int HEADER_IHDR = 0;
        final int HEADER_PLTE = 1;
        final int HEADER_IDAT = 2;

        // Reading length of image
        int len = _inputstream.readUnsignedShort();

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(len);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        p_daos.writeLong(0x89504E470D0A1A0Al);

        while (p_baos.size() < (len - 12))
        {
            int i_crc = 0;
            // reading chunk
            int i_chunk = _inputstream.readUnsignedByte();
            switch (i_chunk)
            {
                case HEADER_IHDR:
                    {
                        int i_width = _inputstream.readUnsignedByte();
                        int i_height = _inputstream.readUnsignedByte();
                        int i_flag = _inputstream.readUnsignedByte();

                        int i_bpp = i_flag >> 4;
                        int i_color = i_flag & 0x07;

                        i_crc = 0x575e51f5;

                        i_crc = crc32(i_crc, i_width);
                        i_crc = crc32(i_crc, i_height);
                        i_crc = crc32(i_crc, (byte) i_bpp);
                        i_crc = crc32(i_crc, (byte) i_color);

                        p_daos.writeLong(0x0000000D49484452l);
                        p_daos.writeInt(i_width);
                        p_daos.writeInt(i_height);

                        p_daos.writeByte(i_bpp);
                        p_daos.writeByte(i_color);

                        p_daos.writeShort(0x0);
                        i_crc = crc32(i_crc, (byte) 0);
                        i_crc = crc32(i_crc, (byte) 0);

                        if ((i_flag & 0x08) == 0)
                        {
                            p_daos.writeByte(0);
                            i_crc = crc32(i_crc, (byte) 0);
                        }
                        else
                        {
                            p_daos.writeByte(1);
                            i_crc = crc32(i_crc, (byte) 1);
                        }
                    }
                    ;
                    break;
                case HEADER_IDAT:
                    {
                        int i_len = _inputstream.readUnsignedByte();
                        if (i_len >= 0x80)
                        {
                            i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                        }
                        p_daos.writeInt(i_len);
                        p_daos.writeInt(0x49444154);

                        i_crc = 0xca50f9e1;


                        for (int li = 0; li < i_len; li++)
                        {
                            byte b_byte = (byte) _inputstream.read();
                            p_daos.writeByte(b_byte);
                            i_crc = crc32(i_crc, b_byte);
                        }
                    }
                    ;
                    break;
                case HEADER_PLTE:
                    {
                        int i_len = _inputstream.readUnsignedByte();
                        if (i_len >= 0x80)
                        {
                            i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                        }
                        p_daos.writeInt(i_len);
                        p_daos.writeInt(0x504C5445);

                        i_crc = 0xb45776aa;

                        for (int li = 0; li < i_len; li++)
                        {
                            byte b_byte = (byte) _inputstream.read();
                            p_daos.writeByte(b_byte);
                            i_crc = crc32(i_crc, b_byte);
                        }
                    }
                    ;
                    break;
            }
            p_daos.writeInt(i_crc ^ -1);
        }

        p_daos.writeLong(0x0000000049454E44);
        p_daos.writeInt(0xAE426082);
        p_daos.flush();
        p_daos = null;

        byte[] ab_bufferpngarray = p_baos.toByteArray();
        p_baos = null;

        Image p_newimage = Image.createImage(ab_bufferpngarray, 0, len);
        ab_bufferpngarray = null;
        return p_newimage;
    }

}
