package com.igormaznitsa.midp.Rms;

public class RMSException extends Exception
{
    public int Error_Code = 0;

    public RMSException(int code)
    {
        super("RMS error #" + code);
        Error_Code = code;
    }
}