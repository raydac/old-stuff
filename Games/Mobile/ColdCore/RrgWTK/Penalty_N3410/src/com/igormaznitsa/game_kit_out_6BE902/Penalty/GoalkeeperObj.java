package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class GoalkeeperObj
{
    public static final byte STATE_STAND = 0;
    public static final byte STATE_JUMPTOP = 1;
    public static final byte STATE_JUMPLEFT = 2;
    public static final byte STATE_JUMPRIGHT = 3;
    public static final byte STATE_JUMPLEFTTOP = 4;
    public static final byte STATE_JUMPRIGHTTOP = 5;
    public static final byte STATE_JUMPDOWN = 6;

    protected byte [] p_sizesarray;

    protected int i_state;
    protected int i_x;
    protected int i_y;

    protected int i_startx;
    protected int i_starty;
    protected int i_midx;
    protected int i_midy;
    protected int i_endx;
    protected int i_endy;
    protected int i8_xcoeff1;
    protected int i8_xcoeff2;
    protected int i8_ycoeff1;
    protected int i8_ycoeff2;

    protected int i_nextstate;
    protected static final int I8_COEFF = (8<<8)/Penalty_SB.MAX_Z;

    protected byte [] ai_states_cur;

    private static final byte [] ai_states_jumpleft = new byte []
    {
        STATE_STAND,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPLEFTTOP,
        STATE_JUMPLEFTTOP,
        STATE_JUMPLEFT,
        STATE_JUMPLEFT,
        STATE_JUMPLEFT
    };

    private static final byte [] ai_states_jumpright = new byte []
    {
        STATE_STAND,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPRIGHTTOP,
        STATE_JUMPRIGHTTOP,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT
    };

    private static final byte [] ai_states_jumptop = new byte []
    {
        STATE_STAND,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPTOP,
        STATE_JUMPTOP
    };

    private static final byte [] ai_states_jumpdown = new byte []
    {
        STATE_STAND,
        STATE_JUMPRIGHTTOP,
        STATE_JUMPRIGHTTOP,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT,
        STATE_JUMPRIGHT
    };

    public static final byte [] ai_far_sizes = new byte[]
    {
        // Width , height
        // Stand
        8, 12,
        // Top jump
        8, 13,
        // Left jump
        13, 8,
        // Right jump
        13, 8,
        // Left top jump
        14,14,
        // Right top jump
        14,14
    };

    public static final byte [] ai_near_sizes = new byte[]
    {
        // Width , height
        // Stand
        13, 17,
        // Top jump
        13, 19,
        // Left jump
        19, 13,
        // Right jump
        19, 13,
        // Left top
        19,20,
        // Right top
        20,20
    };

    public GoalkeeperObj()
    {
    }

    public int getScrX()
    {
        return i_x-(getScrWidth()>>1);
    }

    public int getScrY()
    {
        return i_y-(getScrHeight()>>1);
    }

    public int getScrWidth()
    {
        return p_sizesarray[i_state<<1];
    }

    public int getScrHeight()
    {
        return p_sizesarray[(i_state<<1)+1];
    }

    public void initMode(int _mode,int _gatecenterx,int _gatedowny)
    {
        if (_mode == Penalty_SB.MODE_GOALKEEPER)
        {
            p_sizesarray = ai_near_sizes;
        }
        else
        {
            p_sizesarray = ai_far_sizes;
        }

        i_state = STATE_STAND;
        i_x = _gatecenterx;
        i_y = _gatedowny-(getScrHeight()>>1);
    }

    public void setXY(int _x,int _y)
    {
        i_x = _x;
        i_y = _y;
    }

    public void setState(int _state)
    {
        i_nextstate = _state;
        switch(i_nextstate)
        {
            case STATE_JUMPLEFT : ai_states_cur = ai_states_jumpleft;break;
            case STATE_JUMPRIGHT : ai_states_cur = ai_states_jumpright;break;
            case STATE_JUMPTOP : ai_states_cur = ai_states_jumptop;break;
            case STATE_JUMPDOWN : ai_states_cur = ai_states_jumpdown;break;
            case STATE_STAND : ai_states_cur = null;break;
        }
    }

    public int getState()
    {
           return i_state;
    }

    public void setStateForZ(int _z,int _mode)
    {
        if (ai_states_cur == null) i_state = STATE_STAND;
        else
        {
            int i_indx = (_z * I8_COEFF)>>8;
            if (i_indx>7) i_indx = 7;
            if (i_indx<0) i_indx = 0;
            if (_mode == Penalty_SB.MODE_PLAYER)
            {
                i_state = ai_states_cur[i_indx];
            }
            else
            {
                i_state = ai_states_cur[7-i_indx];
            }
        }
    }

}
