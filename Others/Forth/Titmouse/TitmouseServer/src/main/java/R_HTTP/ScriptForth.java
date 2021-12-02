package R_HTTP;

import java.io.*;
import java.util.*;  
import java.sql.*;
import java.net.*; 
import RImage.*; 
import java.lang.reflect.*;   

public class ScriptForth
{
	class ScriptFileData
	{
		String home_dir;
		DataInputStream file_stream;
		int StringCounter=0;

		public ScriptFileData(DataInputStream dis, String homedir, int StringCounter)
		{
			this.StringCounter = StringCounter; 
			file_stream = dis;
			home_dir = homedir;
		}
		
		public void Close()
		{
			home_dir = null;
			try
			{
				file_stream.close();
			}
			catch (Exception e)
			{}
			file_stream = null;	
		}
	}
	
	class ScriptForthDBConnection
	{
		protected DataBaseRecord data_base_record=null;
		public java.sql.Connection Connection=null;  
		public int StateConnection=-1;
		protected boolean succesfull=false;

		public void Close() throws SQLException 
		{
			if ((data_base_record!=null) && succesfull) data_base_record.SubConnection();
			data_base_record=null;
			if (Connection!=null) 
			{
				if (!Connection.isClosed())
				{
					if (Connection.getAutoCommit()) Connection.commit();
					Connection.close();
				}
			}
			Connection=null;
		}
		
		public ScriptForthDBConnection(String alias,Properties prop,Hashtable dbaliastbl) throws Exception 
		{
			long ConnectionTime=0;
			data_base_record = (DataBaseRecord) dbaliastbl.get(alias.trim().toUpperCase());
			if (data_base_record==null) throw new Exception("Unknown Database alias \'"+alias+"\'");
			if (data_base_record.AccessDenied())
			{
				ConnectionTime=System.currentTimeMillis();
				while(true)
				{
					if ((System.currentTimeMillis()-ConnectionTime)>data_base_record.getConnectionTimeout()) 
					{
						StateConnection = 0;
						return;
					}
					else
					{
						if (!data_base_record.AccessDenied()) break;
						else Thread.sleep(50);
					}
				}
			}
			Connection = DriverManager.getConnection(data_base_record.getDataBaseURL(),prop);
			data_base_record.AddConnection(); succesfull = true;  			
		}

		protected void finalize ()
		{
			try
			{
				if ((data_base_record!=null) && succesfull) data_base_record.SubConnection();
				data_base_record=null;
				if (Connection!=null) 
				{
					if (!Connection.isClosed())
					{
						if (Connection.getAutoCommit()) Connection.commit();
						Connection.close();
					}
				}
				Connection=null;
			}
			catch (Exception e) {}
		}
	}
	
	class ScriptForthException extends Exception
	{
		public ScriptForthException(String msg)
		{
			super (msg);	
		}
	}
	
	class ForthIntStack
	{
		int [] PStack;
		int StackSize;
		
		public ForthIntStack(int ASize)
		{
			PStack = new int [ASize];	
			StackSize = 0;
		}

		public int GetStackSize()
		{
			return StackSize;	
		}
		
		public void PushElement(int AValue)
		{
			if (StackSize==(PStack.length-1))
			{
				int [] tmpstck = new int [PStack.length*2];
				System.arraycopy(PStack,0,tmpstck,0,PStack.length);
				PStack = null;
				PStack = tmpstck;
			}
			
			PStack [StackSize] = AValue;
			StackSize = StackSize+1;
		}

		public int GetElementAt(int AValue)
		{
			return PStack[AValue];	
		}

		public void SetElementAt(int AValue,int AIndex)
		{
			PStack[AIndex]=AValue;	
		}
		
		public int LastElement()
		{
			return PStack[StackSize-1];
		}
		
		public int PopElement()
		{
			StackSize = StackSize-1;
			return PStack[StackSize];
		}

		public void removeAllElements()
		{
			StackSize = 0;	
		}
	}

	
	class ForthStack
	{
		long [] PStack;
		int StackSize;
		
		public ForthStack(int ASize)
		{
			PStack = new long [ASize];	
			StackSize = 0;
		}

		public int GetStackSize()
		{
			return StackSize;	
		}
		
		public void PushElement(long AValue)
		{
			if (StackSize==(PStack.length-1))
			{
				long [] tmpstck = new long [PStack.length*2];
				System.arraycopy(PStack,0,tmpstck,0,PStack.length);
				PStack = null;
				PStack = tmpstck;
			}
			
			PStack [StackSize] = AValue;
			StackSize++;
		}

		public long GetElementAt(int AValue)
		{
			return PStack[AValue];	
		}

		public void SetElementAt(long AValue,int AIndex)
		{
			PStack[AIndex]=AValue;	
		}
		
		public long LastElement()
		{
			return PStack[StackSize-1];
		}
		
		public long PopElement() throws EmptyStackException
		{
			try
			{
				StackSize--;
				return PStack[StackSize];
			}
			catch (Exception e)
			{
				StackSize = 0;
				throw new EmptyStackException();
			}
		}

		public void removeAllElements()
		{
			StackSize = 0;	
		}
		
	}
	
		final static int TOKEN_STRING = 0;
        final static int TOKEN_WORD = 1;
        final static int TOKEN_NUMBER = 2;
        final static int TOKEN_EOF = 3;

		private BusServerParams busp;
		private server_par servpar;
		public int StringNumber = 1; // Номер обрабатываемой строки       
		
		public int streamdest ; // Содержит номер выходного потока (0-буффер,1-сокет,2 - консоль, 3 - файл)
		public int streamsrc ; // Содержит номер входного потока (0-сокет,1 - файл, 2- блоб поле)
		
		Method [] method_table; // Таблица указателей на процедуры, реализующие команды FVM
		
		java.util.Date CurrentDate; //  Объект даты
        public DataInputStream fis ;    // Текущий поток для чтения из шаблона
        private Stack FileStreamStack ; // Буфер для хранения потоков открытых файлов
		private OutputStream DestinationStream;      // Текущий выходной поток
        private BufferedReader SourceStream;      // Текущий входной поток
		public ScriptForthBuffer BufferDestination; // Внутренний накапливающий буффер
		public ForthStack ArithmeticStack;  // Арифметический стек
        public Stack ReturnStack;      // Стек возвратов
        public Stack StringVariable;   // Память для строк
        public ForthStack IntVariable;      // Память для переменных

		String LastError = ""; // Сообщение о последней ошибка
		
		ForthStack LocalVariableMemory=null; //Указатель на память локальных переменных
		int LocalCount = 0; // Содержит количество локальных переменных для слова
		
        public Socket Sys_Socket; // Сокет для которого производится подготовка данных

        ForthIntStack Cycle_Index;        // Адрес возврата для циклов
        ForthIntStack IF_index;           // Адреса команд IF 
		ForthIntStack TRY_index,EXCEPT_index;		  // Стек адресов исключений
		Stack TRY_stack;		  // Стек обработчиков исключений
		Stack SQLConnections;  // SQL коннекты 
        Stack SQLStatements;   // SQL запросы
        Stack SQLResultSets;          // Результаты SQL запросов  
        Stack StrTokenizers;          // Токенизеры 

		Stack ImageStack;				// Изображения RImage
		Stack FontStack;				// Стек фонтов
		Stack PaletteStack;			// Стек палитр

		smtp SMTPClient; // Клиент для SMTP
		
		
		int LEAVE_address;             // Адрес команды LEAVE 
        public boolean IsQuit;                // Флаг немедленного завершения ФОРТпрограммы

		Hashtable MainVocabulary;		// Словарь фиксированных слов
        Hashtable NameVocabulary;       // Словарь заголовков       
		Hashtable GlobalVariableVocabulary;   // Словарь переменных
		Hashtable LocalVariable;		// Имена локальных переменных
		
		RImageFont default_font=null; // Фонт по умолчанию

		FileOutputStream CurrentWorkOutputFileStream=null; // Текущий рабочий выходной файловый поток
		FileInputStream CurrentWorkInputFileStream=null; // Текущий рабочий входной файловый поток

		Socket CurrentWorkSocket=null; // Текущий рабочий сокет
		int CurrentWorkSocketTimeWait=0; // Время ожидания рабочего сокета
		InputStream CurrentWorkInputBlobStream=null; // Текущий входной поток из базы данных
	
		String svar ; // Содержит строковый токен
        long    nvar ; // Содержит числовой токен

        Stack Cur_Word=null ; // Тело выполняющегося слова
        int PC_Counter=1 ;     // Номер команды в данном слове

        char [] lineBuffer = null;

		private boolean flag_plaintext =true; // Флаг показывает, что идет голый текст
		
		Stack Acm; // Накопитель компилируемого слова
        StreamTokenizer stream_tokenizer; 
        String Home_Dir; // Директорий, являющийся домашним для текущего скрипта
        Enumeration enum_clients; // Последовательность клиентов
		Thread mainthread = null; // Поток, в котором выполняется скрипт
		
        boolean State = false; // Флаг, показывает, что система в режиме компиляции

		// Индексы команд виртуальной машины
        final static int INDX_DROPALL = 0;
        final static int INDX_DROPALLR = 1;
        final static int INDX_DROP = 2;
        final static int INDX_DROPR = 3;
        final static int INDX_DUP = 4;
        final static int INDX_DUPR = 5;
        final static int INDX_SWAP = 6;
        final static int INDX_SWAPR = 7;
        final static int INDX_OVER = 8;
        final static int INDX_ROT = 9;
        final static int INDX_ROTR = 10;
        final static int INDX_TO_R = 11;
        final static int INDX_FROM_R = 12;
        final static int INDX_COPY_R = 13;
        final static int INDX_ADD = 14;
        final static int INDX_SUB = 15;
        final static int INDX_MUL = 16;
        final static int INDX_AND = 17;
        final static int INDX_OR = 18;
        final static int INDX_XOR = 19;
        final static int INDX_NOT = 20;
        final static int INDX_DIV = 21;
        final static int INDX_EQU_ZERO = 22;
        final static int INDX_MORE_ZERO = 23;
        final static int INDX_SMALL_ZERO = 24;
        final static int INDX_SMALL = 25;
        final static int INDX_MORE = 26;
        final static int INDX_EQU = 27;
        final static int INDX_MULL_TWO = 28;
        final static int INDX_DIV_TWO = 29;
        final static int INDX_TO_MEM = 30;
        final static int INDX_FROM_MEM = 31;
        final static int INDX_RAISE = 32;
//        final static int INDX_NEW_WORD_START = 33;
//        final static int INDX_NEW_WORD_END = 34;
        final static int INDX_NEXT = 35;
        final static int INDX_SETOUTSTREAM = 36;
        final static int INDX_TRY = 37;
        final static int INDX_S_ADD = 38;
        final static int INDX_PUSH = 39;
        final static int INDX_CYCLE_I = 40;
        final static int INDX_CYCLE_J = 41;
        final static int INDX_IF = 42;
        final static int INDX_THEN = 43;
        final static int INDX_ELSE = 44;
        final static int INDX_DO = 45;
        final static int INDX_LOOP = 46;
        final static int INDX_CALL = 47;
        final static int INDX_DEPTH = 48;
        final static int INDX_DEPTHR = 49;
        final static int INDX_S_EQU = 50;
        final static int INDX_S_EQUNC = 51;
        final static int INDX_ADD_LOOP = 52;
        final static int INDX_UNTIL = 53;
        final static int INDX_BEGIN = 54;
        final static int INDX_ALLOT = 55;
        final static int INDX_NO_EQU = 56;
        final static int INDX_INC = 57;
        final static int INDX_DEC = 58;
        final static int INDX_QUIT = 59;
        final static int INDX_LEAVE = 60;
        final static int INDX_S_UC = 61;
        final static int INDX_S_LC = 62;
        final static int INDX_S_AT = 63;
        final static int INDX_S_LN = 64;
        final static int INDX_S_PS = 65;
        final static int INDX_CHR = 66;
        final static int INDX_TO_STRING = 67;
        final static int INDX_SQL_GETCOLUMNSTR = 68;
        final static int INDX_SQL_GETCOLUMNINT = 69;
        final static int INDX_SQL_NEXT = 70;
        final static int INDX_SQL_EXECUTEQ = 71;
        final static int INDX_SQL_EXECUTEU = 72;
        final static int INDX_SQL_CLOSE = 73;
        final static int INDX_SQL_LOADDRIVER = 74;
        final static int INDX_SQL_CONNECT = 75;

        final static int INDX_INCMEM = 76;
        final static int INDX_STREAM_WRITENUM=77;

        final static int INDX_ADDCHAR2STR = 78;
        final static int INDX_TKN_SET = 79;
        final static int INDX_TKN_MT = 80;
        final static int INDX_TKN_NEXT = 81;
        final static int INDX_STR_S2HTML = 82;
        final static int INDX_DT_WEEKDAY = 83;
        final static int INDX_HEREI = 84;
        final static int INDX_IZAPYATAYA = 85;

        final static int INDX_STR_URLENCODE = 86;
        final static int INDX_STR_URLDECODE = 87;

        final static int INDX_SQL_ROLLBACK = 88;
        final static int INDX_SQL_COMMIT = 89;
        final static int INDX_SQL_AUTOCOMMIT = 90;
        final static int INDX_SQL_TRANSISOLATION = 91;                
        final static int INDX_SQL_GETNAMECOLUMNINT = 92;
        final static int INDX_SQL_GETNAMECOLUMNSTR = 93;

        final static int INDX_SENDHTTPHEADER = 94;

		final static int INDX_LOCAL_FROM = 95;
        final static int INDX_LOCAL_TO = 96;

        final static int INDX_STREAM_WRITEBYTE = 97;
        final static int INDX_STREAM_WRITESTRING = 98;

        final static int INDX_TRYEND = 99;

        final static int INDX_SQL_NGETBLOB = 100;
        final static int INDX_SQL_GETBLOB =101;
        final static int INDX_S_SB = 102;
        final static int INDX_FILELIST=103;

        final static int INDX_DT_DATE_BREAK=104;
        final static int INDX_DT_TIME_BREAK = 105;        
        final static int INDX_DT_DATETIME = 106;        
        final static int INDX_DT_SET_SECONDS = 107;        
        final static int INDX_DT_SET_MINUTES = 108;        
        final static int INDX_DT_SET_HOURS = 109;
        final static int INDX_DT_DATE_PACK = 110;
        
        final static int INDX_PLAYFILE = 111;

        final static int INDX_S2I = 112;
        final static int INDX_I2S = 113;
        final static int INDX_I2H = 114;

        final static int INDX_SQL_RSCLOSE = 115;
        final static int INDX_SEXECUTE = 116;

        final static int INDX_SFIND = 117;
        final static int INDX_S_TR = 118;

        final static int INDX_S2SQL = 119;
		
		final static int INDX_IMG_CREATEIMAGE = 120; 
		final static int INDX_IMG_GETPOINT  = 121;
		final static int INDX_IMG_SETPOINT  = 122;
		final static int INDX_IMG_SETPALETTE= 123;
		final static int INDX_IMG_GETINDXCOLOR= 124;
		final static int INDX_IMG_DEFAULTPALETTE = 125;
		final static int INDX_IMG_LINE		= 126;
		final static int INDX_IMG_LINEFROM  = 127;
		final static int INDX_IMG_CIRCLE	= 128;
		final static int INDX_IMG_FILL		= 129;
		final static int INDX_IMG_RECTANGLE = 130;
		final static int INDX_IMG_FILLRECTANGLE = 131;
		final static int INDX_IMG_RECTANGLEROUND= 132;
		final static int INDX_IMG_SETCOLOR  = 133;
		final static int INDX_IMG_LOADIMAGE = 134;
		final static int INDX_IMG_DRAW		= 135;
		final static int INDX_IMG_DRAWTRN	= 136;
		final static int INDX_IMG_SENDGIF = 137;	
		final static int INDX_IMG_DRAWTEXT	= 138;
		final static int INDX_IMG_LOADFONT	= 139;
		final static int INDX_IMG_LOADPALETTE= 140;
		final static int INDX_IMG_SETLINEWIDTH= 141;
		final static int INDX_IMG_GETMETRICS = 142;
		final static int INDX_IMG_SETINDXCOLOR= 143;
		final static int INDX_IMG_ELPS= 144;
		final static int INDX_IMG_SENDWBMP= 145;
		final static int INDX_CLRBUFFER= 146;
		final static int INDX_DELAY= 147;
		final static int INDX_PRIORITY= 148;
		final static int INDX_OPENFILE= 149;
		final static int INDX_CLOSEOUTSTREAM= 150;
        final static int INDX_SETINPSTREAM = 151;
        final static int INDX_READBYTEFROMINPSTREAM = 152;
        final static int INDX_READLINEFROMINPSTREAM = 153;
        final static int INDX_CLOSEINPSTREAM = 154;
		final static int INDX_SENDBUFFER2EMAIL = 155;

		final static int INDX_SERVER_GLOBAL_CONTAINS = 156;
		final static int INDX_SERVER_GLOBAL_GETSTR = 157;
		final static int INDX_SERVER_GLOBAL_GETINT = 158;
		final static int INDX_SERVER_GLOBAL_SETSTR = 159;
		final static int INDX_SERVER_GLOBAL_SETINT = 160;		

		final static int INDX_OPENSCKT = 161;
		
        // Производит считывание строки из входящего потока
        String readLine(InputStream in) throws IOException 
        {
        	char buf[] = lineBuffer;

	        if (buf == null) 
                {
	                buf = lineBuffer = new char[128];
	        }

	        int room = buf.length;
	        int offset = 0;
	        int c;
                loop:	while (true) 
                {
	                switch (c = in.read()) 
                        {
	                        case -1: 
	                        case '\n': break loop;
                	        case '\r':
		                int c2 = in.read();
		                if (c2 != '\n') 
                                {
		                        if (!(in instanceof PushbackInputStream)) 
                                        {
			                        in = in = new PushbackInputStream(in);
		                        }
		                        ((PushbackInputStream)in).unread(c2);
		                }
		                break loop;
                  	        default:
		                        if (--room < 0) 
                                        {
		                                buf = new char[offset + 128];
		                                room = buf.length - offset - 1;
		                                System.arraycopy(lineBuffer, 0, buf, 0, offset);
		                                lineBuffer = buf;
		                        }
		                        buf[offset++] = (char) c;
		                        break;
	               }
	        }
	        if ((c == -1) && (offset == 0)) 
                {
	                return null;
	        }
	return String.copyValueOf(buf, 0, offset);
    }


        // Производит вывод содержимого строки с заданным номером из массива строк
        public String GetStringVar(int indx) throws Exception
        {
                return (String)StringVariable.elementAt(indx);  
        }
        
        // Извлекает значение из ячейки ФОРТ-памяти с номером indx
        public long GetIntegerVar(int indx) throws Exception
        {
                return IntVariable.GetElementAt(indx); 
        }

        // Производит кодировку символьной строки в формат принятый в интернет
        public String EncodeString(String AValue)
        {
                return URLEncoder.encode(AValue); 
        }

        // Производит раскодировку символьной строки из формата принятого в интернет в нормальную форму
        public String DecodeString(String src)
        {
                int li;
                char cur_char;
                String tmp_buf;
                String dest = "";
                for (li=0;li<src.length();li++)
                {
                   cur_char = src.charAt(li);
                                                
                   switch(cur_char)
                   {
                        case '+' : dest = dest +" "; break;
                        case '%' : {
                                li++;
                                tmp_buf = String.valueOf(src.charAt(li));
                                li++;  
                                tmp_buf = tmp_buf+src.charAt(li);
                                try
                                {
                                        cur_char = (char)Integer.parseInt(tmp_buf,16); 
                                        dest = dest + cur_char;
                                }
                                catch (Exception e)
                                {
                                        dest = dest + "%"+tmp_buf;
                                }
                              }; break;  
                        default : dest = dest + cur_char; break;
                   }               
                } 
                return dest;
        }

        // Производит добавление заданной строковой константы в словарь 
        public int AddStringConstant(String name,String content) throws Exception 
        {
			try
			{
				name=name.toUpperCase(); 
                if (NameVocabulary.containsKey(name))
				{
						LastError = "Duplicate constant name "+name;
						throw new Exception();  
				}
				Acm = new Stack();
                int lindx = StringVariable.size(); 
                StringVariable.push(content);
                Integer li = new Integer(StringVariable.size()-1);   
                Acm.push(null);
				Acm.push(new Integer(INDX_PUSH));
                Acm.push(new Long(li.longValue()));  
                Acm.push(new Integer(INDX_NEXT));  
                NameVocabulary.put(name,Acm);
                return lindx;  
			}
			catch (Exception e)
			{
				LastError = "Error in NEXT word";
				throw new Exception(); 
			}
        }

        // Производит добавление заданной числовой переменной в словарь
        public int AddNumberValue(String name,long content) throws Exception
        {
           if (GlobalVariableVocabulary.containsKey(name))
		   {
			   LastError = "Duplicate name "+name;
			   throw new Exception(); 
		   }
           IntVariable.PushElement(content);
		   GlobalVariableVocabulary.put(name,new Long(IntVariable.GetStackSize()-1));
		   return (IntVariable.GetStackSize()-1);            
        } 

		// Добавляет в словарь адрес пользователя IP адрес (4 числа FSP-CIP0,FSP-CIP1,FSP-CIP2,FSP-CIP3) и имя машины клиента FSP-CNM
		public void AddClientIPAddr() throws Exception 
		{
			if (Sys_Socket!=null)
			{
				InetAddress ina = Sys_Socket.getInetAddress();
				byte [] baddr=ina.getAddress();
				int[]iaddr= new int[4];
				for (int li=0; li<4; li++)
				{
					if (baddr[li]<0) iaddr[li] = (Math.abs(baddr[li])^127) + 129;
					else iaddr[li] = (int)baddr[li];
				}
				AddNumberConstant("FSP-CIP0",iaddr[3]); 
				AddNumberConstant("FSP-CIP1",iaddr[2]); 
				AddNumberConstant("FSP-CIP2",iaddr[1]); 
				AddNumberConstant("FSP-CIP3",iaddr[0]); 
				AddStringConstant("FSP-CNM",ina.getHostName());
			}
		}
		
		// Производит добавление заданной числовой константы в словарь 
        public void AddNumberConstant(String name,int content) throws Exception 
        {
				name=name.toUpperCase(); 
                if (GlobalVariableVocabulary.containsKey(name)) 
				{
					LastError = "Duplicate constant name";
					throw new Exception();
				}
                GlobalVariableVocabulary.put(name,new Long(content));
        }


		private OutputStream TranslateScript(DataInputStream dis) throws Exception
		{
			    StringBuffer tmpstrbuf = new StringBuffer(3);
				int lll;
				boolean fl_end=true;
				while(fl_end)
                {
					if (!flag_plaintext)
					{
						lll = GetNextToken(fis,false);
                        switch (lll)
                        {
                                case TOKEN_EOF : fl_end=false; break;
                                case TOKEN_NUMBER  : {
                                                        if (State)
                                                        {
														   Acm.push(new Integer(INDX_PUSH));
                                                           Acm.push(new Long(nvar));         
                                                        }
                                                        else
                                                        {
                                                           ArithmeticStack.PushElement(nvar);       
                                                        }
                                                     }; break;

                                case TOKEN_STRING  : {
                                                        if (!State)
                                                        {
                                                                StringVariable.push(svar);
                                                                ArithmeticStack.PushElement(StringVariable.size()-1);         
                                                        }
                                                        else
                                                        {
                                                                StringVariable.push(svar);
                                                                Acm.push(new Integer(INDX_PUSH));
                                                                Acm.push(new Long(StringVariable.size()-1));         
                                                        }
                                                     }; break;
                                case TOKEN_WORD    : {
                                                        ExecuteTranslateCommand(svar);
                                                        if (IsQuit) 
                                                        {
                                                           fis.close(); 
                                                           return DestinationStream;
                                                        }        
                                                     }; break; 
							}
						}
						else
						{
							lll = GetNextToken(fis,true);
							if  (lll<0) { fl_end=false; break;}
							switch(lll)
							{
								case '<' :  {
												if (tmpstrbuf.length()==0)
												{
													tmpstrbuf.append((char)lll); break; 
												}
												else
												{
													for (int lli=0;lli<tmpstrbuf.length();lli++) DestinationStream.write((byte)tmpstrbuf.charAt(lli));
													DestinationStream.write((byte)lll);
													DestinationStream.flush();
												}
												tmpstrbuf.setLength(0); 
											}
							
								case '%' : 	{
											   switch (tmpstrbuf.length())
											   {
											    case 0 : DestinationStream.write('%'); break; 
											    case 1 : tmpstrbuf.append((char)lll); break;
											    case 2 : {
															if (tmpstrbuf.length()==2) 
															{
																flag_plaintext = false;
																tmpstrbuf.setLength(0); 
																continue;
															}
														 }; break;
											 }; break;
								default : if (tmpstrbuf.length()>0)  
										  {  
												tmpstrbuf.append((char)lll);
												for (int lli=0;lli<tmpstrbuf.length();lli++) DestinationStream.write((byte)tmpstrbuf.charAt(lli));
												DestinationStream.flush();
											    tmpstrbuf.setLength(0); break;							  
										  }
										  else
										  {
												  DestinationStream.write((byte)lll);
										  }
									}
							}
						}
					}
				    return DestinationStream;
		}

		// Функция производит обработку скрипта по команде LOAD
        public OutputStream PlayLOAD(String file_name) throws Exception
        {
				FileStreamStack.push(new ScriptFileData(fis,Home_Dir,this.StringNumber));  
				file_name=Home_Dir+file_name;
				try
				{
					fis = new DataInputStream ( new FileInputStream(file_name));
				}
				catch (FileNotFoundException ee)
				{
				  LastError = "File \'"+file_name+"\' not found";
				  throw new Exception(ee.getMessage());  
				}

				this.StringNumber = 1;
                int lll=file_name.length()-1;                
                boolean fl_end=false;
                Home_Dir = "";
                while(lll>=0)
                {
                        if (fl_end)
                        {
                                Home_Dir = file_name.charAt(lll)+Home_Dir;
                        }
                        else
                        {
                                if (file_name.charAt(lll)==File.separatorChar)
                                {
                                        Home_Dir = Home_Dir + File.separatorChar;
                                        fl_end = true; 
                                }  
                        }
                        lll--;
                }
                
                IsQuit = false;
                State = false;

			try
            {        
				OutputStream los = TranslateScript(fis);
				try
				{
					fis.close(); 
				}
				catch (Exception j) {}
				ScriptFileData hhh = (ScriptFileData)FileStreamStack.pop();
				fis = hhh.file_stream; 
				Home_Dir = hhh.home_dir;
				this.StringNumber = hhh.StringCounter; 
				hhh = null;				
		
				return  los;
				
			}
			 catch (Exception e)
             {
                  fis.close(); 
				  LastError = e.getMessage(); 
				  throw new Exception(e.getMessage());  
			 }            
		} 
		
		// Функция производит обработку скрипта и возвращает выходную строку
        public OutputStream PlayScript(String file_name) throws Exception
        {
				try
				{
					fis = new DataInputStream ( new FileInputStream(file_name));
				}
				catch (FileNotFoundException ee)
				{
				  LastError = "File \'"+file_name+"\' not found";
				  throw new Exception(ee.getMessage());  
				}

				this.StringNumber = 1;
                int lll=file_name.length()-1;                
                boolean fl_end=false;
                Home_Dir = "";
                while(lll>=0)
                {
                        if (fl_end)
                        {
                                Home_Dir = file_name.charAt(lll)+Home_Dir;
                        }
                        else
                        {
                                if (file_name.charAt(lll)==File.separatorChar)
                                {
                                        Home_Dir = Home_Dir + File.separatorChar;
                                        fl_end = true; 
                                }  
                        }
                        lll--;
                }
                
                IsQuit = false;
                State = false;

			try
            {        
				return TranslateScript(fis);
			}
			 catch (Exception e)
             {
                  fis.close(); 
				  LastError = e.getMessage(); 
				  throw new Exception(e.getMessage());  
			 }            
		} 

        // Конструктор
        public ScriptForth(Thread mthr,Socket cl_socket,BusServerParams bsp,server_par svp) throws Exception
        {
				default_font = bsp.dfont; 
				mainthread = mthr;
				busp = bsp;			
				servpar = svp;
				
				// Инициализируем таблицу указателей на процедуры
				method_table = new Method[256];
				MainVocabulary = new Hashtable(180); 
				
				method_table[INDX_RAISE]=this.getClass().getDeclaredMethod("RAISE",null);					
				MainVocabulary.put("RAISE",this.getClass().getDeclaredMethod("RAISE",null));  

				method_table[INDX_SENDBUFFER2EMAIL]=this.getClass().getDeclaredMethod("SENDBUFFER2EMAIL",null);					
				MainVocabulary.put("BUFF2EMAIL",this.getClass().getDeclaredMethod("SENDBUFFER2EMAIL",null));  
				
				method_table[INDX_CLOSEINPSTREAM]=this.getClass().getDeclaredMethod("CLOSEINPSTREAM",null);					
				MainVocabulary.put("CLOSEINPSTREAM",this.getClass().getDeclaredMethod("CLOSEINPSTREAM",null));  
				
				method_table[INDX_OPENSCKT]=this.getClass().getDeclaredMethod("OPENSCKT",null);
				MainVocabulary.put("OPENSCKT",this.getClass().getDeclaredMethod("OPENSCKT",null));  				
				
				method_table[INDX_OPENFILE]=this.getClass().getDeclaredMethod("OPENFILE",null);
				MainVocabulary.put("OPENFILE",this.getClass().getDeclaredMethod("OPENFILE",null));  
				
				method_table[INDX_CLOSEOUTSTREAM]=this.getClass().getDeclaredMethod("CLOSEOUTSTREAM",null);
				MainVocabulary.put("CLOSEOUTSTREAM",this.getClass().getDeclaredMethod("CLOSEOUTSTREAM",null));  				
				
				method_table[INDX_TRY]=this.getClass().getDeclaredMethod("TRY",null);
				MainVocabulary.put("TRY",this.getClass().getDeclaredMethod("TRY",null));  				
				
				method_table[INDX_TRYEND]=this.getClass().getDeclaredMethod("TRYEND",null);
				MainVocabulary.put("TRYEND",this.getClass().getDeclaredMethod("TRYEND",null));  				
				
				method_table[INDX_DELAY]=this.getClass().getDeclaredMethod("DELAY",null);
				MainVocabulary.put("DELAY",this.getClass().getDeclaredMethod("DELAY",null));  
				
				method_table[INDX_PRIORITY]=this.getClass().getDeclaredMethod("PRIORITY",null);
				MainVocabulary.put("PRIORITY",this.getClass().getDeclaredMethod("PRIORITY",null));  				
				
				method_table[INDX_LEAVE]=this.getClass().getDeclaredMethod("LEAVE",null);
				MainVocabulary.put("LEAVE",this.getClass().getDeclaredMethod("LEAVE",null));  				
				
				method_table[INDX_ADD]=this.getClass().getDeclaredMethod("ADD",null);
				MainVocabulary.put("+",this.getClass().getDeclaredMethod("ADD",null));  										  
				
				method_table[INDX_AND]=this.getClass().getDeclaredMethod("AND",null);
				MainVocabulary.put("AND",this.getClass().getDeclaredMethod("AND",null));                  
				
				method_table[INDX_COPY_R]=this.getClass().getDeclaredMethod("COPY_R",null);
				MainVocabulary.put("R@",this.getClass().getDeclaredMethod("COPY_R",null));  
				
				method_table[INDX_CYCLE_I]=this.getClass().getDeclaredMethod("CYCLE_I",null);
				MainVocabulary.put("I",this.getClass().getDeclaredMethod("CYCLE_I",null));  				
				
				method_table[INDX_CYCLE_J]=this.getClass().getDeclaredMethod("CYCLE_J",null);
				MainVocabulary.put("J",this.getClass().getDeclaredMethod("CYCLE_J",null));  				
				
				method_table[INDX_DIV]=this.getClass().getDeclaredMethod("DIV",null);
				MainVocabulary.put("/",this.getClass().getDeclaredMethod("DIV",null));  				
				
				method_table[INDX_DIV_TWO]=this.getClass().getDeclaredMethod("DIV_TWO",null);
				MainVocabulary.put("2/",this.getClass().getDeclaredMethod("DIV_TWO",null));  				
				
				method_table[INDX_DO]=this.getClass().getDeclaredMethod("DO",null);
				MainVocabulary.put("DO",this.getClass().getDeclaredMethod("DO",null));  				
				
				method_table[INDX_DROP]=this.getClass().getDeclaredMethod("DROP",null);
				MainVocabulary.put("DROP",this.getClass().getDeclaredMethod("DROP",null));  				
				
				method_table[INDX_DROPALL]=this.getClass().getDeclaredMethod("DROPALL",null);
				MainVocabulary.put("DROPALL",this.getClass().getDeclaredMethod("DROPALL",null));  				
				
				method_table[INDX_DROPR]=this.getClass().getDeclaredMethod("DROPR",null);                        
				MainVocabulary.put("DROPR",this.getClass().getDeclaredMethod("DROPR",null));  				
				
				method_table[INDX_DROPALLR]=this.getClass().getDeclaredMethod("DROPALLR",null);                        
				MainVocabulary.put("DROPALLR",this.getClass().getDeclaredMethod("DROPALLR",null));  				
				
				method_table[INDX_DUPR]=this.getClass().getDeclaredMethod("DUPR",null);                        
				MainVocabulary.put("DUPR",this.getClass().getDeclaredMethod("DUPR",null));  				
				
				method_table[INDX_DUP]=this.getClass().getDeclaredMethod("DUP",null);                        
				MainVocabulary.put("DUP",this.getClass().getDeclaredMethod("DUP",null));  				
				
				method_table[INDX_ELSE]=this.getClass().getDeclaredMethod("ELSE",null);                        
				MainVocabulary.put("ELSE",this.getClass().getDeclaredMethod("ELSE",null));  				
				
				method_table[INDX_EQU]=this.getClass().getDeclaredMethod("EQU",null);                        
				MainVocabulary.put("=",this.getClass().getDeclaredMethod("EQU",null));  				
				
				method_table[INDX_EQU_ZERO]=this.getClass().getDeclaredMethod("EQU_ZERO",null);                        
				MainVocabulary.put("0=",this.getClass().getDeclaredMethod("EQU_ZERO",null));  				
				
				method_table[INDX_FROM_MEM]=this.getClass().getDeclaredMethod("FROM_MEM",null);                        
				MainVocabulary.put("@",this.getClass().getDeclaredMethod("FROM_MEM",null));  				
				
				method_table[INDX_FROM_R]=this.getClass().getDeclaredMethod("FROM_R",null);                        
				MainVocabulary.put("R>",this.getClass().getDeclaredMethod("FROM_R",null));  				
				
				method_table[INDX_IF]=this.getClass().getDeclaredMethod("IF",null);                        
				MainVocabulary.put("IF",this.getClass().getDeclaredMethod("IF",null));  				
				
				method_table[INDX_LOOP]=this.getClass().getDeclaredMethod("LOOP",null);                        
				MainVocabulary.put("LOOP",this.getClass().getDeclaredMethod("LOOP",null));  				
				
				method_table[INDX_MORE]=this.getClass().getDeclaredMethod("MORE",null);                        
				MainVocabulary.put(">",this.getClass().getDeclaredMethod("MORE",null));  				
				
				method_table[INDX_MORE_ZERO]=this.getClass().getDeclaredMethod("MORE_ZERO",null);                        
				MainVocabulary.put("0>",this.getClass().getDeclaredMethod("MORE_ZERO",null));  
				
				method_table[INDX_MUL]=this.getClass().getDeclaredMethod("MUL",null);                        
				MainVocabulary.put("*",this.getClass().getDeclaredMethod("MUL",null));  				
				
				method_table[INDX_MULL_TWO]=this.getClass().getDeclaredMethod("MUL_TWO",null);                        
				MainVocabulary.put("2*",this.getClass().getDeclaredMethod("MUL_TWO",null));  				
				
				method_table[INDX_NEXT]=this.getClass().getDeclaredMethod("NEXT",null);                  
				MainVocabulary.put("NEXT",this.getClass().getDeclaredMethod("NEXT",null));  
				
				method_table[INDX_NOT]=this.getClass().getDeclaredMethod("NOT",null);                        
				MainVocabulary.put("NOT",this.getClass().getDeclaredMethod("NOT",null));  				
				
				method_table[INDX_OR]=this.getClass().getDeclaredMethod("OR",null);                        
				MainVocabulary.put("OR",this.getClass().getDeclaredMethod("OR",null));  				
				
				method_table[INDX_OVER]=this.getClass().getDeclaredMethod("OVER",null);                        
				MainVocabulary.put("OVER",this.getClass().getDeclaredMethod("OVER",null));  				
				
				method_table[INDX_PUSH]=this.getClass().getDeclaredMethod("PUSH",null);                        
				
				method_table[INDX_ROT]=this.getClass().getDeclaredMethod("ROT",null);                        
				MainVocabulary.put("ROT",this.getClass().getDeclaredMethod("ROT",null));  				
				
				method_table[INDX_ROTR]=this.getClass().getDeclaredMethod("ROTR",null);                        
				MainVocabulary.put("ROTR",this.getClass().getDeclaredMethod("ROTR",null));  				
				
				method_table[INDX_S_ADD]=this.getClass().getDeclaredMethod("S_ADD",null);                        
				MainVocabulary.put("S+",this.getClass().getDeclaredMethod("S_ADD",null));  				
				
				method_table[INDX_SMALL]=this.getClass().getDeclaredMethod("SMALL",null);                        
				MainVocabulary.put("<",this.getClass().getDeclaredMethod("SMALL",null));  
				
				method_table[INDX_SMALL_ZERO]=this.getClass().getDeclaredMethod("SMALL_ZERO",null);                        
				MainVocabulary.put("0<",this.getClass().getDeclaredMethod("SMALL_ZERO",null));  
				
				method_table[INDX_SUB]=this.getClass().getDeclaredMethod("SUB",null);                        
				MainVocabulary.put("-",this.getClass().getDeclaredMethod("SUB",null));  
				
				method_table[INDX_SWAP]=this.getClass().getDeclaredMethod("SWAP",null);                        
				MainVocabulary.put("SWAP",this.getClass().getDeclaredMethod("SWAP",null));  
				
				method_table[INDX_SWAPR]=this.getClass().getDeclaredMethod("SWAPR",null);                        
				MainVocabulary.put("SWAPR",this.getClass().getDeclaredMethod("SWAPR",null));  				
				
				method_table[INDX_THEN]=this.getClass().getDeclaredMethod("THEN",null);                        
				MainVocabulary.put("THEN",this.getClass().getDeclaredMethod("THEN",null));  				
				
				method_table[INDX_TO_MEM]=this.getClass().getDeclaredMethod("TO_MEM",null);                        
				MainVocabulary.put("!",this.getClass().getDeclaredMethod("TO_MEM",null));  				
				
				method_table[INDX_TO_R]=this.getClass().getDeclaredMethod("TO_R",null);                        
				MainVocabulary.put(">R",this.getClass().getDeclaredMethod("TO_R",null));  				
				
				method_table[INDX_XOR]=this.getClass().getDeclaredMethod("XOR",null);                        
				MainVocabulary.put("XOR",this.getClass().getDeclaredMethod("XOR",null));  
				
				method_table[INDX_CALL]=this.getClass().getDeclaredMethod("CALL",null);                        
				
				method_table[INDX_DEPTH]=this.getClass().getDeclaredMethod("DEPTH",null);                        
				MainVocabulary.put("DEPTH",this.getClass().getDeclaredMethod("DEPTH",null));  				
				
				method_table[INDX_DEPTHR]=this.getClass().getDeclaredMethod("DEPTHR",null);                        
				MainVocabulary.put("DEPTHR",this.getClass().getDeclaredMethod("DEPTHR",null));  				
				
				method_table[INDX_S_EQU]=this.getClass().getDeclaredMethod("S_EQU",null);                        
				MainVocabulary.put("S=",this.getClass().getDeclaredMethod("S_EQU",null));  				
				
				method_table[INDX_S_EQUNC]=this.getClass().getDeclaredMethod("S_EQUNC",null);                        
				MainVocabulary.put("S==",this.getClass().getDeclaredMethod("S_EQUNC",null));  
				
				method_table[INDX_ADD_LOOP]=this.getClass().getDeclaredMethod("ADD_LOOP",null);                        
				MainVocabulary.put("+LOOP",this.getClass().getDeclaredMethod("ADD_LOOP",null));  
				
				method_table[INDX_BEGIN]=this.getClass().getDeclaredMethod("BEGIN",null);                        
				MainVocabulary.put("BEGIN",this.getClass().getDeclaredMethod("BEGIN",null));  
				
				method_table[INDX_UNTIL]=this.getClass().getDeclaredMethod("UNTIL",null);                        
				MainVocabulary.put("UNTIL",this.getClass().getDeclaredMethod("UNTIL",null));  
				
				method_table[INDX_ALLOT]=this.getClass().getDeclaredMethod("ALLOT",null);                        
				MainVocabulary.put("ALLOT",this.getClass().getDeclaredMethod("ALLOT",null));  
				
				method_table[INDX_INC]=this.getClass().getDeclaredMethod("INC",null);                        
				MainVocabulary.put("1+",this.getClass().getDeclaredMethod("INC",null));  
				
				method_table[INDX_DEC]=this.getClass().getDeclaredMethod("DEC",null);                        
				MainVocabulary.put("1-",this.getClass().getDeclaredMethod("DEC",null));  				
				
				method_table[INDX_NO_EQU]=this.getClass().getDeclaredMethod("NO_EQU",null);                        
				MainVocabulary.put("<>",this.getClass().getDeclaredMethod("NO_EQU",null));  				
				
				method_table[INDX_QUIT]=this.getClass().getDeclaredMethod("QUIT",null);                        
				MainVocabulary.put("QUIT",this.getClass().getDeclaredMethod("QUIT",null));  
				
				method_table[INDX_S_UC]=this.getClass().getDeclaredMethod("UPPERCASE",null);                        
				MainVocabulary.put("S_UC",this.getClass().getDeclaredMethod("UPPERCASE",null));  				
				
				method_table[INDX_S_TR]=this.getClass().getDeclaredMethod("SYMBOLTR",null);                        
				MainVocabulary.put("S_TR",this.getClass().getDeclaredMethod("SYMBOLTR",null));  
				
				method_table[INDX_S_LC]=this.getClass().getDeclaredMethod("LOWERCASE",null);                        
				MainVocabulary.put("S_LC",this.getClass().getDeclaredMethod("LOWERCASE",null));  
				
				method_table[INDX_S_AT]=this.getClass().getDeclaredMethod("SYMBOLAT",null);                        
				MainVocabulary.put("S_AT",this.getClass().getDeclaredMethod("SYMBOLAT",null));  
				
				method_table[INDX_S_SB]=this.getClass().getDeclaredMethod("SUBSTRING",null);                        
				MainVocabulary.put("S_SB",this.getClass().getDeclaredMethod("SUBSTRING",null));  
				
				method_table[INDX_S_LN]=this.getClass().getDeclaredMethod("SYMBOLLENGTH",null);                        
				MainVocabulary.put("S_LN",this.getClass().getDeclaredMethod("SYMBOLLENGTH",null));  
				
				method_table[INDX_S_PS]=this.getClass().getDeclaredMethod("SYMBOL_POS",null);                        
				MainVocabulary.put("S_PS",this.getClass().getDeclaredMethod("SYMBOL_POS",null));  
				
				method_table[INDX_CHR]=this.getClass().getDeclaredMethod("CHR",null);                        
				MainVocabulary.put("CHR",this.getClass().getDeclaredMethod("CHR",null));  
				
				method_table[INDX_STR_URLDECODE]=this.getClass().getDeclaredMethod("URL_DEC",null);                        
				MainVocabulary.put("S_DEC",this.getClass().getDeclaredMethod("URL_DEC",null));  
				
				method_table[INDX_STR_URLENCODE]=this.getClass().getDeclaredMethod("URL_ENC",null);                        
				MainVocabulary.put("S_ENC",this.getClass().getDeclaredMethod("URL_ENC",null));  				
				
				method_table[INDX_TO_STRING]=this.getClass().getDeclaredMethod("TO_STRING",null);                        
				MainVocabulary.put("S!",this.getClass().getDeclaredMethod("TO_STRING",null));  				
				
				method_table[INDX_SENDHTTPHEADER]=this.getClass().getDeclaredMethod("SENDHTTPHEADER",null);                        
				MainVocabulary.put("SENDHTTPHDR",this.getClass().getDeclaredMethod("SENDHTTPHEADER",null));  				
				
				method_table[INDX_SQL_CLOSE]=this.getClass().getDeclaredMethod("SQL_CLOSE",null);                        
				MainVocabulary.put("DB_CLOSE",this.getClass().getDeclaredMethod("SQL_CLOSE",null));  				
				
				method_table[INDX_SQL_CONNECT]=this.getClass().getDeclaredMethod("SQL_CONNECT",null);                        
				MainVocabulary.put("DB_CONNECT",this.getClass().getDeclaredMethod("SQL_CONNECT",null));  				
				
				method_table[INDX_INCMEM]=this.getClass().getDeclaredMethod("INCMEM",null);                        
				MainVocabulary.put("1+!",this.getClass().getDeclaredMethod("INCMEM",null));  				
				
				method_table[INDX_SQL_EXECUTEQ]=this.getClass().getDeclaredMethod("SQL_EXECUTEQ",null);                        
				MainVocabulary.put("DB_EXQ",this.getClass().getDeclaredMethod("SQL_EXECUTEQ",null));  				
				
				method_table[INDX_SQL_EXECUTEU]=this.getClass().getDeclaredMethod("SQL_EXECUTEU",null);                        
				MainVocabulary.put("DB_EXU",this.getClass().getDeclaredMethod("SQL_EXECUTEU",null));  				
				
				method_table[INDX_SQL_GETCOLUMNINT]=this.getClass().getDeclaredMethod("SQL_GETCOLUMNINT",null);                        
				MainVocabulary.put("DB_GETINT",this.getClass().getDeclaredMethod("SQL_GETCOLUMNINT",null));  
				
				method_table[INDX_SQL_GETCOLUMNSTR]=this.getClass().getDeclaredMethod("SQL_GETCOLUMNSTR",null);                        
				MainVocabulary.put("DB_GETSTR",this.getClass().getDeclaredMethod("SQL_GETCOLUMNSTR",null));  
				
				method_table[INDX_SQL_LOADDRIVER]=this.getClass().getDeclaredMethod("SQL_LOADDRIVER",null);                        
				MainVocabulary.put("DB_LOAD",this.getClass().getDeclaredMethod("SQL_LOADDRIVER",null));  				
				
				method_table[INDX_SQL_NEXT]=this.getClass().getDeclaredMethod("SQL_NEXT",null);                        
				MainVocabulary.put("DB_NEXT",this.getClass().getDeclaredMethod("SQL_NEXT",null));  				
				
				method_table[INDX_SQL_AUTOCOMMIT]=this.getClass().getDeclaredMethod("SQL_AUTOCOMMIT",null);                        
				MainVocabulary.put("DB_AUTOCOMMIT",this.getClass().getDeclaredMethod("SQL_AUTOCOMMIT",null));  				
				
				method_table[INDX_SQL_GETNAMECOLUMNINT]=this.getClass().getDeclaredMethod("SQL_GETCOLUMNINTFORNAME",null);                        
				MainVocabulary.put("DB_NGETINT",this.getClass().getDeclaredMethod("SQL_GETCOLUMNINTFORNAME",null));  				
				
				method_table[INDX_SQL_GETNAMECOLUMNSTR]=this.getClass().getDeclaredMethod("SQL_GETCOLUMNSTRFORNAME",null);                        
				MainVocabulary.put("DB_NGETSTR",this.getClass().getDeclaredMethod("SQL_GETCOLUMNSTRFORNAME",null));  				
				
				method_table[INDX_SQL_COMMIT]=this.getClass().getDeclaredMethod("SQL_COMMIT",null);                        
				MainVocabulary.put("DB_COMMIT",this.getClass().getDeclaredMethod("SQL_COMMIT",null));  				
				
				method_table[INDX_SQL_ROLLBACK]=this.getClass().getDeclaredMethod("SQL_ROLLBACK",null);                        
				MainVocabulary.put("DB_ROLLBACK",this.getClass().getDeclaredMethod("SQL_ROLLBACK",null));  				
				
				method_table[INDX_SQL_TRANSISOLATION]=this.getClass().getDeclaredMethod("SQL_SETTRANSISOLATION",null);                        
				MainVocabulary.put("DB_TRANS",this.getClass().getDeclaredMethod("SQL_SETTRANSISOLATION",null));  				
								
				method_table[INDX_HEREI]=this.getClass().getDeclaredMethod("HEREI",null);                        
				MainVocabulary.put("HEREI",this.getClass().getDeclaredMethod("HEREI",null));  

				method_table[INDX_STR_S2HTML]=this.getClass().getDeclaredMethod("S2HTML",null);                        
				MainVocabulary.put("S2HTML",this.getClass().getDeclaredMethod("S2HTML",null));  
				
				method_table[INDX_S2SQL]=this.getClass().getDeclaredMethod("S2SQL",null);                        
				MainVocabulary.put("S2SQL",this.getClass().getDeclaredMethod("S2SQL",null));  

				method_table[INDX_IZAPYATAYA]=this.getClass().getDeclaredMethod("IZAPYATAYA",null);                        
				MainVocabulary.put("I,",this.getClass().getDeclaredMethod("IZAPYATAYA",null));  
				
				method_table[INDX_ADDCHAR2STR]=this.getClass().getDeclaredMethod("ADD_CHAR2STR",null);                        
				MainVocabulary.put("CHAR+",this.getClass().getDeclaredMethod("ADD_CHAR2STR",null));  
				
				method_table[INDX_TKN_SET]=this.getClass().getDeclaredMethod("TOKEN_SET",null);                        
				MainVocabulary.put("TKN_SET",this.getClass().getDeclaredMethod("TOKEN_SET",null));  

				method_table[INDX_TKN_NEXT]=this.getClass().getDeclaredMethod("TOKEN_NEXT",null);                        
				MainVocabulary.put("TKN_NXT",this.getClass().getDeclaredMethod("TOKEN_NEXT",null));  
				
				method_table[INDX_TKN_MT]=this.getClass().getDeclaredMethod("TOKEN_MORETOKENS",null);                        
				MainVocabulary.put("TKN_?",this.getClass().getDeclaredMethod("TOKEN_MORETOKENS",null));  
				
				method_table[INDX_DT_WEEKDAY]=this.getClass().getDeclaredMethod("DATE_WEEKDAY",null);                        
				MainVocabulary.put("WEEKDAY",this.getClass().getDeclaredMethod("DATE_WEEKDAY",null));  
				
				method_table[INDX_SQL_NGETBLOB]=this.getClass().getDeclaredMethod("SQL_GETBLOBSTREAMFORNAME",null);                        
				MainVocabulary.put("DB_NGETBLOB",this.getClass().getDeclaredMethod("SQL_GETBLOBSTREAMFORNAME",null));  
				
				method_table[INDX_SQL_GETBLOB]=this.getClass().getDeclaredMethod("SQL_GETBLOBSTREAMFORNUMBER",null);                        
				MainVocabulary.put("DB_GETBLOB",this.getClass().getDeclaredMethod("SQL_GETBLOBSTREAMFORNUMBER",null));  
                        
				method_table[INDX_DT_DATE_BREAK]=this.getClass().getDeclaredMethod("DATE_BREAK",null);                        
				MainVocabulary.put("DATE_BREAK",this.getClass().getDeclaredMethod("DATE_BREAK",null));  
				
				method_table[INDX_DT_DATE_PACK]=this.getClass().getDeclaredMethod("DATE_PACK",null);                        
				MainVocabulary.put("DATE_PACK",this.getClass().getDeclaredMethod("DATE_PACK",null));  
				
				method_table[INDX_DT_DATETIME]=this.getClass().getDeclaredMethod("DATETIME",null);                        
				MainVocabulary.put("DATETIME",this.getClass().getDeclaredMethod("DATETIME",null));  

				method_table[INDX_DT_TIME_BREAK]=this.getClass().getDeclaredMethod("TIME_BREAK",null);                        
				MainVocabulary.put("TIME_BREAK",this.getClass().getDeclaredMethod("TIME_BREAK",null));  
			
				method_table[INDX_DT_SET_HOURS]=this.getClass().getDeclaredMethod("TIME_SETHH",null);                        
				MainVocabulary.put("SETHH",this.getClass().getDeclaredMethod("TIME_SETHH",null));  
				
				method_table[INDX_DT_SET_MINUTES]=this.getClass().getDeclaredMethod("TIME_SETMM",null);                        
				MainVocabulary.put("SETMM",this.getClass().getDeclaredMethod("TIME_SETMM",null));  

				method_table[INDX_DT_SET_SECONDS]=this.getClass().getDeclaredMethod("TIME_SETSS",null);                        
				MainVocabulary.put("SET_SS",this.getClass().getDeclaredMethod("TIME_SETSS",null));  

				method_table[INDX_PLAYFILE]=this.getClass().getDeclaredMethod("PLAYFILE",null);                        
				MainVocabulary.put("PLAYFILE",this.getClass().getDeclaredMethod("PLAYFILE",null));  

				method_table[INDX_FILELIST]=this.getClass().getDeclaredMethod("FILELIST",null);                        
				MainVocabulary.put("FILELIST",this.getClass().getDeclaredMethod("FILELIST",null));  
				
				method_table[INDX_I2S]=this.getClass().getDeclaredMethod("I2S",null);                        
				MainVocabulary.put("I2S",this.getClass().getDeclaredMethod("I2S",null));  

				method_table[INDX_I2H]=this.getClass().getDeclaredMethod("I2H",null);                        
				MainVocabulary.put("I2H",this.getClass().getDeclaredMethod("I2H",null));  

				method_table[INDX_S2I]=this.getClass().getDeclaredMethod("S2I",null);                        
				MainVocabulary.put("S2I",this.getClass().getDeclaredMethod("S2I",null));  

				method_table[INDX_SQL_RSCLOSE]=this.getClass().getDeclaredMethod("SQL_CLOSERESULTSET",null);                        
				MainVocabulary.put("DB_RSCLOSE",this.getClass().getDeclaredMethod("SQL_CLOSERESULTSET",null));  

				method_table[INDX_SEXECUTE]=this.getClass().getDeclaredMethod("SEXECUTE",null);                        
				MainVocabulary.put("SEXECUTE",this.getClass().getDeclaredMethod("SEXECUTE",null));  

				method_table[INDX_SFIND]=this.getClass().getDeclaredMethod("SFIND",null);                        
				MainVocabulary.put("SFIND",this.getClass().getDeclaredMethod("SFIND",null));  
												 
				method_table[INDX_IMG_CREATEIMAGE]=this.getClass().getDeclaredMethod("IMG_CREATEIMAGE",null);                        
				MainVocabulary.put("IMG_CRT",this.getClass().getDeclaredMethod("IMG_CREATEIMAGE",null));  

				method_table[INDX_IMG_GETPOINT]=this.getClass().getDeclaredMethod("IMG_GETPOINT",null);                        
				MainVocabulary.put("IMG_GPNT",this.getClass().getDeclaredMethod("IMG_GETPOINT",null));  

				method_table[INDX_IMG_SETPOINT]=this.getClass().getDeclaredMethod("IMG_SETPOINT",null);                        
				MainVocabulary.put("IMG_SPNT",this.getClass().getDeclaredMethod("IMG_SETPOINT",null));  

				method_table[INDX_IMG_SETPALETTE]=this.getClass().getDeclaredMethod("IMG_SETPALETTE",null);                        
				MainVocabulary.put("IMG_SPAL",this.getClass().getDeclaredMethod("IMG_SETPALETTE",null));  
				
				method_table[INDX_IMG_GETINDXCOLOR]=this.getClass().getDeclaredMethod("IMG_GETINDXCOLOR",null);                        
				MainVocabulary.put("IMG_GETINDXC",this.getClass().getDeclaredMethod("IMG_GETINDXCOLOR",null));  

				method_table[INDX_IMG_SETINDXCOLOR]=this.getClass().getDeclaredMethod("IMG_SETINDXCOLOR",null);                        
				MainVocabulary.put("IMG_SETINDXC",this.getClass().getDeclaredMethod("IMG_SETINDXCOLOR",null));  
				
				method_table[INDX_IMG_DEFAULTPALETTE]=this.getClass().getDeclaredMethod("IMG_DEFAULTPALETTE",null);                        
				MainVocabulary.put("IMG_DPAL",this.getClass().getDeclaredMethod("IMG_DEFAULTPALETTE",null));  

				method_table[INDX_IMG_LINE]=this.getClass().getDeclaredMethod("IMG_LINE",null);                        
				MainVocabulary.put("IMG_LINE",this.getClass().getDeclaredMethod("IMG_LINE",null));  
				
				method_table[INDX_IMG_LINEFROM]=this.getClass().getDeclaredMethod("IMG_LINEFROM",null);                        
				MainVocabulary.put("IMG_LINEF",this.getClass().getDeclaredMethod("IMG_LINEFROM",null));  

				method_table[INDX_IMG_CIRCLE]=this.getClass().getDeclaredMethod("IMG_CIRCLE",null);                        
				MainVocabulary.put("IMG_CIRCLE",this.getClass().getDeclaredMethod("IMG_CIRCLE",null));  

				method_table[INDX_IMG_FILL]=this.getClass().getDeclaredMethod("IMG_FILL",null);                        
				MainVocabulary.put("IMG_FILL",this.getClass().getDeclaredMethod("IMG_FILL",null));  

				method_table[INDX_IMG_ELPS]=this.getClass().getDeclaredMethod("IMG_ELPS",null);                        
				MainVocabulary.put("IMG_ELPS",this.getClass().getDeclaredMethod("IMG_ELPS",null));  

				method_table[INDX_IMG_RECTANGLE]=this.getClass().getDeclaredMethod("IMG_RECTANGLE",null);                        
				MainVocabulary.put("IMG_RCTNG",this.getClass().getDeclaredMethod("IMG_RECTANGLE",null));  

				method_table[INDX_IMG_FILLRECTANGLE]=this.getClass().getDeclaredMethod("IMG_FILLRECTANGLE",null);                        
				MainVocabulary.put("IMG_FRCTNG",this.getClass().getDeclaredMethod("IMG_FILLRECTANGLE",null));  

				method_table[INDX_IMG_RECTANGLEROUND]=this.getClass().getDeclaredMethod("IMG_RECTANGLEROUND",null);                        
				MainVocabulary.put("IMG_RRCTNG",this.getClass().getDeclaredMethod("IMG_RECTANGLEROUND",null));  

				method_table[INDX_IMG_SETCOLOR]=this.getClass().getDeclaredMethod("IMG_SETCOLOR",null);                        
				MainVocabulary.put("IMG_SETC",this.getClass().getDeclaredMethod("IMG_SETCOLOR",null));  

				method_table[INDX_IMG_LOADIMAGE]=this.getClass().getDeclaredMethod("IMG_LOADIMAGE",null);                        
				MainVocabulary.put("IMG_LDIMG",this.getClass().getDeclaredMethod("IMG_LOADIMAGE",null));  

				method_table[INDX_IMG_DRAW]=this.getClass().getDeclaredMethod("IMG_DRAW",null);                        
				MainVocabulary.put("IMG_DRW",this.getClass().getDeclaredMethod("IMG_DRAW",null));  

				method_table[INDX_IMG_DRAWTRN]=this.getClass().getDeclaredMethod("IMG_DRAWTRN",null);                        
				MainVocabulary.put("IMG_DRWT",this.getClass().getDeclaredMethod("IMG_DRAWTRN",null));  

				method_table[INDX_IMG_SENDGIF]=this.getClass().getDeclaredMethod("IMG_SENDGIF",null);                        
				MainVocabulary.put("IMG_SENDGIF",this.getClass().getDeclaredMethod("IMG_SENDGIF",null));  
				
				method_table[INDX_IMG_SENDWBMP]=this.getClass().getDeclaredMethod("IMG_SENDWBMP",null);                        
				MainVocabulary.put("IMG_SENDWBMP",this.getClass().getDeclaredMethod("IMG_SENDWBMP",null));  

				method_table[INDX_IMG_DRAWTEXT]=this.getClass().getDeclaredMethod("IMG_DRAWTEXT",null);                        
				MainVocabulary.put("IMG_DRWTXT",this.getClass().getDeclaredMethod("IMG_DRAWTEXT",null));  

				method_table[INDX_IMG_LOADFONT]=this.getClass().getDeclaredMethod("IMG_LOADFONT",null);                        
				MainVocabulary.put("IMG_LDFNT",this.getClass().getDeclaredMethod("IMG_LOADFONT",null));  

				method_table[INDX_IMG_LOADPALETTE]=this.getClass().getDeclaredMethod("IMG_LOADPALETTE",null);                        
				MainVocabulary.put("IMG_LDPAL",this.getClass().getDeclaredMethod("IMG_LOADPALETTE",null));  

				method_table[INDX_IMG_SETLINEWIDTH]=this.getClass().getDeclaredMethod("IMG_SETLINEWIDTH",null);                        
				MainVocabulary.put("IMG_SETPS",this.getClass().getDeclaredMethod("IMG_SETLINEWIDTH",null));  

				method_table[INDX_LOCAL_FROM]=this.getClass().getDeclaredMethod("FROM_LOCAL_VARIABLE",null);                        
				MainVocabulary.put("L@",this.getClass().getDeclaredMethod("FROM_LOCAL_VARIABLE",null));  

				method_table[INDX_LOCAL_TO]=this.getClass().getDeclaredMethod("TO_LOCAL_VARIABLE",null);                        
				MainVocabulary.put("L!",this.getClass().getDeclaredMethod("TO_LOCAL_VARIABLE",null));  
							  
				method_table[INDX_CLRBUFFER]=this.getClass().getDeclaredMethod("CLRBUFFER",null);                        				
				MainVocabulary.put("CLRBUF",this.getClass().getDeclaredMethod("CLRBUFFER",null));  

				method_table[INDX_SETOUTSTREAM]=this.getClass().getDeclaredMethod("SETOUTSTREAM",null);                        														  
				MainVocabulary.put("SETOUTSTREAM",this.getClass().getDeclaredMethod("SETOUTSTREAM",null));  
				
				method_table[INDX_STREAM_WRITEBYTE]=this.getClass().getDeclaredMethod("STREAM_EMIT",null);                        														  
				MainVocabulary.put("EMIT",this.getClass().getDeclaredMethod("STREAM_EMIT",null));  

				method_table[INDX_STREAM_WRITENUM]=this.getClass().getDeclaredMethod("STREAM_WRITENUM",null);                        														  										  
				MainVocabulary.put(".",this.getClass().getDeclaredMethod("STREAM_WRITENUM",null));  
				
				method_table[INDX_STREAM_WRITESTRING]=this.getClass().getDeclaredMethod("STREAM_WRITESTRING",null);                        														  
				MainVocabulary.put("S.",this.getClass().getDeclaredMethod("STREAM_WRITESTRING",null));

				method_table[INDX_SETINPSTREAM]=this.getClass().getDeclaredMethod("SETINPSTREAM",null);                        														  
				MainVocabulary.put("SETINPSTREAM",this.getClass().getDeclaredMethod("SETINPSTREAM",null));  

				method_table[INDX_READBYTEFROMINPSTREAM]=this.getClass().getDeclaredMethod("READBYTEFROMSTREAM",null);                        														  
				MainVocabulary.put("RD_BYTE",this.getClass().getDeclaredMethod("READBYTEFROMSTREAM",null));  

				method_table[INDX_READLINEFROMINPSTREAM]=this.getClass().getDeclaredMethod("READLINEFROMSTREAM",null);                        														  
				MainVocabulary.put("RD_STR",this.getClass().getDeclaredMethod("READLINEFROMSTREAM",null));  
				
				method_table[INDX_SERVER_GLOBAL_CONTAINS]=this.getClass().getDeclaredMethod("SERVERGLOBALCONTAINS",null);
				MainVocabulary.put("GV?",this.getClass().getDeclaredMethod("SERVERGLOBALCONTAINS",null));  

				method_table[INDX_SERVER_GLOBAL_GETINT]=this.getClass().getDeclaredMethod("SERVERGLOBALGETINT",null);                        														  				
				MainVocabulary.put("GVI@",this.getClass().getDeclaredMethod("SERVERGLOBALGETINT",null));  

				method_table[INDX_SERVER_GLOBAL_GETSTR]=this.getClass().getDeclaredMethod("SERVERGLOBALGETSTR",null);                        														  								
				MainVocabulary.put("GVS@",this.getClass().getDeclaredMethod("SERVERGLOBALGETSTR",null));  

				method_table[INDX_SERVER_GLOBAL_SETINT]=this.getClass().getDeclaredMethod("SERVERGLOBALSETINT",null);                        														  								
				MainVocabulary.put("GVI!",this.getClass().getDeclaredMethod("SERVERGLOBALSETINT",null));  

				method_table[INDX_SERVER_GLOBAL_SETSTR]=this.getClass().getDeclaredMethod("SERVERGLOBALSETSTR",null);                        														  								
				MainVocabulary.put("GVS!",this.getClass().getDeclaredMethod("SERVERGLOBALSETSTR",null));  

				MainVocabulary.put("?FIND",this.getClass().getDeclaredMethod("FIND",null));  				
				MainVocabulary.put(":",this.getClass().getDeclaredMethod("NEW_WORD_START",null));  				
				MainVocabulary.put(";",this.getClass().getDeclaredMethod("NEW_WORD_END",null));  				
				MainVocabulary.put("%%>",this.getClass().getDeclaredMethod("ALLTOSTREAM",null));  
				MainVocabulary.put("VARIABLE",this.getClass().getDeclaredMethod("VARIABLE",null));  
				MainVocabulary.put("LOAD",this.getClass().getDeclaredMethod("LOAD",null));  
				MainVocabulary.put("?",this.getClass().getDeclaredMethod("FORCE",null));  
				
				//---------------------------------
			
				SMTPClient = new smtp(busp.MailServerAddress,busp.MailServerPort,busp.BackMailAddress); 
				
				Sys_Socket = cl_socket;
                IF_index       = new ForthIntStack(20);
				TRY_index	   = new ForthIntStack(5); 
				EXCEPT_index   = new ForthIntStack(5); 
				TRY_stack	   = new Stack(); 
				SQLConnections = new Stack();
                SQLResultSets  = new Stack();
                SQLStatements  = new Stack();

				BufferDestination = new ScriptForthBuffer(10000,5000); 

				FileStreamStack=new Stack(); 
				
				ArithmeticStack= new ForthStack(2048);
                ReturnStack    = new Stack();  
                NameVocabulary = new Hashtable(128); 
				GlobalVariableVocabulary = new Hashtable(128); 
				LocalVariable  = new Hashtable(32); 
				StringVariable = new Stack(); 
                IntVariable    = new ForthStack(256);
                Cycle_Index    = new ForthIntStack(32);  
                StrTokenizers  = new Stack(); 
				ImageStack	   = new Stack(); 
				FontStack	   = new Stack();
				PaletteStack   = new Stack();
								
				CurrentDate = new java.util.Date (); 

				// Переводим поток на буффер по умолчанию
				DestinationStream = BufferDestination;
				streamdest = 0;
				streamsrc = 0;
				SourceStream = null;
					
        }

        public void SaveDestination(String file_name) throws Exception
        {
                String tmp_str;
                File tmp_file = new File(file_name);
                if(tmp_file.exists())
                {
                        tmp_file.delete(); 
                } 

                RandomAccessFile raf = new RandomAccessFile(tmp_file,"rw");
                try
                {
                        raf.writeBytes(DestinationStream.toString()); 
                }
                catch (Exception e)
                {
                        raf.close(); 
						LastError = "Error close file";
						throw new IOException(); 
                }                 
                raf.close(); 
        }


// Процедуры реализующие базовые слова ФОРТа
//------------------------------------------

		
		// Общение с глобальными переменными сервера

		void SERVERGLOBALCONTAINS() throws Exception // GV? AS var_name -> 0|-1 Проверяет наличие глобальной переменной сервера 0 -нет -1 - есть
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SERVER_GLOBAL_CONTAINS));
				else
				{
					String lname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					if (servpar==null) throw new Exception("Not for script mode");
					if (servpar.ServerParams.ContainsParameter(lname)) ArithmeticStack.PushElement(-1); else ArithmeticStack.PushElement(0);
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in GV? word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}

		void SERVERGLOBALGETINT() throws Exception // GVI@ AS var_name -> value Кладет на стек содержимое глобальной переменной
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SERVER_GLOBAL_GETINT));
				else
				{
					String lname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					if (servpar==null) throw new Exception("Not for script mode");
					ArithmeticStack.PushElement(servpar.ServerParams.GetLongParameter(lname));  
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in GVI@ word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}

		void SERVERGLOBALGETSTR() throws Exception // GVS@ AS str_indx var_name -> Заносит в строку содержимое глобальной переменной
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SERVER_GLOBAL_GETINT));
				else
				{
					String lname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					int lindx = (int)ArithmeticStack.PopElement();
					if (servpar==null) throw new Exception("Not for script mode");
					String lstr = servpar.ServerParams.GetStringParameter(lname); 
					StringVariable.setElementAt(lstr,lindx); 
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in GVS@ word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}

		void SERVERGLOBALSETINT() throws Exception // GVI! AS int var_name -> Записываем число в глобальную переменную, если таковой переменной нет, то она создается
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SERVER_GLOBAL_SETINT));
				else
				{
					String lname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					long lindx = ArithmeticStack.PopElement();
					if (servpar==null) throw new Exception("Not for script mode");
					servpar.ServerParams.SetLongParameter(lname,lindx);  
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in GVI! word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}

		void SERVERGLOBALSETSTR() throws Exception // GVS! AS str_indx var_name -> Записываем строку в глобальную переменную, если таковой переменной нет, то она создается
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SERVER_GLOBAL_SETINT));
				else
				{
					String lname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					String lstr = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					if (servpar==null) throw new Exception("Not for script mode");
					servpar.ServerParams.SetStringParameter(lname,lstr);  
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in GVS! word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}
		
		// Работа с электронной почтой
		// Отправляет сообщение заданному адресату из буффера
		// AS: address_str -> 0|-1
		// 0 - возникла ошибочная ситуация
		// -1 - Все ОК
		void SENDBUFFER2EMAIL() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_SENDBUFFER2EMAIL));
				else
				{
					String laddr = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());

					SMTPClient.send(laddr,BufferDestination);
					if (SMTPClient.result()) ArithmeticStack.PushElement(-1); else ArithmeticStack.PushElement(0);
				}	
			}
			catch (Exception e)
			{
			   LastError = "Error in BUFF2EMAIL word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}
		
		// Работа с файлами
		// Создает токенизер с перечислением имен файлов и директорий 
		// кладет его индекс на стек
		// alias_str dir_name -> tkn_indx
		void FILELIST() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_FILELIST));
				else
				{
					String ldirname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					String lalias = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());

					if (busp.RHTTPServers.containsKey(lalias.trim().toUpperCase()))
					{
						String lfullpath = ((ServicesRecord)busp.RHTTPServers.get(lalias.trim().toUpperCase())).HomeDir;
						lfullpath +=File.separatorChar+ldirname;
						File tmpfile = new File(lfullpath);
						if (!tmpfile.isDirectory()) throw new Exception("Directory \'"+ldirname+"\' not found"); 
						String [] lfilelist=tmpfile.list();
						tmpfile = null;
						StringBuffer strbuf = new StringBuffer(lfilelist.length*16);  
						for(int li=0;li<lfilelist.length;li++)
						{
							tmpfile = new File(lfullpath+File.separatorChar+lfilelist[li]);
							if (tmpfile.isDirectory()) strbuf.append("/"+lfilelist[li]);
							else strbuf.append(lfilelist[li]);
							strbuf.append('?'); 
						}
						lfilelist = null;
						StringTokenizer lstrt = new StringTokenizer(strbuf.toString(),"?");
						ArithmeticStack.PushElement(StrTokenizers.size());
						StrTokenizers.push(lstrt);
					}
					else
					{
						throw new Exception(" error alias name "+lalias.toUpperCase());
					}
				}
			}
			catch(Exception e)
			{
			   LastError = "Error in FILELIST word ["+e.getMessage()+"]";
			   throw new Exception();
			}
		}
		

		// Открывает служебный сокет с заданными параметрами
		// AS: host_addr_str port_num time_wait -->
		void OPENSCKT()throws Exception 
		{	
			try
			{
				if (State) Acm.push(new Integer(INDX_OPENSCKT));
				else
				{
					if (CurrentWorkSocket!=null)
					{
						try
						{
							CurrentWorkSocket.close();  
						}
						catch (Exception ee){}
						CurrentWorkSocket = null;
					}
					long time_wait = ArithmeticStack.PopElement();
					long port_num = ArithmeticStack.PopElement();
					String host_addr = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					CurrentWorkSocket = new Socket(host_addr,(int)port_num,true);
					CurrentWorkSocketTimeWait = (int)time_wait;	
				}
			}
			catch (Exception e)
			{
				CurrentWorkSocket=null; 
				LastError = "Error in OPENSCKT word ["+e.getMessage()+"]";
				throw new Exception(); 
			}
		}
		
		// Открывает поток файла: AS: alias_str file_name mode ->
		// alias_str - имя сервера, в каталоге которого будет файл
		// file_name - имя файла
		// mode - 0 - создание, 1-дозапись, 2 - чтение, 3 - удаление
		void OPENFILE() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_OPENFILE));
				else
				{
					long lmode = ArithmeticStack.PopElement();
					String lfname = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					String lalias = (String)StringVariable.elementAt((int)ArithmeticStack.PopElement());
					if (busp.RHTTPServers.containsKey(lalias.trim().toUpperCase()))
					{
						String lfilepath = ((ServicesRecord)busp.RHTTPServers.get(lalias.trim().toUpperCase())).HomeDir;
						lfilepath +=File.separatorChar+lfname;
						switch ((int)lmode)
						{
							case 0 : {
										if (CurrentWorkOutputFileStream!=null) 
										{
											CurrentWorkOutputFileStream.close();
											CurrentWorkOutputFileStream = null;
										}
										 CurrentWorkOutputFileStream = new FileOutputStream(lfilepath,false);
									 }; break;
							case 1 : { 
										if (CurrentWorkOutputFileStream!=null) 
										{
											CurrentWorkOutputFileStream.close();
											CurrentWorkOutputFileStream = null;
										}
										 CurrentWorkOutputFileStream = new FileOutputStream(lfilepath,true);
									 }; break;
							case 2 : {
										if (CurrentWorkInputFileStream!=null) CurrentWorkInputFileStream.close();
										 CurrentWorkInputFileStream=null;
										 CurrentWorkInputFileStream = new FileInputStream(lfilepath);
									 };break;
							case 3 : {
										if (CurrentWorkInputFileStream!=null) CurrentWorkInputFileStream.close();
										 CurrentWorkInputFileStream=null;
										 if (!(new File(lfilepath).delete())) throw new Exception("delete unsuccessfull");
									 };break;
							default : throw new Exception("error mode");
						}
					}
					else
					{
						throw new Exception(" error alias name "+lalias.toUpperCase());
					}
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in OPENFILE word ["+e.getMessage()+"]";
			   throw new Exception();
           }          
		}

		// Закрывает текуцщий выходной поток
		void CLOSEOUTSTREAM() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_CLOSEOUTSTREAM));
				else
				{
					if (streamdest!=2)
					{
						DestinationStream.close(); 
						if (streamdest==3) 
						{
							if (CurrentWorkOutputFileStream!=null) CurrentWorkOutputFileStream.close();
							CurrentWorkOutputFileStream = null;
						}
					}
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in CLOSEOUTSTREAM word";
			   throw new Exception();
           }          
		}

		// Закрывает текуцщий входной поток
		void CLOSEINPSTREAM() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_CLOSEINPSTREAM));
				else
				{
					if (SourceStream==null) throw new Exception("Unknown input stream"); 
					SourceStream.close();
					SourceStream = null;
					switch (streamsrc)
					{
						case 1:	{if (CurrentWorkInputFileStream!=null) CurrentWorkInputFileStream.close();
								 CurrentWorkInputFileStream = null;}; break;
						case 2: {if (CurrentWorkInputBlobStream!=null) CurrentWorkInputBlobStream.close();
								 CurrentWorkInputBlobStream = null;}; break;
					}
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in CLOSEINPSTREAM word";
			   throw new Exception();
           }          
		}
		
		// PRIORITY Слово изменяет приоритет выполнения потока от 1 до 10
		void PRIORITY() throws Exception 
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_CLRBUFFER));
				else
				{
					if (mainthread!=null)
					{
						long np = ArithmeticStack.PopElement();
						if (np<1) np=1;
						if (np>10) np=10;
						np=(long)((double)(Thread.MAX_PRIORITY-Thread.MIN_PRIORITY/10)*np);
						np += Thread.MIN_PRIORITY;
						mainthread.setPriority((int)np); 
					}
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in PRIORITY word";
			   throw new Exception();
           }          
		}
		
		// DELAY Слово организует задержку выполнения на заданное количество миллисекунд
		void DELAY()throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_DELAY));
				else
				{
					LastError = "";
					if (ArithmeticStack.StackSize==0) 
					{
						LastError = "Error in DELAY word";
						throw new Exception();   
					}
					long lll = ArithmeticStack.PopElement();  
					Thread.sleep(lll); 
				}
			}
		   catch (Exception e)
           {
			   IsQuit = true;	
			   throw new Exception();
           }          
		}
		
		// Слово очищает внутренний текстовый буффер CLRBUF
		void CLRBUFFER() throws Exception // CLRBUF
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_CLRBUFFER));
				else
				{
					BufferDestination.clear(); 
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in CLRBUF word";
			   throw new Exception();
           }          
		}
		
		// Слова для управления выводом потока документа
		void ALLTOSTREAM() throws Exception // %%> включает перенаправление входного потока программы на вывод в поток
		{
			try
			{
				if (State) throw new Exception("Only for translate mode!");
				else
				{
					flag_plaintext = true;
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in %%> ["+e.getMessage()+"]";
			   throw new Exception();
           }          
		}
		
		void SENDHTTPHEADER() throws Exception // SENDHTTPHDR (ext_indx -->)На основе расширения с индексом на АС, формирует HTTP заголовок и посылает его в сокет
		{									
			try
			{
				RImage tmpimage;
				if (State) Acm.push(new Integer(INDX_SENDHTTPHEADER));
				else
				{
					long i = ArithmeticStack.PopElement();
					String ext = (String)StringVariable.elementAt((int)i);
					String mim = (String)busp.mtable.get(ext.trim().toUpperCase());
					if (mim==null) throw new Exception("Unknown extension \""+ext+"\"");
					ext = "HTTP/1.0 200 OK\r\nMIME-Version: 1.0\r\nServer: Titmouse1.4\r\nContent-type: "+mim+"\r\nPragma: no cache\r\n\r\n";
					DestinationStream.write(ext.getBytes());
					DestinationStream.flush(); 
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in SENDHTTPHDR word ["+e.getMessage()+"]";
			   throw new Exception();
           }          
		}
		
// Слова ФОРТ для работы с изображением
		// IMG_CRT Создает новое изображение
		// AS: w h i -> indx
		void IMG_CREATEIMAGE() throws Exception
		{									
			try
			{
				RImage tmpimage;
				if (State) Acm.push(new Integer(INDX_IMG_CREATEIMAGE));
				else
				{
					long i = ArithmeticStack.PopElement();
					long h = ArithmeticStack.PopElement();
					long w = ArithmeticStack.PopElement();
					tmpimage = new RImage();
					tmpimage.InitImage((int)w,(int)h,(int)i);
					ArithmeticStack.PushElement(ImageStack.size());
					ImageStack.push(tmpimage); 
				}
			}
		   catch (Exception e)
           {
			   LastError = "Error in IMG_CRT word";
			   throw new Exception();
           }          
		}
		
		// Считывает цвет заданной точки в изображении IMG_GPNT
		// image_indx x y -> image_indx color
		void IMG_GETPOINT() throws Exception
		{
		  try
		  {
			if (State) Acm.push(new Integer(INDX_IMG_GETPOINT));
			else
			{
				RImage tmpimage;
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long img = ArithmeticStack.LastElement();
				tmpimage = (RImage) ImageStack.elementAt((int)img); 
				ArithmeticStack.PushElement(tmpimage.GetPoint((int)x,(int)y));  
			}
		  }
		   catch (Exception e)
           {
			   LastError = "Error in IMG_GPNT word";
			   throw new Exception();
           }          
		}
		
		//Записывает цвет в заданную точку IMG_SPNT
		// AS: img_indx x y -> img_indx
		void IMG_SETPOINT() throws Exception 
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_SETPOINT));
			else
			{
				RImage tmpimage;
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long img = ArithmeticStack.LastElement();
				tmpimage = (RImage) ImageStack.elementAt((int)img); 
				tmpimage.SetPoint((int)x,(int)y); 
			}
			}
		   catch (Exception e)
           {
			   LastError = "Error in IMG_SPNT word";
			   throw new Exception();
           }          
		}

		// Устанавливает палитру для заданного изображения IMG_SPAL
		// AS: img_indx palette_indx -> img_indx
		void IMG_SETPALETTE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_SETPALETTE));
			else
			{
				long pal = ArithmeticStack.PopElement();
				long img = ArithmeticStack.LastElement();
				RImage timage = (RImage) ImageStack.elementAt((int)img);  
				RImagePalette tpal = (RImagePalette) PaletteStack.elementAt((int)pal);  
				timage.Palette=tpal; 
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SPAL word";
				throw new Exception();
			}
		}
		
		// Возвращает триаду для заданного индекса в таблице цвета IMG_GETINDXC
		// img_indx indx_color -> img_indx r g b
		void IMG_GETINDXCOLOR() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_IMG_GETINDXCOLOR));
				else
				{
					long indxcolor = ArithmeticStack.PopElement();
					long img = ArithmeticStack.LastElement();
					RImage timage = (RImage) ImageStack.elementAt((int)img);  
					ArithmeticStack.PushElement(timage.Palette.GetR((int)indxcolor));      
					ArithmeticStack.PushElement(timage.Palette.GetG((int)indxcolor));      
					ArithmeticStack.PushElement(timage.Palette.GetB((int)indxcolor));      
				}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_GETINDXC word";
				throw new Exception();
			}

		}

		// Заносит триаду по заданному индексу в таблице цвета IMG_SETINDXC
		// img_indx r g b color_indx  -> img_indx
		void IMG_SETINDXCOLOR() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_SETINDXCOLOR));
			else
			{
				long indxcolor = ArithmeticStack.PopElement();
				long bcolor = ArithmeticStack.PopElement();
				long gcolor = ArithmeticStack.PopElement();
				long rcolor = ArithmeticStack.PopElement();
				long img = ArithmeticStack.LastElement();
				RImage timage = (RImage) ImageStack.elementAt((int)img);  
				timage.Palette.SetRGB((int)indxcolor,(int)rcolor,(int)gcolor,(int)bcolor);      
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SETINDXC word";
				throw new Exception();
			}
		}
		
		// Выставляет палитру у изображения по умолчанию IMG_DPAL
		// AS: image_indx -> image_indx
		void IMG_DEFAULTPALETTE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_DEFAULTPALETTE));
			else
			{
				long pal = ArithmeticStack.LastElement();
				RImage rpal = (RImage) ImageStack.elementAt((int)pal); 
				rpal.Palette.DefaultPalette();  
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_DPAL word";
				throw new Exception();
			}
		}
		
		// Чертит линию текущим цветом IMG_LINE
		// AS: img_indx x1 y1 x2 y2 -> img_indx
		void IMG_LINE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_LINE));
			else
			{
				long y2 = ArithmeticStack.PopElement();
				long x2 = ArithmeticStack.PopElement();
				long y1 = ArithmeticStack.PopElement();
				long x1 = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.Line((int)x1,(int)y1,(int)x2,(int)y2);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_LINE word";
				throw new Exception();
			}
		}
		
		// Рисует линию из последней точки IMG_LINEF
		// AS: img_indx x y -> img_indx
		void IMG_LINEFROM() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_LINEFROM));
			else
			{
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.LineFrom((int)x,(int)y);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_LINEF word";
				throw new Exception();
			}
		}

		// Рисует окружность IMG_CRCLE
		// AS: img_indx x y r -> img_indx
		void IMG_CIRCLE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_CIRCLE));
			else
			{
				long r = ArithmeticStack.PopElement();
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.Circle((int)x,(int)y,(int)r);    
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_CRCLE word";
				throw new Exception();
			}
		}
		
		// Заполняет область IMG_FILL
		// AS: img_indx x y b -> img_indx
		void IMG_FILL() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_FILL));
			else
			{
				long r = ArithmeticStack.PopElement();
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.Fill((int)x,(int)y,(int)r);    
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_FILL word";
				throw new Exception();
			}
		}

		// Рисует прямоугольник IMG_RCTNG
		// AS: img_indx x1 y1 x2 y2 -> img_indx
		void IMG_RECTANGLE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_RECTANGLE));
			else
			{
				long y2 = ArithmeticStack.PopElement();
				long x2 = ArithmeticStack.PopElement();
				long y1 = ArithmeticStack.PopElement();
				long x1 = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.Rectangle((int)x1,(int)y1,(int)x2,(int)y2);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_RCTNG word";
				throw new Exception();
			}
		}

		// Рисует эллипс, вписанный в прямоугольник IMG_ELPS
		// AS: img_indx x1 y1 x2 y2 -> img_indx
		void IMG_ELPS() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_ELPS));
			else
			{
				long y2 = ArithmeticStack.PopElement();
				long x2 = ArithmeticStack.PopElement();
				long y1 = ArithmeticStack.PopElement();
				long x1 = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.Ellipse((int)x1,(int)y1,(int)x2,(int)y2);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_RCTNG word";
				throw new Exception();
			}
		}
		
		// Рисует прямоугольник IMG_FRCTNG
		// AS: img_indx x1 y1 x2 y2 -> img_indx
		void IMG_FILLRECTANGLE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_FILLRECTANGLE));
			else
			{
				long y2 = ArithmeticStack.PopElement();
				long x2 = ArithmeticStack.PopElement();
				long y1 = ArithmeticStack.PopElement();
				long x1 = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.FillRectangle((int)x1,(int)y1,(int)x2,(int)y2);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_FRCTNG word";
				throw new Exception();
			}
		}
		
		// Рисует прямоугольник с закругленными краями IMG_RRCTNG
		// AS: img_indx x1 y1 x2 y2 r -> img_indx
		void IMG_RECTANGLEROUND() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_RECTANGLEROUND));
			else
			{
				long r = ArithmeticStack.PopElement();
				long y2 = ArithmeticStack.PopElement();
				long x2 = ArithmeticStack.PopElement();
				long y1 = ArithmeticStack.PopElement();
				long x1 = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.RectangleRounded((int)x1,(int)y1,(int)x2,(int)y2,(int)r);     
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_RRCTNG word";
				throw new Exception();
			}
		}
		
		// Устанавливает текущий цвет IMG_SETC
		// AS: img_indx color -> img_indx
		void IMG_SETCOLOR() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_SETCOLOR));
			else
			{
				long color = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.SetColor((int)color);  
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SETC word";
				throw new Exception();
			}
		}

		// Устанавливает размер точки IMG_SETPS
		// AS: img_indx point_size -> img_indx
		void IMG_SETLINEWIDTH() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_SETLINEWIDTH));
			else
			{
				long color = ArithmeticStack.PopElement();
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				rimg.LineWidth=(int)color & 0xFF;  
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SETPS word";
				throw new Exception();
			}
		}

		// Считывает метрики изображения IMG_GETMTX
		// AS: img_indx -> img_indx w h
		void IMG_GETMETRICS() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_GETMETRICS));
			else
			{
				long imgindx = ArithmeticStack.LastElement();
				RImage rimg = (RImage)ImageStack.elementAt((int)imgindx);
				ArithmeticStack.PushElement((long)rimg.GetImageWidth()); 
				ArithmeticStack.PushElement((long)rimg.GetImageHeight()); 
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_GETMTX word";
				throw new Exception();
			}
		}
		
		//	Загружает изображение с носителя, автоматически выставляет размеры IMG_LDIMG
		//  AS: img_indx str_indx -> img_indx
		void IMG_LOADIMAGE() throws Exception 
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_LOADIMAGE));
			else
			{
				long str = ArithmeticStack.PopElement();
				long img_indx = ArithmeticStack.LastElement();
				RImage rimg = (RImage) ImageStack.elementAt((int)img_indx);  
				String strd = (String) StringVariable.elementAt((int)str);   
				rimg.LoadImage(Home_Dir+strd); 
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_LDIMG word";
				throw new Exception();
			}
		}
		
		// Выводит одно изображение на другое IMG_DRW
		// AS: img_dst img_src x y -> img_dst
		void IMG_DRAW() throws Exception 
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_DRAW));
			else
			{
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long imgsrc = ArithmeticStack.PopElement();
				RImage rimgsrc = (RImage)ImageStack.elementAt((int)imgsrc);
				long imgdst = ArithmeticStack.LastElement();
				RImage rimgdst = (RImage)ImageStack.elementAt((int)imgdst);
				rimgdst.Draw(rimgsrc,(int)x,(int)y);   
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_DRW word";
				throw new Exception();
			}
		}

		// Выводит одно изображение на другое IMG_DRWT текущий цвет прозрачный
		// AS: img_dst img_src x y -> img_dst
		void IMG_DRAWTRN() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_DRAWTRN));
			else
			{
				long y = ArithmeticStack.PopElement();
				long x = ArithmeticStack.PopElement();
				long imgsrc = ArithmeticStack.PopElement();
				RImage rimgsrc = (RImage)ImageStack.elementAt((int)imgsrc);
				long imgdst = ArithmeticStack.LastElement();
				RImage rimgdst = (RImage)ImageStack.elementAt((int)imgdst);
				rimgdst.DrawTransparent(rimgsrc,(int)x,(int)y);   
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_DRWT word";
				throw new Exception();
			}
		}
		
		// Посылает изображение клиенту через сокет IMG_SENDGIF в формате GIF
		// AS: img_indx ->
		void IMG_SENDGIF() throws Exception
		{
		    try
			{
				if (State) Acm.push(new Integer(INDX_IMG_SENDGIF));
				else
				{
					long imgsrc = ArithmeticStack.PopElement();
					RImage rimgsrc = (RImage)ImageStack.elementAt((int)imgsrc);
					DataOutputStream os = new DataOutputStream(DestinationStream);
					GIF ggg = new GIF();
					ggg.compress(rimgsrc, os,true);
				}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SENDGIF word";
				throw new Exception();
			}
		}

		// Посылает изображение клиенту через сокет IMG_SENDWBMP в формате WBMP (для сотовых телефонов)
		// AS: img_indx ->
		 void IMG_SENDWBMP() throws Exception
		{
		    try
			{
				if (State) Acm.push(new Integer(INDX_IMG_SENDWBMP));
				else
				{
					long imgsrc = ArithmeticStack.PopElement();
					RImage rimgsrc = (RImage)ImageStack.elementAt((int)imgsrc);
					DataOutputStream os = new DataOutputStream(DestinationStream);
					WbmpEncoder ggg = new WbmpEncoder(rimgsrc,os);
					ggg.encode();
				}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_SENDWBMP word";
				throw new Exception();
			}
		}
		

		// Рисует текст в заданных координатах АС: img_indx str_indx bckcolor x y -> img_indx
		// bckcolor - цвет фона : -1 - прозрачный фон
		void IMG_DRAWTEXT() throws Exception
		{
			try
			{
				if (State) Acm.push(new Integer(INDX_IMG_DRAWTEXT));
				else
				{
					int ly = (int) ArithmeticStack.PopElement();
					int lx = (int) ArithmeticStack.PopElement();
					int lbckcolor = (int) ArithmeticStack.PopElement();
					String lstr = (String) StringVariable.elementAt((int)ArithmeticStack.PopElement());
					RImage rimgsrc = (RImage)ImageStack.elementAt((int)ArithmeticStack.LastElement());
					default_font.DrawStringOnImage(rimgsrc,lx,ly,lbckcolor,lstr); 
				}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_DRWTXT word";
				throw new Exception();
			}
		}
		
		void IMG_LOADFONT() throws Exception
		{
			if (State) Acm.push(new Integer(INDX_IMG_LOADFONT));
			else
			{
				
			}
		}
		
		// Загружает палитру из файла с именем IMG_LDPAL
		// AS: string_index -> palette_index
		void IMG_LOADPALETTE() throws Exception
		{
			try
			{
			if (State) Acm.push(new Integer(INDX_IMG_LOADPALETTE));
			else
			{
				long str=ArithmeticStack.PopElement();
				String rstr = (String) StringVariable.elementAt((int)str);  
				RImagePalette rimpal = new RImagePalette(Home_Dir+rstr);
				ArithmeticStack.PushElement((long)PaletteStack.size());                                            
				PaletteStack.push(rimpal); 
			}
			}
			catch (Exception e)
			{ 
				LastError = "Error in IMG_LDPAL word";
				throw new Exception();
			}
		}
		
		
// Слова ФОРТ для работы с датами и временем
        void TIME_BREAK() throws Exception // TIME_BREAK Разложить число в формат AS: hh mm ss
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_TIME_BREAK));
                        else
                        {
                                long lstrindx = ArithmeticStack.PopElement();
                                CurrentDate = new java.util.Date(lstrindx);
                                ArithmeticStack.PushElement(CurrentDate.getHours());    
                                ArithmeticStack.PushElement(CurrentDate.getMinutes());    
                                ArithmeticStack.PushElement(CurrentDate.getSeconds());    
                        }
                }
				catch (Exception e)
				{ 
					LastError = "Error in TIME_BREAK word";
					throw new Exception();
				}
	
		}

        void DATE_BREAK() throws Exception // DATE_BREAK Разложить число в формат AS: dd  mm yyyy 
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_DATE_BREAK));
                        else
                        {
                                long lstrindx = ArithmeticStack.PopElement();
                                CurrentDate = new java.util.Date(lstrindx);
                                ArithmeticStack.PushElement(CurrentDate.getDate());    
                                ArithmeticStack.PushElement(CurrentDate.getMonth()+1);    
                                ArithmeticStack.PushElement(CurrentDate.getYear()+1900);    
                        }
                }
				catch (Exception e)
				{ 
					LastError = "Error in DATE_BREAK word";
					throw new Exception();
				}
		}

        void DATETIME() throws Exception // DATETIME Кладет на стек текущее время-дату в пакованном формате
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_DATETIME));
                        else
                        {
                                ArithmeticStack.PushElement(System.currentTimeMillis());            
                        }
                }
			catch (Exception e)
			{ 
				LastError = "Error in DATETIME word";
				throw new Exception();
			}
		}

        void TIME_SETSS() throws Exception // SETSS изменяет количество секунд : AS: int ss -> int
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_SET_SECONDS));
                        else
                        {
                                long ss = ArithmeticStack.PopElement();
                                long dat = ArithmeticStack.PopElement();
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setSeconds((int)ss);       
                                ArithmeticStack.PushElement(CurrentDate.getTime());  
                        }
                }
			catch (Exception e)
			{ 
				LastError = "Error in SETSS word";
				throw new Exception();
			}
		}

        void TIME_SETMM() throws Exception // SETMM изменяет количество минут : AS: int mm -> int
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_SET_MINUTES));
                        else
                        {
                                long ss = ArithmeticStack.PopElement();
                                long dat = ArithmeticStack.PopElement();
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setMinutes((int)ss);       
                                ArithmeticStack.PushElement(CurrentDate.getTime());  
                        }
                }
				catch (Exception e)
				{ 
					LastError = "Error in SETMM word";
					throw new Exception();
				}
		}

        void TIME_SETHH() throws Exception // SETHH изменяет количество минут : AS: int hh -> int
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_SET_HOURS));
                        else
                        {
                                long ss = ArithmeticStack.PopElement();
                                long dat = ArithmeticStack.PopElement();
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setHours((int)ss);       
                                ArithmeticStack.PushElement(CurrentDate.getTime());  
                        }
                }
				catch (Exception e)
				{ 
					LastError = "Error in SET_HH word";
					throw new Exception();
				}
        }

        void DATE_PACK() throws Exception // DATE_PACK пакует дату : AS: dd mm yyyy -> int
        {
                try
                {
                        if (State) Acm.push(new Integer(INDX_DT_DATE_PACK));
                        else
                        {
                                long yyyy = ArithmeticStack.PopElement()-1900;
                                long mm = ArithmeticStack.PopElement();
                                long dd = ArithmeticStack.PopElement();
                                
                                CurrentDate = new java.util.Date((int)yyyy,(int)mm-1,(int)dd);
                                ArithmeticStack.PushElement(CurrentDate.getTime());  
                        }
                }
				catch (Exception e)
				{ 
					LastError = "Error in DATE_PACK word";
					throw new Exception();
				}
		}


		void READBYTEFROMSTREAM() throws Exception // RD_BYTE AS: timeout -> int Считывает байт из входного потока и кладет его на стек, на стеке время ожидания байта в миллисекундах, если 0 - неограничено
		{
           try
           {     
                if (State) Acm.push(new Integer(INDX_READBYTEFROMINPSTREAM));
                else
                {
						long rbyte=0;
						long lstrindx = ArithmeticStack.PopElement();
						if (SourceStream==null) throw new Exception("Unknown input stream"); 
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(CurrentWorkSocketTimeWait);  
						rbyte = SourceStream.read();
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(0);  
						ArithmeticStack.PushElement(rbyte);  
				}
           }
			catch (Exception e)
			{ 
				LastError = "Error in RD_BYTE word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}


		void READLINEFROMSTREAM() throws Exception // RD_STR AS: str_indx -> 0|-1Считывает строку из входного потока , на стеке целевая строка, если все нормально, то 0 иначе -1
		{
           try
           {     
                if (State) Acm.push(new Integer(INDX_READLINEFROMINPSTREAM ));
                else
                {
						String lstrbuf = null;
						long lstrindx = ArithmeticStack.PopElement();
						if (SourceStream==null) throw new Exception("Unknown input stream"); 
						
						switch (streamsrc)
						{
							// 0 - сокет
							case 0 : { lstrbuf = SourceStream.readLine(); ArithmeticStack.PushElement(0);}break;
							// 1 - файл
							case 1 : { lstrbuf = SourceStream.readLine(); 
									   if (lstrbuf==null) { ArithmeticStack.PushElement(-1); lstrbuf=""; } else ArithmeticStack.PushElement(0);
									 } break;
							// 2 - блоб
							case 2 : { lstrbuf = SourceStream.readLine(); 
									   if (lstrbuf==null) { ArithmeticStack.PushElement(-1); lstrbuf=""; } else ArithmeticStack.PushElement(0);
									 } break;
							// 3 - сокет
							case 3 : {
										if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(CurrentWorkSocketTimeWait);  
										lstrbuf = SourceStream.readLine(); 
										if (lstrbuf==null) { ArithmeticStack.PushElement(-1); lstrbuf=""; } else ArithmeticStack.PushElement(0);
										if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(0);  
									 } break;
						}
						StringVariable.setElementAt(lstrbuf,(int)lstrindx);
				}
           }
			catch (Exception e)
			{ 
				LastError = "Error in RD_STR word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}
		
		void SETINPSTREAM() throws Exception // SetInpStream устанавливает поток ввода 0 - сокет, 1 - файл открытый пользователем на чтение, 2-поток из БД
		{
           try
           {     
                if (State) Acm.push(new Integer(INDX_SETINPSTREAM));
                else
                {
                        long lstrindx = ArithmeticStack.PopElement();
						switch ((int)lstrindx)
						{
							// 0 - сокет
							case 0 : SourceStream = new BufferedReader(new InputStreamReader(Sys_Socket.getInputStream())); break; 
							// 1 - файл
							case 1 : {
										 if (CurrentWorkInputFileStream==null) throw new Exception(" file open not found");
										 SourceStream = new BufferedReader(new InputStreamReader((InputStream)CurrentWorkInputFileStream));
									 }; break;
							// 2 - блоб из БД
							case 2 : {
										 if (CurrentWorkInputBlobStream==null) throw new Exception(" blob stream not found");
										 SourceStream = new BufferedReader(new InputStreamReader((InputStream)CurrentWorkInputBlobStream));
									 }; break;
							// 3 - текущий рабочий сокет
							case 3 : {
										 if (CurrentWorkSocket==null) throw new Exception(" open socket not found");
										 SourceStream = new BufferedReader(new InputStreamReader(CurrentWorkSocket.getInputStream()));
									 }; break;
						default : throw new Exception("unknown mode");
						}
						streamsrc = (int)lstrindx;
				}
           }
			catch (Exception e)
			{ 
				LastError = "Error in SetInpStream word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

		void SETOUTSTREAM() throws Exception // SetOutStream устанавливает поток вывода
		{
           try
           {     
                if (State) Acm.push(new Integer(INDX_SETOUTSTREAM));
                else
                {
                        long lstrindx = ArithmeticStack.PopElement();
						switch ((int)lstrindx)
						{
							// 0 - буффер	
							case 0 : DestinationStream = BufferDestination; break; 
							// 1 - сокет
							case 1 : DestinationStream = Sys_Socket.getOutputStream(); break;
							// 2 - консоль сервера
							case 2 : DestinationStream = (OutputStream)System.out; break;
							// 3- Открытый пользователем файл
							case 3 : {
										 if (CurrentWorkOutputFileStream==null) throw new Exception(" file open not found");
										 DestinationStream = (OutputStream)CurrentWorkOutputFileStream; 
									 }; break;
							// 4 - открытый пользователем сокет
							case 4 : {
										 if (CurrentWorkSocket==null) throw new Exception(" open socket not found");
										 DestinationStream = CurrentWorkSocket.getOutputStream();  
									 }
						default : throw new Exception("unknown mode");
						}
						streamdest = (int)lstrindx;
				}
           }
			catch (Exception e)
			{ 
				LastError = "Error in SetOutStream word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}
		
		void STREAM_EMIT() throws Exception // EMIT Запись байта в поток
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_STREAM_WRITEBYTE));
                else
                {
                        long lstrindx = ArithmeticStack.PopElement();
                        DestinationStream.write((int)lstrindx);   
                        DestinationStream.flush(); 
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in EMIT word";
				throw new Exception();
			}
        }

        void STREAM_WRITESTRING() throws Exception // S. Запись строки из ячейки с номером на АС
        {
          try
          { 
                if (State) Acm.push(new Integer(INDX_STREAM_WRITESTRING));
                else
                {
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(CurrentWorkSocketTimeWait);  
						long lstrindx = ArithmeticStack.PopElement();
                        String lstr = (String)StringVariable.elementAt((int)lstrindx);
                        byte [] lbyte = lstr.getBytes(); 
						for (int li=0; li<lbyte.length; li++) DestinationStream.write((byte)lbyte[li]);
                        DestinationStream.flush();
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(0);  
                }
          }
  			catch (Exception e)
			{ 
				LastError = "Error in S. word";
				throw new Exception();
			}
        }

        void STREAM_WRITENUM() throws Exception // . Запись числа с вершины АС в виде строки
        {
          try
          { 
                if (State) Acm.push(new Integer(INDX_STREAM_WRITENUM));
                else
                {
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(CurrentWorkSocketTimeWait);  
						long lstrindx = ArithmeticStack.PopElement();
                        String lstr = String.valueOf((int)lstrindx); 
                        byte [] lbyte = lstr.getBytes(); 
						for (int li=0; li<lbyte.length; li++) DestinationStream.write((byte)lbyte[li]);  
                        DestinationStream.flush();
						if (CurrentWorkSocket!=null) CurrentWorkSocket.setSoTimeout(0);  
                }
          }
		  catch (Exception e)
		  { 
		 	LastError = "Error in . word";
			throw new Exception();
		  }
		}

        void SEXECUTE() throws Exception // SEXECUTE Выполняет слово, в строке с индексом на АС
        {
           String lnew_string="";
           try
           {      
                if (State) Acm.push(new Integer(INDX_SEXECUTE));
                else
                {
                        long li = ArithmeticStack.PopElement(); 
                        lnew_string = ((String)StringVariable.elementAt((int)li)).trim().toUpperCase();   

						Stack cur_cmnd = (Stack)NameVocabulary.get(lnew_string);
                        if (cur_cmnd!=null)
						{
							ReturnStack.push(new Long(PC_Counter));
							ReturnStack.push(Cur_Word);
							PC_Counter=1;
							LocalVariableMemory=null;
							if (cur_cmnd!=null) LocalVariableMemory=(ForthStack)cur_cmnd.elementAt(0);
							if (LocalVariableMemory!=null) LocalCount=LocalVariableMemory.GetStackSize(); 
							Cur_Word = cur_cmnd; 
						}
						else
						{
							Long llong = (Long) GlobalVariableVocabulary.get(lnew_string);
							if (llong==null) throw new Exception();
							ArithmeticStack.PushElement(llong.longValue());   
						}
                } 
           }
			catch (Exception e)
			{ 
				LastError="Error in SEXECUTE word ["+lnew_string+"] "+e.getMessage();
				throw new Exception();
			}
		}

        void SFIND() throws Exception // SFIND Ищет слово в словаре и если оно найдено, то кладет -1 иначе 0
        {
           String lnew_string="";
           try
           {      
                if (State) Acm.push(new Integer(INDX_SFIND));
                else
                {
                        long li = ArithmeticStack.PopElement(); 
                        lnew_string = ((String)StringVariable.elementAt((int)li)).trim().toUpperCase();   
						
						if (GlobalVariableVocabulary.containsKey(lnew_string))
						{
							ArithmeticStack.PushElement(-1);  
						}
						else
						{
							if (NameVocabulary.containsKey(lnew_string))
							ArithmeticStack.PushElement(-1);  
							else ArithmeticStack.PushElement(0); 
						}
                } 
           }
           catch (Exception e)
           {
                LastError="Error in SFIND word ["+lnew_string+"] "+e.getMessage();
				throw new Exception();
			}
		}

        void ADD_CHAR2STR() throws Exception // CHR+ Берет код символа с вершины АС и прбавляет его к строке с индексом во втором элементе
        {
           try
           {      
                if (State) Acm.push(new Integer(INDX_ADDCHAR2STR));
                else
                {
                        long li = ArithmeticStack.PopElement(); 
                        long lii= ArithmeticStack.PopElement();
                        String lnew_string = (String)StringVariable.elementAt((int)lii);   
                        byte [] lib = new byte[1];
                        lib[0] = (byte)li;
                        lnew_string = lnew_string + new String(lib);  
                        StringVariable.setElementAt(lnew_string,(int)lii);   
                } 
           }
   			catch (Exception e)
			{ 
				LastError = "Error in CHR+ word";
				throw new Exception();
			}

        }

        void TOKEN_SET() throws Exception // TKN_SET Создает токенизер на вершине код делимитера, а во втором эл-те индекс разбираемой строки
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_TKN_SET));
                else
                {
                        long ch = ArithmeticStack.PopElement(); 
                        long li = ArithmeticStack.PopElement(); 
                        String str = (String)StringVariable.elementAt((int)li);   
                        byte [] chh = new byte [1];
                        chh[0]=(byte)ch;
                        StringTokenizer str_tok= new StringTokenizer(str,new String(chh));
                        ArithmeticStack.PushElement(StrTokenizers.size());
                        StrTokenizers.push(str_tok); 
                }
           }     
			catch (Exception e)
			{ 
				LastError = "Error in TKN_SET word";
				throw new Exception();
			}
        }

        void TOKEN_NEXT() throws Exception // TKN_NXT Берет индекс токенизера из второго элемента и записывает следующий токен в строку с индексом на вершине
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_TKN_NEXT)); 
                else
                {
                        long li_si = ArithmeticStack.PopElement();
                        long li_ti = ArithmeticStack.PopElement();
                        StringTokenizer str_tok=(StringTokenizer)StrTokenizers.elementAt((int)li_ti);  
                        String  str_ss = str_tok.nextToken();
                        StringVariable.setElementAt(str_ss,(int)li_si);    
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in TKN_NXT word";
				throw new Exception();
			}
        }

        void TOKEN_MORETOKENS() throws Exception // TKN_? Если в токенизере с индексом на вершине есть еще токены, то истина, иначе фалсе
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_TKN_MT)); 
                else
                {
                        long li_ti = ArithmeticStack.PopElement();
                        StringTokenizer str_tok=(StringTokenizer)StrTokenizers.elementAt((int)li_ti);  
                        if (str_tok.hasMoreTokens()) 
                         ArithmeticStack.PushElement(-1);
                        else     
                         ArithmeticStack.PushElement(0);
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in TKN_? word";
				throw new Exception();
			}
     
        }

        void S2HTML() throws Exception // S2HTML преобразует строку с индексом на вершине в формат принятый в HTML AS: a -> a
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_STR_S2HTML)); 
                else
                {
                        StringBuffer str_buf=new StringBuffer("");
                        long li = ArithmeticStack.LastElement();
                        String str = (String)StringVariable.elementAt((int)li);   
                        for(int lii=0; lii<str.length(); lii++)
                        {
                                switch (str.charAt(lii))
                                {
										case '$' : str_buf.append("$$"); break; 
										case '&' : str_buf.append("&amp;"); break; 
										case ' ' : str_buf.append("&nbsp;"); break; 
                                        case '<' : str_buf.append("&lt;"); break; 
                                        case '>' : str_buf.append("&gt;"); break; 
										case '\'' : str_buf.append("&apos;"); break; 
										case '\"': str_buf.append("&quot;"); break; 
                                        default :
                                                {
                                                        str_buf.append(str.charAt(lii));  
                                                }
                                } 
                        } 
                        StringVariable.setElementAt(str_buf.toString(),(int)li);   
                }
          }
  			catch (Exception e)
			{ 
				LastError = "Error in S2HTML word";
				throw new Exception();
			}

        }

        void S2SQL() throws Exception // S2SQL преобразует строку с индексом на вершине в формат принятый в SQL AS: a -> a
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_S2SQL)); 
                else
                {
                        StringBuffer str_buf=new StringBuffer("");
                        long li = ArithmeticStack.LastElement();
                        String str = (String)StringVariable.elementAt((int)li);   
                        for(int lii=0; lii<str.length(); lii++)
                        {
                                switch (str.charAt(lii))
                                {
                                        case '\'' : str_buf.append("\'\'"); break; 
                                        default :
                                                {
                                                        str_buf.append(str.charAt(lii));  
                                                }
                                } 
                        } 
                        StringVariable.setElementAt(str_buf.toString(),(int)li);   
                }
          }
			catch (Exception e)
			{ 
				LastError = "Error in S2SQL word";
				throw new Exception();
			}
        }

        void DATE_WEEKDAY() throws Exception // WEEKDAY Берет дату со стека и кладет на вершину номер, соотв. дню недели
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_DT_WEEKDAY)); 
                else
                {
                        long li_si = ArithmeticStack.PopElement();
                        li_si = new java.util.Date(li_si).getDay();  
                        ArithmeticStack.PushElement(li_si);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in WEEKDAY word";
				throw new Exception();
			}
        }

        void HEREI() throws Exception // HEREI Кладет на AS текущую позицию указателя в памяти переменных
        {
          try
          {            
                if (State) Acm.push(new Integer(INDX_HEREI)); 
                else
                {
                        ArithmeticStack.PushElement(IntVariable.GetStackSize());  
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in HEREI word";
				throw new Exception();
			}
        }

        void IZAPYATAYA() throws Exception // I, Записывает содержимое вершины AS в память переменных, увеличивая указатель
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_IZAPYATAYA));
                else
                {
                        long li_tmp = ArithmeticStack.PopElement();
                        IntVariable.PushElement(li_tmp);          
                }
             }
			catch (Exception e)
			{ 
				LastError = "Error in I, word";
				throw new Exception();
			}
		}       

        // Слова ФОРТ для работы с БД
        void SQL_COMMIT() throws Exception // DB_COMMIT Завершение транзакции для Connect c индексом на АС (AS: indx ->)
        {
          try
          {      
                if (State) Acm.push(new Integer(INDX_SQL_COMMIT));
                else
                {
                        long li_si = ArithmeticStack.PopElement();
                        ScriptForthDBConnection cur_con = (ScriptForthDBConnection) SQLConnections.elementAt((int)li_si);   
						cur_con.Connection.commit(); 
                }  
          }
			catch (Exception e)
			{ 
				LastError = "Error in DB_COMMIT word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_ROLLBACK() throws Exception // DB_ROLLBACK Откат транзакции
        {
           try
           {      
                if (State) Acm.push(new Integer(INDX_SQL_ROLLBACK));
                else
                {
                        long li_si = ArithmeticStack.PopElement();
                        ScriptForthDBConnection cur_con = (ScriptForthDBConnection) SQLConnections.elementAt((int)li_si);   
                        cur_con.Connection.rollback(); 
                }  
           }
			catch (Exception e)
			{ 
				LastError = "Error in DB_ROLLBACK word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_AUTOCOMMIT() throws Exception // DB_AUTOCOMMIT Режим автотранзакции (AS: connect_indx int_mode -> )  
        {
           try
           { 
                if (State) Acm.push(new Integer(INDX_SQL_AUTOCOMMIT)); 
                else
                {
                        long isolmode = ArithmeticStack.PopElement();
                        long conindx =  ArithmeticStack.PopElement();
                        ScriptForthDBConnection cur_con = (ScriptForthDBConnection) SQLConnections.elementAt((int)conindx);                                           
                        if (isolmode==0) cur_con.Connection.setAutoCommit(false); else cur_con.Connection.setAutoCommit(true);  
                }
            }
			catch (Exception e)
			{ 
				LastError = "Error in DB_AUTOCOMMIT word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}
        
        void SQL_SETTRANSISOLATION() throws Exception// DB_TRANS Задает режим изоляции транзакции (AS: connect_indx int ->)
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_SQL_TRANSISOLATION)); 
                else
                {
                        long isolmode = ArithmeticStack.PopElement();
                        long conindx =  ArithmeticStack.PopElement();
                        ScriptForthDBConnection cur_con = (ScriptForthDBConnection) SQLConnections.elementAt((int)conindx);                                           

                        switch ((int)isolmode) 
                        {
                                case 0 : cur_con.Connection.setTransactionIsolation(cur_con.Connection.TRANSACTION_READ_UNCOMMITTED); break;
                                case 1 : cur_con.Connection.setTransactionIsolation(cur_con.Connection.TRANSACTION_READ_COMMITTED); break;
                                case 2 : cur_con.Connection.setTransactionIsolation(cur_con.Connection.TRANSACTION_REPEATABLE_READ); break;
                                case 3 : cur_con.Connection.setTransactionIsolation(cur_con.Connection.TRANSACTION_SERIALIZABLE); break;
                                case 4 : cur_con.Connection.setTransactionIsolation(cur_con.Connection.TRANSACTION_NONE); break;
                                default : throw new Exception("Error mode for DB_TRANSISOLATION");  
                        }
                }
            }
			catch (Exception e)
			{ 
				LastError = "Error in DB_TRANSISOLATION word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_GETBLOBSTREAMFORNAME() throws Exception // DB_NGETBLOB берет имя столбца с АС, индекс ResultSet из второго элемента, и открывает поток
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_SQL_NGETBLOB));
                else
                {
                        long li_ci = ArithmeticStack.PopElement();
                        String lcolname = (String) StringVariable.elementAt((int)li_ci);
   
                        li_ci = ArithmeticStack.PopElement();
                        java.sql.ResultSet lrs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_ci);   
                               
						if (CurrentWorkInputBlobStream!=null) 
						{
							CurrentWorkInputBlobStream.close();
							CurrentWorkInputBlobStream=null;;
						}
						InputStream lis = lrs.getBinaryStream(lcolname);
						CurrentWorkInputBlobStream = lis;
				}
           }
			catch (Exception e)
			{ 
				LastError = "Error in DB_NGETBLOB word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}        

        void SQL_GETBLOBSTREAMFORNUMBER() throws Exception // DB_GETBLOB берет номер столбца с АС, индекс ResultSet из второго элемента, формирует входной поток
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_SQL_GETBLOB));
                else
                {
						CurrentWorkInputBlobStream = null;
					    long li_ci = ArithmeticStack.PopElement();
 
                        li_ci = ArithmeticStack.PopElement();
                        java.sql.ResultSet lrs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_ci);   
                                
                        InputStream lis = lrs.getBinaryStream((int)li_ci);
						if (CurrentWorkInputBlobStream!=null) 
						{
							CurrentWorkInputBlobStream.close();
							CurrentWorkInputBlobStream=null;;
						}
						CurrentWorkInputBlobStream = lis;
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in DB_GETBLOB word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }        

        void SQL_GETCOLUMNINTFORNAME() throws Exception // DB_NGETINT то же, что и DB_GETINT, вместо номера используется имя столбца
        {
		   String nmcol=null; 
           try
           {     
                if (State) Acm.push(new Integer(INDX_SQL_GETNAMECOLUMNINT));
                else
                {
						CurrentWorkInputBlobStream = null;
					    long li_ci = ArithmeticStack.PopElement();
                        long li_rs = ArithmeticStack.PopElement();
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_rs);
                        if (my_rs==null) throw new Exception("Error");
                        nmcol = (String)StringVariable.elementAt((int)li_ci);
						Object tobj = my_rs.getObject (nmcol);
						
						long ff;
						if (my_rs.wasNull() || (tobj==null)) ff=0; 
						else 
						{	
							if (tobj instanceof java.util.Date) ff = ((java.util.Date)tobj).getTime(); 
							else
							ff=((Number)tobj).longValue();
						}
                        ArithmeticStack.PushElement(ff);  
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in DB_NGETINT word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_GETCOLUMNSTRFORNAME() throws Exception // DB_NGETSTR то же, что и DB_GETSTR, но вместо номера используется имя столбца
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_SQL_GETNAMECOLUMNSTR));
                else
                {
						CurrentWorkInputBlobStream = null;
					    long li_si = ArithmeticStack.PopElement();
						long li_ci = ArithmeticStack.PopElement();
						long li_rs = ArithmeticStack.PopElement();

						java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_rs);
						if (my_rs==null) throw new Exception("Error");
                        String tt = (String) StringVariable.elementAt((int)li_ci);
						Object tobj =my_rs.getBytes(tt);
						String ff=null;
						if (my_rs.wasNull() || (tobj==null)) ff="";	else ff = new String((byte[])tobj);
						StringVariable.setElementAt(ff,(int)li_si);                                
                }
           }
			catch (Exception e)
			{ 
				LastError = "Error in DB_NGETSTR word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_GETCOLUMNSTR() throws Exception // DB_GETSTR Получить значение столбца с номером во втором эл-те, у текущей записи ResultSet'a в третьем эл-те 
                                                 // в виде строки и положить эту строку в ячейку с индексом на вершине АС
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_SQL_GETCOLUMNSTR));
                else
                {
                        long li_si = ArithmeticStack.PopElement();
                        long li_ci = ArithmeticStack.PopElement();
                        long li_rs = ArithmeticStack.PopElement();
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_rs);
                        if (my_rs==null) throw new Exception("Error");
						String ff;Object tstr=null;
						tstr = my_rs.getBytes((int)li_ci);
						if (my_rs.wasNull() || (tstr==null)) ff=""; else ff=new String((byte[])tstr);
                        StringVariable.setElementAt(ff,(int)li_si);                                
                }
            }
			catch (Exception e)
			{ 
				LastError = "Error in DB_GETSTR word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_GETCOLUMNINT() throws Exception // DB_GETINT Получить значение столбца с номером на АС у текущей записи ResultSet'a во втором эл-те 
                                                 // в виде целого числа и положить это число на вершину
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_SQL_GETCOLUMNINT));
                else
                {
                        long li_ci = ArithmeticStack.PopElement();
                        long li_rs = ArithmeticStack.PopElement();
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_rs);
                        if (my_rs==null) throw new Exception("Error");
						Object nobj = my_rs.getObject((int)li_ci);
						long ff=0;
						if (my_rs.wasNull() || (nobj==null)) ff=0; else 
						{
							if (nobj instanceof java.util.Date) ff = ((java.util.Date)nobj).getTime(); else 
							ff= ((java.lang.Number)nobj).longValue();
						}
                        ArithmeticStack.PushElement(ff);  
                }
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_GETINT word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_NEXT() throws Exception // DB_NEXT Переходит к следующей записи у ResultSet с индексом на вершине, если нет больше записей, то false иначе true
        {
            try
            {     
                if (State) Acm.push(new Integer(INDX_SQL_NEXT));
                else
                {
                        long li_ci = ArithmeticStack.PopElement();
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)li_ci);
                        boolean ff = my_rs.next(); 
                        if (ff) ArithmeticStack.PushElement(-1);
                        else 
                        {                                
                           ArithmeticStack.PushElement(0);
                           my_rs.close();
                           my_rs = null;           
                        }
                }
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_NEXT word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_CLOSERESULTSET() throws Exception // DB_RSCLOSE Закрывает ResultSet с индексом на AS
        {
            try
            {        
                if (State) Acm.push(new Integer(INDX_SQL_RSCLOSE));
                else
                {
                       long lid; 
                       try
                       { 
                        lid = ArithmeticStack.PopElement();
                       }
                       catch (Exception e)
                       {
                         throw new Exception("Empty stack"); 
                       }   
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt((int)lid);
						java.sql.Statement my_ss = (java.sql.Statement) SQLStatements.elementAt((int)lid);
						if (SQLResultSets.size()==((int)lid+1)) 
						{
                               SQLResultSets.pop(); 
                               SQLStatements.pop();
						}
						try
						{
							if (my_rs!=null) my_rs.close(); 
							if (my_ss!=null) my_ss.close(); 
						} catch (SQLException s) {}
						my_rs=null;
						my_ss=null;
                }  
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_RSCLOSE word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_EXECUTEQ() throws Exception // DB_EXQ отправляет строку SQL серверу БД, на АС кладется индекс SQLResultSets
                                               // Вторым элементом является индекс в SQLConnect 
        {
            try
            {        
                if (State) Acm.push(new Integer(INDX_SQL_EXECUTEQ));
                else
                {
                       long lid;
                       String lss;  
                       lid = ArithmeticStack.PopElement();
                       lss = (String) StringVariable.elementAt((int)lid); 
                       lid = ArithmeticStack.PopElement();
                       java.sql.Statement my_statement = ((ScriptForthDBConnection) SQLConnections.elementAt((int)lid)).Connection.createStatement();   
                       SQLStatements.push(my_statement); 
                       java.sql.ResultSet new_rs=my_statement.executeQuery(lss); 
                       ArithmeticStack.PushElement(SQLResultSets.size());
                       SQLResultSets.push(new_rs); 
                }  
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_EXQ word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void SQL_EXECUTEU() throws Exception // DB_EXU отправляет строку SQL серверу БД, на АС кладется значение результата
                                               // Вторым элементом является индекс в SQLStatements 
        {
            try
            {     
                if (State) Acm.push(new Integer(INDX_SQL_EXECUTEU));
                else
                {
                        long lid = ArithmeticStack.PopElement();
                        String lss = (String) StringVariable.elementAt((int)lid); 
                        lid = ArithmeticStack.PopElement();
                        java.sql.Statement my_statement = ((ScriptForthDBConnection) SQLConnections.elementAt((int)lid)).Connection.createStatement();   
                        if (my_statement==null) throw new Exception("Error");
                        int ll = my_statement.executeUpdate(lss); 
                        ArithmeticStack.PushElement(ll);
                        my_statement.close();
                }  
            }
			catch (Exception e)
			{ 
				LastError = "Error in DB_EXU word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_CLOSE() throws Exception // DB_CLOSE закрывает соединение с индексом на АС
        {
            try
            {        
                if (State) Acm.push(new Integer(INDX_SQL_CLOSE));
                else
                {
                        long lid = ArithmeticStack.PopElement();
                        ScriptForthDBConnection my_connection = (ScriptForthDBConnection) SQLConnections.elementAt((int)lid);                          
                        if (my_connection==null) throw new Exception("Error");
                        my_connection.Close();  
                        my_connection = null;
                } 
            }
			catch (Exception e)
			{ 
				LastError = "Error in DB_CLOSE word ["+e.getMessage()+"]";
				throw new Exception();
			}
		}

        void SQL_LOADDRIVER() throws Exception // DB_LOAD Загружает драйвер базы данных, если успешно, то true  иначе false
        {
             try
             {   
                if (State) Acm.push(new Integer(INDX_SQL_LOADDRIVER));
                else
                {
                        long lid = ArithmeticStack.PopElement();
                        String lss = (String) StringVariable.elementAt((int)lid); 
                        try
                        {
                                Class.forName(lss).newInstance();
                                ArithmeticStack.PushElement(-1);
                        }
                        catch (Exception e)
                        {
                                ArithmeticStack.PushElement(0);
                        }
                }  
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_LOAD word ["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void INCMEM() throws Exception // 1+! Инкремент ячейки памяти с номером на АС 
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_INCMEM ));
                else
                {
                        long jj;
                        long addr = ArithmeticStack.PopElement();
                        jj = IntVariable.GetElementAt((int)addr)+1 ; 
                        IntVariable.SetElementAt(jj,(int)addr);                        
                }  
           }
			catch (Exception e)
			{ 
				LastError = "Error in 1+! word";
				throw new Exception();
			}
		}

        void SQL_CONNECT() throws Exception // DB_CONNECT Производит коннект к заданной базе данных, 
										 // AS: url_base user_name user_password code_page -> err_code | indx
                                            //  во втором эл-те индекс имени пользователя, в 3-м URL базы данных
                                            // если успешно, то индекс, иначе исключение, а на вершине код ошибки
        {
            try
            {        
                if (State) Acm.push(new Integer(INDX_SQL_CONNECT));
                else
                {
                        long lid = ArithmeticStack.PopElement();
						String lscp = (String) StringVariable.elementAt((int)lid); 
                        lid = ArithmeticStack.PopElement();
						String lsp = (String) StringVariable.elementAt((int)lid); 
                        lid = ArithmeticStack.PopElement();
                        String lsn = (String) StringVariable.elementAt((int)lid); 
                        lid = ArithmeticStack.PopElement();
                        String lsu = (String) StringVariable.elementAt((int)lid); 

                        Properties lprop = new Properties();
                        lprop.put("user",lsn);lprop.put("password",lsp);
						lprop.put("charSet",lscp); 
        
						ScriptForthDBConnection new_connect = new ScriptForthDBConnection(lsu,lprop,busp.dbaliases);  
						if (new_connect.StateConnection>=0)
						{
							ArithmeticStack.PushElement(new_connect.StateConnection);
							new_connect.Close(); new_connect=null;
							throw new Exception("Not connect to database, timeout may be");
						}
						else
						{
							ArithmeticStack.PushElement(SQLConnections.size()); 
							SQLConnections.push(new_connect);  
						}
                }  
             }
			catch (Exception e)
			{ 
				LastError = "Error in DB_CONNECT word";
				if (e.getMessage()!=null) LastError+="["+e.getMessage()+"]";
				throw new Exception();
			}
        }

        void URL_ENC() throws Exception // S_ENC кодирует строку в формат URL , строка в ячейкес индексом на вершине АС
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_STR_URLENCODE));
                else
                {
                        long lid = ArithmeticStack.LastElement();
                        String lis = (String)StringVariable.elementAt((int)lid);
                        lis = EncodeString(lis);    
                        StringVariable.setElementAt(new String(lis),(int)lid);  
                }  
            }
			catch (Exception e)
			{ 
				LastError = "Error in S_ENC word";
				throw new Exception();
			}
		}

        void URL_DEC() throws Exception // S_DEC декодирует строку из формата URL , строка в ячейке с индексом на вершине АС
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_STR_URLDECODE));
                else
                {
                        long lid = ArithmeticStack.LastElement();
                        String lis = (String)StringVariable.elementAt((int)lid);
                        lis = DecodeString(lis);    
                        StringVariable.setElementAt(new String(lis),(int)lid);  
                }  
            }
			catch (Exception e)
			{ 
				LastError = "Error in S_DEC word";
				throw new Exception();
			}
		}

        void I2S() throws Exception // i2s переводит числовое значение в строку и записывает её по адресу на вершине (AS: int str_indx ->)
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_I2S));                
                else
                {
                        long lid = ArithmeticStack.PopElement();  
                        long lis = ArithmeticStack.PopElement();  
                        StringVariable.setElementAt(String.valueOf(lis),(int)lid);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in I2S word";
				throw new Exception();
			}
        }

        void I2H() throws Exception // I2H переводит числовое значение в строковое шестнадцатиричное представление и записывает её по адресу на вершине (AS: int str_indx ->)
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_I2H));                
                else
                {
                        long lid = ArithmeticStack.PopElement();  
                        long lis = ArithmeticStack.PopElement();  
                        String hexstr=Long.toHexString(lis);
                        if (hexstr.length()<6) 
                        {
                                String spc_buf = "000000";
                                if (hexstr.length()<6) hexstr = spc_buf.substring(0,6-hexstr.length())+hexstr;  
                        }
                        StringVariable.setElementAt(hexstr,(int)lid);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in I2H word";
				throw new Exception();
			}

        }

        void S2I() throws Exception // s2i переводит строку в числовое значение (AS: str_indx -> int)
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_S2I));                
                else
                {
                        long lid = ArithmeticStack.PopElement();  
                        String lls = (String)StringVariable.elementAt((int)lid);
                        long lis = Long.valueOf(lls).longValue();
                        ArithmeticStack.PushElement(lis);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in S2I word";
				throw new Exception();
			}

        }

        void TO_STRING() throws Exception // S! Копирует строку из элемента с индексом во втором элементе в элемент с индексом на вершине АС
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_TO_STRING));                
                else
                {
                        long lid = ArithmeticStack.PopElement();  
                        long lis = ArithmeticStack.PopElement();  
                        String lss = (String) StringVariable.elementAt((int)lis); 
                        StringVariable.setElementAt(new String(lss),(int)lid);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in S! word";
				throw new Exception();
			}
		}

        void LEAVE() throws Exception // LEAVE Обеспечивает немедленный выход из цикла DO-LOOP или BEGIN-UNTIL
        {
            try
            {        
                if (State)
                {
                        Acm.push(new Integer(INDX_LEAVE));                
                        LEAVE_address = Acm.size(); 
                        Acm.push(new Integer(0));                
                }
                else
                {
                        ReturnStack.pop();      
						ReturnStack.pop();      
                        Cycle_Index.PopElement();

                        Integer li=(Integer)Cur_Word.elementAt(PC_Counter); 
                        PC_Counter=li.intValue(); 
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in LEAVE word";
				throw new Exception();
			}
		}

        void QUIT() throws Exception // Обеспечивает немедленный выход из программы
        {
                if (State)
                {
                        Acm.push(new Integer(INDX_QUIT));                
                }
                else
                {
                        IsQuit = true;
                }
        }

        void FIND() throws Exception // Ищет в словаре слово с именем из следующего слова, если есть то кладет на АС true иначе false
        {
                Object lobj;
                int ltt = GetNextToken(fis,false); 
                switch(ltt)
                {
					case TOKEN_EOF : { LastError = "Error: After \"'\" must be word" ; throw new Exception();};
					case TOKEN_NUMBER : { LastError = "Error: After \"'\" must be word" ; throw new Exception();};
					case TOKEN_STRING : { LastError = "Error: After \"'\" must be word" ; throw new Exception();};
                }
                svar = svar.toUpperCase(); 
                lobj = NameVocabulary.get(svar);
                if (State) 
                {
                   Acm.push(new Integer(INDX_PUSH));
                   if (lobj!=null) 
                   Acm.push(new Long(-1));
                   else Acm.push(new Long(0));     
                }
                else
                {
                   if (lobj!=null) ArithmeticStack.PushElement(-1);
                   else ArithmeticStack.PushElement(0);     
                }
        }

        void ALLOT() throws Exception // Выделяет память (инициализируя нулем) в области переменных и кладет адрес её начала на АС
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_ALLOT));
                else
                {
                        long li = ArithmeticStack.PopElement();
                        long cur_indx=IntVariable.GetStackSize();  
                        for(int ll=0;ll<(int)li;ll++)
                        {
                                IntVariable.PushElement(0);  
                        }  
                        ArithmeticStack.PushElement(cur_indx);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in ALLOT word";
				throw new Exception();
			}
		}

        void DEPTH() throws Exception // Кладет на АС количество элементов до выполнение команды
        {
                if (State) Acm.push(new Integer(INDX_DEPTH));
                else
                {
                        long li = (long)ArithmeticStack.GetStackSize();
                        ArithmeticStack.PushElement(li);  
                }
        }

        void PLAYFILE() throws Exception // Передает управление файлу с именем на АС
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_PLAYFILE));
                else
                {
                        long li = ArithmeticStack.PopElement();

                        String lstr = Home_Dir+(String) StringVariable.elementAt((int)li);
                        fis.close();
                        PlayScript(lstr);
                        IsQuit = true;
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in PLAYFILE word";
				throw new Exception();
			}
		}

        void DEPTHR() throws Exception // Кладет на АС количество элементов СВ 
        {
                if (State) Acm.push(new Integer(INDX_DEPTHR));
                else
                {
                        long li = ReturnStack.size();
                        ArithmeticStack.PushElement(li);  
                }
        }

        void CALL() throws Exception // Передает управление слову с номером за командой
        {
                if (State) Acm.push(new Integer(INDX_CALL));
                else
                {
                        Stack li;
                        li = (Stack) (Cur_Word.elementAt(PC_Counter));
                        PC_Counter++;
						ReturnStack.push(new Long(PC_Counter)); // Кладем показатель счетчика
                        ReturnStack.push(Cur_Word); // Кладем указатель на тело
                        PC_Counter=1;
						LocalVariableMemory=(ForthStack)li.elementAt(0); 
						if (LocalVariableMemory!=null) LocalCount=LocalVariableMemory.GetStackSize();
                        Cur_Word = li; 
						if (!TRY_stack.empty())
						{
								int ltmp = ((Integer)TRY_stack.peek()).intValue()+1;
								TRY_stack.setElementAt(new Integer(ltmp),TRY_stack.size()-1);
						}
				}
        } 

        void CYCLE_I() throws Exception // I
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_CYCLE_I)); else 
                {
                        Long li1 = (Long) ReturnStack.peek(); 
                        ArithmeticStack.PushElement(li1.longValue());  
                }  
           }
   			catch (Exception e)
			{ 
				LastError = "Error in I word";
				throw new Exception();
			}
		}

        void CYCLE_J() throws Exception // J
        {
           try
           {                  
                if (State) Acm.push(new Integer(INDX_CYCLE_J)); else 
                {
                        Long li1 = (Long) ReturnStack.elementAt(ReturnStack.size()-3); 
                        ArithmeticStack.PushElement(li1.longValue());  
                }  
           }
   			catch (Exception e)
			{ 
				LastError = "Error in J word";
				throw new Exception();
			}
		}

		void RAISE() throws Exception // Оператор TRY
		{
			if (State) 
			{
					Acm.push(new Integer(INDX_RAISE));
            }               
            else
            {
				LastError="This error generate with RAISE word";
				throw new Exception();
			}
		}
		
		void TRY() throws Exception // Оператор TRY
		{
			if (State) 
			{
					Acm.push(new Integer(INDX_TRY));
					TRY_index.PushElement(Acm.size());
					Acm.push(new Integer(0));
            }               
            else
            {
                   Integer lii = (Integer) Cur_Word.elementAt (PC_Counter);
				   PC_Counter++;
                   if (lii.intValue()==0)
				   {
					   LastError = "Not closed TRY structure";
					   throw new Exception();     
				   }
				   TRY_stack.push(Cur_Word);
				   TRY_stack.push(lii);
				   TRY_stack.push(new Integer(0));
			}
		}

        void EXCEPT() throws Exception // Оператор EXCEPT
        {
                int li;
                if (State) 
                {
                   try
                   {          
					    li=TRY_index.PopElement();
						Acm.setElementAt(new Integer(Acm.size()+2),li);      
						Acm.push(new Integer(INDX_ELSE));  
						EXCEPT_index.PushElement(Acm.size());
						Acm.push(new Integer(0));  
				   }     
                   catch (Exception e)
                   {
					   LastError ="Error in EXCEPT word";
					   throw new Exception();
                   }          
                }               
                else
                {
					LastError = "Use EXCEPT in compile mode, only!";
					throw new Exception ();                        
                }
        }

        void TRYEND() throws Exception // Оператор TRYEND
        {
                int li;
                if (State) 
                {
                   try
                   {          
					    li=EXCEPT_index.PopElement();
                        Acm.setElementAt(new Integer(Acm.size()),li);      
					    Acm.push(new Integer(INDX_TRYEND));
                   }     
                   catch (Exception e)
                   {
					   LastError ="Error in TRYEND word";
					   throw new Exception();
                   }          
                }               
                else
                {
					if (TRY_stack.size()<3)
					{
						LastError = "TRYEND without TRY";
						throw new Exception(); 
					}
					TRY_stack.pop(); 
					TRY_stack.pop(); 
					TRY_stack.pop();
				}
        }
		
		void IF() throws Exception // Оператор IF
        {
                if (State) 
                {
                        Acm.push(new Integer(INDX_IF));
                        IF_index.PushElement(Acm.size());
                        Acm.push(new Integer(0));
                }               
                else
                {
                        long li = ArithmeticStack.PopElement();  
                        if (li==0)
                        {
                           Integer lii = (Integer) Cur_Word.elementAt (PC_Counter);
                           if (lii.intValue()==0)
						   {
							   LastError = "Not closed IF structure";
							   throw new Exception();     
						   }
                           PC_Counter = lii.intValue();
                        } 
                        else
                        {
                           PC_Counter++;     
                        }
                }
        }

        void THEN() throws Exception // Оператор THEN
        {
                int li;
                if (State) 
                {
                   try
                   {          
                        li=IF_index.PopElement();
                        Acm.setElementAt(new Integer(Acm.size()),li);
                   }     
                   catch (Exception e)
                   {
					   LastError ="Error in THEN word";
					   throw new Exception();
                   }          
                }               
                else
                {
					LastError = "Use THEN in translate mode";
					throw new Exception ();                        
                }
        }

        void ELSE() throws Exception // Оператор ELSE
        {
                int li;
                if (State) 
                {
                        li=IF_index.PopElement();
                        Acm.push(new Integer(INDX_ELSE));
                        IF_index.PushElement(Acm.size());
                        Acm.push(new Integer(0));
                        Acm.setElementAt(new Integer(Acm.size()),li);      
                }               
                else
                {
                        li=((Integer)Cur_Word.elementAt(PC_Counter)).intValue(); 
                        PC_Counter=li; 
                }
        } 

        void DO() throws Exception // Начинает цикл
        {
           try
           {          
                if (State) Acm.push(new Integer(INDX_DO)); else 
                {
                   long li1 = ArithmeticStack.PopElement();
                   long li2 = ArithmeticStack.PopElement();
                   ReturnStack.push(new Long(li2));
                   ReturnStack.push(new Long(li1));                  
                   Cycle_Index.PushElement(PC_Counter);  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in DO word";
				throw new Exception();
			}

        }

        void BEGIN() throws Exception // Начинает цикл BEGIN-UNTIL
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_BEGIN)); else 
                {
                   Cycle_Index.PushElement(PC_Counter);  
                   ReturnStack.push(new Long(0));      
                   ReturnStack.push(new Long(0));
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in BEGIN word";
				throw new Exception();
			}
		}

        void LOOP() throws Exception // Заканчивает цикл
        {
          try
          {            
                if (State) 
                {
                   Acm.push(new Integer(INDX_LOOP)); 
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else 
                {
                   Long li1 = (Long) ReturnStack.pop();
                   Long li2 = (Long) ReturnStack.peek();       
                   li1 = new Long(li1.intValue()+1);      
                   if (li1.longValue()>=li2.longValue())  
                   {
                        ReturnStack.pop();      
                        Cycle_Index.PopElement();  
                   }      
                   else 
                   {
                        PC_Counter = Cycle_Index.LastElement(); 
                        ReturnStack.push(li1); 
                   }          
                }  
           }
   			catch (Exception e)
			{ 
				LastError = "Error in LOOP word";
				throw new Exception();
			}
        }

        void ADD_LOOP() throws Exception // +LOOP Заканчивает цикл c приращением на АС
        {
            try
            {    
                if (State)
                {
                   Acm.push(new Integer(INDX_ADD_LOOP)); 
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else
                {
                   Long li1 = (Long) ReturnStack.pop();
                   Long li2 = (Long) ReturnStack.peek();       
                   long li3 = ArithmeticStack.PopElement();  
                   li1 = new Long(li1.longValue()+li3);      

                   if (li3<0)
                   {
                        if (li1.longValue()<li2.longValue())  
                        {
                                ReturnStack.pop();      
                                Cycle_Index.PopElement();  
                        }      
                        else 
                        {
                                PC_Counter = (int)Cycle_Index.LastElement(); 
                                ReturnStack.push(li1); 
                        }          
                   }  
                   else
                   {          
                        if (li1.longValue()>=li2.longValue())  
                        {
                                ReturnStack.pop();      
                                Cycle_Index.PopElement();  
                        }      
                        else 
                        {
                                PC_Counter = (int)Cycle_Index.LastElement(); 
                                ReturnStack.push(li1); 
                        }          
                   }  
               } 
           }
   			catch (Exception e)
			{ 
				LastError = "Error in +LOOP word";
				throw new Exception();
			}
		}


        void UNTIL() throws Exception // Конец цикла BEGIN-UNTIL
        {
            try
            {     
                if (State)
                {
                   Acm.push(new Integer(INDX_UNTIL));
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else
                {
                   long li3 = ArithmeticStack.PopElement();  
                   if (li3==0)     
                   {
                       PC_Counter = (int)Cycle_Index.LastElement(); 
                   } 
                   else
                   {
                       Cycle_Index.PopElement();  
                       ReturnStack.pop();                              
                       ReturnStack.pop();      
                   }          
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in UNTIL word";
				throw new Exception();
			}
		}

        void PUSH() throws Exception // Кладет на стек, следующее за ним слово
        {
                if (State) Acm.push(new Integer(INDX_PUSH)); 
                else 
                {  
                        Long li;
                        li = (Long) (Cur_Word.elementAt(PC_Counter));
                        PC_Counter++;
                        ArithmeticStack.PushElement(li.longValue());                   
                }
        }        

        void S_EQU() throws Exception // S= Сравнивает две строки, с адресами на АС, с учетом регистра
        {
           try
           {       
                if (State) Acm.push(new Integer(INDX_S_EQU)); 
                else 
                {
                        long li1 = ArithmeticStack.PopElement(); 
                        long li2 = ArithmeticStack.PopElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        String s2 = (String) StringVariable.elementAt((int)li2); 
                        if (s1.compareTo(s2)==0)li1=-1; else li1=0;
                        ArithmeticStack.PushElement(li1);  
                }
         }
   			catch (Exception e)
			{ 
				LastError = "Error in S= word";
				throw new Exception();
			}
		}

        void S_EQUNC() throws Exception // S== Сравнивает две строки, с адресами на АС, без учета регистра
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_S_EQUNC)); 
                else 
                {
                        long li1 = ArithmeticStack.PopElement(); 
                        long li2 = ArithmeticStack.PopElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        String s2 = (String) StringVariable.elementAt((int)li2); 
                        if (s1.equalsIgnoreCase(s2))li1=-1; else li1=0;
                        ArithmeticStack.PushElement(li1);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S== word";
				throw new Exception();
			}
		}

        void SYMBOL_POS() throws Exception // S_PS Ищет в строке из второго элемента, подстроку с номером на АС, если нет то -1 на АС иначе номер вхождения
        {
           try
           {      
                if (State) Acm.push(new Integer(INDX_S_PS)); 
                else 
                {
                        long li1 = ArithmeticStack.PopElement(); 
                        long li2 = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        String s2 = (String) StringVariable.elementAt((int)li2); 
                        ArithmeticStack.PushElement(s2.indexOf(s1));  
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in S_PS word";
				throw new Exception();
			}
		}

        void CHR() throws Exception // CHR (AS: int str_indx -> str_indx)
        {
           try
           {      
                if (State) Acm.push(new Integer(INDX_CHR)); 
                else 
                {
                        long li = ArithmeticStack.PopElement(); 
                        long lil= ArithmeticStack.PopElement(); 
                        ArithmeticStack.PushElement(li);
                        String lnew_string = String.valueOf((char)lil);
                        StringVariable.setElementAt(lnew_string,(int)li);   
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in CHR word";
				throw new Exception();
			}
		}


        void S_ADD() throws Exception // S+ Складывает две строки, с адресами на АС, полученное значение кладется в 1-е слагаемое
        {
           try
           {        
                if (State) Acm.push(new Integer(INDX_S_ADD)); 
                else 
                {
                        long li1 = ArithmeticStack.PopElement(); 
                        long li2 = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        String s2 = (String) StringVariable.elementAt((int)li2); 
                        s2 = s2+s1;
                        StringVariable.setElementAt(s2,(int)li2); 
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S+ word";
				throw new Exception();
			}
		}

        void UPPERCASE() throws Exception // S_UC Приводит все символы в верхний регистр
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_S_UC)); 
                else 
                {  
                        long li1 = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        s1=s1.toUpperCase();                         
                        StringVariable.setElementAt(s1,(int)li1);                         
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in S_UC word";
				throw new Exception();
			}
        }

		void SUBSTRING() throws Exception // S_SB Копирует подстроку AS: str_src str_dest start_pos length ->
        {
            try
            {             
                if (State) Acm.push(new Integer(INDX_S_SB)); 
                else 
                {  
                        long llen = ArithmeticStack.PopElement(); 
						long lstart = ArithmeticStack.PopElement(); 
                        long ldestindx = ArithmeticStack.PopElement(); 
						String str_dest = (String) StringVariable.elementAt((int)ldestindx); 
                        long lsrcindx = ArithmeticStack.PopElement(); 
						String str_src = (String) StringVariable.elementAt((int)lsrcindx);
						
						if (llen<0)
						str_dest = str_src.substring((int)lstart);
						else
						{
							if ((llen+lstart)>str_src.length())
							{
								str_dest = str_src.substring((int)lstart);
							}
							else
							{
								str_dest = str_src.substring((int)lstart,(int)(llen+lstart));
							}
						}

						StringVariable.setElementAt(str_dest,(int)ldestindx); 
						str_src = null;
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S_SB word";
				throw new Exception(e.getMessage());
			}
		}
		
        void SYMBOLLENGTH() throws Exception // S_LN Кладет на АС длину строки, номер при этом не удаляет
        {
            try
            {             
                if (State) Acm.push(new Integer(INDX_S_LN)); 
                else 
                {  
                        long li1 = ArithmeticStack.PopElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        ArithmeticStack.PushElement(s1.length());                         
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S_LN word";
				throw new Exception();
			}
		}

        void LOWERCASE() throws Exception // S_LC Приводит все символы в нижний регистр
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_S_LC)); 
                else 
                {  
                        long li1 = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li1); 
                        s1=s1.toLowerCase();                         
                        StringVariable.setElementAt(s1,(int)li1);                         
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S_LC word";
				throw new Exception();
			}
		}

        void SYMBOLAT() throws Exception // S_AT Кладет на стек код символа из позиции на АС из строки с кодом во втором элементе
        {
          try
          {
                if (State) Acm.push(new Integer(INDX_S_AT)); 
                else 
                {  
                        char lch;
                        long li1 = ArithmeticStack.PopElement(); 
                        long li2 = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li2); 

                        try
                        {
                          lch = s1.charAt((int)li1);                         
                        }
                        catch (StringIndexOutOfBoundsException e) 
                        {
                                ArithmeticStack.PushElement(-1);  
                                return;
                        }

                        ArithmeticStack.PushElement(lch);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in S_AT word";
				throw new Exception();
			}
		}

        void SYMBOLTR() throws Exception // S_TR Убирает незначащие пробелы у строки с индексом на вершине, состояние вершины не изменяется
        {
          try
          {
                if (State) Acm.push(new Integer(INDX_S_TR)); 
                else 
                {  
                        long li = ArithmeticStack.LastElement(); 
                        String s1 = (String) StringVariable.elementAt((int)li);
						s1 = s1.trim();
						StringVariable.setElementAt(s1,(int)li); 
                }
          }
   			catch (Exception e)
			{ 
				LastError = "Error in S_TR word";
				throw new Exception();
			}
		}
		
        void DROPALL() throws Exception // ALLDROP Удаляет все содержимое АС
        {
                if (State) Acm.push(new Integer(INDX_DROPALL)); 
                else 
                 ArithmeticStack.removeAllElements(); 
        } 

        void DROPALLR() throws Exception // ALLDROPR Удаляет все содержимое СВ
        {
                if (State) Acm.push(new Integer(INDX_DROPALLR)); 
                else 
                 ReturnStack.removeAllElements(); 
        } 

        void DROP() throws Exception // DROP Удаляет верхнее значение со стека
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_DROP)); 
                else 
                        ArithmeticStack.PopElement();
            }
   			catch (Exception e)
			{ 
				LastError = "Error in DROP word";
				throw new Exception();
			}
		}

        void DROPR() throws Exception // DROPR Удаляет верхнее значение со стека СВ
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_DROPR)); 
                else 
                        ReturnStack.pop(); 
            }
   			catch (Exception e)
			{ 
				LastError = "Error in DROPR word";
				throw new Exception();
			}
		}

        void DUP() throws Exception // DUP Дублирует верхнее значение на стеке
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_DUP)); 
                else 
                {
                        long tmp_obj = ArithmeticStack.PopElement(); 
                        ArithmeticStack.PushElement(tmp_obj);
						ArithmeticStack.PushElement(tmp_obj);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in DUP word";
				throw new Exception();
			}
		}

        void DUPR() throws Exception // DUPR Дублирует верхнее значение на стеке СВ
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_DUPR)); 
                else 
                {
                        Long tmp_obj = (Long)ReturnStack.peek(); 
                        tmp_obj = new Long(tmp_obj.longValue());  
                        ReturnStack.push(tmp_obj);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in DUPR word";
				throw new Exception();
			}
		}

        void SWAP() throws Exception // SWAP Меняет местами два верхних элемента АС
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_SWAP)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();
                        long obj2 = ArithmeticStack.PopElement();
						ArithmeticStack.PushElement(obj1);
						ArithmeticStack.PushElement(obj2);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in SWAP word";
				throw new Exception();
			}
		}

        void SWAPR() throws Exception // SWAPR Меняет местами два верхних элемента СВ
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_SWAPR)); 
                else 
                {  
                        Object obj1 = ReturnStack.pop();
                        Object obj2 = ReturnStack.pop(); 
                        ReturnStack.push(obj1);
                        ReturnStack.push(obj2);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in SWAPR word";
				throw new Exception();
			}
		}

        void OVER() throws Exception // OVER Дублирует второй элемент стека на вершину
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_OVER)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();
						long obj2 = ArithmeticStack.LastElement();
						ArithmeticStack.PushElement(obj1);
						ArithmeticStack.PushElement(obj2);
				}
           }
   			catch (Exception e)
			{ 
				LastError = "Error in OVER word";
				throw new Exception();
			}
		}

        void ROT() throws Exception // ROT Переносит третий от вершины элемент стека на вершину
        {
            try
            { 
                if (State) Acm.push(new Integer(INDX_ROT)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();
						long obj2 = ArithmeticStack.PopElement();
						long obj3 = ArithmeticStack.PopElement();
						ArithmeticStack.PushElement(obj2);
						ArithmeticStack.PushElement(obj1);
						ArithmeticStack.PushElement(obj3);
				}
             }
   			catch (Exception e)
			{ 
				LastError = "Error in ROT word";
				throw new Exception();
			}
		}

        void ROTR() throws Exception // ROTR Переносит третий от вершины СВ элемент  на вершину
        {
              try
              {
                if (State) Acm.push(new Integer(INDX_ROTR)); 
                else 
                {  
                        Object obj1 = ReturnStack.pop();
						Object obj2 = ReturnStack.pop();
						Object obj3 = ReturnStack.pop();
						ReturnStack.push(obj2);
						ReturnStack.push(obj1);
						ReturnStack.push(obj3);
                }
              }
   			catch (Exception e)
			{ 
				LastError = "Error in ROTR word";
				throw new Exception();
			}
        }

        void TO_R() throws Exception // >R Переносит вершину арифметического стека на вершину стека возвратов
        {
            try
            { 
                if (State) Acm.push(new Integer(INDX_TO_R)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();  
                        ReturnStack.push(new Long(obj1)); 
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in >R word";
				throw new Exception();
			}
		}

        void FROM_R() throws Exception  // R> Переносит вершину стека возвратов на вершину арифметического стека
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_FROM_R)); 
                else 
                {  
                        Long obj1 = (Long)ReturnStack.pop();  
                        ArithmeticStack.PushElement(obj1.longValue()); 
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in R> word";
				throw new Exception();
			}
		}

        void COPY_R() throws Exception // R@ Копирует вершину стека возвратов на вершину арифметического стека
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_COPY_R)); 
                else 
                {  
                        Long obj1 = (Long)ReturnStack.peek();  
                        ArithmeticStack.PushElement(obj1.longValue()); 
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in R@ word";
				throw new Exception();
			}
		}       

        void ADD() throws Exception // + Складывает два верхних элемента арифметического стека
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_ADD)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2+obj1);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in + word";
				throw new Exception();
			}
		}

        void INC() throws Exception // 1+ Увеличивает вершину АС на единицу
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_INC)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj1+1);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in 1+ word";
				throw new Exception();
			}
        }


        void DEC() throws Exception // 1- Уменьшает вершину АС на единицу
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_DEC)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj1-1);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in 1- word";
				throw new Exception();
			}
		}

        void SUB() throws Exception // - Вычитает из второго элемента арифметического стека, значение вершины 
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_SUB)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2-obj1);
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in - word";
				throw new Exception();
			}
		}

        void MUL() throws Exception // * Перемножает два верхних элемента арифметического стека
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_MUL)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2*obj1);
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in * word";
				throw new Exception();
			}
        }

        void AND() throws Exception // AND вершины АС и второго элемента
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_AND)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2&obj1);
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in AND word";
				throw new Exception();
			}
		}

        void OR() throws Exception // OR вершины АС и второго элемента
        {
             try
             {
                if (State) Acm.push(new Integer(INDX_OR)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2|obj1);
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in OR word";
				throw new Exception();
			}
		}
        
        void XOR() throws Exception // XOR вершины АС и второго элемента
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_XOR)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2^obj1);
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in XOR word";
				throw new Exception();
			}
		}

        void NOT() throws Exception // NOT инвертация вершины АС
        {
            try
            {            
                if (State) Acm.push(new Integer(INDX_NOT)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(~obj1);
                }
           }
   			catch (Exception e)
			{ 
				LastError = "Error in NOT word";
				throw new Exception();
			}
		}

        void DIV() throws Exception // / Делит второй элемент арифметического стека на значение вершины
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_DIV)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        ArithmeticStack.PushElement(obj2/obj1);
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in DIV word";
				throw new Exception();
			}
		}        

        void EQU_ZERO() throws Exception // 0= Сравнивает вершину фрифметического стека с нулем
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_EQU_ZERO)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        if (obj1==0) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in 0= word";
				throw new Exception();
			}
		}

        void MORE_ZERO() throws Exception // 0> Проверяет вершину АС на превосходство над нулем
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_MORE_ZERO)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        if (obj1>0) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in 0> word";
				throw new Exception();
			}
		}

        void SMALL_ZERO() throws Exception // 0< Проверяет вершину АС на превосходство над нулем
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_SMALL_ZERO)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        if (obj1<0) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in 0< word";
				throw new Exception();
			}
		}

        void SMALL() throws Exception // < Проверяет второй элемент АС на превосходство над вершиной
        {
            try
            {    
                if (State) Acm.push(new Integer(INDX_SMALL)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        if (obj2<obj1) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in < word";
				throw new Exception();
			}
		}

        void EQU() throws Exception // = Сравнивает вершину АС со вторым элементом
        {
           try
           {
                if (State) Acm.push(new Integer(INDX_EQU)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        if (obj2==obj1) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in = word";
				throw new Exception();
			}
		}

        void MORE() throws Exception // > Проверяет вершину АС на превосходство над вторым элементом
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_MORE)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        if (obj2>obj1) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in > word";
				throw new Exception();
			}
		}

        void NO_EQU() throws Exception // <> Проверяет вершину АС на неравенство со вторым элементом
        {
           try
           {     
                if (State) Acm.push(new Integer(INDX_MORE)); 
                else 
                {  
                        long obj1 = ArithmeticStack.PopElement();                  
                        long obj2 = ArithmeticStack.PopElement();                  
                        if (obj2!=obj1) obj1=-1; else obj1=0;
                        ArithmeticStack.PushElement(obj1);  
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in <> word";
				throw new Exception();
			}
        }

        void MUL_TWO() throws Exception // 2* Сдвиг вершины АС влево на один разряд
        {
             try
             {
                if (State) Acm.push(new Integer(INDX_MULL_TWO)); 
                else 
                {  
                        long lobj1 = ArithmeticStack.PopElement();
                        lobj1=lobj1<<1;  
                        ArithmeticStack.PushElement(lobj1);  
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in 2* word";
				throw new Exception();
			}
		}

        void DIV_TWO() throws Exception // 2/ Сдвиг вершины АС вправо на один разряд
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_DIV_TWO)); 
                else 
                {  
                        long lobj1 = ArithmeticStack.PopElement();
                        lobj1=lobj1>>1;  
                        ArithmeticStack.PushElement(lobj1);  
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in 2/ word";
				throw new Exception();
			}
		}

        void TO_MEM() throws Exception // ! Запись в переменную с номером на вершине АС значения из второго элемента АС 
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_TO_MEM)); 
                else 
                {  
                        long addr = ArithmeticStack.PopElement();
                        long val = ArithmeticStack.PopElement();
                        IntVariable.SetElementAt(val,(int)addr); 
                }
            }
   			catch (Exception e)
			{ 
				LastError = "Error in ! word";
				throw new Exception();
			}
		}

        void FROM_MEM() throws Exception // @ Вывод на вершину АС значения переменной с номером на вершине АС  
        {
            try
            {
                if (State) Acm.push(new Integer(INDX_FROM_MEM)); 
                else 
                {  
                        long addr = ArithmeticStack.PopElement();
                        long var = IntVariable.GetElementAt((int)addr);
                        ArithmeticStack.PushElement(var);   
                }
             }
   			catch (Exception e)
			{ 
				LastError = "Error in @ word";
				throw new Exception();
			}
        }

        void VARIABLE() throws Exception // Variable создает переменную, выделяет ей память. Если арифметический стек не пуст, то переменная инициализируется значением на стеке
        {
                if (State)
				{		
						LastError = "Error: \"Variable\" used only in translate mode";
						throw new Exception();
				}
                int ltt;
                ltt = GetNextToken(fis,false); 
				long lobj = 0; 
				if (ArithmeticStack.GetStackSize()>0) lobj = ArithmeticStack.PopElement();
				switch(ltt)
                {
						case TOKEN_EOF    : { LastError = "Not var name";throw new Exception();};
                        case TOKEN_NUMBER : { LastError = "Bad var name";throw new Exception();};
                        case TOKEN_STRING : { LastError = "Bad var name";throw new Exception();};
                        case TOKEN_WORD : {
                                                svar = svar.toUpperCase(); 
                                                if (GlobalVariableVocabulary.containsKey(svar))
												{	
													LastError = "Duplicate variable '"+svar+"\'"; 
													throw new Exception("Duplicate name"); 
												}
                                                IntVariable.PushElement(lobj);
												GlobalVariableVocabulary.put(svar,new Long(IntVariable.GetStackSize()-1));
                                          }; break;      
                }
        } 

        void NEW_WORD_START() throws Exception // : Начинает описание нового слова
        {
				if (State) {
					LastError = "Error \":\" used only in translate mode";	
					throw new Exception();
				}
				int ltt;
                Object lobj;
                State = true;
                ltt = GetNextToken(fis,false); 
                LEAVE_address = 0;
                switch(ltt)
                {
						case TOKEN_EOF : { LastError = "Error: After \":\" must be word";throw new Exception();};
                        case TOKEN_NUMBER : { LastError = "Error: After \":\" must be word";throw new Exception();};
                        case TOKEN_STRING : { LastError = "Error: After \":\" must be word";throw new Exception();};
                        case TOKEN_WORD : {
                                                svar = svar.toUpperCase(); 
                                                lobj = NameVocabulary.get(svar);
                                                if (lobj!=null) 
												{
													LastError = "Duplicate word";
													throw new Exception(); 
												}
                                                IF_index.removeAllElements();
												TRY_index.removeAllElements();
												EXCEPT_index.removeAllElements(); 
                                                Acm = new Stack();
												Acm.push(new Integer(0)); // Резервируем место для указателя количества локальных переменных 
												NameVocabulary.put(svar,Acm);     
                                          }; break;      
                }
				LocalCount = 0;
        } 

		void NEW_LOCAL_VARIABLE() throws Exception // Local Декларирует новую локальную переменную
		{
			    if (!State) 
				{	
					LastError = "Error: \"Local\" not available in translate mode";
					throw new Exception();
				}	
				int ltt;
                Object lobj;
                State = true;
                ltt = GetNextToken(fis,false); 
                LEAVE_address = 0;
                switch(ltt)
                {
                        case TOKEN_EOF : { LastError = "Error: After \"LOCAL\" must be word";throw new Exception();};
                        case TOKEN_NUMBER : { LastError = "Error: After \"LOCAL\" must be word";throw new Exception();};
                        case TOKEN_STRING : { LastError = "Error: After \"LOCAL\" must be word";throw new Exception();};
                        case TOKEN_WORD : {
											 try
											 {

											    svar = svar.toUpperCase(); 
                                                lobj = LocalVariable.get(svar);
                                                if (lobj!=null) 
												{
													LastError = "Redeclared of local variable "+svar;
													throw new Exception(); 
												}
												LocalCount++;
												Integer lad = new Integer(LocalCount-1);
												LocalVariable.put(svar,lad);  
											 }
											 catch (Exception e)
											 {
												LastError = "Error in LOCAL word";
												throw new Exception();
											 }
										  }; break;      
                }
		}

		void FROM_LOCAL_VARIABLE() throws Exception // L@ AS: laddr -> val Возвращает значение из локальной области памяти с заданным адресом
		{
            try
            {
                if (State) Acm.push(new Integer(INDX_LOCAL_FROM)); 
                else 
                {  
                        long addr = ArithmeticStack.PopElement();
						if (addr<LocalCount)
						{
							ArithmeticStack.PushElement(LocalVariableMemory.PStack[(int)addr]);
						}
						else 
							throw new Exception("Error address of local variable!"); 
				}
            }
   			catch (Exception e)
			{ 
				LastError = "Error in L@ word";
				throw new Exception();
			}
		}

		void TO_LOCAL_VARIABLE() throws Exception // L! AS: value laddr -> Записывает значение в локальную область памяти с заданным адресом
		{
            try
            {
                if (State) Acm.push(new Integer(INDX_LOCAL_TO)); 
                else 
                {  
                        long addr = ArithmeticStack.PopElement();
                        long valr = ArithmeticStack.PopElement();
						if (addr<LocalCount)
						{
							LocalVariableMemory.PStack[(int)addr]=valr;
						}
						else throw new Exception("Error address of local variable!"); 
				}
            }
   			catch (Exception e)
			{ 
				LastError = "Error in L! word";
				throw new Exception();
			}
		}
		
        void NEW_WORD_END() throws Exception // ; Заканчивает описание нового слова
        {
				if (!State) 
				{
					LastError = "Error: \";\" used only in compiled mode";
					throw new Exception();
				}
				else
				{
					try
					{
						State = false;

						if (!TRY_stack.isEmpty()) throw new Exception("A TRY construction are no closed"); 
						if (IF_index.GetStackSize()!=0) throw new Exception("An IF construction are no closed ("+IF_index.GetStackSize()+")"); 
						
						Acm.push(new Integer(INDX_NEXT));  
						ForthStack ll = new ForthStack(LocalCount);
						ll.StackSize = LocalCount; 
						Acm.setElementAt(ll,0);
						LocalCount=0;
						LocalVariable.clear();
					}
					catch(Exception e)
					{
						LastError = "Error in \';\' word ["+e.getMessage()+"]";
						throw new Exception(); 
					}
				}
        }

		void LOAD() throws Exception // LOAD интерпретирует файл с именем на стеке и возвращает управление вызвавшей программе
		{
			if (State) 
			{
				LastError = "Error: LOAD used only in translate mode";
				throw new Exception();
			}
			try
			{
                long li1 = ArithmeticStack.LastElement(); 
                String s1 = (String) StringVariable.elementAt((int)li1); 
				try
				{
					PlayLOAD(s1);
				}
				catch (Exception e)
				{ 
					LastError = "File \'"+s1+"\' ("+LastError+")";
					throw new Exception();
				}
			}
   			catch (Exception e)
			{ 
				LastError = "Error in LOAD word ["+LastError+"]";
				throw new Exception();
			}
		}
		
        void NEXT() throws Exception // NEXT Заканчивает выполнение данного слова и переходит по адресу на СВ
        {
				if (State) Acm.push(new Integer(INDX_NEXT));
				else
				{
					if (!TRY_stack.empty())
					{
						int ltemp = ((Integer)TRY_stack.peek()).intValue()-1;
						TRY_stack.setElementAt(new Integer(ltemp),TRY_stack.size()-1);
					}
					Cur_Word = (Stack) ReturnStack.pop(); // Снимаем указатель на тело слова
					Long lpc_cntr = (Long) ReturnStack.pop(); // Снимаем номер выполняемой команды
					LocalVariableMemory = null;
					if (Cur_Word!=null) LocalVariableMemory = (ForthStack) Cur_Word.elementAt(0);// Снимаем указатель на локальные переменные
					if (LocalVariableMemory!=null) LocalCount=LocalVariableMemory.GetStackSize();
					PC_Counter = lpc_cntr.intValue();
				}
        }

        void FORCE() throws Exception // ? Компилирует слово следующее в потоке если оно есть и пропускает его если его неть в словаре
        {
          if (!State)
		  {
			  LastError = "Word \"?\" not available in translate mode";
			  throw new Exception();
		  }
                Object lobj;
                int ltt = GetNextToken(fis,false); 
				switch(ltt)
                {
				case TOKEN_EOF : {
									LastError = "After \"?\" must be word";
									 throw new Exception();
								 }
				case TOKEN_NUMBER : {
									LastError = "After \"?\" must be word";
									 throw new Exception();
									}
				case TOKEN_STRING : {
									LastError = "After \"?\" must be word";
									 throw new Exception();
									} 
                }
                svar = svar.toUpperCase(); 
                lobj = NameVocabulary.get(svar);
                if (lobj!=null)
                {
                    Acm.push(new Integer(INDX_CALL));
                    Acm.push(lobj);
                }
				else
				{
					lobj = GlobalVariableVocabulary.get(svar);
					if (lobj!=null)
					{
						Acm.push(new Integer(INDX_PUSH));
						Acm.push(lobj);
					}
				}
         }

//------------------------------------------
        void ExecuteTranslateCommand(String cmnd) throws Exception
        {
                cmnd = cmnd.trim().toUpperCase();
                Stack cur_cmnd;                
				try
				{
/*
					if (cmnd.compareTo("%%>")==0) ALLTOSTREAM(); else  
					if (cmnd.compareTo("LOCAL")==0) NEW_LOCAL_VARIABLE(); else  
					if (cmnd.compareTo("L!")==0) TO_LOCAL_VARIABLE(); else
					if (cmnd.compareTo("L@")==0) FROM_LOCAL_VARIABLE(); else
					if (cmnd.compareTo("DATE_BREAK")==0) DATE_BREAK(); else
					if (cmnd.compareTo("DATE_PACK")==0) DATE_PACK(); else
					if (cmnd.compareTo("DATETIME")==0) DATETIME(); else
					if (cmnd.compareTo("TIME_BREAK")==0) TIME_BREAK(); else
					if (cmnd.compareTo("SETHH")==0) TIME_SETHH(); else
					if (cmnd.compareTo("SETMM")==0) TIME_SETMM(); else
					if (cmnd.compareTo("SETSS")==0) TIME_SETSS(); else
					if (cmnd.compareTo("EMIT")==0) STREAM_EMIT(); else
					if (cmnd.compareTo("HEREI")==0) HEREI(); else
					if (cmnd.compareTo("S2HTML")==0) S2HTML(); else
					if (cmnd.compareTo("S2SQL")==0) S2SQL(); else
					if (cmnd.compareTo("I,")==0) IZAPYATAYA(); else
					if (cmnd.compareTo("CHR+")==0)ADD_CHAR2STR(); else
					if (cmnd.compareTo("TKN_SET")==0) TOKEN_SET(); else
					if (cmnd.compareTo("TKN_NXT")==0) TOKEN_NEXT(); else
					if (cmnd.compareTo("TKN_?")==0) TOKEN_MORETOKENS(); else
					if (cmnd.compareTo("WEEKDAY")==0) DATE_WEEKDAY(); else
					if (cmnd.compareTo("DB_CLOSE")==0) SQL_CLOSE(); else
					if (cmnd.compareTo("DB_CONNECT")==0) SQL_CONNECT(); else
					if (cmnd.compareTo("1+!")==0) INCMEM(); else
					if (cmnd.compareTo("DB_EXQ")==0) SQL_EXECUTEQ(); else
					if (cmnd.compareTo("DB_EXU")==0) SQL_EXECUTEU(); else
					if (cmnd.compareTo("DB_GETINT")==0) SQL_GETCOLUMNINT(); else
					if (cmnd.compareTo("DB_GETSTR")==0) SQL_GETCOLUMNSTR(); else
					if (cmnd.compareTo("DB_LOAD")==0) SQL_LOADDRIVER(); else
					if (cmnd.compareTo("DB_NEXT")==0) SQL_NEXT(); else
					if (cmnd.compareTo("S!")==0) TO_STRING(); else
					if (cmnd.compareTo("S_PS")==0) SYMBOL_POS(); else
					if (cmnd.compareTo("CHR")==0) CHR(); else
					if (cmnd.compareTo("S_SB")==0) SUBSTRING(); else
					if (cmnd.compareTo("S_UC")==0) UPPERCASE(); else
					if (cmnd.compareTo("S_LC")==0) LOWERCASE(); else
					if (cmnd.compareTo("S_AT")==0) SYMBOLAT(); else
					if (cmnd.compareTo("S_LN")==0) SYMBOLLENGTH(); else
					if (cmnd.compareTo("S_TR")==0) SYMBOLTR(); else
					if (cmnd.compareTo("LEAVE")==0) LEAVE(); else
					if (cmnd.compareTo("IF")==0) IF(); else
					if (cmnd.compareTo("THEN")==0) THEN(); else
					if (cmnd.compareTo("ELSE")==0) ELSE(); else
					if (cmnd.compareTo(".")==0) STREAM_WRITENUM(); else
					if (cmnd.compareTo("S.")==0) STREAM_WRITESTRING(); else
					if (cmnd.compareTo("S+")==0) S_ADD(); else                
					if (cmnd.compareTo("DROPALL")==0) DROPALL(); else
					if (cmnd.compareTo("DROPALLR")==0) DROPALLR(); else
					if (cmnd.compareTo("DROP")==0) DROP(); else
					if (cmnd.compareTo("DROPR")==0) DROPR(); else
					if (cmnd.compareTo("DUP")==0) DUP(); else
					if (cmnd.compareTo("DUPR")==0) DUPR(); else
					if (cmnd.compareTo("SWAP")==0) SWAP(); else
					if (cmnd.compareTo("OVER")==0) OVER(); else
					if (cmnd.compareTo("ROT")==0) ROT(); else
					if (cmnd.compareTo("ROTR")==0) ROTR(); else
					if (cmnd.compareTo(">R")==0) TO_R(); else
					if (cmnd.compareTo("R>")==0) FROM_R(); else
					if (cmnd.compareTo("R@")==0) COPY_R(); else
					if (cmnd.compareTo("+")==0) ADD(); else
					if (cmnd.compareTo("-")==0) SUB(); else
					if (cmnd.compareTo("*")==0) MUL(); else
					if (cmnd.compareTo("AND")==0) AND(); else
					if (cmnd.compareTo("OR")==0) OR(); else
					if (cmnd.compareTo("XOR")==0) XOR(); else
					if (cmnd.compareTo("NOT")==0) NOT(); else
					if (cmnd.compareTo("/")==0) DIV(); else
					if (cmnd.compareTo("0=")==0) EQU_ZERO(); else
					if (cmnd.compareTo("0>")==0) MORE_ZERO(); else
					if (cmnd.compareTo("0<")==0) SMALL_ZERO(); else
					if (cmnd.compareTo(">")==0) MORE(); else
					if (cmnd.compareTo("<")==0) SMALL(); else
					if (cmnd.compareTo("=")==0) EQU(); else
					if (cmnd.compareTo("2*")==0) MUL_TWO(); else
					if (cmnd.compareTo("2/")==0) DIV_TWO(); else
					if (cmnd.compareTo("!")==0) TO_MEM(); else
					if (cmnd.compareTo("@")==0) FROM_MEM(); else
					if (cmnd.compareTo("VARIABLE")==0) VARIABLE(); else
					if (cmnd.compareTo(":")==0) NEW_WORD_START(); else
					if (cmnd.compareTo(";")==0) NEW_WORD_END(); else
					if (cmnd.compareTo("NEXT")==0) NEW_WORD_END(); else
					if (cmnd.compareTo("DEPTH")==0) DEPTH(); else
					if (cmnd.compareTo("DEPTHR")==0) DEPTHR(); else
					if (cmnd.compareTo("S=")==0) S_EQU(); else
					if (cmnd.compareTo("S==")==0) S_EQUNC(); else
					if (cmnd.compareTo("DO")==0) DO(); else
					if (cmnd.compareTo("LOOP")==0) LOOP(); else
					if (cmnd.compareTo("+LOOP")==0) ADD_LOOP(); else
					if (cmnd.compareTo("I")==0) CYCLE_I(); else
					if (cmnd.compareTo("J")==0) CYCLE_J(); else
					if (cmnd.compareTo("BEGIN")==0) BEGIN(); else
	                if (cmnd.compareTo("SENDHTTPHDR")==0) SENDHTTPHEADER(); else
					if (cmnd.compareTo("UNTIL")==0) UNTIL(); else
					if (cmnd.compareTo("ALLOT")==0) ALLOT(); else
					if (cmnd.compareTo("1+")==0) INC(); else
					if (cmnd.compareTo("1-")==0) DEC(); else
					if (cmnd.compareTo("<>")==0) NO_EQU(); else
					if (cmnd.compareTo("'")==0) FIND(); else
					if (cmnd.compareTo("?")==0) FORCE(); else
					if (cmnd.compareTo("QUIT")==0) QUIT(); else
					if (cmnd.compareTo("S_ENC")==0) URL_ENC(); else
					if (cmnd.compareTo("S_DEC")==0) URL_DEC(); else
					if (cmnd.compareTo("DB_AUTOCOMMIT")==0) SQL_AUTOCOMMIT(); else
					if (cmnd.compareTo("DB_NGETINT")==0) SQL_GETCOLUMNINTFORNAME(); else
					if (cmnd.compareTo("DB_NGETSTR")==0) SQL_GETCOLUMNSTRFORNAME(); else
					if (cmnd.compareTo("DB_COMMIT")==0) SQL_COMMIT(); else
					if (cmnd.compareTo("DB_ROLLBACK")==0) SQL_ROLLBACK(); else
					if (cmnd.compareTo("DB_TRANS")==0) SQL_SETTRANSISOLATION(); else
					if (cmnd.compareTo("DB_NGETBLOB")==0) SQL_GETBLOBSTREAMFORNAME(); else
					if (cmnd.compareTo("DB_GETBLOB")==0) SQL_GETBLOBSTREAMFORNUMBER(); else
					if (cmnd.compareTo("LOAD")==0) LOAD(); else
					if (cmnd.compareTo("FILELIST")==0) FILELIST(); else
					if (cmnd.compareTo("PLAYFILE")==0) PLAYFILE(); else
					if (cmnd.compareTo("I2S")==0) I2S(); else
					if (cmnd.compareTo("I2H")==0) I2H(); else
					if (cmnd.compareTo("S2I")==0) S2I(); else
					if (cmnd.compareTo("SEXECUTE")==0)  SEXECUTE(); else 
					if (cmnd.compareTo("SFIND")==0)  SFIND(); else 
					if (cmnd.compareTo("DB_RSCLOSE")==0) SQL_CLOSERESULTSET(); else
					if (cmnd.compareTo("SETOUTSTREAM")==0) SETOUTSTREAM(); else
					if (cmnd.compareTo("IMG_DRWTXT")==0) IMG_DRAWTEXT(); else
					if (cmnd.compareTo("IMG_CRT")==0) IMG_CREATEIMAGE(); else
					if (cmnd.compareTo("IMG_GPNT")==0) IMG_GETPOINT(); else
					if (cmnd.compareTo("IMG_SPNT")==0) IMG_SETPOINT(); else
					if (cmnd.compareTo("IMG_SPAL")==0) IMG_SETPALETTE(); else
					if (cmnd.compareTo("IMG_DPAL")==0) IMG_DEFAULTPALETTE(); else
					if (cmnd.compareTo("IMG_LINE")==0) IMG_LINE(); else
					if (cmnd.compareTo("IMG_LINEF")==0) IMG_LINEFROM(); else
					if (cmnd.compareTo("IMG_CIRCLE")==0) IMG_CIRCLE(); else
					if (cmnd.compareTo("IMG_FILL")==0) IMG_FILL(); else
					if (cmnd.compareTo("IMG_ELPS")==0) IMG_ELPS(); else
					if (cmnd.compareTo("IMG_RCTNG")==0) IMG_RECTANGLE(); else
					if (cmnd.compareTo("IMG_FRCTNG")==0) IMG_FILLRECTANGLE(); else
					if (cmnd.compareTo("IMG_RRCTNG")==0) IMG_RECTANGLEROUND(); else
					if (cmnd.compareTo("IMG_SETC")==0) IMG_SETCOLOR(); else
					if (cmnd.compareTo("IMG_LDIMG")==0) IMG_LOADIMAGE(); else
					if (cmnd.compareTo("IMG_DRW")==0) IMG_DRAW(); else
					if (cmnd.compareTo("IMG_DRWT")==0) IMG_DRAWTRN(); else
					if (cmnd.compareTo("IMG_SENDGIF")==0) IMG_SENDGIF(); else
					if (cmnd.compareTo("IMG_SENDWBMP")==0) IMG_SENDWBMP(); else
					if (cmnd.compareTo("IMG_LDPAL")==0) IMG_LOADPALETTE(); else
					if (cmnd.compareTo("IMG_SETPS")==0) IMG_SETLINEWIDTH(); else
					if (cmnd.compareTo("IMG_GETMTX")==0) IMG_GETMETRICS(); else
					if (cmnd.compareTo("IMG_SETINDXC")==0) IMG_SETINDXCOLOR(); else
					if (cmnd.compareTo("IMG_GETINDXC")==0) IMG_GETINDXCOLOR(); else
					if (cmnd.compareTo("CLRBUF")==0) CLRBUFFER(); else						
					if (cmnd.compareTo("PRIORITY")==0) PRIORITY(); else						
					if (cmnd.compareTo("DELAY")==0) DELAY(); else						
					if (cmnd.compareTo("RAISE")==0) RAISE(); else
					if (cmnd.compareTo("TRY")==0) TRY(); else
					if (cmnd.compareTo("EXCEPT")==0) EXCEPT(); else
					if (cmnd.compareTo("TRYEND")==0) TRYEND(); else
					if (cmnd.compareTo("OPENSCKT")==0) OPENSCKT(); else
					if (cmnd.compareTo("OPENFILE")==0) OPENFILE(); else
					if (cmnd.compareTo("CLOSEOUTSTREAM")==0) CLOSEOUTSTREAM(); else
					if (cmnd.compareTo("RD_BYTE")==0) READBYTEFROMSTREAM(); else
					if (cmnd.compareTo("RD_STR")==0) READLINEFROMSTREAM(); else
					if (cmnd.compareTo("SETINPSTREAM")==0) SETINPSTREAM(); else
					if (cmnd.compareTo("CLOSEINPSTREAM")==0) CLOSEINPSTREAM(); else
					if (cmnd.compareTo("BUFF2EMAIL")==0) SENDBUFFER2EMAIL(); else
					if (cmnd.compareTo("GV?")==0) SERVERGLOBALCONTAINS(); else
					if (cmnd.compareTo("GVS@")==0) SERVERGLOBALGETSTR(); else
					if (cmnd.compareTo("GVI@")==0) SERVERGLOBALGETINT(); else
					if (cmnd.compareTo("GVS!")==0) SERVERGLOBALSETSTR(); else
					if (cmnd.compareTo("GVI!")==0) SERVERGLOBALSETINT(); else
*/
					
					Method mth = (Method) MainVocabulary.get(cmnd);
					if (mth!=null)
					{
						mth.invoke(this,null); 
					}
					else
					if (cmnd.compareTo("<%%")!=0)
					{
					   Integer loc_wrd;	
					   Long lon_wrd;
					   if(State)
                       {  
						loc_wrd = (Integer)LocalVariable.get(cmnd);
						if (loc_wrd!=null)
						{
							Acm.push(new Integer(INDX_PUSH));
							Acm.push(new Long(loc_wrd.intValue()));
						}
						else
						{
							lon_wrd = (Long)GlobalVariableVocabulary.get(cmnd);
							if (lon_wrd!=null)
							{
								Acm.push(new Integer(INDX_PUSH));
								Acm.push(new Long(lon_wrd.longValue())); 
							}
							else
							{
								cur_cmnd = (Stack)NameVocabulary.get(cmnd);
								if (cur_cmnd==null) 
								{
									LastError = "Unknown word \""+cmnd+"\"";
									throw new Exception(); 
								}
								Acm.push(new Integer(INDX_CALL)); 
								Acm.push(cur_cmnd);
							}
						}
                       }
                       else
                       {
						lon_wrd = (Long)GlobalVariableVocabulary.get(cmnd);
						if (lon_wrd!=null)
						{
							ArithmeticStack.PushElement(lon_wrd.longValue());   	
						}
						else
						{
							cur_cmnd = (Stack)NameVocabulary.get(cmnd);
							if (cur_cmnd==null)
							{
								LastError = "Unknown word \""+cmnd+"\"";
								throw new Exception();
							}
							UserMacroExecute(cur_cmnd);
						}
                       }    
                }
				}
				catch (Exception e)
				{
					throw new Exception(LastError);
				}
        }

        // Выполнение макрослова пользователя
        void UserMacroExecute(Stack body) throws Exception
        {
				ReturnStack.push(new Long(PC_Counter));  
                ReturnStack.push(Cur_Word);  
                PC_Counter = 1;
				Cur_Word = body;
				int lll;
				
				LocalVariableMemory = null;
				if (Cur_Word!=null) LocalVariableMemory = ((ForthStack) Cur_Word.elementAt(0)); 
				if (LocalVariableMemory!=null) LocalCount=LocalVariableMemory.GetStackSize();
				
                while(PC_Counter<Cur_Word.size())
                {                
                        lll = ((Integer)Cur_Word.elementAt(PC_Counter)).intValue();
                        PC_Counter++;
						try
						{
							method_table[lll].invoke(this,null);
						}
						catch (Exception ee)
						{ 
							if (LastError.length()==0) 
							{
								if (mainthread.isAlive())
								{
									LastError = "Error in command code "+String.valueOf(lll)+" call to developer, please! "+ee.getMessage();
									throw new Exception();
								}
							}
							else  
							{
								if (!TRY_stack.empty())
								{
									int lcount = ((Integer)TRY_stack.peek()).intValue();
									while(lcount>0){ ReturnStack.pop();ReturnStack.pop();lcount--;}  
									TRY_stack.setElementAt(new Integer(0),TRY_stack.size()-1); 
									PC_Counter = ((Integer)TRY_stack.elementAt(TRY_stack.size()-2)).intValue();
									Cur_Word = (Stack)TRY_stack.elementAt(TRY_stack.size()-3);
								}
								else
								throw new Exception(LastError);
							}
						}
                        if ((Cur_Word==null)||(IsQuit)) break;  
                }
        } 

        // Считывает из входного потока следующий токен и возвращает его тип, если mode=true то считывает поток побайтно без токенов, а в конце файла возвращает -1
        int GetNextToken(DataInputStream dis,boolean mode) throws Exception 
        {
				if (mode)
				{
					if (dis==null) return -1;
					int bbb = dis.read();
					if (bbb<0) return -1;
					return bbb;
				}
				if (dis==null) return TOKEN_EOF;
                String laccm="";
                boolean is_str=false;
                boolean is_slash=false;
                boolean is_char=false;

                boolean is_comment=false;

                int cur_char;

                while (true)
                {
                       cur_char=dis.read();
                
                       if (cur_char=='\n') StringNumber++;  
                       if (!is_comment) 
                       { 
                                if (cur_char<0) 
                                { 
                                        if (is_str) throw new Exception("Not closed string");  
                                        if (laccm.length()>0)
                                        {
												if (laccm.startsWith("$"))
												{
													try
													{
														laccm = laccm.substring(1); 
														nvar = Long.valueOf(laccm,16).longValue();	
														svar = laccm;
                                                        laccm = "";
                                                        return TOKEN_NUMBER; 
													}
													catch (NumberFormatException e)
													{
														throw new Exception("Bad hexadecimal number");
													}
												}
												else
												{
													try
													{
														nvar = Long.valueOf(laccm).longValue();
														svar = laccm;
                                                        laccm = "";
                                                        return TOKEN_NUMBER; 
													}
													catch (NumberFormatException e)
													{
														svar = laccm;
														laccm = "";
														return TOKEN_WORD; 
													}
												}
                                        } 
                                        return TOKEN_EOF; 
                                }

                                if (is_char)
                                {
                                        if (is_slash)
                                        {
                                                switch (cur_char)
                                                {
                                                        case '\\' : laccm = laccm + '\\'; break;  
                                                        case '\"' : laccm = laccm + '\"'; break;  
                                                        case 'r'  : laccm = laccm + '\r'; break;
                                                        case 'n'  : laccm = laccm + '\n'; break;
                                                        case '\'' : laccm = laccm + '\''; break;
                                                        default : {
                                                                                throw new Exception("Bad symbol after slash at string");  
                                                                        };         
                                                }       
                                                is_slash = false;
                                        }    
                                        else
                                        {
                                                switch(cur_char)
                                                {
                                                        case '\'' : {
                                                                                if (laccm.length()>1) throw new Exception("Too long char string");   
                                                                                if (laccm.length()==0) throw new Exception("Too short char string");    
                                                                                nvar=laccm.charAt(0);
                                                                                is_char = false;                                                          
                                                                                laccm="";
                                                                                return TOKEN_NUMBER; 
                                                                        }; 
                                                        case '\\' : is_slash = true;break;
                                                        default  :  laccm=laccm+(char)cur_char;
                                                }
                                          }    
                                      }
                                      else 
                                       if (is_str)
                                       {
                                                if (is_slash)
                                                {
                                                        switch (cur_char)
                                                        {
                                                                case '\\' : laccm = laccm + '\\'; break;  
                                                                case '\"' : laccm = laccm + '\"'; break;  
                                                                case 'r'  : laccm = laccm + '\r'; break;
                                                                case 'n'  : laccm = laccm + '\n'; break;
                                                                case '\'' : laccm = laccm + '\''; break;
                                                                default : {
                                                                                        throw new Exception("Bad symbol after slash at string");  
                                                                                };         
                                                         }       
                                                is_slash = false;
                                         }    
                                        else
                                        {
                                                switch(cur_char)
                                                {
                                                        case '\"' : {
                                                                                svar=laccm;
                                                                                is_str=false;
                                                                                laccm="";
                                                                                return TOKEN_STRING; 
                                                                        }; 
                                                        case '\\' : is_slash = true;break;
                                                        case '\'' : is_char = true; break;
                                                        default  :  {
                                                                       byte [] barr = {(byte)cur_char};
                                                                       laccm=laccm+new String(barr);
                                                                    }    
                                                      }
                                              }    
                                      }
                                      else
                                      {
                                                if (cur_char<=' ')
                                                {
                                                        if (laccm.length()>0)
                                                        {
															if (laccm.startsWith("$"))
															{
																try
																{
																	laccm = laccm.substring(1); 
																	nvar = Long.valueOf(laccm,16).longValue();	
																	svar = laccm;
																	laccm = "";
																	return TOKEN_NUMBER; 
																}
																catch (NumberFormatException e)
																{
																	throw new Exception("Bad hexadecimal number");
																}
															}
															else
															{
																try
                                                                {
                                                                        nvar = Integer.valueOf(laccm).intValue();
                                                                        svar = laccm;
                                                                        laccm = "";
                                                                        return TOKEN_NUMBER; 
                                                                }
                                                                catch (Exception e)
                                                                {
                                                                        svar = laccm;
                                                                        laccm = "";
                                                                        return TOKEN_WORD; 
                                                                }
															}
                                                        } 
                                                }
                                                else
                                                if (cur_char=='\"') 
                                                {
                                                        if (laccm.length()>0) laccm=laccm+(char)cur_char;
                                                        else is_str=true; 
                                                }
                                                else 
                                                if (cur_char=='\'') 
                                                {
                                                        if (laccm.length()>0) laccm=laccm+(char)cur_char;
                                                        else is_char=true; 
                                                }
                                                else
                                                {
                                                               if ((laccm.length()==0) && (cur_char=='('))
                                                               {
                                                                        is_comment = true;      
                                                               }
                                                               else
                                                               {  
                                                                        laccm=laccm+(char)cur_char;
                                                               }
                                                }
                                         }
                                 }
                        else
                        {
                                if (cur_char==')') is_comment = false;
                        }       
                 }
       }
          

        public void close()
        {
						if (MainVocabulary!=null)
						{
							MainVocabulary.clear();
							MainVocabulary=null;
						}
						if (CurrentWorkSocket!=null)
						{
							try
							{
								CurrentWorkSocket.close();  
							}
							catch(Exception rr){}
							CurrentWorkSocket=null;
						}
					
						if (fis!=null) 
						{	
							try
							{
								fis.close();  
							}
							catch (Exception e){}
							fis=null;
						}
						
						if (DestinationStream!=null) 
						{
							if ((streamdest != 2) && (streamdest != 1)) 
							{	
							 try
							 {
								DestinationStream.close();
							 }
							 catch (Exception e){}
							}
							DestinationStream = null;
						}
						
						if (SourceStream!=null) 
						{
							if (streamsrc != 0)	
							{	
							 try
							 {
								SourceStream.close();
							 }
							 catch (Exception e) {}
							}
						}
						SourceStream = null;

						if (CurrentWorkInputFileStream!=null) 
						{	
							try
							{
								CurrentWorkInputFileStream.close();
							}
							catch (Exception e){}
							CurrentWorkInputFileStream = null;
						}
						
						if (CurrentWorkOutputFileStream!=null)
						{	
							try
							{
								CurrentWorkOutputFileStream.close();
							}
							catch (Exception e){}
							CurrentWorkOutputFileStream = null;							
						}
						
						if (CurrentWorkInputBlobStream!=null) 
						{	
							try
							{
								CurrentWorkInputBlobStream.close();
							}
							catch(Exception ej){}
							CurrentWorkInputBlobStream = null;
						}

				if (FileStreamStack!=null)
				{
					while (!FileStreamStack.empty())
					{
						try
						{
							((ScriptFileData)FileStreamStack.pop()).Close();
						}
						catch(Exception e)
						{}
					}
					FileStreamStack = null;
				}

                if (SQLResultSets!=null)
				{
					for(int li=0;li<SQLResultSets.size();li++)
					{
                        if (SQLResultSets.elementAt(li)!=null)
                        {
                                try
                                {
                                        ((java.sql.ResultSet)SQLResultSets.elementAt(li)).close();   
                                }
                                catch (Exception e)
                                {}
                        } 
					}  
					SQLResultSets.removeAllElements();     
					SQLResultSets = null;
				}

                if (SQLStatements!=null)
				{
					for(int li=0;li<SQLStatements.size();li++)
		            {
                        if (SQLStatements.elementAt(li)!=null)
                        {
                          try
                          {
                              ((java.sql.Statement)SQLStatements.elementAt(li)).close(); 
                          }
                          catch (Exception e) {}                  
                        } 
			        } 
					SQLStatements.removeAllElements();
					SQLStatements = null;
				}

				if (SQLConnections!=null)
				{
					for(int li=0;li<SQLConnections.size();li++)
					{
                        if (SQLConnections.elementAt(li)!=null)
                        {
                          try
                          {
                            ((ScriptForthDBConnection)SQLConnections.elementAt(li)).Close();
                          }
                          catch (Exception e) {}                  
                        } 
					} 
					SQLConnections.removeAllElements();
					SQLConnections = null;
				}

                
                if (IF_index!=null)
				{
					IF_index.removeAllElements();
					IF_index=null;
				}
				
				if (TRY_index!=null)
				{
					TRY_index.removeAllElements();
					TRY_index=null;
				}
				
				if (TRY_stack!=null)
				{
					TRY_stack.removeAllElements();
					TRY_stack=null;
				}

				if (EXCEPT_index != null)
				{
					EXCEPT_index.removeAllElements();
					EXCEPT_index=null;
				}
				
				fis = null;
                DestinationStream=null;          
				SourceStream=null;

				if (Cycle_Index!=null)
				{
					Cycle_Index.removeAllElements();
					Cycle_Index=null;
				}
				
				if (StringVariable!=null)
				{
					StringVariable.removeAllElements();
					StringVariable=null; 
				}
				
				if (IntVariable!=null)
				{	
					IntVariable.removeAllElements();  
					IntVariable = null;
				}

				if (StrTokenizers!=null)
				{
					StrTokenizers.removeAllElements();
					StrTokenizers=null;
				}

				if (ImageStack!=null)
				{
					ImageStack.removeAllElements();
					ImageStack=null;
				}
				
				if (FontStack!=null)
				{
					FontStack.removeAllElements();
					FontStack=null;
				}

				if (PaletteStack!=null)
				{
					PaletteStack.removeAllElements();
					PaletteStack=null;
				}
				
				SMTPClient=null; 

				if (ArithmeticStack!=null)
				{
					ArithmeticStack.removeAllElements(); 
					ArithmeticStack=null;
				}
				
				if (ReturnStack!=null)
				{
					ReturnStack.removeAllElements();
					ReturnStack=null;
				}
				
				if (NameVocabulary!=null)
				{
					NameVocabulary.clear();
					NameVocabulary = null;
				}

				if (GlobalVariableVocabulary!=null)
				{
					GlobalVariableVocabulary.clear();
					GlobalVariableVocabulary=null;
				}
				
				if (LocalVariable!=null)
				{
					LocalVariable.clear();
					LocalVariable=null;	
				}
				LocalVariableMemory = null;
		}
		
}
