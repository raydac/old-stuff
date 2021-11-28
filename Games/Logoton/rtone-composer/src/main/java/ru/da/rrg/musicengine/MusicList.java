// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.musicengine;

import java.util.*; 
import java.io.*;

public class MusicList extends Vector
{
	protected String name = "";
	protected boolean isunicode = false;
	
	int default_scale =-1;
	int default_duration =-1;
	int default_beatsperminute =-1;
	int default_style =-1;

	public String getName()
	{
		return name;	
	}
	
	public MusicList()
	{
		super();	
	}
	
	public MusicPattern getPatternForIndex(int indx)
	{
		if (indx>=size()) return null;
		return (MusicPattern)elementAt(indx);  
	}

	protected MusicEntity [] rtt_notedecode(String cntrl) throws IOException 
	{
		MusicEntity me = null;
		MusicEntity me2 = null;
		MusicEntity me3 = null;
		cntrl = cntrl.toUpperCase(); 

		int lduration = default_duration;
		int lnote = -1;
		int lscale = default_scale;
		int lspecial = MusicEntity.SPECIFIER_NONE;
		
		if (cntrl.length()>=1)
		{
			try
			{
				RStringTokenizer stk = new RStringTokenizer(cntrl);
				String bfr = null;
				// Reading duration
				bfr = stk.nextToken("PCDEFGAH").trim();
				if (bfr.length()!=0)
				{
					if (bfr.length()>2) throw new IOException("Error duration format");
					
					if (bfr.equals("1")) lduration = MusicEntity.DURATION_FULL;
					else
					if (bfr.equals("2")) lduration = MusicEntity.DURATION_1_2;
					else
					if (bfr.equals("4")) lduration = MusicEntity.DURATION_1_4;
					else
					if (bfr.equals("8")) lduration = MusicEntity.DURATION_1_8;
					else
					if (bfr.equals("16")) lduration = MusicEntity.DURATION_1_16;
					else
					if (bfr.equals("32")) lduration = MusicEntity.DURATION_1_32;
					else
						throw new IOException("Error duration value"); 
				}
				bfr = stk.nextToken("4567.").trim();
				if (bfr.length()==0) throw new IOException("Error note format");  
				if (bfr.equals("P")) lnote = MusicEntity.NOTE_PAUSE;
				else
				if (bfr.equals("C")) lnote = MusicEntity.NOTE_C;
				else
				if (bfr.equals("C#")) lnote = MusicEntity.NOTE_Сis;
				else
				if (bfr.equals("D")) lnote = MusicEntity.NOTE_D;
				else
				if (bfr.equals("D#")) lnote = MusicEntity.NOTE_Dis;
				else
				if (bfr.equals("E")) lnote = MusicEntity.NOTE_E;
				else
				if (bfr.equals("F")) lnote = MusicEntity.NOTE_F;
				else
				if (bfr.equals("F#")) lnote = MusicEntity.NOTE_Fis;
				else
				if (bfr.equals("G")) lnote = MusicEntity.NOTE_G;
				else
				if (bfr.equals("G#")) lnote = MusicEntity.NOTE_Gis;
				else
				if (bfr.equals("A")) lnote = MusicEntity.NOTE_A;
				else
				if (bfr.equals("A#")) lnote = MusicEntity.NOTE_Ais;
				else
				if (bfr.equals("H")) lnote = MusicEntity.NOTE_H;
				else
					throw new IOException("Error note"); 

				if (stk.hasMoreTokens())
				{
					bfr = stk.nextToken(".").trim();
					if (bfr.length()>0)
					{
						if (bfr.length()!=1) throw new IOException("Error note scale format");
						char chr = bfr.charAt(0);
						switch(chr)
						{
							case '4' :	lscale = MusicEntity.SCALE_1;break; 
							case '5' :	lscale = MusicEntity.SCALE_2;break;
							case '6' :	lscale = MusicEntity.SCALE_3;break;
							case '7' :	lscale = MusicEntity.SCALE_4;break;
							default : throw new IOException("Error note scale"); 
						}
						if (default_scale!=lscale)
						{
							me2 = new MusicEntity(MusicEntity.ENTITY_SCALE,lscale);
							me3 = new MusicEntity(MusicEntity.ENTITY_SCALE,default_scale);
						}
					}
				}
				
				if (stk.hasMoreTokens())
				{
					bfr = stk.nextToken("").trim();
					if (bfr.length()>0)
					{
						if (bfr.length()>1) throw new IOException("Error note special duration");
						if (bfr.equals("."))
						{
							lspecial = MusicEntity.SPECIFIER_DOTTED; 
						}
						else
							throw new IOException("Unsupported special duration");
					}
				}
			}
			catch(NoSuchElementException exx)
			{
				throw new IOException("Error format of note value"); 	
			}
		}
		else
		{
			throw new IOException("Error note"); 	
		}
		
		me = new MusicEntity(lnote,lduration,lspecial);
		
		if (me2!=null) return new MusicEntity[]{me2,me,me3}; else return new MusicEntity[]{me};
	}
	
	protected MusicEntity rtt_controlpairdecode(MusicPattern mp,String cntrl) throws IOException 
	{
		MusicEntity me = null;
		cntrl = cntrl.toUpperCase().trim(); 
		StringTokenizer stt = new StringTokenizer(cntrl,"=");
		try
		{
			cntrl = cntrl.toUpperCase(); 
			String lprt = stt.nextToken().trim();
			String rprts = null;
			int rprt = -1;
			try
			{
				rprts = stt.nextToken().trim();
				if (!lprt.equals("S"))
				{
					rprt = Integer.parseInt(rprts);
				}
			}
			catch(NumberFormatException ex)
			{
				throw new IOException("Error value format"); 
			}
			
			if (lprt.equals("L"))
			{
				rprt = rprt&0xF; 
				mp.setLoopCount(rprt); 
			}
			else
			if (lprt.equals("O"))
			{
				switch(rprt)
				{
					case 4 : rprt = MusicEntity.SCALE_1;break;  
					case 5 : rprt = MusicEntity.SCALE_2;break;  
					case 6 : rprt = MusicEntity.SCALE_3;break;  
					case 7 : rprt = MusicEntity.SCALE_4;break;  
					default : throw new IOException("Error scale parameter ["+rprt+"]"); 
				}
				default_scale = rprt;
				me = new MusicEntity(MusicEntity.ENTITY_SCALE,rprt); 
			}
			else
			if (lprt.equals("D"))
			{
				switch(rprt)
				{
					case 1 : rprt = MusicEntity.DURATION_FULL;break;  
					case 2 : rprt = MusicEntity.DURATION_1_2;break;  
					case 4 : rprt = MusicEntity.DURATION_1_4;break;  
					case 8 : rprt = MusicEntity.DURATION_1_8;break;
					case 16: rprt = MusicEntity.DURATION_1_16;break;  
					case 32: rprt = MusicEntity.DURATION_1_32;break;  
					default : throw new IOException("Error duration parameter ["+rprt+"]"); 
				}
				default_duration = rprt;
			}
			else
			if (lprt.equals("B"))
			{
				switch(rprt)
				{
					case 25 : rprt = MusicEntity.BEATSPERMINUTE_25 ; break;
					case 28 : rprt = MusicEntity.BEATSPERMINUTE_28 ; break;
					case 31 : rprt = MusicEntity.BEATSPERMINUTE_31 ; break; 
					case 35 : rprt = MusicEntity.BEATSPERMINUTE_35 ; break;
					case 40 : rprt = MusicEntity.BEATSPERMINUTE_40 ; break;
					case 45 : rprt = MusicEntity.BEATSPERMINUTE_45 ; break;
					case 50 : rprt = MusicEntity.BEATSPERMINUTE_50 ; break;
					case 56 : rprt = MusicEntity.BEATSPERMINUTE_56 ; break;
					case 63 : rprt = MusicEntity.BEATSPERMINUTE_63 ; break;
					case 70 : rprt = MusicEntity.BEATSPERMINUTE_70 ; break;
					case 80 : rprt = MusicEntity.BEATSPERMINUTE_80 ; break;
					case 90 : rprt = MusicEntity.BEATSPERMINUTE_90 ; break;
					case 100: rprt = MusicEntity.BEATSPERMINUTE_100; break;
					case 112: rprt = MusicEntity.BEATSPERMINUTE_112; break;
					case 125: rprt = MusicEntity.BEATSPERMINUTE_125; break;
					case 140: rprt = MusicEntity.BEATSPERMINUTE_140; break;
					case 160: rprt = MusicEntity.BEATSPERMINUTE_160; break;
					case 180: rprt = MusicEntity.BEATSPERMINUTE_180; break;
					case 200: rprt = MusicEntity.BEATSPERMINUTE_200; break;
					case 225: rprt = MusicEntity.BEATSPERMINUTE_225; break;
					case 250: rprt = MusicEntity.BEATSPERMINUTE_250; break;
					case 285: rprt = MusicEntity.BEATSPERMINUTE_285; break;
					case 320: rprt = MusicEntity.BEATSPERMINUTE_320; break;
					case 355: rprt = MusicEntity.BEATSPERMINUTE_355; break;
					case 400: rprt = MusicEntity.BEATSPERMINUTE_400; break;
					case 450: rprt = MusicEntity.BEATSPERMINUTE_450; break;
					case 500: rprt = MusicEntity.BEATSPERMINUTE_500; break;
					case 565: rprt = MusicEntity.BEATSPERMINUTE_565; break;
					case 635: rprt = MusicEntity.BEATSPERMINUTE_635; break;
					case 715: rprt = MusicEntity.BEATSPERMINUTE_715; break;
					case 800: rprt = MusicEntity.BEATSPERMINUTE_800; break;
					case 900: rprt = MusicEntity.BEATSPERMINUTE_900; break;
					default : throw new IOException("Unsupported temp value ["+rprt+"]"); 
				}
				me = new MusicEntity(MusicEntity.ENTITY_TEMPO,rprt);  
			}
			else
			if (lprt.equals("S"))
			{
				if (rprts.equals("S")) rprt = MusicEntity.STYLE_STACCATO;
				else
				if (rprts.equals("N")) rprt = MusicEntity.STYLE_NATURAL;
				else
				if (rprts.equals("C")) rprt = MusicEntity.STYLE_CONTINOUS;
				else
					throw new IOException("Unsupported style value"); 
				me = new MusicEntity(MusicEntity.ENTITY_STYLE,rprt);  
			}
		}
		catch(NoSuchElementException exx)
		{
			throw new IOException("Error file"); 	
		}
		return me;
	}	
		
	public void loadFromRTT(byte[] arr) throws IOException 
	{
		name = "";
		char chr = ' ';

		removeAllElements();
		MusicPattern newptrn = new MusicPattern(0,0);
		
		StringTokenizer stkn = new StringTokenizer(new String(arr));
		
		// Reading of tone name
		name = stkn.nextToken(":"); 
		
		// Reading control section
		String cs = stkn.nextToken(":");
		StringTokenizer tkz = new StringTokenizer(cs,",");
		while(tkz.hasMoreTokens())
		{
			String nt = tkz.nextToken();
			MusicEntity ne = rtt_controlpairdecode(newptrn,nt);
			if (ne!=null) newptrn.addEntity(ne);  
		}
		
		while(stkn.hasMoreTokens())
		{
			String nt = stkn.nextToken(",").trim();
			if (nt.lastIndexOf("=")>=0)
			{
				MusicEntity men = rtt_controlpairdecode(newptrn,nt);
				if (men!=null) newptrn.addEntity(men);  
			}
			else
			{
				MusicEntity [] mena = rtt_notedecode(nt);
				for(int li=0;li<mena.length;li++) newptrn.addEntity(mena[li]);  
			}
		}
		addElement(newptrn); 
	}

	public static int HexToInt(char ch)
	{
			int h = 0;
			switch(ch)
			{
				case '0' :	h = 0;break;
				case '1' :	h = 1;break;
				case '2' :	h = 2;break;
				case '3' :	h = 3;break;
				case '4' :	h = 4;break;
				case '5' :	h = 5;break;
				case '6' :	h = 6;break;
				case '7' :	h = 7;break;
				case '8' :	h = 8;break;
				case '9' :	h = 9;break;
				case 'A' :	h = 10;break;
				case 'B' :	h = 11;break;
				case 'C' :	h = 12;break;
				case 'D' :	h = 13;break;
				case 'E' :	h = 14;break;
				case 'F' :	h = 15;break;
			}
			return h;		
	}
	
	public void loadFromTextOTT(byte[] arr) throws IOException 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for(int li=0;li<arr.length;li+=4)
		{
			char ch = (char)arr[li];
			char ch2 = (char)arr[li+2];
			
			int k = HexToInt(ch);
			int k2 = HexToInt(ch2);

			k = (k<<4)|k2;
			baos.write(k); 
		}
		loadFromOTT(baos.toByteArray());  
	}
	
	public void loadFromOTT(byte[] arr) throws IOException 
	{
		isunicode = false;
		name = "";
		removeAllElements(); 
		BitInputStream bis = new BitInputStream(new ByteArrayInputStream(arr));
		while(true)
		{
			int cmnd = bis.readBits(8);
			if (cmnd==0) break;
			for(int li=0;li<cmnd;li++)
			{
				int cmpl = bis.readBits(7);
				switch(cmpl)
				{
					// CANCEL COMMAND
					case 0x05 :{
							   
							   };break;
					// RINGING TONE PROGRAMMING
					case 0x25 :{
							   };break;
					// SOUND
					case 0x1D :{
								   // Load song type
								   int stype=bis.readBits(3);
								   switch(stype)
								   {
										// BASIC SONG
										case 1 :   {
														int titlelength = bis.readBits(4);
														for(int lli=0;lli<titlelength;lli++)
														{
															if (isunicode)
															{
																char chr = (char)bis.readBits(16);
																name += chr;
															}
															else
															{
																byte chr = (byte)bis.readBits(8);
																name += (char)chr;
															}
														}
														int songsequencelength = bis.readBits(8);
														for(int lli=0;lli<songsequencelength;lli++)
														{
															// Reading of pattern header
															int lph = bis.readBits(3);
															if (lph!=0) throw new IOException("Error OTT file");
															// Reading of pattern id
															int ptrnid = bis.readBits(2);
															// Reading of pattern loop value
															int loopvalue = bis.readBits(4);
															// Reading pattern specifier
															int ptrnspcfr = bis.readBits(8);
															if (ptrnspcfr!=0)
															{
																// New pattern
																MusicPattern newmp=new MusicPattern(ptrnid,loopvalue);
																MusicEntity me = null;
																for(int lxi=0;lxi<ptrnspcfr;lxi++)
																{
																	int instrid = bis.readBits(3);
																	switch(instrid)
																	{
																		// Note
																		case 1:{	
																				   // Reading note value
																				   int notevalue = bis.readBits(4);
																				   // Reading note duration
																				   int noteduration = bis.readBits(3);
																				   // Reading note duration specifier
																				   int durationspecifier = bis.readBits(2);
																				   me = new MusicEntity(notevalue,noteduration,durationspecifier);
																			   };break;
																		// Scale
																		case 2:{
																				  // Reading note scale
																				   int notescale = bis.readBits(2); 
																				   me = new MusicEntity(MusicEntity.ENTITY_SCALE,notescale); 
																			   };break;
																		// Style
																		case 3 :{
																				  // Reading style value
																				   int stylevalue = bis.readBits(2); 
																				   me = new MusicEntity(MusicEntity.ENTITY_STYLE,stylevalue); 
																				};break;
																		// Tempo
																		case 4 :{
																				  // Reading bits per minute
																				   int bitsperminute = bis.readBits(5); 
																				   me = new MusicEntity(MusicEntity.ENTITY_TEMPO,bitsperminute); 
																				};break;
																		// Volume
																		case 5 :{
																				  // Reading volume
																				   int volume = bis.readBits(4);
																				   me = new MusicEntity(MusicEntity.ENTITY_VOLUME,volume); 
																				};break;
																		default : throw new IOException("Unknown instruction ["+instrid+"]");
																	}
																	newmp.addEntity(me); 
																}
																addElement(newmp);
															}
															else
															{
																//Existed pattern
																MusicPattern pt = null;
																for(int llk=(this.size()-1);llk>=0;llk--)
																{
																	MusicPattern lptrn = (MusicPattern) elementAt(llk);
																	if (lptrn.getPatternID()== ptrnid) 
																	{
																		pt = lptrn;
																		break;
																	}
																}
																
																if (pt==null) throw new IOException("Pointer to undefined pattern");
																MusicPattern mpt = new MusicPattern(pt,loopvalue); 
															}
														}
												   };break;
										// TEMPORARY SONG
										case 2 :   ;break;
								       default : throw new IOException("Unsupported song format"); 
								   }
							   };break;
					// UNICODE
					case 0x22 :{
								isunicode = true;
							   };break;
				}
				bis.octetAlign(); 
			}			   
		}
		
	}
	
}
