package com.igormaznitsa.midp;

import com.igormaznitsa.midp.Melody.Melody;
import com.igormaznitsa.midp.GameCanvas;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Interface descrbes a sound game block.
 */
public class GameSoundBlock
{
    protected Melody [] ap_melody;
    protected GameCanvas p_stub;

    public GameSoundBlock(GameCanvas _phonestub, String resource_name,GameCanvas loadListener) throws IOException
    {
        Runtime.getRuntime().gc();
        p_stub = _phonestub;
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);
        // Reading of melody number
        int num = ds.readUnsignedByte();

        ap_melody = new Melody[num];

        try
        {
            for (int li = 0; li < num; li++)
            {
                ap_melody[li] = new Melody(li,ds);

                // Reading length of image
                if (loadListener != null) loadListener.nextItemLoaded(1);
                Runtime.getRuntime().gc();
            }
            for (int li = 0; li < num; li++)
            {
                if (!_phonestub.convertMelody(ap_melody[li])) throw new IOException("I can't convert melody to native format:"+li);
            }
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                }
                dis = null;
            }
            ds = null;
            Runtime.getRuntime().gc();
        }
    }

    /**
     * Play a sound for id.
     * @param idSound the id of a sound
     * @param blocking flag of play blocking. If it is true, this function blocks the thread when the sound is played, else not...
     */
    public void playSound(int idSound,boolean blocking)
    {
        if (p_stub==null) return;
        p_stub.playMelody(ap_melody[idSound],blocking);
    }
 }
