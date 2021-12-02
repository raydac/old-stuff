package ScriptForth;

import java.io.*;
import java.util.*;  
import java.sql.*;
import R_HTTPResponse.*;  
import java.net.*; 

public class ScriptForth
{
        final static int TOKEN_STRING = 0;
        final static int TOKEN_WORD = 1;
        final static int TOKEN_NUMBER = 2;
        final static int TOKEN_EOF = 3;

        public int StringNumber = 1; // Номер обрабатываемой строки       

        java.util.Date CurrentDate; //  Объект даты
        public DataInputStream fis ;    // Поток для чтения из шаблона
        public StringBuffer Destination;      // Обработанный шаблон
        public Vector ArithmeticStack;  // Арифметический стек
        public Vector ReturnStack;      // Стек возвратов
        public Vector StringVariable;   // Память для строк
        public Vector IntVariable;      // Память для переменных
        public Vector BodyVocabulary;   // Словарь тел

        public Socket Sys_Socket; // Сокет для которого производится подготовка данных

        Vector Cycle_Index;            // Адрес возврата для циклов
        Vector IF_index;               // Адреса команд IF 
        Vector SQLConnections;         // SQL коннекты 
        Vector SQLStatements;          // SQL запросы
        Vector SQLResultSets;          // Результаты SQL запросов  
        Vector SQLInputStream;         // Потоки из BLOB БД
        int LEAVE_address;             // Адрес команды LEAVE 
        boolean IsQuit;                // Флаг немедленного завершения ФОРТпрограммы

        Hashtable NameVocabulary;       // Словарь заголовков       

        String svar ; // Содержит строковый токен
        int    nvar ; // Содержит числовой токен

        Vector Cur_Word=null ; // Тело выполняющегося слова
        int PC_Counter=0 ;     // Номер команды в данном слове

        char [] lineBuffer = null;

        Vector Acm; // Накопитель компилируемого слова

        StreamTokenizer stream_tokenizer; 

        String Home_Dir; // Директорий, являющийся домашним для текущего скрипта

        Hashtable sys_clients; // Список пользователей Auth сервера

        Enumeration enum_clients; // Последовательность клиентов
        ClientRecord cur_client; // Содержит объект текущего клиента 

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
        final static int INDX_VARIABLE = 32;
        final static int INDX_NEW_WORD_START = 33;
        final static int INDX_NEW_WORD_END = 34;
        final static int INDX_NEXT = 35;
        final static int INDX_PRINT_N = 36;
        final static int INDX_PRINT_S = 37;
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
        final static int INDX_SOCKET_WRITENUM=77;

        final static int INDX_ADDCHAR2STR = 78;
        final static int INDX_ADM_CLIENTNEXT = 79;
        final static int INDX_ADM_CLIENTNAME = 80;
        final static int INDX_ADM_CLIENTPASSWORD = 81;
        final static int INDX_ADM_CLIENTCONNECT = 82;
        final static int INDX_ADM_CLIENTREMOVE = 83;
        final static int INDX_ADM_CLIENTEXISTS = 84;
        final static int INDX_ADM_CLIENTADD = 85;

        final static int INDX_STR_URLENCODE = 86;
        final static int INDX_STR_URLDECODE = 87;

        final static int INDX_SQL_ROLLBACK = 88;
        final static int INDX_SQL_COMMIT = 89;
        final static int INDX_SQL_AUTOCOMMIT = 90;
        final static int INDX_SQL_TRANSISOLATION = 91;                
        final static int INDX_SQL_GETNAMECOLUMNINT = 92;
        final static int INDX_SQL_GETNAMECOLUMNSTR = 93;

        final static int INDX_SYS_QCONNECT = 94;

        final static int INDX_SOCKET_READBYTE = 95;
        final static int INDX_SOCKET_READSTRING = 96;
        final static int INDX_SOCKET_WRITEBYTE = 97;
        final static int INDX_SOCKET_WRITESTRING = 98;

        final static int INDX_SYS_MESSAGE = 99;

        final static int INDX_SQL_NGETBLOB = 100;
        final static int INDX_SQL_GETBLOB =101;
        final static int INDX_SQL_READBLOBBYTE = 102;
        final static int INDX_SQL_CLOSEINPBLOB=103;

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
                return ((Long)(IntVariable.elementAt(indx))).longValue(); 
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
                name=name.toUpperCase(); 
                if (NameVocabulary.containsKey(name)) throw new Exception("Duplicate constant name");  
                Acm = new Vector();
                int lindx = StringVariable.size(); 
                StringVariable.addElement(content);
                Integer li = new Integer(StringVariable.size()-1);   
                Acm.addElement(new Integer(INDX_PUSH));
                Acm.addElement(li);  
                Acm.addElement(new Integer(INDX_NEXT));  
                NameVocabulary.put(name,Acm);
                return lindx;  
        }

        // Производит добавление заданной числовой переменной в словарь
        public int AddNumberValue(String name,long content) throws Exception
        {
           Object lobj =  NameVocabulary.get(name);
           if (lobj!=null) throw new Exception("Duplicate name"); 
           IntVariable.addElement(new Long(content));
           Acm = new Vector();
           Acm.addElement(new Integer(INDX_PUSH));
           Acm.addElement(new Integer(IntVariable.size()-1));
           Acm.addElement(new Integer(INDX_NEXT));    
           NameVocabulary.put(name,Acm);     
           return (IntVariable.size()-1);            
        } 

        // Производит добавление заданной числовой константы в словарь 
        public void AddNumberConstant(String name,int content) throws Exception 
        {
                Acm = new Vector();
                Acm.addElement(new Integer(INDX_PUSH));
                Acm.addElement(new Integer(content));  
                Acm.addElement(new Integer(INDX_NEXT));  
                name=name.toUpperCase(); 
                if (NameVocabulary.containsKey(name)) throw new Exception("Duplicate constant name");  
                NameVocabulary.put(name,Acm);  
        }

        // Функция производит обработку скрипта и возвращает выходную строку
        public StringBuffer PlayScript(String file_name) throws Exception
        {
                fis = new DataInputStream ( new FileInputStream(file_name));

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
                while(fl_end)
                {
                        lll = GetNextToken(fis);

                        switch (lll)
                        {
                                case TOKEN_EOF : fl_end=false; break;
                                case TOKEN_NUMBER  : {
                                                        if (State)
                                                        {
                                                           Acm.addElement(new Integer(INDX_PUSH));
                                                           Acm.addElement(new Integer(nvar));         
                                                        }
                                                        else
                                                        {
                                                           ArithmeticStack.addElement(new Long(nvar));       
                                                        }
                                                     }; break;

                                case TOKEN_STRING  : {
                                                        if (!State)
                                                        {
                                                                StringVariable.addElement(svar);
                                                                ArithmeticStack.addElement(new Long(StringVariable.size()-1));         
                                                        }
                                                        else
                                                        {
                                                                StringVariable.addElement(svar);
                                                                Acm.addElement(new Integer(INDX_PUSH));
                                                                Acm.addElement(new Integer(StringVariable.size()-1));         
                                                        }
                                                     }; break;
                                case TOKEN_WORD    : {
                                                        ExecuteTranslateCommand(svar);
                                                        if (IsQuit) 
                                                        {
                                                           fis.close(); 
                                                           return Destination;
                                                        }        
                                                     }; break; 
                        }
                }
             }
             catch (Exception e)
             {
                  fis.close(); 
                  throw new Exception(e.getMessage());  
             }            
                fis.close(); 
                return Destination;
        } 

        // Конструктор
        public ScriptForth(Socket cl_socket,Hashtable list_clients) throws Exception
        {
                Sys_Socket = cl_socket;
                sys_clients = list_clients;
                IF_index       = new Vector();
                SQLConnections = new Vector();
                SQLResultSets  = new Vector();
                SQLStatements  = new Vector();
                SQLInputStream = new Vector();

                Destination    = new StringBuffer("");  
                ArithmeticStack= new Vector();
                ReturnStack    = new Vector();  
                BodyVocabulary = new Vector();
                NameVocabulary = new Hashtable(); 
                StringVariable = new Vector(); 
                IntVariable    = new Vector();
                Cycle_Index    = new Vector();  
                CurrentDate = new java.util.Date (); 
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
                        raf.writeBytes(Destination.toString()); 
                }
                catch (Exception e)
                {
                        raf.close(); 
                        throw new IOException("Error close file"); 
                }                 
                raf.close(); 
        }


// Процедуры реализующие базовые слова ФОРТа
//------------------------------------------


// Слова ФОРТ для работы с датами и временем
        void TIME_BREAK() throws Exception // TIME_BREAK Разложить число в формат AS: hh mm ss
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_TIME_BREAK));
                        else
                        {
                                Long lstrindx = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                CurrentDate = new java.util.Date(lstrindx.longValue());
                                ArithmeticStack.addElement(new Long(CurrentDate.getHours()));    
                                ArithmeticStack.addElement(new Long(CurrentDate.getMinutes()));    
                                ArithmeticStack.addElement(new Long(CurrentDate.getSeconds()));    
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in TIME_BREAK word");                        
                }
        }

        void DATE_BREAK() throws Exception // DATE_BREAK Разложить число в формат AS: dd  mm yyyy 
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_DATE_BREAK));
                        else
                        {
                                Long lstrindx = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                CurrentDate = new java.util.Date(lstrindx.longValue());
                                ArithmeticStack.addElement(new Long(CurrentDate.getDate()));    
                                ArithmeticStack.addElement(new Long(CurrentDate.getMonth()+1));    
                                ArithmeticStack.addElement(new Long(CurrentDate.getYear()+1900));    
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in DATE_BREAK word");                        
                }
        }

        void DATETIME() throws Exception // DATETIME Кладет на стек текущее время-дату в пакованном формате
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_DATETIME));
                        else
                        {
                                ArithmeticStack.addElement(new Long(System.currentTimeMillis()));            
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in DATETIME word");                        
                }
        }

        void TIME_SETSS() throws Exception // SETSS изменяет количество секунд : AS: int ss -> int
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_SET_SECONDS));
                        else
                        {
                                Long ss = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                long dat = ((Long)ArithmeticStack.lastElement()).longValue();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setSeconds(ss.intValue());       
                                ArithmeticStack.addElement(new Long(CurrentDate.getTime()));  
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in SETSS word");                        
                }
        }

        void TIME_SETMM() throws Exception // SETMM изменяет количество минут : AS: int mm -> int
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_SET_MINUTES));
                        else
                        {
                                Long ss = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                long dat = ((Long)ArithmeticStack.lastElement()).longValue();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setMinutes(ss.intValue());       
                                ArithmeticStack.addElement(new Long(CurrentDate.getTime()));  
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in SETMM word");                        
                }
        }

        void TIME_SETHH() throws Exception // SETHH изменяет количество минут : AS: int hh -> int
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_SET_HOURS));
                        else
                        {
                                Long ss = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                long dat = ((Long)ArithmeticStack.lastElement()).longValue();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                CurrentDate = new java.util.Date(dat);
                                CurrentDate.setHours(ss.intValue());       
                                ArithmeticStack.addElement(new Long(CurrentDate.getTime()));  
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in SETHH word");                        
                }
        }

        void DATE_PACK() throws Exception // DATE_PACK пакует дату : AS: dd mm yyyy -> int
        {
                try
                {
                        if (State) Acm.addElement(new Integer(INDX_DT_DATE_PACK));
                        else
                        {
                                int yyyy = (int)(((Long)ArithmeticStack.lastElement()).longValue()-1900);
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                Long mm = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                                Long dd = (Long)ArithmeticStack.lastElement();
                                ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            

                                CurrentDate = new java.util.Date(yyyy,mm.intValue()-1,dd.intValue());
                                ArithmeticStack.addElement(new Long(CurrentDate.getTime()));  
                        }
                }
                catch (Exception e)
                {
                        throw new Exception("Error in DATE_PACK word");                        
                }
        }

// Слова ФОРТ для работы с клиентским сокетом
        void SOCKET_READBYTE() throws Exception // SCKT_RDB Блокирующее чтение байта из порта, байт кладется на АС
        {
          try
          {            
                if (State) Acm.addElement(new Integer(INDX_SOCKET_READBYTE));
                else
                {
                     int li = Sys_Socket.getInputStream().read(); 
                     ArithmeticStack.addElement(new Long(li));  
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in SCKT_RDB word");
           }          
        }

        void SOCKET_READSTRING() throws Exception // SCKT_RDS Блокирующее чтение строки из порта в ячейку с номером на АС, номер на АС не уничтожается
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_SOCKET_READSTRING));
                else
                {
                        Long lstrindx = (Long)ArithmeticStack.lastElement();
                        if (StringVariable.size()<lstrindx.longValue()) throw new Exception("Bad string index");  
                        String li = readLine(Sys_Socket.getInputStream()); 
                        StringVariable.setElementAt(li,lstrindx.intValue());  
                }
            }  
           catch (Exception e)
           {
                throw new Exception("Error in SCKT_RDS word");
           }          

        }

        void SOCKET_WRITEBYTE() throws Exception // SCKT_WRB Запись байта в порт, байт берется с АС
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SOCKET_WRITEBYTE));
                else
                {
                        Long lstrindx = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                        Sys_Socket.getOutputStream().write((byte)lstrindx.intValue());   
                        Sys_Socket.getOutputStream().flush(); 
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in SCKT_WRB word");
           }          
     
        }

        void SOCKET_WRITESTRING() throws Exception // SS. Запись строки из ячейки с номером на АС
        {
          try
          { 
                if (State) Acm.addElement(new Integer(INDX_SOCKET_WRITESTRING));
                else
                {
                        Long lstrindx = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                        String lstr = (String)StringVariable.elementAt(lstrindx.intValue());
                        byte [] lbyte = lstr.getBytes(); 
                        for (int li=0; li<lbyte.length; li++) 
                        Sys_Socket.getOutputStream().write((byte)lbyte[li]);  
                        Sys_Socket.getOutputStream().flush();
                }
          }
           catch (Exception e)
           {
                throw new Exception("Error in SS. word");
           }          
        }

        void SOCKET_WRITENUM() throws Exception // SN. Запись числа с вершины АС в виде строки
        {
          try
          { 
                if (State) Acm.addElement(new Integer(INDX_SOCKET_WRITENUM));
                else
                {
                        Long lstrindx = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                        String lstr = String.valueOf(lstrindx); 
                        byte [] lbyte = lstr.getBytes(); 
                        for (int li=0; li<lbyte.length; li++) 
                        Sys_Socket.getOutputStream().write((byte)lbyte[li]);  
                        Sys_Socket.getOutputStream().flush();
                }
          }
           catch (Exception e)
           {
                throw new Exception("Error in SN. word");
           }          
        }

// Слова ФОРТ для администрирования системы
        void ADD_CHAR2STR() throws Exception // CHR+ Берет код символа с вершины АС и прбавляет его к строке с индексом во втором элементе
        {
           try
           {      
                if (State) Acm.addElement(new Integer(INDX_ADDCHAR2STR));
                else
                {
                        Long li = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);                                            
                        Long lii= (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-2);
                        String lnew_string = (String)StringVariable.elementAt(lii.intValue());   
                        byte [] lib = new byte[1];
                        lib[0] = li.byteValue(); 
                        lnew_string = lnew_string + new String(lib);  
                        StringVariable.setElementAt(lnew_string,lii.intValue());   
                } 
           }
           catch (Exception e)
           {
                throw new Exception("Error in CHR+ word");
           }          
        }

        void ADMIN_CLIENTNEXT() throws Exception // ADM_CLIENTNXT Перемещает "курсор" на следующего клиента
        {
          try
          {      
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTNEXT));
                else
                {
                        if (enum_clients.hasMoreElements())
                        {
                                cur_client=(ClientRecord)enum_clients.nextElement();   
                                ArithmeticStack.addElement(new Long(-1));
                        }
                        else
                        {
                                ArithmeticStack.addElement(new Long(0));
                        } 
                }
           }     
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTNXT word");
           }          

        }

        void ADMIN_CLIENTNAME() throws Exception // ADM_CLIENTNAME Записывает в строковую ячейку с номером на АС, имя текущего клиента
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTNAME)); 
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        StringVariable.setElementAt(cur_client.name,li_si.intValue());                                
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTNAME word");
           }          
             
        }

        void ADMIN_CLIENTPASSWORD() throws Exception // ADM_CLIENTPSSW Записывает в строковую ячейку с номером на АС, пароль текущего клиента
        {
          try
          {      
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTPASSWORD)); 
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        StringVariable.setElementAt(cur_client.name,li_si.intValue());                                
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTPSSW word");
           }          
     
        }

        void ADMIN_CLIENTCONNECT() throws Exception // ADM_CLIENTCON Кладет на стек допустимое количество подключений клиента 
        {
          try
          {      
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTCONNECT)); 
                else
                {
                        ArithmeticStack.addElement(new Long(cur_client.MaxConnectNumber));  
                }
          }
          catch (Exception e)
          {
                throw new Exception("Error in ADM_CLIENTCON word");
          }          

        }

        void ADMIN_CLIENTREMOVE() throws Exception // ADM_CLIENTREM Удаляет клиента с заданным именем из списка зарегестрированных клиентов
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTREMOVE)); 
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String cl_name = (String)StringVariable.elementAt(li_si.intValue());   
                        sys_clients.remove(cl_name);  
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTREM word");
           }          
 
        }

        void ADMIN_CLIENTADD() throws Exception // ADM_CLIENTADD Добавляет клиента, (АС: name_index,password_index,connect_count -> )
        {
          try
          {            
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTADD)); 
                else
                {
                        Long li_tmp;
                        Long li_connect = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        li_tmp =(Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String cl_password = ((String)StringVariable.elementAt(li_tmp.intValue())).trim();   
                        li_tmp =(Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String cl_name = ((String)StringVariable.elementAt(li_tmp.intValue())).trim().toUpperCase();  
                        if (sys_clients.containsKey(cl_name))
                        {
                                ArithmeticStack.addElement(new Long(0));  
                        }
                        else
                        {
                                sys_clients.put(cl_name,new ClientRecord(cl_name,cl_password,li_connect.intValue()));   
                                ArithmeticStack.addElement(new Long(-1));  
                        }   
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTADD word");
           }          

        }

        void ADMIN_CLIENTEXIST() throws Exception // ADM_CLIENTEXT Проверяет наличие клиента в списке клиентов
        {
          try
          {      
                if (State) Acm.addElement(new Integer(INDX_ADM_CLIENTEXISTS));
                else
                {
                        Long li_tmp =(Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String cl_name = ((String)StringVariable.elementAt(li_tmp.intValue())).trim().toUpperCase();   
                        if (sys_clients.containsKey(cl_name))
                        {
                                cur_client = (ClientRecord) sys_clients.get(cl_name);  
                                ArithmeticStack.addElement(new Long(-1)); 
                        }
                        else
                        {
                                ArithmeticStack.addElement(new Long(0));
                        }  
                }
             }
           catch (Exception e)
           {
                throw new Exception("Error in ADM_CLIENTEXT word");
           }          

        }        

        // Слова ФОРТ для работы с БД

        void SQL_COMMIT() throws Exception // DB_COMMIT Завершение транзакции для Connect c индексом на АС (AS: indx ->)
        {
          try
          {      
                if (State) Acm.addElement(new Integer(INDX_SQL_COMMIT));
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Connection cur_con = (java.sql.Connection) SQLConnections.elementAt(li_si.intValue());   
                        cur_con.commit(); 
                }  
          }
          catch (Exception e)
           {
                throw new Exception("Error in DB_COMMIT word");
           }          
      
        }

        void SQL_ROLLBACK() throws Exception // DB_ROLLBACK Откат транзакции
        {
           try
           {      
                if (State) Acm.addElement(new Integer(INDX_SQL_ROLLBACK));
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Connection cur_con = (java.sql.Connection) SQLConnections.elementAt(li_si.intValue());   
                        cur_con.rollback(); 
                }  
           }
           catch (Exception e)
           {
                throw new Exception("Error in DB_ROLLBACK word");
           }          
     
        }

        void SQL_AUTOCOMMIT() throws Exception // DB_AUTOCOMMIT Режим автотранзакции (AS: connect_indx int_mode -> )  
        {
           try
           { 
                if (State) Acm.addElement(new Integer(INDX_SQL_AUTOCOMMIT)); 
                else
                {
                        Long isolmode = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long conindx = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Connection cur_con = (java.sql.Connection) SQLConnections.elementAt(conindx.intValue());                                           
                        if (isolmode.intValue()==0) cur_con.setAutoCommit(false); else cur_con.setAutoCommit(true);  
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in DB_AUTOCOMMIT word ["+e.getMessage()+"]");
           }          
        }
        
        void SQL_SETTRANSISOLATION() throws Exception// DB_TRANS Задает режим изоляции транзакции (AS: connect_indx int ->)
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SQL_TRANSISOLATION)); 
                else
                {
                        Long isolmode = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long conindx = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Connection cur_con = (java.sql.Connection) SQLConnections.elementAt(conindx.intValue());                                           

                        switch (isolmode.intValue()) 
                        {
                                case 0 : cur_con.setTransactionIsolation(cur_con.TRANSACTION_READ_UNCOMMITTED); break;
                                case 1 : cur_con.setTransactionIsolation(cur_con.TRANSACTION_READ_COMMITTED); break;
                                case 2 : cur_con.setTransactionIsolation(cur_con.TRANSACTION_REPEATABLE_READ); break;
                                case 3 : cur_con.setTransactionIsolation(cur_con.TRANSACTION_SERIALIZABLE); break;
                                case 4 : cur_con.setTransactionIsolation(cur_con.TRANSACTION_NONE); break;
                                default : throw new Exception("Error mode for DB_TRANSISOLATION");  
                        }
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in DB_TRANSISOLATION word ["+e.getMessage()+"]");
           }          
        }

        void SQL_GETBLOBSTREAMFORNAME() throws Exception // DB_NGETBLOB берет имя столбца с АС, индекс ResultSet из второго элемента, формирует входной поток и кладет индекс потока на АС 
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_SQL_NGETBLOB));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lcolname = (String) StringVariable.elementAt(li_ci.intValue());
   
                        li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.ResultSet lrs = (java.sql.ResultSet) SQLResultSets.elementAt(li_ci.intValue());   
                                
                        InputStream lis = lrs.getBinaryStream(lcolname);
                        ArithmeticStack.addElement(new Long(SQLInputStream.size()));   
                        SQLInputStream.addElement(lis);   
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in DB_NGETBLOB word");
           }          
        }        

        void SQL_GETBLOBSTREAMFORNUMBER() throws Exception // DB_GETBLOB берет номер столбца с АС, индекс ResultSet из второго элемента, формирует входной поток и кладет индекс потока на АС 
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_SQL_GETBLOB));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lcolname = (String) StringVariable.elementAt(li_ci.intValue());
   
                        li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.ResultSet lrs = (java.sql.ResultSet) SQLResultSets.elementAt(li_ci.intValue());   
                                
                        InputStream lis = lrs.getBinaryStream(lcolname);
                        ArithmeticStack.addElement(new Long(SQLInputStream.size()));   
                        SQLInputStream.addElement(lis);   
                }
           }
          catch (Exception e)
           {
                throw new Exception("Error in DB_GETBLOB word");
           }          

        }        

        void SQL_READBLOBBYTE() throws Exception // DB_RDBLOB берет номер входного потока с АС, считывает байт на АС, если данных нет, то на АС -1
        {
           try
            {    
                if (State) Acm.addElement(new Integer(INDX_SQL_READBLOBBYTE));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        InputStream lis = (InputStream) SQLInputStream.elementAt(li_ci.intValue());
                        int l_ci = lis.read();
                        ArithmeticStack.addElement(new Long(l_ci));   
                }
            }
            catch (Exception e)
           {
                throw new Exception("Error in DB_RDBLOB word");
           }          
    
        }

        void SQL_CLOSEINPBLOB() throws Exception // DB_CLOSEINBLOB Закрывает поток считывания из базы с индексом на АС
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SQL_CLOSEINPBLOB));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        ((InputStream)SQLInputStream.elementAt(li_ci.intValue())).close();         
                }
            }
            catch (Exception e)
           {
                throw new Exception("Error in DB_CLOSEINBLOB word");
           }          
    
        }

        void SQL_GETCOLUMNINTFORNAME() throws Exception // DB_NGETINT то же, что и DB_GETINT, вместо номера используется имя столбца
        {
          String nmcol=null; 
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SQL_GETNAMECOLUMNINT));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_rs = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  

                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt(li_rs.intValue());
                        if (my_rs==null) throw new Exception("Error");
                        nmcol = (String)StringVariable.elementAt(li_ci.intValue());
                        long ff=my_rs.getLong (nmcol);
                        ArithmeticStack.addElement(new Long(ff));  
                }
           }
           catch (Exception e)
           {
              try  
              {  
                nmcol = "Error in DB_NGETINT word, column name is \'"+nmcol+"\' ";
                throw new Exception(nmcol);
              }  
              catch (Exception ee)
              {  
                throw new Exception("Error in DB_NGETINT word ");
              }    
           }          

        }

        void SQL_GETCOLUMNSTRFORNAME() throws Exception // DB_NGETSTR то же, что и DB_GETSTR, но вместо номера используется имя столбца
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SQL_GETNAMECOLUMNSTR));
                else
                {
                        byte[] larr;
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_rs = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  

                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt(li_rs.intValue());
                        if (my_rs==null) throw new Exception("Error");

                        String tt = (String) StringVariable.elementAt(li_ci.intValue());
                        larr = my_rs.getBytes(tt);
                        String ff = new String(larr);
                        StringVariable.setElementAt(ff,li_si.intValue());                                
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in DB_NGETSTR word ["+e.getMessage()+"]");
           }          
     
        }

        void SQL_GETCOLUMNSTR() throws Exception // DB_GETSTR Получить значение столбца с номером во втором эл-те, у текущей записи ResultSet'a в третьем эл-те 
                                                 // в виде строки и положить эту строку в ячейку с индексом на вершине АС
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_SQL_GETCOLUMNSTR));
                else
                {
                        Long li_si = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_rs = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  

                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt(li_rs.intValue());
                        if (my_rs==null) throw new Exception("Error");
                        String ff=my_rs.getString(li_ci.intValue());
                        StringVariable.setElementAt(ff,li_si.intValue());                                
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in DB_GETSTR word");
           }          
        }

        void SQL_GETCOLUMNINT() throws Exception // DB_GETINT Получить значение столбца с номером на АС у текущей записи ResultSet'a во втором эл-те 
                                                 // в виде целого числа и положить это число на вершину
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_SQL_GETCOLUMNINT));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long li_rs = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  

                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt(li_rs.intValue());
                        if (my_rs==null) throw new Exception("Error");
                        long
 ff=my_rs.getLong(li_ci.intValue());
                        ArithmeticStack.addElement(new Long(ff));  
                }
             }
             catch (Exception e)
           {
                throw new Exception("Error in DB_GETINT word");
           }          
   
        }

        void SQL_NEXT() throws Exception // DB_NEXT Переходит к следующей записи у ResultSet с индексом на вершине, если нет больше записей, то false иначе true
        {
            try
            {     
                if (State) Acm.addElement(new Integer(INDX_SQL_NEXT));
                else
                {
                        Long li_ci = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.ResultSet my_rs = (java.sql.ResultSet) SQLResultSets.elementAt(li_ci.intValue());
                        if (my_rs==null) throw new Exception("Error");
                        boolean ff = my_rs.next(); 
                        if (ff) ArithmeticStack.addElement(new Long(-1));
                        else 
                        {                                
                           ArithmeticStack.addElement(new Long(0));
                           my_rs.close();
                           my_rs = null;           
                        }
                }
             }
             catch (Exception e)
             {
                throw new Exception("Error in DB_NEXT word ["+e.getMessage()+"]");
             }          
        }

        void SQL_EXECUTEQ() throws Exception // DB_EXQ отправляет строку SQL серверу БД, на АС кладется индекс SQLResultSets
                                               // Вторым элементом является индекс в SQLConnect 
        {
            try
            {        
                if (State) Acm.addElement(new Integer(INDX_SQL_EXECUTEQ));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lss = (String) StringVariable.elementAt(lid.intValue()); 
                        lid = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Statement my_statement = ((java.sql.Connection) SQLConnections.elementAt(lid.intValue())).createStatement();   
                        SQLStatements.addElement(my_statement); 
                        if (my_statement==null) throw new Exception("Error");
                        java.sql.ResultSet new_rs=my_statement.executeQuery(lss); 
                        if (new_rs==null) throw new Exception("Error");
                        ArithmeticStack.addElement(new Long(SQLResultSets.size()));
                        SQLResultSets.addElement(new_rs); 
                }  
             }
           catch (Exception e)
           {
               throw new Exception("Error in DB_EXQ word ["+e.getMessage()+"]");
           }          

        }

        void SQL_EXECUTEU() throws Exception // DB_EXU отправляет строку SQL серверу БД, на АС кладется значение результата
                                               // Вторым элементом является индекс в SQLStatements 
        {
            try
            {     
                if (State) Acm.addElement(new Integer(INDX_SQL_EXECUTEU));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lss = (String) StringVariable.elementAt(lid.intValue()); 
                        lid = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Statement my_statement = ((java.sql.Connection) SQLConnections.elementAt(lid.intValue())).createStatement();   
                        SQLStatements.addElement(my_statement); 
                        if (my_statement==null) throw new Exception("Error");
                        int ll = my_statement.executeUpdate(lss); 
                        ArithmeticStack.addElement(new Long(ll));
                }  
            }
           catch (Exception e)
           {
                throw new Exception("Error in DB_EXU word ["+e.getMessage()+"]");
           }          
         }

        void SQL_CLOSE() throws Exception // DB_CLOSE закрывает соединение с индексом на АС
        {
            try
            {        
                if (State) Acm.addElement(new Integer(INDX_SQL_CLOSE));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        java.sql.Connection my_connection = (java.sql.Connection) SQLConnections.elementAt(lid.intValue());                          
                        if (my_connection==null) throw new Exception("Error");
                        if (!my_connection.isClosed()) my_connection.close();  
                        my_connection = null;
                        
                } 
            }
           catch (Exception e)
           {
                throw new Exception("Error in DB_CLOSE word");
           }          
        }

        void SQL_LOADDRIVER() throws Exception // DB_LOAD Загружает драйвер базы данных, если успешно, то true  иначе false
        {
             try
             {   
                if (State) Acm.addElement(new Integer(INDX_SQL_LOADDRIVER));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lss = (String) StringVariable.elementAt(lid.intValue()); 
                        try
                        {
                                Class.forName(lss);
                                ArithmeticStack.addElement(new Long(-1));
                        }
                        catch (Exception e)
                        {
                                ArithmeticStack.addElement(new Long(0));
                        }
                }  
             }
           catch (Exception e)
           {
                throw new Exception("Error in DB_LOAD word");
           }          
   
        }

        void INCMEM() throws Exception // 1+! Инкремент ячейки памяти с номером на АС 
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_INCMEM ));
                else
                {
                        long jj;
                        Long var;
                        Long addr = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        jj =((Long) IntVariable.elementAt(addr.intValue())).longValue()+1 ; 
                        var = new Long(jj); 
                        IntVariable.setElementAt(var,addr.intValue());                        
                }  
           }
        catch (Exception e)
           {
                throw new Exception("Error in 1+!  word");
           }          
        }

        void SQL_CONNECT() throws Exception // DB_CONNECT Производит коннект к заданной базе данных, на вершине АС лежит индекс пароля, 
                                            //  во втором эл-те индекс имени пользователя, в 3-м URL базы данных
                                            // если успешно, то индекс, иначе -1    
        {
            try
            {        
                if (State) Acm.addElement(new Integer(INDX_SQL_CONNECT));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lsp = (String) StringVariable.elementAt(lid.intValue()); 
                        lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lsn = (String) StringVariable.elementAt(lid.intValue()); 
                        lid = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lsu = (String) StringVariable.elementAt(lid.intValue()); 

                        Properties lprop = new Properties();
                        lprop.put("user",lsn);lprop.put("password",lsp); 

        
                        java.sql.Connection new_connect = DriverManager.getConnection(lsu,lprop);  
                        ArithmeticStack.addElement(new Long(SQLConnections.size())); 
                        SQLConnections.addElement(new_connect);  
                }  
             }
           catch (Exception e)
           {
                throw new Exception("Error in DB_CONNECT word ["+e.getMessage()+"]");
           }          

        }

        void SYS_MESSAGE() throws Exception // MSG. выводит на консоль строку с индексом на АС
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_SYS_MESSAGE));
                else
                {
                        Long lid = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lss = (String) StringVariable.elementAt(lid.intValue()); 
                        System.out.println(lss);  
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in MSG. word");
           }          
     
        }

        void QCONNECT() throws Exception //?CONNECT проверяет состояние порта, если поддерживается, то true иначе false
        {
            try
            {        
                OutputStream os;
                if (State) Acm.addElement(new Integer(INDX_SYS_QCONNECT));
                else
                {
                   try
                   {          
                        os = Sys_Socket.getOutputStream(); 
                        if (os!=null) ArithmeticStack.addElement(new Long(-1)); 
                        else 
                        {
                                ArithmeticStack.addElement(new Long(0));    
                                System.out.println("Socket unavailable"); 
                        }
                        os = null;
                   }
                   catch (Exception e)
                   {
                        ArithmeticStack.addElement(new Long(0));    
                        System.out.println("Socket unavailable");
                        os = null;
                   }     
                }  
            }
        catch (Exception e)
           {
                throw new Exception("Error in ?CONNECT word");
           }          
        }

        void URL_ENC() throws Exception // S_ENC кодирует строку в формат URL , строка в ячейкес индексом на вершине АС
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_STR_URLENCODE));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        String lis = (String)StringVariable.elementAt(lid.intValue());
                        lis = EncodeString(lis);    
                        StringVariable.setElementAt(new String(lis),lid.intValue());  
                }  
            }
           catch (Exception e)
           {
                throw new Exception("Error in S_ENC word");
           }          
        }

        void URL_DEC() throws Exception // S_DEC декодирует строку из формата URL , строка в ячейке с индексом на вершине АС
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_STR_URLDECODE));
                else
                {
                        Long lid = (Long) ArithmeticStack.lastElement();
                        String lis = (String)StringVariable.elementAt(lid.intValue());
                        lis = DecodeString(lis);    
                        StringVariable.setElementAt(new String(lis),lid.intValue());  
                }  
            }
           catch (Exception e)
           {
                throw new Exception("Error in S_DEC word");
           }          
        }

        void I2S() throws Exception // i2s переводит числовое значение в строку и записывает её по адресу на вершине (AS: int str_indx ->)
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_I2S));                
                else
                {
                        Long lid = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long lis = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        StringVariable.setElementAt(String.valueOf(lis),lid.intValue());  
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in I2S word");
           }          
        }

        void S2I() throws Exception // s2i переводит строку в числовое значение (AS: str_indx -> int)
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_S2I));                
                else
                {
                        Long lid = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lls = (String)StringVariable.elementAt(lid.intValue());
                        Long lis = Long.valueOf(lls);
                        ArithmeticStack.addElement(lis);  
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in S2I word");
           }          
        }

        void TO_STRING() throws Exception // S! Копирует строку из элемента с индексом во втором элементе в элемент с индексом на вершине АС
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_TO_STRING));                
                else
                {
                        Long lid = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long lis = (Long)ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        String lss = (String) StringVariable.elementAt(lis.intValue()); 
                        StringVariable.setElementAt(new String(lss),lid.intValue());  
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in S! word");
           }          
        }

        void LEAVE() throws Exception // LEAVE Обеспечивает немедленный выход из цикла DO-LOOP или BEGIN-UNTIL
        {
            try
            {        
                if (State)
                {
                        Acm.addElement(new Integer(INDX_LEAVE));                
                        LEAVE_address = Acm.size(); 
                        Acm.addElement(new Integer(0));                
                }
                else
                {
                        ReturnStack.removeElementAt(ReturnStack.size()-1);      
                        ReturnStack.removeElementAt(ReturnStack.size()-1);      
                        Cycle_Index.removeElementAt(Cycle_Index.size()-1);  

                        Integer li=(Integer)Cur_Word.elementAt(PC_Counter); 
                        PC_Counter=li.intValue(); 
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in LEAVE word");
           }          
        }

        void QUIT() throws Exception // Обеспечивает немедленный выход из программы
        {
                if (State)
                {
                        Acm.addElement(new Integer(INDX_QUIT));                
                }
                else
                {
                        IsQuit = true;
                }
        }

        void FIND() throws Exception // Ищет в словаре слово с именем из следующего слова, если есть то кладет на АС true иначе false
        {
                Object lobj;
                int ltt = GetNextToken(fis); 
                switch(ltt)
                {
                  case TOKEN_EOF : throw new Exception("Error: After \"'\" must be word");
                  case TOKEN_NUMBER : throw new Exception("Error: After \"'\" must be word");
                  case TOKEN_STRING : throw new Exception("Error: After \"'\" must be word");
                }
                svar = svar.toUpperCase(); 
                lobj = NameVocabulary.get(svar);
                if (State) 
                {
                   Acm.addElement(new Integer(INDX_PUSH));
                   if (lobj!=null) 
                   Acm.addElement(new Integer(-1));
                   else Acm.addElement(new Integer(0));     
                }
                else
                {
                   if (lobj!=null) ArithmeticStack.addElement(new Long(-1));
                   else ArithmeticStack.addElement(new Long(0));     
                }
        }

        void ALLOT() throws Exception // Выделяет память (инициализируя нулем) в области переменных и кладет адрес её начала на АС
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_ALLOT));
                else
                {
                        Long li = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long cur_indx=new Long(IntVariable.size());  
                        for(int ll=0;ll<li.intValue();ll++)
                        {
                                IntVariable.addElement(new Long(0));  
                        }  
                        ArithmeticStack.addElement(cur_indx);  
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in ALLOT word");
           }          
        }

        void DEPTH() throws Exception // Кладет на АС количество элементов до выполнение команды
        {
                if (State) Acm.addElement(new Integer(INDX_DEPTH));
                else
                {
                        Long li = new Long((ArithmeticStack.size()));
                        ArithmeticStack.addElement(li);  
                          
                }
        }

        void PLAYFILE() throws Exception // Передает управление файлу с именем на АС
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_PLAYFILE));
                else
                {
                        Long li = (Long)ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  

                        String lstr = Home_Dir+(String) StringVariable.elementAt(li.intValue());
                        fis.close();
                        StringBuffer tmp_dest= Destination;
                        Destination = new StringBuffer("");
                        Destination = PlayScript(lstr);
                         tmp_dest.append(Destination);  
                        IsQuit = true;
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in PLAYFILE word");
           }                    
        }

        void DEPTHR() throws Exception // Кладет на АС количество элементов СВ 
        {
                if (State) Acm.addElement(new Integer(INDX_DEPTHR));
                else
                {
                        Long li = new Long((ReturnStack.size()));
                        ArithmeticStack.addElement(li);  
                }
        }

        void CALL() throws Exception // Передает управление слову с номером за командой
        {
                if (State) Acm.addElement(new Integer(INDX_CALL));
                else
                {
                        Vector li;
                        li = (Vector) (Cur_Word.elementAt(PC_Counter));
                        PC_Counter++;
                        ReturnStack.addElement(new Long(PC_Counter));
                        ReturnStack.addElement(Cur_Word);
                        PC_Counter=0;
                        Cur_Word = li; 
                }
        } 

        void CYCLE_I() throws Exception //
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_CYCLE_I)); else 
                {
                        Long li1 = (Long) ReturnStack.lastElement(); 
                        ArithmeticStack.addElement(new Long(li1.longValue()));  
                }  
           }
           catch (Exception e)
           {
                throw new Exception("Error in I word");
           }          
        }

        void CYCLE_J() throws Exception //
        {
           try
           {                  
                if (State) Acm.addElement(new Integer(INDX_CYCLE_J)); else 
                {
                        Long li1 = (Long) ReturnStack.elementAt(ReturnStack.size()-3); 
                        ArithmeticStack.addElement(new Long(li1.longValue()));  
                }  
           }
           catch (Exception e)
           {
                throw new Exception("Error in J word");
           }          
        }

        void IF() throws Exception // Оператор IF
        {
                if (State) 
                {
                        Acm.addElement(new Integer(INDX_IF));
                        IF_index.addElement(new Integer(Acm.size()));
                        Acm.addElement(new Integer(0));
                }               
                else
                {
                        Long li = (Long) ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (li.longValue()==0)
                        {
                           Integer lii = (Integer) Cur_Word.elementAt(PC_Counter);
                           if (lii.intValue()==0) throw new Exception("Not closed IF structure");     
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
                Integer li;
                if (State) 
                {
                   try
                   {          
                        li=(Integer)IF_index.lastElement();
                        IF_index.removeElementAt(IF_index.size()-1); 
                        Acm.setElementAt(new Integer(Acm.size()),li.intValue());      
                   }     
                   catch (Exception e)
                   {
                      throw new Exception("Error in THEN word");
                   }          
                }               
                else
                {
                        throw new Exception ("Use THEN in translate mode");                        
                }
        }

        void ELSE() throws Exception // Оператор ELSE
        {
                Integer li;
                if (State) 
                {
                        li=(Integer)IF_index.lastElement();
                        IF_index.removeElementAt(IF_index.size()-1); 
                        Acm.addElement(new Integer(INDX_ELSE));
                        IF_index.addElement(new Integer(Acm.size()));
                        Acm.addElement(new Integer(0));
                        Acm.setElementAt(new Integer(Acm.size()),li.intValue());      
                }               
                else
                {
                        li=(Integer)Cur_Word.elementAt(PC_Counter); 
                        PC_Counter=li.intValue(); 
                }
        } 

        void DO() throws Exception // Начинает цикл
        {
           try
           {          
                if (State) Acm.addElement(new Integer(INDX_DO)); else 
                {
                   Long li1 = (Long) ArithmeticStack.lastElement();
                   ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);            
                   Long li2 = (Long) ArithmeticStack.lastElement();
                   ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                   ReturnStack.addElement(li2);
                   ReturnStack.addElement(li1);                  
                   Cycle_Index.addElement(new Integer(PC_Counter));  
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in DO word");
           }          
        }

        void BEGIN() throws Exception // Начинает цикл BEGIN-UNTIL
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_BEGIN)); else 
                {
                   Cycle_Index.addElement(new Integer(PC_Counter));  
                   ReturnStack.addElement(new Long(0));      
                   ReturnStack.addElement(new Long(0));
                }
             }
           catch (Exception e)
           {
                throw new Exception("Error in BEGIN word");
           }          
        }

        void LOOP() throws Exception // Заканчивает цикл
        {
          try
          {            
                if (State) 
                {
                   Acm.addElement(new Integer(INDX_LOOP)); 
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else 
                {
                   Long li1 = (Long) ReturnStack.lastElement();
                   ReturnStack.removeElementAt(ReturnStack.size()-1);      
                   Long li2 = (Long) ReturnStack.lastElement();       
                   li1 = new Long(li1.intValue()+1);      
                   if (li1.longValue()>li2.longValue())  
                   {
                        ReturnStack.removeElementAt(ReturnStack.size()-1);      
                        Cycle_Index.removeElementAt(Cycle_Index.size()-1);  
                   }      
                   else 
                   {
                        PC_Counter = ((Integer)Cycle_Index.lastElement()).intValue(); 
                        ReturnStack.addElement(li1); 
                   }          
                }  
           }
        catch (Exception e)
           {
                throw new Exception("Error in LOOP word");
           }          
        }

        void ADD_LOOP() throws Exception // Заканчивает цикл c приращением на АС
        {
            try
            {    
                if (State)
                {
                   Acm.addElement(new Integer(INDX_ADD_LOOP)); 
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else
                {
                   Long li1 = (Long) ReturnStack.lastElement();
                   ReturnStack.removeElementAt(ReturnStack.size()-1);      
                   Long li2 = (Long) ReturnStack.lastElement();       
                   Long li3 = (Long) ArithmeticStack.lastElement();  
                   ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);           
                   li1 = new Long(li1.longValue()+li3.longValue());      

                   if (li3.longValue()<0)
                   {
                        if (li1.longValue()<li2.longValue())  
                        {
                                ReturnStack.removeElementAt(ReturnStack.size()-1);      
                                Cycle_Index.removeElementAt(Cycle_Index.size()-1);  
                        }      
                        else 
                        {
                                PC_Counter = ((Integer)Cycle_Index.lastElement()).intValue(); 
                                ReturnStack.addElement(li1); 
                        }          
                   }  
                   else
                   {          
                        if (li1.longValue()>li2.longValue())  
                        {
                                ReturnStack.removeElementAt(ReturnStack.size()-1);      
                                Cycle_Index.removeElementAt(Cycle_Index.size()-1);  
                        }      
                        else 
                        {
                                PC_Counter = ((Integer)Cycle_Index.lastElement()).intValue(); 
                                ReturnStack.addElement(li1); 
                        }          
                   }  
               } 
           }
        catch (Exception e)
           {
                throw new Exception("Error in +LOOP word");
           }          
        }


        void UNTIL() throws Exception // Конец цикла BEGIN-UNTIL
        {
            try
            {     
                if (State)
                {
                   Acm.addElement(new Integer(INDX_UNTIL));
                   if (LEAVE_address>0)
                   {
                        Acm.setElementAt(new Integer(Acm.size()),LEAVE_address);  
                        LEAVE_address=0;
                   }                
                }
                else
                {
                   Long li3 = (Long) ArithmeticStack.lastElement();  
                   ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);           
                   if (li3.longValue()==0)     
                   {
                       PC_Counter = ((Integer)Cycle_Index.lastElement()).intValue(); 
                   } 
                   else
                   {
                       Cycle_Index.removeElementAt(Cycle_Index.size()-1);  
                       ReturnStack.removeElementAt(ReturnStack.size()-1);                              
                       ReturnStack.removeElementAt(ReturnStack.size()-1);      
                   }          
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in UNTIL word");
           }          
        }

        void PUSH() throws Exception // Кладет на стек, следующее за ним слово
        {
                if (State) Acm.addElement(new Integer(INDX_PUSH)); 
                else 
                {  
                        Integer li;
                        li = (Integer) (Cur_Word.elementAt(PC_Counter));
                        PC_Counter++;
                        ArithmeticStack.addElement(new Long(li.intValue()));                   
                }
        }        

        void S_EQU() throws Exception // S= Сравнивает две строки, с адресами на АС, с учетом регистра
        {
           try
           {       
                if (State) Acm.addElement(new Integer(INDX_S_EQU)); 
                else 
                {
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Long li2 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        String s2 = (String) StringVariable.elementAt(li2.intValue()); 
                        if (s1.compareTo(s2)==0)li1=new Long(-1); else li1=new Long(0);
                        ArithmeticStack.addElement(li1);  
                }
         }
        catch (Exception e)
           {
                throw new Exception("Error in S= word");
           }          
        }

        void S_EQUNC() throws Exception // S== Сравнивает две строки, с адресами на АС, без учета регистра
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_S_EQUNC)); 
                else 
                {
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Long li2 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        String s2 = (String) StringVariable.elementAt(li2.intValue()); 
                        if (s1.equalsIgnoreCase(s2))li1=new Long(-1); else li1=new Long(0);
                        ArithmeticStack.addElement(li1);  
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in S== word");
           }          
        }

        void SYMBOL_POS() throws Exception // S_PS Ищет в строке из второго элемента, подстроку с номером на АС, если нет то -1 на АС иначе номер вхождения
        {
           try
           {      
                if (State) Acm.addElement(new Integer(INDX_S_PS)); 
                else 
                {
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Long li2 = (Long)ArithmeticStack.lastElement(); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        String s2 = (String) StringVariable.elementAt(li2.intValue()); 
                        ArithmeticStack.addElement(new Long(s2.indexOf(s1)));  
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in S_PS word");
           }          
        }

        void CHR() throws Exception // CHR (AS: int str_indx -> str_indx)
        {
           try
           {      
                if (State) Acm.addElement(new Integer(INDX_CHR)); 
                else 
                {
                        Long li = (Long)ArithmeticStack.lastElement(); 
                        Long lil= (Long)ArithmeticStack.elementAt(ArithmeticStack.size()-2); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-2);
                        String lnew_string = String.valueOf((char)(lil.intValue()));
                        StringVariable.setElementAt(lnew_string,li.intValue());   
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in CHR word");
           }          
        }


        void S_ADD() throws Exception // S+ Складывает две строки, с адресами на АС, полученное значение кладется в 1-е слагаемое
        {
           try
           {        
                if (State) Acm.addElement(new Integer(INDX_S_ADD)); 
                else 
                {
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Long li2 = (Long)ArithmeticStack.lastElement(); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        String s2 = (String) StringVariable.elementAt(li2.intValue()); 
                        s2 = s2+s1;
                        StringVariable.setElementAt(s2,li2.intValue()); 
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in S+ word");
           }          
        }

        void PRINT_S() throws Exception // S. Выводит в поток строку с номером на АС
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_PRINT_S)); 
                else 
                {  
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        Destination.append(s1);
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in S. word");
           }          
        }

        void UPPERCASE() throws Exception // S_UC Приводит все символы в верхний регистр
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_S_UC)); 
                else 
                {  
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        s1=s1.toUpperCase();                         
                        StringVariable.setElementAt(s1,li1.intValue());                         
                }
             }
           catch (Exception e)
           {
                throw new Exception("Error in S_UC word");
           }          

        }

        void SYMBOLLENGTH() throws Exception // S_LN Кладет на АС длину строки, номер при этом не удаляет
        {
            try
            {             
                if (State) Acm.addElement(new Integer(INDX_S_LN)); 
                else 
                {  
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        ArithmeticStack.addElement(new Long(s1.length()));                         
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in S_LN word");
           }          
        }

        void LOWERCASE() throws Exception // S_LC Приводит все символы в нижний регистр
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_S_LC)); 
                else 
                {  
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        String s1 = (String) StringVariable.elementAt(li1.intValue()); 
                        s1=s1.toLowerCase();                         
                        StringVariable.setElementAt(s1,li1.intValue());                         
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in S_LC word");
           }          
        }

        void SYMBOLAT() throws Exception // S_AT Кладет на стек код символа из позиции на АС из строки с кодом во втором элементе
        {
          try
          {
                if (State) Acm.addElement(new Integer(INDX_S_AT)); 
                else 
                {  
                        char lch;
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Long li2 = (Long)ArithmeticStack.lastElement(); 
                        String s1 = (String) StringVariable.elementAt(li2.intValue()); 

                        try
                        {
                          lch = s1.charAt(li1.intValue());                         
                        }
                        catch (StringIndexOutOfBoundsException e) 
                        {
                                ArithmeticStack.addElement(new Long(-1));  
                                return;
                        }

                        ArithmeticStack.addElement(new Long(lch));  
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in S_AT word");
           }          
        }

        void PRINT_N() throws Exception // . Выводит в поток число на АС
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_PRINT_N)); 
                else 
                {  
                        Long li1 = (Long)ArithmeticStack.lastElement(); 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
                        Destination.append(li1.toString());
                }
            }
        catch (Exception e)
          {
             throw new Exception("Error in . word");
          }          
        }

        void DROPALL() throws Exception // ALLDROP Удаляет все содержимое АС
        {
                if (State) Acm.addElement(new Integer(INDX_DROPALL)); 
                else 
                 ArithmeticStack.removeAllElements(); 
        } 

        void DROPALLR() throws Exception // ALLDROPR Удаляет все содержимое СВ
        {
                if (State) Acm.addElement(new Integer(INDX_DROPALLR)); 
                else 
                 ReturnStack.removeAllElements(); 
        } 

        void DROP() throws Exception // DROP Удаляет верхнее значение со стека
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_DROP)); 
                else 
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1); 
            }
        catch (Exception e)
           {
                throw new Exception("Error in DROP word");
           }          
        }

        void DROPR() throws Exception // DROPR Удаляет верхнее значение со стека СВ
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_DROPR)); 
                else 
                        ReturnStack.removeElementAt(ArithmeticStack.size()-1); 
            }
        catch (Exception e)
           {
                throw new Exception("Error in DROPR word");
           }          
        }

        void DUP() throws Exception // DUP Дублирует верхнее значение на стеке
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_DUP)); 
                else 
                {
                        Long tmp_obj = (Long)ArithmeticStack.lastElement(); 
                        tmp_obj = new Long(tmp_obj.longValue());  
                        ArithmeticStack.addElement(tmp_obj);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in DUP word");
           }          
        }

        void DUPR() throws Exception // DUPR Дублирует верхнее значение на стеке СВ
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_DUPR)); 
                else 
                {
                        Long tmp_obj = (Long)ReturnStack.lastElement(); 
                        tmp_obj = new Long(tmp_obj.longValue());  
                        ReturnStack.addElement(tmp_obj);
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in DUPR word");
           }          
        }

        void SWAP() throws Exception // SWAP Меняет местами два верхних элемента АС
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_SWAP)); 
                else 
                {  
                        Object obj1 = ArithmeticStack.lastElement();
                        Object obj2 = ArithmeticStack.elementAt(ArithmeticStack.size()-2); 
                        ArithmeticStack.setElementAt(obj1,ArithmeticStack.size()-2);
                        ArithmeticStack.setElementAt(obj2,ArithmeticStack.size()-1);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in SWAP word");
           }          
        }

        void SWAPR() throws Exception // SWAPR Меняет местами два верхних элемента СВ
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_SWAPR)); 
                else 
                {  
                        Object obj1 = ReturnStack.lastElement();
                        Object obj2 = ReturnStack.elementAt(ReturnStack.size()-2); 
                        ReturnStack.setElementAt(obj1,ReturnStack.size()-2);
                        ReturnStack.setElementAt(obj2,ReturnStack.size()-1);
                }
            }
          catch (Exception e)
           {
                throw new Exception("Error in SWAPR word");
           }          
        }

        void OVER() throws Exception // OVER Дублирует второй элемент стека на вершину
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_OVER)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.elementAt(ArithmeticStack.size()-2);
                        Long obj2 = new Long(obj1.intValue());  
                        ArithmeticStack.addElement(obj2);
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in OVER word");
           }          
        }

        void ROT() throws Exception // ROT Переносит третий от вершины элемент стека на вершину
        {
            try
            { 
                if (State) Acm.addElement(new Integer(INDX_ROT)); 
                else 
                {  
                        Object obj1 = ArithmeticStack.elementAt(ArithmeticStack.size()-3);  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-3); 
                        ArithmeticStack.addElement(obj1); 
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in ROT word");
           }          
        }

        void ROTR() throws Exception // ROTR Переносит третий от вершины СВ элемент  на вершину
        {
              try
              {
                if (State) Acm.addElement(new Integer(INDX_ROTR)); 
                else 
                {  
                        Object obj1 = ReturnStack.elementAt(ReturnStack.size()-3);  
                        ReturnStack.removeElementAt(ReturnStack.size()-3); 
                        ReturnStack.addElement(obj1); 
                }
              }
           catch (Exception e)
           {
                throw new Exception("Error in ROTR word");
           }          

        }

        void TO_R() throws Exception // >R Переносит вершину арифметического стека на вершину стека возвратов
        {
            try
            { 
                if (State) Acm.addElement(new Integer(INDX_TO_R)); 
                else 
                {  
                        Object obj1 = ArithmeticStack.lastElement();  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);    
                        ReturnStack.addElement(obj1); 
                }
            }
           catch (Exception e)
           {
                throw new Exception("Error in >R word");
           }          
        }

        void FROM_R() throws Exception  // R> Переносит вершину стека возвратов на вершину арифметического стека
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_FROM_R)); 
                else 
                {  
                        Object obj1 = ReturnStack.lastElement();  
                        ReturnStack.removeElementAt(ArithmeticStack.size()-1);    
                        ArithmeticStack.addElement(obj1); 
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in R> word");
           }          
        }

        void COPY_R() throws Exception // R@ Копирует вершину стека возвратов на вершину арифметического стека
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_COPY_R)); 
                else 
                {  
                        Object obj1 = ReturnStack.lastElement();  
                        ArithmeticStack.addElement(obj1); 
                }
             }
           catch (Exception e)
           {
                throw new Exception("Error in R@ word");
           }          
        }       

        void ADD() throws Exception // + Складывает два верхних элемента арифметического стека
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_ADD)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj1.longValue() + obj2.longValue());
                        ArithmeticStack.addElement(obj2);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in + word");
           }          
        }

        void INC() throws Exception // 1+ Увеличивает вершину АС на единицу
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_INC)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj1  = new Long(obj1.longValue() + 1);
                        ArithmeticStack.addElement(obj1);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in 1+ word");
           }          
        }


        void DEC() throws Exception // 1- Уменьшает вершину АС на единицу
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_DEC)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj1  = new Long(obj1.intValue() - 1);
                        ArithmeticStack.addElement(obj1);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in 1- word");
           }          
        }

        void SUB() throws Exception // - Вычитает из второго элемента арифметического стека, значение вершины 
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_SUB)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue()-obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
             }
           catch (Exception e)
           {
               throw new Exception("Error in - word");
           }          
        }

        void MUL() throws Exception // * Перемножает два верхних элемента арифметического стека
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_MUL)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue()*obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
           }
           catch (Exception e)
           {
                throw new Exception("Error in * word");
           }          
     
        }

        void AND() throws Exception // AND вершины АС и второго элемента
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_AND)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue() & obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in AND word");
           }          
        }

        void OR() throws Exception // OR вершины АС и второго элемента
        {
             try
             {
                if (State) Acm.addElement(new Integer(INDX_OR)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue() | obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in OR word");
           }          
        }
        
        void XOR() throws Exception // XOR вершины АС и второго элемента
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_XOR)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue() ^ obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in XOR word");
           }          
        }

        void NOT() throws Exception // NOT инвертация вершины АС
        {
            try
            {            
                if (State) Acm.addElement(new Integer(INDX_NOT)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2  = new Long(~obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
           }
        catch (Exception e)
           {
                throw new Exception("Error in NOT word");
           }          
        }

        void DIV() throws Exception // / Делит второй элемент арифметического стека на значение вершины
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_DIV)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);
                        obj2  = new Long(obj2.longValue()/obj1.longValue());
                        ArithmeticStack.addElement(obj2);
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in / word");
           }          
        }        

        void EQU_ZERO() throws Exception // 0= Сравнивает вершину фрифметического стека с нулем
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_EQU_ZERO)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj1.longValue()==0) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in 0= word");
           }          
        }

        void MORE_ZERO() throws Exception // 0> Проверяет вершину АС на превосходство над нулем
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_MORE_ZERO)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj1.intValue()>0) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in 0> word");
           }          
        }

        void SMALL_ZERO() throws Exception // 0< Проверяет вершину АС на превосходство над нулем
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_SMALL_ZERO)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj1.longValue()<0) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in 0< word");
           }          
        }

        void SMALL() throws Exception // > Проверяет второй элемент АС на превосходство над вершиной
        {
            try
            {    
                if (State) Acm.addElement(new Integer(INDX_SMALL)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj2.longValue()<obj1.longValue()) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
             }
           catch (Exception e)
           {
                throw new Exception("Error in > word");
           }          
        }

        void EQU() throws Exception // = Сравнивает вершину АС со вторым элементом
        {
           try
           {
                if (State) Acm.addElement(new Integer(INDX_EQU)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj2.longValue()==obj1.longValue()) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in = word");
           }          
        }

        void MORE() throws Exception // < Проверяет вершину АС на превосходство над вторым элементом
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_MORE)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj2.longValue()>obj1.longValue()) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in < word");
           }          
        }

        void NO_EQU() throws Exception // <> Проверяет вершину АС на неравенство со вторым элементом
        {
           try
           {     
                if (State) Acm.addElement(new Integer(INDX_MORE)); 
                else 
                {  
                        Long obj1 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        Long obj2 = (Long)ArithmeticStack.lastElement();                  
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        if (obj2.longValue()!=obj1.longValue()) obj1=new Long(-1); else obj1=new Long(0);
                        ArithmeticStack.addElement(obj1);  
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in <> word");
           }          
        }

        void MUL_TWO() throws Exception // 2* Сдвиг вершины АС влево на один разряд
        {
             try
             {
                if (State) Acm.addElement(new Integer(INDX_MULL_TWO)); 
                else 
                {  
                        Long lobj1 = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        lobj1=new Long(lobj1.longValue()<<1);  
                        ArithmeticStack.addElement(lobj1);  
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in 2* word");
           }          
        }

        void DIV_TWO() throws Exception // 2/ Сдвиг вершины АС вправо на один разряд
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_DIV_TWO)); 
                else 
                {  
                        Long lobj1 = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);  
                        lobj1=new Long(lobj1.longValue()>>1);  
                        ArithmeticStack.addElement(lobj1);  
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in 2/ word");
           }          
        }

        void TO_MEM() throws Exception // ! Запись в переменную с номером на вершине АС значения из второго элемента АС 
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_TO_MEM)); 
                else 
                {  
                        Long addr = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);   
                        Long val = (Long) ArithmeticStack.lastElement();
                        ArithmeticStack.removeElementAt(ArithmeticStack.size()-1);   
                        IntVariable.setElementAt(val,addr.intValue()); 
                }
            }
        catch (Exception e)
           {
                throw new Exception("Error in ! word");
           }          
        }

        void FROM_MEM() throws Exception // @ Вывод на вершину АС значения переменной с номером на вершине АС  
        {
            try
            {
                if (State) Acm.addElement(new Integer(INDX_FROM_MEM)); 
                else 
                {  
                        Long addr = (Long) ArithmeticStack.lastElement();
                        Object var = IntVariable.elementAt(addr.intValue()); 
                        ArithmeticStack.setElementAt(var,ArithmeticStack.size()-1);   
                }
             }
        catch (Exception e)
           {
                throw new Exception("Error in @ word");
           }          
        }

        void VARIABLE() throws Exception // Variable создает переменную, выделяет ей память
        {
                if (State) throw new Exception("Error: \"Variable\" used only in translate mode");
                int ltt;
                Object lobj;
                ltt = GetNextToken(fis); 
                switch(ltt)
                {
                        case TOKEN_EOF    : throw new Exception("Not var name");
                        case TOKEN_NUMBER : throw new Exception("Bad var name");
                        case TOKEN_STRING : throw new Exception("Bad var name");
                        case TOKEN_WORD : {
                                                svar = svar.toUpperCase(); 
                                                lobj =  NameVocabulary.get(svar);
                                                if (lobj!=null) throw new Exception("Duplicate name"); 
                                                IntVariable.addElement(new Long(0));
                                                Acm = new Vector();
                                                Acm.addElement(new Integer(INDX_PUSH));
                                                Acm.addElement(new Integer(IntVariable.size()-1));
                                                Acm.addElement(new Integer(INDX_NEXT));    
                                                NameVocabulary.put(svar,Acm);     
                                          }; break;      
                }
        } 

        void NEW_WORD_START() throws Exception // : Начинает описание нового слова
        {
                if (State) throw new Exception("Error: \":\" used only in translate mode");
                int ltt;
                Object lobj;
                State = true;
                ltt = GetNextToken(fis); 
                LEAVE_address = 0;
                switch(ltt)
                {
                        case TOKEN_EOF : throw new Exception("Error: After \":\" must be word");
                        case TOKEN_NUMBER : throw new Exception("Error: After \":\" must be word");
                        case TOKEN_STRING : throw new Exception("Error: After \":\" must be word");
                        case TOKEN_WORD : {
                                                svar = svar.toUpperCase(); 
                                                lobj = NameVocabulary.get(svar);
                                                if (lobj!=null) throw new Exception("Duplicate word"); 
                                                IF_index.removeAllElements();
                                                Acm = new Vector();
                                                NameVocabulary.put(svar,Acm);     
                                          }; break;      
                }
        } 

        void NEW_WORD_END() throws Exception // ; Заканчивает описание нового слова
        {
                if (!State) throw new Exception("Error: \";\" used only in compiled mode");
                State = false;
                Acm.addElement(new Integer(INDX_NEXT));  
        }

        void NEXT() throws Exception // NEXT Заканчивает выполнение данного слова и переходит по адресу на СВ
        {
                if (State) Acm.addElement(new Integer(INDX_NEXT)); 
                Cur_Word = (Vector) ReturnStack.lastElement();
                ReturnStack.removeElementAt(ReturnStack.size()-1);    
                Long lpc_cntr = (Long) ReturnStack.lastElement();
                ReturnStack.removeElementAt(ReturnStack.size()-1);    
                PC_Counter = lpc_cntr.intValue();
        }

        void FORCE() throws Exception // ? Компилирует слово следующее в потоке если оно есть и пропускает его если его неть в словаре
        {
          if (!State) throw new Exception("Word \"?\" not available in translate mode");
                Object lobj;
                int ltt = GetNextToken(fis); 
                switch(ltt)
                {
                  case TOKEN_EOF : throw new Exception("Error: After \"?\" must be word");
                  case TOKEN_NUMBER : throw new Exception("Error: After \"?\" must be word");
                  case TOKEN_STRING : throw new Exception("Error: After \"?\" must be word");
                }
                svar = svar.toUpperCase(); 
                lobj = NameVocabulary.get(svar);
                if (lobj!=null) 
                {
                    Acm.addElement(new Integer(INDX_CALL));
                    Acm.addElement(lobj);
                }
         }

//------------------------------------------


        void ExecuteCompileCommand(int cmnd) throws Exception 
        {
                switch (cmnd)
                {
                        case INDX_LEAVE         : LEAVE()   ; break;
                        case INDX_ADD           : ADD()     ; break;
                        case INDX_AND           : AND()     ; break;
                        case INDX_COPY_R        : COPY_R()  ; break;
                        case INDX_CYCLE_I       : CYCLE_I() ; break;
                        case INDX_CYCLE_J       : CYCLE_J() ; break;
                        case INDX_DIV           : DIV()     ; break;
                        case INDX_DIV_TWO       : DIV_TWO() ; break;
                        case INDX_DO            : DO()      ; break;
                        case INDX_DROP          : DROP()    ; break;
                        case INDX_DROPALL       : DROPALL() ; break;
                        case INDX_DROPALLR      : DROPALLR(); break;
                        case INDX_DROPR         : DROPR()   ; break;
                        case INDX_DUP           : DUP()     ; break;
                        case INDX_DUPR          : DUPR()    ; break;
                        case INDX_ELSE          : ELSE()    ; break;    
                        case INDX_EQU           : EQU()     ; break;
                        case INDX_EQU_ZERO      : EQU_ZERO(); break;
                        case INDX_FROM_MEM      : FROM_MEM(); break;
                        case INDX_FROM_R        : FROM_R()  ; break;
                        case INDX_IF            : IF()      ; break;
                        case INDX_LOOP          : LOOP()    ; break;
                        case INDX_MORE          : MORE()    ; break;
                        case INDX_MORE_ZERO     : MORE_ZERO(); break;
                        case INDX_MUL           : MUL()     ; break;
                        case INDX_MULL_TWO      : MUL_TWO() ; break;
                        case INDX_NEW_WORD_END  : NEW_WORD_END();break;
                        case INDX_NEW_WORD_START: NEW_WORD_START(); break;
                        case INDX_NEXT          : NEXT()    ; break;
                        case INDX_NOT           : NOT()     ; break;
                        case INDX_OR            : OR()      ; break;
                        case INDX_OVER          : OVER()    ; break;
                        case INDX_PRINT_N       : PRINT_N() ; break;
                        case INDX_PRINT_S       : PRINT_S() ; break;
                        case INDX_PUSH          : PUSH()    ; break;
                        case INDX_ROT           : ROT()     ; break;
                        case INDX_ROTR          : ROTR()    ; break;
                        case INDX_S_ADD         : S_ADD()   ; break;
                        case INDX_SMALL         : SMALL()   ; break;
                        case INDX_SMALL_ZERO    : SMALL_ZERO(); break;
                        case INDX_SUB           : SUB()     ; break;
                        case INDX_SWAP          : SWAP()    ; break;
                        case INDX_SWAPR         : SWAPR()   ; break;
                        case INDX_THEN          : THEN()    ; break;
                        case INDX_TO_MEM        : TO_MEM()  ; break;
                        case INDX_TO_R          : TO_R()    ; break;
                        case INDX_VARIABLE      : VARIABLE(); break;
                        case INDX_XOR           : XOR()     ; break;
                        case INDX_CALL          : CALL()    ; break;                                                                             
                        case INDX_DEPTH         : DEPTH()   ; break;                                                                             
                        case INDX_DEPTHR        : DEPTHR()  ; break;
                        case INDX_S_EQU         : S_EQU()   ; break;    
                        case INDX_S_EQUNC       : S_EQUNC() ; break;
                        case INDX_ADD_LOOP      : ADD_LOOP(); break;
                        case INDX_BEGIN         : BEGIN()   ; break;
                        case INDX_UNTIL         : UNTIL()   ; break;
                        case INDX_ALLOT         : ALLOT()   ; break;            
                        case INDX_INC           : INC()     ; break;            
                        case INDX_DEC           : DEC()     ; break;            
                        case INDX_NO_EQU        : NO_EQU()  ; break;            
                        case INDX_QUIT          : QUIT()    ; break;    
                        case INDX_S_UC          : UPPERCASE(); break;    
                        case INDX_S_LC          : LOWERCASE(); break;    
                        case INDX_S_AT          : SYMBOLAT(); break;    
                        case INDX_S_LN          : SYMBOLLENGTH(); break;    
                        case INDX_S_PS          : SYMBOL_POS(); break; 
                        case INDX_CHR           : CHR(); break; 
                        case INDX_STR_URLDECODE : URL_DEC(); break;
                        case INDX_STR_URLENCODE : URL_ENC(); break;
                        case INDX_TO_STRING     : TO_STRING(); break; 
                        case INDX_SYS_QCONNECT  : QCONNECT(); break;

                        case INDX_SQL_CLOSE           : SQL_CLOSE(); break; 
                        case INDX_SQL_CONNECT         : SQL_CONNECT(); break; 
                        case INDX_INCMEM : INCMEM(); break; 
                        case INDX_SQL_EXECUTEQ        : SQL_EXECUTEQ(); break; 
                        case INDX_SQL_EXECUTEU        : SQL_EXECUTEU(); break; 
                        case INDX_SQL_GETCOLUMNINT    : SQL_GETCOLUMNINT(); break; 
                        case INDX_SQL_GETCOLUMNSTR    : SQL_GETCOLUMNSTR(); break; 
                        case INDX_SQL_LOADDRIVER      : SQL_LOADDRIVER(); break; 
                        case INDX_SQL_NEXT            : SQL_NEXT(); break; 
                        case INDX_SQL_AUTOCOMMIT      : SQL_AUTOCOMMIT(); break;
                        case INDX_SQL_GETNAMECOLUMNINT: SQL_GETCOLUMNINTFORNAME(); break;
                        case INDX_SQL_GETNAMECOLUMNSTR: SQL_GETCOLUMNSTRFORNAME(); break;
                        case INDX_SQL_COMMIT          : SQL_COMMIT(); break;
                        case INDX_SQL_ROLLBACK        : SQL_ROLLBACK(); break;                                    
                        case INDX_SQL_TRANSISOLATION  : SQL_SETTRANSISOLATION(); break; 

                        case INDX_ADM_CLIENTADD       : ADMIN_CLIENTADD(); break;
                        case INDX_ADM_CLIENTCONNECT   : ADMIN_CLIENTCONNECT(); break;
                        case INDX_ADM_CLIENTEXISTS    : ADMIN_CLIENTEXIST(); break;
                        case INDX_ADDCHAR2STR         : ADD_CHAR2STR(); break;
                        case INDX_ADM_CLIENTNAME      : ADMIN_CLIENTNAME(); break;
                        case INDX_ADM_CLIENTNEXT      : ADMIN_CLIENTNEXT(); break;
                        case INDX_ADM_CLIENTPASSWORD  : ADMIN_CLIENTPASSWORD(); break;
                        case INDX_ADM_CLIENTREMOVE    : ADMIN_CLIENTREMOVE(); break;

                        case INDX_SOCKET_READBYTE     : SOCKET_READBYTE(); break;
                        case INDX_SOCKET_WRITEBYTE    : SOCKET_WRITEBYTE(); break;
                        case INDX_SOCKET_READSTRING   : SOCKET_READSTRING(); break;
                        case INDX_SOCKET_WRITESTRING  : SOCKET_WRITESTRING(); break;

                        case INDX_SYS_MESSAGE         : SYS_MESSAGE(); break;          

                        case INDX_SQL_NGETBLOB        : SQL_GETBLOBSTREAMFORNAME(); break;
                        case INDX_SQL_GETBLOB         : SQL_GETBLOBSTREAMFORNUMBER(); break;
                        case INDX_SQL_READBLOBBYTE    : SQL_READBLOBBYTE(); break;
                        case INDX_SQL_CLOSEINPBLOB    : SQL_CLOSEINPBLOB(); break;
                        
                        case INDX_DT_DATE_BREAK  : DATE_BREAK(); break;
                        case INDX_DT_DATE_PACK : DATE_PACK(); break;
                        case INDX_DT_DATETIME : DATETIME(); break;
                        case INDX_DT_TIME_BREAK : TIME_BREAK(); break;
                        case INDX_DT_SET_HOURS : TIME_SETHH(); break;
                        case INDX_DT_SET_MINUTES : TIME_SETMM(); break;
                        case INDX_DT_SET_SECONDS : TIME_SETSS (); break;

                        case INDX_PLAYFILE : PLAYFILE(); break;    
                        case INDX_I2S : I2S(); break;
                        case INDX_S2I : S2I(); break;
                        case INDX_SOCKET_WRITENUM :  SOCKET_WRITENUM(); break;
                        default : throw new  Exception("Unknown command code "+String.valueOf(cmnd));
                }
        }

        void ExecuteTranslateCommand(String cmnd) throws Exception
        {
                cmnd = cmnd.trim().toUpperCase();
                Vector cur_cmnd;                
                if (cmnd.compareTo("SN.")==0)  SOCKET_WRITENUM(); else 
                if (cmnd.compareTo("DATE_BREAK")==0) DATE_BREAK(); else
                if (cmnd.compareTo("DATE_PACK")==0) DATE_PACK(); else
                if (cmnd.compareTo("DATETIME")==0) DATETIME(); else
                if (cmnd.compareTo("TIME_BREAK")==0) TIME_BREAK(); else
                if (cmnd.compareTo("SETHH")==0) TIME_SETHH(); else
                if (cmnd.compareTo("SETMM")==0) TIME_SETMM(); else
                if (cmnd.compareTo("SETSS")==0) TIME_SETSS(); else
                if (cmnd.compareTo("MSG.")==0) SYS_MESSAGE(); else
                if (cmnd.compareTo("SCKT_RDB")==0) SOCKET_READBYTE(); else
                if (cmnd.compareTo("SCKT_RDS")==0) SOCKET_READSTRING(); else
                if (cmnd.compareTo("SCKT_WRB")==0) SOCKET_WRITEBYTE(); else
                if (cmnd.compareTo("SS.")==0) SOCKET_WRITESTRING(); else          
//                if (cmnd.compareTo("ADM_CLIENTADD")==0) ADMIN_CLIENTADD(); else
//                if (cmnd.compareTo("ADM_CLIENTCON")==0) ADMIN_CLIENTCONNECT(); else
//                if (cmnd.compareTo("ADM_CLIENTEXT")==0) ADMIN_CLIENTEXIST(); else
                if (cmnd.compareTo("CHR+")==0)ADD_CHAR2STR(); else
//                if (cmnd.compareTo("ADM_CLIENTNAME")==0)ADMIN_CLIENTNAME(); else
//                if (cmnd.compareTo("ADM_CLIENTNXT")==0) ADMIN_CLIENTNEXT(); else
//                if (cmnd.compareTo("ADM_CLIENTPSSW")==0)ADMIN_CLIENTPASSWORD(); else
//                if (cmnd.compareTo("ADM_CLIENTREM")==0) ADMIN_CLIENTREMOVE(); else
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
                if (cmnd.compareTo("S_UC")==0) UPPERCASE(); else
                if (cmnd.compareTo("S_LC")==0) LOWERCASE(); else
                if (cmnd.compareTo("S_AT")==0) SYMBOLAT(); else
                if (cmnd.compareTo("S_LN")==0) SYMBOLLENGTH(); else
                if (cmnd.compareTo("LEAVE")==0) LEAVE(); else
                if (cmnd.compareTo("IF")==0) IF(); else
                if (cmnd.compareTo("THEN")==0) THEN(); else
                if (cmnd.compareTo("ELSE")==0) ELSE(); else
                if (cmnd.compareTo(".")==0) PRINT_N(); else
                if (cmnd.compareTo("S.")==0) PRINT_S(); else
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
                if (cmnd.compareTo(">")==0) SMALL(); else
                if (cmnd.compareTo("<")==0) MORE(); else
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
                if (cmnd.compareTo("?CONNECT")==0) QCONNECT(); else
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
                if (cmnd.compareTo("DB_RDBLOB")==0) SQL_READBLOBBYTE(); else
                if (cmnd.compareTo("DB_CLOSEINBLOB")==0) SQL_CLOSEINPBLOB(); else
                if (cmnd.compareTo("PLAYFILE")==0) PLAYFILE(); else
                if (cmnd.compareTo("I2S")==0) I2S(); else
                if (cmnd.compareTo("S2I")==0) S2I(); else
                {
                       if(State)
                       {  
                        cur_cmnd = (Vector)NameVocabulary.get(cmnd);
                        if (cur_cmnd==null) throw new Exception("Unknown word \""+cmnd+"\""); 
                        Acm.addElement(new Integer(INDX_CALL)); 
                        Acm.addElement(cur_cmnd);  
                       }
                       else
                       {
                        cur_cmnd = (Vector)NameVocabulary.get(cmnd);
                        if (cur_cmnd==null) throw new Exception("Unknown word \""+cmnd+"\"");
                        UserMacroExecute(cur_cmnd);        
                       }    
                }
        }

        // Выполнение макрослова пользователя
        void UserMacroExecute(Vector body) throws Exception
        {
                ReturnStack.addElement(new Long(PC_Counter));  
                ReturnStack.addElement(Cur_Word);  
                PC_Counter = 0;
                Cur_Word = body;
                Integer lobj=null;
                int lll;

                while(PC_Counter<Cur_Word.size())
                {                
                        lobj = (Integer)Cur_Word.elementAt(PC_Counter);
                        PC_Counter++;
                        lll=((Integer)lobj).intValue(); 
                        ExecuteCompileCommand(((Integer)lobj).intValue());
                        if ((Cur_Word==null)||(IsQuit)) break;  
                }
        } 

        // Считывает из входного потока следующий токен и возвращает его тип
        int GetNextToken(DataInputStream dis) throws Exception 
        {
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
                                                                        }
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
                                                                        }
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
                                                                                }
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
                                                                        }
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
                try
                {
                        if (fis!=null) fis.close();  
                }
                catch (IOException e){}

                for(int li=0;li<SQLConnections.size();li++)
                {
                        if (SQLConnections.elementAt(li)!=null)
                        {
                          try
                          {
                                if (!((java.sql.Connection)SQLConnections.elementAt(li)).isClosed())
                                        {
                                           if (!((java.sql.Connection)SQLConnections.elementAt(li)).getAutoCommit())      
                                           ((java.sql.Connection)SQLConnections.elementAt(li)).rollback(); 
                                        }
                          }
                          catch (Exception e) {}                  
                        } 
                } 

                for(int li=0;li<SQLInputStream.size();li++)
                {
                        if (SQLInputStream.elementAt(li)!=null)
                        {
                                try
                                {
                                        ((InputStream)SQLInputStream.elementAt(li)).close();   
                                }
                                catch (Exception e)
                                {}
                        } 
                }  

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

                for(int li=0;li<SQLConnections.size();li++)
                {
                        if (SQLConnections.elementAt(li)!=null)
                        {
                          try
                          {
                                if (!((java.sql.Connection)SQLConnections.elementAt(li)).isClosed())
                                        {
                                           ((java.sql.Connection)SQLConnections.elementAt(li)).close(); 
                                        }
                          }
                          catch (Exception e) {}                  
                        } 
                } 

                SQLConnections.removeAllElements();
                SQLResultSets.removeAllElements();     
                SQLStatements.removeAllElements();
                SQLConnections=null;
                SQLResultSets=null;
                SQLStatements=null;
                
                IF_index.removeAllElements();
                IF_index=null;
                fis = null;
                Destination=null;          
                Cycle_Index.removeAllElements();
                Cycle_Index=null;
                StringVariable.removeAllElements();
                StringVariable=null; 
                IntVariable.removeAllElements();  
                IntVariable = null;
                ArithmeticStack.removeAllElements(); 
                ArithmeticStack=null;
                ReturnStack.removeAllElements();
                ReturnStack=null;
                NameVocabulary.clear();
                NameVocabulary = null;
                BodyVocabulary.removeAllElements();
                BodyVocabulary = null; 
        }

}
