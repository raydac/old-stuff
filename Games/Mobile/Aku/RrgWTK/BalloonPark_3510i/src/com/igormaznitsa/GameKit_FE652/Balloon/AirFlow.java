package com.igormaznitsa.GameKit_FE652.Balloon;

public class AirFlow
{
    /**
     * Y coordinate of beginning of the flow on screen
     */
    public int _y;
    /**
     * Height of the flow
     */
    public int _h;
    /**
     * X coordinate of the marker of the flow
     */
    public int _markerx;
    /**
     * Y coordinate of the marker of the flow
     */
    public int _markery;
    /**
     * Speed of the flow
     */
    public int _speed;

    /**
     * Constructor
     * @param y
     * @param h
     * @param mx
     */
    public AirFlow(int y,int h,int mx,int speed)
    {
        _markery = ((h - Balloon_SB.FLOWMARKER_HEIGHT)>>1)+y;
        _markerx = mx;
        _y = y;
        _h = h;
        _speed = speed;
    }
}
