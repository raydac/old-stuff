package com.raydac_research.FormEditor.RrgFormComponents;

public class PathPoint
{
    public int i_X;
    public int i_Y;
    public int i_Steps;

    public int i_Index;

    public String toString()
    {
        return "["+i_Index+"] X:"+i_X+" Y:"+i_Y+" S:"+i_Steps;
    }

    public PathPoint(int _x,int _y,int _s)
    {
        i_Index = 0;

        i_X = _x;
        i_Y = _y;
        i_Steps = _s;
    }

    public PathPoint(int _x,int _y,int _s,int _index)
    {
        i_Index = 0;

        i_X = _x;
        i_Y = _y;
        i_Steps = _s;
        i_Index = _index;
    }

    public Object clone()
    {
        return new PathPoint(i_X,i_Y,i_Steps,i_Index);
    }
}
