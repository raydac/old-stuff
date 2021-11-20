import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataInputStream;

/*
 * Copyright © 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This class describes a melody
 */
public class Melody
{
    public static final int MELODY_TYPE_OTT = 0;
    public static final int MELODY_TYPE_WAV = 1;
    public static final int MELODY_TYPE_MIDI = 2;
    public static final int MELODY_TYPE_MP3 = 3;
    public static final int MELODY_TYPE_MMF = 4;
    public static final int MELODY_TYPE_AMR = 5;

    public byte[] ab_arr;
    public int i_MelodyType;
    public Object p_NativeFormat;
    public int i_MelodyID;
    public int i_MelodyLength;
    public boolean lg_MelodyPlayed;
    public String s_mediaType;

    public Melody(int _indx,int _type,byte[] _arr)
    {
        i_MelodyID = _indx;
        i_MelodyType = _type;
        ab_arr = _arr;
        p_NativeFormat = null;
        lg_MelodyPlayed = false;
    }

    public Melody(int _id,DataInputStream _inputStream) throws IOException
    {
        i_MelodyID = _id;
        i_MelodyType = _inputStream.readUnsignedByte();

        switch(i_MelodyType)
        {
            case MELODY_TYPE_MIDI : {s_mediaType = "audio/x-mid";};break;
            case MELODY_TYPE_WAV  : {s_mediaType = "audio/x-wav";};break;
            case MELODY_TYPE_OTT  : {s_mediaType = "audio/x-tone-seq";};break;
            case MELODY_TYPE_MP3  : {s_mediaType = "audio/mpeg";};break;
            case MELODY_TYPE_MMF  : {s_mediaType = "application/vnd.smaf";};break;
            case MELODY_TYPE_AMR  : {s_mediaType = "audio/AMR";};break;
        }

        int i_b0 = _inputStream.readUnsignedByte();
        int i_b1 = _inputStream.readUnsignedByte();
        int i_b2 = _inputStream.readUnsignedByte();

        i_MelodyLength = (i_b0<<16) | (i_b1<<8) | i_b2;
        int i_size = _inputStream.readUnsignedByte();
        if ((i_size & 0x80) != 0) i_size = ((i_size & 0x7F) << 8) | _inputStream.readUnsignedByte();

        ab_arr = new byte[i_size];
        _inputStream.read(ab_arr);
        p_NativeFormat = null;
        lg_MelodyPlayed = false;
    }

    public InputStream getMelodyStream()
    {
        return new ByteArrayInputStream(ab_arr);
    }

    public byte[] getMelodyArray()
    {
        return ab_arr;
    }

    public void removeLoadedArray()
    {
        ab_arr = null;
        Runtime.getRuntime().gc();
    }

    public int getMelodyType()
    {
        return i_MelodyType;
    }
}
