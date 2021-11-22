/*
 * Created by IntelliJ IDEA.
 * User: Евгений
 * Date: 11.09.2001
 * Time: 17:11:55
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
//package Soc;

package com.igormaznitsa.GameKit_FE652.Socoban;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;


public class Socoban_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_RIGHT = 2;
    public static final int DIRECT_UP = 4;
    public static final int DIRECT_DOWN = 8;
    public static final int DIRECT_BACK = 16;
    protected int _direct;

    public void setDirect(int direct)
    {
        _direct = direct;
    }

    public int getDirect()
    {
        return _direct;
    }

      public Socoban_PMR(int direct)
    {
        _direct = direct;
    }
}

