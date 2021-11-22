package knight;

import com.itx.mbgame.GameObject;


public class Guard extends GameObject
    {
        int losthpcounter = 0;
        int ID;
        int defaultDir; // 0 - horizontal , 1 - vertical
        int HP;
        public boolean inAttack = false;

        public Guard(int w, int h, boolean acv)
        {
            super(w, h, acv);
        }

        public int getArrayLength()
        {
            return GameObject.getStaticArrayLength()+3;
        }

        public int[] getMeAsByteArray()
        {
            int ret[] = new int[3 + GameObject.getStaticArrayLength()];
            ret[0] = ID;
            ret[1] = defaultDir;
            ret[2] = HP;
            System.arraycopy(super.getMeAsByteArray(), 0, ret, 3, GameObject.getStaticArrayLength());
            return ret;
        }

        public void setMeAsByteArray(int[] ret)
        {
            ID = ret[0];
            defaultDir = ret[1];
            HP = ret[2];
            int tmp[] = new int[GameObject.getStaticArrayLength()];
            System.arraycopy(ret, 3, tmp, 0, GameObject.getStaticArrayLength());
            super.setMeAsByteArray(tmp);
        }

    }
