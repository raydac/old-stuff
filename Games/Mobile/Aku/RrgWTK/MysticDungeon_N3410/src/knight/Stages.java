package knight;

import java.io.*;

public class Stages {

    /*
        ---- Map format ----
        00      , floor
        01-09   , wall
        10-19   , vertical door  [ door 10 , door 11 , ...
        20-29   , horizontal door
        30-49   , RESERVED !!!

        НЕ БОЛЬШЕ 5ти ключей на уровень !!!!!!!!!!!!!
        50-69   , key   [ key 30 its key for door 10 , key 31 its key for door 11 , ...

        70-89   , floor button   [ button 50 can open door 10 , button 51 can open door 11 , ...
        90      , Pushed button
        91      , Opened vertical door
        92      , Opened horizontal door
        93-109  , Guardian

        110-127 , Treasures

        150-151 , Electroshok
    */
    public final static int TOTAL_STAGES = 11;

    // wall
    public final static int FLOOR = 0;
    public final static int WALL0 = 1;
    public final static int WALL1 = 2;
    public final static int WALL2 = 3;

    // treasure


    public final static int SHORT_DAGGER  =   110; // Atk +2
    public final static int SHORT_SWORD   =   111; // Atk +5
    public final static int LONG_SWORD    =   112; // Atk +5
    public final static int MAGIC_SWORD   =   113; // Atk +10
    public final static int SCALE_ARMOR   =   114; // Arm +2
    public final static int PLATE_ARMOR   =   115; // Arm +4
    public final static int PLATE_SHIELD  =   116; // Def +15


    public final static int FIRST_AAD_ITEM_IDX = SHORT_DAGGER;
    public final static int TOTAL_AAD_ITEMS = PLATE_SHIELD - FIRST_AAD_ITEM_IDX +1;

    public final static int ATK      =   0;
    public final static int ARM      =   1;
    public final static int DEF      =   2;

    public final static byte[][] AAD = {
                            { +2, 0, 0 }, // FIST
                            { +1, 0, 0 }, // SHORT_DAGGER
                            { +3, 0, 0 }, // SHORT_SWORD
                            { +4, 0, 1 }, // LONG_SWORD
                            { +6, 0, 0 }, // MAGIC_SWORD
                            {  0,-1, 0 }, // SCALE_ARMOR
                            {  0,-2, 0 }, // PLATE_ARMOR
                            {  0, 0, 3 }, // PLATE_SHIELD
    };

    // action

    public final static int ELECTROSHOCK = 150;
    public final static int ELECTROSHOCK_CLS = 151;
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
