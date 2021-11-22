import roadwarrior.*;
import com.itx.mbgame.GameObject;


import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 128;

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

  private final static int EXPLOSION_FRAMES = 6;


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
  protected Game client_sb;
  protected game_PMR client_pmr;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private boolean useDblBuffer = !this.isDoubleBuffered();

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
  private int [] stageSleepTime = {100,100,100,100,100,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

//  private final static int BkgCOLOR = 0x000061; //for Nokia
  private final static int BACKGROUND_COLOR = 0x008040;
  private int HealthBarFillColor,HealthBarEmptyColor;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int scr2, center_x, center_y, start_x,start_y;
  private final static int CELL_SIZE = 12;
  private final static int ROAD_BORDER = 3;

  private int FLOOR_COLOR;// = 0x737373;
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

    client_sb = new Game(VisWidth, VisHeight, this);
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
	HealthBarFillColor = 0xff0000;
	HealthBarEmptyColor = 0x000040;
    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
	HealthBarFillColor = 0x000000;
	HealthBarEmptyColor = 0xffffff;
    }
    scr2 = VisWidth>>1;
    isFirst = true;
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
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];
	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;

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
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
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
       isShown = false;
       stopAllMelodies();
       direct = 0;
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
	                          switch (keyCode) {
			             case MENU_KEY:  midlet.ShowGameMenu(); break;
				     case END_KEY:  midlet.ShowQuitMenu(true); break;
				     case Canvas.KEY_NUM2 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = game_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM6 : direct = game_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = game_PMR.BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = game_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = game_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = game_PMR.BUTTON_UP; break;
					       case Canvas.DOWN : direct = game_PMR.BUTTON_DOWN; break;
					           default: direct=game_PMR.BUTTON_NONE;
				            }
	                          }
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
//			   if (client_sb.bulgar.getDirection()==GameObject.DIR_STOP && (ticks%3 == 0))
//			     client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_DOWN/*4*/);
                           client_pmr.i_Value = game_PMR.BUTTON_NONE;
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
//	                   DoubleBuffer(gBuffer);
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
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
	                        bufferChanged=true;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
			   client_pmr.i_Value = direct;
                           client_sb.nextGameStep(client_pmr);

//			   if(!is_painting)
			     //updateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>Game.TOTAL_STAGES)
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
//        if(is_painting) serviceRepaints();

      }
    }



    public void gameAction(int actionID){}
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

    private void drawCar(Graphics gBuffer, GameObject pc){
      Image img;
      int i,x,y;
        if(pc.isActive())
        {
           switch(pc.getType())  {
	       case Game.POLICE_CAR1: i = IMG_CAR01_01; break;
               case Game.POLICE_CAR2: i = IMG_CAR02_01; break;
               case Game.POLICE_CAR3: i = IMG_CAR03_01; break;
               case Game.POLICE_CAR4: i = IMG_CAR04_01; break;
                       default: // case client_sb.POLICE_CAR1:
		                           i = IMG_CAR05_01; break;
           }
           img = GetImage(i+(ticks&1));
           x = pc.X()+((pc.WIDTH() - img.getWidth())>>1);
           y = pc.Y()+((pc.HEIGHT() - img.getHeight())>>1);
/*
           gBuffer.drawImage(img,x,y,0);
*/
           if(pc.getState() == client_sb.CAR_STATE_EXPLOSION){
	     int n = EXPLOSION_FRAMES-(client_sb.flashpointer*EXPLOSION_FRAMES+1)/client_sb.MAXFLASHPOINTER-1;
             x += img.getWidth()>>1; y += img.getHeight()>>1;
	     img = GetImage(IMG_EXPL01 + n);
             x -= img.getWidth()>>1; y -= img.getHeight()>>1;
	     gBuffer.drawImage(img,x,y,0);
           }
	     else
                 gBuffer.drawImage(img,x,y,0);

        }
        if(pc.aX >=0)
             gBuffer.fillRect(pc.aX,pc.aY,2,2);
    }


    private void drawRoadFragment(Graphics gBuffer,int road_type,int y, int h){
      if(h>0 && y>=0 && y<client_sb.road.length){

	Image img;
	switch (road_type){
	   case Game.ROAD_STRAIGHT:
	                   {
                           int xx = gBuffer.getClipX();
	                   int yy = gBuffer.getClipY();
	                   int ww = gBuffer.getClipWidth();
	                   int hh = gBuffer.getClipHeight();

			      img = GetImage(IMG_ROAD01);
			      int x = client_sb.road[y] - ROAD_BORDER;;
			      gBuffer.setClip(xx,y,ww,h);
			      int _y=y+h;
			      while(_y>y){
				gBuffer.drawImage(img,x,y,0);
				y+=img.getHeight();
			      }

			   gBuffer.setClip(xx,yy,ww,hh);
	                   } break;
	   case Game.ROAD_LEFT:{
			      img = GetImage(IMG_ROAD05);
			      int x = client_sb.road[y] - ROAD_BORDER;;
			      gBuffer.drawImage(img,x,y,0);
			      }break;
	   case Game.ROAD_RIGHT:{
			      img = GetImage(IMG_ROAD03);
			      int x = client_sb.road[y]+client_sb.ROADWIDTH-img.getWidth() + ROAD_BORDER-1;
			      gBuffer.drawImage(img,x,y,0);
			      }break;
	   case Game.ROAD_FROM_LEFT:
	                   {
			      img = GetImage(IMG_ROAD04);
			      int x = client_sb.road[y] - ROAD_BORDER;
			      gBuffer.drawImage(img,x,y,0);
	                   } break;
	   case Game.ROAD_FROM_RIGHT:
	                   {
			      img = GetImage(IMG_ROAD02);
			      int x = client_sb.road[y]+client_sb.ROADWIDTH-img.getWidth() + ROAD_BORDER;
			      gBuffer.drawImage(img,x,y,0);
	                   } break;
	}
      }
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

        Image img = null; int i;
//        gBuffer.setColor(0xffffff);gBuffer.fillRect(0,0,VisWidth,VisHeight);
      // ground
      gBuffer.setColor(BACKGROUND_COLOR);
      i = VisWidth-client_sb.ROADWIDTH;
//      gBuffer.fillRect(0,0,i,VisHeight);
//      gBuffer.fillRect(client_sb.ROADWIDTH,0,i,VisHeight);


      img = GetImage(IMG_GROUND);
//      gBuffer.drawImage(img,0,0,0);
//      gBuffer.drawImage(img,VisWidth-img.getWidth(),0,0);

      i = img.getHeight();
      int gnd = VisHeight /*+ client_sb.miles_counter % i*/ - i;
      while (gnd>-i){
	gBuffer.drawImage(img,0,gnd,0);
	gBuffer.drawImage(img,VisWidth-img.getWidth(),gnd,0);
	gnd-=i;
      }



	gBuffer.setColor(0x0);
/**********************/
      // Road && RoadItems
      int y=0,x=client_sb.road[y];

      int ty = client_sb.road.length-1,prev_ty = ty;
      while(ty>=0){
        if(client_sb.roaditems[ty][1]==Game.ROADTYPE_MARK){
	  drawRoadFragment(gBuffer,client_sb.roaditems[ty][0],ty,prev_ty-ty+1);
//	  gBuffer.drawLine(0,ty,VisWidth,ty); // sections delimiter
	  prev_ty = ty;
        }
        ty--;
      }

      if(prev_ty>0){
      	switch (client_sb.curswaytype){
	   case Game.ROAD_STRAIGHT:
	                   {
			      int xx = gBuffer.getClipX();
	                      int yy = gBuffer.getClipY();
	                      int ww = gBuffer.getClipWidth();
	                      int hh = gBuffer.getClipHeight();

			      img = GetImage(IMG_ROAD01);
			      x = client_sb.road[0] - ROAD_BORDER;
			      gBuffer.setClip(xx,0,ww,prev_ty+1);

			      int h = img.getHeight();
			      ty = -h;
			      if(prev_ty == client_sb.road.length-1)
			         prev_ty += (client_sb.miles_counter % h)-1;
			      prev_ty -= h -1;
			      while(prev_ty>ty){
				gBuffer.drawImage(img,x,prev_ty,0);
				prev_ty-=h;
			      }
			      gBuffer.setClip(xx,yy,ww,hh);
	                   } break;
	   case Game.ROAD_LEFT:
	                      {
			        img = GetImage(IMG_ROAD05);
			        x = client_sb.road[prev_ty] + client_sb.ROADWIDTH-img.getWidth() + ROAD_BORDER;
			        gBuffer.drawImage(img,x,prev_ty-img.getHeight(),0);
			      }break;
	   case Game.ROAD_RIGHT:
	                      {
			        img = GetImage(IMG_ROAD03);
			        x = client_sb.road[prev_ty] - ROAD_BORDER;
			        gBuffer.drawImage(img,x,prev_ty-img.getHeight(),0);
			      }break;
	   case Game.ROAD_FROM_LEFT:
	                      {
			        img = GetImage(IMG_ROAD04);
			        x = client_sb.road[prev_ty] + client_sb.ROADWIDTH-img.getWidth() + ROAD_BORDER-1;
			        gBuffer.drawImage(img,x,prev_ty-img.getHeight(),0);
			      }break;
	   case Game.ROAD_FROM_RIGHT:
	                      {
			        img = GetImage(IMG_ROAD02);
			        x = client_sb.road[prev_ty] - ROAD_BORDER;
			        gBuffer.drawImage(img,x,prev_ty-img.getHeight(),0);
			      }break;
	}
      }
/*
// show the wire-frame road
      for (ty = 0; ty < client_sb.road.length; ty++)
      {


	  int tx = client_sb.road[ty];
	  if(x == tx) continue;

	  gBuffer.drawLine(x,y,tx,ty);
	  gBuffer.drawLine(x+client_sb.ROADWIDTH,y,tx+client_sb.ROADWIDTH,ty);

	  x = tx;
	  y = ty;

      }
	  gBuffer.drawLine(x,y,x,VisHeight);
	  gBuffer.drawLine(x+client_sb.ROADWIDTH,y,x+client_sb.ROADWIDTH,VisHeight);
*/

      for (ty = 0; ty < client_sb.road.length; ty++)
      {

	x = client_sb.roaditems[ty][0];
	if (x > 0){
	  switch (client_sb.roaditems[ty][1]) {
	     case Game.OBJECT_MINE:    i = IMG_STONE01; break;
	     case Game.OBJECT_CARMINE: i = IMG_MINE; break;
	     case Game.OBJECT_TREE:    i = IMG_TREE1; break;
//	     case Game.ROADTYPE_MARK:  continue;
	           default:            continue;
					//  System.out.println("Error ID in road stream:"+client_sb.roaditems[ty][1]+" (see at TREE0)");
	                                //  i = IMG_TREE0; break;
	  }
	  img = GetImage(i);
	  gBuffer.drawImage(img,x-((img.getWidth()-Game.MINE_ROADWIDTH)>>1),ty - ((img.getHeight()-Game.MINE_ROADWIDTH)>>1),0);
	}
      }

      // Shot
      if(client_sb.shotX >= 0)
      {
        gBuffer.setColor(0);
        gBuffer.fillRect(client_sb.shotX,client_sb.shotY,2,2);
      }

      // Main car
      // burn car after collision
      i = IMG_PLAYER01+(ticks&1);
      if(gameStatus == Crashed)
        i = IMG_PLAYER_DEAD;
       else
         if(client_sb.car.getState() == client_sb.CAR_STATE_EXPLOSION)
	    i = IMG_PLAYER_BURN01+(ticks&3);
      img = GetImage(i);
      x = client_sb.car.X()+((client_sb.car.WIDTH() - img.getWidth())>>1);
      y = client_sb.car.Y();//+((client_sb.car.HEIGHT() - img.getHeight())>>1);
      if(gameStatus != Crashed || blink)
      gBuffer.drawImage(img,x,y,0);
//    gBuffer.drawRect(client_sb.car.X(),client_sb.car.Y(),client_sb.car.WIDTH(),client_sb.car.HEIGHT());


      // Police cars
      for (i = 0; i < client_sb.MAXPOLICE; i++)
               drawCar(gBuffer, client_sb.policecar[i]);
      // Front cars
      for (i = 0; i < client_sb.MAXFRONTCAR; i++)
               drawCar(gBuffer, client_sb.frontcar[i]);


       // health bar
       img = GetImage(IMG_BAR);
       x = 2; y = (VisHeight - img.getHeight())>>1;

       int h  = img.getHeight()-2;
       int fh = h-(client_sb.lives*h+1)/client_sb.MAXLIVES;
       int w = img.getWidth()-2;
       if(fh>0){
       gBuffer.setColor(HealthBarEmptyColor);
         gBuffer.fillRect(x+1,y+1,w,fh);
       }
       gBuffer.setColor(HealthBarFillColor);
       gBuffer.fillRect(x+1,y+fh+1,w,h-fh);
       gBuffer.drawImage(img,x,y,0);

       // mine ico
       if(client_sb.my_minecounter_delay == client_sb.MINE_READY){
         img = GetImage(IMG_BOMB_ICO);
         gBuffer.drawImage(img,2,0/*VisHeight-img.getHeight()-1*/,0);
       }

       font.drawDigits(gBuffer,0,VisHeight-font.fontHeight,4,client_sb.i_PlayerScore);
//       font.drawDigits(gBuffer,VisWidth-font.fontWidth*3,0,3,client_sb.carspeed/10);



/*********************/
/*
      if(gameStatus!=Crashed || blink){
        // Lives
        img = GetImage(IMG_HEART);
	   for (i=0;i<client_sb.lives-1;i++)
              gBuffer.drawImage(img,VisWidth-(i+1)*(img.getWidth()+2)-10,VisHeight-img.getHeight(),0);
      }
*/
/*********************/

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
      if(baseX !=0 || baseY !=0){
        gBuffer.translate(-baseX,-baseY);
        gBuffer.setClip(0,0,scrwidth,scrheight);
      }

	bufferChanged=false;
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
			        //if(bufferChanged)
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
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
//       client_sb.nextGameStep(client_pmr);
//       repaintBackScreen();
       gameStatus=newStage;
       bufferChanged=true;
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
 if(ByteArray!=null)
  for(int i=0;i<TOTAL_IMAGES_NUMBER;i++)
      if(ByteArray[i]!=null) Elements[i]=null;

  Runtime.getRuntime().gc();
}

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_BAR = 4;
private static final int IMG_BOMB_ICO = 5;
private static final int IMG_PLAYER01 = 6;
private static final int IMG_PLAYER05 = 7;
private static final int IMG_PLAYER_BURN01 = 8;
private static final int IMG_PLAYER_BURN03 = 9;
private static final int IMG_PLAYER_BURN02 = 10;
private static final int IMG_PLAYER_DEAD = 12;
private static final int IMG_CAR01_01 = 13;
private static final int IMG_CAR01_02 = 14;
private static final int IMG_CAR02_01 = 15;
private static final int IMG_CAR02_02 = 16;
private static final int IMG_CAR03_01 = 17;
private static final int IMG_CAR03_02 = 18;
private static final int IMG_CAR04_01 = 19;
private static final int IMG_CAR04_02 = 20;
private static final int IMG_CAR05_01 = 21;
private static final int IMG_CAR05_02 = 22;
private static final int IMG_BURST01 = 23;
private static final int IMG_BURST02 = 24;
private static final int IMG_BURST03 = 25;
private static final int IMG_BURST04 = 26;
private static final int IMG_EXPL01 = 27;
private static final int IMG_EXPL02 = 28;
private static final int IMG_EXPL03 = 29;
private static final int IMG_EXPL04 = 30;
private static final int IMG_EXPL05 = 31;
private static final int IMG_EXPL06 = 32;
private static final int IMG_ROAD01 = 33;
private static final int IMG_ROAD02 = 34;
private static final int IMG_ROAD03 = 35;
private static final int IMG_ROAD04 = 36;
private static final int IMG_ROAD05 = 37;
private static final int IMG_STONE01 = 38;
private static final int IMG_MINE = 39;
private static final int IMG_TREE0 = 40;
private static final int IMG_TREE1 = 41;
private static final int IMG_NFONT = 42;
private static final int IMG_GROUND = 43;
private static final int IMG_PLAYER_BURN04 = 11;
private static final int TOTAL_IMAGES_NUMBER = 44;
}

