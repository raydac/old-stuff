package com.igormaznitsa.GameKit_FE652.StarGun;

public class FlyingObject
{
    public static final int NONE = -1;
    public static final int UFO = 0;
    public static final int BOMB = 1;
    public static final int ROCKET = 2;
    public static final int UFO_BURNING = 3;
    public static final int BOMB_EXPLODE = 4;
    public static final int AIR_EXPLODE = 5;
    public static final int UFO_EXPLODE = 6;
//    public static final

    public int _x;
    public int _y;
    protected int _xInc;
    protected int _yInc;
    public int _Vx;
    public int _Vy;
    public int _type = NONE;
    public int CELL_HEIGHT;
    public int CELL_WIDTH;
//    public int _lastTicks = 0; // time before object will be destroyed
    protected int _vert_cell;
    protected int _shot_delay;
    private int _gravity = StarGun_SB.GRAVITY;
    protected boolean gravity_enabled = false;

    private static int crash_delay = 2;
    protected int crash_counter;
    public int crash_stage;
    public boolean crashing = false;

    public FlyingObject(){
    }

    public void nextStep() {
       if (crashing){
        if(--crash_counter <= 0){
	 crash_counter = crash_delay;
	 crash_stage --;
        }
       }// else {
          _x = (_xInc+=_Vx)>>8;
          _y = (_yInc+=(_Vy+=(gravity_enabled?_gravity:0)))>>8;
          if (_shot_delay != 0)_shot_delay--;
       //}
    }

    public void setPosition(int x, int y) {
        _xInc = x<<8;
        _yInc = y<<8;
	_x = x;
	_y = y;
    }
    /**
     *
     * @param type _type
     * @param x _x
     * @param y _y
     * @param c_width CELL_WIDTH
     * @param c_height CELL_HEIGHT
     * @param Vx _Vx
     * @param Vy _Vy
     * @param vert_cell _vertcell
     */
    public void rebuild (int type, int x, int y, int c_width, int c_height, int Vx, int Vy, int vert_cell){
      setPosition (x,y);
      _type = type;
      _Vx = Vx;
      _Vy = Vy;
      CELL_HEIGHT = c_height;
      CELL_WIDTH = c_width;
      crashing = false;
      //_vert_cell = vert_cell;
      if (Vy == 0) gravity_enabled = false;
         else
	     gravity_enabled = true;
    }

    public void crashIt(){
       crash_counter = crash_delay;
       crashing = true;
       _Vy = 0;
       gravity_enabled = false;

      switch (_type) {
	case ROCKET:
	           _type = _type;
	case BOMB: _type = BOMB_EXPLODE;
	           crash_stage = 2;
		   _Vx=0;
		   _xInc-=0x400;
		   _x=_xInc>>8;
	           break;
	case UFO_BURNING:
		  gravity_enabled =false;
		  _Vy = 0x100;

	case UFO: _type = UFO_EXPLODE;
	          crash_stage = 5;
		  //gravity_enabled =true;
		  _Vy = 0x100;
		  crash_delay = 3;
	           break;
	case AIR_EXPLODE:
	           crash_stage = 2;
	           break;
      }
    }
    public void remove(){
      crash_delay = 2;
       crashing = false;
       gravity_enabled = false;
       _type = NONE;
       _Vx = 0;
       _Vy = 0;

    }
}
