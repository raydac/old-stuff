package com.igormaznitsa.GameKit_FE652.ShipsDuel;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;
import com.igormaznitsa.GameAPI.Utils.Sine;

import java.io.*;

public class ShipsDuel_SB implements StrategicBlock
{
/**
 * NOTE: ALL COORDINATES BASED ON LEFT-BOTTOM CORNER OF OBJECT RECTANGLE
 * ==================================================================
 */

 /** general constants */

    public static final int SCREEN_WIDTH = 101;
    public static final int SCREEN_HEIGHT = 64;

    private static final int BORDER_TOP = -101;
    private static final int BORDER_LEFT = -10;
    private static final int BORDER_RIGHT = SCREEN_WIDTH+10;
    private static final int BORDER_BOTTOM = SCREEN_HEIGHT;
    public static final int GROUND_POSITION = SCREEN_HEIGHT - 10;

    public static final int ROCKET_WIDTH = 4;
    public static final int ROCKET_HEIGHT = 4;
    public static final int SHIP_WIDTH = 20;
    public static final int SHIP_HEIGHT = 9;
    public static final int FLOW_MARKER_WIDTH = 15;
    public static final int FLOW_MARKER_HEIGHT = 5;

    public static final int FLOW_TOP = 0;
    public static final int FLOW_RANGE = 45;

    protected static final int PLAYER_START_X_POSITION = 10;
    protected static final int PLAYER_START_Y_POSITION = GROUND_POSITION-SHIP_HEIGHT;
    protected static final int ENEMY_START_X_POSITION = (SCREEN_WIDTH - SHIP_WIDTH - 10);
    protected static final int ENEMY_START_Y_POSITION = GROUND_POSITION-SHIP_HEIGHT;

    public static final int EXPLODE_STAGES = 4;
    public static final int WATER_EXPLODE_STAGES = 8;

    protected static final int ANGLE_CHANGE_STEP = 5;

    protected static final int GRAVITY = 150; // ~0.55

    public static final int LEVEL0 = 0;
    protected static final int LEVEL0_FLOWS = 4;
    protected static final int LEVEL0_FLOW_MAXIMAL_SPEED = 2;
    protected static final int LEVEL0_ROCKETS = 40;
    protected static final int LEVEL0_CREW = 200;

    public static final int LEVEL1 = 1;
    protected static final int LEVEL1_FLOWS = 7;
    protected static final int LEVEL1_FLOW_MAXIMAL_SPEED = 3;
    protected static final int LEVEL1_ROCKETS = 50;
    protected static final int LEVEL1_CREW = 300;

    public static final int LEVEL2 = 2;
    protected static final int LEVEL2_FLOWS = 9;
    protected static final int LEVEL2_FLOW_MAXIMAL_SPEED = 3;
    protected static final int LEVEL2_ROCKETS = 60;
    protected static final int LEVEL2_CREW = 400;

    public static final int DEMO_LEVEL = 3;
    protected static final int LEVEL3_FLOWS = 5;
    protected static final int LEVEL3_FLOW_MAXIMAL_SPEED = 2;
    protected static final int LEVEL3_ROCKETS = 60;
    protected static final int LEVEL3_CREW = 400;


    public static final int ISLAND_WIDTH = 20;
    public static final int ISLAND_HEIGHT = 10;
    public static final int ISLAND_START_X = (SCREEN_WIDTH-ISLAND_WIDTH)>>1;

    public static final int RACKET_WEIGHT = 128;



    protected ShipsDuel_GSR _game_state = null;
    protected static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());
    protected PlayerBlock _player = null;


    public ShipsDuel_SB()
    {
        _rnd = new RndGenerator(System.currentTimeMillis());
    }


    public void newGame(int level)
    {
        _game_state = null;
        _game_state = new ShipsDuel_GSR(level);
	Runtime.getRuntime().gc();

        if (_player != null) _player.initPlayer();
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {

           DataOutputStream dos = new DataOutputStream(outputStream);
	   dos.writeByte(_game_state._level);
           for (int i = 0; i < _game_state._flows_speed.length; i++) {
	       dos.writeByte(_game_state._flows_speed[i]);
	       dos.writeByte(_game_state._flows_x_markers[i]);
           }
	   dos.writeBoolean(_game_state._rocket!=null);
	   if(_game_state._rocket!=null)
           for (int i = 0; i < _game_state._rocket.length; i++) {
	       dos.writeInt(_game_state._rocket[i]);
           }
	   dos.writeBoolean(_game_state._player_turn);
	   dos.writeBoolean(_game_state.isWaterExpl);

	   dos.writeByte(_game_state._enemy._angle);
	   dos.writeByte(_game_state._enemy._power);
	   dos.writeShort(_game_state._enemy._men_left);
	   dos.writeByte(_game_state._enemy._rockets);
	   dos.writeShort(_game_state._enemy._last_dx);

	   dos.writeByte(_game_state._player._angle);
	   dos.writeByte(_game_state._player._power);
	   dos.writeShort(_game_state._player._men_left);
	   dos.writeByte(_game_state._player._rockets);
	   dos.writeShort(_game_state._player._last_dx);

	   dos.flush();
	   outputStream.flush();
	   dos = null;
	   Runtime.getRuntime().gc();

    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
           DataInputStream dos = new DataInputStream(inputStream);
	   newGame(dos.readUnsignedByte());

           for (int i = 0; i < _game_state._flows_speed.length; i++) {
	       _game_state._flows_speed[i] = dos.readByte();
	       _game_state._flows_x_markers[i] = dos.readByte();
           }

	   if (dos.readBoolean()){
	      _game_state._rocket = new int[5];
	      for (int i = 0; i < _game_state._rocket.length; i++)
	          _game_state._rocket[i] = dos.readInt();
	   }
	   _game_state._player_turn =dos.readBoolean();
	   _game_state.isWaterExpl = dos.readBoolean();

	   _game_state._enemy.changeAngle(dos.readByte());
	   _game_state._enemy._power = dos.readByte();
	   _game_state._enemy._men_left = dos.readShort();
	   _game_state._enemy._rockets = dos.readByte();
	   _game_state._enemy._last_dx = dos.readShort();

	   _game_state._player.changeAngle(dos.readByte());
	   _game_state._player._power = dos.readByte();
	   _game_state._player._men_left = dos.readShort();
	   _game_state._player._rockets = dos.readByte();
	   _game_state._player._last_dx = dos.readShort();

	   dos.close();
	   dos = null;
	   Runtime.getRuntime().gc();
    }
    public boolean isLoadSaveSupporting(){
      return true;
    }


    public void nextGameStep()
    {
        if (_player == null || _game_state == null ||
	    _game_state.endGameCheck()) return;


        // process flows
	for (int i = 0; i< _game_state._flows_x_markers.length;i++)
	  {
	    _game_state._flows_x_markers[i]+=_game_state._flows_speed[i];
	    if (_game_state._flows_speed[i]>0 && _game_state._flows_x_markers[i]>SCREEN_WIDTH) _game_state._flows_x_markers[i] = - FLOW_MARKER_WIDTH;
	    if (_game_state._flows_speed[i]<0 && _game_state._flows_x_markers[i]<- FLOW_MARKER_WIDTH) _game_state._flows_x_markers[i] = SCREEN_WIDTH;
	  }


	// process rockets
	if (_game_state._rocket!=null){
	  if (_game_state._rocket[4]>0){
	     if ((!_game_state.isWaterExpl && _game_state._rocket[4]>=EXPLODE_STAGES) ||
	         (_game_state.isWaterExpl && _game_state._rocket[4]>=WATER_EXPLODE_STAGES))
		 {
		   if(_game_state._player_turn)
	               _game_state._enemy._last_dx = (_game_state._rocket[0]>>8)-_game_state._player._x-(_game_state._player.SHIP_WIDTH>>1);
		    else
	               _game_state._player._last_dx = (_game_state._rocket[0]>>8)-_game_state._enemy._x-(_game_state._enemy.SHIP_WIDTH>>1);
	            _game_state._rocket=null;
		 }
	       else _game_state._rocket[4]++;
	     return;
	  }
	  int x = _game_state._rocket[0]>>8;
	  int y = _game_state._rocket[1]>>8;
	  if ( x > BORDER_RIGHT || x < BORDER_LEFT ||
	       y < BORDER_TOP || y > BORDER_BOTTOM ) {
		   if(_game_state._player_turn)
	               _game_state._enemy._last_dx = (_game_state._rocket[0]>>8)-_game_state._player._x-(_game_state._player.SHIP_WIDTH>>1);
		    else
	               _game_state._player._last_dx = (_game_state._rocket[0]>>8)-_game_state._enemy._x-(_game_state._enemy.SHIP_WIDTH>>1);
	     _game_state._rocket=null;
	  }
	      else
	        if (_game_state._player.isHitted(_game_state._rocket) || _game_state._enemy.isHitted(_game_state._rocket)){
		  _game_state.isWaterExpl  = false;
		  _game_state._rocket[4]++; _game_state._rocket[1]=GROUND_POSITION<<8;
	        } else if(y>=(GROUND_POSITION-ROCKET_HEIGHT)) {
		     _game_state.isWaterExpl = !(x>ISLAND_START_X && x<(ISLAND_START_X+ISLAND_WIDTH));
	             _game_state._rocket[4]++;                   // Explode stage
		     _game_state._rocket[1]=GROUND_POSITION<<8;
		  } else {
		    int wind = 0;
		    if(y<FLOW_TOP+FLOW_RANGE-1 && y>FLOW_TOP && _game_state._flows_step>0) {
		      int stream = (y-FLOW_TOP-1)/_game_state._flows_step;
		      if(stream>=0 && stream<_game_state._flows_speed.length)
		             wind = _game_state._flows_speed[stream];
		    }
		    _game_state._rocket[0]+=_game_state._rocket[2]+wind*RACKET_WEIGHT;   // x+Vx+wind
		    _game_state._rocket[1]+=_game_state._rocket[3];   // y+Vy
		    _game_state._rocket[3]+=GRAVITY;                  // Vy+GRAVITY
		  }
	} else {
	  if (_game_state._player_turn){
	  // process player
	    if(_game_state._level!=DEMO_LEVEL)
	    {
             switch(((ShipsDuel_PMR) _player.getPlayerMoveRecord(_game_state)).getDirect()){
	        case ShipsDuel_PMR.DIRECT_ANGLE_LEFT: _game_state._player.changeAngle(+ANGLE_CHANGE_STEP);break;
	        case ShipsDuel_PMR.DIRECT_ANGLE_RIGHT:_game_state._player.changeAngle(-ANGLE_CHANGE_STEP);break;
	        case ShipsDuel_PMR.DIRECT_POWER_LESS:_game_state._player.changePower(-1);break;
	        case ShipsDuel_PMR.DIRECT_POWER_MORE:_game_state._player.changePower(+1);break;
	        case ShipsDuel_PMR.DIRECT_FIRE: _game_state._rocket = _game_state._player.shot();
		                                _game_state._player._power = Ship.MIN_POWER;
		                                _game_state._player.rotateGun(64);
						_game_state._player_turn=false;
						break;
             }
	    } else {
	      _game_state._player.NextRandomTarget(_rnd.getInt(Ship.POWER_RANGE>>1)+Ship.MIN_POWER+1+(Ship.POWER_RANGE>>1));
	      _game_state._rocket = _game_state._player.shot();
	      _game_state._player_turn=false;
	    }
	  } else {
	  // process enemy
	    _game_state._enemy.NextRandomTarget(_rnd.getInt(Ship.POWER_RANGE>>1)+Ship.MIN_POWER+1+(Ship.POWER_RANGE>>1));
	    _game_state._rocket = _game_state._enemy.shot();
	    _game_state._player_turn=true;
	  }
	}
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player = playerBlock;
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
    }
}
