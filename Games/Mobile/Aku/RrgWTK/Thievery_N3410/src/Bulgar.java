
import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.GameObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class Bulgar extends Gamelet
{
    public static final int ACTION_BUTTONPUSH = 0;
    public static final int ACTION_DOOR_BUTTON_OPEN = 1;
    public static final int ACTION_DOOR_KEY_OPENEED = 2;
    public static final int ACTION_KEYFOUNDED = 3;
    public static final int ACTION_TREASUREFOUNDED = 4;
    // -------------------
    GameActionListener _gameActionListener;

    Stages stages = new Stages();

    int W = 100;
    int H = 100;
    int quadWH = 12; // Quads width/height
    int MAX_TREASURES = 5;
    int MAXLIVES = 3;
    int LIVE_W = quadWH , LIVE_H = quadWH;
    int max_guardDELAY = 1;
    int MAX_shockcounter = 60;
    // -------------------
    int level[][];

    // -------------------
    int gold = 0;

    int globalcounter = 0;

    int exitX,exitY;
    int beginX,beginY;

    int treasure_counter = 0;

    int lives = MAXLIVES;

    int keys[] = new int[3];

    int shiftX = 0 , shiftY = 0;
    boolean bul_move = false;

    int shockcounter = MAX_shockcounter;

    int guardDELAY = 0;

    boolean beginMove = false;
    int tmpX,tmpY;

    GameObject bulgar = new GameObject(LIVE_W, LIVE_H, true);

    class Guard extends GameObject
    {
        int ID;
        int defaultDir; // 0 - horizontal , 1 - vertical

        public Guard(int w, int h, boolean acv)
        {
            super(w, h, acv);
        }

        public int getArrayLength()
        {
            return GameObject.getStaticArrayLength()+2;
        }

        public byte[] getMeAsByteArray()
        {
            byte ret[] = new byte[2 + GameObject.getStaticArrayLength()];
            ret[0] = (byte) ID;
            ret[1] = (byte) defaultDir;
            System.arraycopy(super.getMeAsByteArray(), 0, ret, 2, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(byte[] ret)
        {
            ID = ret[0];
            defaultDir = ret[1];
            byte tmp[] = new byte[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 2, tmp, 0, GameObject.getStaticArrayLength());
            super.setMeAsByteArray(tmp);
        }

    }

    Guard guards[];

    public Bulgar(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
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
        lives = MAXLIVES;
        gold = 0;
        //initStage(0);
    }

    public void initStage(int _stage)
    {
        super.initStage(_stage);
        level = stages.getStage(_stage);
        int c[] = stages.getCoords(_stage);
        shockcounter = MAX_shockcounter;
        treasure_counter = 0;
        beginX = c[0];
        beginY = c[1];
        exitX = c[2];
        exitY = c[3];
        guardDELAY = 0;
        bulgar.setDirection(GameObject.DIR_STOP);
        tmpX = 0;
        tmpX = 0;
        bulgar.aX = 0;
        bulgar.aY = 0;
        shiftX = 0;
        shiftY = 0;
        beginMove = false;
        bulgar.setCoord(beginX, beginY);
        for (int y = 0; y < keys.length; y++)
            keys[y] = 0;

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


    public void nextGameStep(Object _playermoveobject)
    {
        game_PMR keys = (game_PMR) _playermoveobject;

        if (bulgar.getDirection() == GameObject.DIR_STOP && i_PlayerState == PLAYERSTATE_NORMAL)
        {
            switch (keys.i_Value)
            {
                case game_PMR.BUTTON_LEFT:
                    {
                        bulgar.setDirection(GameObject.DIR_LEFT);
                    }
                    break;
                case game_PMR.BUTTON_RIGHT:
                    {
                        bulgar.setDirection(GameObject.DIR_RIGHT);
                    }
                    break;
                case game_PMR.BUTTON_UP:
                    {
                        bulgar.setDirection(GameObject.DIR_UP);
                    }
                    break;
                case game_PMR.BUTTON_DOWN:
                    {
                        bulgar.setDirection(GameObject.DIR_DOWN);
                    }
                    break;
            }
        }
        gameAction();
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        bulgar.setCoord(beginX, beginY);
        tmpX = 0;
        tmpY = 0;
        bulgar.setDirection(GameObject.DIR_STOP);
        initGuards();
    }

    public void gameAction()
    {
        globalcounter++;
        if(globalcounter > 255)
            globalcounter = 0;
        if (i_PlayerState != PLAYERSTATE_NORMAL)
            return;
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
            tmpX = 0;
            tmpY = 0;
            shiftY = 0;
            shiftX = 0;
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
        if (beginMove)
        {
            tmpX += shiftX*2;
            tmpY += shiftY*2;
            if (Math.abs(tmpX) >= quadWH || Math.abs(tmpY) >= quadWH)
            {
                bulgar.setCoord(bulgar.X() + shiftX, bulgar.Y() + shiftY);
                bulgar.vDir = bulgar.getDirection();
                bulgar.setDirection(GameObject.DIR_STOP);
                beginMove = false;
                tmpX = 0;
                tmpY = 0;
            }
        }
        // Exit
        if(treasure_counter >= MAX_TREASURES)
        {
            if(bulgar.X() == exitX && bulgar.Y() == exitY)
            {
                i_PlayerState = Gamelet.PLAYERSTATE_WON;
                if(i_GameStage >= Stages.TOTAL_STAGES)
                    i_GameState = Gamelet.GAMESTATE_OVER;
            }
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
                _gameActionListener.gameAction(ACTION_BUTTONPUSH, bulgar.X(), bulgar.Y());

            for (int y = 0; y < level.length; y++)
                for (int x = 0; x < level[0].length; x++)
                {
                    if (level[y][x] >= 10 && level[y][x] <= 29)
                    {
                        if (level[y][x] - 10 == qd - 70)
                        {
                            if (level[y][x] < 20)
                                level[y][x] = 91;
                            else
                                level[y][x] = 92;
                            if (_gameActionListener != null)
                                _gameActionListener.gameAction(ACTION_DOOR_BUTTON_OPEN, x, y);
                        }
                    }
                }

        }
        // Keys
        if (qd >= 50 && qd <= 69)
        {
            for (int y = 0; y < keys.length; y++)
                if (keys[y] == 0)
                {
                    keys[y] = qd;
                    level[bulgar.Y()][bulgar.X()] = 0;
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
                        for (int i = 0; i < keys.length; i++)
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
            level[bulgar.Y()][bulgar.X()] = 0;
            gold += ((qd - 100) + 1) * 3;
            i_PlayerScore = gold;
            treasure_counter++;
        }
        // Guards
        if (guardDELAY == 0)
        {
            for (int i = 0; i < guards.length; i++)
            {
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
                    diePlayer();
                }
                if (shx <= 3 && shy <= 3)
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
                    guards[i].aY--;
                    if (Math.abs(guards[i].aY) > quadWH)
                    {
                        guards[i].vDir = GameObject.DIR_UP;
                        guards[i].aY = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftY(-1);
                        level[gy][gx] = guard_num;
                    }
                }
                if (guards[i].getDirection() == GameObject.DIR_DOWN && (level[gy + 1][gx] < 1 || level[gy + 1][gx] > 29))
                {
                    guards[i].aY++;
                    if (guards[i].aY > quadWH)
                    {
                        guards[i].vDir = GameObject.DIR_DOWN;
                        guards[i].aY = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftY(1);
                        level[gy][gx] = guard_num;
                    }
                }
                if (guards[i].getDirection() == GameObject.DIR_LEFT && (level[gy][gx - 1] < 1 || level[gy][gx - 1] > 29))
                {
                    guards[i].aX--;
                    if (Math.abs(guards[i].aX) > quadWH)
                    {
                        guards[i].vDir = GameObject.DIR_LEFT;
                        guards[i].aX = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftX(-1);
                        level[gy][gx] = guard_num;
                    }
                }
                if (guards[i].getDirection() == GameObject.DIR_RIGHT && (level[gy][gx + 1] < 1 || level[gy][gx + 1] > 29))
                {
                    guards[i].aX++;
                    if (guards[i].aX > quadWH)
                    {
                        guards[i].vDir = GameObject.DIR_RIGHT;
                        guards[i].aX = 0;
                        level[gy][gx] = 0;
                        guards[i].shiftX(1);
                        level[gy][gx] = guard_num;
                    }
                }
            }
        }
        guardDELAY--;
        if (guardDELAY < 0)
            guardDELAY = max_guardDELAY;
    }

    public void diePlayer()
    {
        i_PlayerState = PLAYERSTATE_LOST;
        bulgar.setDirection(GameObject.DIR_STOP);
        tmpX = 0;
        tmpX = 0;
        bulgar.aX = 0;
        bulgar.aY = 0;
        shiftX = 0;
        shiftY = 0;
        beginMove = false;
        lives--;
        if (lives <= 0)
            this.i_GameState = Gamelet.GAMESTATE_OVER;
    }

    public boolean checkDirection(int x, int y, int dir)
    {
        int sx = 0,sy = 0;
        if (bulgar.getDirection() == GameObject.DIR_LEFT)
            sx = -1;
        if (bulgar.getDirection() == GameObject.DIR_RIGHT)
            sx = 1;
        if (bulgar.getDirection() == GameObject.DIR_UP)
            sy = -1;
        if (bulgar.getDirection() == GameObject.DIR_DOWN)
            sy = 1;
        if (sx + x >= 0 && sy + y >= 0 && sx + x < level[0].length && sy + y < level.length)
        {
            if (level[sy + y][sx + x] >= 1 && level[sy + y][sx + x] <= 29)
                return false;
        }
        else
            return false;
        return true;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeInt(i_GameStage);
        _dataOutputStream.writeInt(gold);
        _dataOutputStream.writeInt(globalcounter);
        _dataOutputStream.writeInt(treasure_counter);
        _dataOutputStream.writeInt(lives);
        for(int i=0;i<keys.length;i++)
            _dataOutputStream.writeInt(keys[i]);
        _dataOutputStream.writeInt(shiftX);
        _dataOutputStream.writeInt(shiftY);
        _dataOutputStream.writeBoolean(bul_move);
        _dataOutputStream.writeInt(shockcounter);
        _dataOutputStream.writeInt(guardDELAY);
        _dataOutputStream.writeBoolean(beginMove);
        _dataOutputStream.writeInt(tmpX);
        _dataOutputStream.writeInt(tmpY);
        _dataOutputStream.write(bulgar.getMeAsByteArray());

        _dataOutputStream.writeInt(guards.length);
        for (int i = 0; i < guards.length; i++)
            _dataOutputStream.write(guards[i].getMeAsByteArray());

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
         _dataOutputStream.writeByte(0x0);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_GameStage = _dataInputStream.readInt();
        initStage(i_GameStage);
        gold = _dataInputStream.readInt();
        globalcounter = _dataInputStream.readInt();
        treasure_counter = _dataInputStream.readInt();
        lives = _dataInputStream.readInt();
        for(int i=0;i<keys.length;i++)
            keys[i] = _dataInputStream.readInt();
        shiftX = _dataInputStream.readInt();
        shiftY = _dataInputStream.readInt();
        bul_move = _dataInputStream.readBoolean();
        shockcounter = _dataInputStream.readInt();
        guardDELAY = _dataInputStream.readInt();
        beginMove = _dataInputStream.readBoolean();
        tmpX = _dataInputStream.readInt();
        tmpY = _dataInputStream.readInt();

        int len = bulgar.getArrayLength();
        byte tmp[] = new byte[len];
        _dataInputStream.read(tmp,0,len);
        bulgar.setMeAsByteArray(tmp);

        len = _dataInputStream.readInt();
        guards = new Guard[len];
        for (int i = 0; i < len; i++)
        {
            guards[i] = new Guard(LIVE_W,LIVE_H,true);
            int zlen = guards[i].getArrayLength();
            tmp = new byte[zlen];
            _dataInputStream.read(tmp,0,zlen);
            guards[i].setMeAsByteArray(tmp);
        }
        try
        {
	  int x,y;
	  while((x = _dataInputStream.readUnsignedByte())!=0x0){
                 y = _dataInputStream.readUnsignedByte();
                 level[y][x] = _dataInputStream.readUnsignedByte();
	  }
        } catch(Exception ex){}
    }

    public String getGameID()
    {
        return "unknow";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 0;
    }


}
