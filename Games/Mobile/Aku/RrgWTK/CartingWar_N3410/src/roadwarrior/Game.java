package roadwarrior;

import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class Game extends Gamelet
{
    public final static int CAR_STATE_NORMAL = 0;
    public final static int CAR_STATE_EXPLOSION = 1;

    public final static int POLICE_CAR1 = 0;
    public final static int POLICE_CAR2 = 1;
    public final static int POLICE_CAR3 = 2;
    public final static int POLICE_CAR4 = 3;
    public final static int POLICE_CAR5 = 4;

    int DELAY = 100;

    public final static int MINE_READY = 0;
    public final static int MINE_ROADWIDTH = 4;

    private final static int MAX_PLAYER_SPEED = 1000;//100mph 0x60e; // 155 mph
    private final static int START_PLAYER_SPEED = MAX_PLAYER_SPEED;
    private final static int MAX_OFFROAD_BRAKES = 0x16;  // ,8 bf
    private final static int CAR_ACCELERATE_STEP = 0x10; // ,8 bf

    public final static int OBJECT_MINE    = 0;
    public final static int OBJECT_CARMINE = 1;
    public final static int OBJECT_TREE    = 2;
    public final static int ROADTYPE_MARK  = 3;   //road fragments paint acceleration engine,
    // valid marks
    public final static int ROAD_STRAIGHT   = 0;
    public final static int ROAD_LEFT       = 1;  // turn left         (addTobrokeroad <> 0, diradd < 0)
    public final static int ROAD_RIGHT      = 2;  // turn right        (addTobrokeroad <> 0, diradd > 0)
    public final static int ROAD_FROM_LEFT  = 3;  // return from left  (addTobrokeroad == 0, diradd < 0)
    public final static int ROAD_FROM_RIGHT = 4;  // return from right (addTobrokeroad == 0, diradd > 0)

    public final static int TURN_RADIUS    = 10;  // turn radius = 10
    public final static int TURN_DEGREES   = 14;  // xx degree half-arc resolution (except STRAIGHT-mode)
                                                  // NOTE: height of road frags must be calculated (TURN_DEGREES+1)*2
						  //       "2" - is alignment of "roaddelay" variable, used for increase trajectory

						  //               ->: :<- TURN_RADIUS
						  // -TURN_DEGREES | | :
						  //                \0\:
						  //                 | | +TURN_DEGREES


    public final static int CAR_WIDTH = 10;
    public final static int CAR_HEIGHT = 15;
    public final static int PCAR_WIDTH = 10;
    public final static int PCAR_HEIGHT = 15;
    public final static int FCAR_WIDTH = 10;
    public final static int FCAR_HEIGHT = 15;

    public final static int TOTAL_STAGES = 3;

    int W;
    int H;

    public int MAXFLASHPOINTER = 12;

    public int MAXLIVES = 30;

    public final int ROADWIDTH = 69;

    int SWAY_ROAD;
    int STAGECOUNTERMAX;
    int MINE_RANDOM;
    int TREE_RANDOM;
    int POLICE_RANDOM;
    int POLICESHOT_RANDOM;
    int FRONTCAR_RANDOM;
    public int MAXPOLICE = 2;
    int maxpolice_onstage;
    public int MAXFRONTCAR = 4;
    int maxfront_onstage;

    public int road[];
    public int roaditems[][];
    public int roadLeftX;
    boolean brokeroad;
    int addTobrokeroad;
    public int curswaytype;
    boolean roadmark_protection;
    int diradd;
    int curroaddir;
    public int miles_counter;
    int roaddelay = 0;

    public int lives;
    public int carspeed;
    int curspeed_accumulator;
    public int carshift;
    public int flashpointer;
    public int my_minecounter_delay;
    public int shotY;
    public int shotX;

    int stage = 0;
    int stagecounter = 0;

    public GameObject car = new GameObject(CAR_WIDTH,CAR_HEIGHT,true);

    public GameObject policecar[] = new GameObject[MAXPOLICE];

    public GameObject frontcar[] = new GameObject[MAXFRONTCAR];


    public Game(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        W = _screenWidth;
        H = _screenHeight;
        newGameSession(0);
    }

    public void setStage(int lvl)
    {
        STAGECOUNTERMAX = (stage + 1) * 10;
        setDifficulty(lvl);
    }

    public void setDifficulty(int lvl)
    {
      if (lvl == 0)
      {
          MINE_RANDOM = 30;
          TREE_RANDOM = 50;
          SWAY_ROAD = 20;
          maxpolice_onstage = 1;
          maxfront_onstage = 2;
          POLICE_RANDOM = 75;
          FRONTCAR_RANDOM = 55;
          POLICESHOT_RANDOM = 15;
      }
      if (lvl == 1)
      {
          MINE_RANDOM = 25;
          TREE_RANDOM = 30;
          SWAY_ROAD = 40;
          maxpolice_onstage = 2;
          maxfront_onstage = 3;
          POLICE_RANDOM = 65;
          FRONTCAR_RANDOM = 45;
          POLICESHOT_RANDOM = 10;
      }
      if (lvl == 2)
      {
          MINE_RANDOM = 20;
          TREE_RANDOM = 10;
          SWAY_ROAD = 20;
          maxpolice_onstage = 2;
          maxfront_onstage = MAXFRONTCAR;
          POLICE_RANDOM = 50;
          FRONTCAR_RANDOM = 30;
          POLICESHOT_RANDOM = 5;
      }
    }

    public void initStage(int _stage)
    {
        super.initStage(_stage);
        stage = _stage;
        setStage(stage);

	brokeroad = false;
	addTobrokeroad = 0;
	curroaddir = 0;
	curswaytype = ROAD_STRAIGHT;
	roadmark_protection = false;

        for (int ty = 0; ty < road.length; ty++)
            road[ty] = roadLeftX;
        for (int i = 0;i<road.length;i++)
        {
            roaditems[i][0] = 0;
            roaditems[i][1] = 0;
        }
     // disable player shot
        shotX = -5;
     // center player car
        car.setCoord(W/2-car.WIDTH()/2,H/2-car.HEIGHT()/3);
     // disable all enemies
      for (int i = 0; i < MAXFRONTCAR; i++)
      {
          frontcar[i].setActiveState(false);
          frontcar[i].setState(CAR_STATE_NORMAL);
      }

      for (int i = 0; i < MAXPOLICE; i++)
       {
         policecar[i].setActiveState(false);
         policecar[i].setState(CAR_STATE_NORMAL);
       }
    }

    public void newGameSession(int _level)
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        i_PlayerScore = 0;
        miles_counter = 0;
	i_PlayerScore = 0;

        carspeed = START_PLAYER_SPEED;
//        ROADWIDTH = W / 2+W/5;
        road = new int[H];
        roaditems = new int[H][2];
        roadLeftX = (W - ROADWIDTH) / 2;
        curspeed_accumulator = 0;
        curroaddir = 1; // 0 left , 1 center , 2 right
        diradd = 0;
        brokeroad = false;
        addTobrokeroad = 0;
	curswaytype = ROAD_STRAIGHT;
	roadmark_protection = false;
        for (int ty = 0; ty < road.length; ty++)
            road[ty] = roadLeftX;
        car.setCoord(W/2-car.WIDTH()/2,H/2-car.HEIGHT()/3);
        carshift = 0;
        lives = MAXLIVES;
        flashpointer = 0;
        my_minecounter_delay = 0;
        shotY = -5;
        shotX = -5;
        for (int i = 0; i < frontcar.length; i++)
          frontcar[i] = new GameObject(FCAR_WIDTH,FCAR_HEIGHT,false);
        for (int i = 0; i < policecar.length; i++)
        {
            policecar[i] = new GameObject(PCAR_WIDTH,PCAR_HEIGHT,false);
            policecar[i].aX = -1;
            policecar[i].aY = -1;
        }
        stage = 0;
        stagecounter = 0;
        setStage(stage);
    }

    public void nextGameStep(int _playermoveobject){}
    public void nextGameStep(Object _playermoveobject)
    {
        game_PMR keys = (game_PMR) _playermoveobject;
	carshift = 0;
        switch (keys.i_Value)
        {
            case game_PMR.BUTTON_LEFT:
                {
                  if(car.X()>-(car.WIDTH()>>1))carshift = -2;
                }
                break;
            case game_PMR.BUTTON_RIGHT:
                {
                  if(car.X()<i_ScreenWidth-(car.WIDTH()>>1))carshift = 2;
                }
                break;
            case game_PMR.BUTTON_UP:
                {
                  shotForward();
                }
                break;
            case game_PMR.BUTTON_DOWN:
                {
                  dropMine();
                }
                break;
        }
        gameAction();
    }

    public void gameAction()
    {
      curspeed_accumulator += carspeed;

      if(flashpointer > 0)
        flashpointer--;
      else
      {
        if(car.getState() == CAR_STATE_EXPLOSION)
           car.setState(CAR_STATE_NORMAL);
        flashpointer = MAXFLASHPOINTER;
      }

      if(my_minecounter_delay > 0)
         my_minecounter_delay--;

      processShotForward();

      for (int i = 0; i < MAXFRONTCAR; i++)
      {
        if(frontcar[i].getState() == CAR_STATE_EXPLOSION && flashpointer <=0)
        {
          frontcar[i].setActiveState(false);
          frontcar[i].setState(CAR_STATE_NORMAL);
        }
      }

      for (int i = 0; i < MAXPOLICE; i++)
       if(policecar[i].getState() == CAR_STATE_EXPLOSION && flashpointer <=0)
       {
         policecar[i].setActiveState(false);
         policecar[i].setState(CAR_STATE_NORMAL);
       }

      if(getRandomInt(FRONTCAR_RANDOM) == 1)
      {
        boolean freeplc = false;
        for (int i = 0; i < MAXFRONTCAR; i++)
        {
          if(i+1 > maxfront_onstage) continue;
          if(!frontcar[i].isActive())
          {
            freeplc = true;
            break;
          }
        }
        if(freeplc)
        {
         int i = getRandomInt(maxfront_onstage-1);
         while(frontcar[i].isActive())
          i = getRandomInt(maxfront_onstage-1);
         int tfx = road[0] + ((ROADWIDTH - (MAXFRONTCAR*(FCAR_WIDTH+2)) )/2);
         tfx += (i*(FCAR_WIDTH+2));
         if(!frontcar[i].isActive() && getRandomInt(2) == 1)
         {
           frontcar[i].setActiveState(true);
           frontcar[i].setState(CAR_STATE_NORMAL);
           frontcar[i].setCoord(tfx,-(frontcar[i].WIDTH()/2));
         }
        }
      }

      if(getRandomInt(POLICE_RANDOM) == 1)
      {
       for (int i = 0; i < MAXPOLICE; i++)
       {
         if(i+1 > maxpolice_onstage) continue;
         if(!policecar[i].isActive())
         {
           policecar[i].setState(CAR_STATE_NORMAL);
           policecar[i].setType(getRandomInt(2));
           policecar[i].setActiveState(true);
           int pcx = car.X();
           if(i == 0 && policecar[1].isActive())
            pcx = policecar[1].X() - policecar[1].WIDTH()-3;
           if(i == 1 && policecar[0].isActive())
            pcx = policecar[0].X() + (policecar[0].WIDTH()+3);
           policecar[i].setCoord(pcx,H-policecar[i].HEIGHT()/2);
           policecar[i].aX = -1;
           policecar[i].aY = -1;
           break;
         }
       }
      }

      if (!brokeroad && getRandomInt(SWAY_ROAD) == 1 && roaditems[0][1]!=ROADTYPE_MARK)
      {
          switch (curroaddir)
          {
              case 1:          // now center
                  diradd = 1;       // 1 or -1
                  curroaddir = 2;   // 2 or 1
                  break;
              case 2:
                  diradd = -1; // now right
                  curroaddir = 1;
                  break;
              case 0:
                  diradd = 1;  // now left
                  curroaddir = 2;
                  break;
          }
          if (stagecounter > STAGECOUNTERMAX && stage < 3)
          {
//              System.out.println("New stage!");
              stagecounter = 0;
              i_PlayerState = PLAYERSTATE_WON;
              stage++;
              setStage(stage);
          }
          if (stage == 3)
          {
//              System.out.println("Player win!");
              i_PlayerState = PLAYERSTATE_WON;
              i_GameState = GAMESTATE_OVER;
          }
          stagecounter++;
          brokeroad = true;

	  // add mark to roaditems
	  roaditems[0][1] = ROADTYPE_MARK;    // insert mark
	  roaditems[0][0] = curswaytype;      // previous type of road
	  roadmark_protection = true;         // lock for changes

                                              // detect new type of road
	  curswaytype = (addTobrokeroad == 0 ?(diradd > 0 ? ROAD_FROM_RIGHT : ROAD_FROM_LEFT):(diradd > 0 ? ROAD_RIGHT : ROAD_LEFT));
//	                    ?
//       (addTobrokeroad!=0 ? ROAD_FROM_LEFT : ROAD_RIGHT)
//	                    :
//	 (addTobrokeroad!=0 ? ROAD_FROM_RIGHT : ROAD_LEFT));
      }

      genereateRoad(curspeed_accumulator >> 8);
      curspeed_accumulator &= 0xff;
      mainCarAction();
      policeCarAction();
      frontCarAction();
    }

    public void frontCarAction()
    {
      for (int i = 0; i < MAXFRONTCAR; i++)
      {
         int tfx = road[0] + ((ROADWIDTH - (MAXFRONTCAR*(FCAR_WIDTH+2)) )/2);
         tfx += i*(FCAR_WIDTH+2);
         if(frontcar[i].isActive() && frontcar[i].getState() == CAR_STATE_NORMAL)
         {
          frontcar[i].setX(tfx);
          if(getRandomInt(2)==1 && carspeed >= START_PLAYER_SPEED-0x50)
            frontcar[i].shiftY(1);
          if(carspeed  <= START_PLAYER_SPEED-0x50)
            frontcar[i].shiftY(-1);
          if(carspeed  <= START_PLAYER_SPEED-0x100)
            frontcar[i].shiftY(-2);
          if(frontcar[i].Y() > H)
            frontcar[i].setActiveState(false);
          if(frontcar[i].Y() < -(frontcar[i].WIDTH()*2))
            frontcar[i].setActiveState(false);
          // Road items collision
          for (int ty = frontcar[i].Y(); ty < frontcar[i].Y() + frontcar[i].HEIGHT()/3; ty++)
          {
            if(ty<0 || ty>=H) continue;
            int gx = roaditems[ty][0];
            int itm = roaditems[ty][1];
            if(gx != 0 && itm == OBJECT_CARMINE && frontcar[i].checkCollision(gx,ty,MINE_ROADWIDTH,MINE_ROADWIDTH))
            {
              frontcar[i].setState(CAR_STATE_EXPLOSION);
              i_PlayerScore +=2;
              roaditems[ty][0] = 0;
              break;
            }
          }
          // collision with police car
          for (int ty = 0; ty < policecar.length; ty++)
	   if(policecar[ty].isActive() && frontcar[i].checkCollision(policecar[ty].X(),policecar[ty].Y(),PCAR_WIDTH,PCAR_HEIGHT))
              frontcar[i].setState(CAR_STATE_EXPLOSION);
         }
      }
    }

    public void policeCarAction()
    {
      for (int i = 0; i < MAXPOLICE; i++)
      {
        if(policecar[i].isActive() && policecar[i].getState() ==  CAR_STATE_NORMAL)
        {
         // Police car shot
         if(getRandomInt(POLICESHOT_RANDOM) == 1 && policecar[i].aX == -1)
         {
           policecar[i].aX = policecar[i].X()+policecar[i].WIDTH()/2;
           policecar[i].aY = policecar[i].Y()-1;
         }
         if(policecar[i].aX != -1)
         {
           if(policecar[i].aY < car.Y()+car.WIDTH())
           {
             policecar[i].aX = -1;
             policecar[i].aY = -1;
           }
           else
             policecar[i].aY -= 2;
           // Car collision
           if(car.checkCollision(policecar[i].aX,policecar[i].aY,2,2))
             decreaseLive(5);
         }

         // Move police car
         if(getRandomInt(1)==1)
         {
          if(policecar[i].X() < car.X())
          {
            policecar[i].shiftX(1);
            if(checkPoliceCollision(i))
              policecar[i].shiftX(-1);
          }
          else
          if(policecar[i].X()+policecar[i].WIDTH() > car.X()+car.WIDTH())
          {
            policecar[i].shiftX(-1);
            if(checkPoliceCollision(i))
              policecar[i].shiftX(1);
          }
         }
         // Road items collision
         for (int ty = policecar[i].Y(); ty < policecar[i].Y() + policecar[i].HEIGHT()/3; ty++)
         {
           if(ty<0 || ty>=H) continue;
           int gx = roaditems[ty][0];
           int itm = roaditems[ty][1];
           if(gx != 0 && itm == OBJECT_CARMINE && policecar[i].checkCollision(gx,ty,MINE_ROADWIDTH,MINE_ROADWIDTH))
           {
             policecar[i].setState(CAR_STATE_EXPLOSION);
             policecar[i].aX = -1;
             policecar[i].aY = -1;
             i_PlayerScore +=5;
             roaditems[ty][0] = 0;
             break;
           }
         }
        }
      }
    }

    public boolean checkPoliceCollision(int pos)
    {
      for (int i = 0; i < MAXPOLICE; i++)
        if(i != pos && policecar[i].isActive())
          if(policecar[i].checkCollision(policecar[pos]))
            return true;
      return false;
    }

    public void mainCarAction()
    {
      // Move car left/right
      if(carshift !=0)
        car.shiftX(carshift);
      // Road collisions
      if(
           car.checkCollision(0,car.Y(),road[car.Y()],2) ||
           car.checkCollision(road[car.Y()] + ROADWIDTH,car.Y(),W-ROADWIDTH,2)
         )
        {if(carspeed>0)carspeed -= MAX_OFFROAD_BRAKES;else carspeed=0;}
      else
      {
        if (carspeed < MAX_PLAYER_SPEED)
            carspeed += CAR_ACCELERATE_STEP;
	 else
	    carspeed = MAX_PLAYER_SPEED;
      }
      // Road items collision
      for (int ty = car.Y(); ty < car.Y() + car.HEIGHT(); ty++)
      {
        int gx = roaditems[ty][0];
        if(gx !=0 && car.checkCollision(gx,ty,MINE_ROADWIDTH,MINE_ROADWIDTH))
        {
          decreaseLive(1);
        }
      }
      // Front car collision
      for (int i = 0; i < MAXFRONTCAR; i++)
       if(frontcar[i].isActive() && frontcar[i].checkCollision(car))
       {
        decreaseLive(2);
        // Push main car to another side from collision car
        carspeed = START_PLAYER_SPEED-0x101;
       }
    }

    public void processShotForward()
    {
      if(shotX > 0 && shotY >0)
      {
        shotY-=2;
	GameObject go;
        for (int i = 0; i < MAXFRONTCAR; i++){
	 go = frontcar[i];
         if(go.isActive() && go.getState()==CAR_STATE_NORMAL && go.checkCollision(shotX,shotY,2,2))
         {
           go.setState(CAR_STATE_EXPLOSION);
           i_PlayerScore += 2;
           shotX = -5;
           shotY = -5;
         }
        }
      }
      else
      {
        shotX = -5;
        shotY = -5;
      }
    }

    public void shotForward()
    {
      if(shotX >= 0) return;
      shotX = car.X()+car.WIDTH()/2;
      shotY = car.Y();
    }

    public void dropMine()
    {
      if(my_minecounter_delay >0) return;
      int my = car.Y()+car.HEIGHT();
      my_minecounter_delay = my;
      if(roaditems[my][1]==ROADTYPE_MARK)
        if(++my>=roaditems.length-1)return;
      roaditems[my][0] = (car.X()+(car.WIDTH()/2))-MINE_ROADWIDTH/2;
      roaditems[my][1] = OBJECT_CARMINE;
    }

    public void decreaseLive(int hp)
    {
      if(car.getState() == CAR_STATE_EXPLOSION) return;
      car.setState(CAR_STATE_EXPLOSION);
      lives -= hp;
      if(lives <0) lives = 0;
      flashpointer = MAXFLASHPOINTER;
      if(lives <= 0) {
        i_GameState = GAMESTATE_OVER;
	i_PlayerState = PLAYERSTATE_LOST;
      }
    }

    public void genereateRoad(int offset)
    {
        if (offset > 0 && offset < road.length)
        {
            miles_counter += offset;
            int i = 0,j = 0;
            for (i = road.length - 1; i >= offset; i--)
            {
                j = i - offset;
                road[i] = road[j];
                if (roaditems[j][0] > 0)
                {
                    roaditems[i][0] = roaditems[j][0];
                }

                roaditems[i][0] = roaditems[j][0];
                roaditems[i][1] = roaditems[j][1];
            }

            for (i = offset - 1; i >= 0; i--)
            {
                roaddelay++;
                if (brokeroad && roaddelay%2==0)
                {
                    addTobrokeroad += diradd;
                    int s = xSine(TURN_RADIUS,addTobrokeroad & 63);
                    road[i] = roadLeftX+(int)s;

                    //j = (W - ROADWIDTH) >> 1;

                    if (addTobrokeroad > TURN_DEGREES || addTobrokeroad < -TURN_DEGREES)
                    {
                        brokeroad = false;               // switch off turning
                        roaddelay = 0;

                        roaditems[i][0] = curswaytype;   // set up roadmark
                        roaditems[i][1] = ROADTYPE_MARK;

			curswaytype = ROAD_STRAIGHT;     // activate stright road
			roadmark_protection = true;      // protect the mark from erasing

                    } else
		       if(addTobrokeroad == 0)           // is it a point of sign-changing?
		       {
                        roaditems[i][0] = curswaytype;   // set up roadmark
                        roaditems[i][1] = ROADTYPE_MARK;
			roadmark_protection = true;      // aligning the road
			curswaytype = curswaytype == ROAD_LEFT ?
			            ROAD_FROM_LEFT : ROAD_FROM_RIGHT;
		       }
                }
                else
                    road[i] = road[i + 1];

		if(roadmark_protection){
		  roadmark_protection = false;
		} else {
                  roaditems[i][0] = 0;
                  roaditems[i][1] = 0;
		}
            }

	    if(roaditems[1][1] != ROADTYPE_MARK)  // mark protection
	    {
              if (getRandomInt(MINE_RANDOM) == 0)
              {
                int xc = road[1] + getRandomInt(ROADWIDTH);
                roaditems[1][0] = xc;
                roaditems[1][1] = OBJECT_MINE;
              }
              else
                if (getRandomInt(TREE_RANDOM) < 5)
                {
                    int xc = getRandomInt(W - ROADWIDTH);
                    if (xc >= road[1] - 4) xc += ROADWIDTH + 8;  //   <-4..| road |..+4>
                    roaditems[1][0] = xc;
                    roaditems[1][1] = OBJECT_TREE;
                }
	    }
        }
    }

    public void writeToStream(DataOutputStream ds) throws IOException
    {
      int tmp[];
      ds.writeInt(roadLeftX);
      ds.writeBoolean(brokeroad);
      ds.writeInt(addTobrokeroad);
      ds.writeInt(curswaytype);
      ds.writeBoolean(roadmark_protection);
      ds.writeInt(diradd);
      ds.writeInt(curroaddir);
      ds.writeInt(miles_counter);
      ds.writeInt(roaddelay);

      ds.writeInt(lives);
      ds.writeInt(carspeed);
      ds.writeInt(curspeed_accumulator);
      ds.writeInt(carshift);
      ds.writeInt(flashpointer);
      ds.writeInt(my_minecounter_delay);
      ds.writeInt(shotY);
      ds.writeInt(shotX);

      ds.writeInt(stage);
      ds.writeInt(stagecounter);

      tmp = car.getMeAsByteArray();
      for(int j=0;j<tmp.length;j++)
        ds.writeShort(tmp[j]);

      for(int i=0;i<policecar.length;i++)
      {
       tmp = policecar[i].getMeAsByteArray();
       for(int j=0;j<tmp.length;j++)
         ds.writeShort(tmp[j]);
      }

      for(int i=0;i<frontcar.length;i++)
      {
       tmp = frontcar[i].getMeAsByteArray();
       for(int j=0;j<tmp.length;j++)
         ds.writeShort(tmp[j]);
      }

      for (int ty = 0; ty < road.length; ty++)
      {
          ds.writeShort(road[ty]);
          ds.writeShort(roaditems[ty][0]);
          ds.writeShort(roaditems[ty][1]);
      }

    }

    public void readFromStream(DataInputStream is) throws IOException
    {
      int tmp[] = new int[GameObject.getStaticArrayLength()];
      roadLeftX = is.readInt();
      brokeroad = is.readBoolean();
      addTobrokeroad = is.readInt();
      curswaytype = is.readInt();
      roadmark_protection = is.readBoolean();
      diradd = is.readInt();
      curroaddir = is.readInt();
      miles_counter = is.readInt();
      roaddelay = is.readInt();

      lives = is.readInt();
      carspeed = is.readInt();
      curspeed_accumulator = is.readInt();
      carshift = is.readInt();
      flashpointer = is.readInt();
      my_minecounter_delay = is.readInt();
      shotY = is.readInt();
      shotX = is.readInt();

      stage = is.readInt();
      stagecounter = is.readInt();

      for(int i=0;i<GameObject.getStaticArrayLength();i++)
        tmp[i] = is.readShort();
      car.setMeAsByteArray(tmp);

      for(int i=0;i<policecar.length;i++)
      {
        for(int j=0;j<GameObject.getStaticArrayLength();j++)
          tmp[j] = is.readShort();
        policecar[i].setMeAsByteArray(tmp);
      }

      for(int i=0;i<frontcar.length;i++)
      {
        for(int j=0;j<GameObject.getStaticArrayLength();j++)
          tmp[j] = is.readShort();
        frontcar[i].setMeAsByteArray(tmp);
      }

      for (int ty = 0; ty < road.length; ty++)
      {
          road[ty] = is.readShort();
          roaditems[ty][0] = is.readShort();
          roaditems[ty][1] = is.readShort();
      }
//      setDifficulty(stage);
    }

    public String getGameID()
    {
        return "RoadWarrior309";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1200;
    }


}
