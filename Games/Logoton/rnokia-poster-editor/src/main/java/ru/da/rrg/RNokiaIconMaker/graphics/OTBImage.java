// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.io.*;
import java.awt.*; 
import java.awt.image.*;  

public class OTBImage extends RImage 
{
	public static final int IF_CONCATENATIONFLAG = 0x80;
	public static final int IF_COMPRESSION = 0x40;
	public static final int IF_EXTERNALPALETTE = 0x20;
	public static final int IF_MAXSIZEOFICON = 0x10;
	public static final int IF_NUMBEROFANIMATEDICONS = 0x0F;
	
	public static final int EF0_CONCATENATIONFLAG = 0x80;
	public static final int EF0_BITMAPVER_r4 = 0x70;
	public static final int EF0_RESERVED = 0x0F;
	
	public static final int EF1_CONCATENATIONFLAG = 0x80;
	public static final int EF1_RESERVED = 0x7F;
	
	public int width;
	public int height;
	public int [] image_array=null;
	
	public int[] getImageArray(){return image_array;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	
	public static int reverseInt(int in)
	{
		return (in & 0xFF)|((in >>8)& 0xFF)|((in>>16)&0xFF)|((in>>24)&0xFF);
	}

	public static short reverseShort(short in)
	{
		return (short)((in & 0xFF)|((in >>8)& 0xFF));
	}
	
	public OTBImage(byte[] arr) throws IOException
	{
		decode(arr);
	}
	
	public static byte [] encodeImage(Image inimg) throws IOException 
	{
		ByteArrayOutputStream barr =new ByteArrayOutputStream();
		DataOutputStream baos = new DataOutputStream(barr);
		// Creating Infofield
		int inffld = 0;
		boolean longWH = false;
		if ((inimg.getWidth(null)>0xFF)|(inimg.getHeight(null)>0xFF))
		{
			longWH = true;
			inffld|=IF_MAXSIZEOFICON; 
		}
		baos.write(inffld);
		if (longWH)
		{
			baos.writeShort(inimg.getWidth(null));
			baos.writeShort(inimg.getHeight(null));
		}
		else
		{
			baos.write(inimg.getWidth(null));
			baos.write(inimg.getHeight(null));
		}
		
		int [] imgarr = new int[inimg.getWidth(null)*inimg.getHeight(null)]; 
		baos.write(1);
		PixelGrabber pxg = new PixelGrabber(inimg,0,0,inimg.getWidth(null),inimg.getHeight(null),imgarr,0,inimg.getWidth(null));
		try
		{
			pxg.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return null;	
		}
		
		int clrblck = Color.black.getRGB(); 
		for(int ly=0;ly<inimg.getHeight(null);ly++)
		{
			int lbx=0;
			bry:	
			{
				int lxl = inimg.getWidth(null)>>3;
				if ((inimg.getWidth(null)&0x03)!=0)lxl++;
				for(int lx=0;lx<lxl;lx++)
				{
					int lms = 0x80;
					int lba = 0;
					for(int lmr=0;lmr<8;lmr++)
					{
						if (checkColor(imgarr[(lx<<3)+lmr+ly*inimg.getWidth(null)],clrblck))
						{
							lba|=lms;						
						}
						lms=lms>>>1;
						lbx++;
						if (lbx==inimg.getWidth(null))
						{
							baos.write(lba);
							break bry;
						}
					}
					baos.write(lba);
				}
			}
		}
		return  barr.toByteArray(); 
	}
	
	protected void decode(byte [] otaarray) throws IOException 
	{
		DataInputStream dis = new DataInputStream (new ByteArrayInputStream(otaarray));
		int ifld = 0;
		int ef0 = 0;
		int ef1 = 0;
		// reading Infofield
		ifld = dis.read(); 
		if ((ifld&IF_CONCATENATIONFLAG)!=0)
		{
			ef0 = dis.read();
			if (((ef0&EF0_BITMAPVER_r4)>>4)!=1) throw new IOException("Unsupported version"); 
			if ((ef0&EF0_CONCATENATIONFLAG)!=0)
			{
				ef1 = dis.read(); 	
			}
		}
		if ((ifld&IF_MAXSIZEOFICON)!=0)
		{
			width = dis.readShort();
			height = dis.readShort();
		}
		else
		{
			width = dis.read();
			height = dis.read();
		}
		// Reading of the depth parameter
		dis.read(); 
		
		image_array = new int[width*height]; 
		// Reading image data
		int lwb = width>>3;
		if ((width&0x7)!=0) lwb++;
		for(int ly=0;ly<height;ly++)
		{
			int lwa=0;
			brx :
			{
				for(int lx=0;lx<lwb;lx++)
				{
					int bt = dis.read();
					int msk = 0x80;
					for(int lm=0;lm<8;lm++)
					{
						if ((msk&bt)!=0) image_array [lm+(lx<<3)+(ly*width)]=0xFFFFFFFF;
							else image_array [lm+(lx<<3)+(ly*width)]=0x00; 
						msk=msk>>>1;
						lwa++;
						if (lwa==width) break brx;
					}
				}
			}
		}
	}

}
