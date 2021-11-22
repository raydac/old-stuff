package com.igormaznitsa.gameapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * This block saves the state of a game during all game cycle.
 */
public abstract class GameStateBlock
{
    /**
     * Game is played
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

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Current game level
     */
    public int i_gameLevel;
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Current game stage
     */
    public int i_gameStage;
//#endif

    /**
     * Current player's state
     */
    public int i_playerState;

    /**
     * Current AI state
     */
    public int i_aiState;

    /**
     * Current game state
     */
    public int i_gameState;

    /**
     * Current player score
     */
    public int i_playerScore;

    /**
     * Current ai score
     */
    public int i_aiScore;

    /**
     * Constructor
     */
    public GameStateBlock()
    {
    }

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Initing of a game level
     */
    public void initLevel(int _level)
    {
        i_aiState = PLAYERSTATE_NORMAL;
        i_playerState = PLAYERSTATE_NORMAL;
        i_gameState = GAMESTATE_PLAYED;
        i_gameLevel = _level;
        i_playerScore = 0;
        i_aiScore = 0;
    }
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Initing of a game stage
     */
    public void initStage(int _stage)
    {
        i_gameStage = _stage;
        i_aiState = PLAYERSTATE_NORMAL;
        i_playerState = PLAYERSTATE_NORMAL;
        i_gameState = GAMESTATE_PLAYED;
    }
//#endif

    /**
     * Get current game state
     * @return the current game state as int value
     */
    public int getGameState()
    {
        return i_gameState;
    }

    /**
     * Get current player's score
     * @return the current player scores as int value
     */
    public int getPlayerScore()
    {
        return i_playerScore;
    }

    /**
     * Get current ai's score
     * @return the current ai scores as int value
     */
    public int getAIScore()
    {
        return i_aiScore;
    }

    /**
     * Get current player's state
     * @return the current player's state as int value
     */
    public int getPlayerState()
    {
        return i_playerState;
    }

    /**
     * Get current ai's state
     * @return the current ai's state as int value
     */
    public int getAIState()
    {
        return i_aiState;
    }

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Get current game level
     * @return the current game level as int value
     */
    public int getLevel()
    {
        return i_gameLevel;
    }
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Get current game stage
     * @return the current game stage as int value
     */
    public int getStage()
    {
        return i_gameStage;
    }
//#endif

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
