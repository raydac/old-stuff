package com.igormaznitsa.GameKit_FE652.Slalom;

public class Obstacle
{
    public static final int NONE = 0x4;
    public static final int NORMAL_SKIER = 0;
    public static final int DROPPED_SKIER = 1;
    public static final int SKIER_ON_FLAG = 2;
    public static final int FLAG = 3;

    public int _x;
    public int _y;
    public int _type;
    public int _speed;

    public Obstacle(int x,int y,int type,int speed)
    {
        _x = x;
        _y = y;
        _type = type;
        _speed = speed;
    }
}
