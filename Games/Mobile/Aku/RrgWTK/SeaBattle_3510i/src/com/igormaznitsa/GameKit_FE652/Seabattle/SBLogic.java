package com.igormaznitsa.GameKit_FE652.Seabattle;

import com.igormaznitsa.GameAPI.Utils.RndGenerator;

public class SBLogic
{
    // Game variables
    public byte _ourGameField [] = null; // Our gamefield
    public byte _opponentGameField [] = null; // Opponent gamefield

    // Constatnts for game field
    public static final byte BS_FIELD_EMPTY = 0; // Empty field
    public static final byte BS_FIELD_SHIP = 1;  // Ship field
    public static final byte BS_FIELD_HIT = 2;   // Hit field
    public static final byte BS_FIELD_MISS = 3;  // Miss field
    public static final byte BS_FIELD_SHIPDESTROY = 4;// Hit in Ship

    // Subpoint of MOVE state ==============================
    public static final byte BS_MOVE_OK = 0;			// All OK
    public static final byte BS_MOVE_GAMEPAUSE = 1;		// Pause of the game
    public static final byte BS_MOVE_GAMEEXIT = 2;		// Exit from the game
    public static final byte BS_MOVE_HIT = 3;			// Hit in ship
    public static final byte BS_MOVE_SHIPDESTRUCTION = 4;// Ship destruction
    public static final byte BS_MOVE_MISS = 5;			// Miss
    //======================================================

    // Game constants
    public static final byte SHIPS_COUNT_1 = 4;		// Number of well-type ships
    public static final byte SHIPS_COUNT_2 = 3;		// Number of two-funneled ships
    public static final byte SHIPS_COUNT_3 = 2;		// Number of three-funneled ships
    public static final byte SHIPS_COUNT_4 = 1;		// Number of four-funneled ships

    public static final int SUMMARY_SHIPS_FIELDS = SHIPS_COUNT_1 + SHIPS_COUNT_2 * 2 + SHIPS_COUNT_3 * 3 + SHIPS_COUNT_4 * 4;	// Summary ships fields
    public static final int SUMMARY_SHIPS_COUNT = SHIPS_COUNT_1 + SHIPS_COUNT_2 + SHIPS_COUNT_3 + SHIPS_COUNT_4;	// Summary ships number

    public static final byte DIRECTION_NORTHWARD = 0; //Northward
    public static final byte DIRECTION_EASTWARD = 1;  //Eastward
    public static final byte DIRECTION_SOUTHWARD = 2; //Southward
    public static final byte DIRECTION_WESTWARD = 3;  //Westward

    static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis()); // Randomize number generator

    protected static final int MAX_ATTEMPT = 75;
    protected Seabattle_GSR _game_state = null;
    protected int _current_hits = 0;

    protected int _1_ship = 0;
    protected int _2_ship = 0;
    protected int _3_ship = 0;
    protected int _4_ship = 0;

    private int _lastshipnumber = 0;

    public int getHitNumber()
    {
        return _current_hits;
    }

    public int getShip1Counter()
    {
        return _1_ship;
    }

    public int getShip2Counter()
    {
        return _2_ship;
    }

    public int getShip3Counter()
    {
        return _3_ship;
    }

    public int getShip4Counter()
    {
        return _4_ship;
    }

    public void init()
    {
        _1_ship = SHIPS_COUNT_1;
        _2_ship = SHIPS_COUNT_2;
        _3_ship = SHIPS_COUNT_3;
        _4_ship = SHIPS_COUNT_4;

        clearOpponentField();
        clearOurField();
        _current_hits = 0;
    }

    public SBLogic()
    {
        _ourGameField = new byte[Seabattle_SB.GAMEFIELD_WIDTH * Seabattle_SB.GAMEFIELD_HEIGHT];
        _opponentGameField = new byte[_ourGameField.length];
    }

    public byte getOurElement(int x, int y)
    {
        if ((x>=Seabattle_SB.GAMEFIELD_WIDTH)||(x<0)) return BS_FIELD_MISS;
        if ((y>=Seabattle_SB.GAMEFIELD_HEIGHT)||(y<0)) return BS_FIELD_MISS;
        int indx = x + y * Seabattle_SB.GAMEFIELD_WIDTH;
        return _ourGameField[indx];
    }

    public void setOurElement(int x, int y, byte ele)
    {
        _ourGameField[x + y * Seabattle_SB.GAMEFIELD_WIDTH] = ele;
    }

    public byte getOpponentElement(int x, int y)
    {
        if ((x>=Seabattle_SB.GAMEFIELD_WIDTH)||(x<0)) return BS_FIELD_MISS;
        if ((y>=Seabattle_SB.GAMEFIELD_HEIGHT)||(y<0)) return BS_FIELD_MISS;
        int indx = x + y * Seabattle_SB.GAMEFIELD_WIDTH;
        return _opponentGameField[indx];
    }

    public void setOpponentElement(int x, int y, byte ele)
    {
        _opponentGameField[x + y * Seabattle_SB.GAMEFIELD_WIDTH] = ele;
    }

    public byte[] getOpponentArray()
    {
        return _opponentGameField;
    }

    public byte[] getOurArray()
    {
        return _ourGameField;
    }

    // Checking of ending of ship arrangement
    protected boolean isEndOfArrangement()
    {
        int cnt = 0;
        for (int li = 0; li < _ourGameField.length; li++)
            if (_ourGameField[li] == BS_FIELD_SHIP) cnt++;

        if (cnt != SUMMARY_SHIPS_FIELDS) return false; else return true;
    }

    // Clearing of our game field
    public  void clearOurField()
    {
        for (int li = 0; li < _ourGameField.length; li++)
            _ourGameField[li] = BS_FIELD_EMPTY;
    }

    // Clearing of opponent game field
    public void clearOpponentField()
    {
        for (int li = 0; li < _opponentGameField.length; li++)
            _opponentGameField[li] = BS_FIELD_EMPTY;
    }

    // Function of placing of the ship. If OK then TRUE else FALSE.
    public boolean placingShip(int ship, int start_x, int start_y, int direction)
    {
        int dx = 0;
        int dy = 0;

        int ll = 0;

        int la = 0;
        int lb = 0;
        int la1 = 0;
        int lb1 = 0;

        switch (direction)
        {
            case DIRECTION_EASTWARD:
                {
                    dx = 1;
                    dy = 0;
                }
                ;
                break;
            case DIRECTION_NORTHWARD:
                {
                    dx = 0;
                    dy = -1;
                }
                ;
                break;
            case DIRECTION_SOUTHWARD:
                {
                    dx = 0;
                    dy = 1;
                }
                ;
                break;
            case DIRECTION_WESTWARD:
                {
                    dx = -1;
                    dy = 0;
                }
                ;
                break;
            default :
                {
                    dx = 0;
                    dy = -1;
                }
        }

        // Check place for ship
        la = start_x;
        lb = start_y;

        for (ll = 0; ll < ship; ll++)
        {
            if ((la >= Seabattle_SB.GAMEFIELD_WIDTH) || (la < 0)) return false;
            if ((lb >= Seabattle_SB.GAMEFIELD_HEIGHT) || (lb < 0)) return false;

            for (la1 = (la - 1); la1 <= (la + 1); la1++)
            {
                for (lb1 = (lb - 1); lb1 <= (lb + 1); lb1++)
                {
                    if ((lb1 >= Seabattle_SB.GAMEFIELD_HEIGHT) || (lb1 < 0) || (la1 >= Seabattle_SB.GAMEFIELD_WIDTH) || (la1 < 0)) continue;
                    if (getOurElement(la1, lb1) != BS_FIELD_EMPTY) return false;
                }
            }
            la += dx;
            lb += dy;
        }

        // Place the Ship
        la = start_x;
        lb = start_y;
        for (ll = 0; ll < ship; ll++)
        {
            setOurElement(la, lb, BS_FIELD_SHIP);
            la += dx;
            lb += dy;
        }
        return true;
    }

    // Auto placing of all ships
    public void autoPlacingOurShips()
    {
        clearOurField();

        int li = 0;
        int lx = 0;
        int ly = 0;
        int ld = 0;

        int attempt = 0;

        boolean put_flag = false;

        while (true)
        {
            clearOurField();

            //	Well-type ships
            for (li = 0; li < SHIPS_COUNT_1; li++)
            {
                put_flag = false;
                attempt = 0;

                while (!put_flag)
                {
                    lx = _rnd.getInt(Seabattle_SB.GAMEFIELD_WIDTH - 1);
                    ly = _rnd.getInt(Seabattle_SB.GAMEFIELD_HEIGHT - 1);
                    if (placingShip(1, lx, ly, ld))
                    {
                        put_flag = true;
                    }
                    attempt++;
                    if (attempt > MAX_ATTEMPT) break;
                }

                if (attempt > MAX_ATTEMPT) break;
            }

            if (attempt > MAX_ATTEMPT) continue;

            //	two-funneled ships
            for (li = 0; li < SHIPS_COUNT_2; li++)
            {
                put_flag = false;
                attempt = 0;

                while (!put_flag)
                {
                    lx = _rnd.getInt(Seabattle_SB.GAMEFIELD_WIDTH - 1);
                    ly = _rnd.getInt(Seabattle_SB.GAMEFIELD_HEIGHT - 1);
                    ld = _rnd.getInt(DIRECTION_EASTWARD);

                    for (int ln = 0; ln <= 4; ln++)
                    {
                        if (placingShip(2, lx, ly, ld))
                        {
                            put_flag = true;
                            break;
                        }
                        ld++;
                        if (ld > DIRECTION_EASTWARD) ld = DIRECTION_NORTHWARD;
                    }
                    attempt++;
                    if (attempt > MAX_ATTEMPT) break;
                }
                if (attempt > MAX_ATTEMPT) break;
            }
            if (attempt > MAX_ATTEMPT) continue;

            //	three-funneled ships
            for (li = 0; li < SHIPS_COUNT_3; li++)
            {
                put_flag = false;
                attempt = 0;
                while (!put_flag)
                {
                    lx = _rnd.getInt(Seabattle_SB.GAMEFIELD_WIDTH - 1);
                    ly = _rnd.getInt(Seabattle_SB.GAMEFIELD_HEIGHT - 1);
                    ld = _rnd.getInt(DIRECTION_EASTWARD);

                    for (int ln = 0; ln <= 4; ln++)
                    {
                        if (placingShip(3, lx, ly, ld))
                        {
                            put_flag = true;
                            break;
                        }
                        ld++;
                        if (ld > DIRECTION_EASTWARD) ld = DIRECTION_NORTHWARD;
                    }
                    attempt++;
                    if (attempt > MAX_ATTEMPT) break;
                }
                if (attempt > MAX_ATTEMPT) break;
            }
            if (attempt > MAX_ATTEMPT) continue;

            //	four-funneled ships
            for (li = 0; li < SHIPS_COUNT_4; li++)
            {
                put_flag = false;
                attempt = 0;
                while (!put_flag)
                {
                    lx = _rnd.getInt(Seabattle_SB.GAMEFIELD_WIDTH - 1);
                    ly = _rnd.getInt(Seabattle_SB.GAMEFIELD_HEIGHT - 1);
                    ld = _rnd.getInt(DIRECTION_EASTWARD);

                    for (int ln = 0; ln <= 4; ln++)
                    {
                        if (placingShip(4, lx, ly, ld))
                        {
                            put_flag = true;
                            break;
                        }
                        ld++;
                        if (ld > DIRECTION_EASTWARD) ld = DIRECTION_NORTHWARD;
                    }
                    attempt++;
                    if (attempt > MAX_ATTEMPT) break;
                }
                if (attempt > MAX_ATTEMPT) break;
            }
            if (attempt > MAX_ATTEMPT) continue; else break;
        }
    }

    protected int calcShipBit(int x, int y)
    {
        int lx0,ly0;
        int rslt = 0;
        for (int lx = -1; lx < 2; lx++)
            for (int ly = -1; ly < 2; ly++)
            {
                lx0 = x + lx;
                ly0 = y + ly;
                if ((lx == 0) && (ly == 0)) continue;
                if ((lx0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 < 0) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) continue;
                if (getOpponentElement(lx0, ly0) == BS_FIELD_HIT) rslt++;
            }
        return rslt;
    }

    // True if destroyed else False
    protected boolean isShipDestroed(int x, int y)
    {
        int lx0,ly0;
        boolean rslt = false;
        int dx = 0,dy = 0,dax = 0,day = 0;

        _lastshipnumber = 0;

        for (int lx = -1; lx < 2; lx++)
        {
            for (int ly = -1; ly < 2; ly++)
            {
                lx0 = x + lx;
                ly0 = y + ly;
                if ((lx == 0) && (ly == 0)) continue;
                if ((lx0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 < 0) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) continue;
                if ((getOurElement(lx0, ly0) == BS_FIELD_HIT) || (getOurElement(lx0, ly0) == BS_FIELD_SHIP))
                {
                    rslt = true;
                    dx = lx;
                    dy = ly;
                    break;
                }
            }
            if (rslt) break;
        }

        if (!rslt) return true;

        dax = 0 - dx;
        day = 0 - dy;

        lx0 = x+dx;
        ly0 = y+dy;

        while (true)
        {
            if ((lx0 < 0) || (ly0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) break;
            if ((getOurElement(lx0, ly0) == BS_FIELD_MISS) || (getOurElement(lx0, ly0) == BS_FIELD_EMPTY)) break;
            if (getOurElement(lx0, ly0) == BS_FIELD_SHIP) return false;
            lx0 += dx;
            ly0 += dy;
            _lastshipnumber++;
        }

        lx0 = x;
        ly0 = y;
        while (true)
        {
            if ((lx0 < 0) || (ly0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) break;
            if ((getOurElement(lx0, ly0) == BS_FIELD_MISS) || (getOurElement(lx0, ly0) == BS_FIELD_EMPTY)) break;
            if (getOurElement(lx0, ly0) == BS_FIELD_SHIP) return false;
            lx0 += dax;
            ly0 += day;
            _lastshipnumber++;
        }
        return true;
    }

    protected void setShipBitDestroy(int x, int y)
    {
        int lx0,ly0;
        for (int lx = -1; lx < 2; lx++)
            for (int ly = -1; ly < 2; ly++)
            {
                lx0 = x + lx;
                ly0 = y + ly;
                if ((lx0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 < 0) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) continue;
                if ((lx == 0) && (ly == 0))
                    setOpponentElement(lx0, ly0, BS_FIELD_SHIPDESTROY);
                else
                    switch (getOpponentElement(lx0, ly0))
                    {
                        case BS_FIELD_HIT:
                        case BS_FIELD_SHIPDESTROY:
                            setOpponentElement(lx0, ly0, BS_FIELD_SHIPDESTROY);
                            break;
                        default:
                            setOpponentElement(lx0, ly0, BS_FIELD_MISS);
                    }
            }
    }

    public byte getShotResult(int x, int y)
    {
        int lf = getOurElement(x, y);
        if (lf == BS_FIELD_SHIP)
        {
            _current_hits++;
            setOurElement(x, y, BS_FIELD_HIT);
            if (isShipDestroed(x, y))
            {
                switch(_lastshipnumber)
                {
                    case 0 : _1_ship--;break;
                    case 2 : _2_ship--;break;
                    case 3 : _3_ship--;break;
                    case 4 : _4_ship--;break;
                    default : System.err.println("ERROR UNKNOWN TYPE SHIP IS DESTROYED");
                }
                return BS_MOVE_SHIPDESTRUCTION;
            }
            else
                return BS_MOVE_HIT;
        }
        setOurElement(x, y, BS_FIELD_MISS);
        return BS_MOVE_MISS;
    }

    protected void setShipDestroyed(int x, int y)
    {
        int lx0,ly0;
        boolean rslt = false;
        int dx = -1;
        int dy = -1;
        int dax = -1;
        int day = -1;

        for (int lx = -1; lx < 2; lx++)
        {
            for (int ly = -1; ly < 2; ly++)
            {
                lx0 = x + lx;
                ly0 = y + ly;
                if ((lx == 0) && (ly == 0)) continue;
                if ((lx0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 < 0) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) continue;
                if ((getOpponentElement(lx0, ly0) == BS_FIELD_HIT) || (getOpponentElement(lx0, ly0) == BS_FIELD_SHIP))
                {
                    rslt = true;
                    dx = lx;
                    dy = ly;
                    break;
                }
            }
            if (rslt) break;
        }

        setShipBitDestroy(x, y);
        if (!rslt) return;

        dax = 0 - dx;
        day = 0 - dy;

        lx0 = x;
        ly0 = y;

        while (true)
        {
            if ((lx0 < 0) || (ly0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) break;
            if ((getOpponentElement(lx0, ly0) == BS_FIELD_MISS) || (getOpponentElement(lx0, ly0) == BS_FIELD_EMPTY))
                break;
            setShipBitDestroy(lx0, ly0);
            lx0 += dx;
            ly0 += dy;
        }

        lx0 = x;
        ly0 = y;
        while (true)
        {
            if ((lx0 < 0) || (ly0 < 0) || (lx0 >= Seabattle_SB.GAMEFIELD_WIDTH) || (ly0 >= Seabattle_SB.GAMEFIELD_HEIGHT)) break;
            if ((getOpponentElement(lx0, ly0) == BS_FIELD_MISS) || (getOpponentElement(lx0, ly0) == BS_FIELD_EMPTY))
                break;
            setShipBitDestroy(lx0, ly0);
            lx0 += dax;
            ly0 += day;
        }
    }

    public void setShotResult(int x, int y, byte shot_status)
    {
        switch (shot_status)
        {
            case BS_MOVE_HIT:
                {
                    setOpponentElement(x, y, BS_FIELD_HIT);
                }
                ;
                break;
            case BS_MOVE_MISS:
                {
                    setOpponentElement(x, y, BS_FIELD_MISS);
                }
                ;
                break;
            case BS_MOVE_SHIPDESTRUCTION:
                {
                    setShipDestroyed(x, y);
                }
                ;
                break;
        }
    }

    public Seabattle_PMR getStrikeCoord()
    {
        int lex = -1;
        int ley = -1;

        for (int ly = 0; ly < Seabattle_SB.GAMEFIELD_HEIGHT; ly++)
        {
            for (int lx = 0; lx < Seabattle_SB.GAMEFIELD_WIDTH; lx++)
            {
                int lb = getOpponentElement(lx, ly);
                switch (lb)
                {
                    case BS_FIELD_EMPTY:
                        {
                            lex = lx;
                            ley = ly;
                        }
                        ;
                        break;
                    case BS_FIELD_HIT:
                        {
                            int onestrx = 0,onestry = 0;
                            boolean _nobreak = true;
                            for (int dx = -1; (dx < 2) && _nobreak; dx++)
                                for (int dy = -1; (dy < 2) && _nobreak; dy++)
                                {
                                    int tmp = dx + dy;
                                    if (tmp != -1 && tmp != 1) continue;

                                    int idx = 0 - dx;
                                    int idy = 0 - dy;

                                    int ele0 = getOpponentElement(lx + dx, ly + dy);
                                    int ele1 = getOpponentElement(lx + idx, ly + idy);

                                    if ((ele0 == BS_FIELD_HIT) && (ele1 == BS_FIELD_EMPTY)) return new Seabattle_PMR(lx + idx, ly + idy);
                                    if ((ele1 == BS_FIELD_HIT) && (ele0 == BS_FIELD_EMPTY)) return new Seabattle_PMR(lx + dx, ly + dy);

                                    if ((ele0 == BS_FIELD_HIT) || (ele1 == BS_FIELD_HIT))
                                    {
                                        _nobreak = false;
                                        continue;
                                    }

                                    if (ele0 == BS_FIELD_EMPTY)
                                    {
                                        onestrx = lx + dx;
                                        onestry = ly + dy;

                                    }
                                    else if (ele1 == BS_FIELD_EMPTY)
                                    {
                                        onestrx = lx + idx;
                                        onestry = ly + idy;
                                    }
                                }
                            if (_nobreak)
                            {
                                return new Seabattle_PMR(onestrx, onestry);
                            }
                        }
                        ;
                        break;
                }
            }
        }

        for (int la = 0; la < MAX_ATTEMPT; la++)
        {
            int lx = _rnd.getInt(Seabattle_SB.GAMEFIELD_WIDTH - 1);
            int ly = _rnd.getInt(Seabattle_SB.GAMEFIELD_HEIGHT - 1);

            if (getOpponentElement(lx, ly) == BS_FIELD_EMPTY) return new Seabattle_PMR(lx, ly);
        }

        return new Seabattle_PMR(lex, ley);
    }
}
