import java.io.*;
public class Stages
{
    public static final int ELE_EMPTY = 0;
    public static final int ELE_STONE = 1;
    public static final int ELE_STAIR = 2;
    public static final int ELE_BRILLIANT = 3;
    public static final int ELE_KEY = 4;
    public static final int ELE_DOOR = 5;
    public static final int ELE_REDMUMMY = 6;
    public static final int ELE_YELLOWMUMMY = 7;
    public static final int ELE_BLACKMUMMY = 8;
    public static final int ELE_HAMMER = 9;
    public static final int ELE_PLAYER = 10;
    public static final int ELE_VESSEL = 11;
    public static final int ELE_STAKES = 12;
    public static final int ELE_PICK   = 13;
    public static final int ELE_BOULDER= 14;

    public static int i_stageWidth = 0;
    public static int i_stageHeight = 0;
    public static byte[] ab_newArray = null;
    public static byte[] ab_decoration = null;

    public static int getLastStageWidth()
    {
        return i_stageWidth;
    }

    public static int getLastStageHeight()
    {
        return i_stageHeight;
    }

    public static byte [] getStage(int _stage)
    {
        byte []a = getStage("/res/level"+(_stage+1)+".bin");
        i_stageWidth = a[0]&0xff;
        i_stageHeight = a[1]&0xff;
        ab_newArray = new byte[a.length-2];
        System.arraycopy(a,2,ab_newArray,0,a.length-2);
        a=null;
        ab_decoration = getStage("/res/Level"+(_stage+1)+"s.bin");
        return ab_newArray;
    }
    public static byte[] getStage(String name) {
        Runtime.getRuntime().gc();
 byte []ret = null;
        try
        {
           InputStream is = (new Object()).getClass().getResourceAsStream(name);
           DataInputStream ds = new DataInputStream(is);
    ret = new byte[ds.readShort()];
           int size = ds.readShort();
           ds.read(ret);
           ret = Gamelet.RLEdecompress(ret,size);
        } catch(Exception e) {
   ret = null;
          // #if DEBUG
            e.printStackTrace();
            System.out.println("Name of requested resource:"+name);
          // #endif
 }
 return ret;
    }

}
