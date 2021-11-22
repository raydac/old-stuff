
import com.igormaznitsa.gameapi.*;
import com.itx.mbgame.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class RescueHelicopter extends Gamelet
{
    static class VulcanObject extends GameObject
    {
        int VCOUNTERMAX = 30;

        int vcounter;
        int vtype;  // 0 - left , 1 - right , 2 - vertical
        boolean explosive = false;
        boolean exploionAction = true;

        int radius = 90;
        int xdecr = 2;
        int lastx = 0;

        int speed = 1;

        int phase = 0;

        int aW;
        int aH;

        public int getArrayLength()
        {
            return GameObject.getStaticArrayLength() + 11;
        }

        public int[] getMeAsByteArray()
        {
            int ret[] = new int[11 + GameObject.getStaticArrayLength()];
            ret[0] = (int)vcounter;
            ret[1] = (int)vtype;
            ret[2] = explosive ? (int) 0 : 1;
            ret[3] = exploionAction ? (int) 0 : 1;
            ret[4] = (int)radius;
            ret[5] = (int)xdecr;
            ret[6] = (int)lastx;
            ret[7] = (int)speed;
            ret[8] = (int) phase;
            ret[9] = (int)aW;
            ret[10] = (int)aH;
            System.arraycopy(super.getMeAsByteArray(), 0, ret, 11, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(int[] ret)
        {
            vcounter = ret[0];
            vtype = ret[1];
            explosive = ret[2] == 0 ? true : false;
            exploionAction = ret[3] == 0 ? true : false;
            radius = ret[4];
            xdecr = ret[5];
            lastx = ret[6];
            speed = ret[7];
            phase = ret[8];
            aW = ret[9];
            aH = ret[10];
            int tmp[] = new int[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 11, tmp, 0, GameObject.getStaticArrayLength());
            super.setMeAsByteArray(tmp);
        }

        public void reset()
        {
            vcounter = 0;
            setActiveState(true);
            setSize(aW, aH);
            exploionAction = true;
            for (int ty = 0; ty < traceX.length; ty++)
            {
                traceX[ty] = -1;
                traceY[ty] = -1;
            }
        }

        int traceX[] = new int[4];
        int traceY[] = new int[4];

        public void shiftTraceXY()
        {
            int fix = traceX.length - 1;
            for (int ty = 0; ty < traceX.length - 1; ty++)
            {
                traceX[fix] = traceX[fix - 1];
                traceY[fix] = traceY[fix - 1];
                fix--;
            }
            traceX[0] = X();
            traceY[0] = Y();
        }

        public boolean isExplosiveEnd()
        {
            return vcounter > VCOUNTERMAX / 2;
        }

        public VulcanObject(int w, int h, boolean acv)
        {
            super(w, h, acv);
            aW = w;
            aH = h;
            vcounter = 0;
            for (int i = 0; i < traceX.length; i++)
            {
                traceX[i] = -1;
                traceY[i] = -1;
            }
        }

    }
    // ==============================================================================

    static VulcanObject vulcan[] = new VulcanObject[20];
    static VulcanObject peoples[] = new VulcanObject[20];

    GameObject hel = new GameObject((int) 10, (int) 11, true);

    GameObject flypp[] = new GameObject[2];
    // ==============================================================================

    public static int W = 100;
    public static int H = 100;
    int MAXSPEED = 4;
    public static int HELBASE_WIDTH = 10;
    int MAXLIVES = 3;

    int lives;

    int peoples_left;
    int score;
    boolean STAGE_COMPLETE;

    int leftspeed;
    int rightspeed;
    int upspeed;
    int downspeed;
    int helcounter;

    byte level[]; // 1 level point = 2 screen point
    int screenX;

    boolean helExplosion;
    int helExplosionCounter;
    int helExplosionCounterMax;

    // ==============================================================================

    public RescueHelicopter(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        W = _screenWidth;
        //H = _screenHeight;
        lives = MAXLIVES;
        STAGE_COMPLETE = false;
        newGameSession(2);
    }

    public void initStage(int st)
    {
        i_GameStage = st;
        for (int i = 0; i < vulcan.length; i++)
            vulcan[i] = null;
        for (int i = 0; i < flypp.length; i++)
            flypp[i] = null;
        for (int i = 0; i < peoples.length; i++)
            peoples[i] = null;
        hel.setScreen(W, H);
        hel.setCoord((int) (W - hel.WIDTH() - 3), (int) (H - hel.HEIGHT() - 3));
        hel.setDirection(GameObject.DIR_RIGHT);
        leftspeed = 0;
        rightspeed = 0;
        upspeed = 0;
        downspeed = 0;
        peoples_left = 0;
        helExplosion = false;
        helExplosionCounter = 0;
        helExplosionCounterMax = 5;
        i_PlayerState = PLAYERSTATE_NORMAL;
        STAGE_COMPLETE = false;
        i_GameState = GAMESTATE_PLAYED;
        createLevel();
    }

    public void resetHelicopter()
    {
        leftspeed = 0;
        rightspeed = 0;
        upspeed = 0;
        downspeed = 0;
        helExplosion = false;
        helExplosionCounter = 0;
        i_PlayerState = PLAYERSTATE_NORMAL;
        hel.setScreen(W, H);
        hel.setCoord((int) (W - hel.WIDTH() - 3), (int) (H - hel.HEIGHT() - 3));
        hel.setDirection(GameObject.DIR_RIGHT);
        screenX = (int) (level.length - W / 2); // Do not change!!! 2 screen points on 1 level point !!!

    }

    public void resumeGameAfterPlayerLost()
    {
        resetHelicopter();
    }

    public void newGameSession(int _level)
    {
        score = 0;
        lives = MAXLIVES;
        MAXSPEED = (int) ((_level + 1) * 2);
        initStage( 0);
    }


    public int getPlayerScore()
    {
        i_PlayerScore = score;
        return score;
    }

    public void createLevel()
    {
        level = Stages.getStage(i_GameStage);
        screenX =  (level.length - W / 2); // Do not change!!! 2 screen points on 1 level point !!!
        Stages.initStageObjects(i_GameStage);

        for (int i = 0; i < peoples.length; i++)
            if (peoples[i] != null)
                peoples_left++;
    }

    public int getScreenX(int levelX)
    {
        return (levelX - screenX) * 2;
    }

    public void nextGameStep(int _playermoveobject){}

    public void nextGameStep(Object _playermoveobject)
    {
        if (_playermoveobject != null)
        {
            hel_PMR keys = (hel_PMR) _playermoveobject;
            switch (keys.i_Value)
            {
                case hel_PMR.BUTTON_LEFT:
                    {
                        hel.setDirection(GameObject.DIR_RIGHT);
                        rightspeed = 0;
                        if (leftspeed < MAXSPEED)
                            leftspeed += 2;
                    }
                    break;
                case hel_PMR.BUTTON_RIGHT:
                    {
                        hel.setDirection(GameObject.DIR_LEFT);
                        leftspeed = 0;
                        if (rightspeed < MAXSPEED)
                            rightspeed += 2;
                    }
                    break;
                case hel_PMR.BUTTON_UP:
                    {
                        downspeed = 0;
                        if (upspeed < MAXSPEED)
                            upspeed += 2;
                    }
                    break;
                case hel_PMR.BUTTON_DOWN:
                    {
                        upspeed = 0;
                        if (downspeed < MAXSPEED)
                            downspeed += 2;
                    }
                    break;
                case hel_PMR.BUTTON_ANY:
                    {
                        if (STAGE_COMPLETE)
                        {
                            i_GameStage++;
                            initStage(i_GameStage);
                        }
                        else
                            if (i_PlayerState == PLAYERSTATE_LOST)
                            {
                                if (lives == 0)
                                {
                                    score = 0;
                                    lives = MAXLIVES;
                                    initStage( 0);
                                }
                                else
                                    initStage(i_GameStage);
                            }
                    }
                    break;
            }
        }
        if(_playermoveobject != null)
            gameAction(((hel_PMR) _playermoveobject).i_Value);
        else
            gameAction(hel_PMR.BUTTON_NONE);
    }

    public void gameAction(int key)
    {
        if (i_PlayerState == PLAYERSTATE_WON || STAGE_COMPLETE || i_PlayerState == PLAYERSTATE_LOST) return;

        helcounter++;
        if (helcounter > 255)
            helcounter = 0;
        // Helicopter base collision detection
//        int lx =  (level.length - HELBASE_WIDTH);
        boolean helbase = false;
        if (screenX <= level.length - (HELBASE_WIDTH>>1))
        {
            int sX =  getScreenX(level.length/*lx*/)-HELBASE_WIDTH-1;
            int sY =  (H - 4);
            int sW = HELBASE_WIDTH;
            int sH = 4;
            if (hel.checkCollision(sX, sY, sW, sH))
            {
	        hel.setY(sY-hel.HEIGHT());
                helbase = true;
                for (int j = 0; j < flypp.length; j++)
                {
                    if (flypp[j] != null)
                    {
                        flypp[j] = null;
                        score += 2;
                        peoples_left--;
                        if (peoples_left <= 0)
                        {
                            STAGE_COMPLETE = true;
                            i_PlayerState = PLAYERSTATE_WON;
                            if (i_GameStage == (Stages.NUMBER_OF_LEVELS - 1))
                            {
                                i_PlayerState = PLAYERSTATE_WON;
                                i_GameState = GAMESTATE_OVER;
                            }
                        }

                    }
                }
            }
        }

        // Helicpoter found some sort of people ;)
        for (int i = 0; i < peoples.length; i++)
        {
            if (peoples[i] != null && peoples[i].isActive())
            {
                int sX = getScreenX(peoples[i].aX);
                int sY = peoples[i].aY;
                int sW = peoples[i].WIDTH();
                int sH = peoples[i].HEIGHT();
                if (hel.checkCollision(sX, sY, sW, sH))
                {
                    for (int j = 0; j < flypp.length; j++)
                    {
                        if (flypp[j] == null)
                        {
                            peoples[i].setActiveState(false);
                            flypp[j] = peoples[i];
                            score++;
                            break;
                        }
                    }
                }
            }
        }

        // Move helicopter
        if (!helExplosion)
        {
            if (hel.X() > W / 2 - hel.WIDTH() / 2 && rightspeed > 0 && screenX < level.length - W / 2)
                screenX += 2;
            else
                hel.shiftX(rightspeed);

            if (hel.X() < W / 2 - hel.WIDTH() / 2 && leftspeed > 0 && screenX > 0)
                screenX -= 2;
            else
                hel.shiftX( -leftspeed);

            hel.shiftY( -upspeed);
            if (!helbase)
	      if(hel.checkCollision(getScreenX(level.length)-HELBASE_WIDTH-1, (H - 4)-downspeed+1, HELBASE_WIDTH, 4)){
                hel.shiftY((H-4) - (hel.Y()+hel.HEIGHT()) );
		downspeed = 0;
	      }
	       else
                hel.shiftY(downspeed);

//            if (key == hel_PMR.BUTTON_NONE)
            {
                if (rightspeed > 0)
                    rightspeed--;
                if (leftspeed > 0)
                    leftspeed--;
                if (downspeed > 0)
                    downspeed--;
                if (upspeed > 0)
                    upspeed--;
            }
        }
        // Helicopter crash
        else
            if (i_PlayerState != PLAYERSTATE_LOST)
            {
                helExplosionCounter++;
                if (helExplosionCounter > helExplosionCounterMax)
                {
                    if (lives <= 0)
                    {
                        i_GameState = GAMESTATE_OVER;
                        i_PlayerState = PLAYERSTATE_LOST;
                    }
                    else
                    {
                        i_PlayerState = PLAYERSTATE_LOST;
                        resetHelicopter();
                    }
                }
            }

        // Vulcans
        for (int v = 0; v < vulcan.length; v++)
        {
            // Fire & stones coordinates
            if (vulcan[v] != null && !helExplosion)
            {
                vulcan[v].phase++;
                if (vulcan[v].phase > 4)
                    vulcan[v].phase = 0;
                //  counter for fire and stones
                if (vulcan[v].vcounter < vulcan[v].VCOUNTERMAX)
                    vulcan[v].vcounter += vulcan[v].speed;
                else
                    vulcan[v].reset();
                boolean fl = true;
                if (vulcan[v].explosive && vulcan[v].isExplosiveEnd())
                {
                    vulcan[v].setX( getScreenX(vulcan[v].lastx));
                    if (vulcan[v].WIDTH() < vulcan[v].aW * 3)
                    {
                        int w = vulcan[v].WIDTH();
                        int h = vulcan[v].HEIGHT();
                        w += 3;
                        h += 3;
                        vulcan[v].shiftX( -1);
                        vulcan[v].shiftY( -1);
                        vulcan[v].setSize( w,  h);
                    }
                    else
                        vulcan[v].setCoord( -50,  -50);
                    vulcan[v].exploionAction = true;
                    fl = false;
                }
                if ((vulcan[v].vtype <= 1 || vulcan[v].vtype == 3) && fl)
                {
                    int bx =  getScreenX(vulcan[v].aX);
                    int by = vulcan[v].aY;
                    int radius = vulcan[v].radius;
                    int xc = vulcan[v].xdecr;
                    int angle =  ((vulcan[v].vcounter + 2) & 63);
                    int yy =  (by - xSine(radius, angle));
                    int xx = -1;
                    if (vulcan[v].vtype == 0)
                        xx =  (bx - (radius / xc) + xCoSine((radius / xc), angle));
                    if (vulcan[v].vtype == 1)
                        xx =  (bx + (radius / xc) - xCoSine((radius / xc), angle));
                    vulcan[v].lastx =  (screenX + (xx / 2));
                    vulcan[v].setCoord(xx, yy);
                    vulcan[v].shiftTraceXY();
                }
                else
                    if (vulcan[v].vtype == 2)
                    {
                        vulcan[v].setX( getScreenX(vulcan[v].aX));
                        vulcan[v].setY( (vulcan[v].aY - vulcan[v].vcounter));
                    }
                // Detect collisions
                if (hel.checkCollision(vulcan[v]) && !helExplosion)
                {
                    lives--;
                    helExplosion = true;
                }
            }
        }

        // Detect deadly collisions
        collisionDetection();
    }

    public void collisionDetection()
    {
        // Level collision
        for (int i = hel.X(); i < hel.X() + hel.WIDTH(); i += 2)
        {
            int sX = i;
            int sY = 0;
            try
            {
                sY =  (H - level[screenX + i / 2]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            int sW = 2;
            int sH = level[screenX + i / 2];

            if (hel.checkCollision(sX, sY, sW, sH) && !helExplosion)
            {
                lives--;
                helExplosion = true;
            }
        }
    }

    // ####################################################################################
    // ####################################################################################

    public void writeToStream(DataOutputStream out) throws IOException
    {
        out.writeInt(i_GameStage);
        out.writeInt(lives);
        out.writeInt(peoples_left);
        out.writeInt(score);
        out.writeBoolean(STAGE_COMPLETE);
        out.writeInt(leftspeed);
        out.writeInt(rightspeed);
        out.writeInt(upspeed);
        out.writeInt(downspeed);
        out.writeInt(screenX);
        out.writeBoolean(helExplosion);
        out.writeInt(helExplosionCounter);
        out.writeInt(helExplosionCounterMax);

        int ln;

        ln = arrayLength(vulcan);
        out.writeInt(ln);
        for (int i = 0; i < ln; i++){
            writeIntArray(out,vulcan[i].getMeAsByteArray());
	    out.writeInt(vulcan[i].VCOUNTERMAX);
        }

        ln = arrayLength(peoples);
        out.writeInt(ln);
        for (int i = 0; i < ln; i++)
            writeIntArray(out,peoples[i].getMeAsByteArray());

        writeIntArray(out,hel.getMeAsByteArray());

        ln = arrayLength(flypp);
        out.writeInt(ln);
        for (int i = 0; i < ln; i++)
            writeIntArray(out,flypp[i].getMeAsByteArray());

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

    public int arrayLength(Object obj[])
    {
        int ln = 0;
        for (int i = 0; i < obj.length; i++)
            if (obj[i] != null)
                ln++;
        return ln;
    }

    public void readFromStream(DataInputStream in) throws IOException
    {
        i_GameStage = in.readInt();
        initStage(i_GameStage);
        lives = in.readInt();
        peoples_left = in.readInt();
        score = in.readInt();
        STAGE_COMPLETE = in.readBoolean();
        leftspeed = in.readInt();
        rightspeed = in.readInt();
        upspeed = in.readInt();
        downspeed = in.readInt();
        screenX = in.readInt();
        helExplosion = in.readBoolean();
        helExplosionCounter = in.readInt();
        helExplosionCounterMax = in.readInt();

        int ln;

        ln = in.readInt();
        for (int i = 0; i < ln; i++)
        {
            vulcan[i] = new VulcanObject((int) 0, (int) 0, true);
            int aln = vulcan[i].getArrayLength();
            int tmp[] = new int[aln];
            readIntArray(in,tmp, 0, aln);
            vulcan[i].setMeAsByteArray(tmp);
	    vulcan[i].VCOUNTERMAX = in.readInt();
        }

        ln = in.readInt();
        for (int i = 0; i < ln; i++)
        {
            peoples[i] = new VulcanObject((int) 0, (int) 0, true);
            int aln = peoples[i].getArrayLength();
            int tmp[] = new int[aln];
            readIntArray(in,tmp, 0, aln);
            peoples[i].setMeAsByteArray(tmp);
        }

        int aln = hel.getArrayLength();
        int tmp[] = new int[aln];
        readIntArray(in,tmp, 0, aln);
        hel.setMeAsByteArray(tmp);

        ln = in.readInt();
        for (int i = 0; i < ln; i++)
        {
            flypp[i] = new GameObject(0, 0, true);
            aln = flypp[i].getArrayLength();
            tmp = new int[aln];
            readIntArray(in,tmp, 0, aln);
            flypp[i].setMeAsByteArray(tmp);
        }
    }

    public String getGameID()
    {
        return "ResHell793";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 13 + (21 * 43) + 3; // 929
    }

}
