//#if VENDOR=="SIEMENS"
//#if MODEL=="M50"
//$import com.siemens.mp.game.MelodyComposer;
//$import com.siemens.mp.game.Melody;
//#else
//$import com.siemens.mp.media.Player;
//$import com.siemens.mp.media.Manager;
//$import com.siemens.mp.media.Control;
//$import com.siemens.mp.media.control.VolumeControl;
//#endif
//#endif

//#if VENDOR=="SAMSUNG"
//$import com.samsung.util.AudioClip;
//#endif

//#if VENDOR=="LG"
//$import mmpp.media.MediaPlayer;
//#endif

//#local SAVEDATA = VENDOR=="SIEMENS" && !(MODEL=="M50")
//#local PLAYTHREAD = (MIDP=="2.0") || (VENDOR=="SIEMENS" && (MODEL!="M50"))

//#if (MIDP=="2.0" && VENDOR!="SIEMENS") || (VENDOR=="SE" && MODEL=="T610")

import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.Control;
import javax.microedition.media.control.VolumeControl;
//#endif

//#if VENDOR=="NOKIA" && MIDP=="1.0"
//$import com.nokia.mid.sound.Sound;
//$import java.io.ByteArrayOutputStream;
//#endif

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * ����� ��������� ������ �������� �����
 * @author Igor Maznitsa
 * @version 2.1 (23 jun 2005)
 */
public class SoundManager
{
    //#if PLAYTHREAD
    //$private static final Object p_SynchroSoundObject = new Object();
    //#endif

    public static final int SOUND_THEME = 0;
    public static final int SOUND_LOST = 1;
    public static final int SOUND_WIN = 2;
    public static final int SOUND_HIT = 3;
    public static final int SOUND_EXPL = 4;
    public static final int SOUND_CRUISER = 5;

    private static final int SOUNDS_NUMBER = 6;

    private static final int[] ai_SoundPriority = new int[]
            {
                        0, // theme
                        0, // lost
                        0, // win
                        2, // hit
                        1, // expl
                        3, // cruiser
                };

    private static final int[] ai_SoundDelay = new int[]
            {
                        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA") || (VENDOR=="LG")
                        //$3000, // Theme
                        //$3000, // Lost
                        //$3000, // Win
                        //$200, // Hit
                        //$500, // Expl
                        //$1000, // Cruiser
                        //#else
                        3000, // Theme
                        3000, // Lost
                        3000, // Win
                        150, // Hit
                        1100, // Expl
                        1900, // Cruiser
                        //#endif
                };

    private static final String[] as_Resources = new String[]
            {
                        //#if VENDOR=="SIEMENS"  && MODEL=="M50"

                        //$"/snd/theme.smn",
                        //$"/snd/lost.smn",
                        //$"/snd/win.smn",
                        //$"/snd/hit.smn",
                        //$"/snd/expl.smn",
                        //$"/snd/cruiser.smn",

                        //#else
                        //#if VENDOR=="NOKIA"  && MIDP=="1.0"

                        //$"/snd/theme.ott",
                        //$"/snd/lost.ott",
                        //$"/snd/win.ott",
                        //$"/snd/hit.ott",
                        //$"/snd/expl.ott",
                        //$"/snd/cruiser.ott",

                        //#else

                        //#if VENDOR=="LG"

                        //$        "/snd/theme.mid",
                        //$        "/snd/lost.mid",
                        //$        "/snd/win.mid",
                        //$        "/snd/hit.mid",
                        //$        "/snd/expl.mid",
                        //$        "/snd/cruiser.mid",

                        //#else

                        //#if VENDOR=="SAMSUNG"
                        //$        "/snd/theme.mmf",
                        //$        "/snd/lost.mmf",
                        //$        "/snd/win.mmf",
                        //$        "/snd/hit.mmf",
                        //$        "/snd/expl.mmf",
                        //$        "/snd/cruiser.mmf",
                        //#else

                        //#if VENDOR=="SIEMENS" && MODEL=="S55"
                        //$        "/snd/theme.mid",
                        //$        "/snd/lost.mid",
                        //$        "/snd/win.mid",
                        //$        "/snd/hit.mid",
                        //$        "/snd/expl.mid",
                        //$        "/snd/cruiser.mid",
                        //#else

                        "/snd/theme.mid",
                        "/snd/lost.mid",
                        "/snd/win.mid",
                        //#if VENDOR=="SE" || VENDOR=="MOTOROLA" || (VENDOR=="SIEMENS" && (MODEL=="C65" || MODEL=="CX65"))
                        //$"/snd/hit.amr",
                        //$"/snd/expl.amr",
                        //$"/snd/cruiser.amr",
                        //#else
                        "/snd/hit.wav",
                        "/snd/expl.wav",
                        "/snd/cruiser.wav",
                        //#endif

                        //#endif
                        //#endif

                        //#endif
                        //#endif

                        //#endif
                };

    //#if VENDOR=="SIEMENS" && MODEL=="M50"
    //$private static final Melody[] ap_Players = new Melody[SOUNDS_NUMBER];
    //#else
    //#if VENDOR=="LG"
    //$private static final MediaPlayer[] ap_Players = new MediaPlayer[SOUNDS_NUMBER];
    //#else

    //#if VENDOR=="SAMSUNG"
    private static int i_SamsungVolume;
    //$private static final AudioClip[] ap_Players = new AudioClip[SOUNDS_NUMBER];
    //#else
    //#if VENDOR=="NOKIA" && MIDP=="1.0"
    //$private static final Sound[] ap_Players = new Sound[SOUNDS_NUMBER];
    //#else
    private static final Player[] ap_Players = new Player[SOUNDS_NUMBER];
    //#endif
    //#endif
    //#endif
    //#endif

    //#if SAVEDATA
    private static int i_Volume;

    private static final int[] ai_DataTypeArray = new int[SOUNDS_NUMBER];

    private static final ByteArrayInputStream[] ap_DataStreams = new ByteArrayInputStream[SOUNDS_NUMBER];

    private final static int SND_MIDI = 0;
    private final static int SND_WAV = 1;
    private final static int SND_AMR = 2;
    private final static int SND_MMF = 3;

    private static final void initPlayer(int _index) throws Exception
    {
        final int i_dataType = ai_DataTypeArray[_index];
        final ByteArrayInputStream p_bais = ap_DataStreams[_index];
        p_bais.reset();
        String s_type = null;

        switch (i_dataType)
        {
            case SND_AMR:
                {
                    s_type = "audio/amr";
                }
        ;
                break;
            case SND_WAV:
                {
                    s_type = "audio/x-wav";
                }
        ;
                break;
            case SND_MIDI:
                {
                    //#if VENDOR=="SIEMENS"
                    //$ s_type="audio/x-mid";
                    //#else
                    s_type = "audio/midi";
                    //#endif
                }
        ;
                break;
            case SND_MMF:
                {
                    //#if VENDOR=="SAMSUNG"
                    //$s_type = "application/vnd.smaf";
                    //#else
                    s_type = "application/x-smaf";
                    //#endif
                }
        ;
                break;
        }

        Player p_player = Manager.createPlayer(p_bais, s_type);
        p_player.realize();

        //#if VENDOR=="SIEMENS"
        //$Control p_volcontrol = p_player.getControl("com.siemens.mp.media.control.VolumeControl");
        //#endif
        //#if (MIDP=="2.0" && VENDOR!="SIEMENS")
        Control p_volcontrol = p_player.getControl("javax.microedition.media.control.VolumeControl");
        //#endif
        if (p_volcontrol != null) ((VolumeControl) p_volcontrol).setLevel(i_Volume);
        //#if DEBUG
        else
            System.out.println("Can't change volume");
        //#endif

        //#if VENDOR!="MOTOROLA"
        p_player.prefetch();
        //#endif

        ap_Players[_index] = p_player;
    }
    //#endif

    private static int i_lastStarted = -1;
    private static int i_curPriority = -1;
    private static long l_endCurrentSoundTime;

    public static final void initBlock(Class _parent, int _volume) throws Exception
    {
        //#if VENDOR=="SIEMENS" && MODEL=="M50"
        //$        for(int li=0;li<SOUNDS_NUMBER;li++)
        //$        {
        //$            String s_resource = as_Resources[li];
        //#if SHOWSYS
        //$            System.out.println("Sound [" + s_resource + "]");
        //#endif
        //$            InputStream p_inStream = _parent.getResourceAsStream(s_resource);

        //$            int i_blockLen = p_inStream.read()&0xFF;
        //$            int i_bpm = (p_inStream.read()&0xFF)<<8;
        //$            i_bpm |= p_inStream.read()&0xFF;

        //$            int [] ai_melodyArray = new int [i_blockLen];
        //$            for(int lx=0;lx<i_blockLen;lx++) ai_melodyArray[lx] = p_inStream.read() & 0xFF;

        //$            MelodyComposer p_melComp = new MelodyComposer(ai_melodyArray,i_bpm);
        //$            ap_Players[li] = p_melComp.getMelody();
        //$            p_melComp = null;
        //$        }
        //$        Runtime.getRuntime().gc();
        //#else

        //#if VENDOR=="SAMSUNG"
        i_SamsungVolume = _volume / 20;
        //#endif

        //#if SAVEDATA
        byte[] ab_loadBlock = new byte[256];
        ByteArrayOutputStream p_accumStream = new ByteArrayOutputStream(8000);
        int[] ai_SoundDataBlockOffset = new int[SOUNDS_NUMBER];
        int[] ai_SoundDataBlockLength = new int[SOUNDS_NUMBER];
        i_Volume = _volume;
        //#endif

        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            String s_resource = as_Resources[li];
            String s_type = null;

            //#if SHOWSYS
            System.out.println("Sound [" + s_resource + "]");
            //#endif

            //#if SAVEDATA
            int i_type = -1;
            if (s_resource.endsWith(".mid")) i_type = SND_MIDI;
            else if (s_resource.endsWith(".wav")) i_type = SND_WAV;
            else if (s_resource.endsWith(".mmf")) i_type = SND_MMF;
            else if (s_resource.endsWith(".amr")) i_type = SND_AMR;

            InputStream p_inStream = _parent.getResourceAsStream(s_resource);
            ai_DataTypeArray[li] = i_type;
            ai_SoundDataBlockOffset[li] = p_accumStream.size();

            int i_len = 0;
            while (true)
            {
                int i_readed = p_inStream.read(ab_loadBlock);
                if (i_readed <= 0) break;
                i_len += i_readed;
                p_accumStream.write(ab_loadBlock, 0, i_readed);
            }
            ai_SoundDataBlockLength[li] = i_len;
            p_inStream.close();
            p_accumStream.flush();
            p_inStream = null;
            //#else

            //#if VENDOR=="LG"
            //$MediaPlayer p_Player = new MediaPlayer();
            //$p_Player.setMediaLocation(s_resource);
            //$ap_Players[li] = p_Player;
            //#else

            //#if VENDOR=="NOKIA" && MIDP=="1.0"
            //$int i_type = Sound.FORMAT_TONE;
            //$if (s_resource.endsWith(".wav")) i_type = Sound.FORMAT_WAV;
            //$InputStream p_inStream = _parent.getResourceAsStream(s_resource);
            //$ByteArrayOutputStream p_bais = new ByteArrayOutputStream(1024);
            //$while(true)
            //${
            //$int i_code = p_inStream.read();
            //$if (i_code<0) break;
            //$p_bais.write(i_code);
            //$}
            //$p_inStream.close();
            //$p_bais.close();
            //$p_inStream = null;

            //$byte [] ab_array = p_bais.toByteArray();
            //$p_bais = null;

            //$Sound p_snd = new Sound(ab_array,i_type);
            //$ap_Players[li] = p_snd;

            //#else

            //#if VENDOR=="SAMSUNG"
            //$s_type = "application/vnd.smaf";
            //#else
            if (s_resource.endsWith(".wav")) s_type = "audio/x-wav";
            //#if VENDOR=="SIEMENS"
            //$if (s_resource.endsWith(".mid")) s_type="audio/x-mid";
            //#else
            if (s_resource.endsWith(".mid")) s_type = "audio/midi";
            //#endif
            if (s_resource.endsWith(".mmf")) s_type = "application/x-smaf";
            //#if VENDOR=="SE" || VENDOR=="MOTOROLA" || VENDOR=="SIEMENS"
            if (s_resource.endsWith(".amr")) s_type = "audio/amr";
            //#endif

            //#endif

            //#if  VENDOR=="SAMSUNG"
            //$AudioClip p_clip = new AudioClip(AudioClip.TYPE_MMF,s_resource);
            //$ap_Players[li] = p_clip;
            //#else
            InputStream p_instr = _parent.getResourceAsStream(s_resource);
            if (p_instr == null) throw new Exception(s_resource);

            Player p_player = Manager.createPlayer(p_instr, s_type);
            if (p_player == null) throw new Exception(s_resource);

            p_player.realize();

            //#if VENDOR!="MOTOROLA"
            p_player.prefetch();
            //#endif

            //#if VENDOR=="SIEMENS"
            //$Control p_volcontrol = p_player.getControl("com.siemens.mp.media.control.VolumeControl");
            //#endif
            //#if (MIDP=="2.0" && VENDOR!="SIEMENS") || (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")
            Control p_volcontrol = p_player.getControl("javax.microedition.media.control.VolumeControl");
            //#endif
            if (p_volcontrol != null) ((VolumeControl) p_volcontrol).setLevel(_volume);
                                                                    //#if DEBUG
            else
                System.out.println("Can't change volume");
            //#endif

            ap_Players[li] = p_player;

            //#endif
            //#endif
            //#endif
            Runtime.getRuntime().gc();

            //#endif
        }

        //#if SAVEDATA
        p_accumStream.close();
        byte[] ab_arrayD = p_accumStream.toByteArray();
        p_accumStream = null;

        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            int i_dataOffset = ai_SoundDataBlockOffset[li];
            int i_dataLength = ai_SoundDataBlockLength[li];
            ByteArrayInputStream p_bais = new ByteArrayInputStream(ab_arrayD, i_dataOffset, i_dataLength);
            ap_DataStreams[li] = p_bais;
        }
        ab_arrayD = null;
        ai_SoundDataBlockLength = null;
        ai_SoundDataBlockOffset = null;
        p_accumStream = null;
        for (int li = 0; li < SOUNDS_NUMBER; li++) initPlayer(li);
        Runtime.getRuntime().gc();
        //#endif

        //#endif

        i_lastStarted = -1;
    }

    public static final void stopSound(int _index)
    {
        try
        {
            if (i_lastStarted == _index)
            {
                //#if VENDOR=="SIEMENS" && MODEL=="M50"
                //$Melody.stop();
                //#else

                //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$Sound p_player = ap_Players[_index];
                //$p_player.stop();
                //#else
                //#if VENDOR=="SAMSUNG"
                //$AudioClip p_player = ap_Players[_index];
                //$p_player.stop();
                //#else
                //#if VENDOR=="LG"
                //$MediaPlayer p_player = ap_Players[_index];
                //$p_player.stop();
                //#else
                Player p_player = ap_Players[_index];
                //#if VENDOR=="SIEMENS"
                //$p_player.stop();
                //#else
                p_player.deallocate();
                //#endif
                //#endif
                //#endif
                //#endif

                //#if SAVEDATA
                //$ap_Players[_index] = null;
                //$initPlayer(_index);
                //#endif

                //#endif

                i_lastStarted = -1;
            }
        }
        catch (Exception e)
        {
        }
    }

    public static final void stopAllSound()
    {
        //#if VENDOR=="SIEMENS" && MODEL=="M50"
        //$Melody.stop();
        //#else

        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            try
            {
                //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$Sound p_player = ap_Players[li];
                //#else
                //#if VENDOR=="SAMSUNG"
                //$AudioClip p_player = ap_Players[li];
                //#else
                //#if VENDOR=="LG"
                //$MediaPlayer p_player = ap_Players[li];
                //#else
                Player p_player = ap_Players[li];
                //#endif
                //#endif
                //#endif

                //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$p_player.stop();
                //#else
                //#if VENDOR=="SE"
                //$if(p_player.getState() == Player.STARTED) p_player.stop();
                //#endif
                //#if VENDOR=="SUN" || VENDOR=="NOKIA" || VENDOR=="MOTOROLA"
                //$if(p_player.getState() == Player.STARTED) p_player.deallocate();
                //#endif

                //#if VENDOR=="SIEMENS"
                //$p_player.stop();
                //#endif

                //#if VENDOR=="SAMSUNG"
                //$p_player.stop();
                //#endif
                //#if VENDOR=="LG"
                //$p_player.stop();
                //#endif
                //#endif
            }
            catch (Exception e)
            {
            }
        }
        //#endif
        i_lastStarted = -1;
    }

    /**
         * ������ ������������ �����
         *
         * @param _index ������ �������������� �����
         * @param _count ���������� ����������
         */
    public static final void playSound(final int _index,final int _count)
    {
        try
        {
            //#if VENDOR=="NOKIA" && MIDP=="1.0"
            //$Sound p_player = ap_Players[_index];
            //#else
            //#if VENDOR=="SAMSUNG"
            //$AudioClip p_player = ap_Players[_index];
            //#else
            //#if VENDOR=="LG"
            //$MediaPlayer p_player = ap_Players[_index];
            //#else
            //#if VENDOR=="SIEMENS" && MODEL=="M50"
            //$Melody p_player = ap_Players[_index];
            //#else
            final Player p_player = ap_Players[_index];
            //#endif
            //#endif
            //#endif
            //#endif

            int i_newPriority = ai_SoundPriority[_index];

            //#if PLAYTHREAD
            //$synchronized(p_SynchroSoundObject)
            //${
            //#endif

            if (i_lastStarted >= 0)
            {
                // �������� �� ��������� ������������ ����������� �����
                if (System.currentTimeMillis() < l_endCurrentSoundTime)
                {
                    if (_index == i_lastStarted) return;
                    if (i_curPriority > i_newPriority) return;
                }

                //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$Sound p_oldPlayer = ap_Players[i_lastStarted];;
                //#else
                //#if VENDOR=="SAMSUNG"
                //$AudioClip p_oldPlayer = ap_Players[i_lastStarted];
                //#else
                //#if VENDOR=="LG"
                //$MediaPlayer p_oldPlayer = ap_Players[i_lastStarted];
                //#else
                //#if VENDOR=="SIEMENS" && MODEL=="M50"
                //$Melody p_oldPlayer = ap_Players[i_lastStarted];
                //#else
                Player p_oldPlayer = ap_Players[i_lastStarted];
                //#endif
                //#endif
                //#endif
                //#endif

                //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$p_oldPlayer.stop();
                //#endif

                //#if VENDOR=="SAMSUNG"
                //$p_oldPlayer.stop();
                //#endif

                //#if VENDOR=="LG"
                //$p_oldPlayer.stop();
                //#endif

                //#if VENDOR=="SE"
                //$p_oldPlayer.stop();
                //#endif

                //#if SAVEDATA
                //$p_oldPlayer.stop();
                //$p_oldPlayer = null;
                //$ap_Players[i_lastStarted] = null;
                //$initPlayer(i_lastStarted);
                //#else

                //#if VENDOR=="SUN" || VENDOR=="SIEMENS" || (VENDOR=="NOKIA" && MIDP=="2.0")|| VENDOR=="MOTOROLA"

                //#if VENDOR!="SIEMENS"
                p_oldPlayer.deallocate();
                //#endif

                //#if VENDOR=="SIEMENS"
                //$p_oldPlayer.stop();
                //#endif

                //#if VENDOR!="MOTOROLA" && VENDOR!="SIEMENS"
                p_oldPlayer.prefetch();
                //#endif
                //#endif
                //#endif
            }

            i_curPriority = i_newPriority;
            int i_delay = ai_SoundDelay[_index];
            l_endCurrentSoundTime = System.currentTimeMillis() + i_delay;

            //#if VENDOR=="SAMSUNG"
            // ��������� ������ ������ ��������� �������� �������
            // ��������������� �� X100, E700
            long l_tms = System.currentTimeMillis();
            long l_delta = l_endCurrentSoundTime - l_tms;
            if (l_delta > i_delay || l_delta <= 0)
            {
                l_endCurrentSoundTime = 0;
                i_lastStarted = -1;
                return;
            }
            //#endif

            i_lastStarted = _index;

            //#if PLAYTHREAD
            //$}
            //#endif

            //#if PLAYTHREAD
            //$new Thread(new Runnable(){
            //$    public void run()
            //$   {
            //$     synchronized(p_SynchroSoundObject)
            //$     {
            //$        try
            //$        {
            //#endif

            //#if VENDOR=="NOKIA" && MIDP=="1.0"
                //$p_player.play(_count);
            //#else

                //#if VENDOR=="MOTOROLA" && !SAVEDATA
                    p_player.prefetch();
                //#endif

                //#if VENDOR=="SAMSUNG"
                    //$p_player.play(_count,i_SamsungVolume);
                //#else
                    //#if VENDOR=="LG"
                        //$p_player.start();
                    //#else
                        //#if VENDOR=="SIEMENS" && MODEL=="M50"
                            //$p_player.play();
                        //#else
                            p_player.setLoopCount(_count);
                            p_player.start();
                        //#endif
                    //#endif
                //#endif
           //#endif

          //#if PLAYTHREAD
          //$ }
          //$ catch(Exception _ex){}
          //$  }
          //$  }
          //$  }).start();
          //#endif
        }
        catch (Exception e)
        {
        }
    }

}
