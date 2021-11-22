package com.GameKit_3.SantaClaus;

public class Stages {

    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;

    public static final int[] ai_AlignArray = new int[]
    {
        //0
        ALIGN_CENTER,
        //1
        ALIGN_CENTER,
        //2
        ALIGN_CENTER,
        //3
        ALIGN_CENTER,
        //4
        ALIGN_RIGHT,
        //5
        ALIGN_LEFT,
        //6
        ALIGN_RIGHT,
        //7
        ALIGN_CENTER,
        //8
        ALIGN_CENTER,
        //9
        ALIGN_CENTER,
        //10
        ALIGN_LEFT,
        //11
        ALIGN_CENTER,
        //12
        ALIGN_CENTER
    };

    public static final byte[] ai_wayarray = new byte[]
    {
        //   0, 2, 3, 11, 5, 6, 8, 7, 9, 4, 12, 1, 12, 10, 0, 11, 2, 2, 3, 12, 1, 12, 10, 8, 9, 0, 7, 7, 3, 5, 6, 8,11,7,0, 9, 4, 3, 2, 11, 5, 8,12,1,12,10
        //8,8,8,3,8,8,4,12,7,13,11,1,5,9,6,3,1,12,10,8,
        7, 7, 7, 7, 7, 7, 2, 7, 7, 3, 11/*!*/, 6, 12, 10, 0, 4, 8, 5, 2, 9, 11/*!*/, 9, 7, //23
        //5, 2, 13, 2, 13, 11, 8, 3,10, 9, 4, 6, 7, 13,2,13,11,1,12,9,
        4/*S*/, 1, 12, 1, 12, 10, 7, 2, 9, 8, 3, 9, 6, 12, 1, 12, 10, 9, 11/*!*/, 8, //43
        //3,10,1,6,4,9,5,13,11,//50
        2, 2, 0, 5, 3, 8, 4, 12, 10,
        //8,8,4,9,3,6,3,7,13,2,13,11,8,1,10,5,12,4,5,11,
        7, 7, 3, 8, 2, 5, 9, 6, 12, 1, 12, 10, 7, 0, 4/*S*/, 9, 11/*!*/, 3, 4, 10,
        //9,13,2,13,11,6,4,4,3,3,10,10,10,1,12,1,8,3,9,4,
        8, 12, 1, 12, 10, 5, 3, 3, 2, 2, 9, 9, 3, 9, 11/*!*/, 0, 7, 2, 8, 3,
        //7,13,2,13,11,9,5,11,8,4,8,1,9,6,5,2,13,11,4,3,
        6, 12, 1, 12, 10, 8, 4/*S*/, 10, 7, 3, 7, 0, 8, 5, 4, 1, 12, 10, 3, 2,
        //3,3,12,9,1,4,8,8,8,10,10,3,3,7,13,2,13,11,1,3,
        2, 9, 11/*!*/, 8, 0, 3, 7, 7, 7, 9, 4/*S*/, 2, 2, 6, 12, 1, 12, 10, 0, 2,
        //6,9,10,3,4,5,11,7,13,2,13,11,8,9,6,4,12,9,1,3,4// 101
        5, 8, 9, 2, 3, 4, 10, 6, 12, 1, 12, 10, 7, 8, 5, 9, 11/*!*/, 8, 0, 2, 4, 3, 3, 3, 7, 7};

    public static final long[][] alal_masks = new long[][]
    {
        //House 1
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0xfffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 2
        new long[]{(long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0xffffffL, (long) 0xffffffL, (long) 0xffffffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x7fffffffL, (long) 0x7fffffffL, (long) 0x7fffffffL, (long) 0x7fffffffL, (long) 0x7fffffffL},
        //House 3
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x1fffL, (long) 0x1fffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7ffL, (long) 0x7ffL, (long) 0x7ffL, (long) 0x7ffL, (long) 0x7ffL, (long) 0x7ffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 4
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0xffffffffffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 5
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x3fffL, (long) 0xffffL, (long) 0x3ffffL, (long) 0xfffffL, (long) 0x3fffffL, (long) 0xffffffL, (long) 0x3ffffffL, (long) 0xfffffffL, (long) 0x3fffffffL, (long) 0x7fffffffL, (long) 0x1fffffffL, (long) 0x7ffffffL, (long) 0x1ffffffL, (long) 0x7fffffL, (long) 0x1fffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL},
        //House 6
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x3ffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 7
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL},
        //House 8
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x3ffffL, (long) 0x3ffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0xfffffL, (long) 0x1fffL, (long) 0x1fffL, (long) 0x1fffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 9
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0xfffffffL, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0xffffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x1ffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 10
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0x3ffffffL, (long) 0x7ffffffL, (long) 0x7ffffffL, (long) 0xfffffffL, (long) 0x3fffffffL, (long) 0x7fffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x1ffffffffffffffL, (long) 0x3fffffffL, (long) 0x3fffffffL, (long) 0x3fffffffL, (long) 0x3fffffffL, (long) 0x3fffffffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 11
        new long[]{(long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x7fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x3ffffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x7fffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 12
        new long[]{(long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x1ffffffffffL, (long) 0x3ffffffffffL, (long) 0x7ffffffffffL, (long) 0xfffffffffffL, (long) 0x1fffffffffffL, (long) 0x3fffffffffffL, (long) 0x7fffffffffffL, (long) 0xffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0x1ffffffffffffL, (long) 0xffffffffffffL, (long) 0x7fffffffffffL, (long) 0x3fffffffffffL, (long) 0x1fffffffffffL, (long) 0xfffffffffffL, (long) 0x7ffffffffffL, (long) 0x3ffffffffffL, (long) 0x1ffffffffffL, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L, (long) 0x0L},
        //House 13
        new long[]{(long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0xffffffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL, (long) 0x1fffffL}
    };

    public static final int BLOCKLENGTH = 40;
    public static final int BLOCK_NUMBER = 12;

    // Format [i8_X,i8_Y]
    protected static final int[] ai_InitPlayerArray = new int[]{/*0x2f08a0, 0x6760*/0x11480, 0x60a0, 0x76bc0, 0x6040, 0x14da00, 0x60a0, 0x1edf20, 0x5f40, 0x265a60, 0x60a0};

    // Format [i8_X,i8_Y,freq]
    protected static final int[] ai_ChimneyArray = new int[]{0x476e0+(8<<8), 0x2500, 70, 0x62320, 0x300, 80, 0x6c220, 0x300, 80,
                                                             0x9e300, 0x300, 80, 0xad240, 0x300, 100, 0xcb380, 0x300, 100, 0xe46a0-(5<<8), 0x2500, 80,
                                                             0x125300, 0x300, 80,
                                                             0x1483e0-(3<<8), 0x2500, 80, 0x152320, 0x300, 80, 0x19d400, 0x300, 80, 0x1a2320, 0x300, 80,
                                                             0x1ac480, 0x300, 90, 0x1b4460+(5<<8), 0x2500, 80, 0x1b6760-(7<<8), 0x2500, 80, 0x206760-(7<<8), 0x2500, 100,
                                                             0x204480-(7<<8), 0x2500, 100, 0x238260, 0x300, 80, 0x247b40-(7<<8), 0x2500, 80, 0x2604e0, 0x300, 80,
                                                             0x28b420+(5<<8), 0x2500, 80, 0x2a1340, 0x300, 80, 0x2e2320, 0x300, 80, 0x2ef1a0+(7<<8), 0x2500, 80,
                                                             0x2f1ba0, 0x2500, 70


    };

    public static long[] getBlock(int _num) {
        return alal_masks[_num];
    }

    public static byte[] getWay() {
        return ai_wayarray;
    }
}
