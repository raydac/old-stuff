package RImage; 

class LZWStringTable
{

    public LZWStringTable()
    {
        strChr_ = new byte[4096];
        strNxt_ = new short[4096];
        strHsh_ = new short[9973];
    }

    public int AddCharString(short word0, byte byte0)
    {
        if(numStrings_ >= 4096)
            return 65535;
        int i;
        for(i = Hash(word0, byte0); strHsh_[i] != -1; i = (i + 2039) % 9973);
        strHsh_[i] = numStrings_;
        strChr_[numStrings_] = byte0;
        strNxt_[numStrings_] = word0 != -1 ? word0 : -1;
        return numStrings_++;
    }

    public short FindCharString(short word0, byte byte0)
    {
        if(word0 == -1)
            return (short)byte0;
        int j;
        for(int i = Hash(word0, byte0); (j = strHsh_[i]) != -1; i = (i + 2039) % 9973)
            if(strNxt_[j] == word0 && strChr_[j] == byte0)
                return (short)j;

        return -1;
    }

    public void ClearTable(int i)
    {
        numStrings_ = 0;
        for(int j = 0; j < 9973; j++)
            strHsh_[j] = -1;

        int k = (1 << i) + 2;
        for(int l = 0; l < k; l++)
            AddCharString((short)-1, (byte)l);

    }

    public static int Hash(short word0, byte byte0)
    {
        return (((short)(byte0 << 8) ^ word0) & 0xffff) % 9973;
    }

    private static final int RES_CODES = 2;
    private static final short HASH_FREE = -1;
    private static final short NEXT_FIRST = -1;
    private static final int MAXBITS = 12;
    private static final int MAXSTR = 4096;
    private static final short HASHSIZE = 9973;
    private static final short HASHSTEP = 2039;
    byte strChr_[];
    short strNxt_[];
    short strHsh_[];
    short numStrings_;
}
