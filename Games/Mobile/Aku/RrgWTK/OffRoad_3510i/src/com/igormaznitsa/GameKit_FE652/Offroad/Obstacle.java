package com.igormaznitsa.GameKit_FE652.Offroad;

public class Obstacle
{
    /** Absolute X coordinate of the obstacle */
    public int _x;
    /** Absolute Z coordinate of the obstacle*/
    public int _z;

    /** Relative X coordinate of the asteroid */
    public int _rx;
    public int _ry;
    public int _type;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_PRESENT = 1;

    public Obstacle()
    {
        _x = 0;
        _z = 0;
        _rx = 0;
        _ry = 0;
        _type = TYPE_NONE;
    }
}
