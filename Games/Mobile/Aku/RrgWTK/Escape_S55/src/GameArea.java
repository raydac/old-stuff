
import javax.microedition.lcdui.*;
//import com.GameKit_3.SantaClaus.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable, LoadListener {//, GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 65; //WARNING, This file contains not scalable objects (h=64)

/*=============================*/

    private static final int KEY_MENU = -7;
    private static final int KEY_CANCEL = -6;
    private static final int KEY_ACCEPT = -7;
    private static final int KEY_BACK = -10;

    public void setScreenLight(boolean state)
    {
     DeviceControl.setLights(0, state?100:0);
    }
    public void activateVibrator(int n){}

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
    public void playSound(int n){
      if(sounds!=null && sounds.length>0 && midlet._Sound){
//	stopAllMelodies();
//       synchronized(sounds){
        if(soundIsPlaying() && System.currentTimeMillis()-sound_timemark<SoundPlayTimeGap) return;
	sound_timemark = System.currentTimeMillis();
	stopAllMelodies();
	sounds[n].play(1);
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
  private Image BackScreen=null;
  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected BCar client_sb;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

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
  private int [] stageSleepTime = {100,100,100,100,70,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private final static int BkgCOLOR = 0x000061;
  private int VisWidth, VisHeight;
  boolean isFirst;


  final static int VERTICAL_LIMIT = 64;
  final static int EXPLOSION_FRAMES = 6;
  final static int EXPLOSION_FRAMES2 = EXPLOSION_FRAMES>>1;
  final static int quant = 8;      // in times, amount
  final static int mark_lenght = VERTICAL_LIMIT/quant;// in lines, size
  final static int mark_width = 4;

  int _explx1,_exply1;
  int _explx2,_exply2;

  int  [] yCorrection;
  int  [] xCorrection;
  int displayable_range = 64;       //from bottom of screen
  int sWidthMultiplicator = 0x64;   // 8b factor, half of'em
  int eWidthMultiplicator = 0x200;  // 8b factor, native width
  int road_rounding = 60;          // acceleration of narrowing

    // precalculated values
  int scr2;            // scrwidth/2;

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

    VisWidth = Math.min(scrwidth,WIDTH_LIMIT);
    VisHeight = Math.min(scrheight,HEIGHT_LIMIT);

    client_sb = new BCar(VisWidth, VisHeight, null);

    if(!this.isDoubleBuffered()){
      Buffer=Image.createImage(scrwidth,scrheight);
      gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    scr2 = VisWidth>>1;
    TInit(client_sb.H);
//    MenuIcon = getImage(IMG_MENUICO);
    isFirst = true;
  }

/* NOKIA, LOAD ON DEMAND
  private Image GetImage(int idx){
    if(items[idx]!=null)return items[idx];
     else{
//       if(Runtime.getRuntime().freeMemory()<15000)Runtime.getRuntime().gc();
      return Image.createImage(ByteArray[idx],0,ByteArray[idx].length);
     }
  }
*/
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
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];
	   font = new drawDigits(Elements[IMG_NFONT]);
	   ib = null;
           Runtime.getRuntime().gc();

//	   BackScreen = Image.createImage(VisWidth,VisHeight);
//	   gBackScreen = BackScreen.getGraphics();
//	   gBackScreen.drawImage(Elements[IMG_BACK01],0,0,0);
//	   Elements[IMG_BACK01] = null;

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
//	  thread.setPriority(Thread.MIN_PRIORITY);
       }
     } catch(Exception e) {
        System.out.println("Can't read images");
	System.exit(-1);
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
       direct=BCar.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
            client_sb.nextGameStep(direct);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
//           client_sb.nextGameStep(BCar.BUTTON_NONE);
	   gameStatus=newStage;
	}
	bufferChanged=true;
//	DoubleBuffer(gBuffer);
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
       isShown = false;;
       stopAllMelodies();
       direct = 0;
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
	                          switch (keyCode) {
			             case MENU_KEY:  midlet.ShowGameMenu(); break;
				     case END_KEY:  midlet.ShowQuitMenu(true); break;
				     case Canvas.KEY_NUM6:direct=BCar.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM4:direct=BCar.BUTTON_LEFT; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT:direct=BCar.BUTTON_LEFT; break;
					       case Canvas.RIGHT:direct=BCar.BUTTON_RIGHT; break;
					           default: direct=BCar.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=BCar.BUTTON_NONE;
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

			   //autopilot
			   int dd=client_sb.road[client_sb.CARY] + (client_sb.ROADWIDTH>>1) - client_sb.CARX;
			   int dir = BCar.BUTTON_NONE;
			   if(dd>5)dir = BCar.BUTTON_RIGHT;
			    else if(dd<-5)dir = BCar.BUTTON_LEFT;


		           client_sb.nextGameStep(dir);
			   //UpdateBackScreen();

			   if (client_sb.i_PlayerState!=Gamelet.PLAYERSTATE_NORMAL || ++ticks>demoLimit || client_sb.carexplosion) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			       isFirst = true;
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=BCar.BUTTON_NONE;

			        client_sb.initStage(stage);
				client_sb.nextGameStep(direct);
//				drawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>25){
				if(stage==1){
				   lightGreen = 0xFFFFC041;
				   darkGreen = 0xFF9C7203;
				} else {
				   lightGreen = 0xFF008000;
				   darkGreen = 0xFF003000;
				}

			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
			   client_sb.nextGameStep(direct);
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>2/*Stages.TOTAL_STAGES*/)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
								      break;
			         }
			   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
//			   serviceRepaints();
			  } break;
	    case Crashed:
			   stopAllMelodies();
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=BCar.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
                                   client_sb.nextGameStep(BCar.BUTTON_NONE);
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           // PlayMelody(SND_APPEARANCE_OTT);
			      }
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
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
			   repaint();
			   break;
 	 }

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
	workDelay = System.currentTimeMillis();

      }
    }


/*

    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case BCar.GAMEACTION_FIELDCHANGED:
		  {
		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
		  }
	 }
    }


    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(0x0);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }
*/


      public void TInit(int map_height){

        yCorrection = new int[map_height];
        xCorrection = new int[map_height];

	int base = 0;//scrheight - displayable_range;
	int offset = (displayable_range)<<8;
        for (int i=map_height-1;i>=0; i--){
          yCorrection[i] =
	             base + (offset>>8);
//	            (scrheight - displayable_range) +
//		    (i*displayable_range/map_height);
          offset -= offset/road_rounding;
          xCorrection[i] =
	            eWidthMultiplicator +
                    (sWidthMultiplicator-eWidthMultiplicator)
                                       *
		          (map_height-i)/map_height;
        }
      }


        public int xTrans(int x,int y){

          if(y>=xCorrection.length){y=xCorrection.length-1;}
          if(y<0){y=0;}

	  int coff = client_sb.CARX+(client_sb.CARWIDTH>>1);
          int s = x-coff;
              s = (s*xCorrection[y])>>8;
              s = s+(scrwidth>>1);

          return s;
        }

        public int yTrans(int y){
          if(y>=yCorrection.length){y=yCorrection.length-1;}
          if(y<0){y=0;}
          return yCorrection[y];
        }

int[] ay = new int[4];
int[] ax = new int[4];

int lightGreen = 0xFF008000;
int darkGreen = 0xFF003000;
int gray = 0xFF555555;
int black = 0xFF000000;
int white = 0xFFffffff;

    protected void DoubleBuffer(Graphics gBuffer){

//      System.out.println(client_sb.CARY+" = "+xCorrection[client_sb.CARY]+"["+(xCorrection[client_sb.CARY]>>8)+":"+(xCorrection[client_sb.CARY]&0xff)+"]");

      if(baseX >0 || baseY >0){
        gBuffer.setColor(0x0);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.setClip(0,0,VisWidth,VisHeight);
        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
      }
////////////////////////////////////////////////

        Image img = null;

	// prepare screen
	gBuffer.setColor(darkGreen/*0xffffff*/);gBuffer.fillRect(0,21,VisWidth,VisHeight-20);
        gBuffer.setColor(0x0);

//	int groundline = yCorrection[0]; //=22
//        gBuffer.drawLine(0,groundline,scrwidth,groundline);


	// precalculation
	int carx_offs = client_sb.CARX+(client_sb.CARWIDTH>>1); // offset of player car, used for centering objects
	int counter = client_sb.road.length/quant;              // steps amount

	int player_y = yCorrection[client_sb.CARY + client_sb.CARHEIGHT];
	int police_y=999;
	int frontcar_y=999;

        if (client_sb.policecar && client_sb.policecarY > 0 && client_sb.policecarY < VERTICAL_LIMIT)
            police_y = client_sb.policecarY+client_sb.CARHEIGHT;
        if (client_sb.frontcar && client_sb.frontcarY > 0 && client_sb.frontcarY < VERTICAL_LIMIT)
            frontcar_y = client_sb.frontcarY+client_sb.CARHEIGHT;

	int rts = client_sb.road[0],
	    prev_yc = yCorrection[0],
	    prev_xc = xCorrection[0],
	    prev_xl = (((rts-carx_offs)*prev_xc)>>8)+scr2,                     // xTrans(rts,ts),
	    prev_xr = (((rts+client_sb.ROADWIDTH-carx_offs)*prev_xc)>>8)+scr2, // xTrans(rts + client_sb.ROADWIDTH,ts);
	    prev_w = (mark_width*prev_xc);

        boolean solid = client_sb.miles_counter % (mark_lenght<<1) < mark_lenght; // first block is solid
	int latch = client_sb.miles_counter % mark_lenght; // tail of current block

	int bsx = (scr2-carx_offs)>>2;
	int _h = IMG_BACK01+stage;
	Elements[_h] = img = GetImage(_h);
/*
 this part of code is necessary only for vertical resolution greater than 64
/////////////////////////////////////////////////////////////////////////////////////////////////////
// WARNING: This part of code contains the coordinates patch
	_h = VisHeight-64;
        gBuffer.drawImage(img,bsx,_h+21-img.getHeight(),0);
	if (bsx>0)gBuffer.drawImage(img,bsx-img.getWidth(),_h+21-img.getHeight(),0);
	  else
	    if (bsx<0)gBuffer.drawImage(img,bsx+img.getWidth(),_h+21-img.getHeight(),0);
        gBuffer.translate(0,_h);
        gBuffer.setClip(0,-_h,VisWidth,VisHeight);
/////////////////////////////////////////////////////////////////////////////////////////////////////
*/

        gBuffer.drawImage(img,bsx,0,0);
	if (bsx>0)gBuffer.drawImage(img,bsx-img.getWidth(),0,0);
	  else
	    if (bsx<0)gBuffer.drawImage(img,bsx+img.getWidth(),0,0);

	int dist8 = 0;
	int d_step8 = ((7<<8))/64;
boolean s = true;
	// draw Roads and stage objects
        for (int ty = 1; ty < client_sb.road.length; ty++)
        {
            if ((latch<=0 || ty==client_sb.road.length-1/*-3*/)/* && ty<client_sb.road.length-1*/)
	    {

		     int yy = yCorrection[ty],
		         h  = yy-prev_yc,
		         rty = client_sb.road[ty],
		         tscorr = xCorrection[ty],
			 xl = (((rty-carx_offs)*tscorr)>>8)+scr2, // xTrans(rts,ts),
		         xr = (((rty+client_sb.ROADWIDTH-carx_offs)*tscorr)>>8)+scr2, // xTrans(rts + client_sb.ROADWIDTH,ts);
			 w8 = (mark_width*tscorr),
			 w = w8>>8,
			 pw = prev_w>>8;


		     ay[0] = prev_yc;
		     ay[1] = prev_yc;
		     ay[2] = yy;
		     ay[3] = yy;

		     DirectGraphics dg = DirectUtils.getDirectGraphics(gBuffer);



                     if(solid)
		     {
		          gBuffer.setColor(lightGreen);
		          gBuffer.fillRect(0,prev_yc,VisWidth,yy-prev_yc);

			   ax[0] = prev_xl-pw;  ax[1] = prev_xl; ax[2] = xl; ax[3] = xl-w;
		           dg.fillPolygon(ax,0,ay,0,4,black);
			   ax[0] = prev_xr;  ax[1] = prev_xr+pw; ax[2] = xr+pw; ax[3] = xr;
		           dg.fillPolygon(ax,0,ay,0,4,black);
                     }
		      else
		     {
//		          gBuffer.setColor(darkGreen);
//		          gBuffer.fillRect(0,prev_yc,VisWidth,yy-prev_yc);

			   ax[0] = prev_xl-pw;  ax[1] = prev_xl; ax[2] = xl; ax[3] = xl-w;
		           dg.fillPolygon(ax,0,ay,0,4,white);
			   ax[0] = prev_xr;  ax[1] = prev_xr+pw; ax[2] = xr+pw; ax[3] = xr;
		           dg.fillPolygon(ax,0,ay,0,4,white);
                     }

		     ax[0] = prev_xl;  ax[1] = prev_xr; ax[2] = xr; ax[3] = xl;
		     dg.fillPolygon(ax,0,ay,0,4,gray);

		      prev_yc = yy;
		      prev_xc = tscorr;
		      prev_xl = xl;
		      prev_xr = xr;
		      prev_w = w8;
		      latch = counter;
		      solid=!solid;
	    }
	    latch--;
        }

        for (int ty = 1; ty < client_sb.road.length; ty++)
        {

            if (client_sb.roaditems[ty][0] != 0)
            {
	       int y = yCorrection[ty],
	           x = (((client_sb.roaditems[ty][0]+(BCar.MINE_ROADWIDTH>>1) - carx_offs)*xCorrection[ty])>>8)+scr2;

	       switch(client_sb.roaditems[ty][1]){
		  case BCar.OBJECT_BOMB:   img = GetImage(IMG_TORN05-(dist8>>8));
		                           y-=(img.getHeight()>>1);
	                                   x+=(((BCar.MINE_HELWIDTH - BCar.MINE_ROADWIDTH)*xCorrection[ty])>>9); ///(((BCar.MINE_HELWIDTH - BCar.MINE_ROADWIDTH)>>1)*xCorrection[ty])>>8);

		                           break;
		  case BCar.OBJECT_MINE:   img = GetImage(IMG_STONE05-(dist8>>8));
		                           y-=(img.getHeight()>>1);
		                           break;
		       default:/*case BCar.OBJECT_TREE:*/
		                           img = GetImage(IMG_FIR12-(dist8>>7));
		                           y-=img.getHeight();

	       }
	       x-=(img.getWidth()>>1);
	       gBuffer.drawImage(img,x,y,0);
            }


            // Police car
            if(police_y==ty){
	       police_y=yCorrection[ty];
	       int _x = client_sb.policecarX + (client_sb.CARWIDTH>>1) -carx_offs;
	       img = GetImage(IMG_CAR2_01+(ticks&1));
	       police_y -= img.getHeight();
	       _x = ((_x*xCorrection[ty])>>8)+scr2 - (img.getWidth()>>1);
               gBuffer.drawImage(img, _x, police_y, 0);
	       police_y = 999;
            }

            // Front car
            if(frontcar_y==ty){
	       frontcar_y=yCorrection[ty];
	       int _x = client_sb.frontcarX + (client_sb.CARWIDTH>>1) -carx_offs;
	       img = GetImage(IMG_CAR3_05 -(dist8>>8));
	       frontcar_y -= img.getHeight();
	       _x = ((_x*xCorrection[ty])>>8)+scr2 - (img.getWidth()>>1);
               gBuffer.drawImage(img, _x, frontcar_y, 0);
	       frontcar_y = 999;
            }

	    // Main car, Helicopter and Helicopter's fire
            if(player_y==ty)
	    {
	      int xc = 1;
              if (client_sb.helicopter) {
		 xc = xCorrection[ty];
		 img = GetImage((client_sb.helicopterLeft?IMG_HEL01:IMG_HEL_RIGHT01) +(ticks&1));
		 gBuffer.drawImage(img,(((client_sb.helicopterX+(client_sb.helicopterWIDTH>>1) - carx_offs)*xc)>>8)+scr2 - (img.getWidth()>>1)
	                              ,client_sb.helicopterY-img.getHeight(), 0);
              }

	      // Helicopter fire
              if (client_sb.helMineFire){
		 img = GetImage(IMG_MINE02);
                 gBuffer.drawImage(img,(((client_sb.helicopterFireX/*+(BCar.MINE_ROADWIDTH>>1)*/ - carx_offs)*xc)>>8)+scr2 - (img.getWidth()>>1)
		                      ,client_sb.helicopterFireY-(img.getHeight()>>1), 0);
              }

	      if(gameStatus != Crashed || blink)
	      {
                img = GetImage(IMG_CAR1_01+direct);
	        player_y = yCorrection[ty];
	        gBuffer.drawImage(img,scr2 -(img.getWidth()>>1),player_y - img.getHeight(),0);

//		gBuffer.drawRect((scrwidth-client_sb.CARWIDTH)>>1,player_y - client_sb.CARHEIGHT,client_sb.CARWIDTH,client_sb.CARHEIGHT);

	        if( client_sb.carexplosion && gameStatus == playGame)
		{
	          int n = client_sb.carexplosioncounter % EXPLOSION_FRAMES;
		  if(n==0){
		     _explx1 = scr2-(img.getWidth()>>1)+client_sb.getRandomInt(img.getWidth());
		     _exply1 = player_y - client_sb.getRandomInt(img.getHeight());
		  } else
		  if(EXPLOSION_FRAMES2==n){
		    _explx2 = scr2-(img.getWidth()>>1)+client_sb.getRandomInt(img.getWidth());
		    _exply2 = player_y - client_sb.getRandomInt(img.getHeight());
		  }
	          img = GetImage(IMG_BURST01+n);
	          gBuffer.drawImage(img,_explx1-(img.getWidth()>>1),_exply1 - (img.getHeight()>>1),0);
		  if(client_sb.carexplosioncounter>EXPLOSION_FRAMES2-2){
		  if(n>=EXPLOSION_FRAMES2) img = GetImage(IMG_BURST01+n - EXPLOSION_FRAMES2);
		   else img = GetImage(IMG_BURST01+n + EXPLOSION_FRAMES2);
	            gBuffer.drawImage(img,_explx2-(img.getWidth()>>1),_exply2 - (img.getHeight()>>1),0);
		  }
	        }
	        player_y = 999;
	      }
	    }

	    // distance step
	    dist8 += d_step8;
	    if(dist8>0x400)dist8=0x400;
        }

            // Front car, deep
            if(police_y<999){
	       img = GetImage(IMG_CAR2_01);
	       police_y=yCorrection[yCorrection.length-1]+police_y-yCorrection.length - img.getHeight();
	       int _x = client_sb.policecarX + (client_sb.CARWIDTH>>1) -carx_offs;
	       _x = ((_x*xCorrection[xCorrection.length-1])>>8)+scr2 - (img.getWidth()>>1);
               gBuffer.drawImage(img, _x, police_y, 0);
            }

            // Front car, deep
            if(frontcar_y<999){
	       img = GetImage(IMG_CAR3_01);
	       frontcar_y=yCorrection[yCorrection.length-1]+frontcar_y-yCorrection.length - img.getHeight();
	       int _x = client_sb.frontcarX + (client_sb.CARWIDTH>>1) -carx_offs;
	       _x = ((_x*xCorrection[xCorrection.length-1])>>8)+scr2 - (img.getWidth()>>1);
               gBuffer.drawImage(img, _x, frontcar_y, 0);
            }
/*
 this part of code is necessary only for vertical resolution greater than 64
        gBuffer.translate(0,-_h);
        gBuffer.setClip(0,0,VisWidth,VisHeight);
*/
        // Speed
        int sp = (client_sb.carspeed/ BCar.SPEED_MULTIPLIER);
	gBuffer.drawImage(GetImage(IMG_SPEED),2,2,0);
	font.drawDigits(gBuffer,3,4,3,sp);

	gBuffer.setColor(0xff0000);

	if(client_sb.STAGECOUNTERMAX>0){
	 int i = (client_sb.stagecounter)*VisHeight/client_sb.STAGECOUNTERMAX;
	 gBuffer.fillRect(VisWidth-2,i,2,VisHeight-i);
	}

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
      if(baseX >0 || baseY >0){
        gBuffer.translate(-gBuffer.getTranslateX(),-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,scrwidth,scrheight);
      }
	bufferChanged=false;
    }


    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    protected void paint(Graphics g) {
            switch (gameStatus) {
	      case notInitialized:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x000080);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  g.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
		          g.setColor(0x00ff00);    // drawin' bar
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
	                   if (colorness){
	                      g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0xFF0000);    // drawin' flyin' text colour
	                   } else {
	                      g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0x0);    // drawin' flyin' monochrome
	                   }
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
	                  } break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
	                   if (colorness){
	                      g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0xFFFF00);    // drawin' flyin' text colour
	                   } else {
	                      g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0x0);    // drawin' flyin' monochrome
	                   }
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				//g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
				// g.setColor(0xFFFF00);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	//g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WIN),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :   if (this.isDoubleBuffered())
			      {

		                DoubleBuffer(g);
			      }
			      else
			      {
//			        if(bufferChanged)
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                             	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       direct=BCar.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_sb.nextGameStep(direct);
//       drawBackScreen();
       gameStatus=newStage;
       bufferChanged=true;
       repaint();
    }

private Image GetImage(int n){
       if(Elements[n]!=null) return Elements[n];
/*
	if(Runtime.getRuntime().freeMemory()<4000){
	  Runtime.getRuntime().gc();
	  if(Runtime.getRuntime().freeMemory()<4000)CleanSpace();
*/
        return Image.createImage(ByteArray[n], 0, ByteArray[n].length);
}

public void CleanSpace(){
 if(ByteArray!=null)
  for(int i=0;i<TOTAL_IMAGES_NUMBER;i++)
      if(ByteArray[i]!=null) Elements[i]=null;
  Runtime.getRuntime().gc();
}

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_SPEED = 4;
private static final int IMG_BACK01 = 5;
private static final int IMG_BACK02 = 6;
private static final int IMG_BACK03 = 7;
private static final int IMG_BURST01 = 8;
private static final int IMG_BURST02 = 9;
private static final int IMG_BURST03 = 10;
private static final int IMG_BURST04 = 11;
private static final int IMG_BURST05 = 12;
private static final int IMG_BURST06 = 13;
private static final int IMG_CAR1_01 = 14;
private static final int IMG_CAR1_LEFT_01 = 15;
private static final int IMG_CAR1_RIGHT_01 = 16;
private static final int IMG_CAR2_01 = 17;
private static final int IMG_CAR2_02 = 18;
private static final int IMG_CAR3_01 = 19;
private static final int IMG_CAR3_02 = 20;
private static final int IMG_CAR3_03 = 21;
private static final int IMG_CAR3_04 = 22;
private static final int IMG_CAR3_05 = 23;
private static final int IMG_TORN01 = 24;
private static final int IMG_TORN02 = 25;
private static final int IMG_TORN03 = 26;
private static final int IMG_TORN04 = 27;
private static final int IMG_TORN05 = 28;
private static final int IMG_MINE02 = 29;
private static final int IMG_STONE01 = 30;
private static final int IMG_STONE02 = 31;
private static final int IMG_STONE03 = 32;
private static final int IMG_STONE04 = 33;
private static final int IMG_STONE05 = 34;
private static final int IMG_FIR3 = 35;
private static final int IMG_FIR4 = 36;
private static final int IMG_FIR5 = 37;
private static final int IMG_FIR6 = 38;
private static final int IMG_FIR7 = 39;
private static final int IMG_FIR8 = 40;
private static final int IMG_FIR9 = 41;
private static final int IMG_FIR10 = 42;
private static final int IMG_FIR11 = 43;
private static final int IMG_FIR12 = 44;
private static final int IMG_HEL01 = 45;
private static final int IMG_HEL02 = 46;
private static final int IMG_HEL_RIGHT01 = 47;
private static final int IMG_HEL_RIGHT02 = 48;
private static final int IMG_NFONT = 49;
private static final int TOTAL_IMAGES_NUMBER = 50;
}

