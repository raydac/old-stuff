package com.igormaznitsa.GameKit_FE652.StarGun;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class StarGun_GSR  implements GameStateRecord
{
/** GLOBAL STATUS */
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYER_NORMAL = 0;
    public static final int PLAYER_BURNED = 1;
    public static final int PLAYER_OUT_OF_AMMO = 2;
    public static final int PLAYER_FINISHED = 3;
    public static final int PLAYER_KILLED = 4;

    protected static final int FLY_NONE = 0;
    protected static final int FLY_LEFT = 1;
    protected static final int FLY_RIGHT = 2;


/** local variables */
    protected int _game_state;
    protected int _level;
    public int _trys_left;
    protected int _vert_cells_used;

    protected int _player_state;
    protected int _player_angle;
    protected int _player_x;
    protected int _player_y;

    public int _rockets_left;
    public int _ufo_fired;
    protected int _ufo_burned;
    public int _ufo_total;
    protected int _ufo_speed;
    protected int _ufo_frequence;
    protected int _bombs_frequence;
    protected int _bombs_fired;

    public int _gun_x1;
    public int _gun_x2;
    public int _gun_y1;
    public int _gun_y2;

    protected FlyingObject [] _enemyes = null;
    protected FlyingObject [] _rockets = null;
    protected int [] _fly_direction = new int [StarGun_SB.VERT_CELLS_TOTAL];
    protected int [] _fly_objects = new int [StarGun_SB.VERT_CELLS_TOTAL];
    protected int [] _fly_objects_distance = new int [StarGun_SB.VERT_CELLS_TOTAL];
    private int _fly_object_delay;
    public int _enemyes_counter = 0;
    public int _rockets_counter = 0;
    protected int _direction_frequency;

    public StarGun_GSR(int level)
    {
        _level = level;
        _trys_left = StarGun_SB.USER_ATTEMPTS;

	if (_enemyes == null)
	{
	   _enemyes = new FlyingObject [StarGun_SB.MAXIMAL_ENEMYES];
	   _rockets = new FlyingObject [StarGun_SB.MAXIMAL_ROCKETS];

	   for (int i = 0; i<StarGun_SB.MAXIMAL_ENEMYES; i++)
	      _enemyes[i] = new FlyingObject();

	   for (int i = 0; i<StarGun_SB.MAXIMAL_ROCKETS; i++)
	      _rockets[i] = new FlyingObject();
	}

	switch (level) {
	  case StarGun_SB.LEVEL0: {
	                            _ufo_total = StarGun_SB.LEVEL0_UFO;
                                    _ufo_speed = StarGun_SB.LEVEL0_MOVE_UFO;
                                    _ufo_frequence = StarGun_SB.LEVEL0_UFO_FREQUENCE; // in ticks
                                    _bombs_frequence = StarGun_SB.LEVEL0_BOMBS_FREQUENCE; // in ticks
                                    _rockets_left = StarGun_SB.LEVEL0_ROCKETS;
				    _vert_cells_used = StarGun_SB.LEVEL0_VERT_CELLS_USED;
				    _direction_frequency = StarGun_SB.LEVEL0_DIR_FREQUENCY;
				    _fly_object_delay = StarGun_SB.UFO_VIRTUAL_CELL_WIDTH / Math.max(1,(_ufo_speed>>8)) + 1;
				  } break;
	  case StarGun_SB.LEVEL1: {
	                            _ufo_total = StarGun_SB.LEVEL1_UFO;
                                    _ufo_speed = StarGun_SB.LEVEL1_MOVE_UFO;
                                    _ufo_frequence = StarGun_SB.LEVEL1_UFO_FREQUENCE; // in ticks
                                    _bombs_frequence = StarGun_SB.LEVEL1_BOMBS_FREQUENCE; // in ticks
                                    _rockets_left = StarGun_SB.LEVEL1_ROCKETS;
				    _vert_cells_used = StarGun_SB.LEVEL1_VERT_CELLS_USED;
				    _direction_frequency = StarGun_SB.LEVEL1_DIR_FREQUENCY;
				    _fly_object_delay = StarGun_SB.UFO_VIRTUAL_CELL_WIDTH / Math.max(1,(_ufo_speed>>8)) + 1;
				  } break;
	  case StarGun_SB.LEVEL2: {
	                            _ufo_total = StarGun_SB.LEVEL2_UFO;
                                    _ufo_speed = StarGun_SB.LEVEL2_MOVE_UFO;
                                    _ufo_frequence = StarGun_SB.LEVEL2_UFO_FREQUENCE; // in ticks
                                    _bombs_frequence = StarGun_SB.LEVEL2_BOMBS_FREQUENCE; // in ticks
                                    _rockets_left = StarGun_SB.LEVEL2_ROCKETS;
				    _vert_cells_used = StarGun_SB.LEVEL2_VERT_CELLS_USED;
				    _direction_frequency = StarGun_SB.LEVEL2_DIR_FREQUENCY;
				    _fly_object_delay = StarGun_SB.UFO_VIRTUAL_CELL_WIDTH / Math.max(1,(_ufo_speed>>8)) + 1;
				  } break;
	  case StarGun_SB.LEVEL_DEMO: {
	                            _ufo_total = StarGun_SB.LEVEL3_UFO;
                                    _ufo_speed = StarGun_SB.LEVEL3_MOVE_UFO;
                                    _ufo_frequence = StarGun_SB.LEVEL3_UFO_FREQUENCE; // in ticks
                                    _bombs_frequence = StarGun_SB.LEVEL3_BOMBS_FREQUENCE; // in ticks
                                    _rockets_left = StarGun_SB.LEVEL3_ROCKETS;
				    _vert_cells_used = StarGun_SB.LEVEL3_VERT_CELLS_USED;
				    _direction_frequency = StarGun_SB.LEVEL3_DIR_FREQUENCY;
				    _fly_object_delay = StarGun_SB.UFO_VIRTUAL_CELL_WIDTH / Math.max(1,(_ufo_speed>>8)) + 1;

				  } break;
	}

        _ufo_fired = 0;
        _ufo_burned = 0;
	_bombs_fired = 0;
	reset();
    }

    protected void reset()
    {
        for (int i = 0; i < _fly_direction.length; i++){
	     _fly_direction[i] = FLY_NONE;
	     _fly_objects[i] = 0;
	     _fly_objects_distance[i] = 0;
        }

	for (int i = 0; i < _enemyes.length; i++){
	   _enemyes[i]._type = FlyingObject.NONE;
	   //_enemyes[i]._gravity = StarGun_SB.GRAVITY;
	   //_enemyes[i]._gravity = StarGun_SB.GRAVITY;
	}
	_enemyes_counter = 0;

	for (int i = 0; i < _rockets.length; i++){
	   _rockets[i]._type = FlyingObject.NONE;
	   //_rockets[i]._gravity = StarGun_SB.GRAVITY;
	}
	_rockets_counter = 0;

        _player_state = PLAYER_NORMAL;
        _player_x = StarGun_SB.START_X_POSITION;
        _player_y = StarGun_SB.START_Y_POSITION;
        _player_angle = StarGun_SB.START_ANGLE_POSITION;
        _game_state = GAMESTATE_PLAYED;
	setGunPosition(StarGun_SB.START_ANGLE_POSITION);
    }

    public int getAttemptNumber() {
        return _trys_left;
    }

    public int getPlayerX() {
        return _player_x;
    }

    public int getPlayerY() {
        return _player_y;
    }

    public int getGameState() {
        return _game_state;
    }

    public int getPlayerState() {
        return _player_state;
    }

    public int getPlayerScores() {
        return _ufo_fired * StarGun_SB.UFO_SCORE +
	       _ufo_burned * StarGun_SB.UFO_BURNED_SCORE +
	       _bombs_fired * StarGun_SB.BOMBS_SCORE +
	       (_rockets_left+1) * StarGun_SB.LEFT_ROCKETS_SCORE +
	       (_trys_left+1) * StarGun_SB.ATTEMPTS_SCORE+
	       _level*500+
	       (_player_state==PLAYER_FINISHED?_level*2000:0);
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


    public void killPlayer() {
//        if (_trys_left != 0){
	   _trys_left--;
           _player_state = PLAYER_BURNED;
//        } else
//           _player_state = PLAYER_KILLED;
	_game_state = GAMESTATE_OVER;
    }

    private void SubRemoveEnemy(int n){
	  if((--_fly_objects[n]) <= 0) {
	     _fly_objects[n] = 0;
  	     _fly_direction[n] = FLY_NONE;
	     _fly_objects_distance[n] = 0;
	  }
    }

    /**
     * Removing enemy from array
     * @param i index
     */
    protected void removeEnemy(int i) {
/*
      String s ="RemoveEnemy-n:"+i+",vc:"+_enemyes[i]._vert_cell+", type:";
      switch(_enemyes[i]._type){
         case FlyingObject.NONE:         s+="NONE"; break;
         case FlyingObject.UFO:          s+="UFO"; break;
         case FlyingObject.BOMB:         s+="BOMB"; break;
         case FlyingObject.ROCKET:       s+="ROCKET"; break;
         case FlyingObject.UFO_BURNING:  s+="UFO BURNING"; break;
         case FlyingObject.BOMB_EXPLODE: s+="BOMB_EXPLODE"; break;
         case FlyingObject.AIR_EXPLODE:  s+="AIR_EXPLODE"; break;
         case FlyingObject.UFO_EXPLODE:  s+="UFO_EXPLODE"; break;
	default: s+="Unknown";
      }
      System.out.println(s);
*/

      if (i<_enemyes_counter && i>=0){
	if (_enemyes[i]._type == FlyingObject.UFO){
	  //if (_enemyes[i].crashing) System.out.println("!!!!!!!!!!!!!!!!!!!!!");
	   SubRemoveEnemy(_enemyes[i]._vert_cell);
	}
	_enemyes[i].remove();
      }

	--_enemyes_counter;

	if(_enemyes_counter>i) {
	  FlyingObject o = _enemyes[i];
	  _enemyes[i] = _enemyes[_enemyes_counter];
	  _enemyes[_enemyes_counter] = o;
	  o = null;
	}
    }


    /**
     * Crashes UFO
     * @param i index in enemy array
     */
    protected void crashUFO(int i) {
     // System.out.println("CrashUfo-n:"+i+",vc:"+_enemyes[i]._vert_cell);
      if (i<_enemyes_counter && i>=0){
        SubRemoveEnemy(_enemyes[i]._vert_cell);
	_enemyes[i]._type = FlyingObject.UFO_BURNING;
	_enemyes[i].gravity_enabled = true;
      }
    }

    /**
     * remove rocket from array
     * @param i index
     */
    protected void removeRocket(int i) {
      if (i<_rockets_counter && i>=0){
	if(--_rockets_counter>i) {
	  FlyingObject o = _rockets[i];
	  _rockets[i] = _rockets[_rockets_counter];
	  _rockets[_rockets_counter] = o;
	  o = null;
	}
	_rockets[_rockets_counter].remove();
      }
    }

    /**
     * Adding UFO to enemyes array
     * @param tunnel vertical cell position
     * @param direction moving left or right
     */
    synchronized void addUFO(int tunnel, int direction) {
      if (_enemyes_counter < _enemyes.length &&
          _fly_objects_distance [tunnel] <= 0)
	  {
	    int count = 0;
	    for (int i=0;i<_enemyes_counter; i++)
	       if (_enemyes[i]._type == FlyingObject.UFO || _enemyes[i]._type == FlyingObject.UFO_BURNING)
	             count++;

	    if (count>(_ufo_total-_ufo_fired)) return;
	     if (_fly_direction[tunnel] == FLY_NONE) _fly_direction[tunnel] = direction;
	     _fly_objects_distance [tunnel] = _fly_object_delay;
	     _fly_objects [tunnel]++;

	     int x = StarGun_SB.UFO_LEFT_START;
	     int Vx = _ufo_speed;

	     if (_fly_direction[tunnel] == FLY_RIGHT) {
	         x = StarGun_SB.UFO_RIGHT_START;
		 Vx = -Vx;
	     }
	     _enemyes[_enemyes_counter].rebuild(
			    FlyingObject.UFO,x,
			    StarGun_SB.UFO_TOP_START + StarGun_SB.UFO_CELL_HEIGHT +
			    tunnel*StarGun_SB.UFO_VIRTUAL_CELL_HEIGHT,
			    StarGun_SB.UFO_CELL_WIDTH,StarGun_SB.UFO_CELL_HEIGHT, Vx,0,tunnel);
            _enemyes_counter++;
      }
    }

    /**
     * Adding bomb to enemyes array
     * @param x horizontal position
     * @param y vertical position
     * @param Vx horizontal speed (positive or negative , bearer related)
     */
     protected void addBomb(int x, int y, int Vx) {
      if (_enemyes_counter < _enemyes.length) {
         _enemyes[_enemyes_counter].rebuild(FlyingObject.BOMB,x,y,
	      StarGun_SB.BOMB_CELL_WIDTH,StarGun_SB.BOMB_CELL_HEIGHT,
	      Vx,StarGun_SB.BOMB_VELOCITY_VERTICAL,0);
	_enemyes_counter++;
      }
    }

    /**
     * Adding new Rocket to rockets array
     * @param x horizontal position
     * @param y vertical position
     * @param Vx horizontal speed (related on current angle of gun)
     * @param Vy vertical speed (related on current angle of gun)
     */
     void addRocket(int x, int y, int Vx, int Vy) {
      if (_rockets_counter < _rockets.length && _rockets_left > 0) {
         _rockets[_rockets_counter].rebuild(FlyingObject.ROCKET,x,y,
	      StarGun_SB.ROCKET_CELL_WIDTH,StarGun_SB.ROCKET_CELL_HEIGHT,
	      Vx,Vy,0);
	_rockets_counter++;
	_rockets_left--;
      }
    }

    protected boolean isFinished(){
       	int _j=0;
	for(int _i=0;_i<StarGun_SB.VERT_CELLS_TOTAL;_i++)_j+=_fly_objects[_i];
	return ((_ufo_fired >_ufo_total && _j<=0)||_player_state==PLAYER_FINISHED);

    }

    public FlyingObject [] getEnemyesArray() {
        return _enemyes;
    }

    public FlyingObject [] getRocketsArray() {
        return _rockets;
    }

    /**
     * Setting the Gun rotation angle , also recalculate new coordinates of the gun
     * @param angle rotation angle
     */
    public void setGunPosition(int angle) {
      _player_angle = angle;

      // calculate base
	_gun_x1 = _player_x+(StarGun_SB.PLAYER_CELL_WIDTH>>1);
	_gun_y1 = _player_y-(StarGun_SB.PLAYER_CELL_HEIGHT>>1);
      // calculate deviation
	_gun_x2 = _gun_x1-StarGun_SB.xCoSine(StarGun_SB.GUN_OFFSET+StarGun_SB.GUN_SIZE,angle);
	_gun_y2 = _gun_y1-StarGun_SB.xSine(StarGun_SB.GUN_OFFSET+StarGun_SB.GUN_SIZE,angle);
	_gun_x1 -= StarGun_SB.xCoSine(StarGun_SB.GUN_OFFSET,angle);
	_gun_y1 -= StarGun_SB.xSine(StarGun_SB.GUN_OFFSET,angle);
    }


}
