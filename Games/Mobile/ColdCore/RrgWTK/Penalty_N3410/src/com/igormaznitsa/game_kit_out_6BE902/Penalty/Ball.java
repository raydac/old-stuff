package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class Ball
{
    protected int i_x;
    protected int i_y;
    protected int i_z;

    protected int i_frame;
    protected int i8_zcoeff;

    protected static final byte [] ai_sizes = new byte []
    {
        9,9,
        7,7,
        5,5
    };

    public Ball()
    {
        i8_zcoeff = ((ai_sizes.length>>1) << 8) / Penalty_SB.MAX_Z;
    }

    public void setXY(int _x,int _y)
    {
        i_x = _x;
        i_y = _y;
    }

    public int getScrWidth()
    {
        return ai_sizes[i_frame<<1];
    }

    public int getScrHeight()
    {
        return ai_sizes[(i_frame<<1)+1];
    }

    public int getScrX()
    {
        return i_x-(getScrWidth()>>1);
    }

    public int getScrY()
    {
        return i_y-(getScrHeight()>>1);
    }

    public int getFrame()
    {
        return i_frame;
    }

    public void setZ(int _z)
    {
        i_z = _z;
        i_frame =  (i8_zcoeff * _z)>>8;
        if (i_frame>= ((ai_sizes.length)>>1)) i_frame = (ai_sizes.length>>1)-1;
    }

    public void initBall(int _scrx, int _scry,int _scrz)
    {
        i_x = _scrx;
        i_y = _scry;
        setZ(_scrz);
    }
}
