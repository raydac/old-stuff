package com.igormaznitsa.WToolkit.classes;

public class ExtAppStructure
{
    public int Status;
    public byte[] outStreamArray;
    public byte[] errStreamArray;

    public ExtAppStructure(int _state,byte [] _out,byte[] _err)
    {
        Status = _state;
        outStreamArray = _out;
        errStreamArray = _err;
    }
}
