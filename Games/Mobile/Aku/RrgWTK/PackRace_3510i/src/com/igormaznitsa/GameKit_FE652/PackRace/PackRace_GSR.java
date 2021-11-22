package com.igormaznitsa.GameKit_FE652.PackRace;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class PackRace_GSR implements GameStateRecord
{
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_KILLED = 1;
    public static final int PLAYERSTATE_WON = 2;

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    protected Creature _creature;
    protected Creature[] _guard;

    protected int _game_state = 0;
    protected int _player_state = 0;

    protected int _summary_scores = 0;
    protected int _cur_max_rice = 0;
    protected int _cur_rice_number = 0;
    protected int _cur_level_scores = 0;

    protected int _lab_num = 0;
    protected int _attempts = 0;
    protected int _cur_delay = 0;
    protected int _level = 0;

    protected int[] _cur_lab = null;

    protected boolean _isguninhend = false;
    protected int _gun_ticks = 0;

    public int getGunTicks()
    {
        return _gun_ticks;
    }

    public int getStageNum()
    {
        return _lab_num;
    }

    public int getLevel()
    {
        return _level;
    }

    public Creature getPlayer()
    {
        return _creature;
    }

    public Creature[] getGuardianArray()
    {
        return _guard;
    }

    protected void resumeGame()
    {
        _player_state = PLAYERSTATE_NORMAL;

        _creature.init();

        for (int li = 0; li < _guard.length; li++)
        {
            _guard[li].init();
        }
    }

    public int getElementAt(int x, int y)
    {
        return _cur_lab[x + y * Labyrints.FIELD_WIDTH];
    }

    protected void setElementAt(int x, int y, int ele)
    {
        _cur_lab[x + y * Labyrints.FIELD_WIDTH] = ele;
    }

    protected void loadLabyrinth(int num)
    {
        _cur_level_scores = 0;
        _cur_rice_number = 0;
        _cur_max_rice = 0;
        _lab_num = num;
        _cur_lab = Labyrints.getLabyrinth(num);
        _gun_ticks = 0;
        _isguninhend = false;

        int guardnum = 0;
        for (int li = 0; li < _cur_lab.length; li++) if (_cur_lab[li] == Labyrints.ELE_HUNTER) guardnum++;

        _guard = new Creature[guardnum];

        guardnum = 0;
        for (int ly = 0; ly < Labyrints.FIELD_HEIGHT; ly++)
        {
            for (int lx = 0; lx < Labyrints.FIELD_WIDTH; lx++)
            {
                switch (getElementAt(lx, ly))
                {
                    case Labyrints.ELE_RICE:
                        _cur_max_rice++;
                        break;
                    case Labyrints.ELE_CREATURE:
                        {
                            _creature = new Creature(lx * PackRace_SB.VIRTUAL_CELL_WIDTH, ly * PackRace_SB.VIRTUAL_CELL_HEIGHT, false);
                            setElementAt(lx, ly, Labyrints.ELE_EMPTY);
                        }
                        ;
                        break;
                    case Labyrints.ELE_HUNTER:
                        {
                            _guard[guardnum++] = new Creature(lx * PackRace_SB.VIRTUAL_CELL_WIDTH, ly * PackRace_SB.VIRTUAL_CELL_HEIGHT, true);
                            setElementAt(lx, ly, Labyrints.ELE_EMPTY);
                        }
                        ;
                        break;
                }
            }
        }

        _creature.init();
        resumeGame();
    }

    public int getTimeDelay()
    {
        return _cur_delay;
    }

    public PackRace_GSR(int level)
    {
        _summary_scores = 0;
        _level = level;
        _game_state = GAMESTATE_PLAYED;
        switch (level)
        {
            case PackRace_SB.LEVEL0:
                _cur_delay = PackRace_SB.LEVEL0_DELAY;
                break;
            case PackRace_SB.LEVEL1:
                _cur_delay = PackRace_SB.LEVEL1_DELAY;
                break;
            default:
                _cur_delay = PackRace_SB.LEVEL2_DELAY;
        }
        _attempts = PackRace_SB.MAX_ATTEMPTIONS;
    }

    public int getAttemptions()
    {
        return _attempts;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _summary_scores;
    }

    public int getLevelScores()
    {
        return _cur_level_scores;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getAIState()
    {
        return 0;
    }

    public int getStage()
    {
        return _lab_num;
    }
}
