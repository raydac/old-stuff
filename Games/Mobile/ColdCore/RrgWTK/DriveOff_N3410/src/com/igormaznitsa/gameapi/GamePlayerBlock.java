package com.igormaznitsa.gameapi;

/**
 * This block must be implemented any player of a game.
 */
public interface GamePlayerBlock
{
    /**
     * Get next player's move record for the strategic block
     * @param gameStateBlock GSB of the game
     * @return
     */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateBlock gameStateBlock);

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer();
}
