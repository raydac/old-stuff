package com.igormaznitsa.gameapi.Utils;

import java.util.Random;

/**
 * A rnd generator for using in games
 */
public class RndGenerator extends Random
{
    /**
     * Constructor
     * @param startvalue Startvalue for the pseudorandomize order
     */
    public RndGenerator(long startvalue)
    {
        super(startvalue);
    }

    /**
     * This function generates a pseudorandom value from 0 to a limit value
     * @param limit Limit value for generation
     * @return
     */
    public int getInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(nextInt())*(long)limit)>>>31);
       return limit;
    }
}
