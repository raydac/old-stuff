/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 01.04.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogoEditor.graphics;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

public class BMPImage
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

    public static boolean checkColor(int color1,int color2)
	{
		color1 = color1 & 0xE0E0E0;
		color2 = color2 & 0xE0E0E0;
		return color1==color2;
	}

	public static int reverseInt(int in)
	{
		return ((in & 0xFF)<<24)|((in & 0xFF00)<<8)|((in & 0xFF0000)>>8)|((in & 0xFF000000)>>24);
	}

	public static short reverseShort(short in)
	{
		return (short)(((in & 0xFF)<<8)|((in & 0xFF00)>>8));
	}

    public void decode(byte [] bmp) throws IOException
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
