import java.io.*;

public class Stages {

    public final static int TOTAL_STAGES = 2;

    public final static int FLOOR = 0;
    public final static int WALL0 = 1;
    public final static int WALL1 = 2;
    public final static int WALL2 = 3;
    public final static int WALL3 = 4;
    public final static int WALL4 = 5;

    public final static int EXIT = 193;
    public final static int PLAYER = 194;
    public final static int TREASURE = 195;
    public final static int GUARDIAN = 196;

    public final static int PUSHED_BUTTON = 90;   //Pushed button
    public final static int OPEN_DOOR_VERT = 91;
    public final static int OPEN_DOOR_GOR = 92;

    static int current_stage = -1;
    static int coords[] = {0, 0, 14, 20};   // Begin X,Y End X,Y
    static int[][] stage;


    public int[] getCoords(int lvl) {
        if (current_stage != lvl) getStage(lvl);
        return coords;
    }

    public int[][] getStage(int lvl) {

        Runtime.getRuntime().gc();
	int [][]ret = null;
        try
        {
           DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("/res/map"+lvl));
	   ret = new int[ds.readUnsignedByte()][ds.readUnsignedByte()];
	   for(int i =0;i<coords.length;i++)
	      coords[i] = ds.readUnsignedByte();
	   for(int i =0;i<ret.length;i++)
	     for(int j =0;j<ret[i].length;j++)
	         ret[i][j] = ds.readUnsignedByte();
        } catch(Exception e) {
	  ret = null;
	  }
	if(ret!=null && ret.length==0)ret = null;
	return ret;
    }

}
