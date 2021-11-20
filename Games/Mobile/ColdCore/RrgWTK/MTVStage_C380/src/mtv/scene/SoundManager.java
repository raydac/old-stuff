package mtv.scene;

import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.Control;
import javax.microedition.media.control.VolumeControl;

public class SoundManager
{
    public static final int SOUND_LOST = 0;
    public static final int SOUND_THEME = 1;
    public static final int SOUND_WIN = 2;
    public static final int SOUND_DROP = 3;
    public static final int SOUND_DEATH = 4;
    public static final int SOUND_HIT = 5;
    public static final int SOUND_GLASS = 6;

    private static final Player[] ap_Players = new Player[7];

    private static int i_lastStarted = -1;

    private static final String [] as_Resources = new String[]
    {
        "/snd/lost.mid",
        "/snd/theme.mid",
        "/snd/win.mid",
        "/snd/drop.wav",
        "/snd/death.wav",
        "/snd/hit.wav",
        "/snd/glass.wav",
    };

    public static final boolean isStarted(int _index)
    {
        if (i_lastStarted>=0)
        {
            return ap_Players[i_lastStarted].getState() == Player.STARTED;
        }
        return false;
    }

    public static final void initBlock(Class _parent,int _volume) throws Exception
    {
        for(int li=0;li<as_Resources.length;li++)
        {
            String s_resource = as_Resources[li];
            String s_type = null;
            if (s_resource.endsWith(".wav")) s_type="audio/x-wav";
            else
            if (s_resource.endsWith(".mid")) s_type="audio/midi";

            Player p_player = Manager.createPlayer(_parent.getResourceAsStream(s_resource),s_type);
            p_player.realize();

            Control [] ab_controls = p_player.getControls();
            for(int lc=0;lc<ab_controls.length;lc++)
            {
                if (ab_controls[lc] instanceof VolumeControl)
                {
                    ((VolumeControl)ab_controls[lc]).setLevel(_volume);
                    break;
                }
            }

            ap_Players[li] = p_player;
        }
        i_lastStarted = -1;
    }

    public static final void stopSound(int _index)
    {
        try
        {
            Player p_player = ap_Players[_index];
            p_player.stop();
        }
        catch (Exception e)
        {
        }
    }

    public static final void stopAllSound()
    {
        for(int li=0;li<ap_Players.length;li++)
        {
            try
            {
                Player p_player = ap_Players[li];
                p_player.stop();
            }
            catch (Exception e)
            {
            }
        }
    }

    public static synchronized final void playSound(int _index,int _count)
    {
        try
        {
            if (i_lastStarted>=0)
            {
                ap_Players[i_lastStarted].deallocate();
            }
            i_lastStarted = _index;
            Player p_player = ap_Players[_index];
            if (p_player.getState() == Player.STARTED) return;
            p_player.prefetch();
            p_player.setLoopCount(_count);
            p_player.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
