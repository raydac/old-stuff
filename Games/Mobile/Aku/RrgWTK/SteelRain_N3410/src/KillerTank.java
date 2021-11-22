
import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class KillerTank extends Gamelet
{
    int W = 101;
    int H = 64;
    int LINEOFFIRE;
    int MAX_PCOUNTER = 60;
    int hobotRadius = 10;

    int MAX_HOBOTRADIUS = H;
    int MIN_HOBOTRADIUS = H/4;

    class Coord
    {
        public int X;
        public int Y;
    }

    class EnemyObject extends GameObject
    {
        byte type;
        boolean fire = false;
        int fbX;
        int fbY;

        public EnemyObject(byte w, byte h, boolean actv)
        {
            super(w, h, actv);
        }

        public int getArrayLength()
        {
            return GameObject.getStaticArrayLength()+4;
        }

        public byte[] getMeAsByteArray()
        {
            byte ret[] = new byte[4 + GameObject.getStaticArrayLength()];
            ret[0] = type;
            if (fire)
                ret[1] = 0;
            else
                ret[1] = 1;
            ret[2] = (byte) fbX;
            ret[3] = (byte) fbY;
            System.arraycopy(super.getMeAsByteArray(), 0, ret, 4, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(byte[] ret)
        {
            type = ret[0];
            if (ret[1] == 0)
                fire = true;
            else
                fire = false;
            fbX = ret[2];
            fbY = ret[3];
            byte tmp[] = new byte[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 4, tmp, 0, GameObject.getStaticArrayLength());
            super.setMeAsByteArray(tmp);
        }
    }

    class FirePoint
    {
        boolean fire = false;
        int fireRadius = targetRadius;
        int fireRadiusCounter = hobotRadius;
        int fireAngle = 14;
        GameObject explosion = new GameObject((byte) 10, (byte) 10, false);
        int explosionCounter = 0;

        public int getArrayLength()
        {
            return 16;
        }

        public byte[] getMeAsByteArray()
        {
            byte ret[] = new byte[5 + GameObject.getStaticArrayLength()];
            if (fire)
                ret[0] = 0;
            else
                ret[0] = 1;
            ret[1] = (byte) fireRadius;
            ret[2] = (byte) fireRadiusCounter;
            ret[3] = (byte) fireAngle;
            ret[4] = (byte) explosionCounter;
            System.arraycopy(explosion.getMeAsByteArray(), 0, ret, 5, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(byte[] ret)
        {
            if (ret[0] == 0)
                fire = true;
            else
                fire = false;
            fireRadius = ret[1];
            fireRadiusCounter = ret[2];
            fireAngle = ret[3];
            explosionCounter = ret[4];
            byte tmp[] = new byte[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 5, tmp, 0, GameObject.getStaticArrayLength());
            explosion.setMeAsByteArray(tmp);
        }
    }

    GameObject tank = new GameObject((byte) 20, (byte) 10, true);

    GameObject aircraftObj = new GameObject((byte) 10, (byte) 10, false);

    GameObject healPresent = new GameObject((byte) 4, (byte) 4, false);
    GameObject shieldPresent = new GameObject((byte) 4, (byte) 4, false);
    int presentCounter = 0;

    EnemyObject enemy[] = new EnemyObject[6];

    FirePoint fp[] = new FirePoint[3];

    int hobotAngle = 14;
    int hobotX = W / 2;
    int hobotY = H - tank.HEIGHT() / 2;

    boolean shield = false;
    int shieldCounter = 0;
    int MAX_SCOUNTER = 300;

    int targetRadius = 40;

    int MAXLIVES = 50;
    int lives = MAXLIVES;

    int MAXEXPLOSION_COUNTER = 15;
    int FIRESPEED = 3;

    int globalcounter = 0;

    int dlevel = 0;

    public KillerTank(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        W = _screenWidth;
        H = _screenHeight;
        hobotX = W / 2;
        hobotY = H - tank.HEIGHT() / 2;
        LINEOFFIRE = H / 2;
        MAX_HOBOTRADIUS = H;
        MIN_HOBOTRADIUS = H/4;
        newGameSession(0);
    }

    public void setDLeve(int level)
    {
        switch (level)
        {
            case 0:
                dlevel = level;
                break;
            case 1:
                dlevel = level;
                break;
            case 2:
                dlevel = level;
                break;
            case 3:
                dlevel = level;
                break;
        }
    }

    public void initStage(int _stage)
    {

    }

    public void newGameSession(int _level)
    {
        lives = MAXLIVES;
        shield = false;
        shieldCounter = 0;
        globalcounter = 0;
	hobotAngle = 14;
	targetRadius = 40;

        healPresent.setActiveState(false);
        shieldPresent.setActiveState(false);
        aircraftObj.setActiveState(false);

        i_PlayerScore = 0;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        for (int i = 0; i < fp.length; i++)
            fp[i] = new FirePoint();
        for (int i = 0; i < enemy.length; i++)
            enemy[i] = new EnemyObject((byte) 10, (byte) 10, false);
            //19x17 for 7210
        setDLeve(0);

    }

    public void nextGameStep(int _playermoveobject)
    {
    }

    public void nextGameStep(Object _playermoveobject)
    {
        KT_PMR keys = (KT_PMR) _playermoveobject;
        switch (keys.i_Value)
        {
            case KT_PMR.BUTTON_LEFT:
                {
                    if (hobotAngle < 30)
                    {
                        hobotAngle++;
                    }
                }
                break;
            case KT_PMR.BUTTON_RIGHT:
                {
                    if (hobotAngle > -2)
                    {
                        hobotAngle--;
                    }
                }
                break;
            case KT_PMR.BUTTON_UP:
                {
                    if (targetRadius < MAX_HOBOTRADIUS)
                    {
                        targetRadius += 3;
                    }
                }
                break;
            case KT_PMR.BUTTON_DOWN:
                {
                    if (targetRadius > MIN_HOBOTRADIUS)
                    {
                        targetRadius -= 3;
                    }
                }
                break;
            case KT_PMR.BUTTON_FIRE:
                {
                    for (int i = 0; i < fp.length; i++)
                    {
                        if (!fp[i].fire)
                        {
                            fp[i].fire = true;
                            fp[i].fireAngle = hobotAngle;
                            fp[i].fireRadius = targetRadius;
                            fp[i].fireRadiusCounter = hobotRadius;
                            break;
                        }
                    }
                }
                break;
        }
        gameAction();
    }

    public void gameAction()
    {
        // Process global counter
        globalcounter++;
        if (globalcounter > 255)
            globalcounter = 0;

        if (i_GameState == GAMESTATE_OVER) return;

        // Tank fire
        for (int i = 0; i < fp.length; i++)
        {
            if (fp[i].fire)
            {
                int rd = fp[i].fireRadius;
                if (fp[i].fireRadiusCounter < rd)
                {
                    if (!fp[i].explosion.isActive())
                        fp[i].fireRadiusCounter += FIRESPEED;
                }
                else
                {
                    fp[i].explosion.setActiveState(true);
                    if (fp[i].explosionCounter >= MAXEXPLOSION_COUNTER)
                        fp[i].explosion.setActiveState(false);
                    fp[i].explosionCounter++;
                    if (!fp[i].explosion.isActive())
                    {
                        fp[i].explosion.setActiveState(false);
                        fp[i].fire = false;
                        fp[i].fireRadiusCounter = hobotRadius;
                        fp[i].explosionCounter = 0;
                    }
                }
            }
            else
                fp[i].explosion.setActiveState(false); // bugfix ;-(
        }
        // Present
        presentCounter++;
        if (presentCounter > MAX_PCOUNTER)
        {
            presentCounter = 0;
            if (getRandomInt(3) == 1 && !aircraftObj.isActive())
            {
                int hx = (10 + getRandomInt(W - 10));
                int hy = -aircraftObj.WIDTH();
                aircraftObj.setCoord(hx, hy);
                aircraftObj.setActiveState(true);
            }
        }
        if (aircraftObj.isActive())
        {
            if (aircraftObj.Y() < H + aircraftObj.WIDTH())
            {
                aircraftObj.shiftY((byte) 1);
                if (!healPresent.isActive() && !shieldPresent.isActive() && aircraftObj.Y() <= LINEOFFIRE && aircraftObj.Y() > 4)
                {
                    presentAction(aircraftObj.X(), aircraftObj.Y());
                }
            }
            else
                aircraftObj.setActiveState(false);
        }

        for (int i = 0; i < fp.length; i++)
        {
            if (fp[i].explosion.isActive())
            {
                if (healPresent.isActive())
                {
                    if (fp[i].explosion.checkCollision(healPresent))
                    {
                        healPresent.setActiveState(false);
                        lives += MAXLIVES / 3;
                        if (lives > MAXLIVES)
                            lives = MAXLIVES;
                        i_PlayerScore+=50;
                    }
                }
                if (shieldPresent.isActive())
                {
                    if (fp[i].explosion.checkCollision(shieldPresent))
                    {
                        shield = true;
                        shieldPresent.setActiveState(false);
                        i_PlayerScore+=50;
                    }
                }
            }
        }
        // Shield
        if (shield)
        {
            shieldCounter++;
            if (shieldCounter > MAX_SCOUNTER)
            {
                shieldCounter = 0;
                shield = false;
            }
        }

        // Enemies
        for (int i = 0; i < enemy.length; i++)
                //for (int i = 0; i < 2; i++)
        {
            if (!enemy[i].isActive())
            {
                if (getRandomInt(128) == 10)
                {
                    // Generate new enemy
                    byte hx = (byte) findX(i);
                    byte hy = (byte) -enemy[i].HEIGHT();
                    enemy[i].setX(hx);
                    enemy[i].setY(hy);
                    enemy[i].type = (byte) getRandomInt(2);
                    enemy[i].setActiveState(true);
                }
            }
            else
            {
                // Move enemy
                if (enemy[i].Y() < LINEOFFIRE)
                {
                    if (globalcounter % 2 == 0)
                        enemy[i].shiftY((byte) 1);
                    // Enemy fire
                    if (globalcounter % 4 == 0 && getRandomInt(200) == 1 && !enemy[i].fire && enemy[i].Y() > 0)
                    {
                        enemy[i].fire = true;
                        enemy[i].fbX = enemy[i].aX = (byte) enemy[i].X();
                        enemy[i].fbY = enemy[i].aY = (byte) enemy[i].Y();
                    }
                }

                // enemy fire
                if (enemy[i].Y() >= LINEOFFIRE && !enemy[i].fire)
                {
                    enemy[i].fire = true;
                    enemy[i].fbX = enemy[i].aX = (byte) enemy[i].X();
                    enemy[i].fbY = enemy[i].aY = (byte) enemy[i].Y();
                }

                // check our fire
                for (int j = 0; j < fp.length; j++)
                {
                    if (fp[j].explosion.isActive())
                    {
                        if (fp[j].explosion.checkCollision(enemy[i]))
                        {
			    i_PlayerScore += (enemy[i].type+1)*5;
                            enemy[i].setActiveState(false);
                        }
                    }
                }
            }

            // process enemy fire
            if (enemy[i].fire)
            {
                Coord c = DrawLine(enemy[i].aX, enemy[i].aY, W / 2, H);
                enemy[i].aX = (byte) c.X;
                enemy[i].aY = (byte) c.Y;
                if (enemy[i].aY >= (H - 3))
                {
                    enemy[i].fire = false;
                    int mhp = 6;
                    if (shield) mhp = 2;
                    lives -= mhp;
                    if (lives < 0)
                    {
                        lives = 0;
                        i_PlayerState = PLAYERSTATE_LOST;
                        i_GameState = GAMESTATE_OVER;
                    }
                }
            }
        }
    }

    Coord DrawLine(int x, int y, int X2, int Y2)
    {
        int T, E, dX, dY, denom, Xinc = 1, Yinc = 1,vertlonger = 0, aux;
        Coord c = new Coord();

        // DRAW
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
        c.X = x;
        c.Y = y;
        if (vertlonger == 1)
        {
            if (dX-- >= 0)
            {
                if ((E += T) > 0)
                    c.X += Xinc;
                c.Y += Yinc;
            }
        }
        else
        {
            if (dX-- >= 0)
            {
                if ((E += T) > 0)
                    c.Y += Yinc;
                c.X += Xinc;
            }
        }
        return c;
    }

    public void presentAction(int hx, int hy)
    {
        if (getRandomInt(50) == 1 && !healPresent.isActive())
        {
            healPresent.setCoord(hx, hy);
            healPresent.setActiveState(true);
        }
        if (getRandomInt(50) == 1 && !shieldPresent.isActive())
        {
            shieldPresent.setCoord(hx, hy);
            shieldPresent.setActiveState(true);
        }
    }

    public int findX(int eidx)
    {
        byte hx = (byte) (2 + getRandomInt(W - enemy[eidx].HEIGHT()));
        return hx;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.write(tank.getMeAsByteArray());

        _dataOutputStream.write(aircraftObj.getMeAsByteArray());

        _dataOutputStream.write(healPresent.getMeAsByteArray());

        _dataOutputStream.write(shieldPresent.getMeAsByteArray());

        for (int i = 0; i < enemy.length; i++)
            _dataOutputStream.write(enemy[i].getMeAsByteArray());

        for (int i = 0; i < fp.length; i++)
            _dataOutputStream.write(fp[i].getMeAsByteArray());

        _dataOutputStream.writeInt(presentCounter);
        _dataOutputStream.writeInt(hobotAngle);
        _dataOutputStream.writeInt(hobotX);
        _dataOutputStream.writeInt(hobotY);
        _dataOutputStream.writeBoolean(shield);
        _dataOutputStream.writeInt(shieldCounter);
        _dataOutputStream.writeInt(targetRadius);
        _dataOutputStream.writeInt(lives);
        _dataOutputStream.writeInt(globalcounter);
        _dataOutputStream.writeInt(dlevel);
        _dataOutputStream.writeInt(i_PlayerScore);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        int len;

        len = tank.getArrayLength();
        byte tmp[] = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        tank.setMeAsByteArray(tmp);

        len = aircraftObj.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        aircraftObj.setMeAsByteArray(tmp);

        len = healPresent.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        healPresent.setMeAsByteArray(tmp);

        len = shieldPresent.getArrayLength();
        tmp = new byte[len];
        _dataInputStream.read(tmp, 0, len);
        shieldPresent.setMeAsByteArray(tmp);

        for (int i = 0; i < enemy.length; i++)
        {
            len = enemy[i].getArrayLength();
            tmp = new byte[len];
            _dataInputStream.read(tmp,0,len);
            enemy[i].setMeAsByteArray(tmp);
        }

        for (int i = 0; i < fp.length; i++)
        {
            len = fp[i].getArrayLength();
            tmp = new byte[len];
            _dataInputStream.read(tmp,0,len);
            fp[i].setMeAsByteArray(tmp);
        }

        presentCounter = _dataInputStream.readInt();
        hobotAngle = _dataInputStream.readInt();
        hobotX = _dataInputStream.readInt();
        hobotY = _dataInputStream.readInt();
        shield = _dataInputStream.readBoolean();
        shieldCounter = _dataInputStream.readInt();
        targetRadius = _dataInputStream.readInt();
        lives = _dataInputStream.readInt();
        globalcounter = _dataInputStream.readInt();
        dlevel = _dataInputStream.readInt();
        i_PlayerScore = _dataInputStream.readInt();

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
