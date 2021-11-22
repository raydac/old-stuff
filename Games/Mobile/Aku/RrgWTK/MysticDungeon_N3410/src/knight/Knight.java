package knight;

import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.GameObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/*
  Особо не проверял!!!

  Атака монстра
  guards[i].inAttack , если true то моснтр атакует когда guardDELAY == 0
  Как я понял 3 кадра анимации удара?
  Тогда можешь их считать по guardDELAY%10 == 0

  Потеря HP
  Наш чар:
  (рисовать звездочку наверно на его морде справа)
  mainlosthp если 0 - то ничего , если >0 то это количество HP которое он потерял
  ~ через пол секунды автоматом убивается

  Монстры:
  guards[N].getState()
  если 0 - то ничего , если >0 то это количество HP которое он потерял
  ~ через пол секунды автоматом убивается

  Сделать меню:
  1. Предметы
  2. Карта
  3. персонаж

*/

public class Knight extends Gamelet
{
    GameActionListener _gameActionListener;
    // -------------------
    public static final int ACTION_BUTTONPUSH = 0;
    public static final int ACTION_DOOR_BUTTON_OPEN = 1;
    public static final int ACTION_DOOR_KEY_OPENEED = 2;
    public static final int ACTION_KEYFOUNDED = 3;
    public static final int ACTION_TREASUREFOUNDED = 4;
    // -------------------
    public static final int OBJECT_NORMAL = 0;
    // Если state обьекта >0 , то это текущее повреждение ему.
    // -------------------
    public static final int GAME_SCREEN = 0;
    public static final int SELECT_SCREEN = 1;
    public static final int ISELECT_MAP = 2;
    public static final int ISELECT_INV = 3;
    public static final int ISELECT_CHR = 4;
    public int GLOBAL_STATE = GAME_SCREEN;
    public int SELECT_STATE = GAME_SCREEN;

    // -------------------
    public static final int STR_INIT = 6;
    public static final int DEX_INIT = 4;
    public static final int INT_INIT = 2;
    // -------------------

    Stages stages = new Stages();
    int W = 100;
    int H = 100;
    int quadWH = 12; // Quads width/height
    int LIVE_W = 10 , LIVE_H = 10;
    int MAX_shockcounter = 60;
    public int gold = 0;
    int globalcounter = 0;
    int shockcounter = MAX_shockcounter;

    // -------------------
    public int MAXTEXTDELAY = 40;
    public int text_delay = 0;
    public boolean TEXT_LEVEL_UP = false;
    int TEXT_GETITEM = 0;

    // -------------------
    public int level[][];
    int exitX,exitY;
    int beginX,beginY;

    // -------------------
    public int MAXLHPCOUNTER = 5;
    int PLAYERATTACKDELAY = 30;
    public int playerAttackBegin = 0;
    int losthpcounter = 0;
    public int mainlosthp = 0;
    public GameObject bulgar = new GameObject(LIVE_W, LIVE_H, true);
    int MAXHP = 20;
    public int hp;
    public int LVL;
    public int STR;
    public int DEX;
    public int INT;
    public int EXP;
    public int KEYPOSITION = 7;
    public int KEY_LEN = 5;
    public int ARMPOSITION = 0;
    public int ARM_LEN = 3;
    public int WPNPOSITION = 3;
    public int WPN_LEN = 4;
    public int keys[] = new int[3+4+5]; // 3 armors , 4 weapon , 5 keys
    public int wear[] = new int[3];     // Player current equipment

    public final static int WEAPON_WEAR = 0;
    public final static int ARMOR_WEAR = 1;
    public final static int SHIELD_WEAR = 2;

    boolean beginMove = false;
    int shiftX = 0 , shiftY = 0;
    boolean bul_move = false;
    // -------------------

    public Guard guards[];
    public int max_guardDELAY = 30;
    public int guardDELAY = 0;
/*
    public class Guard extends GameObject
    {
        int losthpcounter = 0;
        int ID;
        int defaultDir; // 0 - horizontal , 1 - vertical
        int HP;
        public boolean inAttack = false;

        public Guard(int w, int h, boolean acv)
        {
            super(w, h, acv);
        }

        public int getArrayLength()
        {
            return GameObject.getStaticArrayLength()+3;
        }

        public int[] getMeAsByteArray()
        {
            int ret[] = new int[3 + GameObject.getStaticArrayLength()];
            ret[0] = ID;
            ret[1] = defaultDir;
            ret[2] = HP;
            System.arraycopy(super.getMeAsByteArray(), 0, ret, 3, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(int[] ret)
        {
            ID = ret[0];
            defaultDir = ret[1];
            HP = ret[2];
            int tmp[] = new int[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 3, tmp, 0, GameObject.getStaticArrayLength());
            super.setMeAsByteArray(tmp);
        }

    }
*/
    public Knight(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
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
        MAXHP = 20+(LVL+1)*5;
        hp = MAXHP;
        LVL = 0;
        STR = 6;
        DEX = 4;
        INT = 2;
        EXP = 0;
        gold = 0;
	keys = new int[keys.length];
	wear = new int[wear.length];//{Stages.MAGIC_SWORD,Stages.PLATE_ARMOR,Stages.PLATE_SHIELD};//[wear.length];
        initStage(0);
        GLOBAL_STATE = GAME_SCREEN;
    }

    public void initStage(int _stage)
    {
        i_GameStage = _stage;
        super.initStage(_stage);
        level = stages.getStage(_stage);
        int c[] = stages.getCoords(_stage);
        shockcounter = MAX_shockcounter;
        beginX = c[0];
        beginY = c[1];
        exitX = c[2];
        exitY = c[3];
        guardDELAY = 0;
        bulgar.setDirection(GameObject.DIR_STOP);
        bulgar.vDir = GameObject.DIR_UP;
        bulgar.aX = 0;
        bulgar.aY = 0;
        shiftX = 0;
        shiftY = 0;
        beginMove = false;
        bulgar.setCoord(beginX, beginY);
        for (int y = KEYPOSITION; y < keys.length; y++)
            keys[y] = 0;
        GLOBAL_STATE = GAME_SCREEN;
        initGuards();
    }

    public void initGuards()
    {
        int glen = 0;
        for (int y = 0; y < level.length; y++)
            for (int x = 0; x < level[0].length; x++)
            {
                if (level[y][x] >= 93 && level[y][x] <= 109)
                    glen++;
            }
        guards = new Guard[glen];
        guardDELAY = 0;
        int cnt = 0;
        for (int y = 0; y < level.length; y++)
            for (int x = 0; x < level[0].length; x++)
            {
                if (level[y][x] >= 93 && level[y][x] <= 109)
                {
                    guards[cnt] = new Guard(LIVE_W, LIVE_H, true);
                    guards[cnt].setCoord(x, y);
                    guards[cnt].HP = 7*(i_GameStage+1);
                    guards[cnt].losthpcounter = 0;
                    guards[cnt].aX = 0;
                    guards[cnt].aY = 0;
                    guards[cnt].ID = level[y][x];
                    if (level[y][x - 1] == 0 && level[y][x + 1] == 0)
                    {
                        guards[cnt].defaultDir = 0;
                        guards[cnt].setDirection(GameObject.DIR_LEFT);
                    }
                    else
                    {
                        guards[cnt].setDirection(GameObject.DIR_UP);
                        guards[cnt].defaultDir = 1;
                    }
                    cnt++;
                }
            }
    }


    public void nextGameStep(int _playermoveobject)
    {

    }

    public void nextGameStep(Object _playermoveobject)
    {
      // ===============================================
      // Menu
        game_PMR keys = (game_PMR) _playermoveobject;

	int i = -1;
	switch (keys.i_Value) {
	  case game_PMR.BUTTON_INVENT_MAP: i = Knight.ISELECT_MAP; break;
	  case game_PMR.BUTTON_INVENT_INV: i = Knight.ISELECT_INV; break;
	  case game_PMR.BUTTON_INVENT_CHR: i = Knight.ISELECT_CHR; break;
	}
	if(i!=-1){
	  if (i == SELECT_STATE && GLOBAL_STATE == SELECT_SCREEN){
	    GLOBAL_STATE = GAME_SCREEN;
            SELECT_STATE = GAME_SCREEN;
	  } else {
	    GLOBAL_STATE = SELECT_SCREEN;
            SELECT_STATE = i;
	    return;
	  }
	}

	if (GLOBAL_STATE == GAME_SCREEN)

        // ===============================================
        // Game action
        switch (keys.i_Value)
        {
            case game_PMR.BUTTON_LEFT:
                {
                  shiftY = 0;
                  shiftX = 0;
                  if(bulgar.vDir <=1)
                    bulgar.vDir = 4;
                  else
                    bulgar.vDir --;
                }
                break;
            case game_PMR.BUTTON_RIGHT:
                {
                  shiftY = 0;
                  shiftX = 0;
                  if(bulgar.vDir >=4)
                    bulgar.vDir = 1;
                  else
                    bulgar.vDir ++;
                }
                break;
            case game_PMR.BUTTON_UP:
                {
                  shiftY = 0;
                  shiftX = 0;
                  bulgar.setDirection(bulgar.vDir);
                }
                break;
            case game_PMR.BUTTON_DOWN:
                {
                  if(bulgar.vDir == GameObject.DIR_DOWN)
                    bulgar.aY = -1;
                  if(bulgar.vDir == GameObject.DIR_UP)
                    bulgar.aY = 1;
                  if(bulgar.vDir == GameObject.DIR_LEFT)
                    bulgar.aX = 1;
                  if(bulgar.vDir == GameObject.DIR_RIGHT)
                    bulgar.aX = -1;
                }
                break;
            case game_PMR.BUTTON_FIRE:
            {
              if(playerAttackBegin == 0)
                playerAttackBegin =  PLAYERATTACKDELAY;
              break;
            }
        }
	  else
	    if ( SELECT_STATE == ISELECT_INV && keys.i_Value > 0) {
	      i = keys.i_Value -1;
	      if (i < 3){

		int idx = SHIELD_WEAR;
		int j = this.keys[i];

		if(j==0){
		  if(wear[SHIELD_WEAR]==0) idx=ARMOR_WEAR;
		} else if(j!=Stages.PLATE_SHIELD) idx=ARMOR_WEAR;

		this.keys[i]=wear[idx];
		wear[idx] = j;
	      } else
	      if (i < 7){  // exchange weapon
		int j = this.keys[i];
		this.keys[i]=wear[WEAPON_WEAR];
		wear[WEAPON_WEAR] = j;
	      }
	    }

        gameAction();
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        bulgar.setCoord(beginX, beginY);
        bulgar.setDirection(GameObject.DIR_STOP);
        initGuards();
    }

    public void gameAction()
    {
        if (i_PlayerState != PLAYERSTATE_NORMAL)
            return;

        if(GLOBAL_STATE == SELECT_SCREEN)
        {

          return;
        }

        if(text_delay > 0 )
          text_delay--;
        if(text_delay == 0)
        {
          TEXT_LEVEL_UP = false;
          TEXT_GETITEM = 0;
        }

        if(playerAttackBegin == PLAYERATTACKDELAY)
        {
          playerAttackAction();
          playerAttackBegin--;
        }
        else
          if(playerAttackBegin > 0)
           playerAttackBegin--;

        for (int i = 0; i < guards.length; i++)
          if(guards[i].losthpcounter > 0)
            guards[i].losthpcounter--;
          else
            guards[i].setState(0);

        if(losthpcounter > 0)
          losthpcounter--;
        else
          mainlosthp = 0;

        globalcounter++;
        if(globalcounter > 255)
        {
            hp++;
            if(hp >= MAXHP)
              hp = MAXHP;
            globalcounter = 0;
        }

        // Electroshok counter
        if(shockcounter == 0)
        {
        for (int y = 0; y < level.length; y++)
            for (int x = 0; x < level[0].length; x++)
            {
                if(level[y][x] == 150)
                {
                    level[y][x] = 151;
                    continue;
                }
                if(level[y][x] == 151)
                    level[y][x] = 150;
            }
        }
        shockcounter--;
        if(shockcounter < 0 )
            shockcounter = MAX_shockcounter;
        // Thief
        if (bulgar.getDirection() != GameObject.DIR_STOP && !beginMove)
        {
            if (checkDirection(bulgar.X(), bulgar.Y(), bulgar.getDirection()))
            {
                beginMove = true;
                if (bulgar.getDirection() == GameObject.DIR_LEFT)
                    shiftX = -1;
                if (bulgar.getDirection() == GameObject.DIR_RIGHT)
                    shiftX = 1;
                if (bulgar.getDirection() == GameObject.DIR_UP)
                    shiftY = -1;
                if (bulgar.getDirection() == GameObject.DIR_DOWN)
                    shiftY = 1;
            }
            else
                bulgar.setDirection(GameObject.DIR_STOP);
        }
        if(bulgar.aX !=0 || bulgar.aY !=0)
        {
          int dr = 0;
          if(bulgar.aX == -1)
            dr = GameObject.DIR_LEFT;
          if(bulgar.aX == 1)
            dr = GameObject.DIR_RIGHT;
          if(bulgar.aY == -1)
            dr = GameObject.DIR_UP;
          if(bulgar.aY == 1)
            dr = GameObject.DIR_DOWN;
          if (checkDirection(bulgar.X(), bulgar.Y(), dr))
           bulgar.setCoord(bulgar.X() + bulgar.aX, bulgar.Y() + bulgar.aY);
          bulgar.aX = 0;
          bulgar.aY = 0;
        }
        if (beginMove)
        {
                bulgar.setCoord(bulgar.X() + shiftX, bulgar.Y() + shiftY);
                bulgar.vDir = bulgar.getDirection();
                bulgar.setDirection(GameObject.DIR_STOP);
                beginMove = false;
                shiftX = 0;
                shiftY = 0;
        }
        // Exit
        if(bulgar.X() == exitX && bulgar.Y() == exitY)
        {
          i_PlayerState = Gamelet.PLAYERSTATE_WON;
          _gameActionListener.gameAction(6);
          if(i_GameStage >= Stages.TOTAL_STAGES)
             i_GameState = Gamelet.GAMESTATE_OVER;
        }
        int qd = level[bulgar.Y()][bulgar.X()];

        // Electroshok
        if(qd == 150)
        {
            diePlayer();
        }
        // Floor button
        if (qd >= 70 && qd <= 89)
        {
            level[bulgar.Y()][bulgar.X()] = 90;
            if (_gameActionListener != null)
                _gameActionListener.gameAction(ACTION_BUTTONPUSH, bulgar.X(),bulgar.Y());
            for (int y = 0; y < level.length; y++)
                for (int x = 0; x < level[0].length; x++)
                {
                    if (level[y][x] >= 10 && level[y][x] <= 29)
                    {
                        if (level[y][x] - 10 == qd - 70)
                        {
                            if (_gameActionListener != null)
                                _gameActionListener.gameAction(ACTION_DOOR_BUTTON_OPEN, x, y);
                            _gameActionListener.gameAction(5);
                            if (level[y][x] < 20)
                                level[y][x] = 91;
                            else
                                level[y][x] = 92;
                        }
                    }
                }

        }
        // Keys
        if (qd >= 50 && qd <= 69)
        {
            for (int y = KEYPOSITION; y < keys.length; y++)
                if (keys[y] == 0)
                {
                    keys[y] = qd;
                    level[bulgar.Y()][bulgar.X()] = 0;
                    _gameActionListener.gameAction(1);
                    if (_gameActionListener != null)
                        _gameActionListener.gameAction(ACTION_KEYFOUNDED, bulgar.X(), bulgar.Y());
                    break;
                }
        }
        // Doors for keys
        for (int y = bulgar.Y() - 1; y < bulgar.Y() + 2; y++)
            for (int x = bulgar.X() - 1; x < bulgar.X() + 2; x++)
            {
                if (x >= 0 && y >= 0 && x < level[0].length && y < level.length)
                {
                    int nqd = level[y][x];
                    if (nqd >= 10 && nqd <= 29)
                    {
                        for (int i = KEYPOSITION; i < keys.length; i++)
                            if (keys[i] != 0 && keys[i] - 50 == nqd - 10)
                            {
                                if (level[y][x] < 20)
                                    level[y][x] = 91;
                                else
                                    level[y][x] = 92;
                                if (_gameActionListener != null)
                                    _gameActionListener.gameAction(ACTION_DOOR_KEY_OPENEED, x, y);
                                keys[i] = 0;
                                break;
                            }
                    }
                }
            }
        // Treasure
        if (qd >= 110 && qd <= 127)
        {
            if (_gameActionListener != null)
                _gameActionListener.gameAction(ACTION_TREASUREFOUNDED, bulgar.X(), bulgar.Y());
            if(qd >= 110 && qd <= 113)
            {
              _gameActionListener.gameAction(1);
              addItem(WPNPOSITION,WPN_LEN,qd);
              text_delay = MAXTEXTDELAY;
              TEXT_GETITEM = qd;
            }
            else
            if(qd >= 114 && qd <= 116)
            {
              _gameActionListener.gameAction(1);
              addItem(ARMPOSITION,ARM_LEN,qd);
              text_delay = MAXTEXTDELAY;
              TEXT_GETITEM = qd;
            }
            else
            {
             gold += ((qd - 100) + 1) * 3;
             _gameActionListener.gameAction(0);
             i_PlayerScore = gold;
            }
            level[bulgar.Y()][bulgar.X()] = 0;
        }
        // Guards
        if (guardDELAY == 0)
        {
            for (int i = 0; i < guards.length; i++)
            {
                if(!guards[i].isActive())
                  continue;
                int guard_num = 0;
                guard_num = level[guards[i].Y()][guards[i].X()];
                int gx = guards[i].X();
                int gy = guards[i].Y();
                // Hunt thief
                boolean hunt = false;
                int shx = Math.abs(bulgar.X() - gx);
                int shy = Math.abs(bulgar.Y() - gy);
                // check collision
                if (shx <= 1 && shy <= 1)
                {
                    monsterAttackPlayer(guard_num);
                    guards[i].inAttack = true;
                    continue;
                }
                else
                  guards[i].inAttack = false;
                if (shx <= 3 && shy <= 2)
                {
                    if (shx > 0)
                        shx = (bulgar.X() - gx) / shx;
                    else
                        shx = 0;
                    if (shy > 0)
                        shy = (bulgar.Y() - gy) / shy;
                    else
                        shy = 0;

                    if ((level[gy][gx + shx] < 1 || level[gy][gx + shx] > 29) && guards[i].aX == 0 && guards[i].aY == 0)
                    {
                        if (shx > 0)
                        {
                            guards[i].setDirection(GameObject.DIR_RIGHT);
                        }
                        else
                            if (shx < 0)
                            {
                                guards[i].setDirection(GameObject.DIR_LEFT);
                            }
                            else
                            {
                                guards[i].setDirection(GameObject.DIR_STOP);
                            }
                        hunt = true;
                    }

                    if ((level[gy + shy][gx] < 1 || level[gy + shy][gx] > 29) && guards[i].aX == 0 && guards[i].aY == 0)
                    {
                        if (shy > 0)
                        {
                            guards[i].setDirection(GameObject.DIR_DOWN);
                        }
                        else
                            if (shy < 0)
                            {
                                guards[i].setDirection(GameObject.DIR_UP);
                            }
                            else
                                if (guards[i].getDirection() != GameObject.DIR_RIGHT && guards[i].getDirection() != GameObject.DIR_LEFT)
                                {
                                    guards[i].setDirection(GameObject.DIR_STOP);
                                }
                        hunt = true;
                    }
                }
                // horizontal
                if (!hunt)
                {
                    if (guards[i].defaultDir == 0)
                    {
                        int s_y = level[gy][gx - 1];
                        if (guards[i].getDirection() == GameObject.DIR_LEFT && ((s_y >= 1 && s_y <= 29)))
                            guards[i].setDirection(GameObject.DIR_RIGHT);
                        s_y = level[gy][gx + 1];
                        if (guards[i].getDirection() == GameObject.DIR_RIGHT && ((s_y >= 1 && s_y <= 29)))
                            guards[i].setDirection(GameObject.DIR_LEFT);
                    }
                    // vertical
                    else
                    {
                        int s_y = level[gy - 1][gx];
                        if (guards[i].getDirection() == GameObject.DIR_UP && ((s_y >= 1 && s_y <= 29)))
                            guards[i].setDirection(GameObject.DIR_DOWN);
                        s_y = level[gy + 1][gx];
                        if (guards[i].getDirection() == GameObject.DIR_DOWN && ((s_y >= 1 && s_y <= 29)))
                            guards[i].setDirection(GameObject.DIR_UP);
                    }
                }

                if (guards[i].getDirection() == GameObject.DIR_UP && (level[gy - 1][gx] < 1 || level[gy - 1][gx] > 29))
                {
                        guards[i].vDir = GameObject.DIR_UP;
                        guards[i].aY = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftY(-1);
                        level[gy][gx] = guard_num;
                }
                if (guards[i].getDirection() == GameObject.DIR_DOWN && (level[gy + 1][gx] < 1 || level[gy + 1][gx] > 29))
                {
                        guards[i].vDir = GameObject.DIR_DOWN;
                        guards[i].aY = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftY(1);
                        level[gy][gx] = guard_num;
                }
                if (guards[i].getDirection() == GameObject.DIR_LEFT && (level[gy][gx - 1] < 1 || level[gy][gx - 1] > 29))
                {
                        guards[i].vDir = GameObject.DIR_LEFT;
                        guards[i].aX = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftX(-1);
                        level[gy][gx] = guard_num;
                }
                if (guards[i].getDirection() == GameObject.DIR_RIGHT && (level[gy][gx + 1] < 1 || level[gy][gx + 1] > 29))
                {
                        guards[i].vDir = GameObject.DIR_RIGHT;
                        guards[i].aX = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftX(1);
                        level[gy][gx] = guard_num;
                }
            }
        }
        guardDELAY--;
        if (guardDELAY < 0)
            guardDELAY = max_guardDELAY;
    }

    public void addItem(int pos,int len,int item)
    {
     for(int i=pos;i<pos+len;i++)
       if(keys[i] == 0)
       {
         keys[i] = item;
         break;
       }
    }

    public void playerAttackAction()
    {
      int dir = bulgar.vDir;
      int x,y;
      x = bulgar.X();
      y = bulgar.Y();
      if(dir == GameObject.DIR_UP)
        y--;
      if(dir == GameObject.DIR_DOWN)
        y++;
      if(dir == GameObject.DIR_LEFT)
        x--;
      if(dir == GameObject.DIR_RIGHT)
        x++;
      for (int i = 0; i < guards.length; i++)
      {
       if(guards[i].X() == x && guards[i].Y() == y && guards[i].isActive())
       {
         guards[i].losthpcounter = MAXLHPCOUNTER;
         int losthp = getRandomInt(getAADPoints(Stages.ATK));
         guards[i].HP -= losthp;
         guards[i].setState(losthp);
         // System.out.println("Monster lost "+losthp+" HP");
         if(guards[i].HP <=0)
         {
           _gameActionListener.gameAction(3);
           guards[i].setActiveState(false);
           // System.out.println("Monster died");
           addExp();
         }
       }
      }
    }

    public void monsterAttackPlayer(int guard_num)
    {
     int rnd = 2+i_GameStage/2;
     int losthp = getRandomInt(rnd)+getAADPoints(Stages.ARM)-getRandomInt(getAADPoints(Stages.DEF));
     if(losthp>0) hp-=losthp;
     losthpcounter = MAXLHPCOUNTER;
     mainlosthp = losthp;
     // System.out.println("Player lost "+losthp+" HP");
     if(hp<=0)
     {
       hp = 0;
       diePlayer();
       // System.out.println("Player died");
     }
    }

    public int getAADPoints(int type){
        int pt = Stages.AAD[0][type];
        int n;
        for (int idx = 0; idx < wear.length ; idx++){
	  n = wear[idx]-Stages.FIRST_AAD_ITEM_IDX+1;  // FirstWeapon
          if (n>0 && n < Stages.TOTAL_AAD_ITEMS)
	  pt += Stages.AAD[n][type];
        }
      return pt;
    }

    public void addExp()
    {
      int exp = (i_GameStage+1)*10;
      if(EXP>=14 && LVL==0) return;
      EXP += exp;
      if(EXP>=14 && LVL==0)
      {
       LVL++;
       STR+=2;
       DEX++;
       MAXHP = 20+(LVL+1)*5;
       hp = MAXHP;
       text_delay = MAXTEXTDELAY;
       TEXT_LEVEL_UP = true;
       // System.out.println("Level up to "+LVL);
      }
      if(LVL>0 && EXP>=((LVL+1)*1)*((LVL+1)*14))
      {
       if(exp>=((LVL+1)*1)*((LVL+1)*14)) return;
       LVL++;
       STR++;
       if(LVL%2 == 0)
       {
         STR++;
         INT++;
       }
       DEX++;
       MAXHP = 20+(LVL+1)*5;
       hp = MAXHP;
       text_delay = MAXTEXTDELAY;
       TEXT_LEVEL_UP = true;
       _gameActionListener.gameAction(4);
       // System.out.println("Level up to "+LVL);
      }
    }

    public void diePlayer()
    {
        i_PlayerState = PLAYERSTATE_LOST;
        _gameActionListener.gameAction(2);
        bulgar.setDirection(GameObject.DIR_STOP);
        bulgar.aX = 0;
        bulgar.aY = 0;
        shiftX = 0;
        shiftY = 0;
        beginMove = false;
        this.i_GameState = Gamelet.GAMESTATE_OVER;
    }

    public boolean checkDirection(int x, int y, int dir)
    {
        int sx = 0,sy = 0;
        if (dir == GameObject.DIR_LEFT)
            sx = -1;
        if (dir == GameObject.DIR_RIGHT)
            sx = 1;
        if (dir == GameObject.DIR_UP)
            sy = -1;
        if (dir == GameObject.DIR_DOWN)
            sy = 1;
        if (sx + x >= 0 && sy + y >= 0 && sx + x < level[0].length && sy + y < level.length)
        {
            if (level[sy + y][sx + x] >= 1 && level[sy + y][sx + x] <= 29)
                return false;
        }
        else
            return false;
        for (int i = 0; i < guards.length; i++)
         if(guards[i].X() == sx + x && guards[i].Y() == sy + y && guards[i].isActive())
           return false;

        return true;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeInt(i_GameStage);
        _dataOutputStream.writeInt(gold);
        _dataOutputStream.writeInt(globalcounter);
        _dataOutputStream.writeInt(hp);
        _dataOutputStream.writeInt(MAXHP);
        for(int i=0;i<keys.length;i++)
            _dataOutputStream.writeInt(keys[i]);
        for(int i=0;i<wear.length;i++)
            _dataOutputStream.writeInt(wear[i]);
        _dataOutputStream.writeInt(shiftX);
        _dataOutputStream.writeInt(shiftY);
        _dataOutputStream.writeBoolean(bul_move);
        _dataOutputStream.writeInt(shockcounter);
        _dataOutputStream.writeInt(guardDELAY);
        _dataOutputStream.writeBoolean(beginMove);

        _dataOutputStream.writeShort(LVL);
        _dataOutputStream.writeShort(STR);
        _dataOutputStream.writeShort(DEX);
        _dataOutputStream.writeShort(INT);
        _dataOutputStream.writeShort(EXP);

        writeIntArray(_dataOutputStream,bulgar.getMeAsByteArray());

        _dataOutputStream.writeInt(guards.length);
        for (int i = 0; i < guards.length; i++)
            writeIntArray(_dataOutputStream,guards[i].getMeAsByteArray());

        int btmp[][] = stages.getStage(i_GameStage);
        for (int y = 0; y < level.length; y++)
            for (int x = 0; x < level[0].length; x++)
            {
                 if(btmp[y][x] != level[y][x])
                 {
                     _dataOutputStream.writeByte(x);
                     _dataOutputStream.writeByte(y);
                     _dataOutputStream.writeByte(level[y][x]);
                 }
            }
        _dataOutputStream.writeByte((byte)0xff);

    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_GameStage = _dataInputStream.readInt();
        initStage(i_GameStage);
        gold = _dataInputStream.readInt();
        globalcounter = _dataInputStream.readInt();
        hp = _dataInputStream.readInt();
        MAXHP = _dataInputStream.readInt();
        for(int i=0;i<keys.length;i++)
            keys[i] = _dataInputStream.readInt();
        for(int i=0;i<wear.length;i++)
            wear[i] = _dataInputStream.readInt();
        shiftX = _dataInputStream.readInt();
        shiftY = _dataInputStream.readInt();
        bul_move = _dataInputStream.readBoolean();
        shockcounter = _dataInputStream.readInt();
        guardDELAY = _dataInputStream.readInt();
        beginMove = _dataInputStream.readBoolean();

	LVL  = _dataInputStream.readUnsignedShort();
	STR  = _dataInputStream.readUnsignedShort();
	DEX  = _dataInputStream.readUnsignedShort();
	INT  = _dataInputStream.readUnsignedShort();
	EXP  = _dataInputStream.readUnsignedShort();

        int len = bulgar.getArrayLength();
        int tmp[] = new int[len];

        readIntArray(_dataInputStream,tmp, 0, len);
        bulgar.setMeAsByteArray(tmp);

        len = _dataInputStream.readInt();
        guards = new Guard[len];
        for (int i = 0; i < len; i++)
        {
            guards[i] = new Guard(LIVE_W,LIVE_H,true);
            int zlen = guards[i].getArrayLength();
            tmp = new int[zlen];
            readIntArray(_dataInputStream,tmp, 0, zlen);
            guards[i].setMeAsByteArray(tmp);
        }

        int btmp[][] = new int[level.length][level[0].length];
        try
        {
	 int x,y;
	 while((x=_dataInputStream.readUnsignedByte())!=0xff)
         {
                 y = _dataInputStream.readUnsignedByte();
                 level[y][x] = _dataInputStream.readUnsignedByte();
         }
        } catch(Exception ex){}
        GLOBAL_STATE = GAME_SCREEN;
    }

    public void writeIntArray(DataOutputStream out,int [] bf) throws IOException
    {
        for(int i=0;i<bf.length;i++)
            out.writeInt(bf[i]);
    }

    public void readIntArray(DataInputStream in,int bf[],int pos,int len) throws IOException
    {
        for(int i=0;i<len;i++)
            bf[pos+i] = in.readInt();
    }

    public String getGameID()
    {
        return "unknow";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1000;
    }


}
