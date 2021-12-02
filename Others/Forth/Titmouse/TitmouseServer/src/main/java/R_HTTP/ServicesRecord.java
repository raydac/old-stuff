package R_HTTP;

public class ServicesRecord
{
        public String Alias; // Наименование сервера
        public String HomeDir; // Домашний каталог
        public String DefaultPage; // Страница по умолчанию
		public int Port; // Номер порта
        public int MaxClCount ; // Максимальное количество одновременных клиентов
		public R_HTTPServer server=null; // Указатель на связанный сервер
		public int Time_delay;
		public String StartScript;
		public String StopScript;
		
        public ServicesRecord (String alias, String homedir, String defaultpage, int port, int maxclcount, int tdelay, String startscr,String stopscr)
        {
				Time_delay = tdelay;
				Alias = alias;
                HomeDir = homedir;
                DefaultPage = defaultpage;
                Port = port;
                MaxClCount=maxclcount;
				StartScript = startscr;
				StopScript = stopscr;
        }
}
