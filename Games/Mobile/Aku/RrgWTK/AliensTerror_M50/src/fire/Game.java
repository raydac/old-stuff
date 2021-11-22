package fire;

import com.igormaznitsa.gameapi.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import com.itx.mbgame.*;

public class Game extends Gamelet
{
    GameActionListener _gameActionListener;

    public static final int ACTION_HEALTHUPDATE = 1;

    int W = 100;
    int H = 100;

    public int PLAYER_WIDTH = 12;
    public int PLAYER_HEIGHT = 12;
    public int ENEMY_W = 12;
    public int ENEMY_H = 12;
    public int SHOT_WH = 2;
    public int AIR_WIDTH = 20;
    public int AIR_HEIGHT = 10;

    public static int LEVEL_QWH = 12;

    public static final int ENEMY_1 = 0;
    public static final int ENEMY_2 = 1;
    public static final int ENEMY_3 = 2;
    public static final int ENEMY_4 = 3;

    int FAR_DISTANCE = 5; // Дистанция справа начиная с которой генерим противника

    public int MAXLIVE = 30;

    int AIR_RANDOM = 500;

    public static final int STEP_STOP = 0;
    public static final int STEP_PHASE1 = 1;
    public static final int STEP_PHASE2 = 2;
    public static final int STEP_PHASE3 = 3;
    public static final int STEP_PHASE4 = 4;
    public static final int STEP_JUMP   = 5;

    public static final int ENEMY_BURN  = 6;

    Stage stage_container = new Stage();
    public int cur_stage[][] = null;
    // -----------------------------------------------

    int globalcounter = 0;

    public int live = MAXLIVE;
    public boolean down_action = false;
    public int player_state = GameObject.DIR_STOP;
    public int PLAYER_DEAD_ANIM = 0;

    public GameObject shot[] = new GameObject[3];
    public GameObject player = new GameObject(PLAYER_WIDTH,PLAYER_HEIGHT,true);

    // Рандомно генерящиеся противники
    public GameObject enemyshot = new GameObject(SHOT_WH,SHOT_WH,false);
    public GameObject enemy[] = new GameObject[4];

    // Самолетик
    public GameObject aircraft = new GameObject(AIR_WIDTH,AIR_HEIGHT,false);

    public Game(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        this._gameActionListener = _gameActionListener;
        W = _screenWidth;
        H = _screenHeight;
        newGameSession(0);
    }

    public void newGameSession(int _level)
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        player_state = GameObject.DIR_STOP;
        for(int i=0;i<shot.length;i++)
         shot[i] = new GameObject(SHOT_WH,SHOT_WH,false);
        for(int i=0;i<enemy.length;i++)
          enemy[i] = new GameObject(ENEMY_W,ENEMY_H,false);
        enemyshot = new GameObject(SHOT_WH,SHOT_WH,false);
        PLAYER_DEAD_ANIM = 0;
        live = MAXLIVE;
        down_action = false;
        i_PlayerScore = 0;
        initStage(0);
    }

    public void initStage(int st)
    {
      i_PlayerState = Gamelet.PLAYERSTATE_NORMAL;
      cur_stage = stage_container.getStage(st);
      player.setCoord(0,Stage.LEVEL_HEIGHT-2);
      player.setDirection(GameObject.DIR_RIGHT);
      player.aX = 0;
      player.aY = 0;
    }

    public void nextGameStep(int _playermoveobject){}
    public void nextGameStep(Object _playermoveobject)
    {
        game_PMR keys = (game_PMR) _playermoveobject;
        if(PLAYER_DEAD_ANIM <= 0)
        {
         switch (keys.i_Value)
         {
            case game_PMR.BUTTON_LEFT:
                {
                 if(player.aY ==0 && !down_action)
                 {
                  if(player.getDirection() == GameObject.DIR_RIGHT)
                    player.setDirection(GameObject.DIR_LEFT);
                  else
                    if(player.aX == 0 && player.X() > 0)
                      if(checkQuadPermiability(player.X()-1,player.Y()))
                       player.aX = -1;
                 }
                }
                break;
            case game_PMR.BUTTON_RIGHT:
                {
                  if(player.aY ==0 && !down_action)
                  {
                   if(player.getDirection() == GameObject.DIR_LEFT)
                     player.setDirection(GameObject.DIR_RIGHT);
                   else
                     if(player.aX == 0 && player.X() < cur_stage[0].length-1)
                       if(checkQuadPermiability(player.X()+1,player.Y()))
                        player.aX = 1;
                  }
                }
                break;
              case game_PMR.BUTTON_UP:
                  {
                    if(player.aY == 0 && !down_action)
                      player.aY = -1;
                  }
                  break;
                case game_PMR.BUTTON_FIRE:
                    {
                      for(int i=0;i<shot.length;i++)
                       if(!shot[i].isActive())
                       {
                        int ax = 0;
                        int PX = player.X();
                        if(player.getDirection() == GameObject.DIR_RIGHT)
                        {
                          ax = player.WIDTH()-SHOT_WH+player.aX;
                          if(player.WIDTH()-SHOT_WH+player.aX > LEVEL_QWH)
                          {
                            PX = PX+1;
                            ax = player.aX;
                          }
                        }
                        else
                          ax = player.aX;
                        shot[i].setState(0);
                        shot[i].setActiveState(true);
                        shot[i].setCoord(PX,player.Y());
                        shot[i].setDirection(player.getDirection());
                        shot[i].aX = ax;
                        shot[i].aY = player.HEIGHT()/2-SHOT_WH;
                        break;
                       }
                    }
                    break;
         }
        }
        gameAction();
    }

    public void gameAction()
    {

      if(globalcounter <255)
        globalcounter++;
      else
        globalcounter = 0;

      int xCoord = player.X()+FAR_DISTANCE;
      if(xCoord > cur_stage[0].length-FAR_DISTANCE)
      {
       i_PlayerState = Gamelet.PLAYERSTATE_WON;
       if(i_GameStage >= Stage.TOTAL_STAGES)
          i_GameState = Gamelet.GAMESTATE_OVER;
      }

      if(PLAYER_DEAD_ANIM > 0)
      {
        if(PLAYER_DEAD_ANIM == 4)
        {
         i_PlayerState = Gamelet.PLAYERSTATE_LOST;
         i_GameState = Gamelet.GAMESTATE_OVER;
         return;
        }
        if(globalcounter %50 == 0)
          PLAYER_DEAD_ANIM++;
        return;
      }

      for(int i=0;i<enemy.length;i++)
        if(enemy[i].vDir > 0 && globalcounter%3 == 0)
         enemy[i].vDir--;

      // Player shot
      for(int i=0;i<shot.length;i++)
       shotAction(shot[i]);
      // ----------------------------------------
      // Collisions with enemyes
      for(int i=0;i<enemy.length;i++)
       if(player.X() == enemy[i].X() && player.Y() == enemy[i].Y())
         if(globalcounter%5 == 0)
         {
          live--;
          if(live<=0)
          {
            live = 0;
            PLAYER_DEAD_ANIM = 4;
          }
         }
      // ----------------------------------------
      // Enemy actions
      if(enemyshot.isActive())
      {
        if(Math.abs(enemyshot.aX) < LEVEL_QWH)
        {
          enemyshot.aX--;
        }
        else
        {
          enemyshot.aX = -1;
          enemyshot.shiftX(-1);
          if(player.X() == enemyshot.X() && player.Y() == enemyshot.Y())
          {
            live -=5;
            if(live<=0)
            {
              live = 0;
              PLAYER_DEAD_ANIM = 4;
            }
            enemyshot.setActiveState(false);
          }
          enemyshot.setState(enemyshot.getState()+1);
          if(
              enemyshot.getState()>=FAR_DISTANCE-1 ||
              !checkQuadPermiability(enemyshot.X()-1,enemyshot.Y())
            )
          {
            enemyshot.setActiveState(false);
            enemyshot.aX = 0;
          }
        }
      }
      else
      if(getRandomInt(50)==1)
      {
        int nearly_index = -1;
        for(int i=0;i<enemy.length;i++)
          if(enemy[i].isActive())
          {
            nearly_index = i;
            break;
          }
        if(nearly_index != -1)
        {
         int min = enemy[nearly_index].X()- player.X();
         for(int i=nearly_index+1;i<enemy.length;i++)
         {
           int mn = enemy[i].X()- player.X();
           if(mn < min && enemy[i].isActive())
           {
             nearly_index = i;
             min = mn;
             break;
           }
         }
         if(min <= FAR_DISTANCE)
         {
          enemyshot.setActiveState(true);
          enemyshot.setCoord(enemy[nearly_index].X(),enemy[nearly_index].Y());
          enemyshot.aX = 0;
          enemyshot.setState(0);
          enemyshot.aY = ENEMY_H/2-SHOT_WH;
         }
        }
      }
      if(!aircraft.isActive() && getRandomInt(AIR_RANDOM)==1)
      {
        aircraft.setActiveState(true);
        aircraft.aX = -1;
        aircraft.aY = 0;
        aircraft.setState(0);
        aircraft.setCoord(player.X()+FAR_DISTANCE*2,0);
      }
      else
      {
        if(Math.abs(aircraft.aX) < LEVEL_QWH)
         aircraft.aX -=2;
        else
        {
          aircraft.aX = 0;
          aircraft.shiftX(-1);
          aircraft.setState(aircraft.getState()+1);
          if(aircraft.getState()>FAR_DISTANCE*3)
          {
            aircraft.setActiveState(false);
          }
        }
      }
      generateRandomEnemy();
      for(int i=0;i<enemy.length;i++)
      {
       if(enemy[i].isActive())
       {
        if(enemy[i].getDirection() == GameObject.DIR_STOP)
        {
         if(getRandomInt(4) == 2 )
           enemy[i].setDirection(GameObject.DIR_RIGHT);
         else
         if(getRandomInt(4) == 2 )
           enemy[i].setDirection(GameObject.DIR_LEFT);
        }
        if(enemy[i].getDirection() == GameObject.DIR_RIGHT)
        {
          if(
              !checkQuadPermiability(enemy[i].X()+1,enemy[i].Y()) ||
              checkQuadPermiability(enemy[i].X()+1,enemy[i].Y()+1)
             )
          {
           enemy[i].setDirection(GameObject.DIR_STOP);
          }
          else
          {
           if(enemy[i].aX < LEVEL_QWH)
           {
            if(getRandomInt(4)==2)
             enemy[i].aX++;
            if(enemy[i].aX%2 == 0)
             enemy[i].setState(STEP_PHASE1);
            else
             enemy[i].setState(STEP_PHASE2);
           }
           else
           {
             enemy[i].aX = 0;
             enemy[i].shiftX(1);
             enemy[i].setDirection(GameObject.DIR_STOP);
           }
          }
        }
        if(enemy[i].getDirection() == GameObject.DIR_LEFT)
        {
          if(
              !checkQuadPermiability(enemy[i].X()-1,enemy[i].Y()) ||
              checkQuadPermiability(enemy[i].X()-1,enemy[i].Y()+1)
             )
          {
           enemy[i].setDirection(GameObject.DIR_STOP);
          }
          else
          {
           if(Math.abs(enemy[i].aX) < LEVEL_QWH)
           {
            if(getRandomInt(4)==2)
             enemy[i].aX--;
            if(enemy[i].aX%2 == 0)
             enemy[i].setState(STEP_PHASE1);
            else
             enemy[i].setState(STEP_PHASE2);
           }
           else
           {
             enemy[i].aX = 0;
             enemy[i].shiftX(-1);
             enemy[i].setDirection(GameObject.DIR_STOP);
           }
          }
        }
       }
      }
      // ----------------------------------------
      // Player actions
      if(cur_stage[player.Y()][player.X()] == Stage.HEALTH)
      {
       live+=10;
       if(live > MAXLIVE)
         live = MAXLIVE;
       cur_stage[player.Y()][player.X()] = 0;
       if (_gameActionListener != null)
           _gameActionListener.gameAction(ACTION_HEALTHUPDATE, player.X(),player.Y());
      }
      if(down_action && player.aY == 0)
        player.aY = 1;
        if(player.aY > 0)
        {
          if(Math.abs(player.aY) < LEVEL_QWH)
          {
            player.aY += 1;
          }
          else
          {
            player.aY = 0;
            player.shiftY(1);
            if(checkQuadPermiability(player.X(),player.Y()+1))
              player.aY = 1;
            else
            {
             player_state = STEP_STOP;
             down_action = false;
            }
          }
          return;
        }
      // move player
      if(player.aX  > 0 && player.X() < cur_stage[0].length-1 && checkQuadPermiability(player.X()+1,player.Y()))
      {
        if(player.aX < LEVEL_QWH)
        {
          player.aX+=1;
//          player_state = player.aX/(LEVEL_QWH/4);
          player_state = player.aX*4/LEVEL_QWH;
          if(player_state > 3) player_state = 3;
          /*
          if(player.aX%2 == 0)
           player_state = STEP_PHASE1;
          else
           player_state = STEP_PHASE2;
          */
        }
        else
        {
          player.aX = 0;
          player.shiftX(1);
          if(checkQuadPermiability(player.X(),player.Y()+1))
            down_action = true;
          player_state = STEP_STOP;
        }
      }
      if(player.aX  < 0 && player.X() > 0 && checkQuadPermiability(player.X()-1,player.Y()))
      {
        if(Math.abs(player.aX) < LEVEL_QWH)
        {
          player.aX-=1;
          player_state = Math.abs(player.aX)*4/LEVEL_QWH;
          if(player_state > 3) player_state = 3;
          /*
          if(player.aX%2 == 0)
           player_state = STEP_PHASE1;
          else
           player_state = STEP_PHASE2;
          */
        }
        else
        {
          player.aX = 0;
          player.shiftX(-1);
          if(checkQuadPermiability(player.X(),player.Y()+1))
            down_action = true;
          player_state = STEP_STOP;
        }
      }
      if(player.aY  < 0)
      {
        boolean flag = false;
        if(player.getDirection() == GameObject.DIR_RIGHT)
         flag = checkQuadPermiability(player.X()+1,player.Y()-1);
        if(player.getDirection() == GameObject.DIR_LEFT)
          flag = checkQuadPermiability(player.X()-1,player.Y()-1);
        if(!flag)
        {
          player.aY = 0;
        }
        else
        {
         if(Math.abs(player.aY) < LEVEL_QWH)
         {
           player.aY-=2;
           player_state = STEP_JUMP;
           if(player.getDirection() == GameObject.DIR_RIGHT)
            player.aX++;
           if(player.getDirection() == GameObject.DIR_LEFT)
            player.aX--;
         }
         else
         {
           player.aY = 0;
           player.aX = 0;
           if(player.getDirection() == GameObject.DIR_RIGHT)
            player.shiftX(1);
           if(player.getDirection() == GameObject.DIR_LEFT)
            player.shiftX(-1);
           player.shiftY(-1);
           player_state = STEP_STOP;
           if(checkQuadPermiability(player.X(),player.Y()+1))
             down_action = true;
         }
        }
      }
    }

    public void generateRandomEnemy()
    {
      // Check for leaved enemy
      for(int i=0;i<enemy.length;i++)
        if(enemy[i].X() < player.X()-FAR_DISTANCE)
          enemy[i].setActiveState(false);

      int xCoord = player.X()+FAR_DISTANCE;
      // Seek free palace
      if(xCoord < cur_stage[0].length-FAR_DISTANCE)
      {
       for(int x=xCoord;x<xCoord+FAR_DISTANCE*2;x++)
       {
        for(int y=Stage.LEVEL_HEIGHT-1;y>=0;y--)
        if(checkQuadPermiability(x,y,true))
        {
          if(checkEnemyPermiability(x,y))
            break;
          int enm_num = enemy.length;
          if(i_GameStage == 0)
            enm_num = 2;
          if(i_GameStage == 1)
            enm_num = 3;
          if(i_GameStage == 2)
            enm_num = 4;
          if(getRandomInt(3)==1)
          for(int i=0;i<enm_num;i++)
          {
            if(!enemy[i].isActive() && enemy[i].vDir == 0)
            {
             enemy[i].setActiveState(true);
             if(getRandomInt(2) == 1)
               enemy[i].setDirection(GameObject.DIR_RIGHT);
             else
               enemy[i].setDirection(GameObject.DIR_LEFT);
             enemy[i].setState(STEP_STOP);
             enemy[i].setCoord(x,y);
             enemy[i].setType(getRandomInt(3));
             enemy[i].vDir = 0;
             enemy[i].aX = 0;
             enemy[i].aY = 0;
             break;
            }
          }
          break;
        }
       }
      }
    }

    public void shotAction(GameObject shobj)
    {
      if(shobj.isActive())
      {
        if(shobj.getDirection() == GameObject.DIR_RIGHT)
        {
          if(!checkQuadPermiability(shobj.X(),shobj.Y(),true))
            shobj.setActiveState(false);
          if(Math.abs(shobj.aX) < LEVEL_QWH)
          {
           shobj.aX+=2;
          }
          else
          {
            shobj.aX = 1;
            shobj.setState(shobj.getState()+1);
            shobj.shiftX(1);
          }
        }
        if(shobj.getDirection() == GameObject.DIR_LEFT)
        {
          if(!checkQuadPermiability(shobj.X()-1,shobj.Y(),true))
            shobj.setActiveState(false);
          if(Math.abs(shobj.aX) < LEVEL_QWH)
          {
           shobj.aX-=2;
          }
          else
          {
            shobj.aX = -1;
            shobj.setState(shobj.getState()+1);
            shobj.shiftX(-1);
          }
        }
        if(shobj.getState() > 5)
          shobj.setActiveState(false);
        for(int i=0;i<enemy.length;i++)
          if(shobj.X() == enemy[i].X() && shobj.Y() == enemy[i].Y() && enemy[i].isActive())
          {
            enemy[i].vDir = ENEMY_BURN;
            enemy[i].setActiveState(false);
            shobj.setActiveState(false);
            i_PlayerScore+=2;
            break;
          }
      }
    }

    public boolean checkEnemyPermiability(int x,int y)
    {
      for(int i=0;i<enemy.length;i++)
        if(enemy[i].X() == x && enemy[i].Y() == y)
         return true;
      return false;
    }
    public boolean checkQuadPermiability(int x,int y)
    {
      return checkQuadPermiability(x,y,false);
    }
    public boolean checkQuadPermiability(int x,int y,boolean ignoreenemy)
    {
     if(y<0 || y>Stage.LEVEL_HEIGHT-1 || x <0 || x>cur_stage[0].length-1)
       return false;
     /*
     if(!ignoreenemy)
     {
      for(int i=0;i<enemy.length;i++)
        if(enemy[i].X() == x && enemy[i].Y() == y)
         return false;
     }
     */
     int c = cur_stage[y][x];
     try
     {
      if(Stage.prm[c] == 0)
        return true;
      else
        return false;
     } catch (Exception ex)
     {
       ex.printStackTrace();
       System.out.println("MAP BROKEN: x-"+x+" y-"+y);
       return false;
     }
    }

    public void writeToStream(DataOutputStream ds) throws IOException
    {
      ds.writeInt(globalcounter);
      ds.writeInt(live);
      ds.writeBoolean(down_action);
      ds.writeInt(player_state);
      ds.writeInt(PLAYER_DEAD_ANIM);

      writeObject(player,ds);
      writeObject(enemyshot,ds);
      writeObject(aircraft,ds);
      for(int i=0;i<shot.length;i++)
       writeObject(shot[i],ds);
      for(int i=0;i<enemy.length;i++)
       writeObject(enemy[i],ds);

    }
    public void writeObject(GameObject obj,DataOutputStream ds) throws IOException
    {
      int tmp[] = obj.getMeAsByteArray();
      for(int i=0;i<tmp.length;i++)
        ds.writeInt(tmp[i]);
    }

    public void readFromStream(DataInputStream is) throws IOException
    {
      globalcounter = is.readInt();
      live = is.readInt();
      down_action = is.readBoolean();
      player_state = is.readInt();
      PLAYER_DEAD_ANIM = is.readInt();

      readObject(player,is);
      readObject(enemyshot,is);
      readObject(aircraft,is);
      for(int i=0;i<shot.length;i++)
        readObject(shot[i],is);
      for(int i=0;i<enemy.length;i++)
        readObject(enemy[i],is);

      cur_stage = stage_container.getStage(i_GameStage);
    }

    public void readObject(GameObject obj,DataInputStream is) throws IOException
    {
      int tmp[] = new int[GameObject.getStaticArrayLength()];
      for(int i=0;i<tmp.length;i++)
        tmp[i] = is.readInt();
      obj.setMeAsByteArray(tmp);
    }


    public String getGameID()
    {
        return "AlienTerror1287";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 0;
    }


}
