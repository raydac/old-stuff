// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package com.igormaznitsa.LogoEditor;

import java.awt.*; 
import java.io.*;
import java.applet.*; 
import java.awt.image.*; 

public class RFont
{
	public static final int CHAR_MAX_WIDTH = 14;
	public static final int CHAR_MAX_HEIGHT = 14;
	public static final int CHAR_ARRAY_LENGTH = 80;
	
	protected Applet owner = null;
	protected int [] wdth_arr = null;
	protected byte [] img_arr = null;
	protected String font_name = null;
	
	protected void fill_widtharray(int [] arr)
	{
		int ly = CHAR_ARRAY_LENGTH*CHAR_MAX_WIDTH*CHAR_MAX_HEIGHT;
		for(int li=0;li<CHAR_ARRAY_LENGTH;li++)
		{
			int acc = 0;
			int loff =li*CHAR_MAX_WIDTH;
			for(int lx=0;lx<CHAR_MAX_WIDTH;lx++)
			{
				int clr = arr[ly+lx+loff];
				if ((clr&0xFF000000) ==0) break;
				acc++;
			}
			wdth_arr[li] = acc;
		}
	}
	
	protected void fill_img_arr(int arr[])
	{
		for(int lcc = 0;lcc<CHAR_ARRAY_LENGTH;lcc++)
		{	
			int lcoffst = lcc*CHAR_MAX_WIDTH; 
			for(int lx=0;lx<CHAR_MAX_WIDTH;lx++)
			{
				for(int ly=0;ly<CHAR_MAX_HEIGHT;ly++)
				{
					int clr = arr[lcoffst+lx+ly*(CHAR_MAX_HEIGHT*CHAR_ARRAY_LENGTH)]; 
					if ((clr&0xFF000000)==0x00)
						img_arr [lcoffst+lx+ly*(CHAR_MAX_HEIGHT*CHAR_ARRAY_LENGTH)] = 0x00;
					else
						img_arr [lcoffst+lx+ly*(CHAR_MAX_HEIGHT*CHAR_ARRAY_LENGTH)] = (byte)0xFF;
				}
			}
		}
	}
	
	public String getFontName(){return font_name;}

	public RFont(Applet appl,String fontname,String fontfile) throws IOException
	{
		owner = appl;
		font_name = fontname;
		wdth_arr = new int [CHAR_ARRAY_LENGTH];
		img_arr = new byte [CHAR_MAX_HEIGHT*(CHAR_MAX_WIDTH*CHAR_ARRAY_LENGTH)];
		Image fntimg = common.loadImageResource(owner,"fonts/"+fontfile);
		
		int [] pxarr = new int [(CHAR_MAX_HEIGHT+1)*(CHAR_MAX_WIDTH*CHAR_ARRAY_LENGTH)]; 
		PixelGrabber pxgb = new PixelGrabber(fntimg,0,0,CHAR_MAX_WIDTH*CHAR_ARRAY_LENGTH,CHAR_MAX_HEIGHT+1,pxarr,0,CHAR_MAX_WIDTH*CHAR_ARRAY_LENGTH);
		try
		{
			pxgb.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_widtharray(pxarr);
		fill_img_arr(pxarr);
		pxarr = null;
	}
	
	public int getStringWidth(String strr)
	{
		int ttt = 0;
		for(int li=0;li<strr.length();li++)
		{
			char chr = strr.charAt(li);
			int chrr = decodeCharCode(chr);
			ttt+=getWidthCharForCode(chrr);
		}
		return ttt;
	}

	public int drawSymbol(int [] g,int cde,int x,int y,int w)
	{
		int lwm = getWidthCharForCode(cde);
		int loffst = cde*CHAR_MAX_WIDTH;
		int _h = g.length/w;
        for(int ly=0;ly<CHAR_MAX_HEIGHT;ly++)
		{
			int lly = ly*CHAR_MAX_WIDTH*CHAR_ARRAY_LENGTH;  
			for(int lx=0;lx<lwm;lx++)
			{
				if (img_arr[loffst+lx+lly]!=0)
				{
					int lsx = x+lx;
					int lsy = ly+y;
                    if ((lsx<0)||(lsx>=w)) continue;
                    if ((lsy<0)||(lsy>=_h)) continue;
					g[lsx+lsy*w]=0x00FFFFFF;
				}
			}
		}
		return lwm;
	}
	
	public void drawString(int [] g,String str,int x,int y,int wdth)
	{
        if (wdth <=0) return;
		for(int li=0;li<str.length();li++)
		{
			char ch = str.charAt(li);
			int lc = decodeCharCode(ch);
			x+=drawSymbol(g,lc,x,y,wdth);
		}
	}
	
	public int decodeCharCode(char chr)
	{
		switch(chr)
		{
			case 'A' :	return 0;
			case 'B' :	return 1;
			case 'C' :	return 2;
			case 'D' :	return 3;
			case 'E' :	return 4;
			case 'F' :	return 5;
			case 'G' :	return 6;
			case 'H' :	return 7;
			case 'I' :	return 8;
			case 'J' :	return 9;
			case 'K' :	return 10;
			case 'L' :	return 11;
			case 'M' :	return 12;
			case 'N' :	return 13;
			case 'O' :	return 14;
			case 'P' :	return 15;
			case 'Q' :	return 16;
			case 'R' :	return 17;
			case 'S' :	return 18;
			case 'T' :	return 19;
			case 'U' :	return 20;
			case 'V' :	return 21;
			case 'W' :	return 22;
			case 'X' :	return 23;
			case 'Y' :	return 24;
			case 'Z' :	return 25;
			case ' ' :	return 26;
			case '(' :	return 27;
			case ')' :	return 28;
			case '+' :	return 29;
			case '\"' :	return 30;
			case '$' :	return 31;
			case '&' :	return 32;
			case 'А' :	return 33;
			case 'Б' :	return 34;
			case 'В' :	return 35;
			case 'Г' :	return 36;
			case 'Д' :	return 37;
			case 'Е' :	return 38;
			case 'Ё' :	return 39;
			case 'Ж' :	return 40;
			case 'З' :	return 41;
			case 'И' :	return 42;
			case 'Й' :	return 43;
			case 'К' :	return 44;
			case 'Л' :	return 45;
			case 'М' :	return 46;
			case 'Н' :	return 47;
			case 'О' :	return 48;
			case 'П' :	return 49;
			case 'Р' :	return 50;
			case 'С' :	return 51;
			case 'Т' :	return 52;
			case 'У' :	return 53;
			case 'Ф' :	return 54;
			case 'Х' :	return 55;
			case 'Ц' :	return 56;
			case 'Ч' :	return 57;
			case 'Ш' :	return 58;
			case 'Щ' :	return 59;
			case 'Ы' :	return 60;
			case 'Ъ' :	return 61;
			case 'Ь' :	return 62;
			case 'Э' :	return 63;
			case 'Ю' :	return 64;
			case 'Я' :	return 65;
			case '1' :	return 66;
			case '2' :	return 67;
			case '3' :	return 68;
			case '4' :	return 69;
			case '5' :	return 70;
			case '6' :	return 71;
			case '7' :	return 72;
			case '8' :	return 73;
			case '9' :	return 74;
			case '0' :	return 75;
			default : return 76;
		}
	}
	
	public int getWidthCharForCode(int cde)
	{
		return wdth_arr[cde];	
	}
	
}
