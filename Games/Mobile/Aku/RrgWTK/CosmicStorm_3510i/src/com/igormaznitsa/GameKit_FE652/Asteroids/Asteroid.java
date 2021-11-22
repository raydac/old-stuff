package com.igormaznitsa.GameKit_FE652.Asteroids;

public class Asteroid
{
    /** Absolute X coordinate of the asteroid */
    public int _x;
    /** Absolute Y coordinate of the asteroid */
    public int _y;
    /** Absolute Z coordinate of the asteroid */
    public int _z;

    /** Relative X coordinate of the asteroid */
    public int _rx;
    public int _ry;

    public int _type;

    public static final int TYPE_NONE = 2;
    public static final int TYPE_PRESENT = 1;

    public Asteroid()
    {
        _x = 0;
        _y = 0;
        _z = 0;
        _rx = 0;
        _ry = 0;
        _type = TYPE_NONE;
    }
}
