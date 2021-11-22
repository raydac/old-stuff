package fire;

import java.io.*;
import com.igormaznitsa.gameapi.Gamelet;


public class Stage
{
 public static final int EMPTY = 0;              //  0 - пусто                                       ,
 public static final int GROUND = 1;             //  1 - земля (всегда внизу!!!)                     , ground.gif
 public static final int GRASS0 = 2;             //  2 - травка                                      , grass01.gif
 public static final int GRASS1 = 3;             //  3 - травка                                      , grass02.gif
 public static final int PIPE_LEFT = 4;          //  4 - Труба левый край                            , pipe_end_left.gif
 public static final int PIPE_RIGHT = 5;         //  5 - Труба правй край                            , pipe_end_right.gif
 public static final int PIPE_MIDL1 = 6;         //  6 - Труба середина , первая часть (Она занимает 2 клетки !!!)  , pipe.gif
 public static final int BLOCK0 = 7;             //  7 - Блок 1                                      , block01.gif
 public static final int TREE0 = 8;              //  8 - Дерево 1                                    , tree01.gif
 public static final int TREE1 = 9;              //  9 - Дерево 2                                    , tree02.gif
 public static final int TREE2 = 10;             // 10 - Дерево 3                                    , tree03.gif
 public static final int HOUSE_WINDOWS0 = 11;    // 11 - Часть дома 1                                , house_block01.gif
 public static final int HOUSE_DOOR0 = 12;       // 12 - Часть дома 2                                , house_block02.gif
 public static final int HOUSE_WINDOWS1 = 13;    // 13 - Часть дома 3                                , house_block03.gif
 public static final int HOUSE_WALL0 = 14;       // 14 - Часть дома 4                                , house_block04.gif
 public static final int HOUSE_WINDOWS2 = 15;    // 15 - Часть дома 5                                , house_block05.gif
 public static final int HOUSE_DOOR1 = 16;       // 16 - Часть дома 6                                , house_block06.gif
 public static final int HOUSE_WALL1 = 17;       // 17 - Часть дома 7                                , house_block07.gif
 public static final int HOUSE_ROOF = 18;        // 17 - Часть дома 7                                , house_roof.gif
 public static final int HEART = 19;             // Сердечко                                         ,
 public static final int PIPE_MIDL2 = 20;        //  6 - Труба середина , вторая часть(Она занимает 2 клетки !!!)  , pipe.gif
/*
   0  - пусто                                       ,
   1  - земля (всегда внизу!!!)                     , ground.gif
   2  - травка                                      , grass01.gif
   3  - травка                                      , grass02.gif
   4  - Труба левый край                            , pipe_end_left.gif
   5  - Труба правй край                            , pipe_end_right.gif
   6  - Труба середина (Она занимает 2 клетки !!!)  , pipe.gif
   7  - Блок 1                                      , block01.gif
   8  - Дерево 1                                    , tree01.gif
   9  - Дерево 2                                    , tree02.gif
   10 - Дерево 3                                    , tree03.gif
   11 - Часть дома 1                                , house_block01.gif
   12 - Часть дома 2                                , house_block01.gif
   13 - Часть дома 3                                , house_block01.gif
   14 - Часть дома 4                                , house_block01.gif
   15 - Часть дома 5                                , house_block01.gif
   16 - Часть дома 6                                , house_block01.gif
   17 - Часть дома 7                                , house_block01.gif
   18 - Часть дома 8
   19 - Жизня                                       , heart.gif
   20 - Вторая часть трубы


   WARNING!!!!
   Не должно быть площадок < 4 элементов подряд , лучше 5 ;)
 */
 public static int prm[] = {0,1,0,0,1, 1,1,1,0,0, 0,0,0,0,0, 0,0,0,0,0, 1}; // Проходимость картинок

 public static int HEALTH = 19;

 public static final int LEVEL_HEIGHT = 5;

 public static final int TOTAL_STAGES = 4;

 // Высота всегда 4 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public int[][] getStage(int lvl) {

        Runtime.getRuntime().gc();
	int [][]ret = null;
        try
        {
           DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("/res/map"+lvl));
	   byte []tmp = new byte[ds.readUnsignedShort()];
	   for(int i =0;i<tmp.length;i++)
	         tmp[i] = ds.readByte();

           tmp = Gamelet.RLEdecompress(tmp, (int)ds.readShort());
           int x = (tmp.length / 5);
           ret = new int[5][x];

           for (int y = 0; y < tmp.length; y++)
             ret [y / x][y % x] = tmp[y];

        } catch(Exception e) {
	  ret = null;
	  }
	if(ret!=null && ret.length==0)ret = null;
	return ret;
    }

}
