package com.igormaznitsa.GameKit_FE652.ShipsDuel;
import com.igormaznitsa.GameAPI.Utils.Sine;

public class Ship {
    public static final int MAX_POWER = 0x70;
    public static final int MIN_POWER = 0x10;
    public static final int POWER_RANGE = MAX_POWER-MIN_POWER;
    public static final int POWER_STEP = 0x8;
    protected static final int START_ANGLE_POSITION = 64 ; // ~90 degree
    protected static final int SHIP_WIDTH = ShipsDuel_SB.SHIP_WIDTH;
    protected static final int SHIP_HEIGHT = ShipsDuel_SB.SHIP_HEIGHT;
    protected static final int GUN_SIZE = 4;
    protected static final int GUN_OFFSET = 7;
    private static final int MIN_ANGLE = 24;
    private static final int MAX_ANGLE = 100;
    private static final int PEOPLE_DENSITY = 2;
    private static final int DEATH_RATE = 60;


    public int _angle;
    public int _gun_x1;
    public int _gun_x2;
    public int _gun_y1;
    public int _gun_y2;
    public int _x;
    public int _y;
    public int _power;
    public int _men_left;
    public int _rockets;
    public int _rockets_total;

    public int _last_dx;


  public Ship(int x, int y, int crew, int rockets) {
    _x = x;
    _y = y;
    _power = MIN_POWER;
    _men_left = crew;
    _rockets = rockets;
    _rockets_total = rockets;
    rotateGun(START_ANGLE_POSITION);
    _last_dx = 0;

  }
  protected void rotateGun(int angle){
      if (angle<MIN_ANGLE) angle = MIN_ANGLE;
      else
        if (angle>MAX_ANGLE) angle = MAX_ANGLE;
      _angle = angle;

      // calculate base
	_gun_x1 = _x+(SHIP_WIDTH>>1);
	_gun_y1 = _y+(SHIP_HEIGHT>>1);
      // calculate deviation
	_gun_x2 = _gun_x1-Sine.xCoSine(GUN_OFFSET+GUN_SIZE,angle);
	_gun_y2 = _gun_y1-Sine.xSine(GUN_OFFSET+GUN_SIZE,angle);
	_gun_x1 -= Sine.xCoSine(GUN_OFFSET,angle);
	_gun_y1 -= Sine.xSine(GUN_OFFSET,angle);
  }

  protected int [] shot(){
    _rockets--;
    int [] i = {
      /* x */  (_gun_x2-(ShipsDuel_SB.ROCKET_WIDTH>>1))<<8,
      /* y */  (_gun_y2+(ShipsDuel_SB.ROCKET_HEIGHT>>1))<<8,
      /* Vx */ -(Sine.xCoSineFloat(_power,_angle)>>4),
      /* Vy */ -(Sine.xSineFloat(_power,_angle)>>4),
      /*explode stage */ 0
    };
    return i;
  }

  protected boolean isHitted(int [] rocket){
     if((rocket[1]>>8)>=(_y+3) && (rocket[0]>>8)>=_x-3 && (rocket[0]>>8)<=(_x+SHIP_WIDTH+3)) {

         int damage = ((Math.abs(rocket[3])*DEATH_RATE)>>16)+((SHIP_WIDTH>>1)+3-Math.abs(_x+(SHIP_WIDTH>>1)-(rocket[0]>>8)))*PEOPLE_DENSITY+2;
//	 System.out.println("Vd:"+((Math.abs(rocket[3])*DEATH_RATE)>>16)+" ,d:"+((SHIP_WIDTH>>1)+3-Math.abs(_x+(SHIP_WIDTH>>1)-(rocket[0]>>8))));
         if (damage<=0)damage=1;
	   _men_left-=damage;
	 if(_men_left<0)_men_left=0;

	return true;
     }
     return false;
  }

  protected void changeAngle(int angle){
    rotateGun(angle+_angle);
  }
  protected void changePower(int n){
    _power+=n*POWER_STEP;
    if(_power<MIN_POWER)_power=MIN_POWER;
    else
      if(_power>MAX_POWER)_power=MAX_POWER;
  }

  protected void NextRandomTarget(int random_power){
   if(Math.abs(_last_dx)>((SHIP_WIDTH>>1)))
   if(_last_dx>0){
      if (_angle<=MIN_ANGLE) changePower(+1);
      else
        changeAngle(-4);
   } else{
      if (_angle>=MAX_ANGLE) changePower(+1);
       else
        changeAngle(+4);
   }
//   _power = random_power;
  }

}