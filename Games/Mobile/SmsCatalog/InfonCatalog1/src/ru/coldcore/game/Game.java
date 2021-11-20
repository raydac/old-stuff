//#excludeif GAMEMODE==""
package ru.coldcore.game;

import javax.microedition.lcdui.*;
import java.util.Random;

public abstract class Game
{
    protected static final int IDEALWIDTH = 100;
    protected static final int IDEALHEIGHT = 100;

    public static final int GAMEDATAMAXLEN = 64;

    /**
     * Код последней нажатой кнопки
     */
    private static int i_PressedKey;

    /**
     * Состояние игры
     */
    protected static int i_GameState;

    /**
     * Указатель на текущий объект игры
     */
    private static Game p_CurrentGame;

    /**
     * Инициализация игры
     * @return true если успешно иначе false
     */
    public static final boolean initNewGame(Canvas _canvas,int _numberColors,byte[] _gameData,String _gameClass)
    {
       if (_gameClass==null) return false;
       int i_canvasW = _canvas.getWidth();
       int i_canvasH = _canvas.getHeight();
       int i8_CoeffW = (i_canvasW<<8)/IDEALWIDTH;
       int i8_CoeffH = (i_canvasH<<8)/IDEALHEIGHT;

       i_PressedKey = 0;

        try
        {
            p_CurrentGame = (Game)(Class.forName("gm_"+_gameClass)).newInstance();

            if (!p_CurrentGame.initGameSession(i_canvasW,i_canvasH,i8_CoeffW,i8_CoeffH,_numberColors,_gameData))
            {
                p_CurrentGame = null;
                return false;
            }

            return true;
        }
        catch (Throwable e)
        {
            p_CurrentGame = null;
            return false;
        }
    }

    public abstract boolean initGameSession(int _canvasWidth,int _canvasHeight,int _i8coeffWidth,int _i8coeffHeight,int _colorsNumber,byte [] _gameData);
    public abstract byte[] releaseGameSession();
    public abstract boolean processGameSessionStep(int _pressedKey);
    public abstract void paintGameSessionField(Graphics _g);
    public abstract void restartGameSession();

    public static final void keyPressed(int _key)
    {
        i_PressedKey = _key;
    }

    /**
     * Генератор случайных чисел
     */
    private static Random p_RNDGenerator = new Random(System.currentTimeMillis());

    /**
     * Функция генерирует и возвращает псевдослучайное числовое значение в заданном пределе (включительно).
     *
     * @param _limit предел генерируемого числового значения (включительно)
     * @return сгенерированное псевдослучайное значение в заданном пределе.
     */
    protected static int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_RNDGenerator.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    public static boolean processStep()
    {
        if (p_CurrentGame!=null)
        {
            return p_CurrentGame.processGameSessionStep(i_PressedKey);
        }
        else
            return false;
    }

    public static byte [] releaseGame()
    {
        byte [] ab_result = null;
        if (p_CurrentGame!=null)
        {
            ab_result = p_CurrentGame.releaseGameSession();
        }
        p_CurrentGame = null;
        return ab_result;
    }

    public static void restartGame()
    {
        if (p_CurrentGame!=null) p_CurrentGame.restartGameSession();
    }

    public static void keyReleased(int _key)
    {
         i_PressedKey = 0;
    }

    public static void paint(Graphics _g)
    {
        if (p_CurrentGame!=null) p_CurrentGame.paintGameSessionField(_g);
    }

    public static int getTimedelay()
    {
        return 80;
    }

    public static boolean hasGame()
    {
        return true;
    }
}
