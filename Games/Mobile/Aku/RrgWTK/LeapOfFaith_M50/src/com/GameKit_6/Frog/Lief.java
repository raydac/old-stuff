package com.GameKit_6.Frog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Lief {

   public final static byte NONE           = 0;
   public final static byte LIEF           = 1;
   public final static byte CIRCLES        = 2;
   public final static byte ALLIGATOR      = 3;

   byte []ab_phases = {
    2,//NONE
    7,//LIEF
    4,//CIRCLES
    10,//ALLIGATOR
   };

   public final static int BONUS_NONE  = 0;
   public final static int BONUS_MINUS = 1;
   public final static int BONUS_PLUS  = 2;
   public final static int BONUS_LIFE  = 3;
   public final static int BONUS_LOTUS = 4;


   public int     i_type;
   public int     i_x,i_y;
   public boolean lg_active;
   protected int  i8_phase;
   protected int  i8_velocity;
   protected int  i_cell_X,i_cell_Y;

   public int i_bonus;

  public Lief(int x, int y)
  {
    i_x = x;
    i_y = y;
    deactivate();
  }

  protected void deactivate()
  {
    lg_active = false;
//    i_type = NONE;
    i8_phase = 0;
    i8_velocity = 0;
    i_bonus = NONE;
  }

  protected void activate(int type, int velocity, int bonus)
  {
    i_type =  type;
    i8_phase = ((ab_phases[type]) << 8)-1;
    i8_velocity = velocity;
    i_bonus = bonus;
    lg_active = true;
  }


  public int getPhase()
  {
    return i8_phase>>8;
  }

  /**
   *
   * @return if animation has ended
   */
  public boolean processAnimation()
  {
    if(lg_active){
      i8_phase -= i8_velocity;
      if(i8_phase < i8_velocity)
         switch(i_type){
	   case LIEF      : activate(NONE,0xA0,0);return false;
	   case CIRCLES   : activate(NONE,i8_velocity,0);return false;
	   case ALLIGATOR : activate(CIRCLES,i8_velocity,0);return false;
	          default : deactivate();
         }
       else
	  return false;
    }
    return true;
  }

    protected void writeSpriteToOutputStream(DataOutputStream _dos) throws IOException
    {
        _dos.writeByte(i_type);
        _dos.writeShort(i8_phase);
        _dos.writeShort(i8_velocity);
        _dos.writeBoolean(lg_active);
    }

    protected void loadSpriteFromStream(DataInputStream _dis) throws IOException
    {
        i_type = _dis.readUnsignedByte();
        i8_phase = _dis.readShort();
        i8_velocity = _dis.readShort();
	lg_active = _dis.readBoolean();
    }

}