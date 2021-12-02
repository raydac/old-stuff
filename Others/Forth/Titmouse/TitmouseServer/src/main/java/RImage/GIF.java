package RImage; 

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.*;

public class GIF
{

    public GIF()
    {
    }

    public void compress(RImage image, DataOutputStream dataoutputstream, boolean flag)
    {
        interlaced = flag;
        width_ = (short)image.GetImageWidth();
        height_ = (short)image.GetImageHeight();
        //int i = 0;
        pixels_ = image.ImageArray; 
        colors_ = image.Palette.Palette; 
		numColors_ = 1 << BitUtils.BitsNeeded(255);
        byte abyte3[] = new byte[numColors_ * 3];
        System.arraycopy(colors_, 0, abyte3, 0, numColors_ * 3);
        colors_ = abyte3;
        try
        {
            write(dataoutputstream);
        }
        catch(Exception _ex) { }
    }

    public void write(OutputStream outputstream)
        throws IOException
    {
        BitUtils.WriteString(outputstream, "GIF87a");
        ScreenDescriptor screendescriptor = new ScreenDescriptor(width_, height_, numColors_);
        screendescriptor.Write(outputstream);
        outputstream.write(colors_, 0, colors_.length);
        ImageDescriptor imagedescriptor = new ImageDescriptor(width_, height_, ',', interlaced);
        imagedescriptor.Write(outputstream);
        byte byte0 = BitUtils.BitsNeeded(numColors_);
        if(byte0 == 1)
            byte0++;
        outputstream.write(byte0);
        LZWCompressor.LZWCompress(outputstream, byte0, pixels_);
        outputstream.write(0);
        imagedescriptor = new ImageDescriptor((short)0, (short)0, ';', interlaced);
        imagedescriptor.Write(outputstream);
        outputstream.flush();
    }

    short width_;
    short height_;
    int numColors_;
    byte pixels_[];
    byte colors_[];
    ScreenDescriptor sd_;
    ImageDescriptor id_;
    boolean interlaced;
}
