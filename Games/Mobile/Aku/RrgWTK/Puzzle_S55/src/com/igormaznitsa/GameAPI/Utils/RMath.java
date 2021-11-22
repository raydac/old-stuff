package com.igormaznitsa.GameAPI.Utils;

public class RMath
{
    public static int sqr(int x)
    {
        int bx = x;
        int ax = 1,di = 2,cx = 0,dx = 0;
        while (cx <= bx)
        {
            cx += ax;
            ax += di;
            dx++;
        }
        return dx - 1;
    }
}
