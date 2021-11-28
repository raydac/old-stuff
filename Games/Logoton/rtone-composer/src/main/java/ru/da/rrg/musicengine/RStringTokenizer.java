package ru.da.rrg.musicengine;

public class RStringTokenizer
{
	String tokenstr;
	int cntr;
	
	public RStringTokenizer(String str)
	{
		tokenstr = str;
		cntr = 0;
	}
	
	public boolean hasMoreTokens()
	{
		return cntr<tokenstr.length();
	}

	public String nextToken(String delim)
	{
		char [] arr = delim.toCharArray();
		StringBuffer bfr = new StringBuffer("");
		while(cntr<tokenstr.length())
		{
			char chr = tokenstr.charAt(cntr);
			boolean del = false;
			for(int li=0;li<arr.length;li++) if (chr==arr[li]) { del=true; break;}
			if (del) break;
			bfr.append(chr); 
			cntr++;
		}
		return bfr.toString(); 
	}
	
}
