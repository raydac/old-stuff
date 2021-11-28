// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.musicengine;

import java.util.*; 

public class MusicPattern 
{
	protected int pattern_id;
	protected int loop_count;
	protected Vector musicvector;
	protected boolean ispointer;
	protected byte [] wavearray;
	protected MusicPattern parentpattern;
	protected int mseclength;
	
	public static final int INFINITE_LOOP = 15;
	
	public int getTimeLength()
	{
		if (parentpattern!=null)
			return parentpattern.getTimeLength();  
		else
			return mseclength;  
	}
	
	public MusicEntity getEntityForIndex(int indx)
	{
		if (parentpattern!=null) 
		{
			return parentpattern.getEntityForIndex(indx); 
		}
		else
		{
			if (indx>=musicvector.size()) return null;
			return (MusicEntity) musicvector.elementAt(indx); 
		}
	}
	
	public byte [] getWaveArray()
	{
		if (parentpattern!=null)
		{
			return parentpattern.getWaveArray(); 	
		}
		else
		{
			return wavearray;
		}
	}
	
	public void setWaveArray(byte [] array, int mseclen)
	{
		wavearray = array;	
		mseclength = mseclen; 
	}
	
	public int getLength()
	{
		if (parentpattern!=null)
			return parentpattern.getLength();
		else
			return musicvector.size();  	
	}
	
	public void setLoopCount(int lp)
	{
		loop_count = lp;
	}
	
	public void addEntity(MusicEntity ent)
	{
		musicvector.addElement(ent);	
	}
	
	public int getLoopValue()
	{
		return loop_count;	
	}
	
	public int getPatternID()
	{
		return pattern_id;	
	}
	
	public Vector getMusicVector()
	{
		if (parentpattern!= null) 
			return parentpattern.getMusicVector(); 
		else 
			return musicvector; 	
	}
	
	public boolean isPatternPointer()
	{
		return ispointer;	
	}
	
	public MusicPattern(int id,int loop)
	{
		pattern_id = id;
		loop_count = loop;
		musicvector = new Vector(); 
		ispointer = false;
		wavearray = null;
		parentpattern = null;
	}

	public MusicPattern(MusicPattern ptrn,int loop)
	{
		loop_count = loop;
		ispointer = true;
		wavearray = null;
		parentpattern = ptrn;
	}
	
}
