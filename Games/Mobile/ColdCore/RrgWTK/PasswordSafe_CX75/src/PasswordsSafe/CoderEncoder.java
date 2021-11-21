package PasswordsSafe;

import java.util.Random;

public class CoderEncoder
{
    private static int[] ai_crctable;

    static
    {
        ai_crctable = new int[256];
        for (int n = 0; n < 256; n++)
        {
            int c = n;
            for (int k = 0; k < 8; k++)
            {
                if ((c & 1) != 0)
                    c = 0xedb88320 ^ (c >>> 1);
                else
                    c = c >>> 1;
            }
            ai_crctable[n] = c;
        }
    }

    private static final int calculateCRC32(byte[] _array, int _startpos, int _len)
    {
        int c = -1;
        int i_end = _startpos + _len;

        for (int n = _startpos; n < i_end; n++)
        {
            c = ai_crctable[(c ^ _array[n]) & 0xff] ^ (c >>> 8);
        }
        return c ^ (0xFFFFFFFF);
    }


    public static byte [] generateKeyFromString(String _name,String _password)
    {
        byte [] ab_key = new byte[64];

        int i_NameHash = _name.hashCode();
        int i_PasswordHash = _name.hashCode();

        Random p_rnd = new Random(((long)i_NameHash<<32)|((long)i_PasswordHash));

        for(int li=0;li<ab_key.length;li++)
        {
            byte b_nextByte = (byte) (((((long) Math.abs(p_rnd.nextInt())) * (long) 256) >>> 31)^0xAA);
            ab_key[li] = b_nextByte;
        }

        return ab_key;
    }

    public static  byte [] encodeArray(byte [] _inData,byte [] _key)
    {
        int i_crc = calculateCRC32(_inData,0,_inData.length);

        int i_index = 0;

        byte [] ab_result = new byte[_inData.length+4];
        System.arraycopy(_inData,0,ab_result,4,_inData.length);

        ab_result [0] = (byte)(i_crc >>> 24);
        ab_result [1] = (byte)(i_crc >>> 16);
        ab_result [2] = (byte)(i_crc >>> 8);
        ab_result [3] = (byte)(i_crc);

        for(int li=4;li<ab_result.length;li++)
        {
            byte b_value = ab_result[li];
            byte b_key = _key[i_index++];
            if (i_index==_key.length) i_index = 0;
            b_value = (byte)(b_value ^ b_key);
            ab_result[li] = b_value;
        }

        return ab_result;
    }

    public static byte [] decodeArray(byte [] _inData,byte [] _key)
    {
        int i_crc = (_inData[0] & 0xFF)<<24;
        i_crc |= (_inData[1] & 0xFF)<<16;
        i_crc |= (_inData[2] & 0xFF)<<8;
        i_crc |= (_inData[3] & 0xFF);

        byte [] ab_result = new byte[_inData.length-4];

        int i_index = 0;

        for(int li=0;li<ab_result.length;li++)
        {
            byte b_value = _inData[li+4];
            byte b_key = _key[i_index++];
            if (i_index==_key.length) i_index = 0;
            b_value = (byte)(b_value ^ b_key);
            ab_result[li] = b_value;
        }

        int i_curCRC = calculateCRC32(ab_result,0,ab_result.length);
        if (i_curCRC!=i_crc) return null;
        return ab_result;
    }
}
