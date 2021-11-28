// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.io.*;
import java.awt.*; 
import java.awt.image.*;  

public class BMPImage extends RImage 
{
	public int bfSize ;
	public int bfOffbits;
	public int biSize;
	public int biWidth;
	public int biHeight;
	public short biPlanes;
	public short biBitCount;
	public int biCompression;
	public int biSizeImage;
	public int biXPelsPerMeter;
	public int biYPelsPerMeter;
	public int biClrUsed;
	public int biClrImportant;
	public int [] image_array=null;
	
	public int getWidth(){ return biWidth;}
	public int getHeight(){ return biHeight;}
	public int [] getImageArray(){ return image_array;} 
	
	public BMPImage(byte[] arr) throws IOException
	{
		decode(arr);
	}
	
	public static byte [] encodeImage(Image inimg) throws IOException 
	{
		int lwidth = inimg.getWidth(null);
		int lw = lwidth>>3;
		int lnw = lw;
		switch((lwidth&31)>>3)
		{
			case 1:	lnw =lw+3;break;
			case 2:	lnw =lw+2;break;
			case 3:	lnw =lw+1;break;
		}
		byte [] imgdata = new byte[lnw*inimg.getHeight(null)];

		int [] imggrab = new int[inimg.getWidth(null)*inimg.getHeight(null)];  
		PixelGrabber pxg = new PixelGrabber(inimg,0,0,inimg.getWidth(null),inimg.getHeight(null),imggrab,0,inimg.getWidth(null));
		try
		{
			pxg.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return null;	
		}
		
		int lry = 0;
		int clrblck = Color.black.getRGB();
		for (int ly=inimg.getHeight(null)-1;ly>=0;ly--)
		{
			int accum = 0;
			liy0:
			{
				int lbc = 0;
				
				for (int lx=0;lx<lw;lx++)
				{
					int lmask = 0x80;
					accum = 0;
					for (int lmc=0;lmc<8;lmc++)
					{
						int curcolor = imggrab[(lry*lwidth)+lmc+(lx<<3)];
						if (checkColor(curcolor,clrblck))
						{
							accum |= lmask;	
						}
						lbc++;
						if (lbc==lwidth) 
						{
							imgdata[ly*lnw+lx]=(byte)accum;
							break liy0;
						}
						lmask = lmask>>>1;
					}
					imgdata[ly*lnw+lx]=(byte)accum;
				}
			}
			lry++;
		}
		imggrab=null; 
		pxg=null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		DataOutputStream dos = new DataOutputStream(bos);  
		
		// File type
		dos.writeShort(0x424d); 
		// Size of the file
		dos.writeInt(reverseInt(62+imgdata.length));
		// Reserved
		dos.writeInt(0);
		// Offset of the image data
		dos.writeInt(reverseInt(62));
		// Size of the header
		dos.writeInt(reverseInt(40));
		// Width of the image
		dos.writeInt(reverseInt(inimg.getWidth(null)));
		// Height of the image
		dos.writeInt(reverseInt(inimg.getHeight(null)));
		// Color planes count
		dos.writeShort(reverseShort((short)1));
		// Bits per pixel
		dos.writeShort(reverseShort((short)1));
		// Compression
		dos.writeInt(0);
		// Size of bitmap
		dos.writeInt(0);
		// Horizontal resolution
		dos.writeInt(reverseInt(24));
		// Vertical resolution
		dos.writeInt(reverseInt(24));
		// Color used
		dos.writeInt(reverseInt(0));
		// Colors important
		dos.writeInt(reverseInt(2));

		// Color palette
		// Color 1
		dos.write(102);
		dos.write(204);
		dos.write(102);
		dos.write(0);
		
		// Color 2
		dos.write(0);
		dos.write(0);
		dos.write(0);
		dos.write(0);
		
		dos.write(imgdata);  
		dos.flush(); 
		return bos.toByteArray();
	}
	
	public byte getByte(int src,int byten)
	{
		switch(byten)
		{
			case 0 : return (byte)(src&0xFF);
			case 1 : return (byte)((src>>8)&0xFF);
			case 2 : return (byte)((src>>16)&0xFF);
			case 3 : return (byte)((src>>24)&0xFF);
			default : return -1;
		}
	}
	
	protected void decode(byte [] bmp) throws IOException 
	{
		DataInputStream dis = new DataInputStream (new ByteArrayInputStream(bmp));
		if (dis.readShort()!=0x424d) throw new IOException("Unknown data format"); 
		// Reading of file size
		bfSize = reverseInt(dis.readInt());
		// Reading of two reserved words
		dis.readInt();
		// Reading offset value of image bits from the header
		bfOffbits = reverseInt(dis.readInt());
		
		// Reading of bitmap info header size
		biSize = reverseInt(dis.readInt());
		biWidth = reverseInt(dis.readInt());
		biHeight = reverseInt(dis.readInt());
		biPlanes = reverseShort(dis.readShort());
		biBitCount = reverseShort(dis.readShort());
		biCompression = reverseInt(dis.readInt());
		biSizeImage = reverseInt(dis.readInt());
		biXPelsPerMeter = reverseInt(dis.readInt());
		biYPelsPerMeter = reverseInt(dis.readInt());
		biClrUsed = reverseInt(dis.readInt());
		biClrImportant = reverseInt(dis.readInt()); 
		
		if (biCompression!=0) throw new IOException("Unsupported compression format");
		if (biBitCount!=1) throw new IOException("Unsupported color resolution");

		// Reading of colors
		dis.readInt();
		dis.readInt();
		
		// Creating byte array of the image
		image_array = new int[biWidth*biHeight];

		int limitx = biWidth>>3;
		switch(biWidth&3)
		{
			case 1 : limitx +=3; break;
			case 2 : limitx +=2; break;
			case 3 : limitx +=1; break;
		}
		
		
		// Reading of image data
		for (int ly=biHeight-1;ly>=0;ly--)
		{
			liy:
			{
				int lbc = 0;
				for (int lx=0;lx<limitx;lx++)
				{
					int ldata = dis.readInt(); 
					int lmask = 0x80000000;
					for (int lmc = 0;lmc<32;lmc++)
					{
						if ((lmask&ldata)!=0) image_array [lbc+ly*biWidth] = 0xFFFFFF; else image_array[lbc+ly*biWidth] = 0x0;
						lbc++;
						if (lbc==biWidth) break liy;
						lmask = lmask>>>1;
					}
				}
			}
		}
	}

}
