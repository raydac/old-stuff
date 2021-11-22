import knight.*;

import javax.microedition.lcdui.*;
import com.itx.mbgame.GameObject;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable, LoadListener, GameActionListener {
//long accuracy=0;

  public static GameArea Instance;

  private final static int WIDTH_LIMIT = 96;
  private final static int HEIGHT_LIMIT = 65;

/*=============================*/

    private static final int KEY_MENU = -7;
    private static final int KEY_CANCEL = -6;
    private static final int KEY_ACCEPT = -7;
    private static final int KEY_BACK = -10;

    public void setScreenLight(boolean state)
    {
      DeviceControl.setLights(0, state?100:0);
    }
    public void activateVibrator(int n)
    {
      //if(midlet._Vibra)
      // DeviceControl.startVibra(10,n);
    }

protected Sound [] sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
//       synchronized(sounds){
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               sounds[i].stop();
//       }
//      isFirst = false;
    }
int SoundPlayTimeGap = 250;
long sound_timemark = 0;
    public void playSound(int n,int loop){
      if(sounds!=null && sounds.length>0 && midlet._Sound){
//	stopAllMelodies();
//       synchronized(sounds){
        if(soundIsPlaying() && System.currentTimeMillis()-sound_timemark<SoundPlayTimeGap) return;
	sound_timemark = System.currentTimeMillis();
	stopAllMelodies();
	sounds[n].play(loop);
       }
//      }
    }
    public boolean soundIsPlaying(){
      if(sounds!=null)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               return true;
      return false;
    }

/*=============================*/
  private final static int MENU_KEY = KEY_MENU;
  private final static int END_KEY = KEY_BACK;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 25; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 40; //x200ms = 7 seconds , game over / finished sleep time


  private final static int ZONE_WIDTH  = 74;
  private final static int ZONE_HEIGHT = 64;
  private final static int EDGES = 5;

  private final static int HALFZONE_WIDTH = ZONE_WIDTH/2;

// non transient map elements
  private final static int NON_EXISTENT = 0;        //used like marker on drop down elements, for reduce visualization's latency
  private final static int WALL = 1;
  private final static int DOOR = 2;
  private final static int EXIT = 3;

// transient elements (ground elements)
  private final static int KEY_ON_FLOOR = 4;
  private final static int CHEST = 5;
  private final static int BUTTON = 6;
  private final static int BUTTON_PRESSED = 7;
  private final static int EL_SOURCE = 8;
  private final static int EL_BOLT = 9;
  private final static int INDEFINITE = 15;         //special element, indicate empty place or something nonsignificant

  private final static int TRANSIENT_BORDER = KEY_ON_FLOOR;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;

  private Image []Elements;
  private byte [][] ByteArray;

  private Image MenuIcon;
  private Image Buffer=null;
  private Graphics gBuffer=null;
//  private Image BackScreen=null;
//  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font,font_inv;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected Knight client_sb;
  protected game_PMR client_pmr;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private boolean useDblBuffer = true;//!this.isDoubleBuffered();

  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
			    newStage = 3,
                            playGame = 4,
			    Crashed = 5,
			    Finished = 6,
			    gameOver = 7;
			         // ni  ts  dp  ns  pg  cr  fi  go
  private int [] stageSleepTime = {100,100,100,100,120,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

//  private final static int BkgCOLOR = 0x000061; //for Nokia
  private int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int scr2;//, center_x, center_y, start_x,start_y;
  private final static int CELL_SIZE = 5;
  private int HORIZONTAL_CELLS = 15,VERTICAL_CELLS = 10;
  private int WALL_COLOR,DOOR_COLOR,MAP_BORDERCOLOR;
  private int SCREENS_BACK_COLOR;

  private int CBACK_FAR,CBACK_NEAR,BORDERCOLOR;
  private int [] gradient;

  private Image map;
  private Graphics gmap;
  private int map_x, map_y;

  int pressedKey;

  private final int []y_offset = {56,55,50,47,44,43};
  private final int [][]wear_offset = {{3,29},{16,23},{25,25}};
  private final int [][]inv_places = {{36,64}, // man
                                      {22,30}, // armor and shield
				      {32,10}, // weapon
				      {13,13}};// keys

                                      //                               ************************
  private int []wall_width;           // widths of recessive walls     *  perspective cell's  *
  private int []wall_height;          // their heights                 *      geometry        *
  private int []proj_wall_width;      // widths /*of recessive walls with theirs*/ perspective projection
  private int [][]map_fragment;       // fastdraw element
  private int [][]borderness;     // -//-
  private int farthest_edge_lenght;   // size of the farthest edge
  private int center_element;

  private int FLOOR_COLOR;// = 0x737373;
  /**Construct the displayable*/
  public GameArea(startup m) {
    Instance = this;
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

    VisWidth = Math.min(scrwidth,WIDTH_LIMIT);
    VisHeight = Math.min(scrheight,HEIGHT_LIMIT);

    client_sb = new Knight(VisWidth, VisHeight, this);
    client_pmr = new game_PMR();
    if (useDblBuffer)
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
	FLOOR_COLOR = 0x737373;
	WALL_COLOR = 0x000030;
	DOOR_COLOR = 0x008000;
	MAP_BORDERCOLOR = 0x800080;
	BACKGROUND_COLOR = 0x0D004C;
	CBACK_FAR = 0x0;//0x362f2d;
	CBACK_NEAR = 0x736357;
	BORDERCOLOR = 0xcaa374;
	SCREENS_BACK_COLOR = CBACK_FAR;

    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
	WALL_COLOR = 0x000000;
	DOOR_COLOR = 0x000000;
	MAP_BORDERCOLOR = 0x000000;
	BACKGROUND_COLOR = 0xffffff;

	CBACK_FAR = 0x000000;
	CBACK_NEAR = 0xffffff;
	BORDERCOLOR = 0x000000;

	SCREENS_BACK_COLOR = 0xffffff;
    }


    // create gradient

    int amount = y_offset.length;
    gradient = new int[amount];
    int rs =(CBACK_NEAR>>>16)&0xff,rd=((CBACK_FAR>>>16)&0xff)-rs;
    int gs =(CBACK_NEAR>>>8)&0xff,gd=((CBACK_FAR>>>8)&0xff) - gs;
    int bs =(CBACK_NEAR)&0xff,bd=((CBACK_FAR)&0xff)-bs;
    for (int i = 0; i<amount; i++){
        gradient[i] =((rs+rd*i/amount)<<16) +
	             ((gs+gd*i/amount)<<8)  +
		       bs+bd*i/amount;
    }
    scr2 = VisWidth>>1;
    isFirst = true;

    //rescale inventory screen
    for(int i = 0;i<inv_places.length;i++){
      inv_places[i][0] = inv_places[i][0]*VisWidth/101;
      inv_places[i][1] = inv_places[i][1]*VisHeight/64;
    }

  }

///////////////////////////////////////////////////////////////////////////////////

    long loadtime=0;

    public void nextItemLoaded(int li){
       LoadingNow=Math.min(LoadingNow+1,LoadingTotal);
       if(loadtime < System.currentTimeMillis() || LoadingNow >= LoadingTotal) {
	 loadtime = System.currentTimeMillis()+100; // 0,1sec slice
         repaint();
       }
    }

    public void startIt() {
     loading(67+TOTAL_IMAGES_NUMBER);

     try {

	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock(/*"/res/images.bin",*/this);
           sounds = ib.LoadSound();
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];
	   font = new drawDigits(GetImage(IMG_NFONT));
	   Image img = GetImage(IMG_FONT_INV);
	   font_inv = new drawDigits(img);
	   font_inv.fontWidth = img.getWidth()/7;
	   ib = null;

           Runtime.getRuntime().gc();
	   int minima = VisWidth;
	   wall_width = new int[EDGES];
	   wall_height = new int[EDGES];
	   proj_wall_width = new int[EDGES];
	   for(int i = 0; i<EDGES;i++){
	     img = GetImage(IMG_WALL_H01+i);
	     wall_width[i] = img.getWidth();
	     wall_height[i] = img.getHeight();
	     proj_wall_width[i] = /*wall_width[i]*/+GetImage(IMG_WALL01+i).getWidth();
	     if(minima>wall_width[i])minima=wall_width[i];
	   }
	   if (minima<=0)minima=1;

           farthest_edge_lenght = (VisWidth-minima+1)/2;                  // centering, int
           farthest_edge_lenght = (farthest_edge_lenght+minima-1)/minima; // getting amount of all of walls in edge
	         center_element = farthest_edge_lenght;// + 1;
           farthest_edge_lenght = farthest_edge_lenght*2 + 1;             // (include partially-visible)

           Runtime.getRuntime().gc();


       if (thread==null){
          thread=new Thread(this);
	  thread.start();
//	  thread.setPriority(Thread.MIN_PRIORITY);
       }
     } catch(Exception e) {
        System.out.println("Can't read images");
	midlet.killApp();
//       e.printStackTrace();
     }
     Runtime.getRuntime().gc();
    }


    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////

    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
       repaint();
    }

    public void init(int level, String Picture) {
       stage = 0;
       blink=false;
       ticks = 0;
       pressedKey = 0;
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(demoLevel);
	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(client_pmr);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
//           client_sb.nextGameStep(client_pmr);
	   gameStatus=newStage;
	}
	bufferChanged=true;
	DoubleBuffer(gBuffer);
	map = null;
	repaint();
    }

    public void loading(){
       gameStatus=notInitialized;
       ticks = 0;
       repaint();
    }


    public void endGame() {
	gameStatus=titleStatus;
        ticks =0;
	repaint();
    }


    public void showNotify(){
       isShown = true;
    }

    public void hideNotify(){
       isShown = false;
       stopAllMelodies();
       direct = 0;
       pressedKey = 0;
       CleanSpace();
    }

    protected void keyRepeated(int keyCode) {keyPressed(keyCode);}

    /**
     * Handle a single key event.
     * The LEFT, RIGHT, UP, and DOWN keys are used to
     * move the scroller within the Board.
     * Other keys are ignored and have no effect.
     * Repaint the screen on every action key.
     */
    protected void keyPressed(int keyCode) {
            switch (gameStatus) {
	      case notInitialized: if (keyCode==END_KEY) midlet.killApp();
	                           break;
	      case Finished:
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME){
		                       if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
			               if(keyCode == MENU_KEY || keyCode == END_KEY)
				            ticks -= 5+1;
	                     }
	      case demoPlay:
              case titleStatus : {
		                    stopAllMelodies();
		                    switch (keyCode) {
				      case MENU_KEY:
				                midlet.ShowMainMenu();
				                break;
				      case END_KEY:  midlet.ShowQuitMenu(false);
				                break;
				      default:
				              if (gameStatus == demoPlay || ticks>=TOTAL_OBSERVING_TIME){
	                                        gameStatus=titleStatus;
			                        ticks=0;
			                        blink=false;
					        repaint();
				              }
		                    }
				    //midlet.commandAction(midlet.newGameCommand,this);
                               } break;
              case playGame :  {

				  if(client_sb.SELECT_STATE == Knight.ISELECT_INV) {
				    direct = keyCode-Canvas.KEY_NUM0;
				    pressedKey = direct;
		                    if(direct>0 && direct<8) return;
				  }
	                          switch (keyCode) {
			             case MENU_KEY:  midlet.ShowGameMenu(); break;
				     case END_KEY:  midlet.ShowQuitMenu(true); break;
				     case Canvas.KEY_NUM2 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = game_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = game_PMR.BUTTON_FIRE; break;
				     case Canvas.KEY_NUM6 : direct = game_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = game_PMR.BUTTON_DOWN; break;

				     case Canvas.KEY_STAR : direct = game_PMR.BUTTON_INVENT_INV; break;
				     case Canvas.KEY_NUM0 : direct = game_PMR.BUTTON_INVENT_CHR; break;
				     case Canvas.KEY_POUND : direct = game_PMR.BUTTON_INVENT_MAP; break;

				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = game_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = game_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = game_PMR.BUTTON_UP; break;
					       case Canvas.DOWN : direct = game_PMR.BUTTON_DOWN; break;
				               case Canvas.FIRE : direct = game_PMR.BUTTON_FIRE; break;
					           default: direct=game_PMR.BUTTON_NONE;
				            }
	                          }
				  pressedKey = direct;
				  direct = 0;
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=game_PMR.BUTTON_NONE;
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      while(animation) {
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
	                if(isFirst){
			  isFirst = false;
			  playSound(SND_THEME_OTT,0);
			  //PlayMelody(SND_JBELLS_OTT);
			  ticks = 0;
	                }
			if (ticks<80) ticks++;
			 else
			  if(soundIsPlaying())ticks-=10;
			  else
			  {
			   Runtime.getRuntime().gc();
			   ticks=0;
			   stopAllMelodies();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
			   if (client_sb.bulgar.getDirection()==GameObject.DIR_STOP && (ticks%3 == 0))
			     client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_DOWN/*4*/);
		           client_sb.nextGameStep(client_pmr);
//			   updateBackScreen();

			   if (client_sb.i_PlayerState!=Gamelet.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			       isFirst = true;
			   }
	                   bufferChanged=true;
	                   DoubleBuffer(gBuffer);
			   repaint();
//			   serviceRepaints();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
				pressedKey = 0;
		                direct=game_PMR.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_pmr.i_Value = direct;
                                client_sb.nextGameStep(client_pmr);
//				repaintBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>25){
			        gameStatus=playGame;
				map = null;
				bufferChanged=true;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
                           client_pmr.i_Value = pressedKey;
			   pressedKey = game_PMR.BUTTON_NONE;
                           client_sb.nextGameStep(client_pmr);

//			   if(!is_painting)
			     //updateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>=Stages.TOTAL_STAGES)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
								      break;
			         }
			   bufferChanged=true;
	                   DoubleBuffer(gBuffer);
			   repaint();
//			   serviceRepaints();
			  } break;
	    case Crashed:
			   stopAllMelodies();
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>20){
			     ticks = 0;
			     blink = false;
		             direct=game_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=game_PMR.BUTTON_NONE;
	                           client_pmr.i_Value = direct;
                                   client_sb.nextGameStep(client_pmr);
//				   repaintBackScreen();
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           // PlayMelody(SND_APPEARANCE_OTT);
			      }
			   }
	                   bufferChanged=true;
	                   DoubleBuffer(gBuffer);
			   repaint();
			   break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			     if(soundIsPlaying())ticks=FINAL_PICTURE_OBSERVING_TIME-10;
			      else
			        midlet.newScore(client_sb.getPlayerScore());
			   bufferChanged=true;
			   repaint();
			   break;
 	 }


//	serviceRepaints();

        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

//          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
//	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
//          else
          if(workDelay<=0) workDelay=10;
             Thread.sleep(workDelay);

	} catch (Exception e) {}

//	serviceRepaints();
        Runtime.getRuntime().gc();
	workDelay = System.currentTimeMillis();
//        if(is_painting) serviceRepaints();

      }
    }

//    public void gameAction(int actionID){}
    public void gameAction(int actionID)
    {
      switch(actionID)
      {
        case 0:
          playSound(SND_GOLD_OTT,1);
          break;
        case 1:
          playSound(SND_ITEM_OTT,1);
          break;
        case 2:
          playSound(SND_DEATH_OTT,1);
          break;
        case 3:
          playSound(SND_KILL_OTT,1);
          break;
        case 4:
          playSound(SND_LEVELUP_OTT,1);
          break;
        case 5:
          playSound(SND_BUTTON_OTT,1);
          break;
        case 6:
          playSound(SND_STAGE_OTT,1);
          break;
        case 7:
          activateVibrator(500);
          break;
      }
    }

    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){}

/*
    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(~color);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }
*/

/////////////////////////////////////////////////////////////
/////          INVENTORY
/////////////////////////////////////////////////////////////

    private Image getItem(int n){
      if(n>=50 && n<69){
         n = IMG_KEY_INV01 + (n-50)%5;
      } else
      switch(n){
	case Stages.SHORT_DAGGER: n = IMG_DAGGER_INV; break;
	case Stages.SHORT_SWORD: n = IMG_SWORD02_INV; break;
	case Stages.LONG_SWORD:  n = IMG_SWORD01_INV; break;
	case Stages.MAGIC_SWORD: n = IMG_SWORD03_INV; break;

	case Stages.SCALE_ARMOR:  n = IMG_SPLINTMAIL_INV; break;
	case Stages.PLATE_ARMOR:  n = IMG_FULLPLATE_INV; break;

	case Stages.PLATE_SHIELD:  n = IMG_SHIELD02_INV; break;
	 default:
	          return null;
      }
      return GetImage(n);
    }
    private void drawItemPlace(Graphics gBuffer,int x, int y, int w, int h,int n){
       // draw empty place
       gBuffer.setColor(BORDERCOLOR);
       gBuffer.drawRect(x,y,w,h);
       Image img = getItem(client_sb.keys[n]);
       if(img!=null)
                gBuffer.drawImage(img,x+((w-img.getWidth())>>1),y+((h-img.getHeight())>>1),0);
       if(n<7)
                font_inv.drawDigits(gBuffer,x+1,y+1,1,n);
    }


/////////////////////////////////////////////////////////////
/////          Labyrinth
/////////////////////////////////////////////////////////////

/*
        ---- Map format ----
        01-09   , wall
        10-19   , vertical door  [ door 10 , door 11 , ...
        20-29   , horizontal door
        50-69   , key   [ key 30 its key for door 10 , key 31 its key for door 11 , ...
        70-89   , floor button   [ button 50 can open door 10 , button 51 can open door 11 , ...
        90      , Pushed button
        110-116 , Items
        117-127 , Gold
        150-151 , Electroshok
*/

    private int getCellID(int x, int y){
      if(x<0 || y<0 ||  x>=client_sb.level[0].length || y >= client_sb.level.length) return INDEFINITE;

              int cellID = client_sb.level[y][x];	       // get cell ID
         // ordered by rarity
	      if(cellID==0)                    return INDEFINITE;     // fast return
	      if(cellID >= 1  && cellID <= 9)  return WALL;
	      if(cellID >= 10 && cellID <= 29) return DOOR;
	      if(cellID >= 70 && cellID <= 89) return BUTTON;
	                      if(cellID == 90) return BUTTON_PRESSED; // button pressed

	      if(cellID==Stages.ELECTROSHOCK)  return EL_SOURCE;      // source only
           if(cellID==Stages.ELECTROSHOCK_CLS) return EL_BOLT;        // source and lightning
	      if(cellID >= 50 && cellID <= 69) return KEY_ON_FLOOR;
	      if(cellID >= 110 && cellID <= 127 || cellID==Stages.TREASURE)
	                                       return CHEST;          // something in box
	      if(cellID==Stages.EXIT)          return EXIT;           // exit

	return INDEFINITE;
    }

    private void drawGroundElement(int cellID, int x, int y, int h, int w,int dist, Graphics gBuffer){
            Image img;
	    switch (cellID){
// non transient elements
               case WALL:
	                          {

	                            img = GetImage(IMG_WALL_H01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight();
	                          } break;
               case DOOR:
	                          {
	                            img = GetImage(IMG_DOOR_H01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight();
	                          } break;
               case EXIT:
	                          {
	                            img = GetImage(IMG_EXIT_H01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight();
	                          } break;

// transient elements (ground elements)
               case KEY_ON_FLOOR:
	                          {
				    img = GetImage(IMG_KEY01+dist);
		                    //x+=img.getWidth();
		                    y-=(img.getHeight()>>1);
	                          } break;
               case CHEST:
	                          {
				    img = GetImage(IMG_BOX01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight()>>1;
	                          } break;
               case BUTTON:
	                          {
		                    img = GetImage(IMG_BUTTON01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight()>>1;
	                          } break;
               case BUTTON_PRESSED:
	                          {
		                    img = GetImage((dist<2)?IMG_BUTTON_DOWN:(IMG_BUTTON01+dist));
		                    x+=(w-img.getWidth())>>1;
		                    y-=img.getHeight()>>1;
	                          } break;
               case EL_SOURCE:
	                          {
	                            if(dist<2){
		                       img = GetImage(IMG_LIGHTING01 + (ticks&1));
		                       gBuffer.drawImage(img,x+((w-img.getWidth())>>1),y-h,0);
	                            }
				    img = GetImage(IMG_SHOCKER01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=h+(img.getWidth()>>1);
	                          } break;
               case EL_BOLT:
	                          {
				    img = GetImage(IMG_SHOCKER01+dist);
		                    x+=(w-img.getWidth())>>1;
		                    y-=h+(img.getWidth()>>1);
	                          } break;
		   default: return;
	    }
	    gBuffer.drawImage(img,x,y,0);

    }


    private void drawBorder(int cellID, int x, int y, int dist, Graphics gBuffer, boolean is_left){
       Image img;
	           switch (cellID){
                       case WALL: cellID = IMG_WALL01+dist+1;  break;
                     //case DOOR:
                       default:   cellID = IMG_WALL_DOOR01+dist+1;  break;
	           }
		 if(is_left){
		  cellID += EDGES;
		  img = GetImage(cellID);
		  gBuffer.drawImage(img,x,y-img.getHeight(),0);
		 }
		   else {
		     img = GetImage(cellID);
	             gBuffer.drawImage(img,x-img.getWidth(),y-img.getHeight(),0);
		   }
    }



    private void drawMonster(Guard guard, int x, int y, int h, int w,int dist, Graphics gBuffer){
            Image img;
	    if(guard.inAttack && dist<1) img = GetImage(IMG_ENEMY1_AT01+((ticks>>1)%3));
	       else img = GetImage(IMG_ENEMY1_ST01+dist);
	    x += w>>1;
	    y -= img.getHeight();
	    gBuffer.drawImage(img,x-(img.getWidth()>>1),y,0);

	    dist = guard.getState(); // lost hp

	    if (dist>0){
	      y+=15;
	      img = GetImage(IMG_SPOT);
	      gBuffer.drawImage(img,x-(img.getWidth()>>1),y+((font.fontHeight-img.getHeight())>>1),0);
	      font.drawDigits(gBuffer,x-font.fontWidth,y,2,dist);
	    }
    }



    private void scanMapLeft(int finish_x,int start_x,int map_x,int map_y,int dx,int dy,int xx,int yy,int dist){
/*
      String s = " ";
      for (int i = 0;i<dist;i++)s+="|";
      if(dist>=EDGES) {System.out.println(s+"*"); return;}
      System.out.println("+ "+s+" "+finish_x+"<-"+start_x);
*/
      if(dist>=EDGES) return;
      int w = wall_width[dist];
      int cx = HALFZONE_WIDTH +(w>>1);     // start of zero (center) cell
      int p = (cx-start_x)/w;              // offset from centerline
      int last_visible_x = INDEFINITE;     // visible mark

      int trg_x = map_x-dx*p;
      int trg_y = map_y-dy*p;

      cx = cx - p*w;                       // aligned offset
      p = center_element - p;              // position at map_fragment
      int next_cellID = getCellID(trg_x,trg_y);
      int cellID;
      while(cx>finish_x && p>=0){
	cellID = next_cellID;
	next_cellID = getCellID(trg_x-dx,trg_y-dy);
	map_fragment[dist][p] = cellID;
	if(cellID>=TRANSIENT_BORDER){
	  if(next_cellID<TRANSIENT_BORDER && dist<EDGES-1)borderness[dist][p]=next_cellID;
	  if(last_visible_x == INDEFINITE)last_visible_x=cx;
	} else
        if(last_visible_x != INDEFINITE){
           scanMapLeft(Math.max(cx,finish_x),last_visible_x,map_x-xx,map_y-yy,dx,dy,xx,yy,dist+1);
	   last_visible_x = INDEFINITE;
        }
	p--;
	cx-=w;
	trg_x-=dx;
	trg_y-=dy;
      }
      if(last_visible_x != INDEFINITE)
           scanMapLeft(finish_x,last_visible_x,map_x-xx,map_y-yy,dx,dy,xx,yy,dist+1);
    }
    private void scanMapRight(int start_x,int finish_x,int map_x,int map_y,int dx,int dy,int xx,int yy,int dist){
/*
      String s = " ";
      for (int i = 0;i<dist;i++)s+="|";
      if(dist>=EDGES) {System.out.println(s+"*"); return;}
      System.out.println("+ "+s+" "+start_x+"->"+finish_x);
*/
      if(dist>=EDGES) return;
      int w = wall_width[dist];
      int cx = HALFZONE_WIDTH -(w>>1);     // start of zero (center) cell
      int p = (start_x-cx)/w;              // offset from centerline
      int last_visible_x = INDEFINITE;     // visible mark

      int trg_x = map_x+dx*p;
      int trg_y = map_y+dy*p;

      cx = cx + p*w;                       // aligned offset
      p = center_element + p;              // position at map_fragment
      int next_cellID = getCellID(trg_x,trg_y);
      int cellID;
      int len = map_fragment[dist].length;
      while(cx<finish_x && p<len){
	cellID = next_cellID;
	next_cellID = getCellID(trg_x+dx,trg_y+dy);
	map_fragment[dist][p] = cellID;
	if(cellID>=TRANSIENT_BORDER){
	  if(next_cellID<TRANSIENT_BORDER && dist<EDGES-1)borderness[dist][p+1]=next_cellID;
	  if(last_visible_x == INDEFINITE)last_visible_x=cx;
	} else
        if(last_visible_x != INDEFINITE){
           scanMapRight(last_visible_x,Math.min(cx,finish_x),map_x-xx,map_y-yy,dx,dy,xx,yy,dist+1);
	   last_visible_x = INDEFINITE;
        }
	p++;
	cx+=w;
	trg_x+=dx;
	trg_y+=dy;
      }
      if(last_visible_x != INDEFINITE)
           scanMapRight(last_visible_x,finish_x,map_x-xx,map_y-yy,dx,dy,xx,yy,dist+1);
    }

    protected void DoubleBuffer(Graphics gBuffer){
      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,VisWidth,VisHeight); //see Nokia's defect list
      }

////////////////////////////////////////////////

        Image img = null;
/**********************/
//        gBuffer.setColor(BACKGROUND_COLOR);gBuffer.fillRect(0,0,VisWidth,VisHeight);
	gBuffer.setColor(0x0);

//	DirectGraphics dg = DirectUtils.getDirectGraphics(gBuffer);

        if(client_sb.SELECT_STATE == Knight.GAME_SCREEN) {
	map = null;
//////////////////////////////////////////////////////////////////////////////////////////////
// Gamezone
           gBuffer.setColor(BORDERCOLOR);
	          gBuffer.fillRect(ZONE_WIDTH,0,VisWidth-ZONE_WIDTH,VisHeight);
//	          gBuffer.fillRect(0,ZONE_HEIGHT,VisWidth,VisHeight-ZONE_HEIGHT);

    gBuffer.setClip(0,0,ZONE_WIDTH,ZONE_HEIGHT); //see Nokia's defect list

	   int px,py;
	   int prev_y0=ZONE_HEIGHT,prev_y1=0;
/*
	   for(px = 0;px<gradient.length-1;px++)
	   {
             gBuffer.setColor(gradient[px]);
	          py = y_offset[px];
	          gBuffer.fillRect(0,py,ZONE_WIDTH,prev_y0-py);
		  prev_y0 = py;
		  py-=wall_height[px];
	          gBuffer.fillRect(0,prev_y1,ZONE_WIDTH,py-prev_y1);
		  prev_y1 = py;
	   }

           gBuffer.setColor(gradient[px]);
	   gBuffer.fillRect(0,prev_y1,ZONE_WIDTH,prev_y0-prev_y1);
*/
	   GameObject knight = client_sb.bulgar;
	   int dir = knight.getDirection();
	   if(dir == GameObject.DIR_STOP) dir = knight.vDir;     // get player direction

	   px = knight.X();                                      // player coordinates
	   py = knight.Y();

	   int dx=0,dy,xx,yy,w;

	   img = GetImage(IMG_BKG01 +((px+py)&1));               // draw background
	   dy = ZONE_WIDTH/img.getWidth() +1;
	   for (int i=0; i<dy; i++) {
	      gBuffer.drawImage(img,dx,0,0);
	      dx+=img.getWidth();
	   }


       // intersurface equation
       //============================================
       // Xg =Xp-XX*y-DX*{center_element}+DX*x
       // Yg =Yp-YY*y-DY*{center_element}+DY*x
       // Where: Xp,Yp - player's coordinates
       //        XX,YY - norm.vector
       //        DX,DY - perp.vector

	   switch (dir){
	      case GameObject.DIR_LEFT : dx = 0; dy =-1; xx = 1; yy = 0; break;
	      case GameObject.DIR_RIGHT: dx = 0; dy = 1; xx =-1; yy = 0; break;
	      case GameObject.DIR_DOWN : dx =-1; dy = 0; xx = 0; yy =-1; break;
	      //case GameObject.DIR_UP :
	                       default : dx = 1; dy = 0; xx = 0; yy = 1; break;
	}

	map_fragment = new int[EDGES][farthest_edge_lenght]; // clean up shortmap
	borderness = new int[EDGES][farthest_edge_lenght+1];

        // filllout fragments

	  // test on left and right borders
	  boolean left_border = getCellID(px-dx,py-dy)<TRANSIENT_BORDER;
	  boolean right_border = getCellID(px+dx,py+dy)<TRANSIENT_BORDER;
	  int border_width = (ZONE_WIDTH-wall_width[0])>>1;

	  // visibility scan
	  px -= xx;
	  py -= yy;
	  scanMapLeft(left_border?border_width:0,HALFZONE_WIDTH,px,py,dx,dy,xx,yy,0);
	  scanMapRight(HALFZONE_WIDTH,ZONE_WIDTH-(right_border?border_width:0),px,py,dx,dy,xx,yy,0);

	  // layout guards
	  int i,x,y,j;
	  Guard guard;
	  px = knight.X();                                  // player coordinates
	  py = knight.Y();

	  for(i = 0; i<client_sb.guards.length;i++){
	    guard = client_sb.guards[i];
	     if(guard.isActive()){
             // calculate transition
	        if(dx==0){                // dx,yy =>0
					  // Xg = Xp-XX*y-0*{center_element}+0*x
                                          // Yg = Yp-0*y-DY*{center_element}+DY*x
					  // ...
					  // y =(Xp-Xg)/XX
                                          // x =(Yg-Yp+DY*{center_element})/DY
	           x = (guard.Y()-py+dy*(center_element+0))/dy;
	           y = (px-guard.X())/xx-1;
	        } else {
	                                  // dy,xx =>0
					  // Xg =Xp-0*y-DX*{center_element}+DX*x
                                          // Yg =Yp-YY*y-0*{center_element}+0*x
					  // ...
					  // x =(Xg-Xp+DX*{center_element})/DX
                                          // y =(Yp-Yg)/YY
	           x = (guard.X()-px+dx*(center_element+0))/dx;
	           y = (py-guard.Y())/yy-1;
	        }
		if(x>=0 && x<farthest_edge_lenght && y>=0 && y<EDGES &&
		   ((j=map_fragment[y][x])&0xff)>=TRANSIENT_BORDER){
	            map_fragment[y][x] = (j&0xff)+(((j&~0xff)|(i+1))<<8);   //single guard
//	           System.out.println("      "+client_sb.guards[i].X()+"-"+px+":"+client_sb.guards[i].Y()+"-"+py+" ["+x+":"+y+"]");
		}
	     }
          }



	// draw it
	int [] line;
	int [] brdr;
        int cellID,h;                                // identification of zone and height
//        boolean prev_is_wall;                        // and of visibility, also height
	for (int dist = EDGES-1; dist>=0;dist--){

	     line = map_fragment[dist];                          // shortlink to scanline
	     brdr = borderness[dist];

	     w = wall_width[dist];  xx = (ZONE_WIDTH - w)>>1;  // geometrize cells
	     h = wall_height[dist]; yy = y_offset[dist+1];

	     cellID = (i = line[center_element])&0xff;
	     if (cellID>=TRANSIENT_BORDER) {
	       if ((j=brdr[center_element])>0) drawBorder(j,xx,yy,dist,gBuffer,true);
	       if ((j=brdr[center_element+1])>0) drawBorder(j,xx+w,yy,dist,gBuffer,false);
	     }

	     // draw center
	     drawGroundElement(cellID,xx,yy,h,w,dist,gBuffer);
	     while((i>>>=8)>0) {
		drawMonster(client_sb.guards[--i&0xff],xx,yy,h,w,dist,gBuffer);
	     }

	     // scan left
	     dx = xx;
	     px = center_element-1;
	     while(dx>0 && px>=0){
	       dx -= w;
	       cellID = (i = line[px])&0xff;
	       if (cellID>=TRANSIENT_BORDER && (j=brdr[px])>0){
		 drawBorder(j,dx,yy,dist,gBuffer,true);
	       }
	       drawGroundElement(cellID,dx,yy,h,w,dist,gBuffer);
	       while((i>>>=8)>0) {
		 drawMonster(client_sb.guards[--i&0xff],dx,yy,h,w,dist,gBuffer);
	       }
	       px--;
	     }

	     // scan right
	     dx = xx+w;
	     px = center_element+1;
	     while(dx<ZONE_WIDTH && px<farthest_edge_lenght){
	       cellID = (i = line[px])&0xff;
	       if (cellID>=TRANSIENT_BORDER && (j=brdr[px+1])>0){
		 drawBorder(j,dx+w,yy,dist,gBuffer,false);
	       }
	       drawGroundElement(cellID,dx,yy,h,w,dist,gBuffer);
	       while((i>>>=8)>0) {
		drawMonster(client_sb.guards[--i&0xff],dx,yy,h,w,dist,gBuffer);
	       }
	       dx += w;
	       px++;
	     }
        }

        // border
	int cy = y_offset[0];
	if(left_border) {
	   img = GetImage(IMG_WALL01 + EDGES);
	   gBuffer.drawImage(img,border_width-img.getWidth(),cy-img.getHeight(),0);
	}
	if(right_border) {
	  img = GetImage(IMG_WALL01);
	  gBuffer.drawImage(img,ZONE_WIDTH-img.getWidth(),cy-img.getHeight(),0);
	}

	  if(client_sb.playerAttackBegin>0){
	      i = (ticks>>1)&7;
	      if (i<3){
	        Image g = GetImage(IMG_STRIKE01+((i+1)>>1));
	        gBuffer.drawImage(g,(ZONE_WIDTH - g.getWidth())>>1,ZONE_HEIGHT - g.getHeight(),0);
	      }
	  }

        if(client_sb.text_delay > 0 && client_sb.TEXT_LEVEL_UP) {
	   img = GetImage(IMG_LEV_UP);
	   x = HALFZONE_WIDTH-(img.getWidth()>>1);
	   dy = Math.abs(client_sb.text_delay-(client_sb.MAXTEXTDELAY>>1))-23;
	   if(dy>=0){
	    gBuffer.drawImage(img,x,(ZONE_HEIGHT>>1)+5+(dy<<2),0);
	   } else
	    gBuffer.drawImage(img,x,(ZONE_HEIGHT>>1)+5,0);

        }

        gBuffer.setClip(0,0,VisWidth,VisHeight); //see Nokia's defect list

//////////////////////////////////////////////////////////////////////////////////////////////
//Information Panel
        // Direction arrows
        drawPlayerDungeonArrow(gBuffer);
	// face
	w = VisWidth - ZONE_WIDTH;
	int c = ZONE_WIDTH + (w>>1);
	h = 3;
	img = GetImage((gameStatus == Crashed)?IMG_PLAYER_DEAD:IMG_PLAYER_M);
	gBuffer.drawImage(img,c - (img.getWidth()>>1),h,0);

//  Потеря HP
//  Наш чар:
//  (рисовать звездочку наверно на его морде справа)
//  mainlosthp если 0 - то ничего , если >0 то это количество HP которое он потерял
//  ~ через пол секунды автоматом убивается
	      i = client_sb.mainlosthp;
	      if (i>0){
		y = h+((img.getHeight()-font.fontHeight)>>1);
		Image spot = GetImage(IMG_SPOT);
		gBuffer.drawImage(spot,c-(spot.getWidth()>>1),y+((font.fontHeight-spot.getHeight())>>1),0);
		font.drawDigits(gBuffer,c - font.fontWidth,y,2,i);
	      }


	h+=img.getHeight()+2;
	font.drawDigits(gBuffer,c-((font.fontWidth*3)>>1),h,3,client_sb.hp);

	h+=font.fontHeight+5;
	img = GetImage(IMG_BOX01);
	gBuffer.drawImage(img,c - (img.getWidth()>>1),h,0);

	h+=img.getHeight()+2;
	font.drawDigits(gBuffer,c-((font.fontWidth*4)>>1),h,4,client_sb.gold);
//	h+=font.fontHeight+1;
//	font.drawDigits(gBuffer,ZONE_WIDTH+1+img.getWidth(), h +((img.getHeight() - font.fontHeight)>>1),3,client_sb.gold);
/*
      if(gameStatus!=Crashed || blink){
        // Lives
        img = Elements[IMG_HEART];
	   for (int i=0;i<client_sb.lives-1;i++)
              gBuffer.drawImage(img,VisWidth-(i+1)*(img.getWidth()+2)-10,VisHeight-img.getHeight(),0);
      }
*/
/*********************/
        }
	  else
	    switch (client_sb.SELECT_STATE){
	         case Knight.ISELECT_MAP:
		                     {
//				      try{ System.out.print(">");
				       gBuffer.setColor(SCREENS_BACK_COLOR);gBuffer.fillRect(0,0,VisWidth,VisHeight);
				       if(map==null){
					 map = Image.createImage(HORIZONTAL_CELLS*CELL_SIZE+2,VERTICAL_CELLS*CELL_SIZE+2);
					 gmap = map.getGraphics();
				         gmap.setColor(colorness?BORDERCOLOR:0xffffff);gmap.fillRect(0,0,map.getWidth(),map.getHeight());

	                                 int from_x = client_sb.bulgar.X() - (HORIZONTAL_CELLS>>1), from_y = client_sb.bulgar.Y() - (VERTICAL_CELLS>>1);
                                         int to_x=from_x+HORIZONTAL_CELLS, to_y=from_y+VERTICAL_CELLS;
	                                 int basex = 1-CELL_SIZE, _x;
					 int    _y = 1-CELL_SIZE;
	                                 int []lvl_x = client_sb.level[0];
					 int i;

	                                 if(to_y>client_sb.level.length-1) to_y=client_sb.level.length-1;
	                                 if(from_y<0) {_y+=(-from_y)*CELL_SIZE;from_y=0;}
	                                 if(to_x>lvl_x.length-1) to_x=lvl_x.length-1;
	                                 if(from_x<0) {basex+=(-from_x)*CELL_SIZE;from_x=0;}

                                          for (int y = from_y; y <= to_y; y++)
	                                  {
					    _x = basex;
		                            _y+=CELL_SIZE;
		                            lvl_x = client_sb.level[y];
                                            for (int x = from_x; x <= to_x; x++)
                                            {
					        _x+=CELL_SIZE;
						i = lvl_x[x];
						// walls
                                                if(i >= Stages.WALL0 && i <= Stages.WALL2) {
					          gmap.setColor(WALL_COLOR);
						  gmap.fillRect(_x,_y,CELL_SIZE,CELL_SIZE);
                                                } else
						// doors
                                                if(i >= 10 && i <= 29) {
					          gmap.setColor(DOOR_COLOR);
						  gmap.drawLine(_x,_y+CELL_SIZE,_x+CELL_SIZE,_y+2);
						  gmap.drawLine(_x,_y+CELL_SIZE-2,_x+CELL_SIZE,_y);
                                                }
                                            }
	                                  }
					 gmap.setColor(MAP_BORDERCOLOR);
					 gmap.drawRect(0,0,map.getWidth()-1,map.getHeight()-1);

					 img = GetImage(IMG_PLAYER_ICO);
					 map_x = ((VisWidth - img.getWidth())>>1);
					 map_y = ((VisHeight - img.getHeight() +CELL_SIZE)>>1);
				       }
				       gBuffer.drawImage(map,(VisWidth - map.getWidth())>>1,(VisHeight - map.getHeight())>>1,0);
				       if((ticks&1)==0){
                                         drawPlayerMapArrow(gBuffer,map_x,map_y);
					 //img = GetImage(IMG_PLAYER_ICO);
				         //   gBuffer.drawImage(img,map_x,map_y,0);
				       }
//				       System.out.print("<");
//				      } catch(Exception e){e.printStackTrace();}
		                     } break;
	         case Knight.ISELECT_INV:
		                     {
				        map = null;
				         gBuffer.setColor(SCREENS_BACK_COLOR/*CBACK_FAR*/);gBuffer.fillRect(0,0,VisWidth,VisHeight);

					// draws inventory screen
					// draws man
					 img  = GetImage(IMG_MAN_INV);
					 int m_width = inv_places[0][0];
					 int x_offs = (inv_places[0][0] - img.getWidth())>>1;
					 int y_offs = (inv_places[0][1] - img.getHeight())>>1;
				         gBuffer.drawImage(img,x_offs,y_offs,0);

					// draw armor and shield
					int i, w = inv_places[1][0], h = inv_places[1][1], h1 = inv_places[2][1];
					for (i = 0;i<3;i++)
					  drawItemPlace(gBuffer,m_width+i*w,0,w,h,i);

					// draw weapons
					w = inv_places[2][0];
					for (i = 3;i<7;i++)
					  drawItemPlace(gBuffer,m_width+((i+1)&1)*w,h+(i>4?h1:0),w,h1,i);
					h+=h1<<1;

					// draw keys
					w = inv_places[3][0]; h1 = inv_places[3][1];
					for (i = 7;i<12;i++)
					  drawItemPlace(gBuffer,m_width+(i-7)*w,h,w,h1,i);

					for(i=0;i<3; i++){
					  int n;
                                          switch(client_sb.wear[i]){
					    case Stages.SHORT_DAGGER: n = IMG_DAGGER_INV_VERT; break;
	                                    case Stages.SHORT_SWORD: n = IMG_SWORD02_INV_VERT; break;
	                                    case Stages.LONG_SWORD:  n = IMG_SWORD01_INV_VERT; break;
	                                    case Stages.MAGIC_SWORD: n = IMG_SWORD03_INV_VERT; break;

	                                    case Stages.SCALE_ARMOR:  n = IMG_SPLINTMAIL_INV; break;
	                                    case Stages.PLATE_ARMOR:  n = IMG_FULLPLATE_INV; break;

	                                    case Stages.PLATE_SHIELD:  n = IMG_SHIELD02_INV; break;
					          default: continue;
                                          }
					  img = GetImage(n);
					  int x = wear_offset[i][0]+x_offs;
					  int y = wear_offset[i][1]+y_offs;

					  if (i==0) gBuffer.drawImage(img,x-(img.getWidth()>>1),y-img.getHeight(),0);  //siemens
//					  if (i==0) dg.drawImage(img,x-(img.getHeight()>>1),y-img.getWidth(),0,DirectGraphics.ROTATE_270);  //3510i, 7210...
					   else gBuffer.drawImage(img,x-(img.getWidth()>>1),y-(img.getHeight()>>1),0); //shield and armor
					}



		                     } break;
	         case Knight.ISELECT_CHR:
		                     {
				        map = null;
				         gBuffer.setColor(SCREENS_BACK_COLOR/*CBACK_FAR*/);gBuffer.fillRect(0,0,VisWidth,VisHeight);
                                         gBuffer.setColor(0);
					 img = GetImage(IMG_CHAR_MENU);
					 int i = (VisWidth>>1)+3;
					 int j = (VisHeight-img.getHeight())>>1;
					 int step = (img.getHeight()+3)/6;
					 gBuffer.drawImage(img,i-img.getWidth(),j,0);
					 i+=3; j+=(step-font.fontHeight)>>1;
					 font.drawDigits(gBuffer,i,j,4,client_sb.gold); j+=step; //gold
					 font.drawDigits(gBuffer,i,j,2,client_sb.LVL); j+=step;  //level
					 font.drawDigits(gBuffer,i,j,5,client_sb.EXP); j+=step;  //exp
					 font.drawDigits(gBuffer,i,j,2,client_sb.STR); j+=step;//str
					 font.drawDigits(gBuffer,i,j,2,client_sb.DEX); j+=step;//dex
					 font.drawDigits(gBuffer,i,j,2,client_sb.INT);           //int

					 gBuffer.drawRect(4,3,VisWidth-9,VisHeight-7);

		                     } break;
	    }



	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, HALFZONE_WIDTH,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
      if(baseX !=0 || baseY !=0){
        gBuffer.translate(-baseX,-baseY);
        gBuffer.setClip(0,0,scrwidth,scrheight);
      }

	bufferChanged=false;
    }
    public void drawPlayerDungeonArrow(Graphics g)
    {
      int map_x = ZONE_WIDTH/2-7/2 , map_y = 1;
      g.setColor(InkCOLOR);
      int bx = client_sb.bulgar.X() , sx = 0, by = client_sb.bulgar.Y() , sy = 0;

      if(client_sb.bulgar.vDir == GameObject.DIR_UP)sy--;
      if(client_sb.bulgar.vDir == GameObject.DIR_DOWN)sy++;
      if(client_sb.bulgar.vDir == GameObject.DIR_LEFT)sx--;
      if(client_sb.bulgar.vDir == GameObject.DIR_RIGHT)sx++;
      // up
      if(client_sb.level[by+sy][bx+sx] < 1 || client_sb.level[by+sy][bx+sx] > 29)
        drawArrow(g,GameObject.DIR_UP,map_x,map_y);
      // down
      if(client_sb.level[by-sy][bx-sx] < 1 || client_sb.level[by-sy][bx-sx] > 29)
        drawArrow(g,GameObject.DIR_DOWN,map_x,VisHeight-10);

      map_x = 1 ; map_y = VisHeight/2-7/2; sx = 0; sy = 0;
      if(client_sb.bulgar.vDir == GameObject.DIR_UP)sx--;
      if(client_sb.bulgar.vDir == GameObject.DIR_DOWN)sx++;
      if(client_sb.bulgar.vDir == GameObject.DIR_LEFT)sy++;
      if(client_sb.bulgar.vDir == GameObject.DIR_RIGHT)sy--;
      // left
      if(client_sb.level[by+sy][bx+sx] < 1 || client_sb.level[by+sy][bx+sx] > 29)
        drawArrow(g,GameObject.DIR_LEFT,map_x,map_y);
      // right
      if(client_sb.level[by-sy][bx-sx] < 1 || client_sb.level[by-sy][bx-sx] > 29)
        drawArrow(g,GameObject.DIR_RIGHT,ZONE_WIDTH-10,map_y);
    }

    public void drawPlayerMapArrow(Graphics g,int map_x,int map_y)
    {
     GameObject knight = client_sb.bulgar;
     int dir = knight.vDir;
     g.setColor(0x000000);
     drawArrow(g,dir,map_x,map_y);
    }

    public void drawArrow(Graphics g,int dir,int map_x,int map_y)
    {
      int c = g.getColor();
      g.setColor(0xFFFFFF);
      g.fillRect(map_x,map_y,7,7);
      g.setColor(c);
      if(dir == GameObject.DIR_UP)
      {
        //map_x+=1;
        //map_y+=1;
        g.fillRect(map_x+2,map_y+1,3,5);
        g.fillRect(map_x+3,map_y,1,6);
        g.fillRect(map_x+1,map_y+2,5,1);
      }
      if(dir == GameObject.DIR_DOWN)
      {
        //map_x+=1;
        //map_y+=1;
        g.fillRect(map_x+2,map_y+1,3,5);
        g.fillRect(map_x+3,map_y+6,1,1);
        g.fillRect(map_x+1,map_y+4,5,1);
      }
      if(dir == GameObject.DIR_LEFT)
      {
        //map_y+=1;
        g.fillRect(map_x+2,map_y+2,5,3);
        g.fillRect(map_x+1,map_y+3,1,1);
        g.fillRect(map_x+3,map_y+1,1,5);
      }
      if(dir == GameObject.DIR_RIGHT)
      {
        map_x-=1;
        //map_y+=1;
        g.fillRect(map_x+2,map_y+2,5,3);
        g.fillRect(map_x+7,map_y+3,1,1);
        g.fillRect(map_x+5,map_y+1,1,5);
      }
    }

boolean is_painting = false;
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    protected void paint(Graphics g) {
      is_painting = true;
            switch (gameStatus) {
	      case notInitialized:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(LoadingColor);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  g.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
		          g.setColor(LoadingBar);    // drawin' bar
	                  g.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  g.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  g.setFont(f);
		           //     g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
	                  } break;
              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(GetImage(IMG_FP),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                   g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			   g.setColor(InkCOLOR);    // drawin' flyin' text
			   Font f = Font.getDefaultFont();
			   g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                   g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			   g.setFont(f);
	                  } break;
	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
	                    g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			    g.setColor(InkCOLOR);    // drawin' flyin' text
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WIN),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			    }
			    g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    break;

	      //case demoPlay:
              default :   if (useDblBuffer)
			      {
			        if(bufferChanged)
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
			     else
			      {
		                DoubleBuffer(g);
			      }
                              g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
	  is_painting = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       pressedKey = 0;
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
//       client_sb.nextGameStep(client_pmr);
//       repaintBackScreen();
       gameStatus=newStage;
       bufferChanged=true;
       map = null;
       repaint();
    }


private Image GetImage(int n){
       if(Elements[n]!=null)
               return Elements[n];
/*
	if(Runtime.getRuntime().freeMemory()<4000){
	  Runtime.getRuntime().gc();
	  if(Runtime.getRuntime().freeMemory()<4000)CleanSpace();
*/
        return Image.createImage(ByteArray[n], 0, ByteArray[n].length);
}

public void CleanSpace(){
 map = null;
 if(ByteArray!=null)
  for(int i=0;i<TOTAL_IMAGES_NUMBER;i++)
      if(ByteArray[i]!=null) Elements[i]=null;

  Runtime.getRuntime().gc();
}

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_MAN_INV = 4;
private static final int IMG_NFONT = 5;
private static final int IMG_FONT_INV = 6;
private static final int IMG_PLAYER_DEAD = 7;
private static final int IMG_PLAYER_M = 8;
private static final int IMG_SPOT = 9;
private static final int IMG_STRIKE01 = 10;
private static final int IMG_STRIKE02 = 11;
private static final int IMG_DAGGER_INV = 12;
private static final int IMG_DAGGER_INV_VERT = 13;
private static final int IMG_SWORD01_INV = 14;
private static final int IMG_SWORD01_INV_VERT = 15;
private static final int IMG_SWORD02_INV = 16;
private static final int IMG_SWORD02_INV_VERT = 17;
private static final int IMG_SWORD03_INV = 18;
private static final int IMG_SWORD03_INV_VERT = 19;
private static final int IMG_SPLINTMAIL_INV = 20;
private static final int IMG_FULLPLATE_INV = 21;
private static final int IMG_SHIELD02_INV = 22;
private static final int IMG_KEY_INV01 = 23;
private static final int IMG_KEY_INV02 = 24;
private static final int IMG_KEY_INV03 = 25;
private static final int IMG_KEY_INV04 = 26;
private static final int IMG_KEY_INV05 = 27;
private static final int IMG_WALL_H01 = 28;
private static final int IMG_WALL_H02 = 29;
private static final int IMG_WALL_H03 = 30;
private static final int IMG_WALL_H04 = 31;
private static final int IMG_WALL_H05 = 32;
private static final int IMG_WALL01 = 33;
private static final int IMG_WALL02 = 34;
private static final int IMG_WALL03 = 35;
private static final int IMG_WALL04 = 36;
private static final int IMG_WALL05 = 37;
private static final int IMG_WALL_FLIP01 = 38;
private static final int IMG_WALL_FLIP02 = 39;
private static final int IMG_WALL_FLIP03 = 40;
private static final int IMG_WALL_FLIP04 = 41;
private static final int IMG_WALL_FLIP05 = 42;
private static final int IMG_DOOR_H01 = 43;
private static final int IMG_DOOR_H02 = 44;
private static final int IMG_DOOR_H03 = 45;
private static final int IMG_DOOR_H04 = 46;
private static final int IMG_DOOR_H05 = 47;
private static final int IMG_WALL_DOOR02 = 49;
private static final int IMG_WALL_DOOR03 = 50;
private static final int IMG_WALL_DOOR04 = 51;
private static final int IMG_WALL_DOOR_FLIP02 = 54;
private static final int IMG_WALL_DOOR_FLIP03 = 55;
private static final int IMG_WALL_DOOR_FLIP04 = 56;
private static final int IMG_EXIT_H01 = 58;
private static final int IMG_EXIT_H02 = 59;
private static final int IMG_EXIT_H03 = 60;
private static final int IMG_EXIT_H04 = 61;
private static final int IMG_EXIT_H05 = 62;
private static final int IMG_ENEMY1_AT01 = 63;
private static final int IMG_ENEMY1_AT02 = 64;
private static final int IMG_ENEMY1_AT03 = 65;
private static final int IMG_ENEMY1_ST01 = 66;
private static final int IMG_ENEMY1_ST02 = 67;
private static final int IMG_ENEMY1_ST03 = 68;
private static final int IMG_ENEMY1_ST04 = 69;
private static final int IMG_ENEMY1_ST05 = 70;
private static final int IMG_BOX01 = 71;
private static final int IMG_BOX02 = 72;
private static final int IMG_BOX03 = 73;
private static final int IMG_BOX04 = 74;
private static final int IMG_BOX05 = 75;
private static final int IMG_SHOCKER01 = 76;
private static final int IMG_SHOCKER02 = 77;
private static final int IMG_SHOCKER03 = 78;
private static final int IMG_SHOCKER04 = 79;
private static final int IMG_SHOCKER05 = 80;
private static final int IMG_LIGHTING01 = 81;
private static final int IMG_LIGHTING02 = 82;
private static final int IMG_BUTTON01 = 83;
private static final int IMG_BUTTON02 = 84;
private static final int IMG_BUTTON03 = 85;
private static final int IMG_BUTTON04 = 86;
private static final int IMG_BUTTON05 = 87;
private static final int IMG_BUTTON_DOWN = 88;
private static final int IMG_KEY01 = 89;
private static final int IMG_KEY02 = 90;
private static final int IMG_KEY03 = 91;
private static final int IMG_KEY04 = 92;
private static final int IMG_KEY05 = 93;
private static final int IMG_PLAYER_ICO = 94;
private static final int IMG_CHAR_MENU = 95;
private static final int IMG_BKG01 = 96;
private static final int IMG_BKG02 = 97;
private static final int IMG_LEV_UP = 98;
private static final int IMG_WALL_DOOR01 = 48;
private static final int IMG_WALL_DOOR05 = 52;
private static final int IMG_WALL_DOOR_FLIP01 = 53;
private static final int IMG_WALL_DOOR_FLIP05 = 57;
private static final int TOTAL_IMAGES_NUMBER = 99;

public static final byte SND_BUTTON_OTT = (byte)0;
public static final byte SND_DEATH_OTT = (byte)1;
public static final byte SND_GOLD_OTT = (byte)2;
public static final byte SND_ITEM_OTT = (byte)3;
public static final byte SND_KILL_OTT = (byte)4;
public static final byte SND_LEVELUP_OTT = (byte)5;
public static final byte SND_MONSTER_OTT = (byte)6;
public static final byte SND_STAGE_OTT = (byte)7;
public static final byte SND_THEME_OTT = (byte)8;

}
