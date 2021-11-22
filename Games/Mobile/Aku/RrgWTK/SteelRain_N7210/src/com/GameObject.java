package com;

public class GameObject
{
    // -----------------------------------------------------------------------------

    private byte screenWIDTH = -1;
    private byte screenHEIGHT = -1;

    private byte X = (byte)255; // screen coordinate (hidden by default)
    private byte Y = (byte)255; // screen coordinate (hidden by default)
    private byte Dir = 0; // head direction

    private byte WIDTH;
    private byte HEIGHT;

    private boolean active = false;

    public static byte DIR_STOP = 0;
    public static byte DIR_LEFT = 1;
    public static byte DIR_RIGHT = 2;
    public static byte DIR_UP = 3;
    public static byte DIR_DOWN = 4;
    public static byte DIR_UPRIGHT = 5;
    public static byte DIR_DOWNRIGHT = 6;
    public static byte DIR_UPLEFT = 7;
    public static byte DIR_DOWNLEFT = 8;

    // -----------------------------------------------------------------------------

    public byte aX = (byte)255;  // additional coordinate (for example  in game area)
    public byte aY = (byte)255;  // additional coordinate (for example  in game area)
    public byte vDir = DIR_STOP; // additional direction (vision dir)

    // -----------------------------------------------------------------------------
    public static int getStaticArrayLength()
    {
        return 11;
    }
    public int getArrayLength()
    {
        return 11;
    }

    public byte[] getMeAsByteArray()
    {
        byte ret[] = new byte[11];
        ret[0] = screenWIDTH;
        ret[1] = screenHEIGHT;
        ret[2] = X;
        ret[3] = Y;
        ret[4] = Dir;
        ret[5] = WIDTH;
        ret[6] = HEIGHT;
        ret[7] = aX;
        ret[8] = aY;
        if (active)
            ret[9] = 0;
        else
            ret[9] = 1;
        ret[10] = vDir;
        return ret;
    }

    public void setMeAsByteArray(byte[] val)
    {
        screenWIDTH = val[0];
        screenHEIGHT = val[1];
        X = val[2];
        Y = val[3];
        Dir = val[4];
        WIDTH = val[5];
        HEIGHT = val[6];
        aX = val[7];
        aY = val[8];
        if (val[9] == 0)
            active = true;
        else
            active = false;
        vDir = val[10];
    }

    // -----------------------------------------------------------------------------
    /*
       Create new object with specified height , width and active state
    */
    public GameObject(int w, int h, boolean acv)
    {
        WIDTH = (byte)w;
        HEIGHT = (byte)h;
        active = acv;
    }

    /*
      Set screen (game level) size
      If called - object always check him coordinates for being creen rectangle
    */
    public void setScreen(int w, int h)
    {
        screenWIDTH = (byte)w;
        screenHEIGHT = (byte)h;
    }

    /*
       Return state of this object
    */
    public boolean isActive()
    {
        return active;
    }

    /*
      Set state of this object
    */
    public void setActiveState(boolean val)
    {
        active = val;
    }

    /*
      For graphics representation
      Coordinates system based on 0,0 in left-upper corner of screen
    */
    public boolean checkCollision(GameObject obj)
    {
        return checkCollision(obj.X, obj.Y, obj.WIDTH, obj.HEIGHT);
    }

    /*
      For graphics representation
      Coordinates system based on 0,0 in left-upper corner of screen
    */
    public boolean checkCollision(int objX, int objY, int objWidth, int objHeight)
    {
        boolean ret = false;
        boolean yok = false;
        boolean xok = false;

        boolean leftcollision = false;
        if (X >= objX && X <= objX + objWidth)
            leftcollision = true;
        boolean rightcollision = false;
        if (X + WIDTH >= objX && X + WIDTH <= objX + objWidth)
            rightcollision = true;
        if (X <= objX && X + WIDTH >= objX + objWidth)
        {
            leftcollision = true;
            rightcollision = true;
        }

        boolean upcollision = false;
        if (Y >= objY && Y <= objY + objHeight)
            upcollision = true;
        boolean downcollision = false;
        if (Y + HEIGHT >= objY && Y + HEIGHT <= objY + objHeight)
            downcollision = true;
        if (Y <= objY && Y + HEIGHT >= objY + objHeight)
        {
            upcollision = true;
            downcollision = true;
        }

        if (upcollision || downcollision) yok = true;

        if (leftcollision || rightcollision) xok = true;

        if (xok && yok) ret = true;

        return ret;
    }

    /*
      Return curent head direction of this object
      See static variables DIR_<...> in header of this file
    */
    public byte getDirection()
    {
        return Dir;
    }

    /*
      Set curent head direction of this object
      See static variables DIR_<...> in header of this file
    */
    public void setDirection(int dir)
    {
        Dir = (byte)dir;
    }

    /*
      Set size of this object
      Required for collision detection and screen collision detection
    */
    public void setSize(int w, int h)
    {
        WIDTH = (byte)w;
        HEIGHT = (byte)h;
    }

    /*
      Set size of this object
    */
    //public Dimension getSize()
    //{
    //    return new Dimension(WIDTH, HEIGHT);
    //}

    /*
       Function add number to X coorediante of object
    */
    public void shiftX(int add)
    {
        setX((byte)(X() + add));
    }

    /*
       Function add number to Y coorediante of object
    */
    public void shiftY(int add)
    {
        setY((byte)(Y() + add));
    }

    /*
       X coordinate of this object
    */
    public int X()
    {
        return X;
    }

    /*
       Y coordinate of this object
    */
    public int Y()
    {
        return Y;
    }

    /*
       Object width
    */
    public int WIDTH()
    {
        return WIDTH;
    }

    /*
       Object height
    */
    public int HEIGHT()
    {
        return HEIGHT;
    }

    /*
      set X coord of this object
      if be called function setScreen() with not -1 parameters
      this function not give object leave screen
    */
    public void setX(int x)
    {
        if (screenWIDTH == -1)
            X = (byte)x;
        else
            if (x >= 0 && x <= screenWIDTH - WIDTH())
                X = (byte)x;
    }

    /*
      set Y coord of this object
      if be called function setScreen() with not -1 parameters
      this function not give object leave screen
    */
    public void setY(int y)
    {
        if (screenWIDTH == -1)
            Y = (byte)y;
        else
            if (y >= 0 && y <= screenHEIGHT - HEIGHT())
                Y = (byte)y;
    }

    /*
       Set X and Y coords of this object
    */
    public void setCoord(int x, int y)
    {
        setX(x);
        setY(y);
    }

}
