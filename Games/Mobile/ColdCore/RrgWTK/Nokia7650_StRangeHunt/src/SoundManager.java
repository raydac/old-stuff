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

//#local SAVEDATA = (VENDOR=="SIEMENS" && !(MODEL=="M50")) || VENDOR=="MOTOROLA"
//#local PLAYTHREAD = (MIDP=="2.0") || (VENDOR=="SIEMENS" && (MODEL!="M50"))
//#local SINGLEPLAYER = SAVEDATA

//#if (MIDP=="2.0" && VENDOR!="SIEMENS") || (VENDOR=="SE" && MODEL=="T610")

import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VolumeControl;
//#endif

//#if (VENDOR=="NOKIA" && MIDP=="1.0")
//$import com.nokia.mid.sound.Sound;
//$import java.io.ByteArrayOutputStream;
//#endif

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Класс реализует работу звуковой схемы
 *
 * @author Igor Maznitsa
 * @version 2.5 (30 jul 2005)
 */
public class SoundManager
{
    public static final int SOUND_THEME = 0;
    public static final int SOUND_LOST = 1;
    public static final int SOUND_WIN = 1;
    public static final int SOUND_FIGHTFIRE = 2;
    public static final int SOUND_HIT = 3;
    public static final int SOUND_TANKFIRE = 4;
    public static final int SOUND_KILLED = 5;

    private static final int SOUNDS_NUMBER = 6;

    //#if SINGLEPLAYER
    //$private static final int PLAYERS_NUMBER = 1;
    private static boolean lg_PlayerInIniting = false;
    //#else
    private static final int PLAYERS_NUMBER = SOUNDS_NUMBER;
    //#endif

    private static final int[] ai_SoundPriority = new int[]
    {
        0, // theme
        0, // lost/win
        2, // fightfire
        1, // hit
        2, // tankfire
        3, // killed
    };

    private static final int[] ai_SoundDelay = new int[]
    {
        6000, // Theme
        4000, // Lost/Win
        1000, // fightfire
        400, // hit
        300, // tankfire
        2000, // killed
    };

    private static final String[] as_Resources = new String[]
    {
        //#if (VENDOR=="NOKIA" && MIDP=="1.0")

        //$"theme.ott",
        //$"lost.ott",
        //$"fighterfire.ott",
        //$"hit.ott",
        //$"tankfire.ott",
        //$"killed.ott",

        //#else
        //#if VENDOR=="SAMSUNG"
        //$"theme.mmf",
        //$"lost.mmf",
        //$"fighterfire.mmf",
        //$"hit.mmf",
        //$"tankfire.mmf",
        //$"killed.mmf",
        //#else
        "theme.mid",
        "lost.mid",
        "fighterfire.mid",
        "hit.mid",
        "tankfire.mid",
        "killed.mid",
        //#endif
        //#endif
    };

    //#if VENDOR=="SIEMENS" && MODEL=="M50"
    //$private static final Melody[] ap_Players = new Melody[PLAYERS_NUMBER];
    //#else
    //#if VENDOR=="LG"
    //$private static final MediaPlayer[] ap_Players = new MediaPlayer[PLAYERS_NUMBER];
    //#else

    //#if VENDOR=="SAMSUNG"
    private static int i_SamsungVolume;
    //$private static final AudioClip[] ap_Players = new AudioClip[PLAYERS_NUMBER];
    //#else
    //#if (VENDOR=="NOKIA" && MIDP=="1.0")
    //$private static final Sound[] ap_Players = new Sound[PLAYERS_NUMBER];
    //#else
    private static final Player[] ap_Players = new Player[PLAYERS_NUMBER];
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
        //#if SINGLEPLAYER
        //$final int i_playerIndex = 0;
        //#else
        final int i_playerIndex = _index;
        //#endif

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

        //#if SINGLEPLAYER
        if (i_lastStarted >= 0) stopSound(i_lastStarted,false);
        //#endif

        Player p_player = Manager.createPlayer(p_bais, s_type);

        //#if VENDOR=="SIEMENS" && MIDP=="1.0"
        //$p_player.realize();
        //#endif

        //#if (VENDOR=="SE" || MIDP=="2.0")
        p_player.prefetch();
        //#endif

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

        ap_Players[i_playerIndex] = p_player;
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
        //$            String s_resource = "/snd/"+as_Resources[li];
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
            String s_resource = "/snd/" + as_Resources[li];
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

            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
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
        Runtime.getRuntime().gc();
        //#endif

        //#endif

        i_lastStarted = -1;
    }

    public static final void stopSound(int _index,boolean _waitFroEndOfIniting)
    {
        //#if SINGLEPLAYER

        //#if SHOWSYS
        System.out.println("Wait for player initing");
        //#endif

        //$while(_waitFroEndOfIniting && lg_PlayerInIniting){Thread.yield();}

        //#if SHOWSYS
        System.out.println("stop player");
        //#endif
        //#endif

        try
        {
            //#if !SINGLEPLAYER
            if (i_lastStarted == _index)
            //#endif
            {
                //#if SINGLEPLAYER
                //$int i_playerIndex = 0;
                //#else
                int i_playerIndex = _index;
                //#endif

                //#if VENDOR=="SIEMENS" && MODEL=="M50"
                //$Melody.stop();
                //#else

                //#if (VENDOR=="NOKIA" && MIDP=="1.0")
                //$Sound p_player = ap_Players[i_playerIndex];
                //$if (p_player!=null) p_player.stop();
                //#else
                //#if VENDOR=="SAMSUNG"
                //$AudioClip p_player = ap_Players[i_playerIndex];
                //$if (p_player!=null) p_player.stop();
                //#else
                //#if VENDOR=="LG"
                //$MediaPlayer p_player = ap_Players[i_playerIndex];
                //$if (p_player!=null) p_player.stop();
                //#else
                Player p_player = ap_Players[i_playerIndex];
                //#if VENDOR=="SIEMENS" || VENDOR=="SE"
                //$if (p_player!=null) p_player.stop();
                //#else
                if (p_player != null) p_player.deallocate();
                //#endif
                //#endif
                //#endif
                //#endif

                //#if SAVEDATA
                //$ap_Players[i_playerIndex] = null;
                //#endif

                //#endif

                i_lastStarted = -1;
            }
        }
        catch (Exception e)
        {
            //#if SHOWSYS
            e.printStackTrace();
            //#endif
        }
    }

    public static final void stopAllSound()
    {
        //#if SHOWSYS
        System.out.println("All sounds stoped");
        //#endif
        //#if VENDOR=="SIEMENS" && MODEL=="M50"
        //$Melody.stop();
        //#else

        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            stopSound(li,true);
        }
        //#endif
        i_lastStarted = -1;
    }


    /**
     * Запуск проигрывания звука
     *
     * @param _index индекс проигрываемого звука
     * @param _count количество повторений
     */
    public synchronized static final void playSound(final int _index, final int _count)
    {
        //#if SHOWSYS
        System.out.println("Play sound :" + _index + " count :" + _count);
        //#endif
        try
        {
            //#if SINGLEPLAYER
            //$final int i_newPlayerIndex = 0;
           //$while(lg_PlayerInIniting){Thread.yield();}
            //#else
            final int i_newPlayerIndex = _index;
            //#endif

            int i_newPriority = ai_SoundPriority[_index];

            if (i_lastStarted >= 0)
            {
                // Проверка на окончание проигрывания предыдущего звука
                if (System.currentTimeMillis() < l_endCurrentSoundTime)
                {
                    if (_index == i_lastStarted) return;
                    if (i_curPriority > i_newPriority) return;
                }

                //#if SINGLEPLAYER
                //$stopSound(0,true);
                //#else
                stopSound(i_lastStarted,true);
                //#endif
            }
            //#if SINGLEPLAYER
            //$lg_PlayerInIniting = true;
            //#if SHOWSYS
            System.out.println("player initing");
            //#endif
            //#endif

            i_curPriority = i_newPriority;
            int i_delay = ai_SoundDelay[_index];
            l_endCurrentSoundTime = System.currentTimeMillis() + i_delay;

            //#if VENDOR=="SAMSUNG"
            // Коррекция редкой ошибки получения текущего времени
            // зафиксированной на X100, E700
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
            //$new Thread(new Runnable(){
            //$    public void run()
            //$   {
            //$        try
            //$        {
            //#endif

            //#if SAVEDATA
            //#if SHOWSYS
            //$try{
            //#endif
            //$initPlayer(_index);
            //#if SHOWSYS
            //$}catch(Exception _ex)
            //${
            //$_ex.printStackTrace();
            //$return;
            //$}
            //#endif
            //#endif

            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
            //$Sound p_player = ap_Players[i_newPlayerIndex];
            //#else
            //#if VENDOR=="SAMSUNG"
            //$AudioClip p_player = ap_Players[i_newPlayerIndex];
            //#else
            //#if VENDOR=="LG"
            //$MediaPlayer p_player = ap_Players[i_newPlayerIndex];
            //#else
            //#if VENDOR=="SIEMENS" && MODEL=="M50"
            //$Melody p_player = ap_Players[i_newPlayerIndex];
            //#else
            final Player p_player = ap_Players[i_newPlayerIndex];
            //#endif
            //#endif
            //#endif
            //#endif

            //#if (VENDOR=="NOKIA" && MIDP=="1.0")
            //$p_player.play(_count);
            //#else

            //#if (MIDP=="2.0" || VENDOR=="SE") && (VENDOR!="MOTOROLA" && !SAVEDATA && VENDOR!="SAMSUNG"  && VENDOR!="LG")
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
            //$ catch(Exception _ex){
            //#if SHOWSYS
            //$_ex.printStackTrace();
            //#endif
            //$}
            //#if SINGLEPLAYER
            //$finally
            //${
            //#if SHOWSYS
            System.out.println("player has inited");
            //#endif
            //$   lg_PlayerInIniting = false;
            //$}
            //#endif
            //$  }
            //$  }).start();
            //#endif
        }
        catch (Exception e)
        {
            //#if SHOWSYS
            e.printStackTrace();
            //#endif
        }
    }

}
