package com.igormaznitsa.gameapi;

import java.io.*;
import java.util.Random;
import com.GameArea;
/**
 * This interface descrtibes a strategic game block. A game alghoritm must be implemented in this place.
 */
public abstract class StrategicBlock extends Random
{
//=======================Useful functions====================
    /**
     * Decompressing a byte array with RLE
     * @param inarray incomming packed byte array
     * @param dstlen length of outgoing bayte array
     * @return unpacked byte array
     */
    public static final byte[] RLEdecompress(byte[] inarray,int dstlen)
    {
        Runtime.getRuntime().gc();
        byte[] out = new byte[dstlen];
        int indx = 0;
        int outindx = 0;
        while (indx < inarray.length)
        {
            int val = inarray[indx++] & 0xFF;
            int counter = 1;
            int value = 0;
            if ((val & 0xC0) == 0xC0)
            {
                counter = val & 0x3F;
                value = inarray[indx++] & 0xFF;
            }
            else
            {
                value = val;
            }

            while (counter != 0)
            {
                out[outindx++] = (byte)value;
                counter--;
            }
        }
        Runtime.getRuntime().gc();
        return out;
    }

    /**
     * Compressing a byte array with RLE
     * @param inarray incoming byte array
     * @return packed byte array
     */
    public static final byte[] RLEcompress(byte[] inarray)
    {
        Runtime.getRuntime().gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(inarray.length);

        int inindx = 0;
        while (inindx < inarray.length)
        {
            int value = inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < inarray.length)
            {
                if ((inarray[inindx] & 0xFF) == value)
                {
                    count++;
                    inindx++;
                    if (count == 63) break;
                }
                else
                    break;
            }
            if (count > 1)
            {
                baos.write(count | 0xC0);
                baos.write(value);
            }
            else
            {
                if (value > 63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte[] outarray = baos.toByteArray();
        baos = null;
        Runtime.getRuntime().gc();
        return outarray;
    }

    /**
     * Square root
     * @param x
     * @return int value
     */
    public static int sqr(int x)
    {
        int bx = x;
        int ax = 1,di = 2,cx = 0,dx = 0;
        while (cx <= bx)
        {
            cx += ax;
            ax += di;
            dx++;
        }
        return dx - 1;
    }


    public final static int[] sineTable = new int[]{0,25,50,74,98,121,142,162,181,198,213,226,237,245,251,255,256,255,251,245,237,226,213,198,181,162,142,121,98,74,50,25,0,-25,-50,-74,-98,-121,-142,-162,-181,-198,-213,-226,-237,-245,-251,-255,-256,-255,-251,-245,-237,-226,-213,-198,-181,-162,-142,-121,-98,-74,-50,-25};

    /**
     * Converting radian value to 64 based angle value
     * @param _radians radian value in I16 format;
     * @return
     */
    public int convertRadiansTo64BasedAngle(int _radians)
    {
        return (_radians / 6536);
    }

    /**
     * Calculating of sin with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSine(int x,int index)
    {
        return (x * sineTable[index]) >> 8;
    }

    /**
     * Calculating of cos with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSine(int x,int index)
    {
        index += 16;
        return (x * sineTable[index & 63]) >> 8;
    }

    /**
     * Calculating of sin with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSineFloat(int x,int index)
    {
        return (x * sineTable[index]);
    }

    /**
     * Calculating of cos with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSineFloat(int x,int index)
    {
        index += 16;
        return (x * sineTable[index & 63]);
    }
//===========================================================

    /**
     * Game state block for current game
     */
    public GameStateBlock p_gameStateBlock;

    /**
     * Width of the output screen
     */
    public int i_screenWidth;

    /**
     * Height of the output screen
     */
    public int i_screenHeight;

    /**
     * The link to a game action listener
     */
    public GameArea p_gameActionListener;

    /**
     * Load a game state from an input stream
     * @param _dataInputStream
     * @throws java.io.IOException
     */
    public void loadGameState(DataInputStream _dataInputStream) throws IOException
    {
        Runtime.getRuntime().gc();

        int _level = _dataInputStream.readUnsignedByte();
        newGameSession(_level);

        int _stage = _dataInputStream.readUnsignedByte();
        p_gameStateBlock.initStage(_stage);

        p_gameStateBlock.i_playerScore = _dataInputStream.readInt();
        p_gameStateBlock.i_aiScore = _dataInputStream.readInt();
        p_gameStateBlock.i_playerState = _dataInputStream.readUnsignedByte();
        p_gameStateBlock.i_aiState = _dataInputStream.readByte();

        p_gameStateBlock.readFromStream(_dataInputStream);

        _dataInputStream = null;
        Runtime.getRuntime().gc();
    }

    /**
     * Save current game state to an output stream
     * @param _dataOutputStream
     * @throws java.io.IOException
     */
    public void saveGameState(DataOutputStream _dataOutputStream) throws IOException
    {
        Runtime.getRuntime().gc();

        _dataOutputStream.writeByte(p_gameStateBlock.i_gameLevel);

        _dataOutputStream.writeByte(p_gameStateBlock.i_gameStage);

        _dataOutputStream.writeInt(p_gameStateBlock.i_playerScore);
        _dataOutputStream.writeInt(p_gameStateBlock.i_aiScore);
        _dataOutputStream.writeByte(p_gameStateBlock.i_playerState);
        _dataOutputStream.writeByte(p_gameStateBlock.i_aiState);
        _dataOutputStream.flush();

        p_gameStateBlock.writeToStream(_dataOutputStream);

        _dataOutputStream = null;

        Runtime.getRuntime().gc();
    }

    /**
     * This function generates a pseudorandom value from 0 to a limit value
     * @param limit Limit value for generation
     * @return int value
     */
    public int getRandomInt(int limit)
    {
        limit++;
        limit = (int)(((long)Math.abs(nextInt()) * (long)limit) >>> 31);
        return limit;
    }

    /**
     * Constructor of the block
     * @param _screenWidth the width of the game screen
     * @param _screenHeight the height of the game screen
     * @param _gameActionListener the game listener for the block
     */
    public StrategicBlock(int _screenWidth,int _screenHeight,GameArea _gameActionListener)
    {
        super(System.currentTimeMillis());
        i_screenWidth = _screenWidth;
        i_screenHeight = _screenHeight;
        p_gameActionListener = _gameActionListener;
        p_gameStateBlock = null;
    }

    /**
     * Initing of a new game session
     * @param _level Level for the game
     */
    public abstract void newGameSession(int _level);

    /**
     * Process a game step
     * @param _playermoveobject Object contains the move of the player for current step
     */
    public abstract void nextGameStep(Object _playermoveobject);

    /**
     * Get current game state record
     * @return
     */
    public GameStateBlock getGameStateBlock()
    {
        return p_gameStateBlock;
    }

    /**
     * Deiniting all of game resources
     */
    public void endGameSession()
    {
        p_gameStateBlock = null;
        Runtime.getRuntime().gc();
    }

    /**
     * This function returns the string id of the game
     */
    public abstract String getGameID();

    /**
     * This function should return the max size of saved game block
     * @return
     */
    public abstract int getMaxSizeOfSavedGameBlock();

    /**
     * Stop the game for time
     */
    public void pauseGame()
    {
    }

    /**
     * Resume the game after pause
     */
    public void resumeGame()
    {
    }

    /**
     *  Resume game after player lost
     */
    public void resumeGameAfterPlayerLost()
    {
    }
}
