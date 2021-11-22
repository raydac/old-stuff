package com.igormaznitsa.GameKit_FE652.Pitman;

//import com.igormaznitsa.GameAPI.GameStateRecord;

public class Pitman_GSR //implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    protected int _game_state ;
    protected int _player_state;
    protected int _player_scores;
    protected int _level_scores;

    protected int _level;
    protected int _stage;

    protected int _time_delay;
    protected byte [] _lab;
    protected int _player_index;

    protected MovingElement [] _stones;
    protected int _moving_stone_counter;

    protected int _max_diamods;
    protected MovingCreature [] creatures;

    protected int _new_player_dir;

    public byte getElementAt(int x,int y)
    {
        return _lab[x+(y*Labyrinths.LAB_WIDTH)];
    }

    public void setElementAt(int x,int y,byte elem)
    {
        _lab[x+(y*Labyrinths.LAB_WIDTH)] = elem;
    }

    public int getTimeDelay()
    {
        return _time_delay;
    }

    public Pitman_GSR(int level)
    {
        _level = level;
        switch(level)
        {
            case Pitman_SB.LEVEL0 :{_time_delay = Pitman_SB.LEVEL0_TIMEDELAY;};break;
            case Pitman_SB.LEVEL1 :{_time_delay = Pitman_SB.LEVEL1_TIMEDELAY;};break;
            case Pitman_SB.LEVEL2 :{_time_delay = Pitman_SB.LEVEL2_TIMEDELAY;};break;
            default: _time_delay = Pitman_SB.LEVEL0_TIMEDELAY;
        }
    }

    public MovingCreature[] getCreaturesArray()
    {
        return creatures;
    }

    protected MovingCreature getInactiveCreature()
    {
        for(int li=0;li<creatures.length;li++)
        {
            if (creatures[li]._type == MovingCreature.CREATURE_INACTIVE) return creatures[li];
        }
        return null;
    }

    protected void initStage(int stage)
    {
        _stones = null;
        creatures = null;
        System.gc();

        _player_state = PLAYERSTATE_NORMAL;
        _game_state = GAMESTATE_PLAYED;

        _stage = stage;
        _lab = Labyrinths.getLabyrinth(stage);

        int cnt =0;
        int dmnd = 0;
        int rlng = 0;
        _level_scores = 0;
        for(int li=0;li<_lab.length;li++)
        {
            switch(_lab[li])
            {
                case Labyrinths.ELEMENT_STONE : rlng++;break;
                case Labyrinths.ELEMENT_DIAMOND: dmnd++; rlng++; break;
                case Labyrinths.ELEMENT_FIREFLY:
                case Labyrinths.ELEMENT_BUTTERFLY:  rlng+=9; cnt+=9;break;
                case Labyrinths.ELEMENT_PLAYER : cnt++;break;
            }
        }
        _max_diamods = dmnd;
        _moving_stone_counter = 0;
        _stones = new MovingElement[rlng];
        for(int li=0;li<_stones.length;li++)
        {
            _stones [li] = new MovingElement();
        }

        creatures = new MovingCreature [cnt];
        for(int li=0;li<creatures.length;li++)
        {
            creatures[li] = new MovingCreature(MovingCreature.CREATURE_INACTIVE,0,0);
        }

        cnt = 0;
        for(int li=0;li<_lab.length;li++)
        {
            int lx = li%Labyrinths.LAB_WIDTH;
            int ly = li/Labyrinths.LAB_WIDTH;
            switch(_lab[li])
            {
                case Labyrinths.ELEMENT_BUTTERFLY:
                    {
                        MovingCreature cr = creatures[cnt++];
                        cr.setCXCYS(MovingCreature.CREATURE_BUTTERFLY,lx,ly);
                    };break;
                case Labyrinths.ELEMENT_FIREFLY:
                    {
                        MovingCreature cr = creatures[cnt++];
                        cr.setCXCYS(MovingCreature.CREATURE_FIREFLY,lx,ly);
                    };break;
                case Labyrinths.ELEMENT_PLAYER :
                    {
                        _player_index = cnt;
                        MovingCreature cr = creatures[cnt++];
                        cr.setCXCYS(MovingCreature.CREATURE_PLAYER,lx,ly);
                    };break;
            }
        }
    }

    public MovingCreature getPlayer()
    {
        return creatures[_player_index];
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _player_scores+(_level+1)*20;
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

    public int getLevel()
    {
        return _level;
    }

    public int getStage()
    {
        return _stage;
    }
}
