package com.igormaznitsa.GameKit_FE652.Mosaic;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

/**
 * the player record for the Mosaic game
 */
public class Mosaic_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = -1;
    public static final int DIRECT_TOP = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_DOWN = 3;
    public static final int DIRECT_RIGHT = 2;

    protected int _current_direct;

    /**
     * Constructor
     * @param direct Direction of moving the empty cell
     */
    public Mosaic_PMR(int direct)
    {
        _current_direct = direct;
    }

    /**
     * Set new value of coordinates
     * @param direct New value of direction
     */
    public void setDirect(int direct)
    {
        _current_direct = direct;
    }

    /**
     * Get direct value
     * @return
     */
    public int getDirect()
    {
        return _current_direct;
    }

}
