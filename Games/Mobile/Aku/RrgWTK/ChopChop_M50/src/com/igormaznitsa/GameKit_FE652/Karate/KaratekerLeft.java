package com.igormaznitsa.GameKit_FE652.Karate;

public class KaratekerLeft extends Karateker
{
    private static SenseBreakingZone[][] sensezones = new SenseBreakingZone [][] {
        //Stand
        new SenseBreakingZone[]{
                                // Body
                                new SenseBreakingZone(21,4,9,10, 2),
                                // Leg
                                new SenseBreakingZone(26,24,10,16,1)},
        //Left
        new SenseBreakingZone[]{
                                    // Head
                                    new SenseBreakingZone(14,0,8,9,3),
                                    // Body
                                    new SenseBreakingZone(13,9,11,12,2),
                                    // Legs
                                    new SenseBreakingZone(12,21,12,19,1)
        },
        //Right
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(14,0,8,9,3),
            // Body
            new SenseBreakingZone(13,9,11,12,2),
            // Legs
            new SenseBreakingZone(12,21,12,19,1)
        },
        //Block top
        new SenseBreakingZone[]{
            // Body
            new SenseBreakingZone(21,14,9,10,2),
            // Leg
            new SenseBreakingZone(26,24,10,16,1)
        },
        //Block mid
        new SenseBreakingZone[]{
                                // head
                                new SenseBreakingZone(20,0,8,9,3),
                                // leg
                                new SenseBreakingZone(25,22,11,18,1)
        },
        // Block down
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(21,0,7,9,3),
            // Body
            new SenseBreakingZone(18,9,14,14,2)
        },
        // Punch top
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(5,0,8,9,3),
            // body
            new SenseBreakingZone(5,9,13,13,2),
            // leg
            new SenseBreakingZone(8,22,9,18,1)
        },
        // Punch mid
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(4,0,8,8,3),
            // Body
            new SenseBreakingZone(4,8,17,21,2),
            // Leg
            new SenseBreakingZone(9,22,8,18,1)
        },
        // Punch down
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(5,0,8,9,3),
            // Body
            new SenseBreakingZone(5,9,11,12,2),
            // Leg
            new SenseBreakingZone(10,21,6,19,1)
        }};

    private static SenseBreakingZone[][] breakingzones = new SenseBreakingZone [][] {
        //Stand
        new SenseBreakingZone[0],
        //Left
        new SenseBreakingZone[0],
        //Right
        new SenseBreakingZone[0],

        //Block top
        new SenseBreakingZone[0],

        //Block mid
        new SenseBreakingZone[]{ new SenseBreakingZone(33,6,5,6,1)},

        // Block down
        new SenseBreakingZone[0],

        // Punch top
        new SenseBreakingZone[]{
                                new SenseBreakingZone(27,5,9,6,3),
                                new SenseBreakingZone(21,7,6,6,2)
        },
        // Punch mid
        new SenseBreakingZone[]{
                                new SenseBreakingZone(24,13,16,6,3)},
        // Punch down
        new SenseBreakingZone[]{
                                new SenseBreakingZone(20,19,11,8,2),
                                new SenseBreakingZone(26,23,10,6,3)
        }
    };


    public KaratekerLeft(int x,int y)
    {
        super(x,y,sensezones,breakingzones);
    }

    public void init(int x, int y)
    {
        super.init(x, y);
    }
}
