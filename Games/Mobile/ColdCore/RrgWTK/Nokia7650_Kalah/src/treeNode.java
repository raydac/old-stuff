public class treeNode
{
   private static final int  MOVE_SIZE = 30;
   public static final int  DESK_SIZE = 14;


   public int          extent;
   public int          move[];
   public int          desk[];
                    /*	A (0..6)  B (7..13).	*/
   public treeNode()
   {
      move = new int[MOVE_SIZE];
      desk = new int[DESK_SIZE];
   }

   public void copyInstance(treeNode src)
   {
     int  i;
     extent = src.extent;
     for (i = 0; i < MOVE_SIZE; i++)
       move[i] = src.move[i];
     for (i = 0; i < DESK_SIZE; i++)
       desk[i] = src.desk[i];
   }

}
