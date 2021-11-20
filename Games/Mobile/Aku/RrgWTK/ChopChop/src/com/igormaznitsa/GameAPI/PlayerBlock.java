package com.igormaznitsa.GameAPI;

public interface PlayerBlock
{
    /**
     * Get next player's move record for the strategic block
     * @param gameStateRecord
     * @return
     */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord);

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer();
}
