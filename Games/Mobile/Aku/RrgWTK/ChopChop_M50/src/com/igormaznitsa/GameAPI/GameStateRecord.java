package com.igormaznitsa.GameAPI;

/**
 * This interface describes record of a "Game World" object.
 */
public interface GameStateRecord
{
    /**
     * Get current game state
     */
    public int getGameState();

    /**
     * Get current player's scores
     * @return
     */
    public int getPlayerScores();

    /**
     * Get current ai's scores
     * @return
     */
    public int getAIScores();

    /**
     * Get current player's state
     * @return
     */
    public int getPlayerState();

    /**
     * Get current ai's state
     * @return
     */
    public int getAIState();

    public int getLevel();
    public int getStage();

}
