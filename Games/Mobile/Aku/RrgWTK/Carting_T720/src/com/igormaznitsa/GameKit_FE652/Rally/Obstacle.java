package com.igormaznitsa.GameKit_FE652.Rally;

public class Obstacle
{
    public static final int NONE = 4;
    public static final int NORMAL_CAR = 0;
    public static final int FIRING_CAR = 1;
    public static final int MOVING_CAR = 2;
    public static final int FIRING_MOVED_CAR = 3;


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
