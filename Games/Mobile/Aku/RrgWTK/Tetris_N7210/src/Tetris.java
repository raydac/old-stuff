
import com.igormaznitsa.gameapi.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class Tetris extends Gamelet
{
    int W;
    int H;
    int gridW = 12; // кол. клеток по x
    int gridH = 18; // кол. клеток по y
    int DEFSPEED = 7;

    int cursepeed;
    int speed;

    int figure[][] = new int[3][3];
    int figX;
    int figY;

    int pole[][] = new int[gridW][gridH];

    boolean redrawPole = false;
    boolean figurefixed = false;

    int score;

    public Tetris(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        W = _screenWidth;
        H = _screenHeight;
        newGameSession(0);
    }

    public void newGameSession(int _level)
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        cursepeed = DEFSPEED;
        speed = cursepeed;
        clearPole();
        generateFigure();
    }

    public void clearPole()
    {
        figX = gridW/2-1;
        figY = 0;
        for(int y=0;y<3;y++)
            for(int x=0;x<3;x++)
                figure[x][y] = 0;
        for(int y=0;y<gridH;y++)
            for(int x=0;x<gridW;x++)
                pole[x][y] = 0;
    }

    public void nextGameStep(int _playermoveobject){}

    public void nextGameStep(Object _playermoveobject)
    {
        game_PMR keys = (game_PMR) _playermoveobject;
        switch (keys.i_Value)
        {
            case game_PMR.BUTTON_LEFT:
                {
                    moveFigureLeft();
                }
                break;
            case game_PMR.BUTTON_RIGHT:
                {
                    moveFigureRight();
                }
                break;
            case game_PMR.BUTTON_UP:
                {
                    rotateFigure(1);
                }
                break;
            case game_PMR.BUTTON_DOWN:
                {
                    moveFigureDown();
                }
                break;
        }
      gameAction();
    }


    public void gameAction()
    {
        if(i_PlayerState != GAMESTATE_PLAYED) return;
        speed--;
        if(speed <= 0)
        {
            moveFigureDown();
            speed = cursepeed;
        }
        if(score >= ((Math.abs(cursepeed-DEFSPEED))+1)*70 && cursepeed > 0)
            cursepeed--;
    }

    public void moveFigureDown()
    {
        if(figY < gridH-3 || (figY == gridH-3 && !checkRow(2,figure)))
        {
         if(checkFigureAtPole(figX,figY+1,figure))
            figY++;
         else
         {
             copyFigureToPole();
             if(figY == 0)
             {
                 i_PlayerState = PLAYERSTATE_LOST;
                 i_GameState = GAMESTATE_OVER;
                 return;
             }
             generateFigure();
         }
        }
        else
        {
            copyFigureToPole();
            generateFigure();
        }
    }

    public void copyFigureToPole()
    {
        // Копируем фигуру
        for(int y=0;y<3;y++)
            for(int x=0;x<3;x++)
                if(figure[x][y] !=0)
                    pole[figX+x][figY+y] = figure[x][y];
        // Проверяем поле на собранные линии
        for(int y=0;y<gridH;y++)
        {
            boolean rowcomplete = true;
            for(int x=0;x<gridW;x++)
            {
                if(pole[x][y] == 0)
                {
                    rowcomplete = false;
                    break;
                }
            }
            if(rowcomplete)
                pushRowDown(y);
        }
      redrawPole = true;
      figurefixed = true;
    }

    public void pushRowDown(int cury)
    {
        for(int y=cury;y>0;y--)
            for(int x=0;x<gridW;x++)
            {
                pole[x][y] = pole[x][y-1];
            }
        score+=7;
    }

    public void generateFigure()
    {
        figX = gridW/2-1;
        figY = 0;

        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                figure[i][j] = 0;
        int nm = 1+getRandomInt(4);
        int fg = 1;
        fg = getRandomInt(6);
        switch(fg)
        {
            case 0:
                figure[0][1] = nm;  // ###
                figure[1][1] = nm;
                figure[2][1] = nm;
                break;
            case 1:
                figure[0][1] = nm;  //  #
                figure[1][1] = nm;  // ###
                figure[2][1] = nm;
                figure[1][0] = nm;
                break;
            case 2:
                figure[0][1] = nm;  // #
                figure[1][1] = nm;  // ###
                figure[2][1] = nm;
                figure[0][0] = nm;
                break;
            case 3:
                figure[0][1] = nm;  //   #
                figure[1][1] = nm;  // ###
                figure[2][1] = nm;
                figure[2][0] = nm;
                break;
            case 4:
                for(int i=0;i<2;i++)      // ##
                    for(int j=0;j<2;j++)  // ##
                        figure[i][j] = 1; //
                break;
            case 5:
                figure[0][0] = nm;  // #
                figure[0][1] = nm;  // ##
                figure[1][1] = nm;  //  #
                figure[1][2] = nm;
                break;
            case 6:
                figure[1][0] = nm;  //  #
                figure[0][1] = nm;  // ##
                figure[1][1] = nm;  // #
                figure[0][2] = nm;
                break;
        }
    }

    public void rotateFigure(int dir) // 0 - left , 1 - right
    {
        int tfigure[][] = new int[3][3];
        if(dir == 1)
        {
            for(int i=0;i<3;i++)
            {
                tfigure[2][i] = figure[i][0];
                tfigure[1][i] = figure[i][1];
                tfigure[0][i] = figure[i][2];
            }

        }
        else
        {
            for(int i=0;i<3;i++)
            {
                tfigure[0][2-i] = figure[i][0];
                tfigure[1][2-i] = figure[i][1];
                tfigure[2][2-i] = figure[i][2];
            }

        }
        boolean acc = true;
        if(figX < 0 && !checkColumn(0,figure))
            acc = false;
        if(figX > gridW-3 && !checkColumn(2,figure))
            acc = false;
        if(acc)
            figure = tfigure;
    }

    public void moveFigureLeft()
    {
        if(!checkFigureAtPole(figX-1,figY,figure))
            return;
        if(figX > 0)
        {
            figX--;
            return;
        }
        if(figX == 0)
        {
          if(!checkColumn(0,figure))
              figX--;
        }
    }

    public void moveFigureRight()
    {
        if(!checkFigureAtPole(figX+1,figY,figure))
            return;
        if(figX < gridW-3)
        {
            figX++;
            return;
        }
        if(figX == gridW-3)
        {
            if(!checkColumn(2,figure))
                figX++;
        }
    }

    public boolean checkFigureAtPole(int fx,int fy , int fg[][])
    {
        boolean ret = true;
        for(int y=0;y<3;y++)
            for(int x=0;x<3;x++)
            {
                if(fx+x >=0 && fx+x < gridW && fy+y >=0 && fy+y < gridH)
                    if(figure[x][y] !=0 && pole[fx+x][fy+y]!=0)
                        ret =  false;
            }
        return ret;
    }

    public boolean checkColumn(int col,int fg[][])
    {
        boolean ret = false;
        for(int i=0;i<3;i++)
         if(fg[col][i] !=0)
         {
            ret = true;
             break;
         }
        return ret;
    }

    public boolean checkRow(int row,int fg[][])
    {
        boolean ret = false;
        for(int i=0;i<3;i++)
         if(fg[i][row] !=0)
         {
            ret = true;
             break;
         }
        return ret;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
      _dataOutputStream.writeInt(cursepeed);
      _dataOutputStream.writeInt(speed);
      _dataOutputStream.writeInt(figX);
      _dataOutputStream.writeInt(figY);
      _dataOutputStream.writeInt(score);

      for(int i=0;i<3;i++)
        for(int j=0;j<3;j++)
          _dataOutputStream.writeInt(figure[j][i]);

      for(int i=0;i<gridH;i++)
        for(int j=0;j<gridW;j++)
          _dataOutputStream.writeInt(pole[j][i]);

      _dataOutputStream.writeBoolean(redrawPole);
      _dataOutputStream.writeBoolean(figurefixed);

    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
      cursepeed = _dataInputStream.readInt();
      speed = _dataInputStream.readInt();
      figX = _dataInputStream.readInt();
      figY = _dataInputStream.readInt();
      score = _dataInputStream.readInt();

      for(int i=0;i<3;i++)
        for(int j=0;j<3;j++)
          figure[j][i] = _dataInputStream.readInt();

      for(int i=0;i<gridH;i++)
        for(int j=0;j<gridW;j++)
          pole[j][i] = _dataInputStream.readInt();

      redrawPole = _dataInputStream.readBoolean();
      figurefixed = _dataInputStream.readBoolean();
      redrawPole = true;
    }

    public String getGameID()
    {
        return "tetris0ZUID";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 0;
    }

    public int getPlayerScore()
    {
      i_PlayerScore = score;
      return i_PlayerScore;
    }

}
