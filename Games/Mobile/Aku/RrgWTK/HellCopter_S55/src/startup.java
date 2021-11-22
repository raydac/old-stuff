/**
 * <p>Title: Startup</p>
 * <p>Description: Startup games engine</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Akumiitti</p>
 * @version 1.0
 *    */


import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.igormaznitsa.midp.Rms.*;
import com.igormaznitsa.midp.*;
import javax.microedition.lcdui.List;


public class startup extends MIDlet implements CommandListener {

    static Display display;
//    Displayable prevDisplay;
    GameArea canvas;

    RMSEngine SavedGames;                   // RMS storage handler for saved games
    RMSEngine TopTen;                       // RMS storage handler for TopTen highscores
    LanguageBlock MSG;
    String []saMSG;

    private final static String LANG_RES = "/res/langs.bin";
    private final static String RMSSavedGames = "Saves_Game1";   // name of RMS storage
    private final static String RMSTopTen = "TopTen_Game1";      // name of RMS storage
    private final static String NameMask = "              ";
    private int _lang = -1;
    private boolean RecordsAvailable = false, LanguagesAvailable = true,  ReverseOption = false;
    public boolean _Sound = false ,_Light = true ,_Reverse = true, _Vibra = true;
    private String PlayerName = null;
    private String AskNameAddIn = null;
    private int LngIdx = 500, SndIdx = 500, VbrIdx = 500, LghtIdx = 500;

    private static final int sMAIN = 0,
			     sGAME = 1,
			     sMAINLOAD = 2,
			     sGAMELOADSAVE = 3,
			     sMAINOPTIONS = 4,
			     sGAMEOPTIONS = 5;
//			     sGAMELOAD = 6,
//			     sGAMESAVE = 7;

    private static final int subLOAD = 0,
			     subSAVE = 1,
			     subDELETE = 2,
			     subLANGUAGES = 3,
			     subLIGHT = 4,
			     subSOUND = 5,
			     subVIBRA = 6,
			     subEXIT = 7,
			     subENDGAME = 8,
			     subCLEARALL = 9,
			     subHIGHSCORE = 10,
			     subNEWGAME = 11;

    private static final int rOPTIONS = 1,
                             rHIGHSCORE =2; //3

    private int menuState = -1, subMenu = -1, lastIndex = -1, SourceRec = -1;

    private Command cBack, cYes, cNo, cAccept, cClear;
    private List ListOfSavedGames;//ListItem;

    private Form AskName;
    private TextField NameField;

    private int [] _scores = new int[11];
    private String [] _names = new String[11];
    public boolean ACSENDING_HIGHSCORE = true;
    private int _gamescore = 0;

    private void ErrorAlert(String title,int n){

     // System.out.println("Error alert");

	 Alert a=new Alert(title,saMSG[n],null,AlertType.WARNING);
	 a.setTimeout(-2);
	 display.setCurrent(a,display.getCurrent());

    }


    private void addHighScore(String s, int n){
      int i=9;
      for (;i>=0;i--)
       if ((ACSENDING_HIGHSCORE && n<_scores[i]) || (!ACSENDING_HIGHSCORE && n>_scores[i])) break;
         else {
           _scores[i+1]=_scores[i];
           _names[i+1]=_names[i];
         }
       _scores[i+1]=n;
       _names[i+1]=s;
    }

   private void showHighScore(){
         String msg = saMSG[HighContent];
         StringBuffer sb = new StringBuffer();
	 subMenu = subHIGHSCORE;
	 for(int i=0;i<10;i++){
	   if (_names[i]==null)break;
           sb.append(i+1);
           sb.append(". ");
           sb.append(_names[i]);
           sb.append("\n      ");
	   sb.append(_scores[i]);
	   sb.append("\n");
	 }
	 if (sb.length()>0) msg = sb.toString();
	 sb = null;
	 Form a=new Form(saMSG[HighTitle]);
	 a.append(msg);
	 a.addCommand(cClear);
	 a.addCommand(cBack);
	 a.setCommandListener(this);
	 display.setCurrent(a);
	 a=null;
	 Runtime.getRuntime().gc();
   }

    private void loadHighScore(){
      for (int i=0;i<11;i++){
           _scores[i]=0;
           _names[i]=null;
      }
      if (TopTen != null)
      try{
        DataInputStream dis = TopTen.GetRecord(rHIGHSCORE);
	for(int i=0;i<10;i++){
	addHighScore(dis.readUTF(),dis.readInt());
	}
      }catch (Exception e){}
      Runtime.getRuntime().gc();
    }

    private void saveHighScore(){
     if(TopTen!=null)
      try {
	if (TopTen.GetNumRecords()<rHIGHSCORE) saveOptions();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
	DataOutputStream dos = new DataOutputStream(baos);
	dos.writeUTF(NameMask);

        for (int i=0;i<10;i++){
	   if (_names[i]==null) break;
	   dos.writeUTF(_names[i]);
	   dos.writeInt(_scores[i]);
        }
	dos.flush();
	TopTen.StoreRecord(rHIGHSCORE,baos.toByteArray());
	dos=null; baos = null;
      } catch (Exception e){if(e instanceof RMSException)ErrorAlert(saMSG[HighTitle],((RMSException)e).Error_Code);}
      Runtime.getRuntime().gc();
    }


    void Delete(int n){
      n++;
      try {
      SavedGames.Delete(n);
      } catch (RMSException e){
	ErrorAlert(saMSG[DeleTitle],e.Error_Code);
	/* System.out.println("Delete error, rec#"+n+"\n"+e.getMessage());*/}
    }

    boolean Save(int n){
//      System.out.println(">> Save");
      boolean ret = false;
      n++;
      Runtime.getRuntime().gc();
      try {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(canvas.client_sb.getMaxSizeOfSavedGameBlock());
//      System.out.println(">> Save -arr");
	DataOutputStream dos = new DataOutputStream(baos);
        String tmp=(PlayerName==null?"":PlayerName)+NameMask;               // align to NameMask
	try {

           dos.writeUTF(tmp.substring(0,NameMask.length()));
	   tmp=null;
           canvas.client_sb.saveGameState(dos);
	   dos.flush();
	   baos.flush();
	   dos = null;
	   Runtime.getRuntime().gc();

           ret = SavedGames.StoreRecord(n,baos.toByteArray());
	   baos = null;

//      System.out.println("<<-<<");

	 } catch (IOException e){/*System.out.println("Save: CANVAS->CLIENT->SAVE, IOEXCEPTION rec#"+n+"\n"+e.getMessage());*/}
      } catch (RMSException e){ErrorAlert(saMSG[SaveTitle],e.Error_Code);}
      Runtime.getRuntime().gc();
      return ret;
    }
    boolean Load(int n){
      n++;
      boolean ret = false;
      Runtime.getRuntime().gc();
      try {
	 try{
	 canvas.client_sb.loadGameState(SavedGames.GetRecord(n));
	 ret = true;
	 } catch (IOException e){/*System.out.println("Load: CANVAS->CLIENT->LOAD, IOEXCEPTION rec#"+n+"\n"+e.getMessage()); return false;*/}
      } catch (RMSException e){
	 ErrorAlert(saMSG[LoadTitle],e.Error_Code);
	 /*System.out.println("Load error, rec#"+n+"\n"+saMSG[e.Error_Code));*/
      }
      Runtime.getRuntime().gc();
      return ret;
    }

    private void saveOptions(){
     if(TopTen!=null)
      try {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
	DataOutputStream dos = new DataOutputStream(baos);
	dos.writeUTF(NameMask);
           if(MSG!=null)_lang=MSG.getCurrentIndex();//-1)_lang=System.getProperty("microedition.locale");
	   dos.writeByte(_lang);
	   dos.writeBoolean(_Light);
	   dos.writeBoolean(_Sound);
	   dos.writeBoolean(_Vibra);
	dos.flush();
	TopTen.StoreRecord(rOPTIONS,baos.toByteArray());
	dos=null; baos = null;
      } catch (Exception e){if(e instanceof RMSException)ErrorAlert("Saving options",((RMSException)e).Error_Code);}
     Runtime.getRuntime().gc();
    }

    private void loadOptions(){
     if(TopTen!=null)
      try {
        DataInputStream dis = TopTen.GetRecord(rOPTIONS);
	_lang = dis.readByte();
	_Light = dis.readBoolean();
	_Sound = dis.readBoolean();
	_Vibra = dis.readBoolean();
	dis=null;
      } catch (Exception e){
	saveOptions();
      }
      setLight(_Light,false);
      setSound(_Sound,false);
      setVibra(_Vibra,false);

/*
      if (MSG.getIndexForID(_lang)==-1)
        if (MSG.getIndexForID(_lang=System.getProperty("microedition.locale"))==-1)
           if (MSG.getLanguageId() == null || MSG.getLanguageId().length == 0 || MSG.getIndexForID(_lang=MSG.getLanguageId()[0])==-1)
	     _lang=null;
*/
      Runtime.getRuntime().gc();
    }

    public void setLight(boolean v,boolean f){
      boolean p = _Light;
      _Light = v;

//      if (_Light) com.siemens.mp.game.Light.setLightOn();
//           else com.siemens.mp.game.Light.setLightOff();
      canvas.setScreenLight(_Light);
      if(p!=v && f) saveOptions();
    }
    public void setSound(boolean v,boolean f){
      boolean p = _Sound;
      _Sound = v;
      if (_Sound) ;
           else  ;
      if(p!=v && f) saveOptions();
    }
    public void setVibra(boolean v,boolean f){
      boolean p = _Vibra;
      _Vibra = v;
      if (canvas!=null && _Vibra);// canvas.keyReverse = true;
           else ;//canvas.keyReverse = false;
      if(p!=v && f) saveOptions();
    }




/////////////////////////////////////////////////////////////////////////////////////////////////////////

    public startup() {
	display = Display.getDisplay(this);         // capture display
    }


    /**
     * Starts the MIDlet from here
     * @throws MIDletStateChangeException
     */
    public void startApp() throws MIDletStateChangeException {
     try {
      Runtime.getRuntime().gc();
        canvas = new GameArea(this);                // initialising our visualisation engine
        display.setCurrent(canvas);
        canvas.startIt();

        TopTen = new RMSEngine(RMSTopTen, 3);
        loadOptions();

        MSG = new LanguageBlock(LANG_RES,_lang,canvas);

        if (MSG!=null)
        {
	  saMSG = MSG.getCurrentStringArray();

	  SavedGames = new RMSEngine(RMSSavedGames,4);
	  SavedGames.setDefName(saMSG[NewRecordContent]);
          loadHighScore();
          rebuildMenuItems();
        }
      String []Langs = MSG.getLanguageNames();
      LanguagesAvailable = (Langs != null && Langs.length>1);

      canvas.gameStatus=canvas.titleStatus;
      canvas.repaint();//custom_paint();

     } catch(Exception e){e.printStackTrace(); System.exit(-1);}
    }

    public void pauseApp() {}

    public void killApp(){
      try {
           destroyApp(false);
      } catch (MIDletStateChangeException ex) {}
      display.setCurrent(null);
    }

    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        ListOfSavedGames = null;
	AskName = null;
        Runtime.getRuntime().gc();

	if (canvas!= null){

	  if (/*canvas.client_sb.isLoadSaveSupporting() && */canvas.autosaveCheck() && SavedGames!=null)
	     {
	         PlayerName = (saMSG[AutoSaveName]+NameMask).substring(0,NameMask.length());

		 int pos = -1;
		 try {
		   String [] names = SavedGames.GetList(false);
		   if (names!=null && PlayerName!=null)
		     for(int i=0;i<names.length;i++){
		        if(PlayerName.compareTo(names[i])==0)
		          { pos=i; break; }
		     }
	           Save(pos);
		 } catch(Exception e){}
	     }
	  canvas.endGame();
	  canvas.destroy();
	}
	if (SavedGames!=null) SavedGames.Close();
	if (TopTen!=null) TopTen.Close();
	notifyDestroyed();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////    Обработка слушателем команд

    void ShowList(String title, boolean AddNew){
      try {
	 ListOfSavedGames = null;
	 String []a = SavedGames.GetList(AddNew);
 	 ListOfSavedGames = new List(title,List.IMPLICIT,SavedGames.GetList(AddNew),null);
         ListOfSavedGames.addCommand(cBack);
	 ListOfSavedGames.setCommandListener(this);
	 display.setCurrent(ListOfSavedGames);
      } catch(Exception e)
       {
	 ErrorAlert(title,NoRecordsAvailableContent);
       }
    }

    void AreYouSure(String content){
       Runtime.getRuntime().gc();
        Form FormItem = new Form(content);
	//if (content == null) System.out.println("FFFFFFFFFFFF");
	FormItem.append(saMSG[AreYouSureTitle]);
	FormItem.addCommand(cYes);
	FormItem.addCommand(cNo);
	FormItem.setCommandListener(this);
	display.setCurrent(FormItem);
    }

    void AskName() {
      if (PlayerName==null || PlayerName.length()>/*saMSG[MaskOfPlayerName].length()*/12)PlayerName=saMSG[DefPlayerName];

      ListOfSavedGames = null;
      NameField = null;
      AskName = null;

      Runtime.getRuntime().gc();

      Form FormItem = new Form(saMSG[PlayerNameTitle]);
      Runtime.getRuntime().gc();
      NameField = new TextField(saMSG[PlayerNameAsk],PlayerName,10,TextField.ANY);

      if (AskNameAddIn!=null){
         FormItem.append(AskNameAddIn);
	 AskNameAddIn = null;
      }
//System.out.println(">>Ask Name:Append"+Runtime.getRuntime().freeMemory());
      FormItem.append(NameField);
      FormItem.addCommand(cAccept);
      FormItem.setCommandListener(this);
      display.setCurrent(FormItem);
//System.out.println("<<Ask Name");

    }


/////////////////////////////////////////////////////

 private List gameMenu(){
  List ListItem = null;
//    if (canvas.isLoadSaveSupporting())
          ListItem = new List(saMSG[GameMenuTitle],List.IMPLICIT,new String[]{
						       saMSG[ResumeGame],
						       saMSG[LoadSaveCMD],
						       saMSG[HelpCMD],
						       saMSG[OptionCMD],
						       saMSG[EndGameCMD]
                                                     },null);
/*    else
          ListItem = new List(saMSG[GameMenuTitle],List.IMPLICIT,new String[]{
						       saMSG[ResumeGame],
						       saMSG[HelpCMD],
						       saMSG[OptionCMD],
						       saMSG[EndGameCMD]
                                                     },null);
*/
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    return ListItem;
 }

 private List newGame(){
    List ListItem = null;
    ListItem = new List(saMSG[NewGameTitle],List.IMPLICIT,new String[]{
                                                       saMSG[LevelName1],
						       saMSG[LevelName2],
						       saMSG[LevelName3]
                                                     },null);
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    return ListItem;
 }

 private List mLoad(){
    List ListItem = null;
    ListItem = new List(saMSG[LoadTitle],List.IMPLICIT,new String[]{
                                                       saMSG[LoadTitle],
						       saMSG[DeleTitle],
						       saMSG[ClearAllTitle]
                                                     },null);
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    return ListItem;
 }

 private List mLanguages(){
    List ListItem = null;
    String []Langs = MSG.getLanguageNames();
    if (Langs != null && Langs.length>1) {
          LanguagesAvailable = true;
          ListItem = new List(saMSG[Language],List.IMPLICIT,Langs,null);
          ListItem.addCommand(cBack);
          ListItem.setCommandListener(this);
    }
    return ListItem;
 }

 private void mOption(){
    List ListItem;
    int idx = 0;
    int []ndx = new int[6];
    if (LanguagesAvailable)
         { LngIdx = idx;  ndx[idx++]=Language; }
//    if ((canvas.getPhoneSupportFlags()&PhoneStub.SUPPORT_LIGHT)>0)
         { LghtIdx = idx; ndx[idx++]=Light; }// else _Light=false;
//    if ((canvas.getPhoneSupportFlags()&PhoneStub.SUPPORT_SOUND)>0)
//         { SndIdx = idx;  ndx[idx++]=Sound; }// else
                                                         _Sound=false;
//    if ((canvas.getPhoneSupportFlags()&PhoneStub.SUPPORT_VIBRATION)>0)
//         { VbrIdx = idx;  ndx[idx++]=Vibration; } else
	                                             _Vibra = false;

    String []tmp = new String[idx];
    for (int i = 0;i<idx;i++)
       tmp[i]=saMSG[ndx[i]];

          ListItem = new List(saMSG[OptionTitle],List.IMPLICIT, tmp,null);
          ListItem.addCommand(cBack);
          ListItem.setCommandListener(this);
    display.setCurrent(ListItem);
 }

 private void sLight(){
    List ListItem = new List(saMSG[Light],List.EXCLUSIVE,new String[]{saMSG[OnCMD],saMSG[OffCMD]},null);
    ListItem.setSelectedIndex((_Light?0:1),true);
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    display.setCurrent(ListItem);
 }

 private void sSound(){
    List ListItem = new List(saMSG[Sound],List.EXCLUSIVE,new String[]{saMSG[OnCMD],saMSG[OffCMD]},null);
    ListItem.setSelectedIndex((_Sound?0:1),true);
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    display.setCurrent(ListItem);
 }
/*
 private void sVibra(){
    List ListItem = new List(saMSG[Vibration],List.EXCLUSIVE,new String[]{saMSG[OnCMD],saMSG[OffCMD]},null);
    ListItem.setSelectedIndex((_Vibra?0:1),true);
    ListItem.addCommand(cBack);
    ListItem.setCommandListener(this);
    display.setCurrent(ListItem);
 }
*/
 private Alert SaveOk(){ return new Alert(saMSG[SaveTitle],saMSG[GameSaved],null,AlertType.INFO);}
 private Alert SaveFailed(){ return new Alert(saMSG[SaveTitle],saMSG[GameNotSaved],null,AlertType.ERROR);}
 private Alert LoadFailed(){ return new Alert(saMSG[LoadTitle],saMSG[GameNotLoaded],null,AlertType.ERROR);}

 private List mainMenu(){
   List ListItem = null;
   Runtime.getRuntime().gc();
   if(SavedGames.GetNumRecords()<=0) {
	                RecordsAvailable = false;
			ListItem = new List(saMSG[MainMenuTitle],List.IMPLICIT, new String []{
						       saMSG[NewGameCMD],
						       saMSG[HelpCMD],
						       saMSG[OptionCMD],
						       saMSG[HighCMD],
						       saMSG[CreditsCMD],
						       saMSG[ExitCMD]
                                                     },null);
   } else {
	                RecordsAvailable = true;
			ListItem = new List(saMSG[MainMenuTitle],List.IMPLICIT, new String []{
						       saMSG[NewGameCMD],
						       saMSG[LoadTitle],
						       saMSG[HelpCMD],
						       saMSG[OptionCMD],
						       saMSG[HighCMD],
						       saMSG[CreditsCMD],
						       saMSG[ExitCMD]
                                                     },null);
   }
   ListItem.addCommand(cBack);
   ListItem.setCommandListener(this);
   return ListItem;
 }
 private List mLoadSave(){
   subMenu = -1;
   menuState = sGAMELOADSAVE;
   List ListItem = null;
   Runtime.getRuntime().gc();
   if(SavedGames.GetNumRecords()<=0) {
	                RecordsAvailable = false;
                        ListItem = new List (saMSG[LoadSaveTitle], List.IMPLICIT,new String[]{
                                                       saMSG[SaveTitle],
						       saMSG[DeleTitle],
						       saMSG[ClearAllTitle]
					             },null);
                        ListItem.addCommand(cBack);
                        ListItem.setCommandListener(this);
   } else {
			RecordsAvailable = false;
                        ListItem = new List (saMSG[LoadSaveTitle], List.IMPLICIT,new String[]{
                                                       saMSG[LoadTitle],
                                                       saMSG[SaveTitle],
						       saMSG[DeleTitle],
						       saMSG[ClearAllTitle]
					             },null);
   }
   ListItem.addCommand(cBack);
   ListItem.setCommandListener(this);
   return ListItem;
 }


    private void showHelp(){
       Runtime.getRuntime().gc();
       Alert a = new Alert(saMSG[HelpTitle]);
             a.setString(saMSG[HelpContent]);
             a.setTimeout(-2);
       display.setCurrent(a);
       a = null;
    }

    private void showCredits(){
       Runtime.getRuntime().gc();
       Alert a = new Alert(saMSG[CreditsTitle]);
	     a.setString(saMSG[CreditsContent]);
             a.setTimeout(-2);
        try {
               Image image = Image.createImage(saMSG[CreditsImage]);
               a.setImage(image);
	       image = null;
        } catch(Exception e) {}
       display.setCurrent(a);
       a = null;
    }

/////////////////////////////////////////////////////


    private void showSubMenu(int menu, int submenu){
      menuState = menu;
      subMenu = submenu;
      switch (subMenu){
	  case subLOAD     : ShowList(saMSG[LoadTitle],false); break;
	  case subSAVE     : ShowList(saMSG[SaveTitle],true); break;
	  case subDELETE   : ShowList(saMSG[DeleTitle],false); break;
	  case subLANGUAGES: display.setCurrent(mLanguages()); break;
	  case subLIGHT    : sLight(); break;
	  case subSOUND    : sSound(); break;
//	  case subVIBRA    : sVibra(); break;
	  case subEXIT     : AreYouSure(saMSG[ExitGameAsk]); break;
	  case subENDGAME  : AreYouSure(saMSG[EndGameAsk]); break;
	  case subCLEARALL : AreYouSure(saMSG[ClearAllAsk]); break;
	  case subNEWGAME  : display.setCurrent(newGame()); break;
	default:
          switch (menuState) {
               case sMAIN:         display.setCurrent(mainMenu()); break;
               case sGAME:         display.setCurrent(gameMenu()); break;
               case sMAINLOAD:     display.setCurrent(mLoad()); break;
               case sGAMELOADSAVE: display.setCurrent(mLoadSave()); break;
//	       case sGAMELOAD:     display.setCurrent(mLoad); break;
//	       case sGAMESAVE:     display.setCurrent(mSave); break;
               case sMAINOPTIONS:
               case sGAMEOPTIONS:  mOption(); break;
	                 default:  display.setCurrent(canvas);
          }
      }
    }



 private void rebuildMenuItems(){
    RecordsAvailable = true;

  // commands
    cBack = new Command(saMSG[BackCMD], Command.BACK, 10);
    cNo = new Command(saMSG[NoCMD], Command.BACK, 12);
    cYes = new Command(saMSG[YesCMD], Command.OK, 11);
    cAccept = new Command(saMSG[AcceptCMD], Command.OK, 11);
    cClear = new Command(saMSG[ClearAllTitle], Command.SCREEN, 99);

  // text constants
    canvas.loadString  = saMSG[Loading];
    canvas.YourScore  = saMSG[YourScore]+" ";
    canvas.NewStage  = saMSG[NewStage]+" ";
//    canvas.unpacking = saMSG[WaitPlease];
 }





    /**
     * From CommandListener interface for operate with command linked with canvas and forms
     * @param cmd command
     * @param scr Displayable object from where it was caused
     */

    public void commandAction(Command cmd, Displayable scr) {

//       lastIndex = -1;
       Runtime.getRuntime().gc();
       if ( scr instanceof List) lastIndex = ((List)scr).getSelectedIndex();

	       if (scr instanceof Form && cmd == cAccept){
	         PlayerName=NameField.getString();
		 NameField = null;
		 switch (subMenu) {
		   case subSAVE:  if(Save(SourceRec)) {
		                         display.setCurrent(SaveOk(),mLoadSave());
		                  }
				  else display.setCurrent(SaveFailed(),mLoadSave());
				break;
		   case subHIGHSCORE: {
		                    addHighScore(PlayerName, _gamescore);
				    canvas.endGame();
				    display.setCurrent(canvas);
				    saveHighScore();
				    //showHighScore();
		                  }
				break;
//		   case qHighScore: if(AddHighScore()) display.setCurrent(SaveOk,HighScoreDisplay());
//				  else display.setCurrent(SaveFailed,HighScoreDisplay());
//				break;
		   default: display.setCurrent(SaveFailed(),gameMenu());
		 }
	       } else
       if (cmd.getCommandType() == Command.BACK/* || cmd == cNo*/){
//	System.out.println("z called"+(n++)+", CMD:"+cmd+", SCR:"+scr);
//           AskPlayerName = null;

	   if(subMenu == subHIGHSCORE || subMenu == subNEWGAME)subMenu = -1;
	   else
	   if (menuState>sGAME) {
//	     if (menuState == sGAMELOAD || menuState == sGAMESAVE) menuState = sGAMELOADSAVE;
//	      else
	         switch(subMenu){

		    case subLANGUAGES: saveOptions(); break;
		    case subLIGHT:    setLight((lastIndex==0),true);break;
		    case subSOUND:    setSound((lastIndex==0),true);break;
		    case subVIBRA:    setVibra((lastIndex==0),true);break;
		    case subLOAD:
		    case subSAVE:
		    case subDELETE:
		    case subCLEARALL: break;
		    default:          menuState &= 1;
	         }
	     subMenu = -1;
	   } else {menuState = -1; subMenu = -1;}
	   showSubMenu(menuState, subMenu);
       } else
          if (cmd == cYes){
	         switch(subMenu){
		    case subSAVE    : //AskName();
		                      if(Save(SourceRec))
		                         display.setCurrent(SaveOk(),mLoadSave());
				       else display.setCurrent(SaveFailed(),mLoadSave());
		                      break;
		    case subDELETE  : Delete(SourceRec); showSubMenu(menuState, -1);break;
		    case subCLEARALL: {
		                        //SavedGames.EraseStorage();
			                //SavedGames.OpenStorage();
					try{
					  while(SavedGames.GetNumRecords()>0)SavedGames.Delete(1);
					}catch(RMSException e){}
				        showSubMenu(menuState, -1);
				      }
				      break;
		    case subENDGAME : canvas.endGame(); showSubMenu(-1,-1); break;
		    case subEXIT    : killApp();
	         }
          } else {
	       if (subMenu == subNEWGAME) {
		   canvas.loading();
		   showSubMenu(-1,-1);
		   canvas.init(lastIndex,null);
	       }
	       else
	       if (scr == ListOfSavedGames){
		SourceRec = lastIndex;
		 switch (subMenu){
		    case subLOAD  : if(Load(SourceRec)) {canvas.gameLoaded(); display.setCurrent(canvas);}
				      else display.setCurrent(LoadFailed(),(menuState==sMAIN?mLoad():mLoadSave()));break;
		    case subSAVE  : if (SourceRec<SavedGames.GetNumRecords()){
				        PlayerName=((List)scr).getString(SourceRec);
				        AreYouSure(saMSG[RewriteAsk]);
				    } else {
				         if(PlayerName==null)PlayerName=saMSG[DefPlayerName];
				         AskName();
				    } break;
		    case subDELETE: AreYouSure(saMSG[DeleAsk]);
		 }
	       } else
	       if (subMenu == subLANGUAGES){
		  _lang = lastIndex;
		  canvas.LanguageCallBack = true;
	       } else{
                if (menuState == sMAIN && subMenu<0){
		 if (SavedGames.GetNumRecords()<=0 && lastIndex>0) lastIndex++;
                 switch (lastIndex) {
		   case 0:
		           showSubMenu(sMAIN,subNEWGAME);
		           //canvas.loading();
		           //showSubMenu(-1,-1);
		           //canvas.init(0,null);
		           break;
		   case 1: showSubMenu(sMAINLOAD,-1);break;
		   case 2: showHelp();break;
		   case 3: showSubMenu(sMAINOPTIONS,-1);break;
		   case 4: showHighScore();break;
		   case 5: showCredits();break;
		   case 6: showSubMenu(menuState,subEXIT);break;
                 }
                }  else
                if (menuState == sGAME && subMenu<0){
//		 if(!canvas.isLoadSaveSupporting() && lastIndex>0)lastIndex++;
                 switch (lastIndex) {
		   case 0: showSubMenu(-1,-1);break;
		   case 1: showSubMenu(sGAMELOADSAVE,-1);break;
		   case 2: showHelp();break;
		   case 3: showSubMenu(sGAMEOPTIONS,-1);break;
		   case 4: showSubMenu(menuState,subENDGAME);break;
                 }
                } else
                if (menuState == sMAINLOAD)
                 switch (lastIndex) {
		   case 0: showSubMenu(menuState,subLOAD);break;
		   case 1: showSubMenu(menuState,subDELETE);break;
		   case 2: showSubMenu(menuState,subCLEARALL);break;
                 }  else
                if (menuState == sGAMELOADSAVE){
		 if (SavedGames.GetNumRecords()<=0) lastIndex++;
                 switch (lastIndex) {
		   case 0: showSubMenu(menuState,subLOAD);break;
		   case 1: showSubMenu(menuState,subSAVE);break;
		   case 2: showSubMenu(menuState,subDELETE);break;
		   case 3: showSubMenu(menuState,subCLEARALL);break;
                 }
                }  else
                if (menuState == sMAINOPTIONS || menuState == sGAMEOPTIONS){
		 if (lastIndex == LngIdx)showSubMenu(menuState,subLANGUAGES);
		 else if (lastIndex == LghtIdx)showSubMenu(menuState,subLIGHT);
		 else if (lastIndex == SndIdx)showSubMenu(menuState,subSOUND);
		 else if (lastIndex == VbrIdx)showSubMenu(menuState,subVIBRA);
/*
                 switch (lastIndex) {
		   case LngIdx: showSubMenu(menuState,subLANGUAGES);break;
		   case LghtIdx: showSubMenu(menuState,subLIGHT);break;
		   case SndIdx: showSubMenu(menuState,subSOUND);break;
		   case VbrIdx: showSubMenu(menuState,subVIBRA);break;
                 }
*/
                } else
		if (cmd == cClear){
		        for (int i=0;i<11;i++){
                            _scores[i]=0;
                            _names[i]=null;
		        }
		     saveHighScore();
		     ShowMainMenu();
		}

	       }
	    }
    }

   public void NewItemLoaded(int n){
     canvas.nextItemLoaded(n);
   }

  public void ShowGameMenu(){
       showSubMenu(sGAME,-1);
  }
  public void ShowMainMenu(){
       showSubMenu(sMAIN,-1);
  }
  public void LoadLanguage(){
      int status = canvas.gameStatus;
      ListOfSavedGames = null;
      Runtime.getRuntime().gc();
      canvas.loading(60);
      display.setCurrent(canvas);
      try{
        MSG.setLanguage(lastIndex,canvas);
	saMSG = MSG.getCurrentStringArray();
      } catch (Exception e){
	//System.out.println("Load Excptn");
      }
//if(saMSG==null)System.out.println("No resources loaded");
//if(saMSG.length<60)System.out.println("Not enough quantity of necessary resources (need:66, really loaded:"+saMSG.length+")");
      rebuildMenuItems();
      display.setCurrent(mLanguages());
      if (SavedGames != null) SavedGames.setDefName(saMSG[NewRecordContent]);
      Runtime.getRuntime().gc();
      canvas.gameStatus = status;
      canvas.repaint();//custom_paint();
  }

  public void ShowQuitMenu(boolean local){
    if (local)
       showSubMenu(menuState,subENDGAME);
      else
         showSubMenu(menuState,subEXIT);
  }

   public void newScore(int n){
       if ((ACSENDING_HIGHSCORE && n>_scores[9]) || (!ACSENDING_HIGHSCORE && n<_scores[9])){
         subMenu = subHIGHSCORE;
	 _gamescore = n;
	 AskNameAddIn = saMSG[YourScore]+"   "+_gamescore;
	 AskName();
       }
   }

 private static byte ExitCMD = 0;
 private static byte OptionCMD = 1;
 private static byte NewGameCMD = 2;
 private static byte LoadSaveCMD = 3;
 private static byte HighCMD = 4;
 private static byte EndGameCMD = 5;
 private static byte HelpCMD = 6;
 private static byte CreditsCMD = 7;
 private static byte CancelCMD = 8;
 private static byte BackCMD = 9;
 private static byte YesCMD = 10;
 private static byte NoCMD = 11;
 private static byte OptionTitle = 12;
 private static byte CreditsTitle = 13;
 private static byte HelpTitle = 14;
 private static byte LoadSaveTitle = 15;
 private static byte HighTitle = 16;
 private static byte SaveTitle = 17;
 private static byte LoadTitle = 18;
 private static byte DeleTitle = 19;
 private static byte ClearAllTitle = 20;
 private static byte AreYouSureTitle = 21;
 private static byte PlayerNameTitle = 22;
 private static byte NewGameTitle = 23;
 private static byte PlayerNameAsk = 24;
 private static byte DeleAsk = 25;
 private static byte RewriteAsk = 26;
 private static byte ClearAllAsk = 27;
 private static byte CreditsContent = 28;
 private static byte CreditsImage = 29;
 private static byte HelpContent = 30;
 private static byte ResumeGame = 31;
 private static byte NewRecordContent = 32;
 private static byte HighContent = 33;
 private static byte NoRecordsAvailableContent = 34;
 private static byte GameSaved = 35;
 private static byte GameNotSaved = 36;
 private static byte GameNotLoaded = 37;
 private static byte RMSCantOpen = 38;
 private static byte RMSHasNoRoom = 39;
 private static byte RMSBaseNotOpen = 40;
 private static byte RMSEmptyList = 41;
 private static byte RMSMaxRecordsLimit = 42;
 private static byte RMSOStreamFail = 43;
 private static byte RMSReadingFail = 44;
 private static byte RMSCantDelete = 45;
 private static byte DefPlayerName = 46;
 private static byte DefSaveName = 47;
 private static byte Language = 48;
 private static byte OnCMD = 49;
 private static byte OffCMD = 50;
 private static byte LevelName1 = 51;
 private static byte LevelName2 = 52;
 private static byte LevelName3 = 53;
 private static byte Light = 54;
 private static byte Sound = 55;
 private static byte Vibration = 56;
 private static byte Reverse = 57;
 private static byte EndGameAsk = 58;
 private static byte ExitGameAsk = 59;
 private static byte AutoSaveName = 60;
 private static byte YourScore = 61;
 private static byte Loading = 62;
 private static byte AcceptCMD = 63;
 private static byte MainMenuTitle = 64;
 private static byte GameMenuTitle = 65;
 private static byte NewStage = 66;
 private static byte SpecialSection = 67;
}
