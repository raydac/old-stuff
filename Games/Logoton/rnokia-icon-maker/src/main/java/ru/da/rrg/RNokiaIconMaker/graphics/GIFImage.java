package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.awt.*;

public class GIFImage extends RImage
{
    int img_wdth = -1;
    int img_hght = -1;
    int [] imgarr = null;
    Component aprnt = null;
    
    public GIFImage(Component prnt,byte [] img) throws IOException
    {
        aprnt = prnt;
        decode(img);
    }
    
    public int getWidth()
    {
        return img_wdth;
    }
    
    public int getHeight()
    {
        return img_hght;
    }
    
    public int [] getImageArray()
    {
        return imgarr;
    }
    
    protected void decode(byte [] img) throws IOException
    {
        Image immg = Toolkit.getDefaultToolkit().createImage(img);
        
        MediaTracker mtr = new MediaTracker(aprnt);
        mtr.addImage(immg,0);
        try
        {
            mtr.waitForAll();
        }
        catch(InterruptedException exx){ return;}
        mtr.removeImage(immg);
        mtr = null;
        
        img_wdth = immg.getWidth(null);
        img_hght = immg.getHeight(null);
        imgarr = new int[img_wdth*img_hght];
        
        PixelGrabber pxg = new PixelGrabber(immg,0,0,img_wdth,img_hght,imgarr,0,img_wdth);
        try
        {
            pxg.grabPixels();
        }
        catch(InterruptedException exx)
        {
            return;
        }
        
		int clrblck = Color.black.getRGB();  
		
        for(int li = 0;li<imgarr.length;li++)
        {
            if (checkColor(imgarr[li],clrblck))
                imgarr[li]=0x00FFFFFF;
            else
                imgarr[li]=0x00;
        }
        
    }
    
    
    public static byte [] encodeImage(Image image,boolean interl,Color transcolor) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        boolean interlaced = interl;
        int imgwidth = (short)image.getWidth(null);
        int imgheight = (short)image.getHeight(null);
        int ai[] = new int[imgwidth * imgheight];
        int trnscolor = -1;
        int trnsRGB = -1;
        if (transcolor!=null)
        {
            trnsRGB = transcolor.getRGB()&0xFFFFFF;
        }
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, imgwidth, imgheight, ai, 0, imgwidth);
        try
        {
            pixelgrabber.grabPixels();
        }
        catch(InterruptedException _ex) 
        {
            return null;
        }
        byte abyte0[][] = new byte[imgwidth][imgheight];
        byte abyte1[][] = new byte[imgwidth][imgheight];
        byte abyte2[][] = new byte[imgwidth][imgheight];
        int i = 0;
        byte [] pixels_ = new byte[imgwidth * imgheight];
        byte [] colors_ = new byte[768];
        int ai1[] = new int[256];
        int l = 0;
        for(int i1 = 0; i1 < imgheight; i1++)
        {
            for(int j1 = 0; j1 < imgwidth; j1++)
            {
                int k = ai[i] & 0xffffff;
                if (trnsRGB>=0)
                {
                    if (trnscolor<0)
                    {
                        if (trnsRGB==k) trnscolor = l;
                    }
                }
                abyte0[j1][i1] = (byte)(k >>> 16);
                abyte1[j1][i1] = (byte)(k >>> 8);
                abyte2[j1][i1] = (byte)k;
                int j;
                for(j = 0; j < l; j++)
                    if(k == ai1[j])
                        break;
                
                if(j > 255)
                    System.err.println("Too many colors.");
                pixels_[i1 * imgwidth + j1] = (byte)j;
                if(j == l)
                {
                    ai1[j] = k;
                    colors_[j *= 3] = abyte0[j1][i1];
                    colors_[++j] = abyte1[j1][i1];
                    colors_[++j] = abyte2[j1][i1];
                    l++;
                }
                i++;
            }
            
        }
        
        int numColors_ = 1 << BitUtils.BitsNeeded(l);
        byte abyte3[] = new byte[numColors_ * 3];
        System.arraycopy(colors_, 0, abyte3, 0, numColors_ * 3);
        colors_ = abyte3;
        try
        {
            BitUtils.WriteString(dos, "GIF89a");
            ScreenDescriptor screendescriptor = new ScreenDescriptor((short)imgwidth, (short)imgheight, numColors_);
            screendescriptor.Write(dos);
            dos.write(colors_, 0, colors_.length);
            if (trnscolor<0) trnscolor = 0;
            GIFGraphicControlExtension ext = new GIFGraphicControlExtension(trnscolor);
            ext.Write(dos);
            GIFImageDescriptor imagedescriptor = new GIFImageDescriptor((short)imgwidth, (short)imgheight, ',', interlaced);
            imagedescriptor.Write(dos);
            byte byte0 = BitUtils.BitsNeeded(numColors_);
            if(byte0 == 1)   byte0++;
            dos.write(byte0);
            LZWCompressor.LZWCompress(dos, byte0, pixels_);
            dos.write(0);
//            imagedescriptor = new GIFImageDescriptor((short)0, (short)0, ';', interlaced);
//            imagedescriptor.Write(dos);
			dos.write((byte)';');
            dos.flush();
        }
        catch(IOException _ex) { }
        
        return baos.toByteArray();
    }
    
}
