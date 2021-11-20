package com.igormaznitsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class TESTPacker
{

    public static final byte[] encodeArray(byte[] _incommingArray) throws Throwable
    {
        ByteArrayOutputStream p_outStr = new ByteArrayOutputStream(_incommingArray.length);

        int i_curVal = -1;
        int i_curLength = 0;

        for(int li=0;li<_incommingArray.length;li++)
        {
            int i_val = _incommingArray[li] & 0xFF;
            if (i_curVal < 0)
            {
                i_curVal = i_val;
                i_curLength = 1;
            }
            else
            {
                if (i_curVal == i_val)
                {
                    i_curLength ++;
                    if (i_curLength == 0xFF)
                    {
                        p_outStr.write(i_curVal);
                        p_outStr.write(i_curLength);
                        i_curVal = -1;
                        i_curLength = 0;
                    }
                }
                else
                {
                        p_outStr.write(i_curVal);
                        p_outStr.write(i_curLength);
                        i_curVal = i_val;
                        i_curLength = 1;
                }
            }
        }

        if (i_curLength > 0)
        {
            p_outStr.write(i_curVal);
            p_outStr.write(i_curLength);
        }

        return p_outStr.toByteArray();
    }

    public static final void main(String[] _args) throws Throwable
    {
        File p_file = new File("d:/purewavelet.dat");
        byte[] ab_array = new byte[(int) p_file.length()];

        new FileInputStream(p_file).read(ab_array);

        ab_array = encodeArray(ab_array);

        new FileOutputStream("d:/testpacker.dat").write(ab_array);

        System.out.println("Array encoded into " + ab_array.length);
    }

}
