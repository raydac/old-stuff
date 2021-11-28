// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.musicengine;

public class MusicEntity
{

	protected int type=0;
	protected int note=0;
	protected int specifier=0;
	protected int duration=0;
	protected int value=0;
	
	public static final int ENTITY_NOTE  = 0;
	public static final int ENTITY_SCALE = 1;
	public static final int ENTITY_STYLE = 2;
	public static final int ENTITY_TEMPO = 3;
	public static final int ENTITY_VOLUME= 4;

	public static final int NOTE_PAUSE		= 0;  
	public static final int NOTE_C			= 1;  
	public static final int NOTE_Сis		= 2;
	public static final int NOTE_D			= 3;
	public static final int NOTE_Dis		= 4;
	public static final int NOTE_E			= 5;
	public static final int NOTE_F			= 6;
	public static final int NOTE_Fis		= 7;
	public static final int NOTE_G			= 8;
	public static final int NOTE_Gis		= 9;
	public static final int NOTE_A			= 10;
	public static final int NOTE_Ais		= 11;
	public static final int NOTE_H			= 12;
	
	public static final int DURATION_FULL		= 0;
	public static final int DURATION_1_2		= 1;
	public static final int DURATION_1_4		= 2;
	public static final int DURATION_1_8		= 3;
	public static final int DURATION_1_16		= 4;
	public static final int DURATION_1_32		= 5;
	
	public static final int SPECIFIER_NONE		  = 0;
	public static final int SPECIFIER_DOTTED	  = 1;
	public static final int SPECIFIER_DOUBLEDOTTED= 2;
	public static final int SPECIFIER_2_3_LENGTH  = 3;
	
	public static final int SCALE_1 = 0;
	public static final int SCALE_2 = 1;
	public static final int SCALE_3 = 2;
	public static final int SCALE_4 = 3;
	
	public static final int STYLE_NATURAL	= 0;
	public static final int STYLE_CONTINOUS = 1;
	public static final int STYLE_STACCATO	= 2;

	public static final int BEATSPERMINUTE_25 = 0;
	public static final int BEATSPERMINUTE_28 = 1;
	public static final int BEATSPERMINUTE_31 = 2;
	public static final int BEATSPERMINUTE_35 = 3;
	public static final int BEATSPERMINUTE_40 = 4;
	public static final int BEATSPERMINUTE_45 = 5;
	public static final int BEATSPERMINUTE_50 = 6;
	public static final int BEATSPERMINUTE_56 = 7;
	public static final int BEATSPERMINUTE_63 = 8;
	public static final int BEATSPERMINUTE_70 = 9;
	public static final int BEATSPERMINUTE_80 = 10;
	public static final int BEATSPERMINUTE_90 = 11;
	public static final int BEATSPERMINUTE_100 =12;
	public static final int BEATSPERMINUTE_112 =13;
	public static final int BEATSPERMINUTE_125 =14;
	public static final int BEATSPERMINUTE_140 =15;
	public static final int BEATSPERMINUTE_160 =16;
	public static final int BEATSPERMINUTE_180 =17;
	public static final int BEATSPERMINUTE_200 =18;
	public static final int BEATSPERMINUTE_225 =19;
	public static final int BEATSPERMINUTE_250 =20;
	public static final int BEATSPERMINUTE_285 =21;
	public static final int BEATSPERMINUTE_320 =22;
	public static final int BEATSPERMINUTE_355 =23;
	public static final int BEATSPERMINUTE_400 =24;
	public static final int BEATSPERMINUTE_450 =25;
	public static final int BEATSPERMINUTE_500 =26;
	public static final int BEATSPERMINUTE_565 =27;
	public static final int BEATSPERMINUTE_635 =28;
	public static final int BEATSPERMINUTE_715 =29;
	public static final int BEATSPERMINUTE_800 =30;
	public static final int BEATSPERMINUTE_900 =31;

	public static final int VOLUME_TONEOFF = 0; 
	public static final int VOLUME_LEVEL1 = 1; 
	public static final int VOLUME_LEVEL2 = 2; 
	public static final int VOLUME_LEVEL3 = 3; 
	public static final int VOLUME_LEVEL4 = 4; 
	public static final int VOLUME_LEVEL5 = 5; 
	public static final int VOLUME_LEVEL6 = 6; 
	public static final int VOLUME_LEVEL7 = 7; 
	public static final int VOLUME_LEVEL8 = 8; 
	public static final int VOLUME_LEVEL9 = 9; 
	public static final int VOLUME_LEVEL10= 10; 
	public static final int VOLUME_LEVEL11= 11; 	
	public static final int VOLUME_LEVEL12= 12; 
	public static final int VOLUME_LEVEL13= 13; 
	public static final int VOLUME_LEVEL14= 14; 
	public static final int VOLUME_LEVEL15= 15; 
	
	public int getType(){return type;}
	public void setType(int newtype){type = newtype;}
	public int getNote(){return note;}
	public void setNote(int newnote){note=newnote;}
	public int getSpecifier(){return specifier;}
	public void setSpecifier(int newspec){specifier=newspec;}
	public int getDuration(){return duration;}
	public void setDuration(int newdur){duration=newdur;}
	public int getValue(){return value;}
	public void setValue(int newvalue){value=newvalue;}
	
	public MusicEntity(int lnote,int lduration,int lspecifier)
	{
		type = ENTITY_NOTE;
		note = lnote;
		duration = lduration;
		specifier = lspecifier;
	}
	
	public MusicEntity(int ltype,int lvalue)
	{
		type = ltype;
		value = lvalue;
	}
	
}
