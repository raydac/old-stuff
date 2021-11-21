package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class Gate
{
    public static final byte GATEMODE_NEAR = 0;
    public static final byte GATEMODE_FAR = 1;

    public static final byte GATEWIDTH_FAR = 48;
    public static final byte GATEHEIGHT_FAR = 16;
    public static final byte GATEWIDTH_NEAR = 72;
    public static final byte GATEHEIGHT_NEAR = 24;

    protected int i_scrx;
    protected int i_scry;
    protected int i_width;
    protected int i_height;
    protected int i_mode;

    public Gate()
    {
    }

    public int getScrWidth()
    {
        return i_width;
    }

    public int getScrHeight()
    {
        return i_height;
    }

    public int getScrX()
    {
        return i_scrx;
    }

    public int getScrY()
    {
        return i_scry;
    }

    public void initGate(int _scrx, int _scry, int _mode)
    {
        i_mode = _mode;
        i_scrx = _scrx;
        i_scry = _scry;

        switch (_mode)
        {
            case GATEMODE_FAR:
                {
                    i_width = GATEWIDTH_FAR;
                    i_height = GATEHEIGHT_FAR;
                }
                ;
                break;
            case GATEMODE_NEAR:
                {
                    i_width = GATEWIDTH_NEAR;
                    i_height = GATEHEIGHT_NEAR;
                }
                ;
                break;
        }
    }
}
