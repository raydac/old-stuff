package com.igormaznitsa.GameKit_FE652.ShipsDuel;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.Utils.Sine;

public class ShipsDuel_GSR  implements GameStateRecord
{
/** GLOBAL STATUS */
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYER_NORMAL = 0;
    public static final int PLAYER_OUT_OF_AMMO = 1;
    public static final int PLAYER_FINISHED = 2;
    public static final int PLAYER_KILLED = 3;

/** local variables */
    protected int _game_state;
    protected int _level;
    protected int _player_state;
    protected Ship _player;
    protected Ship _enemy;
    protected int[] _flows_speed;
    protected int[] _flows_x_markers;
    public int _flows_step;
    public boolean _player_turn;
    protected int[] _rocket;
    public boolean isWaterExpl;

    public ShipsDuel_GSR(int level)
    {
        _level = level;
	_player = null;
	_enemy = null;
	_flows_speed = null;
	_flows_x_markers = null;
	_rocket = null;
        int flows_number = 1, flows_max_speed = 1;

	_game_state = GAMESTATE_PLAYED;
	_player_state = PLAYER_NORMAL;
	_player_turn = ShipsDuel_SB._rnd.getInt(100)<50;

	switch (level) {
	  case ShipsDuel_SB.LEVEL0: {
				     _player = new Ship(ShipsDuel_SB.PLAYER_START_X_POSITION,ShipsDuel_SB.PLAYER_START_Y_POSITION,ShipsDuel_SB.LEVEL0_CREW,ShipsDuel_SB.LEVEL0_ROCKETS);
				     _enemy = new Ship(ShipsDuel_SB.ENEMY_START_X_POSITION,ShipsDuel_SB.ENEMY_START_Y_POSITION,ShipsDuel_SB.LEVEL0_CREW,99999);
				     flows_number = ShipsDuel_SB.LEVEL0_FLOWS;
				     flows_max_speed = ShipsDuel_SB.LEVEL0_FLOW_MAXIMAL_SPEED;
                                     _enemy._angle = 64-16;
                                     _enemy._power = Ship.MAX_POWER>>1;
				  } break;
	  case ShipsDuel_SB.LEVEL1: {
				     _player = new Ship(ShipsDuel_SB.PLAYER_START_X_POSITION,ShipsDuel_SB.PLAYER_START_Y_POSITION,ShipsDuel_SB.LEVEL1_CREW,ShipsDuel_SB.LEVEL1_ROCKETS);
				     _enemy = new Ship(ShipsDuel_SB.ENEMY_START_X_POSITION,ShipsDuel_SB.ENEMY_START_Y_POSITION,ShipsDuel_SB.LEVEL1_CREW,99999);
				     flows_number = ShipsDuel_SB.LEVEL1_FLOWS;
				     flows_max_speed = ShipsDuel_SB.LEVEL1_FLOW_MAXIMAL_SPEED;
                                     _enemy._angle = 64-16;
                                     _enemy._power = Ship.MAX_POWER-10;
				  } break;
	  case ShipsDuel_SB.LEVEL2: {
				     _player = new Ship(ShipsDuel_SB.PLAYER_START_X_POSITION,ShipsDuel_SB.PLAYER_START_Y_POSITION,ShipsDuel_SB.LEVEL2_CREW,ShipsDuel_SB.LEVEL2_ROCKETS);
				     _enemy = new Ship(ShipsDuel_SB.ENEMY_START_X_POSITION,ShipsDuel_SB.ENEMY_START_Y_POSITION,ShipsDuel_SB.LEVEL2_CREW,99999);
				     flows_number = ShipsDuel_SB.LEVEL2_FLOWS;
				     flows_max_speed = ShipsDuel_SB.LEVEL2_FLOW_MAXIMAL_SPEED;
                                     _enemy._angle = 64-16;
                                     _enemy._power = Ship.MAX_POWER;
				  } break;
	  case ShipsDuel_SB.DEMO_LEVEL: {
				     _player = new Ship(ShipsDuel_SB.PLAYER_START_X_POSITION,ShipsDuel_SB.PLAYER_START_Y_POSITION,ShipsDuel_SB.LEVEL2_CREW,ShipsDuel_SB.LEVEL3_ROCKETS);
				     _enemy = new Ship(ShipsDuel_SB.ENEMY_START_X_POSITION,ShipsDuel_SB.ENEMY_START_Y_POSITION,ShipsDuel_SB.LEVEL3_CREW,99999);
				     flows_number = ShipsDuel_SB.LEVEL3_FLOWS;
				     flows_max_speed = ShipsDuel_SB.LEVEL3_FLOW_MAXIMAL_SPEED;
                                     _enemy._angle = 64-16;
                                     _enemy._power = Ship.MAX_POWER-10;
                                     _player._angle = 64+16;
                                     _player._power = Ship.MAX_POWER-10;
				  } break;
	}

	_flows_speed = new int[flows_number];
	_flows_x_markers = new int[flows_number];
        _flows_step = ShipsDuel_SB.FLOW_RANGE/flows_number;

        for (int i = 0; i < flows_number; i++) {
            _flows_speed[i] = ((i&1)==0?1:-1)*(ShipsDuel_SB._rnd.getInt(flows_max_speed +1)+1);
	    _flows_x_markers[i] = ShipsDuel_SB._rnd.getInt(ShipsDuel_SB.SCREEN_WIDTH);
        }


    }

    public boolean endGameCheck(){
       if (_game_state != GAMESTATE_OVER)
       if (_rocket != null) return false;
       else
       if (_player._rockets <= 0){
	 _game_state = GAMESTATE_OVER;
	 _player_state = PLAYER_OUT_OF_AMMO;
       } else
       if (_player._men_left <= 0){
	 _game_state = GAMESTATE_OVER;
	 _player_state = PLAYER_KILLED;
       } else
       if (_enemy._men_left <= 0){
	 _game_state = GAMESTATE_OVER;
	 _player_state = PLAYER_FINISHED;
       } else
       return false;
      return true;
    }


    public int getGameState() {
        return _game_state;
    }

    public int getPlayerState() {
        return _player_state;
    }

    public int getPlayerScores() {
        return
	       _player._men_left*5 + _player._rockets*2+
	       _level*100+
	       (_player_state==PLAYER_FINISHED?_level*1000:0);
    }

    public Ship getPlayer() {
       return _player;
    }
    public Ship getEnemy() {
       return _enemy;
    }
    public int [] getFlowsX() {
       return _flows_x_markers;
    }

    public int [] getRocket() {
       return _rocket;
    }

    public int getAIScores() {
        return 0;
    }


    public int getAIState() {
        return 0;
    }

    public int getStage() {
        return 0;
    }

    public int getLevel() {
        return _level;
    }

}
