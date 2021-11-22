
/**
 * <p>Title: Startup</p>
 * <p>Description: Startup games engine</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Akumiitti</p>
 * @version 1.0
 * @comment damned Nokia's developers
 *
 * NOTE: several bugs counted by us are described below, please see it and take care
 *
 * 1. Dead SOFTKEY#s - when you try to use some UI components the SOFTEKEYs became unusable, damned Nokia's developers!
 * 2. Don't use the "display.setCurrent(Alert a, Displayable d)" sequence , it cause deadlock when you touch anykey at shown alert
 * 3. Use your own RMS allocation for prventing some builtin mistakes
 *    */


import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.List;
import com.igormaznitsa.midp.*;

public class startup extends MIDlet implements CommandListener, Runnable {

    static Display display;
//    Displayable prevDisplay;
    GameArea canvas;

    LanguageBlock MSG;
    String []saMSG;
    GameDataStorage GDS;

    private final static String LANG_RES = "/res/langs.bin";
    private final static String NameMask = "              ";
    private int _maxsaveddata;
    private final static int _maxcharinname = NameMask.length();
    private final static int _maxsavedgames = 5;
    private final static int _maxrecoedsintoplist = 10;
    private int _lang = -1;


    private boolean RecordsAvailable = false, LanguagesAvailable = true,  ReverseOption = false;
    public boolean _Sound = true ,_Light = true ,_Reverse = true, _Vibra = true;
    private String PlayerName = null;
    private String SourceName = null;
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
//			     subVIBRA = 6,
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

    private int _gamescore = 0;

    private void ErrorAlert(String title,int n){

	 Alert a=new Alert(title,saMSG[n],null,AlertType.WARNING);
	 a.setTimeout(-2);
	 display.setCurrent(a);

    }

    private void DataFlush(){
      if(GDS != null && GDS.isChanged())
         try{
            GDS.flush();
         } catch (Exception e){display.setCurrent(new Alert("Can't store data",e.getMessage(),null,AlertType.ERROR));}
    }

    void Delete(String Name){
      if(GDS!=null)
      try {
           GDS.removeSavedGame(Name);
      } catch (IOException e){
	display.setCurrent(new Alert(saMSG[DeleTitle],e.getMessage(),null,AlertType.ERROR));
      }
    }

    void RemoveAllGames(){
      if(GDS!=null)
      try {
           GDS.removeAllSavedGames();
      } catch (IOException e){
	display.setCurrent(new Alert(saMSG[ClearAllTitle],e.getMessage(),null,AlertType.ERROR));
      }
    }
    void ClearTopList(){
      if(GDS!=null){
           GDS.clearTopList();
	   DataFlush();
      }
    }


    public void setLight(boolean v){
      canvas.setScreenLight(v);
      _Light = v;
      if(GDS!=null){
	GDS.setBooleanOption(GameDataStorage.FIELD_LIGHT,v);
	DataFlush();
      }
    }
    public void setSound(boolean v){
      _Sound = v;
      if(GDS!=null){
	GDS.setBooleanOption(GameDataStorage.FIELD_SOUND,v);
	DataFlush();
      }
    }
    public void setVibra(boolean v){
      _Vibra = v;
      if(GDS!=null){
	GDS.setBooleanOption(GameDataStorage.FIELD_VIBRATOR,v);
        if (canvas!=null && v) canvas.keyReverse = true;
           else canvas.keyReverse = false;
	DataFlush();
      }
    }

    boolean Save(String Name){
      boolean ret = false;
      Runtime.getRuntime().gc();
      if(GDS!=null)
      try {
        ByteArrayOutputStream baos=new ByteArrayOutputStream(_maxsaveddata);
	String tmp=PlayerName;
           canvas.client_sb.saveGameState(new DataOutputStream(baos));
	   GDS.saveGameDataForName(tmp,baos.toByteArray());
	   DataFlush();
	   baos = null;
	   ret = true;
      } catch (Exception e){/*System.out.println("Save: CANVAS->CLIENT->SAVE, IOEXCEPTION rec#"+n+"\n"+e.getMessage());*/}
      Runtime.getRuntime().gc();
      return ret;
    }
    boolean Load(String Name){
      boolean ret = false;
      Runtime.getRuntime().gc();
      try {
	  canvas.client_sb.loadGameState(GDS.getSavedGameForName(Name));
	  ret = true;
      } catch (IOException e){/*System.out.println("Load: CANVAS->CLIENT->LOAD, IOEXCEPTION rec#"+n+"\n"+e.getMessage()); return false;*/}
      Runtime.getRuntime().gc();
      return ret;
    }
  public void LoadLanguage(){
      int status = canvas.gameStatus;
      canvas.loading(60);
      display.setCurrent(canvas);
      ListOfSavedGames = null;
      Runtime.getRuntime().gc();
      try{
        MSG.setLanguage(lastIndex,canvas);
	saMSG = MSG.getCurrentStringArray();
      } catch (Exception e){/*System.out.println("Load Excptn");*/}
//if(saMSG==null)System.out.println("No resources loaded");
//if(saMSG.length<60)System.out.println("Amount of loaded resources isn't enough (need:66, really loaded:"+saMSG.length+")");
      rebuildMenuItems();

      if(GDS!=null) {
	GDS.setIntOption(GameDataStorage.FIELD_LANGUAGE,_lang);
	DataFlush();
      }

      Runtime.getRuntime().gc();
      display.setCurrent(mLanguages());
      canvas.gameStatus = status;
      canvas.repaint(); //custom_paint();
  }

   private void showHighScore(){
         String msg = saMSG[HighContent];
         StringBuffer sb = new StringBuffer();
	 subMenu = subHIGHSCORE;
	 String tmp;
	 for(int i=0;i<_maxsavedgames;i++){
	   tmp = GDS.getTopListNameForIndex(i);
	   if (tmp==null)break;
           sb.append(i+1);
           sb.append(". ");
           sb.append(tmp);
           sb.append("\n      ");
	   sb.append(GDS.getTopListScoresForIndex(i));
	   sb.append("\n");
	 }
	 if (sb.length()>0) msg = sb.toString();
	 sb = null;
	 tmp = null;
	 Form a=new Form(saMSG[HighTitle]);
	 a.append(msg);
	 a.addCommand(cClear);
	 a.addCommand(cBack);
	 a.setCommandListener(this);
	 display.setCurrent(a);
	 a=null;
	 Runtime.getRuntime().gc();
   }

    public String[] getSavedGamesList(boolean AddNew)
    {
        String []as = null;
	if(GDS!=null){
	  String []as_savedgames = GDS.getSavedGameNamesArray();
          int i = GDS.getSavedGamesCount();
	  if(AddNew && i<GDS.MAX_SAVEDRECORDS){
            as = new String[i + 1];
            as[i] = saMSG[NewRecordContent];
          } else
          if(i > 0)
            as = new String[i];
          else
            return null;
          for(int j = 0; i > 0; j++)
            if(as_savedgames[j] != null)
                as[--i] = as_savedgames[j];
	}
        return as;
    }


//---------------------------------8<-------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////

    public startup() {
	display = Display.getDisplay(this);         // capture display
    }


    /**
     * Starts the MIDlet from here
     * @throws MIDletStateChangeException
     */
    public void startApp() throws MIDletStateChangeException {
     try{
      Runtime.getRuntime().gc();
        canvas = new GameArea(this);                // initialising our visualisation engine
	_maxsaveddata = 1100;//canvas.client_sb.getMaxSizeOfSavedGameBlock();
	display.setCurrent(canvas);
	startme();
     } catch(Exception e){e.printStackTrace(); System.exit(-1);}
    }

    public void pauseApp() {
      if(canvas.isShown() && canvas.autosaveCheck()) ShowGameMenu();
      notifyPaused();
    }

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
	if (canvas!= null && GDS!=null){
	  if (/*canvas.client_sb.isLoadSaveSupporting() && */canvas.autosaveCheck())
	     {
		 Save(saMSG[AutoSaveName]);
	         DataFlush();
//	System.out.println(saMSG[AutoSaveName]);
	     }
	  canvas.endGame();
	  canvas.destroy();
	}
	notifyDestroyed();
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////    Обработка слушателем команд

    void ShowList(String title, boolean AddNew){
      try {
	 ListOfSavedGames = null;
	 String []a = getSavedGamesList(AddNew);
 	 ListOfSavedGames = new List(title,List.IMPLICIT,getSavedGamesList(AddNew),null);
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
         { LghtIdx = idx; ndx[idx++]=Light; } //else _Light=false;
//    if ((canvas.getPhoneSupportFlags()&PhoneStub.SUPPORT_SOUND)>0)
//         { SndIdx = idx;  ndx[idx++]=Sound; } //else
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

 private Alert SaveOk(){ return new Alert(saMSG[SaveTitle],saMSG[GameSaved],null,AlertType.CONFIRMATION);}
 private Alert SaveFailed(){ return new Alert(saMSG[SaveTitle],saMSG[GameNotSaved],null,AlertType.ERROR);}
 private Alert LoadFailed(){ return new Alert(saMSG[LoadTitle],saMSG[GameNotLoaded],null,AlertType.ERROR);}

 private List mainMenu(){
   List ListItem = null;
   Runtime.getRuntime().gc();
   if(GDS.getSavedGamesCount()<=0) {
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
   if(GDS.getSavedGamesCount()<=0) {
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
	                 default:  startme();
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
		   case subSAVE:
		                  if (GDS.convertSavedNameToIndex(PlayerName)>=0){
				        AreYouSure(saMSG[RewriteAsk]);
				  } else {
//				    display.setCurrent(Save(PlayerName)?SaveOk():SaveFailed(),mLoadSave());
/*!!*/

		                     a = Save(PlayerName)?SaveOk():SaveFailed();
				     f = mLoadSave();
				     startme();

				  }
				break;
		   case subHIGHSCORE: {
		                    GDS.addRecordToTopList(PlayerName, _gamescore);
				    canvas.endGame();
				    startme();
				    DataFlush();

		                  }
				break;
//		   case qHighScore: if(AddHighScore()) display.setCurrent(SaveOk,HighScoreDisplay());
//				  else display.setCurrent(SaveFailed,HighScoreDisplay());
//				break;
		   default:
//		           display.setCurrent(SaveFailed(),gameMenu());

		            f = gameMenu();
		            a = SaveFailed();
			    startme();

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

		    case subLANGUAGES:  break;
		    case subLIGHT:    setLight((lastIndex==0));break;
		    case subSOUND:    setSound((lastIndex==0));break;
//		    case subVIBRA:    setVibra((lastIndex==0));break;
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
//		                      display.setCurrent(Save(SourceName)?SaveOk():SaveFailed(),mLoadSave());

		                      a = Save(SourceName)?SaveOk():SaveFailed();
				      f = mLoadSave();
				      startme();

		                      break;
		    case subDELETE  : Delete(SourceName); showSubMenu(menuState, -1);break;
		    case subCLEARALL: {
		                        RemoveAllGames();
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
		SourceName = ((List)scr).getString(lastIndex);
		 switch (subMenu){
		    case subLOAD  : if(Load(SourceName)) {canvas.gameLoaded(); startme();/*display.setCurrent(canvas);*/}
				      else {
//					     display.setCurrent(LoadFailed(),(menuState==sMAIN?mLoad():mLoadSave()));
					     f = (menuState==sMAIN?mLoad():mLoadSave()); a = LoadFailed();
					   }
				    break;
		    case subSAVE  : if (GDS.convertSavedNameToIndex(SourceName)>=0){
				        PlayerName=SourceName;
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
		 if (GDS.getSavedGamesCount()<=0 && lastIndex>0) lastIndex++;
                 switch (lastIndex) {
		   case 0:
		           //showSubMenu(sMAIN,subNEWGAME);
		           canvas.loading();
		           showSubMenu(-1,-1);
		           canvas.init(0,null);
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
		// if(!canvas.isLoadSaveSupporting() && lastIndex>0)lastIndex++;
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
		 if (GDS.getSavedGamesCount()<=0) lastIndex++;
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
//		 else if (lastIndex == VbrIdx)showSubMenu(menuState,subVIBRA);
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
		     ClearTopList();
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

  public void ShowQuitMenu(boolean local){
    if (local)
       showSubMenu(menuState,subENDGAME);
      else
         showSubMenu(menuState,subEXIT);
  }


   public void newScore(int n){
//       if ((ACSENDING_HIGHSCORE && n>_scores[9]) || (!ACSENDING_HIGHSCORE && n<_scores[9])){
       if (GDS!= null && GDS.getMinScoreInTopList()<n){
         subMenu = subHIGHSCORE;
	 _gamescore = n;
	 AskNameAddIn = saMSG[YourScore]+"   "+_gamescore;
	 AskName();
       }
   }

//============= Nokia kb bug fix-code ==================================
//Alert aa=null;
Displayable f = null;
Alert a = null;
Thread me=null;
Command ok = new Command(" ",Command.OK,10);
//Command bk = new Command(">>",Command.EXIT,11);
boolean JustStarted = true;
 public void startme(){

  if(me==null){
    me=new Thread(this);
    me.start();
  }
 }
  public void run(){
    if(JustStarted){
              JustStarted = false;
	      while (!canvas.isShown()) Thread.yield();
	      try {
                canvas.startIt();
                GDS = new GameDataStorage(canvas.client_sb.getGameID(),_maxsaveddata,_maxcharinname,_maxsavedgames,_maxrecoedsintoplist);
                setLight(GDS.getBooleanOption(GameDataStorage.FIELD_LIGHT));
                setSound(GDS.getBooleanOption(GameDataStorage.FIELD_SOUND));
                setVibra(GDS.getBooleanOption(GameDataStorage.FIELD_VIBRATOR));
                _lang = GDS.getIntOption(GameDataStorage.FIELD_LANGUAGE);

                MSG = new LanguageBlock(LANG_RES,_lang,canvas);

                if (MSG!=null)
                {
	          saMSG = MSG.getCurrentStringArray();
                  rebuildMenuItems();
                }
                String []Langs = MSG.getLanguageNames();
                LanguagesAvailable = (Langs != null && Langs.length>1);

                canvas.gameStatus=canvas.titleStatus;
                canvas.repaint(); //custom_paint();
	      } catch(Exception e){e.printStackTrace(); System.exit(-1);}
              me = null;
    } else
    if(a==null) {
     f = new Form("please wait");
     f.addCommand(ok);
     display.setCurrent(f);
     while(f!=null && !f.isShown())
      try{  me.sleep(10);  }catch(Exception e){}
     f.removeCommand(ok);
     display.setCurrent(canvas);
     me = null;
     f=null;
    }
      else
      {
        if (f==null) f = display.getCurrent();
//	else display.setCurrent(f);
//        while(!f.isShown())me.yield();
//	a.setTimeout(1000);

//        int tt = a.getTimeout();
        long tt = 2000;
	a.setTimeout((int)tt/*Alert.FOREVER*/);
        display.setCurrent(a);
	while(!a.isShown())me.yield();
	tt+=System.currentTimeMillis();
        while(a.isShown() && tt>System.currentTimeMillis())me.yield();
        display.setCurrent(f);
	f = null;
        a = null;
        me = null;
    }
    Runtime.getRuntime().gc();
  }

 private static final byte SaveTitle = 0;
 private static final byte LevelName1 = 1;
 private static final byte LevelName2 = 2;
 private static final byte LevelName3 = 3;
 private static final byte AutoSaveName = 4;
 private static final byte DefSaveName = 5;
 private static final byte HighContent = 6;
 private static final byte YourScore = 7;
 private static final byte OffCMD = 8;
 private static final byte NewGameCMD = 9;
 private static final byte GameNotLoaded = 10;
 private static final byte HelpCMD = 11;
 private static final byte CancelCMD = 12;
 private static final byte RMSEmptyList = 13;
 private static final byte HighCMD = 14;
 private static final byte YesCMD = 15;
 private static final byte Language = 16;
 private static final byte Reverse = 17;
 private static final byte PlayerNameTitle = 18;
 private static final byte DefPlayerName = 19;
 private static final byte GameSaved = 20;
 private static final byte LoadSaveCMD = 21;
 private static final byte RMSOStreamFail = 22;
 private static final byte NoRecordsAvailableContent = 23;
 private static final byte DeleAsk = 24;
 private static final byte HelpTitle = 25;
 private static final byte LoadTitle = 26;
 private static final byte MainMenuTitle = 27;
 private static final byte RMSHasNoRoom = 28;
 private static final byte HelpContent = 29;
 private static final byte RMSReadingFail = 30;
 private static final byte ExitGameAsk = 31;
 private static final byte OptionCMD = 32;
 private static final byte ClearAllAsk = 33;
 private static final byte GameMenuTitle = 34;
 private static final byte CreditsImage = 35;
 private static final byte AcceptCMD = 36;
 private static final byte CreditsTitle = 37;
 private static final byte RMSBaseNotOpen = 38;
 private static final byte Light = 39;
 private static final byte NoCMD = 40;
 private static final byte OnCMD = 41;
 private static final byte Sound = 42;
 private static final byte ExitCMD = 43;
 private static final byte AreYouSureTitle = 44;
 private static final byte CreditsContent = 45;
 private static final byte RMSCantDelete = 46;
 private static final byte PlayerNameAsk = 47;
 private static final byte RMSCantOpen = 48;
 private static final byte SpecialSection = 49;
 private static final byte OptionTitle = 50;
 private static final byte HighTitle = 51;
 private static final byte BackCMD = 52;
 private static final byte RewriteAsk = 53;
 private static final byte NewRecordContent = 54;
 private static final byte CreditsCMD = 55;
 private static final byte NewStage = 56;
 private static final byte ResumeGame = 57;
 private static final byte NewGameTitle = 58;
 private static final byte DeleTitle = 59;
 private static final byte ClearAllTitle = 60;
 private static final byte GameNotSaved = 61;
 private static final byte RMSMaxRecordsLimit = 62;
 private static final byte Vibration = 63;
 private static final byte Loading = 64;
 private static final byte EndGameAsk = 65;
 private static final byte EndGameCMD = 66;
 private static final byte LoadSaveTitle = 67;
}
