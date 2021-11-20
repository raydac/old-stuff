package com.igormaznitsa.midp;

import com.igormaznitsa.midp.Melody.Melody;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * Interface descrbes a sound game block.
 */
public class GameSoundBlock
{
    protected Melody[] ap_melody;
    protected PhoneStub p_stub;

    public GameSoundBlock(PhoneStub _phonestub,String resource_name) throws IOException
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
            for (int li = 0;li < num;li++)
            {
                ap_melody[li] = new Melody(li,ds);

                // Reading length of image
                Runtime.getRuntime().gc();
            }

            for (int li = 0;li < num;li++)
            {
                if (!_phonestub.convertMelody(ap_melody[li])) throw new IOException("I can't convert melody to native format:" + li);
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
        if (p_stub == null) return;
        p_stub.playMelody(ap_melody[idSound],blocking);
    }
}
