package ru.da.rrg.RNokiaIconMaker.graphics;

import java.io.*;

public class GIFGraphicControlExtension 
{
    int trans_index;
    
    public GIFGraphicControlExtension(int trans) 
    {
        trans_index = trans;
    }

    public void Write(OutputStream outputstream) throws IOException
    {
        outputstream.write(0x21); // Introducer
        outputstream.write(0xF9); // Label
        outputstream.write(0x04); // Block size
        if (trans_index<0) outputstream.write(0x00); else outputstream.write(0x01); // Packed mode
        BitUtils.WriteWord(outputstream,(short)0); // Delay time
        outputstream.write(trans_index); // Index of transparance color
        outputstream.write(0x00); // Terminator
    }    
    
}
