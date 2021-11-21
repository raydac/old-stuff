import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Stages
{
    public static final byte ELEMENT_WATER = 0;
    public static final byte ELEMENT_NONBREAKABLEBEACH = 1;
    public static final byte ELEMENT_BREAKABLEBEACH = 2;
    public static final byte ELEMENT_RADAR = 3;
    public static final byte ELEMENT_ARTILLERY1 = 4;
    public static final byte ELEMENT_ARTILLERY2 = 5;
    public static final byte ELEMENT_LEFTTANK = 7;
    public static final byte ELEMENT_RIGHTTANK = 6;
    public static final byte ELEMENT_WATERMINE = 8;
    public static final byte ELEMENT_LINEARSHEEP = 9;

    public static final int PARTCELLWIDTH = 44;
    public static final int PARTCELLHEIGHT = 4;

    private static byte [] ab_linkArray;           // Levels
    private static byte [][] ab_partArray;           // all blocks from 0 to 100
    private static byte [] ab_decoration;

    public static byte [][] getPartArrayForStage(int _stage)
    {
        if(ab_partArray != null) return ab_partArray;

        Runtime.getRuntime().gc();
        try
        {
           DataInputStream ds = new DataInputStream((new Object()).getClass().getResourceAsStream("/res/data.bin"));
           int amount = ds.readUnsignedByte();
           int len = ds.readShort();
           ab_partArray = new byte[amount][];
           for(int i=0;i<amount;i++)
           {
      ab_partArray[i] = new byte[len];
             ds.read(ab_partArray[i]);
           }
        } catch(Exception e) {
   ab_partArray = null;
          // #if DEBUG
            e.printStackTrace();
            System.out.println("Name of requested resource: /res/data.bin");
          // #endif
 }
       return ab_partArray;
    }

    public static byte [] getLinkPartArrayForStage(int _stage)
    {

        ab_linkArray = loadData("/res/data"+(++_stage)+".bin");
        return ab_linkArray;
    }

    public static byte [] getDecorationForStage(int _stage)
    {
        ab_decoration = loadData("/res/level"+(++_stage)+".bin");
        return ab_decoration;
    }

    private final static byte[] loadData(String name)
    {
        Runtime.getRuntime().gc();
 byte []ret = null;
        try
        {
           DataInputStream ds = new DataInputStream((new Object()).getClass().getResourceAsStream(name));
    ret = new byte[ds.readShort()];
           ds.read(ret);
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
