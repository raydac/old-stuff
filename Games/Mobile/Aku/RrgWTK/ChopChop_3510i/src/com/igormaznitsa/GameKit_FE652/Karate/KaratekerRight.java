package com.igormaznitsa.GameKit_FE652.Karate;

public class KaratekerRight extends Karateker
{
    private static SenseBreakingZone[][] sensezones = new SenseBreakingZone[][]{
        //Stand
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(25, 0, 9, 8, 3),
            //Body
            new SenseBreakingZone(23, 8, 12, 14, 2),
            //lleg
            new SenseBreakingZone(16, 23, 11, 17, 1),
            //rleg
            new SenseBreakingZone(30, 24, 11, 17, 1)
        },
        //Left
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(27, 0, 10, 8, 3),
            // Body
            new SenseBreakingZone(25, 7, 12, 14, 2),
            // Leg
            new SenseBreakingZone(25, 20, 14, 21, 1)
        },
        //Right
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(27, 0, 10, 8, 3),
            // Body
            new SenseBreakingZone(25, 7, 12, 14, 2),
            // Leg
            new SenseBreakingZone(25, 20, 14, 21, 1)
        },
        //Block top
        new SenseBreakingZone[]{
            // Body
            new SenseBreakingZone(18, 15, 11, 9, 2),
            // Leg
            new SenseBreakingZone(12, 24, 11, 17, 1)
        },
        //Block mid
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(22, 0, 8, 8, 3),
            // rleg
            new SenseBreakingZone(15, 21, 11, 6, 1),
            //lleg
            new SenseBreakingZone(13, 27, 10, 14, 1),
        },
        // Block down
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(22, 0, 8, 7, 3),
            // Body
            new SenseBreakingZone(19, 7, 11, 12, 2)
        },
        // Punch top
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(38, 0, 8, 8, 3),
            // Body
            new SenseBreakingZone(34, 8, 10, 9, 2),
            // Leg
            new SenseBreakingZone(37, 17, 11, 24, 1),
        },
        // Punch mid
        new SenseBreakingZone[]{
            //Head
            new SenseBreakingZone(38, 0, 8, 8, 3),
            //Body
            new SenseBreakingZone(34, 8, 11, 15, 2),
            //Leg
            new SenseBreakingZone(34, 23, 8, 18, 1)
        },
        // Punch down
        new SenseBreakingZone[]{
            // Head
            new SenseBreakingZone(37, 0, 8, 8, 3),
            // Body
            new SenseBreakingZone(34, 8, 10, 16, 2),
            // Leg
            new SenseBreakingZone(34, 24, 7, 17, 1)
        }
    };

    private static SenseBreakingZone[][] breakingzones = new SenseBreakingZone[][]{
        //Stand
        new SenseBreakingZone[0],
        //Left
        new SenseBreakingZone[0],
        //Right
        new SenseBreakingZone[0],
        //Block top
        new SenseBreakingZone[0],
        //Block mid
        new SenseBreakingZone[0],
        // Block down
        new SenseBreakingZone[0],
        // Punch top
        new SenseBreakingZone[]{new SenseBreakingZone(12, 4, 14, 8, 5)},
        // Punch mid
        new SenseBreakingZone[]{new SenseBreakingZone(10, 13, 12, 6, 5)},
        // Punch down
        new SenseBreakingZone[]{
                                new SenseBreakingZone(11, 25, 9, 5, 5),
                                new SenseBreakingZone(20, 22, 7, 7, 4)
        }
    };

    public KaratekerRight(int x, int y)
    {
        super(x, y, sensezones, breakingzones);
    }

    public void init(int x, int y)
    {
        super.init(x, y);
    }
}
