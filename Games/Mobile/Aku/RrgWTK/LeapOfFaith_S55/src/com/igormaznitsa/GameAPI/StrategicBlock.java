package com.igormaznitsa.GameAPI;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This interface descrtibes a Game Strategic Block. A game alghoritm must be implemented in this place.
 */
public interface StrategicBlock
{
    /**
     * Load a game state from an input stream
     * @param inputStream
     * @throws IOException
     */
    public void loadGameState(InputStream  inputStream) throws IOException;

    /**
     * Save current game state to an output stream
     * @param outputStream
     * @throws java.io.IOException
     */
    public void saveGameState(OutputStream outputStream) throws IOException;

    /**
     * Initing of a new game session
     * @param level Level for the game
     */
    public void newGame(int level);


    /**
     * Processing next game step
     */
    public void nextGameStep();

    /**
     * Get current game state record
     * @return
     */
    public GameStateRecord getGameStateRecord();

    /**
     * Set a Player block
     */
    public void setPlayerBlock(PlayerBlock playerBlock);

    /**
     * Set AI block
     */
    public void setAIBlock(PlayerBlock aiBlock);

    /**
     * Return the flag of support of loading/saving game state
     * True if it is supporting else false
     */
    public boolean isLoadSaveSupporting();

}
