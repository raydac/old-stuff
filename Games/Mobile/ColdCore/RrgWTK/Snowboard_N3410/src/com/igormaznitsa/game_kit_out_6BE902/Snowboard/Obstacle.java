package com.igormaznitsa.game_kit_out_6BE902.Snowboard;

public class Obstacle
{
    public static final byte OBSTACLE_FIR = 0;
    public static final byte OBSTACLE_STONE = 1;
    public static final byte OBSTACLE_HILL = 2;

    public static final int MAXFRAMES = 14;

    public boolean lg_isactive;
    public int i_frame;
    public byte b_type;

    public byte [] ab_cursizearray;

    public int i_scrX,i_scrY;
    protected int i_Z,i_X;

    protected static final byte [] ab_framesizes_fir =
    {
        28,43,
        26,40,
        24,37,
        22,34,
        20,31,
        18,28,
        16,25,
        14,22,
        12,19,
        10,16,
        8,13,
        6,10,
        4,7,
        2,4
    };

    protected static final byte [] ab_framesizes_stone =
    {
        27,18,
        26,17,
        24,16,
        23,15,
        21,14,
        20,13,
        18,12,
        17,11,
        15,10,
        14,9,
        12,8,
        11,7,
        8,5,
        5,3
    };

    protected static final byte [] ab_framesizes_hill =
    {
        25,12,
        23,11,
        21,10,
        19,9,
        17,8,
        15,7,
        13,6,
        11,5,
        9,4,
        8,4,
        7,4,
        5,3,
        3,2,
        3,2
    };

    protected void activate(byte _type,int _x)
    {
        lg_isactive = true;
        b_type = _type;
        i_X = _x;
        i_Z = Snowboard_SB.I_OBSTACLESTARTZ+Snowboard_SB.I_OBSTACLEZSPEED;
        switch(_type)
        {
            case OBSTACLE_FIR : ab_cursizearray = ab_framesizes_fir;break;
            case OBSTACLE_HILL : ab_cursizearray = ab_framesizes_hill;break;
            case OBSTACLE_STONE : ab_cursizearray = ab_framesizes_stone;break;
        }
    }

    public Obstacle ()
    {
        activate(OBSTACLE_FIR,0);
        lg_isactive = false;
    }
}
