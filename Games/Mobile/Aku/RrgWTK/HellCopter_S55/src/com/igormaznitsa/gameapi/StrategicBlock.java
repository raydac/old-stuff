package com.igormaznitsa.gameapi;

import java.io.*;
import java.util.Random;

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
    public static final byte[] RLEdecompress(byte[] inarray, int dstlen)
    {
        System.gc();
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
                out[outindx++] = (byte) value;
                counter--;
            }
        }
        System.gc();
        return out;
    }

    /**
     * Compressing a byte array with RLE
     * @param inarray incoming byte array
     * @return packed byte array
     */
    public static final byte[] RLEcompress(byte[] inarray)
    {
        System.gc();
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
        System.gc();
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
        return (_radians/6536);
    }

    /**
     * Calculating of sin with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSine(int x, int index)
    {
        return (x * sineTable[index]) >> 8;
    }

    /**
     * Calculating of cos with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSine(int x, int index)
    {
        index +=16;
        return (x * sineTable[index&63]) >> 8;
    }

    /**
     * Calculating of sin with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSineFloat(int x, int index)
    {
        return (x * sineTable[index]);
    }

    /**
     * Calculating of cos with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSineFloat(int x, int index)
    {
        index +=16;
        return (x * sineTable[index & 63]);
    }
//===========================================================

    /**
     * All game flags are clear
     */
    public static final int FLAG_EMPTY = 0x00;

    /**
     * A game flag. This flag shows what the game supports load/save operations.
     */
    public static final int FLAG_SAVELOAD_SUPPORT = 0x01;

    /**
     * A game flag. This flag shows what the game supports scores operations.
     */
    public static final int FLAG_SCORES_SUPPORT = 0x02;

    /**
     * A game flag. This flag shows what the game supports levels
     */
    public static final int FLAG_LEVELS_SUPPORT = 0x04;

    /**
     * A game flag. This flag shows what the game supports stages
     */
    public static final int FLAG_STAGES_SUPPORT = 0x08;

    /**
     * Game state block for current game
     */
    public GameStateBlock p_gameStateBlock;

    /**
     * Game's flags
     */
    public int i_gameFlags;

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
    public GameActionListener p_gameActionListener;

    /**
     * Load a game state from an input stream
     * @param dataInputStream
     * @throws java.io.IOException
     */
    public void loadGameState(DataInputStream  dataInputStream) throws IOException
    {
        System.gc();

        int  _level = dataInputStream.readUnsignedByte();

        newGameSession(_level);

        int _stage = dataInputStream.readUnsignedByte();
        p_gameStateBlock.initStage(_stage);

        p_gameStateBlock.i_playerScore = dataInputStream.readInt();
        p_gameStateBlock.i_aiScore = dataInputStream.readInt();
        p_gameStateBlock.i_playerState = dataInputStream.readUnsignedByte();
        p_gameStateBlock.i_aiState = dataInputStream.readByte();

        p_gameStateBlock.readFromStream(dataInputStream);

        dataInputStream = null;
        System.gc();
    }

    /**
     * Save current game state to an output stream
     * @param dataOutputStream
     * @throws java.io.IOException
     */
    public void saveGameState(DataOutputStream dataOutputStream) throws IOException
    {
        System.gc();

        dataOutputStream.writeByte(p_gameStateBlock.i_gameLevel);
        dataOutputStream.writeByte(p_gameStateBlock.i_gameStage);
        dataOutputStream.writeInt(p_gameStateBlock.i_playerScore);
        dataOutputStream.writeInt(p_gameStateBlock.i_aiScore);
        dataOutputStream.writeByte(p_gameStateBlock.i_playerState);
        dataOutputStream.writeByte(p_gameStateBlock.i_aiState);
        dataOutputStream.flush();

        p_gameStateBlock.writeToStream(dataOutputStream);

        dataOutputStream = null;

        System.gc();
    }

    /**
     * This function generates a pseudorandom value from 0 to a limit value
     * @param limit Limit value for generation
     * @return int value
     */
    public int getRandomInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(nextInt())*(long)limit)>>>31);
       return limit;
    }

    /**
     * Constructor of the block
     * @param screenWidth the width of the game screen
     * @param screenHeight the height of the game screen
     * @param gameActionListener the game listener for the block
     */
    public StrategicBlock(int screenWidth,int screenHeight,GameActionListener gameActionListener)
    {
        super(System.currentTimeMillis());
        i_gameFlags = FLAG_EMPTY;
        i_screenWidth = screenWidth;
        i_screenHeight = screenHeight;
        p_gameActionListener = gameActionListener;
        p_gameStateBlock = null;
    }

    /**
     * Initing of a new game session
     * @param level Level for the game
     */
    public void newGameSession(int level)
    {
    }

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
     * Get a game flag
     * @param flag tested flag
     * @return a game flag as a boolean value... if it is true, flag is set else flag is clear
     */
    public boolean getGameFlag(int flag)
    {
        return (i_gameFlags & flag)!=0;
    }

    /**
     * Deiniting all of game resources
     */
    public void endGameSession()
    {
        p_gameStateBlock = null;
        System.gc();
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
}
