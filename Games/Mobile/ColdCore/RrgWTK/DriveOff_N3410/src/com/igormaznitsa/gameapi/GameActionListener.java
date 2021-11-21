package com.igormaznitsa.gameapi;

public interface GameActionListener
{
    public void gameAction(int actionID);
    public void gameAction(int actionID,int param0);
    public void gameAction(int actionID,int param0,int param1);
    public void gameAction(int actionID,int param0,int param1,int param2);
}
