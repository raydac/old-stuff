package R_HTTP;

import java.util.*;  

public class TimeScript
{
        public String ScriptName; // Наименование скрипта
        public String FileName; // Имя файла, содержащего скрипт
        public int TimeDelay; // Интервал исполнения скрипта
		public int HourOfStart=-1; // Час старта
		public int MinuteOfStart=-1; // Минута старта
		public int WeekDayOfStart=-1; // День недели
		public int DayOfStart=-1; // День старта
		public int MonathOfStart=-1; // Месяц старта
		public int YearOfStart=-1; // Год старта
		
		public int TimeCounter; // Счетчик интервала
        public Thread script_thread; // Поток выполнения скрипта
        public int StartCounter; // Счетчик запусков 
		public String SimpleFileName; // Имя файла без пути

		public TimeScript (String sname,String pth,String fname, int tdelay)
        {
                ScriptName = sname;
                FileName = pth+fname;
				SimpleFileName = fname;
                TimeDelay = tdelay;
                TimeCounter = 0;
                StartCounter=0;
                script_thread = null;
        }

		public boolean TimeCompare(Date curdate)
		{
			boolean lflag=true;
			if ((HourOfStart>=0)&&(curdate.getHours()!=HourOfStart)) lflag=false;
			if ((MinuteOfStart>=0)&&(curdate.getMinutes()!=MinuteOfStart))lflag=false;
			if (lflag)
			{	
				if (this.TimeCounter<60) lflag=false;
			}
			if ((WeekDayOfStart>=0)&&(curdate.getDay()!=WeekDayOfStart)) lflag=false;
			if ((DayOfStart>=0)&&(curdate.getDate()!=DayOfStart)) lflag=false;
			if ((MonathOfStart>=0)&&((curdate.getMonth()+1)!=MonathOfStart)) lflag=false;			
			if ((YearOfStart>=0)&&((curdate.getYear()+1900)!=YearOfStart)) lflag=false;
			return lflag;
		}
		
		public TimeScript (String sname,String pth,String fname, int hour,int minutes,int day,int weekday,int month,int year)
        {
                ScriptName = sname;
                FileName = pth+fname;
				SimpleFileName = fname;
                TimeDelay = -1;
				
				HourOfStart=hour; // Час старта
				MinuteOfStart=minutes; // Минута старта
				WeekDayOfStart=weekday; // День недели
				DayOfStart=day; // День старта
				MonathOfStart=month; // Месяц старта
				YearOfStart=year; // Год старта
				
                TimeCounter = 0;
                StartCounter=0;
                script_thread = null;
        }

}
