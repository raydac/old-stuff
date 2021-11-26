import org.havi.ui.HSound;

public class SoundManager
{
    public static final int SOUND_CHANGEBUTTON = 0;
    public static final int SOUND_PRESSBUTTON = 1;
    public static final int SOUND_EXPLOSION = 2;
    public static final int SOUND_BALLBIT = 3;
    
    private static final int SOUNDS_NUMBER = 4;
    private static final int PLAYERS_NUMBER = SOUNDS_NUMBER;

    private static final int[] ai_SoundPriority = new int[] 
                                                          {
            0, 
            0, 
            2,
            1,
    };

    private static final int[] ai_SoundDelay = new int[] 
                                                       {
            400, 
            600, 
            1300, 
            400, 
    };

    private static final String[] as_Resources = new String[] {
            "btnch.mp2", "btnpr.mp2", "expl.mp2", "bita.mp2",
    };

    private static final HSound[] ap_Players = new HSound[PLAYERS_NUMBER];

    private static int i_lastStarted = -1;

    private static int i_curPriority = -1;

    private static long l_endCurrentSoundTime;

    public static final void initBlock() throws Exception
    {
        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            String s_resource = as_Resources[li];
            HSound p_snd = Utils.loadSound("snd/"+s_resource);
            ap_Players[li] = p_snd;
            Runtime.getRuntime().gc();
            Thread.sleep(2);
        }

        i_lastStarted = -1;
    }

    public static final void stopSound(int _index, boolean _waitFroEndOfIniting)
    {

        try
        {
            if (i_lastStarted == _index)
            {
                int i_playerIndex = _index;

                HSound p_player = ap_Players[i_playerIndex];
                if (p_player != null)
                    p_player.stop();

                i_lastStarted = -1;
            }
        }
        catch (Exception e)
        {
        }
    }

    public static final void stopAllSound()
    {

        for (int li = 0; li < SOUNDS_NUMBER; li++)
        {
            stopSound(li, true);
        }
        i_lastStarted = -1;
    }

    public synchronized static final void playSound(final int _index,int _volume)
    {
        System.out.println("Play sound "+_index);
        
        try
        {
            final int i_newPlayerIndex = _index;

            int i_newPriority = ai_SoundPriority[_index];

            if (i_lastStarted >= 0)
            {
                if (System.currentTimeMillis() < l_endCurrentSoundTime)
                {
                    if (_index == i_lastStarted)
                        return;
                    if (i_curPriority > i_newPriority)
                        return;
                }

                stopSound(i_lastStarted, true);
            }

            i_curPriority = i_newPriority;
            int i_delay = ai_SoundDelay[_index];
            l_endCurrentSoundTime = System.currentTimeMillis() + i_delay;

            i_lastStarted = _index;

            HSound p_player = ap_Players[i_newPlayerIndex];

            p_player.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static final void release()
    {
        if (ap_Players!=null)
        {
            for(int li=0;li<ap_Players.length;li++)
            {
                if (ap_Players[li]!=null)
                {
                    try
                    {
                        ap_Players[li].dispose();
                        ap_Players[li] = null;
                    }
                    catch(Throwable _thr)
                    {}
                }
            }
        }
    }
}
