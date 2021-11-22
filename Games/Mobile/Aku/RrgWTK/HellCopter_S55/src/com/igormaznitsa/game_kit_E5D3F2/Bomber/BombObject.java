package com.igormaznitsa.game_kit_E5D3F2.Bomber;

public class BombObject
{
    protected boolean lg_is_active;
    protected int i_scrx;
    protected int i_scry;
    protected int i_destroyed;

    public BombObject()
    {
        lg_is_active = false;
        i_scrx = 0;
        i_scry = 0;
        i_destroyed = 0;
    }

    public void init(int _x,int _y)
    {
        lg_is_active = true;
        i_scrx = _x;
        i_scry = _y;
        i_destroyed = 0;
    }

    public int getScrX()
    {
        return i_scrx;
    }

    public int getScrY()
    {
        return i_scry;
    }

    public boolean isActive()
    {
        return lg_is_active;
    }
}
