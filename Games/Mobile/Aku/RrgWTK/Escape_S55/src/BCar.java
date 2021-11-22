
import com.igormaznitsa.gameapi.Gamelet;
import com.igormaznitsa.gameapi.GameActionListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class BCar extends Gamelet
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;

    private final static int MAX_PLAYER_SPEED = 0x400; // ,8 bf
    private final static int START_PLAYER_SPEED = MAX_PLAYER_SPEED; // ,8 bf
    private final static int MAX_OFFROAD_BRAKES = 0x08;  // ,8 bf
    private final static int MAX_CAR_HIT = 0x14;  // ,8 bf
    private final static int CAR_ACCELERATE_STEP = 0x08; // ,8 bf

    public final static int SPEED_MULTIPLIER = MAX_PLAYER_SPEED / 60;
    private final static int POLICE_RADAR_ALARM_SPEED = 45 * SPEED_MULTIPLIER;

    public final static int OBJECT_MINE = 0;
    public final static int OBJECT_BOMB = 1;
    public final static int OBJECT_TREE = 2;

    public final static int ROAD_BORDER_LEFT = -10;
    public final static int ROAD_BORDER_RIGHT = 110;

    public final static int MINE_ROADWIDTH = 2;
    public final static int MINE_HELWIDTH = 7;

    public final static int carexplosioncounterMAX = 25;


    int police_state;                           // To save it is not necessary, is updated dynamically
    int car_state;                              // To save it is not necessary, is updated dynamically
    boolean carexplosion;
    int carexplosioncounter;


    int miles_counter;

//  public final static

    int W = 101;
    int H = 64;
    int ROADWIDTH = W / 2;
    int CARWIDTH = 16;
    int CARHEIGHT = 10;
    int CARYDECR = 10;
    int DELAY = 100;
    int i_TimeDelay = 120;
    int BRCOUNTERMAX = DELAY;
    int policecarBeginY = H - CARHEIGHT - 2;
    int helicopterWIDTH = 10;
    int helicopterHEIGHT = 10;

    int roadLeftX;
    int cardir;
    int CARX;
    int CARY;
    int carspeed;
    int curspeed_accumulator;
    int diradd;
    int curroaddir;
    boolean brokeroad;
    int addTobrokeroad;
    int brcounter;

    int road[];
    int roaditems[][];

    int policecardowncounter;
    int policecar_begindelay = 0;
    boolean policecar;
    int policecarX;
    int policecarY;

    boolean frontcar;
    int frontcarX;
    int frontcarY;
    int frontcarBeginY;

    boolean helicopter;
    int helicopterX;
    int helicopterY;
    int helicopterDelay;
    int helicopterLiveCounter;
    boolean helicopterUp;
    boolean helicopterLeft;
    boolean helicopterFire;
    boolean helicopterFire2;
    boolean helMineFire;
    int helicopterFireX;
    int helicopterFireY;
    int helicopterFireCounter;

    boolean policecarwin;
    boolean policecaught;
    boolean playerwin = false;

    int stage = 0;
    int stagecounter = 0;
    int STAGECOUNTERMAX;


    int PCAR_RANDOMLEVEL;
    int MINE_RANDOM;
    int TREE_RANDOM;
    int HELFIREADD;
    int SWAY_ROAD;

    public void setDifficulty(int lvl)
    {
        STAGECOUNTERMAX = (stage + 1) * 10;
        if (lvl == 0)
        {
            PCAR_RANDOMLEVEL = 3;
            MINE_RANDOM = 30;
            TREE_RANDOM = 50;
            HELFIREADD = 1;
            SWAY_ROAD = 60;
        }
        if (lvl == 1)
        {
            PCAR_RANDOMLEVEL = 2;
            MINE_RANDOM = 30;
            TREE_RANDOM = 30;
            HELFIREADD = 2;
            SWAY_ROAD = 40;
        }
        if (lvl == 2)
        {
            PCAR_RANDOMLEVEL = 1;
            MINE_RANDOM = 30;
            TREE_RANDOM = 10;
            HELFIREADD = 3;
            SWAY_ROAD = 20;
        }
    }

    public int getPlayerScore()
    {
        i_PlayerScore = miles_counter;
        return i_PlayerScore;
    }

    public BCar(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        //W = _screenWidth;
        //H = _screenHeight;
    }

    public void newGameSession(int _level)
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        startGame();
    }

    public void initStage(int _stage)
    {

        super.initStage(_stage);
        stage = _stage;
        setDifficulty(stage);
        policecar = false;
        helicopter = false;
        if(_stage > 0)
         miles_counter += _stage*1000;
        for (int ty = 0; ty < road.length; ty++)
            road[ty] = roadLeftX;
        for (int i = 0;i<road.length;i++)
        {
            roaditems[i][0] = 0;
            roaditems[i][1] = 0;
        }
    }

    public void nextGameStep(int i_Value)
    {

        switch (i_Value)
        {
            case BUTTON_NONE:
                {
                    cardir = 1;
                }
                break;
            case BUTTON_LEFT:
                {
                    cardir = 0;
                }
                break;
            case BUTTON_RIGHT:
                {
                    cardir = 2;
                }
                break;
        }
        gameAction();
    }

    public void startGame()
    {
        roadLeftX = (W - ROADWIDTH) / 2;
        cardir = 1; // 0 left , 1 center , 2 right
        CARX = (W - CARWIDTH) / 2;
        CARY = H - CARHEIGHT - CARYDECR;
        carspeed = START_PLAYER_SPEED; // 8 bit factor
        curspeed_accumulator = 0;
        miles_counter = 0;
        diradd = 0;
        curroaddir = 1; // 0 left , 1 center , 2 right
        brokeroad = false;
        addTobrokeroad = 0;
        brcounter = 0;

        road = new int[H];
        roaditems = new int[H][2];

        policecardowncounter = 0;
        policecar = false;
        policecarX = -1;
        policecarY = -1;

        frontcar = false;
        frontcarX = -1;
        frontcarY = -1;
        frontcarBeginY = -CARHEIGHT;

        helicopter = false;
        helicopterX = (W - helicopterWIDTH) / 2;
        helicopterY = -helicopterHEIGHT;
        helicopterDelay = 0;
        helicopterLiveCounter = 0;
        helicopterUp = false;
        helicopterLeft = true;
        helicopterFire = false;
        helicopterFire2 = false;
        helMineFire = false;
        helicopterFireX = (W - helicopterWIDTH) / 2;
        helicopterFireY = helicopterHEIGHT;
        helicopterFireCounter = 0;

        policecarwin = false;
        policecaught = false;
        carexplosion = false;
        carexplosioncounter = 0;

        for (int ty = 0; ty < road.length; ty++)
            road[ty] = roadLeftX;

        stage = 1;
        stagecounter = 0;
        setDifficulty(stage);
    }

    public void gameAction()
    {
        if (policecarwin && i_PlayerState != PLAYERSTATE_LOST)
        {
            if (carexplosion)
            {
                carexplosioncounter++;
                if (carexplosioncounter >= carexplosioncounterMAX)
                {
                    i_PlayerState = PLAYERSTATE_LOST;
                    i_GameState = GAMESTATE_OVER;
                }
            }
            else
            {
                i_PlayerState = PLAYERSTATE_LOST;
                i_GameState = GAMESTATE_OVER;
            }
        }

        if (policecar && carspeed <= CAR_ACCELERATE_STEP)
        {
            policecarwin = true;
            policecaught = true;
        }

        if (!policecarwin && !playerwin)
        {

            mainCollisionDetection();

            if (cardir == 0 && CARX > ROAD_BORDER_LEFT)
                CARX -= 2;
            if (cardir == 2 && CARX < ROAD_BORDER_RIGHT)
                CARX += 2;

            int rnd = getRandomInt(3);
            if (!policecar && rnd == 2 && brcounter % 40 == 0)//== (BRCOUNTERMAX >> 1))
            {
                policecarY = policecarBeginY;
                rnd = getRandomInt(2);
                if (rnd == 1)
                    policecarX = CARX + CARWIDTH + 5;
                else
                    policecarX = CARX - 5;
                policecar_begindelay = 0;
                policecar = true;
            }

            rnd = getRandomInt(2);
            if (!helicopter && rnd == 1 && brcounter == 0 && stage > 0)
            {
                helicopterX = (W - helicopterWIDTH) >> 1;
                helicopterY = -helicopterHEIGHT;
                helicopterDelay = 0;
                helicopterLiveCounter = 0;
                helicopter = true;

            }

            rnd = getRandomInt(2);
            if (!frontcar && rnd == 2 && brcounter == BRCOUNTERMAX)
            {
                frontcarY = frontcarBeginY;
                frontcarX = road[frontcarBeginY + CARHEIGHT] + CARWIDTH;
                frontcar = true;
            }

            if (!brokeroad && getRandomInt(SWAY_ROAD) == 1)
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
                    stagecounter = 0;
                    i_PlayerState = PLAYERSTATE_WON;
//                 stage++;
//                 setDifficulty(stage);
//                 System.out.println("New stage ! - "+stage);
                }
                if (stage == 3)
                {
                    playerwin = true;
                    i_PlayerState = PLAYERSTATE_WON;
                    i_GameState = GAMESTATE_OVER;
                }
//                System.out.println(stagecounter);
                stagecounter++;
                brokeroad = true;
            }
            brcounter++;
            if (brcounter > BRCOUNTERMAX)
                brcounter = 0;
/*
	    if (carspeed < MAX_PLAYER_SPEED)carspeed += CAR_ACCELERATE_STEP;
	      else carspeed = MAX_PLAYER_SPEED;
	    curspeed_accumulator += carspeed;
*/
            genereateRoad(curspeed_accumulator >> 8);
            curspeed_accumulator &= 0xff;
            frontCarAction();
            policeCarAction();
            roadItemsCarAction();
            helicopterAction();
            helicopterFireAction();
        }
    }

    public void helicopterFireAction()
    {
        if (helicopterFire && helicopterY > 0)
        {
            int rnd = getRandomInt(2);
            if (rnd == 1 && !helicopterFire2)
            {
                rnd = getRandomInt(2);
                helicopterFire2 = true;
                if (rnd == 1) // Mine
                {
                    helMineFire = false;
                    roaditems[helicopterY][0] = helicopterX;
                    roaditems[helicopterY][1] = 1;
                }
                else // Fire
                {
                    helicopterFireY = helicopterHEIGHT;
                    helicopterFireX = helicopterX + helicopterWIDTH / 2;
                    helMineFire = true;
                }
            }
            if (helicopterFire2 && helicopterFireCounter == 0)//DELAY)
            {
                helMineFire = false;
                helicopterFire = false;
                helicopterFire2 = false;
            }
            helicopterFireCounter++;
            if (helicopterFireCounter > DELAY)
                helicopterFireCounter = 0;
        }
        if (helMineFire)
        {
            helicopterFireY += HELFIREADD;
            if (helicopterFireY > CARY + CARHEIGHT / 2)
            {
                helMineFire = false;
                helicopterFire = false;
                helicopterFire2 = false;

                if (CARX < helicopterFireX && CARX + CARWIDTH > helicopterFireX)
                {
                    policecarwin = true;
                    carexplosion = true;
                }
            }
        }

    }

    public void helicopterAction()
    {
        if (helicopterUp)
        {
            helicopterY--;
            if (helicopterY < -helicopterHEIGHT)
                helicopter = false;
        }
        if (helicopter && helicopterDelay == 0 && !helicopterUp)
        {
            if (helicopterY < helicopterHEIGHT)
                helicopterY++;

            if (helicopterX < CARX)
            {
                helicopterX += 1;
                helicopterLeft = false;
            }
            if (helicopterX > CARX)
            {
                helicopterX -= 1;
                helicopterLeft = true;
            }
            if (Math.abs(helicopterX - CARX) < 5)
            {
                helicopterFire = true;
            }

            helicopterLiveCounter++;
            if (helicopterLiveCounter > DELAY * 3)
                helicopterUp = true;
        }
        helicopterDelay++;
        if (helicopterDelay > 100) ;//DELAY / 4)
        {
            helicopterDelay = 0;
        }
    }

    public void roadItemsCarAction()
    {
        for (int ty = CARY; ty < CARY + CARHEIGHT; ty++)
        {
            int gx = roaditems[ty][0];
            int rw;
            if (roaditems[ty][1] != 1)
                rw = MINE_ROADWIDTH;
            else
                rw = MINE_HELWIDTH;
            if (gx != 0)
            {
                if((CARX < gx+rw  && CARX > gx) ||
                   (CARX+CARWIDTH > gx && CARX+CARWIDTH < gx+rw) ||
                   (CARX < gx && CARX+CARWIDTH > gx+rw)
                  )
                {
                    policecarwin = true;
                    carexplosion = true;
                }
            }
        }
    }

    public void policeCarAction()
    {
        if (policecarwin || playerwin) return;
        if (policecar)
        {
            int ch = getRandomInt(PCAR_RANDOMLEVEL);
            if (ch == 0)
            {
                if (policecarY > (H - CARHEIGHT - CARYDECR) - CARHEIGHT / 2)
                {
                    if (policecarY > H - CARHEIGHT * 2 - CARYDECR)
                    {
                        policecarY -= getRandomInt(2);
                        if ((carspeed / 10) < 45)
                            policecarY--;
                    }
                }
                else
                {
                    if (policecarY > H - CARHEIGHT * 2 - CARYDECR)
                    {
                        policecarY -= 2;
                    }
                    else
                    {
                        if (Math.abs((policecarX + CARWIDTH / 2) - (CARX + CARWIDTH / 2)) <= 5)
                        {
                            policecarwin = true;
                            policecaught = true;
                            carspeed = 0;
                        }
                    }
                    if ((policecarX + CARWIDTH / 2) >= (CARX + CARWIDTH / 2))
                        policecarX--;
                    else
                        policecarX++;
                }
            }
            if (policecarY > 0 && policecarY < H)
            {
                if (road[policecarY] + (CARWIDTH / 2) > policecarX)
                {
                    policecarX++;
                    policecarY++;
                }
                if (road[policecarY] + ROADWIDTH - (CARWIDTH / 2) * 2 < policecarX)
                {
                    policecarX--;
                    policecarY++;
                }
            }

            if (frontcar)
            {
                if (
                        (frontcarY + CARHEIGHT >= policecarY && frontcarY + CARHEIGHT <= policecarY + CARHEIGHT) ||
                        (frontcarY >= policecarY && frontcarY <= policecarY + CARHEIGHT)
                )
                {
                    policecarY += 2;
                }
            }


            if (policecarY >= H - CARHEIGHT)
                policecar = false;

        }
    }

/*
    public void policeCarAction() {
        if (policecarwin || playerwin) return;
        if (policecar) {
            if (policecar_begindelay < 30) policecar_begindelay++;
            if (getRandomInt(PCAR_RANDOMLEVEL) == 0)
            {
                if (policecarY > (H - CARHEIGHT - CARYDECR) - (CARHEIGHT >> 1)) {
                    if (policecarY > H - (CARHEIGHT << 1) - CARYDECR) {
                        policecarY -= getRandomInt(2);
                        if (carspeed < POLICE_RADAR_ALARM_SPEED)
                            policecarY--;
                    }
                } else {
                    if (policecarY > H - (CARHEIGHT << 1) - CARYDECR) {
                        policecarY -= 2;
                    } else {
                        if (Math.abs(policecarX - CARX) <= CARWIDTH + 1 && carspeed > 0) {
                            carspeed -= MAX_CAR_HIT + MAX_CAR_HIT;
                        }
                    }
                    if ((policecarX) >= (CARX))
                        policecarX--;
                    else
                        policecarX++;
                }
            }
            if (policecarY > 0 && policecarY < H) {
                if (road[policecarY] + (CARWIDTH >> 1) > policecarX) {
                    policecarX++;
                    policecarY++;
                }
                if (road[policecarY] + ROADWIDTH - CARWIDTH  < policecarX) {
                    policecarX--;
                    policecarY++;
                }
            }

            if (frontcar) {
                if (
                        (frontcarY + CARHEIGHT >= policecarY && frontcarY + CARHEIGHT <= policecarY + CARHEIGHT) ||
                        (frontcarY >= policecarY && frontcarY <= policecarY + CARHEIGHT)
                ) {
                    policecarY += 2;
                }
            }


            if (policecarY >= H - CARHEIGHT)
                policecar = false;

        }
    }
*/

    public void frontCarAction()
    {
        if (frontcarY >= H || frontcarY < -CARHEIGHT)
            frontcar = false;

        if (frontcar)
        {
            int fcaddY = getRandomInt(3);
            frontcarY += fcaddY;

            if (carspeed <= POLICE_RADAR_ALARM_SPEED)
                frontcarY -= (fcaddY << 1);
            if (frontcarY > 0 && frontcarY < H)
            {
                if (road[frontcarY] + (CARWIDTH >> 1) > frontcarX)
                    frontcarX++;
                if (road[frontcarY] + ROADWIDTH - CARWIDTH /* / 2 * 2 */ < frontcarX)
                    frontcarX--;
            }
        }
    }

    public void mainCollisionDetection()
    {
        boolean cs = collisionDetection(CARX, CARX + CARWIDTH, H - CARHEIGHT - CARYDECR, H - CARYDECR);
        if (cs)
        {
            carspeed -= MAX_OFFROAD_BRAKES;
            if (carspeed < 0) carspeed = 0;
        }
        else
            if (carspeed < MAX_PLAYER_SPEED)
                carspeed += CAR_ACCELERATE_STEP;
            else
                carspeed = MAX_PLAYER_SPEED;
        curspeed_accumulator += carspeed;
    }

    public boolean collisionDetection(int curLeftX, int curRightX, int curUpY, int curDownY)
    {
        boolean cs = false;
        for (int ty = curUpY; ty < curDownY; ty++)
        {
            if (curLeftX < road[ty])
            {
                carspeed -= MAX_OFFROAD_BRAKES;
                cs = true;
                break;
            }
            if (curRightX > (road[ty] + ROADWIDTH))
            {
                carspeed -= MAX_OFFROAD_BRAKES;
                cs = true;
                break;
            }
        }

        if (policecar)
            if (
                    (policecarY + CARHEIGHT - 1 >= curUpY && policecarY + CARHEIGHT - 1 <= curDownY) ||
                    (policecarY >= curUpY && policecarY <= curDownY)
            )
            {
                if (policecarX + CARWIDTH >= curLeftX && policecarX + CARWIDTH <= curRightX)
                {
                    cs = true;
                    if (getRandomInt(5) >= 2)
                        policecarX -= 2;
                    else
                        CARX += 2;
                }
                if (policecarX >= curLeftX && policecarX <= curRightX)
                {
                    cs = true;
                    if (getRandomInt(5) >= 2)
                        policecarX += 2;
                    else
                        CARX -= 2;
                }
            }

        if (frontcar)
            if (
                    (frontcarY + CARHEIGHT >= curUpY && frontcarY + CARHEIGHT <= curDownY) ||
                    (frontcarY >= curUpY && frontcarY <= curDownY)
            )
            {
                if (frontcarX + CARWIDTH >= curLeftX && frontcarX + CARWIDTH <= curRightX)
                {
                    frontcarY--;
                    frontcarX -= 2;
                    carspeed -= MAX_CAR_HIT;
                    cs = true;
                }
                if (frontcarX >= curLeftX && frontcarX <= curRightX)
                {
                    frontcarY--;
                    frontcarX -= 2;
                    carspeed -= MAX_CAR_HIT;
                    cs = true;
                }
            }
        return cs;
    }


    public void genereateRoad(int offset)
    {
        if (offset > 0 && offset < road.length)
        {
            miles_counter += offset;
            int i,j;
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
                if (brokeroad)// && brcounter%2 == 0)
                {
                    road[i] = roadLeftX + addTobrokeroad;
                    addTobrokeroad += diradd;
                    j = (W - ROADWIDTH) / 3;

                    if (addTobrokeroad > j || addTobrokeroad < -j)
                        brokeroad = false;
                }
                else
                    road[i] = road[i + 1];
                roaditems[i][0] = 0;
                roaditems[i][1] = 0;
            }

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
                    if (xc >= road[1] - 8) xc += ROADWIDTH + 16;
                    roaditems[1][0] = xc;
                    roaditems[1][1] = OBJECT_TREE;
                }
        }
    }

    // ----------------------------------------------------------------------------------
    public void writeToStream(DataOutputStream ds) throws IOException
    {
        ds.writeInt(stage);
        ds.writeInt(roadLeftX);
        ds.writeInt(cardir);
        ds.writeInt(CARX);
        ds.writeInt(CARY);
        ds.writeInt(carspeed);
        ds.writeInt(diradd);
        ds.writeInt(curroaddir);
        ds.writeBoolean(brokeroad);
        ds.writeInt(addTobrokeroad);
        ds.writeInt(brcounter);
        ds.writeInt(miles_counter);

        ds.writeInt(policecardowncounter);
        ds.writeBoolean(policecar);
        ds.writeInt(policecarX);
        ds.writeInt(policecarY);

        ds.writeBoolean(frontcar);
        ds.writeInt(frontcarX);
        ds.writeInt(frontcarY);
        ds.writeInt(frontcarBeginY);

        ds.writeBoolean(helicopter);
        ds.writeInt(helicopterX);
        ds.writeInt(helicopterY);
        ds.writeInt(helicopterDelay);
        ds.writeInt(helicopterLiveCounter);
        ds.writeBoolean(helicopterUp);
        ds.writeBoolean(helicopterLeft);
        ds.writeBoolean(helicopterFire);
        ds.writeBoolean(helicopterFire2);
        ds.writeBoolean(helMineFire);
        ds.writeInt(helicopterFireX);
        ds.writeInt(helicopterFireY);
        ds.writeInt(helicopterFireCounter);

        ds.writeBoolean(policecarwin);
        ds.writeBoolean(carexplosion);
        ds.writeInt(carexplosioncounter);

        for (int ty = 0; ty < road.length; ty++)
        {
            ds.writeInt(road[ty]);
            ds.writeInt(roaditems[ty][0]);
            ds.writeInt(roaditems[ty][1]);
        }
        ds.writeInt(stagecounter);
    }

    public void readFromStream(DataInputStream ds) throws IOException
    {
        stage = ds.readInt();
        roadLeftX = ds.readInt();
        cardir = ds.readInt();
        CARX = ds.readInt();
        CARY = ds.readInt();
        carspeed = ds.readInt();
        diradd = ds.readInt();
        curroaddir = ds.readInt();
        brokeroad = ds.readBoolean();
        addTobrokeroad = ds.readInt();
        brcounter = ds.readInt();
        miles_counter = ds.readInt();

        policecardowncounter = ds.readInt();
        policecar = ds.readBoolean();
        policecarX = ds.readInt();
        policecarY = ds.readInt();

        frontcar = ds.readBoolean();
        frontcarX = ds.readInt();
        frontcarY = ds.readInt();
        frontcarBeginY = ds.readInt();

        helicopter = ds.readBoolean();
        helicopterX = ds.readInt();
        helicopterY = ds.readInt();
        helicopterDelay = ds.readInt();
        helicopterLiveCounter = ds.readInt();
        helicopterUp = ds.readBoolean();
        helicopterLeft = ds.readBoolean();
        helicopterFire = ds.readBoolean();
        helicopterFire2 = ds.readBoolean();
        helMineFire = ds.readBoolean();
        helicopterFireX = ds.readInt();
        helicopterFireY = ds.readInt();
        helicopterFireCounter = ds.readInt();

        policecarwin = ds.readBoolean();
        carexplosion = ds.readBoolean();
        carexplosioncounter = ds.readInt();

        for (int ty = 0; ty < road.length; ty++)
        {
            road[ty] = ds.readInt();
            roaditems[ty][0] = ds.readInt();
            roaditems[ty][1] = ds.readInt();
        }
        stagecounter = ds.readInt();
        setDifficulty(stage);
    }

    public String getGameID()
    {
        return "BCar0184576";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1024;
    }
    // ----------------------------------------------------------------------------------

}
