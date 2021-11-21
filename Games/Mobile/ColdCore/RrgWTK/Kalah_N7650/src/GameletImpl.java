
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Kuligin
 * Date: 05.09.2003
 * Time: 11:18:14
 */
public class GameletImpl extends Gamelet
{
    public static final int  AI_LEVEL_SHIFT = 1;

    // STATES
    public static final int KALAH_STATE_PLAYER_SELECTING_HOLE = 1;
    public static final int KALAH_STATE_PLAYER_SELECTED_HOLE = 2;
    public static final int KALAH_STATE_PLAYER_ANIMATE_MOVE = 3;
    public static final int KALAH_STATE_AI_THINK = 4;
    public static final int KALAH_STATE_AI_SELECTED_HOLE = 5;
    public static final int KALAH_STATE_AI_ANIMATE_MOVE = 6;
    public static final int KALAH_STATE_GAME_FINISHED = 7;
    public static final int KALAH_STATE_GAME_OVER = 8;
    public static final int KALAH_STATE_GAME_FINISHING = 9;

    // STONE STATES
    public static final int STONE_STATE_FLY = 0;
    public static final int STONE_STATE_RISE = 1;
    public static final int STONE_STATE_FALL = 2;

    // Sizes
    public static final int FLY_BALL_WIDTH   = 0; //LayoutDefs.BALL_WIDTH + 4;
    public static final int FLY_BALL_HEIGHT  = 0; //LayoutDefs.BALL_HEIGHT + 4;

    public static final int RISE_BALL_WIDTH  = 0; //LayoutDefs.BALL_WIDTH;
    public static final int RISE_BALL_HEIGHT = 0; //LayoutDefs.BALL_HEIGHT;

    public static final int FALL_BALL_WIDTH  = 0; //LayoutDefs.BALL_WIDTH;
    public static final int FALL_BALL_HEIGHT = 0; //LayoutDefs.BALL_HEIGHT;

    public static final int SCALEFACTOR = 1;

    // GAME ACTIONS
    //======================================
    public static final int GAMEACTION_SND_PLAYERMOVE = 1;
    public static final int GAMEACTION_SND_PLAYERGRABSTONES = 2;
    public static final int GAMEACTION_SND_PLAYERWIN = 3;
    public static final int GAMEACTION_SND_PLAYERLOST = 4;
    public static final int GAMEACTION_SND_WRONGSELECTION = 5;
    public static final int GAMEACTION_SND_PLAYERGRABAISTONES = 6;
    public static final int GAMEACTION_SND_AIGRABPLAYERSTONES = 7;
    public static final int GAMEACTION_SND_SELECTIONMOVE = 8;
    public static final int GAMEACTION_SND_PLAYERTURN = 9;

    public static final int GAMEACTION_SHOW_CLOCK = 10;
    public static final int GAMEACTION_REMOVE_STONE = 11;
    public static final int GAMEACTION_PUT_STONE = 12;

    // Player driving keys
    //==============================
    public static final int PLAYERKEY_NONE = 0;
    public static final int PLAYERKEY_LEFT = 1;
    public static final int PLAYERKEY_RIGHT = 2;
    public static final int PLAYERKEY_UP = 4;
    public static final int PLAYERKEY_DOWN = 8;
    public static final int PLAYERKEY_SELECT = 16;

    // Game level definitions
    // The level numbers
    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;
    public static final int LEVEL0_TIMEDELAY = 50;
    public static final int LEVEL0_ATTEMPTIONS = 3;

    public static final int FLY_PATH_STEPS = 7;

    public static final int FLY_FALL_STEP = FLY_PATH_STEPS - 3;

    // Fly Stone definitions
    /*
    public static final int STONE_WIDTH  = 6;
    public static final int STONE_HEIGHT = 6;
    */
    public static final int STONE_FRAMES = 4;
    public static final int STONE_DELAY  = 2;

    ///////////////////////////////////////////////////////////////////////////
    ///   class variable
    public  int         i_PlayerKey;
    // should be set by caller to specify whos turn
    public boolean      lg_aiTurn;
    public int          i_selectedPosition;

    public int      i_currentState;

    private GameAI      aiGame;

    public Sprite       p_FlyStone;
    public boolean lg_FinalMoving;
    public boolean lg_InitFlyMode;
    public boolean lg_NotEmptyHole;
    public boolean lg_MoveToKalah;
    public int i_FlyStoneTime;
    public int i_LastHole;
    public int i_HandStones;
    public boolean lg_Gameturn;
    public int i_FlyIndex;

    public int             i_timer;
    public int             i_aiHole;

    public HoleStruct   hsHoles[];
    public int[][] StoneArray;
    public int[]   StonePath;


    public static final short[] s_KalahHolesX = new short[]
    {
      // user part
      LayoutDefs.X_POS_02, LayoutDefs.X_POS_03, LayoutDefs.X_POS_04, LayoutDefs.X_POS_05,
      LayoutDefs.X_POS_06, LayoutDefs.X_POS_07, LayoutDefs.X_POS_08,
      //  ai part
      LayoutDefs.X_POS_07, LayoutDefs.X_POS_06, LayoutDefs.X_POS_05, LayoutDefs.X_POS_04,
      LayoutDefs.X_POS_03, LayoutDefs.X_POS_02, LayoutDefs.X_POS_01
    };

    public static final int[] StoneCellCoords = new int[]
    {
        // 0
        1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 7
        7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 14
        3,5 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,4 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,3 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        3,2 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,1 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,0 * (LayoutDefs.BALL_HEIGHT + 1) + 3,
        // 20
        1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 27
        7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 34
        4,5 * (LayoutDefs.BALL_HEIGHT + 1) + 4 , 4,4 * (LayoutDefs.BALL_HEIGHT + 1) + 4 , 4,3 * (LayoutDefs.BALL_HEIGHT + 1) + 4 ,
        4,2 * (LayoutDefs.BALL_HEIGHT + 1) + 4 , 4,1 * (LayoutDefs.BALL_HEIGHT + 1) + 4 , 4,0 * (LayoutDefs.BALL_HEIGHT + 1) + 4,
        // 40
        1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 47
        7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 54
        3,5 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,4 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,3 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        3,2 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,1 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,0 * (LayoutDefs.BALL_HEIGHT + 1) + 3,
        // 60
        1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 67
        7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 74
    };

    public static final int[] KalahCellCoords = new int[]
    {
        //LEFT
        // 0
        1,17 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,16 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,15 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,14 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,13 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,12 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 6
        1,11 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,10 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,9 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,8 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,7 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 12
        1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        //RIGHT
        // 18
        7,17 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,16 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,15 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,14 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,13 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,12 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 24
        7,11 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,10 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,9 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,8 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,7 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 30
        7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        //CENTER
        // 36
        3,16 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,15 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,14 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        3,13 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,12 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,11 * (LayoutDefs.BALL_HEIGHT + 1) + 3,
        // 42
        3,10 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,9 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,8 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        3,7 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,6 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,5 * (LayoutDefs.BALL_HEIGHT + 1) + 3,
        // 48
        3,4 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,3 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,2 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        3,1 * (LayoutDefs.BALL_HEIGHT + 1) + 3 , 3,0 * (LayoutDefs.BALL_HEIGHT + 1) + 3 ,
        // 53

        //LEFT
        // 53
        1,17 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,16 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,15 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,14 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,13 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,12 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 59
        1,11 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,10 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,9 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,8 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,7 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 65
        1,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        1,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 1,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        //RIGHT
        // 71
        7,17 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,16 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,15 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,14 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,13 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,12 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 77
        7,11 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,10 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,9 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,8 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,7 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,6 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        // 83
        7,5 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,4 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,3 * (LayoutDefs.BALL_HEIGHT + 1) + 1 ,
        7,2 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,1 * (LayoutDefs.BALL_HEIGHT + 1) + 1 , 7,0 * (LayoutDefs.BALL_HEIGHT + 1) + 1
        // 88
    };

    /*
    public int[] StoneCounters = new int[]
    {
        6,6,6,6,6,6 ,0,
        6,6,6,6,6,6 ,0
    };
    */

    public void InitStoneArray()
    {
        int stone;
        int color;
        StoneArray = new int[14][72];
        for (stone = 0; stone < LayoutDefs.HOLES_COUNT; stone++)
        {
            for (color = 1; color < 9; color++)
            {
                StoneArray[stone][color] = color;
            }
            if (stone == GameAI.PLAYER_KALAH_INDEX || stone == GameAI.AI_KALAH_INDEX)
            {
                StoneArray[stone][0] = 0;
            }
            else
            {
                StoneArray[stone][0] = GameAI.INITIAL_STONES_COUNT;
            }
        }
    }

    public int GetStoneCount(int hole)
    {
        return StoneArray[hole][0];
    }

    public int GetStoneColor(int hole, int number)
    {
        int color = StoneArray[hole][++number];
        return color;
    }

    public void PutStoneColor(int hole, int number, int color)
    {
        StoneArray[hole][++number] = color;
    }


    public Point GetCoords(int Hole, int BallNumber)
    {
        Point coords = new Point(0,0);
        BallNumber <<= 1; // * 2

        if (Hole == GameAI.PLAYER_KALAH_INDEX || Hole == GameAI.AI_KALAH_INDEX)
        {
            coords.x = hsHoles[Hole].x0 + KalahCellCoords[BallNumber];
            BallNumber++;
            coords.y = hsHoles[Hole].y0 + KalahCellCoords[BallNumber];
        }
        else
        {
            coords.x = hsHoles[Hole].x0 + StoneCellCoords[BallNumber];
            BallNumber++;
            coords.y = hsHoles[Hole].y0 + StoneCellCoords[BallNumber];
        }
        return coords;
    }

    void UpdateGame()
    {
        switch (i_currentState)
        {
            case KALAH_STATE_GAME_FINISHING:
                lg_FinalMoving = true;

//                if(!p_FlyStone.lg_SpriteActive &&
//                   72 == StoneArray[GameAI.PLAYER_KALAH_INDEX][0] + StoneArray[GameAI.AI_KALAH_INDEX][0]
//                  )
                i_currentState = KALAH_STATE_GAME_FINISHED;
                break;
            case KALAH_STATE_GAME_FINISHED:
            {

               /*
                if ((i_PlayerKey & PLAYERKEY_SELECT) != 0)
                {
                  resumeGameAfterPlayerLost();
                  lg_aiTurn = false;
                  i_selectedPosition = 0;
                  i_currentState = KALAH_STATE_PLAYER_SELECTING_HOLE;
                }

                else if ((i_PlayerKey & PLAYERKEY_RIGHT) != 0)
                   i_currentState = KALAH_STATE_GAME_OVER;
               */

/*
               if ((i_PlayerKey & PLAYERKEY_SELECT) != 0)
               {
                  i_currentState = KALAH_STATE_GAME_OVER;

                  i_GameState = Gamelet.GAMESTATE_OVER;
                  if (StoneArray[GameAI.PLAYER_KALAH_INDEX][0]
                      >= StoneArray[GameAI.AI_KALAH_INDEX][0])
                        i_PlayerState = PLAYERSTATE_WON;
                    else
                         i_PlayerState = PLAYERSTATE_LOST;
               }
*/
            }; break;

            case KALAH_STATE_PLAYER_SELECTING_HOLE:
            {
                  if ((i_PlayerKey & PLAYERKEY_RIGHT) != 0)
                  {
                    i_selectedPosition++;
                    if(!lg_aiTurn) p_GameActionListener.gameAction(GAMEACTION_SND_SELECTIONMOVE);
                    if (i_selectedPosition >= LayoutDefs.ONE_SIDE_HOLES_COUNT)
                      i_selectedPosition = 0;
                  }
                  else if ((i_PlayerKey & PLAYERKEY_LEFT) != 0)
                  {
                      i_selectedPosition--;
                      if(!lg_aiTurn) p_GameActionListener.gameAction(GAMEACTION_SND_SELECTIONMOVE);
                      if (i_selectedPosition < 0)
                        i_selectedPosition = LayoutDefs.ONE_SIDE_HOLES_COUNT - 1;
                  }
                  else if ((i_PlayerKey & PLAYERKEY_SELECT) != 0)
                  {
                    if (isEmpltyHole(i_selectedPosition))
                    {
                      p_GameActionListener.gameAction(GAMEACTION_SND_WRONGSELECTION);
                      break;
                    }

                      ///aaa
                      lg_InitFlyMode = true;
                      //i_LastHole = i_selectedPosition;

                    i_currentState =  KALAH_STATE_PLAYER_SELECTED_HOLE;
                    p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERMOVE);

                  }

              }; break;

            case KALAH_STATE_PLAYER_ANIMATE_MOVE:
                {
                    if (lg_aiTurn)
                    {
                      i_currentState = KALAH_STATE_AI_THINK;
                    }
                    else
                    {
                      i_currentState = KALAH_STATE_PLAYER_SELECTING_HOLE;
                      //p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERTURN);
                    }
                };
                break;

            case KALAH_STATE_PLAYER_SELECTED_HOLE:
              {
                  //p_gamelet.nextGameStep(p_gamelet);
                  ////////////////////////////////////
                  if (lg_aiTurn)
                  {
                      aiGameStep();
                  }
                  else
                  {
                      int nRet = aiGame.PlayerStep(i_selectedPosition);
                      if (-1 == nRet)
                      {
                          //i_GameState = Gamelet.GAMESTATE_OVER;
                          i_currentState = KALAH_STATE_GAME_FINISHING;
                          break;
                      }
                      else if (0 == nRet)
                          lg_aiTurn = true;
                      else
                          lg_aiTurn = false;
                  }
                  ////////////////////////////////////
                  i_currentState = KALAH_STATE_PLAYER_ANIMATE_MOVE;
              };  break;

            case KALAH_STATE_AI_THINK:
              {
                i_aiHole = getAIMovement();
                i_currentState = KALAH_STATE_AI_SELECTED_HOLE;


                  //ccc
                  lg_InitFlyMode = true;
                  //i_LastHole = i_aiHole;

              }; break;

            case KALAH_STATE_AI_SELECTED_HOLE:
                {
                  i_currentState = KALAH_STATE_AI_ANIMATE_MOVE;

                };break;

            case KALAH_STATE_AI_ANIMATE_MOVE:
                {
                    if (!lg_aiTurn)
                    {
                      i_currentState = KALAH_STATE_PLAYER_SELECTING_HOLE;
                      p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERTURN);
                    }
                    else
                    {
                      i_currentState = KALAH_STATE_AI_THINK;
                    }

                }; break;

        } // end of switch(currentGameState)
    }

public GameletImpl(int _screenWidth, int _screenHeight, startup _gameActionListener, String _staticArrayResource)
    {
      super(_screenWidth, _screenHeight, _gameActionListener, _staticArrayResource);
      aiGame = new GameAI(this);
      aiGame.GlobalInitialize();
    }

    public void initStage(int _stage)
    {
       super.initStage(_stage);
    }

    public void _InitLayout()
    {
        int   i;
        for (i = 0; i < LayoutDefs.HOLES_COUNT; i++)
        {
            hsHoles[i].x0 = s_KalahHolesX[i];
            if (i >= 0 && i <= 5)
            {
              hsHoles[i].y0 = LayoutDefs.Y_POS_02;
            }
            else
              hsHoles[i].y0 = LayoutDefs.Y_POS_01;

            if (i == GameAI.PLAYER_KALAH_INDEX || i == GameAI.AI_KALAH_INDEX)
            {
                hsHoles[i].w = LayoutDefs.KALAH_WIDTH;
                hsHoles[i].h = LayoutDefs.KALAH_HEIGHT;
                hsHoles[i].stonesCount = 0;
            }
            else
            {
                hsHoles[i].w = LayoutDefs.HOLE_WIDTH;
                hsHoles[i].h = LayoutDefs.HOLE_HEIGHT;
                hsHoles[i].stonesCount = GameAI.INITIAL_STONES_COUNT;
            }
        }
    }

    public void newGameSession(int _level)
    {

      StonePath = new int[(FLY_PATH_STEPS +1) * 2];
      InitStoneArray();

      hsHoles = new HoleStruct[LayoutDefs.HOLES_COUNT];
      for (int i = 0; i < LayoutDefs.HOLES_COUNT; i++)
      {
            hsHoles[i] = new HoleStruct();
      }

      i_timer = 0;

      p_FlyStone = new Sprite(-1);
      _InitLayout();

      initLevel(_level);
      i_GameTimeDelay = LEVEL0_TIMEDELAY;

      aiGame.SetAILevel(_level * 2 + AI_LEVEL_SHIFT);
      aiGame.LevelInitialize();

      lg_aiTurn = false;
      lg_FinalMoving = false;
      lg_InitFlyMode = false;
      lg_MoveToKalah = false;
      lg_NotEmptyHole = false;
      i_selectedPosition = 0;

      i_currentState = KALAH_STATE_PLAYER_SELECTING_HOLE;


        resumeGameAfterPlayerLost();
    }

    public void endGameSession()
    {
        p_FlyStone = null;

        super.endGameSession();
        aiGame.LevelRelease();
        Runtime.getRuntime().gc();
    }

    public int  getStonesCountByIndex(int index)
    {
        return aiGame.getStonesCount(index);
    }

    public boolean isEmpltyHole(int index)
    {
      return aiGame.isEmptyHole(index);
    }

    public void resumeGameAfterPlayerLost()
    {
        super.resumeGameAfterPlayerLost();
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;

        aiGame.LevelRestart();
    }

    public int getAIMovement()
    {
        int iHole, nRet;

        if ( aiGame.isAIMoveStackEmpty() )
        {
          aiGameStep();
        }

        iHole =  aiGame.getCurrentAIMove();
        nRet = aiGame.executeCurrentAIMove();
        if (nRet < 0)
        {
          //i_GameState = GAMESTATE_OVER;
          i_currentState = KALAH_STATE_GAME_FINISHING;
          lg_FinalMoving = true;
        }
        else if (nRet == 0)
        {
          lg_aiTurn = false;
        }
        else
          lg_aiTurn = true;

        return iHole; //0;
    }

    private void aiGameStep()
    {
      if (i_GameState != GAMESTATE_OVER)
      {
          if (!aiGame.AIStep())
          {
            //i_GameState = GAMESTATE_OVER;
            lg_FinalMoving = true;
            i_currentState = KALAH_STATE_GAME_FINISHING;
          }
      }
    }

    public void nextGameStep(Object _playermoveobject)
    {
        switch(i_currentState)
        {
            case KALAH_STATE_PLAYER_SELECTING_HOLE:
            case KALAH_STATE_PLAYER_SELECTED_HOLE:
            case KALAH_STATE_PLAYER_ANIMATE_MOVE:
                lg_Gameturn = true;
                break;
            case KALAH_STATE_AI_THINK:
            case KALAH_STATE_AI_SELECTED_HOLE:
            case KALAH_STATE_AI_ANIMATE_MOVE:
                lg_Gameturn = false;
                break;
            case KALAH_STATE_GAME_FINISHING:
            case KALAH_STATE_GAME_FINISHED:
            case KALAH_STATE_GAME_OVER:
            default:
                break;
        }


        if (lg_NotEmptyHole)
        {
            if (p_FlyStone.lg_SpriteActive)
            {
                processFlyStone();
            }
            else
            {

                lg_NotEmptyHole = MakeFlyStone();
            }
        }
        else
        {
            if (lg_InitFlyMode)
            {
                i_LastHole = i_selectedPosition;
                int index = i_selectedPosition;
                if (!lg_Gameturn)
                {
                    i_LastHole = i_aiHole;
                    index = i_aiHole;
                }
                i_HandStones = StoneArray[index][0];
                lg_NotEmptyHole = true;
                lg_InitFlyMode = false;
            }
            else
            {
                if (lg_FinalMoving)
                {
                    if (!FinalStoneFlying())
                    {
                        lg_FinalMoving = false;
                        i_GameState = GAMESTATE_OVER;
                        if (StoneArray[GameAI.PLAYER_KALAH_INDEX][0]
                            >= StoneArray[GameAI.AI_KALAH_INDEX][0])
                              i_PlayerState = PLAYERSTATE_WON;
                          else
                               i_PlayerState = PLAYERSTATE_LOST;

                    }
                }
                UpdateGame();
            }
        }

        i_timer++;
    }

    public int getPlayerScore()
    {
        return aiGame.getTotalPlayerStones();
    }

    public int getAIScore()
    {
        return aiGame.getTotalAIStones();
    }

    public boolean MakeFlyStone()
    {
        int index = i_selectedPosition;
        if (!lg_Gameturn) index = i_aiHole;

        if (i_HandStones > 0)
        {
            //If stones are present
            i_LastHole++;
            if (i_LastHole >= LayoutDefs.HOLES_COUNT) i_LastHole = 0;
            // Whose turn?
            if (lg_Gameturn) { if (i_LastHole == GameAI.AI_KALAH_INDEX)     i_LastHole = 0; }
            else             { if (i_LastHole == GameAI.PLAYER_KALAH_INDEX) i_LastHole++;   }
            //
            i_HandStones--;
            StoneArray[index][0]--;
            MakeFlyStoneSprite(index, i_LastHole, i_HandStones);
            p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, index);
            return true;
        }
        else
        {
            //If there are no balls in the hole
            int ballNumber = StoneArray[i_LastHole][0]; /* Look at last hole */
            if (ballNumber == 1) /* Is there only one ball? */
            {
                //Look at opposite side
                if (lg_Gameturn)
                {
                    //Player
                    if (i_LastHole >= 0 && i_LastHole <= 5)
                    {
                        int newindex = 12 - i_LastHole;
                        ballNumber = StoneArray[newindex][0];
                        if (ballNumber != 0)
                        {
                            lg_MoveToKalah = true;
                            ballNumber--;
                            StoneArray[newindex][0]--;
                            MakeFlyStoneSprite(newindex, GameAI.PLAYER_KALAH_INDEX, ballNumber);
                            p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, newindex);
                            return true;
                        }
                        else
                        {
                            if (lg_MoveToKalah)
                            {
                                lg_MoveToKalah = false;
                                newindex = i_LastHole;
                                ballNumber = 0; // first
                                StoneArray[newindex][0]--;
                                MakeFlyStoneSprite(newindex, GameAI.PLAYER_KALAH_INDEX, ballNumber);
                                p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, newindex);
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                }
                else
                {
                    //Computer
                    if (i_LastHole >= 7 && i_LastHole <= 12)
                    {
                        int newindex = 5 - (i_LastHole - 7);
                        ballNumber = StoneArray[newindex][0];
                        if (ballNumber != 0)
                        {
                            lg_MoveToKalah = true;
                            ballNumber--;
                            StoneArray[newindex][0]--;
                            MakeFlyStoneSprite(newindex, GameAI.AI_KALAH_INDEX, ballNumber);
                            p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, newindex);
                            return true;
                        }
                        else
                        {
                            if (lg_MoveToKalah)
                            {
                                lg_MoveToKalah = false;
                                newindex = i_LastHole;
                                ballNumber = 0; // first
                                StoneArray[newindex][0]--;
                                MakeFlyStoneSprite(newindex, GameAI.AI_KALAH_INDEX, ballNumber);
                                p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, newindex);
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else
            {
                lg_InitFlyMode = false;
            }
        }
        return false;
    }

    public void MakeFlyStoneSprite(int srcindex, int destindex, int ballNumber)
    {
            Point Coords = new Point();
            Coords = GetCoords(srcindex, ballNumber);

            int i_color = GetStoneColor(srcindex,ballNumber);

            int i_stoneX = Coords.x;
            int i_stoneY = Coords.y;

            ballNumber = StoneArray[destindex][0];

            Coords = GetCoords(destindex, ballNumber);

            int i_destX = Coords.x;
            int i_destY = Coords.y;

            /*
            int i_wdth = LayoutDefs.BALL_WIDTH;
            int i_hght = LayoutDefs.BALL_HEIGHT;
            int i_offx = 0;
            int i_offy = 0;
            int i_offwdth = LayoutDefs.BALL_WIDTH;
            int i_offhght = LayoutDefs.BALL_HEIGHT;
            int i_frames = STONE_FRAMES;
            int i_delay = STONE_DELAY;
            int i_animtype = Sprite.ANIMATION_FROZEN;
            int i_anchor = 0;

            p_FlyStone.setAnimation(i_animtype, i_anchor, i_wdth, i_hght, i_frames, 0, i_delay);
            p_FlyStone.setCollisionBounds(i_offx, i_offy, i_offwdth, i_offhght);
            p_FlyStone.lg_SpriteActive = true;
            */
            p_FlyStone.setMainPointXY(i_stoneX, i_stoneY);
            ChangeState(p_FlyStone, STONE_STATE_RISE);
            p_FlyStone.i_DestX = i_destX;
            p_FlyStone.i_DestY = i_destY;
            p_FlyStone.i_DestHole = destindex;
            p_FlyStone.i_Color = i_color;

            i_FlyStoneTime = i_timer;

            BuildStonePath(i_stoneX, i_stoneY, i_destX, i_destY);

    }

    public void ChangeState(Sprite _sprite, int _state)
    {
               int i_offset = _state * 10;
               // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
               int i_wdth = ai_SpriteDefinitionArray[i_offset++];
               int i_hght = ai_SpriteDefinitionArray[i_offset++];
               int i_offx = ai_SpriteDefinitionArray[i_offset++];
               int i_offy = ai_SpriteDefinitionArray[i_offset++];
               int i_offwdth = ai_SpriteDefinitionArray[i_offset++];
               int i_offhght = ai_SpriteDefinitionArray[i_offset++];
               int i_frames = ai_SpriteDefinitionArray[i_offset++];
               int i_delay = ai_SpriteDefinitionArray[i_offset++];
               int i_animtype = ai_SpriteDefinitionArray[i_offset++];
               int i_anchor = ai_SpriteDefinitionArray[i_offset];

               _sprite.setAnimation(i_animtype, i_anchor, i_wdth, i_hght, i_frames, 0, i_delay);
               _sprite.setCollisionBounds(i_offx, i_offy, i_offwdth, i_offhght);
               _sprite.lg_SpriteActive = true;

               _sprite.i_ObjectState = _state;
    }

//Sprite defenition array
    public static final short[] ai_SpriteDefinitionArray = new short[]
    {
        // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
        //
        // FLY
        (FLY_BALL_WIDTH) * SCALEFACTOR,   (FLY_BALL_HEIGHT) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (FLY_BALL_WIDTH) * SCALEFACTOR, (FLY_BALL_HEIGHT) * SCALEFACTOR,    2, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        // RISE
        (RISE_BALL_WIDTH) * SCALEFACTOR,   (RISE_BALL_HEIGHT) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RISE_BALL_WIDTH) * SCALEFACTOR, (RISE_BALL_HEIGHT) * SCALEFACTOR,    1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        // FALL
        (FALL_BALL_WIDTH) * SCALEFACTOR,   (FALL_BALL_HEIGHT) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (FALL_BALL_WIDTH) * SCALEFACTOR, (FALL_BALL_HEIGHT) * SCALEFACTOR,    2, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
    };


    public boolean processFlyStone()
    {
        int i_stoneX = p_FlyStone.i_mainX;
        int i_stoneY = p_FlyStone.i_mainY;
        Point Coords = new Point();
        boolean lg_animationCompleted = p_FlyStone.processAnimation();
        if (lg_animationCompleted)
        {
            if (p_FlyStone.i_ObjectState == STONE_STATE_RISE) ChangeState(p_FlyStone, STONE_STATE_FLY);
            if (p_FlyStone.i_ObjectState == STONE_STATE_RISE && i_FlyIndex == FLY_FALL_STEP) ChangeState(p_FlyStone, STONE_STATE_FALL);
        }

        if (i_stoneX == p_FlyStone.i_DestX && i_stoneY == p_FlyStone.i_DestY)
        {
            int color = p_FlyStone.i_Color;
            int hole = p_FlyStone.i_DestHole;
            int pos = StoneArray[hole][0];
            PutStoneColor(hole, pos, color);
            StoneArray[hole][0]++;
            p_FlyStone.lg_SpriteActive = false;
            p_GameActionListener.gameAction(GAMEACTION_PUT_STONE, hole);
        }
         else
         {
              Coords = GetStonePathPos(i_FlyIndex);
              i_stoneX = Coords.x;
              i_stoneY = Coords.y;
              i_FlyIndex++;

              p_FlyStone.setMainPointXY(i_stoneX, i_stoneY);
         }

        return false;
    }

    public void BuildStonePath(int x1, int y1, int x2, int y2)
    {
        int frames = FLY_PATH_STEPS;
        int i = 0;
        int p = 0;
        int deltaX;
        int deltaY;
        int stepX;
        int stepY;
        int ix;
        int iy;

        deltaX = (x2 - x1) << 8;
        deltaY = (y2 - y1) << 8;

        ix = x1 << 8;
        iy = y1 << 8;

        stepX = deltaX / frames;
        stepY = deltaY / frames;

        while(p<frames * 2)
        {
            StonePath[p++] = ix >> 8;
            StonePath[p++] = iy >> 8;
            //
            ix += stepX;
            iy += stepY;
        }
        StonePath[p++] = x2;
        StonePath[p++] = y2;

        i_FlyIndex = 0;
    }

    public Point GetStonePathPos(int i)
    {
        i <<= 1;
        Point Coords = new Point();
        Coords.x = StonePath[i];
        i++;
        Coords.y = StonePath[i];
        return Coords;
    }

    public boolean FinalStoneFlying()
    {
        int AllStones = 0;
        if (p_FlyStone.lg_SpriteActive)
        {
            processFlyStone();
            return true;
        }
        else
        {
            int i;
            int destination;
            if (lg_Gameturn)
            {
                //Player
                for (i = 0; i < LayoutDefs.HOLES_COUNT; i++)
                {
                    if (i == GameAI.PLAYER_KALAH_INDEX || i == GameAI.AI_KALAH_INDEX) continue;
                    AllStones += StoneArray[i][0];
                    if (i < GameAI.PLAYER_KALAH_INDEX)
                    {
                        destination = GameAI.PLAYER_KALAH_INDEX;
                    }
                    else
                    {
                        destination = GameAI.AI_KALAH_INDEX;
                    }
                    if (DoEmptyFlyStone(i, destination)) return true;
                }
            }
            else
            {
                //Computer
                for (i = LayoutDefs.HOLES_COUNT - 1; i >= 0 ; i--)
                {
                    if (i == GameAI.PLAYER_KALAH_INDEX || i == GameAI.AI_KALAH_INDEX) continue;
                    AllStones += StoneArray[i][0];
                    if (i < GameAI.PLAYER_KALAH_INDEX)
                    {
                        destination = GameAI.PLAYER_KALAH_INDEX;
                    }
                    else
                    {
                        destination = GameAI.AI_KALAH_INDEX;
                    }
                    if (DoEmptyFlyStone(i, destination)) return true;
                }
            }
        }
        return false;
    }

    public boolean DoEmptyFlyStone(int index, int kalah)
    {
        if (StoneArray[index][0] > 0)
        {
            StoneArray[index][0]--;
            MakeFlyStoneSprite(index, kalah, StoneArray[index][0]);
            p_GameActionListener.gameAction(GAMEACTION_REMOVE_STONE, index);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void writeTreeNode(DataOutputStream _dataOutputStream, treeNode node) throws IOException
    {
        int i;
        _dataOutputStream.writeByte(node.extent);
        for(i = 0; i < node.move.length; i++)
           _dataOutputStream.writeByte(node.move[i]);
        for(i = 0; i < node.desk.length; i++)
           _dataOutputStream.writeByte(node.desk[i]);
    }

    private void readTreeNode(DataInputStream _dataInputStream, treeNode node) throws IOException
    {
        int i;
        node.extent = _dataInputStream.readByte();
        for(i = 0; i < node.move.length; i++)
           node.move[i] = _dataInputStream.readByte();
        for(i = 0; i < node.desk.length; i++)
           node.desk[i] = _dataInputStream.readByte();
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
      // we need to save bestMove, rootPos, aiTurn, aiUpdateVar, aiLevel, selectedPosition,
      // playerkeySet, maxLevel, cl and wStack
      // StoneArray

        int i,j,k;

        writeTreeNode(_dataOutputStream, aiGame.rootPos);
        writeTreeNode(_dataOutputStream, aiGame.root);

        _dataOutputStream.writeBoolean(lg_aiTurn);
        _dataOutputStream.writeByte(i_selectedPosition);
        _dataOutputStream.writeByte(i_currentState);

        _dataOutputStream.writeBoolean(lg_FinalMoving);
        _dataOutputStream.writeBoolean(lg_InitFlyMode);
        _dataOutputStream.writeBoolean(lg_NotEmptyHole);
        _dataOutputStream.writeBoolean(lg_MoveToKalah);

        _dataOutputStream.writeByte(p_FlyStone.i_ObjectState);
        p_FlyStone.writeSpriteToStream(_dataOutputStream);

        _dataOutputStream.writeInt(i_FlyStoneTime);
        _dataOutputStream.writeByte(i_LastHole);
        _dataOutputStream.writeByte(i_HandStones);
        _dataOutputStream.writeBoolean(lg_Gameturn);
        _dataOutputStream.writeInt(i_FlyIndex);

        _dataOutputStream.writeInt(i_timer);
        _dataOutputStream.writeByte(i_aiHole);

        for(i = 0; i < StonePath.length; i++)
        {
           _dataOutputStream.writeShort(StonePath[i]);
        }
        for(i = 0; i < StoneArray.length; i++)
        {
           k = StoneArray[i][0];
           _dataOutputStream.writeByte(k);
           for(j = 0; j < k; j++)
           {
              _dataOutputStream.writeByte(StoneArray[i][j+1]);
           }
        }
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        int i,j,k;

        readTreeNode(_dataInputStream, aiGame.rootPos);
        readTreeNode(_dataInputStream, aiGame.root);


        lg_aiTurn           = _dataInputStream.readBoolean();
        i_selectedPosition = _dataInputStream.readByte();
        i_currentState     = _dataInputStream.readByte();

        lg_FinalMoving     = _dataInputStream.readBoolean();
        lg_InitFlyMode     = _dataInputStream.readBoolean();
        lg_NotEmptyHole    = _dataInputStream.readBoolean();
        lg_MoveToKalah     = _dataInputStream.readBoolean();

        ChangeState(p_FlyStone, _dataInputStream.readUnsignedByte());
        p_FlyStone.readSpriteFromStream(_dataInputStream);

        i_FlyStoneTime     = _dataInputStream.readInt();
        i_LastHole         = _dataInputStream.readByte();
        i_HandStones       = _dataInputStream.readByte();
        lg_Gameturn        = _dataInputStream.readBoolean();
        i_FlyIndex         = _dataInputStream.readInt();

        i_timer            = _dataInputStream.readInt();
        i_aiHole           = _dataInputStream.readByte();

        for(i = 0; i < StonePath.length; i++)
        {
           StonePath[i] = _dataInputStream.readShort();
        }
        for(i = 0; i < StoneArray.length; i++)
        {
           k = _dataInputStream.readByte();
           StoneArray[i][0] = k;
           hsHoles[i].stonesCount = k;

           for(j = 0; j < k; j++)
           {
              StoneArray[i][j+1] = _dataInputStream.readByte();
           }
        }



    }

    public String getGameID()
    {
        return "KALAH";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 512;
    }


}
