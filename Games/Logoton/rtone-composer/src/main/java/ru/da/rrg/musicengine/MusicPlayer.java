// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.musicengine;

import stub.com.audio.AudioData;
import stub.com.audio.AudioDataStream;
import stub.com.audio.AudioPlayer;

import java.util.*;

public class MusicPlayer extends Vector implements Runnable 
{
	Thread nthr = null;

	class MusicItem
	{
		private int len;
		private byte [] adstr;
		
		public MusicItem(int length,byte[] ads)
		{
			len = length;
			adstr = ads;
		}
		
		public byte [] getBuffer()
		{
			return adstr;	
		}
		
		public int getLength()
		{
			return 	len;
		}
	}

	protected byte [] etalon_pattern = null;
	
	public static final float NOTE_FREQUENTLY_PAUSE = 0f;     // Pause
	public static final float NOTE_FREQUENTLY_C     = 1f ;    //130.85  Do
	public static final float NOTE_FREQUENTLY_Cis   = 1.0591f;//138.59  Do#
	public static final float NOTE_FREQUENTLY_D     = 1.1221f;//146.83  Re
	public static final float NOTE_FREQUENTLY_Dis   = 1.1888f;//155.56  Re#
	public static final float NOTE_FREQUENTLY_E     = 1.2595f;//164.81  Mi
	public static final float NOTE_FREQUENTLY_F     = 1.3344f;//174.61  Fa
	public static final float NOTE_FREQUENTLY_Fis   = 1.4138f;//184.99  Fa#
	public static final float NOTE_FREQUENTLY_G     = 1.4978f;//195.99  Sol
	public static final float NOTE_FREQUENTLY_Gis   = 1.5869f;//207.65  Sol#
	public static final float NOTE_FREQUENTLY_A     = 1.6813f;//220.00  La
	public static final float NOTE_FREQUENTLY_Ais   = 1.7813f;//233.08  La#
	public static final float NOTE_FREQUENTLY_H     = 1.8872f;//246.94  Si
	
	public static final float LENGTH_FULL = 4f; 
	public static final float LENGTH_HALF = 2f; 
	public static final float LENGTH_1_4  = 1f; 
	public static final float LENGTH_1_8  = 0.5f;
	public static final float LENGTH_1_16 = 0.25f;
	public static final float LENGTH_1_32 = 0.125f;
	
	protected MusicList play_list = null;
	protected int tempocoeff = 0;
	protected int style = MusicEntity.STYLE_NATURAL;
	protected int scale = MusicEntity.SCALE_2;
	protected float transpostition = 1f;

	protected boolean isplaying=false;
	protected AudioPlayer aplayer = AudioPlayer.player;  
	protected AudioDataStream adatastream = null;  
	
	public MusicPlayer(byte [] etalon,float transpon)
	{
		super();
		transpostition = transpon; 
		etalon_pattern = etalon;
	}

	protected void setTempo(int newtemp)
	{
		switch(newtemp)
		{
			case MusicEntity.BEATSPERMINUTE_25 : tempocoeff = 2400;break;
			case MusicEntity.BEATSPERMINUTE_28 : tempocoeff = 2140;break;
			case MusicEntity.BEATSPERMINUTE_31 : tempocoeff = 1900;break;
			case MusicEntity.BEATSPERMINUTE_35 : tempocoeff = 1700;break;
			case MusicEntity.BEATSPERMINUTE_40 : tempocoeff = 1510;break;
			case MusicEntity.BEATSPERMINUTE_45 : tempocoeff = 1350;break;
			case MusicEntity.BEATSPERMINUTE_50 : tempocoeff = 1200;break;
			case MusicEntity.BEATSPERMINUTE_56 : tempocoeff = 1070;break;
			case MusicEntity.BEATSPERMINUTE_63 : tempocoeff = 950;break;
			case MusicEntity.BEATSPERMINUTE_70 : tempocoeff = 850;break;
			case MusicEntity.BEATSPERMINUTE_80 : tempocoeff = 760;break;
			case MusicEntity.BEATSPERMINUTE_90 : tempocoeff = 670;break;
			case MusicEntity.BEATSPERMINUTE_100: tempocoeff = 600;break;
			case MusicEntity.BEATSPERMINUTE_112: tempocoeff = 540;break;
			case MusicEntity.BEATSPERMINUTE_125: tempocoeff = 480;break;
			case MusicEntity.BEATSPERMINUTE_140: tempocoeff = 430;break;
			case MusicEntity.BEATSPERMINUTE_160: tempocoeff = 380;break;
			case MusicEntity.BEATSPERMINUTE_180: tempocoeff = 340;break;
			case MusicEntity.BEATSPERMINUTE_200: tempocoeff = 300;break;
			case MusicEntity.BEATSPERMINUTE_225: tempocoeff = 270;break;
			case MusicEntity.BEATSPERMINUTE_250: tempocoeff = 240;break;
			case MusicEntity.BEATSPERMINUTE_285: tempocoeff = 210;break;
			case MusicEntity.BEATSPERMINUTE_320: tempocoeff = 190;break;
			case MusicEntity.BEATSPERMINUTE_355: tempocoeff = 170;break;
			case MusicEntity.BEATSPERMINUTE_400: tempocoeff = 150;break;
			case MusicEntity.BEATSPERMINUTE_450: tempocoeff = 130;break;
			case MusicEntity.BEATSPERMINUTE_500: tempocoeff = 120;break;
			case MusicEntity.BEATSPERMINUTE_565: tempocoeff = 100;break;
			case MusicEntity.BEATSPERMINUTE_635: tempocoeff = 90;break;
			case MusicEntity.BEATSPERMINUTE_715: tempocoeff = 80;break;
			case MusicEntity.BEATSPERMINUTE_800: tempocoeff = 70;break;
			case MusicEntity.BEATSPERMINUTE_900: tempocoeff = 60;break;
		}		
	}
	
	public void setMusicList(MusicList ml)
	{
		removeAllElements(); 
		play_list = ml;
		setTempo(MusicEntity.BEATSPERMINUTE_63);
		style = MusicEntity.STYLE_NATURAL;
		scale = MusicEntity.SCALE_2;
		
		for (int li=0;li<play_list.size();li++)
		{
			MusicPattern mp = play_list.getPatternForIndex(li);
			if (mp.isPatternPointer()) 
			{
				addElement(mp);
				continue; 
			}
			Vector vct = new Vector(mp.getLength());
			
			for(int lx = 0;lx<mp.getLength();lx++)
			{
				MusicEntity me = mp.getEntityForIndex(lx);
				switch(me.getType())
				{
					case MusicEntity.ENTITY_NOTE : {
													   int lnote = me.getNote();
													   int lduration = me.getDuration();
													   int lspecifier = me.getSpecifier(); 
													   
													   float lfreq = 0;
													   switch(lnote)
													   {
															case MusicEntity.NOTE_PAUSE : lfreq = NOTE_FREQUENTLY_PAUSE ;break;
															case MusicEntity.NOTE_C     : lfreq = NOTE_FREQUENTLY_C ;break;
															case MusicEntity.NOTE_Сis   : lfreq = NOTE_FREQUENTLY_Cis ;break;
															case MusicEntity.NOTE_D     : lfreq = NOTE_FREQUENTLY_D ;break;
															case MusicEntity.NOTE_Dis   : lfreq = NOTE_FREQUENTLY_Dis ;break;
															case MusicEntity.NOTE_E     : lfreq = NOTE_FREQUENTLY_E ;break;
															case MusicEntity.NOTE_F     : lfreq = NOTE_FREQUENTLY_F ;break;
															case MusicEntity.NOTE_Fis   : lfreq = NOTE_FREQUENTLY_Fis ;break;
															case MusicEntity.NOTE_G     : lfreq = NOTE_FREQUENTLY_G ;break;
															case MusicEntity.NOTE_Gis   : lfreq = NOTE_FREQUENTLY_Gis ;break;
															case MusicEntity.NOTE_A     : lfreq = NOTE_FREQUENTLY_A ;break;
															case MusicEntity.NOTE_Ais   : lfreq = NOTE_FREQUENTLY_Ais ;break;
															case MusicEntity.NOTE_H     : lfreq = NOTE_FREQUENTLY_H ;break;
													   }
													   
													   lfreq *= transpostition;
													   switch(scale)
													   {
															case MusicEntity.SCALE_1 :  lfreq = lfreq;break;
															case MusicEntity.SCALE_2 :  lfreq = lfreq*2f;break;
															case MusicEntity.SCALE_3 :  lfreq = lfreq*4f;break;
															case MusicEntity.SCALE_4 :  lfreq = lfreq*8f;break;
													   } 	
													   
													   int llen = 0;
													   switch(lduration)
													   {
															case MusicEntity.DURATION_FULL :  llen = (int)Math.round(tempocoeff*LENGTH_FULL);break;
															case MusicEntity.DURATION_1_2  :  llen = (int)Math.round(tempocoeff*LENGTH_HALF);break;
														    case MusicEntity.DURATION_1_4  :  llen = tempocoeff;break;
															case MusicEntity.DURATION_1_8  :  llen = (int)Math.round(tempocoeff*LENGTH_1_8);break;
															case MusicEntity.DURATION_1_16 :  llen = (int)Math.round(tempocoeff*LENGTH_1_16);break;
															case MusicEntity.DURATION_1_32 :  llen = (int)Math.round(tempocoeff*LENGTH_1_32);break;
													   }
													   
													   switch(lspecifier)
													   {
															case MusicEntity.SPECIFIER_NONE :;break;
															case MusicEntity.SPECIFIER_DOTTED : llen = (int)Math.round(((float)llen*1.5f));break;
															case MusicEntity.SPECIFIER_DOUBLEDOTTED : llen = (int)Math.round(((float)llen*1.75f));break;									
															case MusicEntity.SPECIFIER_2_3_LENGTH : llen = (int)Math.round(((float)llen*0.75f));break;
													   }
													   
													   byte[] ads = null;
													   
													   if (lnote!=MusicEntity.NOTE_PAUSE)
													   {
														   System.out.println("Generate note FRQ="+lfreq);
														   ads = GenerateSoundArray(lfreq,llen,style); 
													   }
														else	
													   {
														   System.out.println("Generate pause FRQ="+lfreq);
														   ads = GenerateSoundArray(0f,llen,MusicEntity.STYLE_CONTINOUS); 
													   }
													   MusicItem mi = new MusicItem(llen,ads); 
													   vct.addElement(mi);
												   };break;
					case MusicEntity.ENTITY_SCALE :{
													   scale = me.getValue(); 
												   };break;
					case MusicEntity.ENTITY_STYLE :{
													   style = me.getValue(); 
												   };break;
					case MusicEntity.ENTITY_TEMPO :{
													   setTempo(me.getValue());
												   };break;
					case MusicEntity.ENTITY_VOLUME :{
													};break;
				}
			}
			convert2array(vct,mp);
			addElement(mp);
		}
	}

	protected void convert2array(Vector vct,MusicPattern mpt)
	{
		if (mpt.isPatternPointer()) return; 
		int llen=0;
		int tlen = 0;
		for(int li=0;li<vct.size();li++)
		{
			llen += ((MusicItem)vct.elementAt(li)).getBuffer().length;  
			tlen += ((MusicItem)vct.elementAt(li)).getLength();  
		}
		
		byte [] narray = new byte[llen];
		llen = 0;
		for(int li=0;li<vct.size();li++)
		{
			MusicItem mi = (MusicItem)vct.elementAt(li);
			byte [] bte = mi.getBuffer(); 
			System.arraycopy(bte,0,narray,llen,bte.length);
			llen += bte.length; 
		}
		
		mpt.setWaveArray(narray,tlen);  
	}
	
	public void run()
	{
		isplaying = true;
		for(int li=0;li<size();li++)
		{
			MusicPattern mpt = (MusicPattern) elementAt(li);

			AudioData adt = new AudioData(AudioData.DEFAULT_FORMAT, mpt.getWaveArray());

			int loopn = mpt.getLoopValue()+1;
			for(int lil=0;lil<loopn;lil++)
			{
				adatastream = new AudioDataStream(adt);
			 
				aplayer.start(adatastream);   
				try
				{
					Thread.sleep(mpt.getTimeLength()+2); 
				}
				catch(InterruptedException ex)
				{
					stop(); 
					return;
				}
				if (mpt.getLoopValue()==MusicPattern.INFINITE_LOOP) lil--;  
			}
		}
		isplaying = false;
		adatastream = null;
		
	}
	
	public boolean isPlaying(){ return isplaying;} 
	
	public void play()
	{
		if (isPlaying())
		{
			nthr.stop(); 
		}
		nthr = new Thread(this); 
		nthr.start();  
	}

	public void stop()
	{
		if (isPlaying())
		{
			aplayer.stop(adatastream);
			nthr.stop();
		}
		isplaying = false;
		adatastream = null;
	}
	
	public byte [] GenerateSoundArray(float freq,int len_millisec,int style)
	{
		byte [] bfr = null;	

		int len_buff = (8*len_millisec);
		int len_buff2 = len_buff; 
		
		switch(style)
		{
			case MusicEntity.STYLE_NATURAL :  len_buff-=(8*20);break;
			case MusicEntity.STYLE_CONTINOUS :   	;break;
			case MusicEntity.STYLE_STACCATO :  len_buff = (int)Math.round((float)len_buff*0.75f);break;
		}
		bfr = new byte [len_buff2]; 
		
		int lk = 0;
		int splen = etalon_pattern.length;
		if (freq!=0f)
		{
			for(int li=0;li<len_buff;li++)
			{
				int indx = (int)Math.round((float)lk*freq);
				if (indx>=splen) indx = indx%splen; 
				bfr[li] = etalon_pattern[indx];
				lk++;
			}
		}
		else
		{
			for(int li=0;li<len_buff;li++) bfr[li] = (byte)0xFF;
		}

		if ((freq!=0f)&&(style!=MusicEntity.STYLE_CONTINOUS))
		{
			for(int li=len_buff;li<bfr.length;li++) bfr[li] = (byte)0xFF;  
		}
		
		return bfr;
	}
	
}
