// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

public abstract class RImage
{
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

	public abstract int getWidth();
	public abstract int getHeight();
	public abstract int [] getImageArray();
}
