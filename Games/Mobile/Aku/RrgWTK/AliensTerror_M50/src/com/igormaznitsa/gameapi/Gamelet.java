package com.igormaznitsa.gameapi;

import java.io.*;
import java.util.Random;

/*
 * Copyright © 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This abstract class describes a Gamelet
 */

public abstract class Gamelet extends Random
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
     * @param _inarray incoming byte array
     * @return packed byte array
     */
    public static final byte[] RLEcompress(byte[] _inarray)
    {
        System.gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(_inarray.length);

        int inindx = 0;
        while (inindx < _inarray.length)
        {
            int value = _inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < _inarray.length)
            {
                if ((_inarray[inindx] & 0xFF) == value)
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
     * @param _x
     * @return int value
     */
    public static int sqr(int _x)
    {
        int bx = _x;
        int ax = 1,di = 2,cx = 0,dx = 0;
        while (cx <= bx)
        {
            cx += ax;
            ax += di;
            dx++;
        }
        return dx - 1;
    }

    /**
     * The table of constants for SIN and COS procedures.
     */
    public final static int[] ai_sineTable = new int[]{0, 25, 50, 74, 98, 121, 142, 162, 181, 198, 213, 226, 237, 245, 251, 255, 256, 255, 251, 245, 237, 226, 213, 198, 181, 162, 142, 121, 98, 74, 50, 25, 0, -25, -50, -74, -98, -121, -142, -162, -181, -198, -213, -226, -237, -245, -251, -255, -256, -255, -251, -245, -237, -226, -213, -198, -181, -162, -142, -121, -98, -74, -50, -25};

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
    public static int xSine(int x, int index)
    {
        return (x * ai_sineTable[index]) >> 8;
    }

    /**
     * Calculating of cos with multiply in index
     * @param _x Multiplier
     * @param _index Angle (0..63)
     * @return
     */
    public static int xCoSine(int _x, int _index)
    {
        _index += 16;
        return (_x * ai_sineTable[_index & 63]) >> 8;
    }

    /**
     * Calculating of sin with multiply in index without right shift
     * @param _x Multiplier
     * @param _index Angle (0..63)
     * @return
     */
    public static int xSineFloat(int _x, int _index)
    {
        return (_x * ai_sineTable[_index]);
    }

    /**
     * Calculating of cos with multiply in index without right shift
     * @param _x Multiplier
     * @param _index Angle (0..63)
     * @return
     */
    public static int xCoSineFloat(int _x, int _index)
    {
        _index += 16;
        return (_x * ai_sineTable[_index & 63]);
    }
//===========================================================

    /**
     * Game is playing
     */
    public static final int GAMESTATE_PLAYED = 0;

    /**
     * Game is over
     */
    public static final int GAMESTATE_OVER = 1;

    /**
     * Player is in normal mode
     */
    public static final int PLAYERSTATE_NORMAL = 0;

    /**
     * Player is winner
     */
    public static final int PLAYERSTATE_WON = 1;

    /**
     * Player is loser
     */
    public static final int PLAYERSTATE_LOST = 2;

    /**
     * Width of the output screen
     */
    public int i_ScreenWidth;

    /**
     * Height of the output screen
     */
    public int i_ScreenHeight;

    /**
     * The link to a game action listener
     */
    public GameActionListener p_GameActionListener;

//#if (FLAG_SAVELOAD_SUPPORT)
    /**
     * Load a game state from an input stream
     * @param _dataInputStream
     * @throws java.io.IOException
     */
    public void loadGameState(DataInputStream _dataInputStream) throws IOException
    {
        System.gc();

        //#if (FLAG_LEVELS_SUPPORT)
        int _level = _dataInputStream.readUnsignedByte();
        newGameSession(_level);
        //#endif

        //#if (FLAG_STAGES_SUPPORT)
        int _stage = _dataInputStream.readUnsignedByte();
        initStage(_stage);
        //#endif

        i_PlayerScore = _dataInputStream.readInt();
        i_AiScore = _dataInputStream.readInt();
        i_PlayerState = _dataInputStream.readUnsignedByte();
        i_AiState = _dataInputStream.readByte();

        readFromStream(_dataInputStream);

        _dataInputStream = null;
        System.gc();
    }

    /**
     * Save current game state to an output stream
     * @param _dataOutputStream
     * @throws java.io.IOException
     */
    public void saveGameState(DataOutputStream _dataOutputStream) throws IOException
    {
        System.gc();

        //#if (FLAG_LEVELS_SUPPORT)
        _dataOutputStream.writeByte(i_GameLevel);
        //#endif

        //#if (FLAG_STAGES_SUPPORT)
        _dataOutputStream.writeByte(i_GameStage);
        //#endif

        _dataOutputStream.writeInt(i_PlayerScore);
        _dataOutputStream.writeInt(i_AiScore);
        _dataOutputStream.writeByte(i_PlayerState);
        _dataOutputStream.writeByte(i_AiState);
        _dataOutputStream.flush();

        writeToStream(_dataOutputStream);

        _dataOutputStream = null;

        System.gc();
    }
//#endif

    /**
     * This function generates a pseudorandom value from 0 to a limit value (the limit value is included in the range)
     * @param _limit Limit value for generation
     * @return int value
     */
    public int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    /**
     * Constructor of the block
     * @param _screenWidth the width of the game screen
     * @param _screenHeight the height of the game screen
     * @param _gameActionListener the game listener for the block
     */
    public Gamelet(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(System.currentTimeMillis());
        i_ScreenWidth = _screenWidth;
        i_ScreenHeight = _screenHeight;
        p_GameActionListener = _gameActionListener;
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
     * Deiniting all of game resources
     */
    public void endGameSession()
    {
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

    /**
     *  Resume game after player lost
     */
    public void resumeGameAfterPlayerLost()
    {
    }

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Current game level
     */
    public int i_GameLevel;
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Current game stage
     */
    public int i_GameStage;
//#endif

    /**
     * Current player's state
     */
    public int i_PlayerState;

    /**
     * Current AI state
     */
    public int i_AiState;

    /**
     * Current game state
     */
    public int i_GameState;

    /**
     * Current player score
     */
    public int i_PlayerScore;

    /**
     * Current ai score
     */
    public int i_AiScore;

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Initing of a game level
     */
    public void initLevel(int _level)
    {
        i_AiState = PLAYERSTATE_NORMAL;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        i_GameLevel = _level;
        i_PlayerScore = 0;
        i_AiScore = 0;
    }
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Initing of a game stage
     */
    public void initStage(int _stage)
    {
        i_GameStage = _stage;
        i_AiState = PLAYERSTATE_NORMAL;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
    }
//#endif

    /**
     * Get current player's score
     * @return the current player scores as int value
     */
    public int getPlayerScore()
    {
        return i_PlayerScore;
    }

    /**
     * Get current ai's score
     * @return the current ai scores as int value
     */
    public int getAIScore()
    {
        return i_AiScore;
    }

//#if (FLAG_SAVELOAD_SUPPORT)
    /**
     * Save the game state block to an output stream
     */
    public abstract void writeToStream(DataOutputStream _dataOutputStream) throws IOException;

    /**
     * Load the game state block from an input stream
     */
    public abstract void readFromStream(DataInputStream _dataInputStream) throws IOException;
//#endif
}
