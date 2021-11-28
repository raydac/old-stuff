// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.io.*;

class BitFile
{
    OutputStream output_;
    byte buffer_[];
    int index_;
    int bitsLeft_;

    public BitFile(OutputStream outputstream)
    {
        output_ = outputstream;
        buffer_ = new byte[256];
        index_ = 0;
        bitsLeft_ = 8;
    }

    public void Flush()
        throws IOException
    {
        int i = index_ + (bitsLeft_ == 8 ? 0 : 1);
        if(i > 0)
        {
            output_.write(i);
            output_.write(buffer_, 0, i);
            buffer_[0] = 0;
            index_ = 0;
            bitsLeft_ = 8;
        }
    }

    public void WriteBits(int i, int j)
        throws IOException
    {
        int k = 0;
        char c = '\377';
        do
        {
            if(index_ == 254 && bitsLeft_ == 0 || index_ > 254)
            {
                output_.write(c);
                output_.write(buffer_, 0, c);
                buffer_[0] = 0;
                index_ = 0;
                bitsLeft_ = 8;
            }
            if(j <= bitsLeft_)
            {
                buffer_[index_] |= (byte)((i & (1 << j) - 1) << 8 - bitsLeft_);
                k += j;
                bitsLeft_ -= j;
                j = 0;
            } else
            {
                buffer_[index_] |= (byte)((i & (1 << bitsLeft_) - 1) << 8 - bitsLeft_);
                k += bitsLeft_;
                i >>= bitsLeft_;
                j -= bitsLeft_;
                buffer_[++index_] = 0;
                bitsLeft_ = 8;
            }
        } while(j != 0);
    }

}
