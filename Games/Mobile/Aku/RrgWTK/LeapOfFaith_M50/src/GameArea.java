import com.GameKit_6.Frog.*;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.*;

public class GameArea extends Canvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 64;

/*=============================*/

    private static final int KEY_MENU = -4;
    private static final int KEY_CANCEL = -1;
    private static final int KEY_ACCEPT = -4;
    private static final int KEY_BACK = -12;

    public void setScreenLight(boolean state) {
        if (state)  Light.setLightOn();
          else Light.setLightOff();
    }
/*
    public void activateVibrator(long duration){
        Vibrator.triggerVibrator((int) duration);
    }
*/

  private Melody []sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i]!=null)
	    try{
               sounds[i].stop();
	    }catch(Exception e){}
      isFirst = false;
    }

    public void PlayMelody(int n){
//      stopAllMelodies();
      if(midlet._Sound && sounds!=null && sounds[n]!=null){
          sounds[n].play();
      }
    }



    public boolean soundIsPlaying(){
//      if(sounds!=null)
//        for(int i = 0;i<sounds.length; i++)
//           if(sounds[i].getState()==Sound.SOUND_PLAYING)
//               return true;
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
//  private Image BackScreen=null;
//  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected GameletImpl client_sb;
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

  int pressedKey;

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

    client_sb = new GameletImpl(VisWidth, VisHeight, this);
//    client_pmr = new game_PMR();
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
	BACKGROUND_COLOR = 0x0D004C;

    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	BACKGROUND_COLOR = 0xffffff;
    }


    // create gradient

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
       pressedKey = 0;
       direct=GameletImpl.PLAYER_BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(demoLevel);
//	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(direct);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
           stageSleepTime[playGame] = client_sb.i_TimeDelay;
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
       pressedKey = 0;
       CleanSpace();
    }

//    protected void keyRepeated(int keyCode) {keyPressed(keyCode);}

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
				     case Canvas.KEY_NUM2 : direct = GameletImpl.PLAYER_BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
				     case Canvas.KEY_NUM6 : direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = GameletImpl.PLAYER_BUTTON_UP; break;
					       case Canvas.DOWN : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
					           default: direct=GameletImpl.PLAYER_BUTTON_NONE;
				            }
	                          }
				  pressedKey = direct;
				  direct = 0;
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=GameletImpl.PLAYER_BUTTON_NONE;
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
//			     client_pmr.i_Value = client_sb.getRandomInt(GameletImpl.PLAYER_BUTTON_DOWN/*4*/);
		           client_sb.nextGameStep(direct);
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
//			   serviceRepaints();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
				pressedKey = 0;
		                direct=GameletImpl.PLAYER_BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
//	                        client_pmr.i_Value = direct;
                                client_sb.nextGameStep(direct);
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
//                           client_pmr.i_Value = pressedKey;
//			   pressedKey = GameletImpl.PLAYER_BUTTON_NONE;
                           client_sb.nextGameStep(pressedKey);
			   pressedKey = 0;

//			   if(!is_painting)
			     //updateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>=GameletImpl.TOTAL_STAGES)
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

                           client_sb.nextGameStep(0);

			   if(ticks>20){
			     ticks = 0;
			     blink = false;
		             direct=GameletImpl.PLAYER_BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=GameletImpl.PLAYER_BUTTON_NONE;
//	                           client_pmr.i_Value = direct;
                                   client_sb.nextGameStep(direct);
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
    private void drawSprite(Graphics gBuffer, Sprites spr){
      if(spr.lg_SpriteActive) {
         switch(spr.i_objectType){
	   case Sprites.OBJECT_PLAYER:
	                  {
			     int j;
			     if(spr.i_objectState == Sprites.STATE_DEATH) return;// j = IMG_CROC01+spr.i_Frame;
			        else j = IMG_FROG_S_L + spr.i_objectState + (spr.i_Frame<<2);
			     Image  img = GetImage(j);
			     gBuffer.drawImage(img,(spr.i8_mainX>>8)-(img.getWidth()>>1),(spr.i8_mainY>>8)-img.getHeight(),0);

	                  }break;

	   case Sprites.OBJECT_BIRD:
	                  {
//			     Image  img = GetImage(spr.lg_SpriteActive IMG_BIRD01 + spr.i_Frame);
//			     gBuffer.drawImage(img,spr.i8_ScreenX>>8,spr.i8_ScreenY>>8,0);
	                  }break;
	    default: return;
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

        Image img = null;
/**********************/
        gBuffer.setColor(BACKGROUND_COLOR);gBuffer.fillRect(0,0,VisWidth,VisHeight);
//	gBuffer.setColor(0x0);

        // draw bog
	Lief lief;
	int i,j;
        for(i = 0; i < client_sb.i_queue_freemark; i++){
	    lief = client_sb.queue[i];
	    if(lief.lg_active) {
	      switch(lief.i_type){
		case Lief.LIEF      : j = IMG_LEAF07-lief.getPhase(); break;
		case Lief.CIRCLES   : j = IMG_WATER04-lief.getPhase(); break;
		case Lief.ALLIGATOR : j = IMG_CROC01+lief.getPhase(); break;
		        default: continue;
	      }
	      img = GetImage(j);
	      gBuffer.drawImage(img,lief.i_x-(img.getWidth()>>1),lief.i_y-(img.getHeight()>>1),0);
	      switch(lief.i_bonus){
		 case Lief.BONUS_MINUS : j = IMG_BAD_BERRY; break;
		 case Lief.BONUS_PLUS  : j = IMG_GOOD_BERRY; break;
		 case Lief.BONUS_LIFE  : j = IMG_HEART; break;
		 case Lief.BONUS_LOTUS : j = IMG_LOTOS; break;
//					 img = GetImage(IMG_LOTOS);
//					 gBuffer.drawImage(img,lief.i_x+((GameletImpl.VIRTUAL_CELL_WIDTH-img.getWidth())>>1),lief.i_y-img.getHeight()+1,0);
//					 continue;
		  default:continue;
	      }
	      img = GetImage(j);
	      gBuffer.drawImage(img,lief.i_x-(img.getWidth()>>1),lief.i_y-(img.getHeight()),0);
	    }
        }

	// draw player and bird
        if(client_sb.p_Player.i_cellY>client_sb.p_Bird.i_cellY){
	   drawSprite(gBuffer,client_sb.p_Player);
	   drawSprite(gBuffer,client_sb.p_Bird);
        } else {
	   drawSprite(gBuffer,client_sb.p_Bird);
	   drawSprite(gBuffer,client_sb.p_Player);
        }

	font.drawDigits(gBuffer,0,0,4,client_sb.getPlayerScore());

        if(!(gameStatus == Crashed && blink)) {
	   img = Elements[IMG_HEART];
	   for (i=0;i<client_sb.i_Attemptions-1;i++)
              gBuffer.drawImage(img,(i)*(img.getWidth()+2),VisHeight-img.getHeight(),0);
        }


	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, VisWidth>>1,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
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
       direct=GameletImpl.PLAYER_BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
//       client_pmr.i_Value = direct;
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
private static final int IMG_HEART = 4;
private static final int IMG_GOOD_BERRY = 5;
private static final int IMG_BAD_BERRY = 6;
private static final int IMG_LOTOS = 7;
private static final int IMG_FROG_S_L = 8;
private static final int IMG_FROG_S_R = 9;
private static final int IMG_FROG_S_BACK = 10;
private static final int IMG_FROG_S_FRONT = 11;
private static final int IMG_FROG_DEAD = 12;
private static final int IMG_FROG_J_L01 = 13;
private static final int IMG_FROG_J_R01 = 14;
private static final int IMG_FROG_J_BACK_01 = 15;
private static final int IMG_FROG_J_FRONT_01 = 16;
private static final int IMG_FROG_J_L02 = 17;
private static final int IMG_FROG_J_R02 = 18;
private static final int IMG_FROG_J_BACK_02 = 19;
private static final int IMG_FROG_J_FRONT_02 = 20;
private static final int IMG_FROG_J_L03 = 21;
private static final int IMG_FROG_J_R03 = 22;
private static final int IMG_FROG_J_BACK_03 = 23;
private static final int IMG_FROG_J_FRONT_03 = 24;
private static final int IMG_LEAF01 = 25;
private static final int IMG_LEAF02 = 26;
private static final int IMG_LEAF03 = 27;
private static final int IMG_LEAF04 = 28;
private static final int IMG_LEAF05 = 29;
private static final int IMG_LEAF06 = 30;
private static final int IMG_LEAF07 = 31;
private static final int IMG_WATER01 = 32;
private static final int IMG_WATER02 = 33;
private static final int IMG_WATER03 = 34;
private static final int IMG_WATER04 = 35;
private static final int IMG_CROC01 = 36;
private static final int IMG_CROC02 = 37;
private static final int IMG_CROC03 = 38;
private static final int IMG_BIRD01 = 46;
private static final int IMG_BIRD02 = 47;
private static final int IMG_NFONT = 48;
private static final int IMG_CROC04 = 39;
private static final int IMG_CROC05 = 40;
private static final int IMG_CROC06 = 41;
private static final int IMG_CROC07 = 42;
private static final int IMG_CROC_C1 = 43;
private static final int IMG_CROC_C2 = 44;
private static final int IMG_CROC_C3 = 45;
private static final int TOTAL_IMAGES_NUMBER = 49;
}
