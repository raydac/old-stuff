
import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.GameObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.Date;


public class Hockey extends Gamelet
{
    int W = 101;
    int H = 64;
    int vorotaW = 40;
    int vorotaH = 15;
    int vorotaX;
    int vorotaY = 5;

    int repeathits = 0;

    GameObject man1 = new GameObject(15, 15, true);
    GameObject man2 = new GameObject(15, 15, true);
    GameObject goaile = new GameObject(15, 15, true);
    GameObject shajba = new GameObject(5, 5, true);

    int goaileDir = 0; // 0 - left , 1 - right

    int shajbaCounter = 0;
    boolean shajbaMove = false;
    int shajbaPlayer = 0; // 0 man1 , 1 man2
    boolean shajbaMoveToGoaile = false;
    int standbycounter = 0;
    boolean missHit = false;
    boolean missHitAnim = false;

    int globalcounter = 0;

    int score = 0;
    int ascore = 0;

    int tmpDir = GameObject.DIR_STOP;

    long time;
    long pause;
    long tm;
    int x3time = 3;
    int arcadelevel = 0;
    int arcadeLevelBase = 5;

    int gametype;

    Coord Man2ToMan1[];
    Coord Man1ToMan2[];
    Coord ShajbaHitPath[];

    class Coord
    {
        public Coord()
        {
        }

        public Coord(int x, int y)
        {
            X = x;
            Y = y;
        }

        public int X;
        public int Y;
    }

    public Hockey(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        W = _screenWidth;
        H = _screenHeight;
        vorotaX = W / 2 - vorotaW / 2;
        newGameSession(2);
    }

    public void newGameSession(int _level)
    {
        goaileDir = 0; // 0 - left , 1 - right

        shajbaCounter = 0;
        shajbaMove = false;
        shajbaPlayer = 0; // 0 man1 , 1 man2
        shajbaMoveToGoaile = false;
        standbycounter = 0;
        missHit = false;
        missHitAnim = false;

        globalcounter = 0;

        score = 0;
        ascore = 0;

        tmpDir = GameObject.DIR_STOP;

        x3time = 3;
        arcadelevel = 0;
        arcadeLevelBase = 5;

        time = (new Date()).getTime();
	pause = 0;

        gametype = _level;
        if (_level == 0)
            x3time = 3;

        i_PlayerState = PLAYERSTATE_NORMAL;
        man1.setCoord(5, H / 2);
        shajba.setCoord(man1.X() + man1.WIDTH(), man1.Y() + man1.HEIGHT());

        man2.setCoord(W - man2.WIDTH() - 5, H - man2.HEIGHT() - 5);

        shajba.setDirection(GameObject.DIR_STOP);

        goaile.setX((vorotaX + vorotaW / 2) - goaile.WIDTH() / 2);
        goaile.setY(vorotaY + vorotaH - goaile.HEIGHT() + goaile.HEIGHT() / 3);

        score = 0;
        ascore = 0;

        arcadelevel = 0;

        missHitAnim = false;

        createPaths();
    }

    public void nextGameStep(int _playermoveobject){}

    public void nextGameStep(Object _playermoveobject)
    {
      if(pause!=0) return;

        hok_PMR keys = (hok_PMR) _playermoveobject;
        switch (keys.i_Value)
        {
            case hok_PMR.BUTTON_LEFT: // left
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_LEFT);
                }
                break;
            case hok_PMR.BUTTON_RIGHT: // right
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_RIGHT);
                }
                break;
            case hok_PMR.BUTTON_UP: // up
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_UP);
                }
                break;
            case hok_PMR.BUTTON_DOWN: // down
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_DOWN);
                }
                break;
            case hok_PMR.BUTTON_UPLEFT: // left/up
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_UPLEFT);
                }
                break;
            case hok_PMR.BUTTON_UPRIGHT: // right/up
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_UPRIGHT);
                }
                break;
            case hok_PMR.BUTTON_DOWNLEFT: // left/down
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_DOWNLEFT);
                }
                break;
            case hok_PMR.BUTTON_DOWNRIGHT: // right/down
                {
                    if (shajba.getDirection() == GameObject.DIR_STOP && !shajbaMove)
                        shajba.setDirection(GameObject.DIR_DOWNRIGHT);
                }
                break;
            case hok_PMR.BUTTON_PASS: // Fire ;-)
                {
                    if (!shajbaMove)
                        shajbaMove = true;
                }
                break;
        }
        gameAction();
    }

    public void pauseGame()
    {
        pause = (new Date()).getTime();
    }

    public void resumeGame()
    {
       if(pause>0) {
        long t = (new Date()).getTime() - pause;
        time += t;
	pause = 0;
       }
    }

    public int getPlayerScore()
    {
        return (score+ascore)*(gametype+1);
    }

    public void gameAction()
    {
        if (i_PlayerState == PLAYERSTATE_WON || i_PlayerState == PLAYERSTATE_LOST)
            return;

        tm = (new Date()).getTime() - time;
        tm = tm / 1000;

        // x3 30 second game
        if (gametype == 0)
        {
            tm = 30 - tm;
            if (tm <= 0)
            {
                time = (new Date()).getTime();
                x3time--;
                if (x3time == 0)
                    i_PlayerState = PLAYERSTATE_WON;
            }
        }

        // Minute game
        if (gametype == 1)
        {
            tm = 60 - tm;
            if (tm <= 0)
                i_PlayerState = PLAYERSTATE_WON;
        }

        // Arcade game (15 sec for score)
        if (gametype == 2)
        {
            tm = (15 + arcadelevel * 2) - tm;
            if (tm <= 0)
            {
                time = (new Date()).getTime();
                if (score < arcadeLevelBase + arcadelevel)
                    i_PlayerState = PLAYERSTATE_LOST;
                else
                    arcadelevel++;
                score = 0;
            }
        }

        globalcounter++;
        if (globalcounter > 254)
            globalcounter = 0;

        // Roll shajba beetwen players
        if (shajbaMove && shajba.getDirection() == GameObject.DIR_STOP)
        {
            if (shajbaPlayer == 0)
            {
                if (shajbaCounter < Man1ToMan2.length)
                    shajba.setCoord(Man1ToMan2[shajbaCounter].X, Man1ToMan2[shajbaCounter].Y);
                else
                    shajba.setCoord(man2.X() - shajba.WIDTH(), man2.Y() + man2.HEIGHT());
                if (shajbaCounter >= Man1ToMan2.length)
                {
                    shajbaPlayer = 1;
                    repeathits = 0;
                    shajbaCounter = 0;
                    shajbaMove = false;
                }
            }
            else
            {
                if (shajbaCounter < Man2ToMan1.length)
                    shajba.setCoord(Man2ToMan1[shajbaCounter].X, Man2ToMan1[shajbaCounter].Y);
                else
                    shajba.setCoord(man1.X() + man1.WIDTH(), man1.Y() + man1.HEIGHT());
                if (shajbaCounter >= Man2ToMan1.length)
                {
                    shajbaPlayer = 0;
                    repeathits = 0;
                    shajbaCounter = 0;
                    shajbaMove = false;
                }
            }
            shajbaCounter++;
        }

        if (!shajbaMove && !shajbaMoveToGoaile && tmpDir != GameObject.DIR_STOP)
        {
            shajba.setDirection(tmpDir);
            tmpDir = GameObject.DIR_STOP;
        }

        // Hit shajba to goaile
        if (shajba.getDirection() != GameObject.DIR_STOP && !shajbaMove && !shajbaMoveToGoaile)
        {
            Coord c = getXYforDirection(shajba.getDirection());
            repeathits++;
            if (shajbaPlayer == 0)
            {
                ShajbaHitPath = PrepareArray(man1.X() + man1.WIDTH(), man1.Y() + man1.HEIGHT(), c.X, c.Y, 8);
                shajbaCounter = 0;
                shajbaMoveToGoaile = true;
            }
            else
            {
                ShajbaHitPath = PrepareArray(man2.X() - shajba.WIDTH(), man2.Y() + man2.HEIGHT(), c.X, c.Y, 8);
                shajbaCounter = 0;
                shajbaMoveToGoaile = true;
            }

        }

        // Move shajba to goaile
        if (shajba.getDirection() != GameObject.DIR_STOP && shajbaMoveToGoaile)
        {
            if (shajbaCounter < ShajbaHitPath.length)
                shajba.setCoord(ShajbaHitPath[shajbaCounter].X, ShajbaHitPath[shajbaCounter].Y);
            else
            {
                if (standbycounter == 0)
                {
                    missHitAnim = true;
                    checkGoal();
                }
                standbycounter++;
                if (standbycounter > 3)
                {
                    missHitAnim = false;
                    shajbaMoveToGoaile = false;
                    shajbaCounter = 0;
                    shajba.setDirection(GameObject.DIR_STOP);
                    if (shajbaPlayer == 0)
                        shajba.setCoord(man1.X() + man1.WIDTH(), man1.Y() + man1.HEIGHT());
                    else
                        shajba.setCoord(man2.X() - shajba.WIDTH(), man2.Y() + man2.HEIGHT());
                    standbycounter = 0;
                }
            }
            shajbaCounter++;
        }

    }

    public void createPaths()
    {
        int bgX,endX;
        int bgY,endY;
        bgX = man1.X() + man1.WIDTH();
        bgY = man1.Y() + man1.HEIGHT();
        endX = man2.X() - shajba.WIDTH();
        endY = man2.Y() + man2.HEIGHT();
        Man1ToMan2 = PrepareArray(bgX, bgY, endX, endY, 8);

        bgX = man2.X() - shajba.WIDTH();
        bgY = man2.Y() + man2.HEIGHT();
        endX = man1.X() + man1.WIDTH();
        endY = man1.Y() + man1.HEIGHT();
        Man2ToMan1 = PrepareArray(bgX, bgY, endX, endY, 8);
    }

    public void checkGoal()
    {
        int rnd = getRandomInt(100);
        if (rnd < 70 && repeathits < 4)
        {
            score++;
            ascore++;
            missHit = false;
        }
        else
        {
            missHit = true;
        }
    }

    public Coord getXYforDirection(int dir)
    {
        Coord c = new Coord();

        if (dir == GameObject.DIR_UP || dir == GameObject.DIR_UPRIGHT || dir == GameObject.DIR_UPLEFT)
            c.Y = vorotaY;
        else
            if (dir == GameObject.DIR_DOWN || dir == GameObject.DIR_DOWNRIGHT || dir == GameObject.DIR_DOWNLEFT)
                c.Y = vorotaY + vorotaH - shajba.HEIGHT();
            else
                c.Y = vorotaY + vorotaH / 2 - shajba.HEIGHT() / 2;

        if (dir == GameObject.DIR_LEFT || dir == GameObject.DIR_UPLEFT || dir == GameObject.DIR_DOWNLEFT)
            c.X = vorotaX;
        else
            if (dir == GameObject.DIR_RIGHT || dir == GameObject.DIR_UPRIGHT || dir == GameObject.DIR_DOWNRIGHT)
                c.X = vorotaX + vorotaW - shajba.WIDTH();
            else
                c.X = vorotaX + vorotaW / 2 - shajba.WIDTH() / 2;

        return c;
    }

    public String getGameID()
    {
        return "Hok23UI561";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 0;
    }

    Coord[] PrepareArray(int x, int y, int X2, int Y2, int del)
    {
        Coord c[] = new Coord[100];
        int coordlen = 0;
        for (int i = 0; i < c.length; i++)
            c[i] = new Coord();

        int X, Y, T, E, dX, dY, denom, Xinc = 1, Yinc = 1,vertlonger = 0, aux;
        dX = X2 - x;
        dY = Y2 - y;
        if (dX < 0)
        {
            Xinc = -1;
            dX = -dX;
        }
        if (dY < 0)
        {
            Yinc = -1;
            dY = -dY;
        }
        if (dY > dX)
        {
            vertlonger = 1;
            aux = dX;
            dX = dY;
            dY = aux;
        }
        denom = dX << 1;
        T = dY << 1;
        E = -dX;
        X = x;
        Y = y;
        if (vertlonger == 1)
            while (dX-- >= 0)
            {
                c[coordlen].X = X;
                c[coordlen].Y = Y;
                coordlen++;
                if ((E += T) > 0)
                {
                    X += Xinc;
                    E -= denom;
                }
                Y += Yinc;
            }
        else
            while (dX-- >= 0)
            {
                c[coordlen].X = X;
                c[coordlen].Y = Y;
                coordlen++;
                if ((E += T) > 0)
                {
                    Y += Yinc;
                    E -= denom;
                }
                X += Xinc;
            }
        // Prepare return array
        Coord ret[] = new Coord[(coordlen / del) + 1];
        for (int i = 0; i < coordlen; i += del)
            if (i / del < ret.length - 1)
                ret[i / del] = c[i];
        ret[ret.length - 1] = new Coord(X2, Y2);
        return ret;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
//        pause = (new Date()).getTime();

        _dataOutputStream.write(man1.getMeAsByteArray());
        _dataOutputStream.write(man2.getMeAsByteArray());
        _dataOutputStream.write(goaile.getMeAsByteArray());
        _dataOutputStream.write(shajba.getMeAsByteArray());

        _dataOutputStream.writeByte(repeathits);
        _dataOutputStream.writeByte(goaileDir);
        _dataOutputStream.writeByte(shajbaCounter);
        _dataOutputStream.writeBoolean(shajbaMove);
        _dataOutputStream.writeByte(shajbaPlayer);
        _dataOutputStream.writeBoolean(shajbaMoveToGoaile);
        _dataOutputStream.writeByte(standbycounter);
        _dataOutputStream.writeBoolean(missHit);
        _dataOutputStream.writeBoolean(missHitAnim);
        _dataOutputStream.writeByte(globalcounter);
        _dataOutputStream.writeByte(score);
        _dataOutputStream.writeByte(ascore);
        _dataOutputStream.writeByte(tmpDir);
        _dataOutputStream.writeLong(time);
        _dataOutputStream.writeLong(pause);
        _dataOutputStream.writeLong(tm);
        _dataOutputStream.writeByte(x3time);
        _dataOutputStream.writeByte(arcadelevel);
        _dataOutputStream.writeByte(arcadeLevelBase);
        _dataOutputStream.writeByte(gametype);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        int len;

        len = man1.getArrayLength();
        byte tmp[] = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        man1.setMeAsByteArray(tmp);

        len = man2.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        man2.setMeAsByteArray(tmp);

        len = goaile.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        goaile.setMeAsByteArray(tmp);

        len = shajba.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        shajba.setMeAsByteArray(tmp);

        repeathits = _dataInputStream.readByte();
        goaileDir = _dataInputStream.readByte();
        shajbaCounter = _dataInputStream.readByte();
        shajbaMove = _dataInputStream.readBoolean();
        shajbaPlayer = _dataInputStream.readByte();
        shajbaMoveToGoaile = _dataInputStream.readBoolean();
        standbycounter = _dataInputStream.readByte();
        missHit = _dataInputStream.readBoolean();
        missHitAnim = _dataInputStream.readBoolean();
        globalcounter = _dataInputStream.readByte();
        score = _dataInputStream.readByte();
        ascore = _dataInputStream.readByte();
        tmpDir = _dataInputStream.readByte();
        time = _dataInputStream.readLong();
        pause = _dataInputStream.readLong();
        tm = _dataInputStream.readLong();
        x3time = _dataInputStream.readByte();
        arcadelevel = _dataInputStream.readByte();
        arcadeLevelBase = _dataInputStream.readByte();
        gametype = _dataInputStream.readByte();

/*
        long t = (new Date()).getTime() - pause;
        time += t;
*/
    }


}
