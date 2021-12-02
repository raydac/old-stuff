package com.raydac_research.RRGImagePacker.tga;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class TGAImageWriter
{
    public static final void WriteImageAsARGB(BufferedImage _image,String _fileName) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_fileName);
        WriteImageAsARGB(_image,p_fos);
        p_fos.flush();
        p_fos.close();
    }

    public static final void WriteImageAsARGB(BufferedImage _image,OutputStream _stream) throws IOException
    {
        if (_image==null) throw new IOException("Image is null");
        if (_image.getType() != BufferedImage.TYPE_INT_ARGB) throw new IOException("You must use ARGB images only!");

        DataOutputStream p_Dos = new DataOutputStream(_stream);

        // Writing header
        p_Dos.writeByte(0); // Writing ID
        p_Dos.writeByte(0); // Writing color map
        p_Dos.writeByte(2); // Writing image type (realistic colours)

        // Writing colour map
        p_Dos.writeShort(0); // The first element of color map
        p_Dos.writeShort(0); // Color map length
        p_Dos.writeByte(32); // Size of color map element

        // Writing of video data
        p_Dos.writeShort(0); // X
        p_Dos.writeShort(0); // Y

        p_Dos.writeByte(_image.getWidth()); // image width
        p_Dos.writeByte(_image.getWidth()>>>8); // image width

        p_Dos.writeByte(_image.getHeight()); // image height
        p_Dos.writeByte(_image.getHeight()>>>8); // image height

        p_Dos.writeByte(32); // bits per pixel
        p_Dos.writeByte(0x20); // image reference bits (left-right up-down)

        int[] ai_ImageBuffer = ((DataBufferInt) _image.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length;

        for (int li=0;li<i_len;li++)
        {
            int i_argb = ai_ImageBuffer[li];
            int i_A = i_argb >>> 24;
            int i_R = (i_argb >>> 16) & 0xFF;
            int i_G = (i_argb >>> 8) & 0xFF;
            int i_B = i_argb & 0xFF;

            p_Dos.writeByte(i_B);
            p_Dos.writeByte(i_G);
            p_Dos.writeByte(i_R);
            p_Dos.writeByte(i_A);
        }

        p_Dos.flush();
        p_Dos = null;
    }
}
