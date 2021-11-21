package com.igormaznitsa.gameapi;

/**
 * This block must be implemented any player of a game.
 */
public interface ControlMenuBlock
{
       /**
        * destroing application (top level)
        */
       public void killApp();
       /**
        * Shows Main menu
        */
       public void ShowMainMenu();
       /**
        * Shows Game menu
        */
       public void ShowGameMenu();
       /**
        * Requesting agreements on cancelation/exitting from game
        * @param globalFlag choosing cancelation or exitting action
        */
       public void ShowQuitMenu(boolean globalFlag);
       /**
        * call back for loading language
        */
       public void LoadLanguage();
       /**
        * Accepting new player's score
        * @param NewScore
        */
       public void newScore(int NewScore);

}
