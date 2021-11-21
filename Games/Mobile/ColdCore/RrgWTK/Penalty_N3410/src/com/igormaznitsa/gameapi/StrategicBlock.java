package com.igormaznitsa.gameapi;

import com.igormaznitsa.midp.GameSoundBlock;

import java.io.*;

/**
 * This interface descrtibes a strategic game block. A game alghoritm must be implemented in this place.
 */
public abstract class StrategicBlock
{
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
     * The link to a game player block object for the player
     */
    public GamePlayerBlock p_gamePlayerBlock;

    /**
     * The link to a game action listener
     */
    public GameActionListener p_gameActionListener;

    /**
     * The link to a game player block object for the AI player
     */
    public GamePlayerBlock p_aiPlayerBlock;

    /**
     * Load a game state from an input stream
     * @param inputStream
     * @throws java.io.IOException
     */
    public void loadGameState(InputStream  inputStream) throws IOException
    {
        System.gc();

        DataInputStream dataInputStream = new DataInputStream(inputStream);

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
     * @param outputStream
     * @throws java.io.IOException
     */
    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

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
     * Constructor of the block
     * @param screenWidth the width of the game screen
     * @param screenHeight the height of the game screen
     * @param gamePlayerBlock the block of the player
     * @param aiPlayerBlock the block for AI player
     * @param gameActionListener the game listener for the block
     */
    public StrategicBlock(int screenWidth,int screenHeight,GamePlayerBlock gamePlayerBlock,GamePlayerBlock aiPlayerBlock,GameActionListener gameActionListener)
    {
        i_gameFlags = FLAG_EMPTY;
        i_screenWidth = screenWidth;
        i_screenHeight = screenHeight;
        p_gamePlayerBlock = gamePlayerBlock;
        p_aiPlayerBlock = aiPlayerBlock;
        p_gameActionListener = gameActionListener;
        p_gameStateBlock = null;
    }

    /**
     * Initing of a new game session
     * @param level Level for the game
     */
    public void newGameSession(int level)
    {
        if (p_gamePlayerBlock!=null) p_gamePlayerBlock.initPlayer();
    }

    /**
     * Process next game step
     */
    public abstract void nextGameStep();

    /**
     * Get current game state record
     * @return
     */
    public GameStateBlock getGameStateBlock()
    {
        return p_gameStateBlock;
    }

    /**
     * Set a Player block
     * @param playerBlock new player block for the game
     */
    public void setPlayerBlock(GamePlayerBlock playerBlock)
    {
        p_gamePlayerBlock = playerBlock;
    }

    /**
     * Set AI block
     * @param aiBlock new AI block for the game
     */
    public void setAIBlock(GamePlayerBlock aiBlock)
    {
        p_aiPlayerBlock = aiBlock;
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
}
