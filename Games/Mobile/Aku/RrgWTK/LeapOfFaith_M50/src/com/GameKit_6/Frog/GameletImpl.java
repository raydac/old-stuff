package com.GameKit_6.Frog;

import com.igormaznitsa.gameapi.Gamelet;
import com.igormaznitsa.gameapi.GameActionListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;



public class GameletImpl extends Gamelet {

    public static final int ACTION_FIELD_CHANGED = 0;

    public static final int PLAYER_BUTTON_NONE = 0;         // WARNING:
    public static final int PLAYER_BUTTON_LEFT = 1;         //      numeric   value  is
    public static final int PLAYER_BUTTON_RIGHT = 2;        //      used  directly from
    public static final int PLAYER_BUTTON_UP = 3;           //      "nextGameStep(int)"
    public static final int PLAYER_BUTTON_DOWN = 4;         //      method

    private static final int GAMELEVEL_EASY = 0;
    private static final int GAMELEVEL_NORMAL = 1;
    private static final int GAMELEVEL_HARD = 2;

    private static final int TIMEDELAY_EASY = 120;
    private static final int TIMEDELAY_NORMAL = 100;
    private static final int TIMEDELAY_HARD = 80;

    private static final int ATTEMPTIONS_EASY = 4;
    private static final int ATTEMPTIONS_NORMAL = 3;
    private static final int ATTEMPTIONS_HARD = 3;

    public static final int TOTAL_STAGES = 15;

    public static final int FROM_STAGE_STORK_APPEAR = 3;
    private static final int I8_MINIMAL_STORK_WAITSTATE = (3+5)<<8; //maximal frog's animation frames + safe margin
    private static final int I8_DECREMENT_STORK_WAITSTATE = 0x80;
    private static final int STORK_FREQ = 15;

    private static final int [] LIEFS_DELAY = {0x10,0x18,0x28,0x40,0x48,0x58};

    public static final int VIRTUAL_CELL_WIDTH = 17;
    public static final int VIRTUAL_CELL_HEIGHT = 11;

    public static final int FIELD_WIDTH = 5;
    public static final int FIELD_HEIGHT = 4;

    private static final int STAGESCORE = 100;              // reward for stage
    private static final int LEVELBONUS = 50;               // bonus for difficulty
    private static final int SCORE_INCREMENT = 25;          // good berry
    private static final int SCORE_DECREMENT = 20;          // poisonous berry

    private static final int CIRCLES_DEF_SPEED = 0x80;

    private static final int sWidthMultiplicator = 0x150;   // 8b factor, half of'em
    private static final int eWidthMultiplicator = 0x0f0;   // 8b factor, native width

    private static final int sHeightMultiplicator = 0x120;  // 8b factor, half of'em
    private static final int eHeightMultiplicator = 0x0f8;  // 8b factor, native width

    private static final int AMOUNT_OF_EMPTY_FIELDS = 2;

    public Lief [] queue;
    public Lief [][] liefs;

    public int i_Attemptions;
    public int i_TimeDelay;

    private int i_stork_appear_freq;
    private int i_stork_appear_delay;
    private int i_stork_appear_precharge;
    private int i_lief_motion_delay_freq;
    private int i_lief_disappear_delay_freq;
    private int i_lief_circles_freq;

    private int i_bonus1_freq;
    private int i_bonus2_freq;
    private int i_bonus3_freq;
    private int i_lief_generator_delay;
    private int i_lief_generator_precharge;

    public Sprites p_Player;
    public Sprites p_Bird;

    public int i_queue_freemark;


  public GameletImpl(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener) {
     super(_screenWidth, _screenHeight, _gameActionListener);

        // setup game field
	  liefs = new Lief[FIELD_WIDTH][FIELD_HEIGHT];
	  queue = new Lief[FIELD_WIDTH*FIELD_HEIGHT];

	  int halfscreen = i_ScreenWidth>>1;
	  int yy = 0;
	  for(int y = 0; y <FIELD_HEIGHT; y++)
	  {
	    int mmy = eHeightMultiplicator + (sHeightMultiplicator-eHeightMultiplicator) * (FIELD_HEIGHT-y)/FIELD_HEIGHT;
	    yy += (VIRTUAL_CELL_HEIGHT*mmy)>>8;
	    int mmx = eWidthMultiplicator + (sWidthMultiplicator-eWidthMultiplicator) * (FIELD_HEIGHT-y)/FIELD_HEIGHT;
	    int line_ofs = (i_ScreenWidth - ((FIELD_WIDTH*VIRTUAL_CELL_WIDTH*mmx)>>8) +VIRTUAL_CELL_WIDTH)>>1;
	    for(int x = 0; x <FIELD_WIDTH; x++)
	    {

                int xx = x*VIRTUAL_CELL_WIDTH*mmx;
		    xx >>=8;
		    xx += line_ofs;

	    	    Lief l = new Lief(xx, yy);
		    l.i_cell_X = x; l.i_cell_Y = y;
		    liefs[x][y] = l;
		    queue[y*FIELD_WIDTH+x] = l;
	    }
	  }
	  yy = (i_ScreenHeight -yy-VIRTUAL_CELL_HEIGHT)>>1;
	  for(int y = 0; y <queue.length; y++){
	    queue[y].i_y = i_ScreenHeight - queue[y].i_y - yy;
	  }

  }
  public void newGameSession(int _level) {

        super.initLevel(_level);
        switch (_level)
        {
            case GAMELEVEL_EASY:
                {
                    i_TimeDelay = TIMEDELAY_EASY;
                    i_Attemptions = ATTEMPTIONS_EASY;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_TimeDelay = TIMEDELAY_NORMAL;
                    i_Attemptions = ATTEMPTIONS_NORMAL;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_TimeDelay = TIMEDELAY_HARD;
                    i_Attemptions = ATTEMPTIONS_HARD;
                }
                ;
                break;
        }

        p_Player = new Sprites(Sprites.OBJECT_PLAYER,Sprites.STATE_LEFT);
        p_Bird = new Sprites(Sprites.OBJECT_BIRD,Sprites.STATE_LEFT);
    }


    public void initStage(int _stage)
    {
       super.initStage(Math.min(TOTAL_STAGES,_stage));
       _stage = i_GameStage;
       int i,j;

       if(_stage>FROM_STAGE_STORK_APPEAR) {
	   i_stork_appear_precharge = ((TOTAL_STAGES - _stage) * I8_DECREMENT_STORK_WAITSTATE + I8_MINIMAL_STORK_WAITSTATE)>>8;
	   i_stork_appear_freq = (TOTAL_STAGES - _stage) * STORK_FREQ;
       } else {
	    i_stork_appear_precharge = -1;
	    i_stork_appear_freq = -1;
       }

       i_stork_appear_delay = i_stork_appear_precharge;

       i_lief_motion_delay_freq = Math.min(_stage,LIEFS_DELAY.length-1);
       i_lief_disappear_delay_freq = _stage;
       if(_stage>1)i_lief_circles_freq = _stage;
        else i_lief_circles_freq = 0;

       i_lief_generator_precharge = _stage*4/TOTAL_STAGES +2;
       i_lief_generator_delay = i_lief_generator_precharge;
       if(_stage>0) {
          i_bonus1_freq = (TOTAL_STAGES - _stage +1)*10;
         if(_stage>1) {
          i_bonus2_freq = (TOTAL_STAGES - _stage +2)*10;
          if(_stage>5) {
            i_bonus3_freq = 500;
          }
         }
       }

       for(i = 0; i < queue.length; i++) {
	 queue[i].deactivate();
	 queue[i].i_type = Lief.NONE;
       }
       liefs[0][0].activate(Lief.LIEF,0,0);
       liefs[liefs.length-1][liefs[0].length-1].activate(Lief.LIEF,0,Lief.BONUS_LOTUS);
       compactQueue();
       Lief start = liefs[0][0];
       p_Player.setCellXY(0,0);
       p_Bird.setCellXY(0,0);

       j = i_queue_freemark>>1;
       for(i = 0; i < j; i++)
       try{
         generateLief();
       }catch(Exception e){e.printStackTrace();}

       p_Player.setMainXY(start.i_x,start.i_y);
       p_Player.initState(Sprites.STATE_RIGHT);
       p_Bird.initState(Sprites.STATE_RIGHT);
       p_Bird.lg_SpriteActive = false;

    }

    public void compactQueue()
    {
      i_queue_freemark = queue.length;
      for(int i = 0; i < i_queue_freemark;)
        if(queue[i].lg_active) i++;
	 else {
	   Lief l = queue[i];
	   queue[i] = queue[--i_queue_freemark];
	   queue[i_queue_freemark] = l;
	 }
    }

    private void generateLief()throws Exception{
      int i = queue.length-i_queue_freemark-AMOUNT_OF_EMPTY_FIELDS;
      if(i>0 && i_queue_freemark<queue.length)
      {
         i=getRandomInt(i)+i_queue_freemark;
	 int bonus=0;
	 if(i_bonus1_freq>0)
	  if(getRandomInt(i_bonus1_freq)==i_bonus1_freq) bonus = Lief.BONUS_PLUS;
	  else
	  if(i_bonus2_freq>0)
	   if(getRandomInt(i_bonus2_freq)==i_bonus2_freq) bonus = Lief.BONUS_MINUS;
	   else
	   if(i_bonus3_freq>0)
	    if(getRandomInt(i_bonus3_freq)==i_bonus3_freq) bonus = Lief.BONUS_LIFE;

	 int speed = LIEFS_DELAY[getRandomInt(i_lief_motion_delay_freq)];

	 int type = Lief.LIEF;
/*
	    if(getRandomInt(i_lief_disappear_delay_freq)==i_lief_disappear_delay_freq) type = Lief.NONE;
	    else
	    if(i_croc_freq>0)
	       if(getRandomInt(i_croc_freq)==i_croc_freq) type = Lief.ALLIGATOR;
*/
         queue[i].activate(Lief.LIEF,speed,bonus);

	 Lief l = queue[i_queue_freemark];
	 queue[i_queue_freemark++] = queue[i];
	 queue[i] = l;
      }
    }




    private void removeLief(int i){
      if(i>=0 && i<i_queue_freemark){
	 Lief l = queue[--i_queue_freemark];
	 queue[i_queue_freemark] = queue[i];
	 queue[i] = l;
      }
    }

    private void addLief(int x,int y,int type,int speed,int bonus){
      Lief l = liefs[x][y];
      if(l.lg_active)l.activate(type,speed,bonus);
       else
       for (int i=0;i<queue.length;i++)
         if(queue[i]==l)
	 {
	    addLief(i,type,speed, bonus);
	    return;
        }
    }

    private void addLief(int i,int type,int speed,int bonus){
         if (i>=i_queue_freemark && i<queue.length){
	     Lief l = queue[i];
	     l.activate(type,speed,bonus);
	     queue[i] = queue[i_queue_freemark];
	     queue[i_queue_freemark++] = l;
         }
    }



    public void nextGameStep(int _playermoveobject)
    {
        if(p_Player.lg_SpriteActive) {
	  if(p_Player.processAnimation()){
	    switch(p_Player.i_objectState){
               case Sprites.STATE_DEATH     : {
				         i_Attemptions--;
                                         i_PlayerState = PLAYERSTATE_LOST;
					 p_Player.lg_SpriteActive = false;
                                         if (i_Attemptions <= 0)
                                         {
					    i_GameState = GAMESTATE_OVER;
                                         }
					  return;
                                      }
               case Sprites.STATE_JUMPLEFT  :
               case Sprites.STATE_JUMPRIGHT :
               case Sprites.STATE_JUMPUP    :
               case Sprites.STATE_JUMPDOWN  :{
		                              p_Player.setMotionDist(0,0);
	                                      Lief l = liefs[p_Player.i_cellX][p_Player.i_cellY];
					      p_Player.setMainXY(l.i_x,l.i_y);
	                                      if(l.lg_active && l.i_type == Lief.LIEF){
	                                         p_Player.initState(p_Player.i_objectState - Sprites.STATE_JUMPLEFT);
						 switch(l.i_bonus){
                                                   case Lief.BONUS_MINUS : i_PlayerScore = Math.max(0,i_PlayerScore - SCORE_DECREMENT); break;
                                                   case Lief.BONUS_PLUS  : i_PlayerScore += SCORE_INCREMENT; break;
                                                   case Lief.BONUS_LIFE  : i_Attemptions++; break;
                                                   case Lief.BONUS_LOTUS : {
                                                                             i_PlayerState = PLAYERSTATE_WON;
					                                     p_Player.lg_SpriteActive = false;
									     i_PlayerScore += STAGESCORE + LEVELBONUS*i_GameLevel;
                                                                             if (i_GameStage+1>=TOTAL_STAGES)
									     {
									        i_GameState = GAMESTATE_OVER;
									     }
					                                     return;
                                                                           }
						 }
						 l.i_bonus = 0;
	                                      } else {
						p_Player.initState(Sprites.STATE_DEATH);
						addLief(p_Player.i_cellX,p_Player.i_cellY,Lief.ALLIGATOR,CIRCLES_DEF_SPEED,0);
//                                                liefs[p_Player.i_cellX][p_Player.i_cellY].activate(Lief.ALLIGATOR,CIRCLES_DEF_SPEED,0);
	                                      }
                                             } break;
	       default:

		      Lief l = liefs[p_Player.i_cellX][p_Player.i_cellY];
		      if(!(l.lg_active && l.i_type == Lief.LIEF))
		      {
						p_Player.initState(Sprites.STATE_DEATH);
						addLief(p_Player.i_cellX,p_Player.i_cellY,Lief.ALLIGATOR,CIRCLES_DEF_SPEED,0);
//                                                liefs[p_Player.i_cellX][p_Player.i_cellY].activate(Lief.CIRCLES,CIRCLES_DEF_SPEED,0);
						return;
		      }
		      switch(_playermoveobject) {
			 case PLAYER_BUTTON_LEFT  : if(p_Player.i_cellX>0) changeDirection(Sprites.STATE_LEFT, -1,0); break;
			 case PLAYER_BUTTON_RIGHT : if(p_Player.i_cellX+1<FIELD_WIDTH) changeDirection(Sprites.STATE_RIGHT, +1,0); break;
			 case PLAYER_BUTTON_UP    : if(p_Player.i_cellY+1<FIELD_HEIGHT) changeDirection(Sprites.STATE_UP, 0,+1); break;
			 case PLAYER_BUTTON_DOWN  : if(p_Player.i_cellY>0) changeDirection(Sprites.STATE_DOWN, 0,-1); break;
		      }
	    }
	  }
        }
      processLiefs();
      processStork();
    }

  private void changeDirection(int newdir, int dx, int dy){
     if (p_Player.i_objectState == newdir ){
           newdir += Sprites.STATE_JUMPLEFT;
           p_Player.initState(newdir);

	   Lief l1 = liefs[p_Player.i_cellX][p_Player.i_cellY];

	   p_Player.setCellXY(p_Player.i_cellX+dx,p_Player.i_cellY+dy);

	   Lief l2 = liefs[p_Player.i_cellX][p_Player.i_cellY];

	   p_Player.setMotionDist(l2.i_x-l1.i_x,l2.i_y-l1.i_y);
//	   if(p_Bird.lg_SpriteActive)p_Bird.lg_backMove = true;
//           i_stork_appear_delay = i_stork_appear_precharge;
     }
      else
           p_Player.initState(newdir);
  }

  private void processLiefs(){
    try{
       Lief l;
       int i,j;
       for(i = 0; i < i_queue_freemark; i++){
	   l = queue[i];
	   j = l.getPhase();
           if (l.processAnimation()){
	     if((l.i_type==Lief.LIEF || l.i_type==Lief.ALLIGATOR) &&
	                  i_lief_circles_freq>0 &&
	        getRandomInt(i_lief_circles_freq) == i_lief_circles_freq)
	                l.activate(Lief.CIRCLES,CIRCLES_DEF_SPEED,0);
	     else {
	     if(i_lief_disappear_delay_freq>0 &&
	        getRandomInt(i_lief_disappear_delay_freq)==i_lief_disappear_delay_freq)
		l.activate(Lief.NONE,CIRCLES_DEF_SPEED,0);
	     else
	       removeLief(i);
	     }
             p_GameActionListener.gameAction(ACTION_FIELD_CHANGED,i);
	     continue;
           }
	   if(j!=l.getPhase())p_GameActionListener.gameAction(ACTION_FIELD_CHANGED,i);
       }

       if (--i_lief_generator_delay <= 0){
         generateLief();
	 i_lief_generator_delay = i_lief_generator_precharge;
       }
    }catch (Exception e){e.printStackTrace();}
  }

  private void processStork(){
    if(p_Bird.lg_SpriteActive){
    } else
      if(i_stork_appear_freq>0 && i_queue_freemark>3 && getRandomInt(i_stork_appear_freq)==i_stork_appear_freq){
//	Lief l=queue[getRandomInt(i_queue_freemark-3)+2]);
//	p_Bird.ac
      }

  }




  public void resumeGameAfterPlayerLost(){
    initStage(i_GameStage);
  }

  public void writeToStream(DataOutputStream _dataOutputStream) throws java.io.IOException {
//       saveGameState(_dataOutputStream);
       _dataOutputStream.writeShort(i_stork_appear_delay);
       _dataOutputStream.writeByte(i_Attemptions);
       _dataOutputStream.writeByte(i_lief_generator_delay);

       for(int i = 0; i < liefs.length; i++)
          for(int j = 0; j < liefs[i].length; j++)
            liefs[i][j].writeSpriteToOutputStream(_dataOutputStream);

       p_Player.writeSpriteToOutputStream(_dataOutputStream);
       p_Bird.writeSpriteToOutputStream(_dataOutputStream);

  }
  public void readFromStream(DataInputStream _dataInputStream) throws java.io.IOException {
//       loadGameState(_dataInputStream);
       i_stork_appear_delay = _dataInputStream.readUnsignedShort();
       i_Attemptions  = _dataInputStream.readUnsignedByte();
       i_lief_generator_delay = _dataInputStream.readUnsignedByte();

       for(int i = 0; i < liefs.length; i++)
          for(int j = 0; j < liefs[i].length; j++) {
	    Lief l = liefs[i][j];
            l.loadSpriteFromStream(_dataInputStream);
	    l.i_cell_X = i; l.i_cell_Y = j;
          }

       compactQueue();

       p_Player.loadSpriteFromStream(_dataInputStream);
       p_Bird.loadSpriteFromStream(_dataInputStream);

  }

  public String getGameID()               {   return  "BoggyFrog";  }
  public int getMaxSizeOfSavedGameBlock() {   return 400;           }
}