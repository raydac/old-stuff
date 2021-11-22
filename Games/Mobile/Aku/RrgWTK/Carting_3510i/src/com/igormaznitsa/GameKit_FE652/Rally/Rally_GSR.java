package com.igormaznitsa.GameKit_FE652.Rally;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Rally_GSR  implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYER_NORMAL   = 0;
    public static final int PLAYER_OUTFIELD = 1;
    public static final int PLAYER_COLLIDED = 2;
    public static final int PLAYER_FINISHED = 3;

    protected int _current_user_attemption;
    protected int _current_road_position;
    protected int _player_x;
    protected int _player_y;
    protected int _player_state;
    protected int _game_state;
    protected int normal_counter = 0;
    protected int move_counter = 0;
    protected int _cur_level;
    protected int _max_moving_obstacles;
    protected int _move_trigger;

    protected Obstacle [] obstacle_array;

    public Rally_GSR(int level)
    {
        _cur_level = level;

        switch(level)
        {
            case Rally_SB.LEVEL0 :{
                obstacle_array = new Obstacle[Rally_SB.LEVEL0_OBSTACLE] ;
                _max_moving_obstacles = Rally_SB.LEVEL0_MOVE_OBSTACLE;
                _move_trigger = Rally_SB.LEVEL0_MOVING_TRIGGER;
            }break;
            case Rally_SB.LEVEL1 : {obstacle_array = new Obstacle[Rally_SB.LEVEL1_OBSTACLE] ;
                _max_moving_obstacles = Rally_SB.LEVEL1_MOVE_OBSTACLE;
                _move_trigger = Rally_SB.LEVEL1_MOVING_TRIGGER;
            }
                ;break;
            case Rally_SB.LEVEL2 : {
                obstacle_array = new Obstacle[Rally_SB.LEVEL2_OBSTACLE] ;
                _max_moving_obstacles = Rally_SB.LEVEL2_MOVE_OBSTACLE;
                _move_trigger = Rally_SB.LEVEL2_MOVING_TRIGGER;
            };break;
            case Rally_SB.LEVEL_DEMO : {
                _max_moving_obstacles = 0;
                _move_trigger = 0;
            obstacle_array = new Obstacle[0] ;};break;
        }
        initObstacleArray();

        _current_road_position = Rally_SB.ROAD_LENGTH;
        _current_user_attemption = Rally_SB.USER_ATTEMPTION_NUMBER;
        _player_state = PLAYER_NORMAL;
        _player_x = Rally_SB.START_X_POSITION;
        _player_y = Rally_SB.START_Y_POSITION;
        _game_state = GAMESTATE_PLAYED;
    }

    public int getLevel()
    {
        return _cur_level;
    }

    public int getAttemptNumber()
    {
        return _current_user_attemption;
    }

    public int getCurrentRoadPosition()
    {
        return _current_road_position;
    }

    public Obstacle [] getObstacleArray()
    {
        return obstacle_array;
    }

    public int getPlayerX()
    {
        return _player_x;
    }

    public int getPlayerY()
    {
        return _player_y;
    }

    public void setPlayerX(int x)
    {
        _player_x = x;
    }

    public void setPlayerY(int y)
    {
        _player_y = y;
    }

    protected void initObstacleArray()
    {
        for(int li=0;li<obstacle_array.length;li++)
        {
            obstacle_array[li] = new Obstacle(-1,-1,Obstacle.NONE,0);
        }
    }

    public void setPlayerState(int state)
    {
        _player_state = state;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        int lnorm = 0;
        int lmov = 0;
        int lroad = 0;

        switch(_cur_level)
        {
            case Rally_SB.LEVEL0 :{ lnorm = Rally_SB.LEVEL0_SCORE_NORMAL;lmov = Rally_SB.LEVEL0_SCORE_MOVE;lroad = Rally_SB.LEVEL0_SCORE_ROAD;} ; break;
            case Rally_SB.LEVEL1 :{ lnorm = Rally_SB.LEVEL1_SCORE_NORMAL;lmov = Rally_SB.LEVEL1_SCORE_MOVE;lroad = Rally_SB.LEVEL1_SCORE_ROAD;} ; break;
            case Rally_SB.LEVEL2 :{ lnorm = Rally_SB.LEVEL2_SCORE_NORMAL;lmov = Rally_SB.LEVEL2_SCORE_MOVE;lroad = Rally_SB.LEVEL2_SCORE_ROAD;} ; break;
        }

        int scores = lnorm * normal_counter+lmov*move_counter+lroad*((Rally_SB.ROAD_LENGTH - _current_road_position)/20);

        return scores ;
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
        return 0;
    }
}
