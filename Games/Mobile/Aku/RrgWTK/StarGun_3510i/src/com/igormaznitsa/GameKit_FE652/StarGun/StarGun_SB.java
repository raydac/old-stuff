package com.igormaznitsa.GameKit_FE652.StarGun;

import com.igormaznitsa.GameAPI.GameStateRecord;
import java.util.Random;

import java.io.*;

public class StarGun_SB extends Random
{
/**
 * NOTE: ALL COORDINATES BASED ON LEFT-BOTTOM CORNER OF OBJECT RECTANGLE
 * ==================================================================
 */

 /** control constants */

    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_RIGHT = 2;
    public static final int DIRECT_ANGLE_LEFT = 4;
    public static final int DIRECT_ANGLE_RIGHT = 8;
    public static final int DIRECT_FIRE = 16;

 /** general constants */

    public static final int SCREEN_WIDTH = 101;
    public static final int SCREEN_HEIGHT = 64;
    protected static final int MAXIMAL_ENEMYES = 30;
    protected static final int MAXIMAL_ROCKETS = 5;

    private static final int BORDER_TOP = -101;
    private static final int BORDER_LEFT = 0;
    private static final int BORDER_RIGHT = 0;
    private static final int BORDER_BOTTOM = 0;
    private static final int GROUND_POSITION = SCREEN_HEIGHT - 8;

    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;
    public static final int LEVEL_DEMO = 3;

    private static final int START_DELAY = 20; // in ticks
    protected static final int GRAVITY = 150; // ~0.55
    protected static final int VERT_CELLS_TOTAL = 4;

    public static final int UFO_SCORE = 3;
    public static final int UFO_BURNED_SCORE = 2;
    public static final int BOMBS_SCORE = 1;
    public static final int LEFT_ROCKETS_SCORE = 1;
    public static final int ATTEMPTS_SCORE = 10;
    private static final int ROCKETS_PER_UFO = 2;
    private static final int ROCKETS_PER_BURNING_UFO = 0;
    private static final int ROCKETS_PER_BOMB = 1;


    /** Initial player parameters */

    public static final int PLAYER_CELL_WIDTH = 15;
    public static final int PLAYER_CELL_HEIGHT = 10;
    public static final int PLAYER_LEFT_BORDER = 0;
    public static final int PLAYER_RIGHT_BORDER = SCREEN_WIDTH - PLAYER_CELL_WIDTH;

    protected static final int START_X_POSITION = (SCREEN_WIDTH - PLAYER_CELL_WIDTH) >> 1;
    protected static final int START_Y_POSITION = GROUND_POSITION;
    protected static final int START_ANGLE_POSITION = 64 ; // ~90 degree

    private static final int ANGLE_PLAYER_SPEED = 5;
    private static final int MIN_ANGLE = 24;
    private static final int MAX_ANGLE = 100;
    private static final int HORIZ_PLAYER_SPEED = 2;
    private static final int PLAYER_SHOT_DELAY = 7;

    public static final int GUN_SIZE = 2;
    public static final int GUN_OFFSET = 5;

    public static final int USER_ATTEMPTS = 3;


    /** Initial Flying objects Parametr */
    // ROCKETS
    public static final int ROCKET_CELL_HEIGHT = 5;
    public static final int ROCKET_CELL_WIDTH = 5;
    protected static final int ROCKET_VELOCITY = 0x8;

    // UFOS
    public static final int UFO_CELL_HEIGHT = 12;
    public static final int UFO_CELL_WIDTH = 14;
    protected static final int UFO_VIRTUAL_CELL_HEIGHT = 10;
    protected static final int UFO_VIRTUAL_CELL_WIDTH = 12;
    protected static final int UFO_TOP_START = 2;
    protected static final int UFO_RIGHT_START = SCREEN_WIDTH;
    protected static final int UFO_LEFT_START = -UFO_CELL_WIDTH;
    protected static final int UFO_BURN_FREQUENCY = 2;
    protected static final int UFO_SHOT_DELAY = 3;

    // BOMBS
    public static final int BOMB_CELL_HEIGHT = 5;
    public static final int BOMB_CELL_WIDTH = 5;
    protected static final int BOMB_VELOCITY_HORIZONTAL = 0x100;
    protected static final int BOMB_VELOCITY_VERTICAL = 0x1;
    private static final int BOMB_START_X = UFO_CELL_WIDTH>>1;
    private static final int BOMB_START_Y = BOMB_CELL_HEIGHT+2;


/** level constants */

    protected static final int LEVEL0_UFO = 35;
    protected static final int LEVEL0_MOVE_UFO = 0x100;
    protected static final int LEVEL0_UFO_FREQUENCE = 30;
    protected static final int LEVEL0_BOMBS_FREQUENCE = 40;
    protected static final int LEVEL0_VERT_CELLS_USED = 2;
    protected static final int LEVEL0_ROCKETS = 10;
    protected static final int LEVEL0_DIR_FREQUENCY = 3;


    protected static final int LEVEL1_UFO = 50;
    protected static final int LEVEL1_MOVE_UFO = 0x200;
    protected static final int LEVEL1_UFO_FREQUENCE = 20;
    protected static final int LEVEL1_BOMBS_FREQUENCE = 30;
    protected static final int LEVEL1_VERT_CELLS_USED = 3;
    protected static final int LEVEL1_ROCKETS = 25;
    protected static final int LEVEL1_DIR_FREQUENCY = 2;

    protected static final int LEVEL2_UFO = 60;
    protected static final int LEVEL2_MOVE_UFO = 0x300;
    protected static final int LEVEL2_UFO_FREQUENCE = 10;
    protected static final int LEVEL2_BOMBS_FREQUENCE = 30;
    protected static final int LEVEL2_VERT_CELLS_USED = 3;
    protected static final int LEVEL2_ROCKETS = 40;
    protected static final int LEVEL2_DIR_FREQUENCY = 1;

    protected static final int LEVEL3_UFO = 60;
    protected static final int LEVEL3_MOVE_UFO = 0x300;
    protected static final int LEVEL3_UFO_FREQUENCE = 10;
    protected static final int LEVEL3_BOMBS_FREQUENCE = 30;
    protected static final int LEVEL3_VERT_CELLS_USED = 3;
    protected static final int LEVEL3_ROCKETS = 40;
    protected static final int LEVEL3_DIR_FREQUENCY = 1;

    private StarGun_GSR _game_state;
    private int _moves_counter;
    private int _shot_delay;
    private int _game_level;
    private int _demo_step=2;
    public int i_Button = DIRECT_NONE;

  public final static int [] sineTable = new int[]{
        0,6,12,18,25,31,37,43,49,56,62,68,74,80,86,92,97,103,109,115,120,126,131,136,142,147,152,157,162,167,
        171,176,181,185,189,193,197,201,205,209,212,216,219,222,225,228,231,234,236,238,241,243,244,246,248,249,251,252,253,254,
        254,255,255,255,256,255,255,255,254,254,253,252,251,249,248,246,244,243,241,238,236,234,231,228,225,222,219,216,212,209,
        205,201,197,193,189,185,181,176,171,167,162,157,152,147,142,136,131,126,120,115,109,103,97,92,86,80,74,68,62,56,
        49,43,37,31,25,18,12,6,0,-6,-12,-18,-25,-31,-37,-43,-49,-56,-62,-68,-74,-80,-86,-92,-97,-103,-109,-115,-120,-126,
        -131,-136,-142,-147,-152,-157,-162,-167,-171,-176,-181,-185,-189,-193,-197,-201,-205,-209,-212,-216,-219,-222,-225,-228,-231,-234,-236,-238,-241,-243,
        -244,-246,-248,-249,-251,-252,-253,-254,-254,-255,-255,-255
   };
   public static int xSine(int x, int index){        return (x*sineTable[index&127])>>8;      }
   public static int xCoSine(int x, int index){      return (x*sineTable[(index&127)+64])>>8; }
   public static int xSineFloat(int x, int index){   return (x*sineTable[index&127]);         }
   public static int xCoSineFloat(int x, int index){ return (x*sineTable[(index&127)+64]);    }



    public StarGun_SB()
    {
        super(System.currentTimeMillis());
    }

    public int getInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(nextInt())*(long)limit)>>>31);
       return limit;
    }

    public void resumeGame()
    {
        if (_game_state._trys_left < 0) _game_state._player_state = _game_state.PLAYER_KILLED;
        if (_game_state.getPlayerState()!=_game_state.PLAYER_FINISHED &&
	    _game_state.getPlayerState()!=_game_state.PLAYER_KILLED) {
	    if (_game_state._rockets_left == 0) _game_state._rockets_left = 5;
            _game_state.reset();
        }
	_shot_delay = 0;
    }


    public void init()
    {
    }

    public void unload()
    {
    }

    public void newGame(int level)
    {
        _game_state = null;
	Runtime.getRuntime().gc();

        _game_state = new StarGun_GSR(level);
        _game_level = level;
	_shot_delay = 0;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {

           DataOutputStream dos = new DataOutputStream(outputStream);
	   dos.writeByte(_game_level);
	   dos.writeByte(_shot_delay);

	   dos.writeByte(_game_state._trys_left);
	   dos.writeByte(_game_state._player_x);
//	   dos.writeShort(_game_state._player_y);
	   dos.writeByte(_game_state._player_angle);
	   dos.writeByte(_game_state._rockets_left);
	   dos.writeByte(_game_state._ufo_fired);
	   dos.writeByte(_game_state._ufo_burned);

	   dos.writeByte(_game_state._enemyes_counter);
	   dos.writeByte(_game_state._rockets_counter);

	   for(int i=0;i<VERT_CELLS_TOTAL;i++){
	      dos.writeByte(_game_state._fly_direction[i]);
	      dos.writeByte(_game_state._fly_objects[i]);
	      dos.writeByte(_game_state._fly_objects_distance[i]);
	   }
	   dos.flush();
	   outputStream.flush();

	   for(int i=0;i<_game_state._enemyes_counter;i++){
	      dos.writeByte(_game_state._enemyes[i]._type);
	      dos.writeByte(_game_state._enemyes[i]._vert_cell);
	      dos.writeShort(_game_state._enemyes[i]._Vx);
	      dos.writeShort(_game_state._enemyes[i]._Vy);
	      dos.writeShort(_game_state._enemyes[i]._xInc);
	      dos.writeShort(_game_state._enemyes[i]._yInc);
	      dos.writeByte(_game_state._enemyes[i].crash_counter);
	      dos.writeByte(_game_state._enemyes[i].crash_stage);
	      dos.writeBoolean(_game_state._enemyes[i].gravity_enabled);
	      dos.writeBoolean(_game_state._enemyes[i].crashing);
	   }

	   for(int i=0;i<_game_state._rockets_counter;i++){
	      dos.writeShort(_game_state._rockets[i]._Vx);
	      dos.writeShort(_game_state._rockets[i]._Vy);
	      dos.writeShort(_game_state._rockets[i]._xInc);
	      dos.writeShort(_game_state._rockets[i]._yInc);
	   }

	   dos.flush();
	   outputStream.flush();
	   dos = null;
	   System.gc();

    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
           DataInputStream dos = new DataInputStream(inputStream);
	   _game_level = dos.readUnsignedByte();
	   newGame(_game_level);

	   _shot_delay = dos.readUnsignedByte();

	   _game_state._trys_left = dos.readUnsignedByte();
	   _game_state._player_x = dos.readByte();
	   _game_state.setGunPosition(dos.readUnsignedByte());
	   _game_state._rockets_left = dos.readUnsignedByte();
	   _game_state._ufo_fired = dos.readUnsignedByte();
	   _game_state._ufo_burned = dos.readUnsignedByte();

	   _game_state._enemyes_counter = dos.readUnsignedByte();
	   _game_state._rockets_counter = dos.readUnsignedByte();

	   for(int i=0;i<VERT_CELLS_TOTAL;i++){
	      _game_state._fly_direction[i] = dos.readByte();
	      _game_state._fly_objects[i] = dos.readByte();
	      _game_state._fly_objects_distance[i] = dos.readByte();
	   }

	   for(int i=0;i<_game_state._enemyes_counter;i++){
	      _game_state._enemyes[i]._type = dos.readByte();
	      _game_state._enemyes[i]._vert_cell = dos.readByte();
	      _game_state._enemyes[i]._Vx = dos.readShort();
	      _game_state._enemyes[i]._Vy = dos.readShort();
	      _game_state._enemyes[i]._xInc = dos.readShort();
	      _game_state._enemyes[i]._yInc = dos.readShort();
	      _game_state._enemyes[i]._x = _game_state._enemyes[i]._xInc>>8;
	      _game_state._enemyes[i]._y = _game_state._enemyes[i]._yInc>>8;
	      _game_state._enemyes[i].crash_counter = dos.readByte();
	      _game_state._enemyes[i].crash_stage = dos.readByte();
	      _game_state._enemyes[i].gravity_enabled = dos.readBoolean();
	      _game_state._enemyes[i].crashing = dos.readBoolean();
	      switch (_game_state._enemyes[i]._type){
		case FlyingObject.UFO:
		case FlyingObject.UFO_BURNING:
		case FlyingObject.UFO_EXPLODE:
					        _game_state._enemyes[i].CELL_WIDTH = UFO_CELL_WIDTH;
					        _game_state._enemyes[i].CELL_HEIGHT = UFO_CELL_HEIGHT;
		                                break;
		case FlyingObject.BOMB:
		case FlyingObject.BOMB_EXPLODE:
		case FlyingObject.AIR_EXPLODE:
					        _game_state._enemyes[i].CELL_WIDTH = BOMB_CELL_WIDTH;
					        _game_state._enemyes[i].CELL_HEIGHT = BOMB_CELL_HEIGHT;
		                                break;
		default:
					        _game_state._enemyes[i].CELL_WIDTH = 0;
					        _game_state._enemyes[i].CELL_HEIGHT = 0;

	      }

	   }
	   for(int i=0;i<_game_state._rockets_counter;i++){
	      _game_state._rockets[i]._type = FlyingObject.ROCKET;
	      _game_state._rockets[i].CELL_WIDTH = ROCKET_CELL_WIDTH;
	      _game_state._rockets[i].CELL_HEIGHT = ROCKET_CELL_HEIGHT;
	      _game_state._rockets[i]._Vx = dos.readShort();
	      _game_state._rockets[i]._Vy = dos.readShort();
	      _game_state._rockets[i]._xInc = dos.readShort();
	      _game_state._rockets[i]._yInc = dos.readShort();
	      _game_state._rockets[i]._x = _game_state._rockets[i]._xInc>>8;
	      _game_state._rockets[i]._y = _game_state._rockets[i]._yInc>>8;
	      _game_state._rockets[i].gravity_enabled = true;
	   }

	   dos.close();
	   dos = null;
	   System.gc();

    }
    public boolean isLoadSaveSupporting(){
      return true;
    }


    public void nextGameStep()
    {
        if (_game_state._game_state == _game_state.GAMESTATE_OVER) return;

        if (_game_state._rockets_left == 0 && _game_state._rockets_counter == 0) {
	  _game_state.killPlayer();
          if (_game_state._player_state == _game_state.PLAYER_BURNED)
	    _game_state._player_state = _game_state.PLAYER_OUT_OF_AMMO;
	  return;
        }

	if (_game_state._ufo_fired > _game_state._ufo_total){
	      _game_state._player_state = _game_state.PLAYER_FINISHED;
	      _game_state._game_state = _game_state.GAMESTATE_OVER;
	}

	if (_shot_delay>0)_shot_delay--;

	for(int i=0; i<VERT_CELLS_TOTAL; i++)
	  if (_game_state._fly_objects_distance[i]>0)_game_state._fly_objects_distance[i]--;


// Processing next step and collision of objects

        if (_game_level == LEVEL_DEMO) {
	   if (_game_state._player_angle>100) {_demo_step=-2;i_Button = DIRECT_FIRE | DIRECT_RIGHT;}
	   if (_game_state._player_angle<20) {_demo_step=2;i_Button = DIRECT_FIRE | DIRECT_LEFT;}
//	   _game_state._player_angle+=_demo_step;
	   _game_state.setGunPosition(_game_state._player_angle+_demo_step);

        }
//	return;

	FlyingObject fy;   // temporary pointer

	// move rockets
	for (int i=0; i<_game_state._rockets_counter; ) {
            fy = _game_state._rockets[i];
	    if (StepAheadAndCheckForRemove(fy))
	            _game_state.removeRocket(i);
	       else {
//	         if (_game_level>=LEVEL2)
		     if (PlayerIntersects(fy))
		     {
		          _game_state.killPlayer();
			  return;
		     }

		     if (fy._y >= GROUND_POSITION){
			  _game_state.addBomb(fy._x,GROUND_POSITION,0);
	                  _game_state.removeRocket(i);
		     }
		        else
	                      i++;
		 }
	}

	// move enemyes
	for (int i=0; i<_game_state._enemyes_counter; /* i++ */) {

	    fy = _game_state._enemyes[i];

	    if (StepAheadAndCheckForRemove(fy))
	            _game_state.removeEnemy(i);
	      else
	       {
		boolean ObjectKilled = false;
	         for (int j=0; j<_game_state._rockets_counter; )
	            if (Intersects(fy,_game_state._rockets[j]))
		    {

			 switch (fy._type) {

			    case FlyingObject.UFO:

	                          _game_state.removeRocket(j);

				  if (getInt(UFO_BURN_FREQUENCY)==UFO_BURN_FREQUENCY)
				  {
				    _game_state.crashUFO(i);
				    _game_state._rockets_left += ROCKETS_PER_BURNING_UFO;
				    _game_state._ufo_burned++;
				  }
				    else
				    {
				       _game_state._ufo_fired++;
				       _game_state._rockets_left += ROCKETS_PER_UFO;
				       //_game_state.removeEnemy(i);
				       fy.crashIt();

				    }
				  break;

			    case FlyingObject.UFO_BURNING:

	                             _game_state.removeRocket(j);

				     _game_state._ufo_fired++;
				     _game_state._rockets_left += ROCKETS_PER_UFO;
				     //_game_state.removeEnemy(i);
				       fy.crashIt();
				     if (_game_state._ufo_fired > _game_state._ufo_total)
				     {
				         _game_state._player_state = _game_state.PLAYER_FINISHED;
				         _game_state._game_state = _game_state.GAMESTATE_OVER;
				     }
				  break;

			    case FlyingObject.BOMB:

	                          _game_state.removeRocket(j);

				  _game_state._bombs_fired++;
				  _game_state._rockets_left += ROCKETS_PER_BOMB;
				  fy._type = fy.AIR_EXPLODE;
				  fy.crashIt();
				  //_game_state.removeEnemy(i);
				  break;

			    default:
			 }
	                 ObjectKilled = true;
			 break;

	            } else j++;

	       if (!ObjectKilled)
	              switch (fy._type) {
			 case FlyingObject.BOMB :
			 case FlyingObject.UFO_BURNING :
			                     if (PlayerIntersects(fy)){
					       _game_state.killPlayer();
			                     } else
					        if (fy._y >= GROUND_POSITION) {
						  if (fy._type == FlyingObject.UFO_BURNING) _game_state._ufo_fired++;
						  fy.crashIt();
						  fy._y = GROUND_POSITION-2;
		                                  fy._Vx=0;
		                                  fy._Vy=0;
			                          fy.gravity_enabled = false;
					        }
			                break;
			 case FlyingObject.UFO :
			                if (_game_state._enemyes_counter < _game_state._enemyes.length && fy._shot_delay==0)
			                  if (getInt(_game_state._bombs_frequence) >= _game_state._bombs_frequence){
		                            _game_state.addBomb(fy._x+BOMB_START_X, fy._y+BOMB_START_Y,
					                       (fy._Vx>0?BOMB_VELOCITY_HORIZONTAL:-BOMB_VELOCITY_HORIZONTAL));
					    fy._shot_delay = UFO_SHOT_DELAY;
			                  }

			                break;
			 default:
	              }
		  i++;
	       }
	}
        if (getInt(_game_state._ufo_frequence) == _game_state._ufo_frequence)
	        _game_state.addUFO(getInt(_game_state._vert_cells_used-1),
		      (getInt(_game_state._direction_frequency)==_game_state._direction_frequency?
		       _game_state.FLY_LEFT:_game_state.FLY_RIGHT));
		//addEnemy(new UFO(-15,(getInt(8)>>3)*14+14,_game_state._ufo_speed,0));


// follow by pressed keys
        if ((i_Button & DIRECT_LEFT)>0) {
                    _game_state._player_x -= HORIZ_PLAYER_SPEED;
                    if (_game_state._player_x < 0) {
                        _game_state._player_x = 0;
                    }
		    _game_state.setGunPosition(_game_state._player_angle);
        }
        if ((i_Button & DIRECT_RIGHT)>0) {
                    _game_state._player_x += HORIZ_PLAYER_SPEED;
                    if (_game_state._player_x > StarGun_SB.PLAYER_RIGHT_BORDER) {
                        _game_state._player_x = StarGun_SB.PLAYER_RIGHT_BORDER;
                    }
		    _game_state.setGunPosition(_game_state._player_angle);
        }
        if ((i_Button & DIRECT_ANGLE_LEFT)>0) {
	            _game_state._player_angle += ANGLE_PLAYER_SPEED;
		    if (_game_state._player_angle > MAX_ANGLE) _game_state._player_angle = MAX_ANGLE;
		    _game_state.setGunPosition(_game_state._player_angle);
        }
        if ((i_Button & DIRECT_ANGLE_RIGHT)>0) {
	            _game_state._player_angle -= ANGLE_PLAYER_SPEED;
		    if (_game_state._player_angle < MIN_ANGLE) _game_state._player_angle = MIN_ANGLE;
		    _game_state.setGunPosition(_game_state._player_angle);
        }
        if ((i_Button & DIRECT_FIRE)>0 && _shot_delay<=0) {
		     int Vx = -xCoSineFloat(ROCKET_VELOCITY,_game_state._player_angle);
		     int Vy = -xSineFloat(ROCKET_VELOCITY,_game_state._player_angle);
		     int bx = _game_state._gun_x2-(ROCKET_CELL_WIDTH>>1);
		     int by = _game_state._gun_y2+3;//(Math.abs(Vx)<256?10:13);//+(ROCKET_CELL_HEIGHT>>1);

		     _game_state.addRocket(bx,by,Vx,Vy);
		     _shot_delay = PLAYER_SHOT_DELAY;

        }





    }

    private boolean Intersects(FlyingObject f1, FlyingObject f2) {
       switch(f1._type){
        case FlyingObject.UFO :
        case FlyingObject.UFO_BURNING :
	        int w = UFO_VIRTUAL_CELL_WIDTH, h = UFO_VIRTUAL_CELL_HEIGHT,
		    x = f1._x+((f1.CELL_WIDTH-w)>>1), y = f1._y-((f1.CELL_HEIGHT-h)>>1);
	        return !((x + w <= f2._x) ||
	 	        (y + h <= f2._y) ||
		        (x >= f2._x + f2.CELL_WIDTH) ||
		        (y >= f2._y + f2.CELL_HEIGHT));
	default:
	   return !((f1._x + f1.CELL_WIDTH <= f2._x) ||
	 	   (f1._y + f1.CELL_HEIGHT <= f2._y) ||
		   (f1._x >= f2._x + f2.CELL_WIDTH) ||
		   (f1._y >= f2._y + f2.CELL_HEIGHT));
       }
    }

    private boolean PlayerIntersects(FlyingObject f1) {
       switch(f1._type){
        case FlyingObject.UFO :
        case FlyingObject.UFO_BURNING :
	        int w = UFO_VIRTUAL_CELL_WIDTH, h = UFO_VIRTUAL_CELL_HEIGHT,
		    x = f1._x+((f1.CELL_WIDTH-w)>>1), y = f1._y-((f1.CELL_HEIGHT-h)>>1);
	        return !((x + w <= _game_state._player_x+1) ||
	 	        (y + h <= _game_state._player_y+1) ||
		        (x >= _game_state._player_x + PLAYER_CELL_WIDTH-2) ||
		        (y >= _game_state._player_y + PLAYER_CELL_HEIGHT-2));
	default:
	return !((f1._x + f1.CELL_WIDTH <= _game_state._player_x+1) ||
		 (f1._y + f1.CELL_HEIGHT <= _game_state._player_y+1) ||
		 (f1._x >= _game_state._player_x + PLAYER_CELL_WIDTH-2) ||
		 (f1._y >= _game_state._player_y + PLAYER_CELL_HEIGHT-2));
       }
    }
    private boolean StepAheadAndCheckForRemove(FlyingObject fy) {
         if (fy!=null){
	     fy.nextStep();
	     if (fy.crashing){
	       if (fy.crash_stage < 0) return true;
	         else return false;
	     } else
	       if (fy._type != fy.NONE)
	         if ( fy._x < SCREEN_WIDTH /* + BORDER_RIGHT */ &&
	            fy._x > -fy.CELL_WIDTH /* + BORDER_LEFT */ &&
		    fy._y < SCREEN_HEIGHT /* + BORDER_BOTTOM */ &&
		    fy._y > -fy.CELL_HEIGHT  + BORDER_TOP ){
		      if (fy._y >= GROUND_POSITION) fy._y = GROUND_POSITION+1;
		       return false;
	         }
         } return true;
    }


    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1000;
    }

}
