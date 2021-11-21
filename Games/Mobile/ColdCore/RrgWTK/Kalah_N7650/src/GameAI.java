
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Kuligin
 *      this class implement the artificial intelligent for the game
 * Date: 05.09.2003
 * Time: 11:55:53
 */
public class GameAI extends Random
{

    public  static final int INITIAL_STONES_COUNT = 6;
    public  static final int HOLES_COUNT = 6;
    public  static final int PLAYER_KALAH_INDEX = 6;
    public  static final int AI_KALAH_INDEX = 13;
    public  static final int AI_INDEX_SHIFT = 7;
    public  static final int CALLBACK_DELAY = 2000;


    public  treeNode rootPos;
    public  treeNode root;
    private treeNode  wStack[];
    private treeNode  bestMove;
    private int   cL;

    private GameletImpl p_Gamelet;
    private int   i_iteration;

    //  ai level
    private int   maxLevel;

    ///  AI move information
    private int  aiMoveIndex;

    ////   store information about grabbed stones
    private boolean bGrabAdditionalStones;
    private int grabbedHoleIndex;
    private int grabbedStonesCount;

    ////////////////////////////////////////////////////////////////////////////
    public boolean  isGrabbedAdditionalStones()
    {
        return bGrabAdditionalStones;
    }

    public int getGrabbedHoleIndex()
    {
        return grabbedHoleIndex;
    }

    public int getGrabbedStonesCount()
    {
      return grabbedStonesCount;
    }

    public int  getCurrentAIMove()
    {
       return bestMove.move[aiMoveIndex];
    }

    public boolean isAIMoveStackEmpty()
    {
      if ((aiMoveIndex >= 0) && (aiMoveIndex <= bestMove.extent))
        return false;
      else
        return true;
    }

    public boolean AIStep()
    {
        i_iteration = 0;
        if (0 == findMove(root))
        {
            return false;
        }
        aiMoveIndex = 0;
        return true;
    }

    /*
    *      returns
    *              0 - OK, but switch move
    *              1 - another player should perform next move
    *             -1 - no more valid move
    */
    public int PlayerStep(int pos)
    {
        int    nRet = 0;
        rootPos.move[0] = pos;
        if (scatterStones(rootPos.desk, rootPos.move[0], true))
            nRet = 1;

        if (isEmpty(root))
        {
            nRet = -1;
        }

        return nRet;
    }

    /*@   execute currect move
     *      returns 0  - no more AI move
     *              1 - another ai move in stack
     *             -1 - no more valid move
    */
    public int executeCurrentAIMove()
    {

        scatterStones(rootPos.desk, bestMove.move[aiMoveIndex], true);
        if (isEmpty(root))
        {
           return -1;
        }
        aiMoveIndex++;
        if (aiMoveIndex > bestMove.extent)
        {
          aiMoveIndex = -1;
          return 0;
        }
        return 1;
    }


    public boolean isEmptyHole(int index)
    {
        if (rootPos.desk[index] == 0)
          return true;
        else
          return false;
    }

    public int getStonesCount(int index)
    {
        return rootPos.desk[index];
    }

    ////////////////////////////////////////////////////////////////////////////
    public GameAI(GameletImpl gamelet)
    {
        maxLevel = 1;
        cL = 0;
        bGrabAdditionalStones = false;
        grabbedHoleIndex = -1;
        grabbedStonesCount = 0;
        aiMoveIndex = -1;
        p_Gamelet = gamelet;
    }

    public boolean GlobalInitialize()
    {
        rootPos = new treeNode();
        root = rootPos;
        bestMove = new treeNode();
        return true;
    }

    public void SetAILevel(int _newLevel)
    {
        maxLevel = _newLevel;
    }

    public void LevelRestart()
    {
        int   i;
                                                            // put stones to the cell
        for (i = 0; i < HOLES_COUNT; i++)
           root.desk[i] = root.desk[i + AI_INDEX_SHIFT] = INITIAL_STONES_COUNT;
                                                            // clears kalah
        root.desk[PLAYER_KALAH_INDEX] = root.desk[AI_KALAH_INDEX] = 0;

        cL = 0;
        rootPos.extent = 0; rootPos.move[0] = 13;
        grabbedHoleIndex = -1;
        grabbedStonesCount = 0;
        bGrabAdditionalStones = false;
        aiMoveIndex = -1;
        for (i = 0; i < maxLevel; i++)
        {
          wStack[i].extent = 0;
        }
    }

    public void LevelInitialize()
    {
        int   i;
        wStack = new treeNode[maxLevel];
        for (i = 0; i < maxLevel; i++)
          wStack[i] = new treeNode();

        LevelRestart();
    }

    public void LevelRelease()
    {
        if(wStack != null)
        {
          for (int i = 0; i < maxLevel; i++)
             wStack[i] = null;
          wStack = null;
        }
    }

    public int  getTotalAIStones()
    {
      return rootPos.desk[AI_KALAH_INDEX];
    }

    public int  getTotalPlayerStones()
    {
      return rootPos.desk[PLAYER_KALAH_INDEX];
    }


//////////////////    AI part
    private int _getRandomInt(int _limit)
    {
       _limit++;
       _limit = (int) (((long) Math.abs(nextInt()) * (long) _limit) >>> 31);
      return _limit;
    }

    public boolean isEmpty(treeNode p)
    {
       int i;
       for (i = 0; i < 6; i++)
         if (p.desk[i] != 0)             // scan for non empty hole
         {
            for (i = 7; i < 13; i++)
              if (p.desk[i] != 0)
                  return false;
         }
       for (i = 0; i < 6; i++)           // collect stones to kalah
       {
              p.desk[6] += p.desk[i];
              p.desk[i] = 0;

              p.desk[13] += p.desk[7+i];
              p.desk[7+i] = 0;
       }
       return true;
    }/*isEmpty*/

    private treeNode  first(treeNode  p)
    {
       if (cL < maxLevel)
       {
         wStack[cL].extent = 0;
         wStack[cL].move[0] = p.move[0] > 6 ? -1 : 6;
         return next(p);
       }
       return null;
    }/*first*/

    private treeNode next(treeNode p)
    {
       int      U = p.move[0] > 6 ? 6 : 13;
       int      move;
       boolean  t;

   /**** will find the next move ****/
       retPos(p);
       move = wStack[cL].move[wStack[cL].extent];

       while(((++move) < U) && (wStack[cL].desk[move] == 0));
       wStack[cL].move[wStack[cL].extent] = move;

       if (move == U)
          return (wStack[cL].extent-- == 0) ? null : next(p);
       else {	        	                                /* move < U */
          t = scatterStones(wStack[cL].desk,move, false);
          if (t)
          {
             if ( isEmpty(wStack[cL]) )
                return wStack[cL];
             else
             {                                                  /* 1.2. (!) */
                wStack[cL].move[++wStack[cL].extent] = p.move[0] > 6 ? -1 : 6;
                return next(p);
             }
          }
          else
             return wStack[cL];
       }
    }/*next*/

    private void retPos(treeNode p)
    {
       int i;
/* for the begining copy position from p         */
       for (i = 0; i < treeNode.DESK_SIZE; i++)
         wStack[cL].desk[i] = p.desk[i];
/* and force the movement to the position right before current   */
       for (i = 0; i < wStack[cL].extent; i++)
       {
          scatterStones(wStack[cL].desk, wStack[cL].move[i], false);
          if ( isEmpty(wStack[cL]) )
            break;
       }
    }/*retPos*/

/* estimate position       */
    private int estimate(treeNode p)
    {
        int  value;
        int  tmp1, tmp2;
        tmp1 = p.desk[6] << 8;
        tmp2 = p.desk[13] << 8;
        value  = tmp1 - tmp2;

/*
        if (0 != getRandomInt(4))
          value = 1;
*/
/*    value += 17.3/(37.5 - p->desk[6]) -
                        17.3/(37.5 - p->desk[13]) + 1.72;*/
        if(! (p.move[0] > 6))
           value = -value;
        return value;
    }/*estimate*/

/*  Alpha-beta pruning, as described in D.Knuth  */
/* return an assessmen for specified node        */
    private int ABprune(treeNode p, int alpha, int beta)
    {
       int          m, t;
       treeNode     curPos;


       i_iteration++;
       if(i_iteration >= CALLBACK_DELAY)
       {
          i_iteration = 0;
          p_Gamelet.p_GameActionListener.gameAction(GameletImpl.GAMEACTION_SHOW_CLOCK);
       }


       cL++;
       curPos = first(p);		/* there are no childs for position P, so returns  NULL*/
       if (curPos == null)
       {
          cL--;
          return estimate(p);
       }
       else
       {
          m = alpha;
          while ((curPos != null) && (m < beta))
          {
             t = -ABprune(curPos, -beta, -m);
             if(t > m)
                m = t;

             curPos = next(p);
          }
       }
       cL--;
       return m;
    }/*ABprune*/

    private int  increment(int i, int move)
    {
      i++;
/* it is a player A move and we are going to put the stone to player B kalah  or */
/* it is a player B move and  array end */
      if ((move < 6) && (i == 13)  || (move > 6) && (i == 14) )
        i = 0;
/* it is a player B move  skip player A kalah */
      else if ((move > 6) && (i == 6))
        i++;
      return i;
    }

    private boolean scatterStones(int desk[], int move, boolean bRealMove)
    {
       int i, stones, tmp1, tmp2;
       boolean  fin = false;

       // reset grabbed stones information
       bGrabAdditionalStones = false;
       grabbedHoleIndex = -1;
       grabbedStonesCount = 0;

       i = move;
       stones = desk[i];                       /* the stones count for this move    */
       desk[i] = 0;
       while (0 != stones--)
       {                                       /* let's put the stones:    */
          i = increment(i, move);                  /* increment i and */
          desk[i]++;                           /* put the next stone  */
       }
 /*** the rule 1.3: ***/
 /* the movement finished on the own side.    */
       if (desk[i] == 1 && desk[12-i] > 0 &&   /* the opposite hole is not empty  */
          ((move < 6 &&                        /* player A move           */
             i < 6) ||                         /* and finished on his side   */
           (move > 6 &&                        /* player B move           */
            i > 6 && i < 13)))                 /* and finished on his side  */
       {
          tmp2 = 12-i;          // opposite index
          tmp1 = desk[tmp2] ;   // opposite stones count
          desk[move<6 ? 6:13] += desk[i] + tmp1;
          desk[i] = desk[tmp2] = 0;
          if (bRealMove)
          {
            // save information about additional stones
              grabbedHoleIndex = tmp2;
              grabbedStonesCount = tmp1;
          }
       }

       if (move < 6 && i == 6 || move > 6 && i == 13) fin = true;
       return fin;
    }/*scatterStones*/

    private int findMove(treeNode p)
    {
       treeNode     pcurPos;
       treeNode     curPos = new treeNode();
       int          res = 0;
       int          t, m;

       cL = 0;
       pcurPos = first(p);                     /* get the first child           */

       if (pcurPos == null)
       {
          return 0;			       /* terminal position - exit	*/
       }
       else
       {
          m = -50 << 8;
          while (pcurPos != null)
          {
             cL = -1;                          /* "right before first" level state    */
             curPos.copyInstance(wStack[0]);   /* take position from stack */
             t = -ABprune(curPos, -50 << 8, 50 << 8);
             if ((t > m) || ((t == m) && (0 != _getRandomInt(4))))
             {
                m = t;
                bestMove.copyInstance(curPos);
                res++;
             }
             wStack[0].copyInstance(curPos); cL = 0;  /* save position to stack */
             pcurPos = next(p);                /*  and generate next one  */
          }
       }
       return res;
    }/*findMove*/

}
