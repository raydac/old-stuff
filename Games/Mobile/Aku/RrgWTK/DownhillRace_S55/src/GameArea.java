
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Slalom.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
import com.siemens.mp.game.Light;


public class GameArea extends Canvas implements Runnable, PlayerBlock, LoadListener {

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 80; //WARNING, This file contains not scalable objects (h=64)

/*=============================*/

    private static final int KEY_MENU = -4;
    private static final int KEY_CANCEL = -1;
    private static final int KEY_ACCEPT = -4;
    private static final int KEY_BACK = -12;

    public void setScreenLight(boolean state) {
        if (state)  Light.setLightOn();
          else Light.setLightOff();
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
  private Thread thread=null;

  private Image Heart=null;
  private Image Fin=null;
  private Image levelImage=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
//  private Image levelImage=null;
/*
  private final static int iPDown = 0,
		          iPLeft = 1,
			  iPRight = 2,
			  iOpponent = 3,
			  iOpponentDropped = 4,
			  iOpponentOnFlag = 5,
			  iFlag = 6;
*/
  private Image []Elements;
  private byte [][] ByteArray;


  private int CurrentLevel=3;
  protected Slalom_PMR client_pmr = null;
  protected int direct=Slalom_PMR.DIRECT_NONE;
  protected Slalom_SB client_sb = new Slalom_SB(null);
//  protected int Amount=3;
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int RoadPosition=0, RoadTopMargin=-0;
  private int RoadStep=4;


  /**
   *  константы обозначающие различные состояния игры,
   *  флаг состояния
   */
  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
                            playGame = 3,
			    crashCar = 4,
			    fAnimation = 5,
			    Finished = 6,
			    gameOver = 7;

  private int [] stageSleepTime = {100,100,70,80,100,80,100,100}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  private int finPosition = 0;


  private boolean isShown = false;
  private Image MenuIcon = null;
  public String loadString  = "Loading...";
  public String YourScore  = "Your score:";
  public String NewStage  = null;
  private int LoadingTotal =0, LoadingNow = 0, ticks = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void startIt() {
     loading(67+TOTAL_IMAGES_NUMBER);

     try {
	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock(/*"/res/images.bin",*/this);
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];

           DemoTitle = GetImage(IMG_DEMO); //Image.createImage(DemoTitleImage];
           Heart=GetImage(IMG_HEART); //Image.createImage(DemoTitleImage];
           setBlinkImage(DemoTitle);
	   levelImage = GetImage(IMG_BCKG);
	   Fin = GetImage(IMG_FINISH);

	   BackScreen = Image.createImage(levelImage.getWidth(),levelImage.getHeight());
	   gBackScreen = BackScreen.getGraphics();
//	   pnl = null;

//	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;

           Runtime.getRuntime().gc();

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     } catch(Exception e) {
        System.out.println("Can't read images");
	System.exit(-1);
     }
     Runtime.getRuntime().gc();
    }



    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
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
    }

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

//    client_sb = new Assault_SB(VisWidth, VisHeight, this);
//    client_pmr = new Assault_PMR();
    client_sb.setPlayerBlock(this);

    if (!this.isDoubleBuffered())
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

//    BackScreen=Image.createImage(VisWidth,VisHeight);
//    gBackScreen=BackScreen.getGraphics();

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
    }
    isFirst = true;
  }


   void setBlinkImage(Image img){
      blinkImage=img;
      blinkX=scrwidth>>1;
      blinkY=scrheight-blinkImage.getHeight()-5/*bottomMargin*/;
   }
///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

       Slalom_GSR Rally = (Slalom_GSR)gameStateRecord;
       client_pmr.setDirect(direct);

       RoadPosition+=client_sb.PLAYER_SPEED;
       if (RoadPosition<-levelImage.getHeight())RoadPosition+=levelImage.getHeight();
       return client_pmr;
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Slalom_PMR(Slalom_PMR.DIRECT_NONE);

    }

    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       playerScore=0;
       blink=false;
       RoadPosition=0;
       RoadStep=client_sb.ROAD_SCROLL_SPEED;
       direct=Slalom_PMR.DIRECT_NONE;

        if (CurrentLevel!=level) {
	   CurrentLevel=level;
        }
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Slalom_SB.LEVEL_DEMO);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGame(CurrentLevel);
//	   client_sb.setPlayerBlock(this);
	   gameStatus=playGame;
	}
	client_sb.nextGameStep();
	bufferChanged=true;
	repaint();
    }

    /**
     * Handle a repeated arrow keys as though it were another press.
     * @param keyCode the key pressed.
     */
    protected void keyRepeated(int keyCode) {
/*
        int action = getGameAction(keyCode);
        switch (action) {
        case Canvas.LEFT:
        case Canvas.RIGHT:
//        case Canvas.UP:
//        case Canvas.DOWN:
            keyPressed(keyCode);
	    break;
        default:
            break;
        }
*/    }

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
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=(direct|Slalom_PMR.DIRECT_RIGHT)&(~Slalom_PMR.DIRECT_LEFT); break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=(direct|Slalom_PMR.DIRECT_LEFT)&(~Slalom_PMR.DIRECT_RIGHT); break;
				      default:
				            switch(getGameAction(keyCode)){
					      case Canvas.RIGHT: direct=(direct|Slalom_PMR.DIRECT_RIGHT)&(~Slalom_PMR.DIRECT_LEFT); break;
	                                      case Canvas.LEFT: direct=(direct|Slalom_PMR.DIRECT_LEFT)&(~Slalom_PMR.DIRECT_RIGHT); break;
						   default:direct=Slalom_PMR.DIRECT_NONE;
				            }
	                       }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);

              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	   direct=Slalom_PMR.DIRECT_NONE;

//              switch (/*getGameAction(*/keyCode/*)*/) {
//  	        case Canvas.KEY_NUM6/*RIGHT*/: direct&=(~Slalom_PMR.DIRECT_RIGHT);break;
//	        case Canvas.KEY_NUM4/*LEFT*/: direct&=(~Slalom_PMR.DIRECT_LEFT); break;
//		case UP: direct&=(~Slalom_PMR.DIRECT_UP); break;
//	        case DOWN:direct&=(~Slalom_PMR.DIRECT_DOWN); break;
//              }
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      int blinks=0;
      while(animation) {
  //       blinks++;
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   blink = (ticks&4) == 0;
                          // RoadPosition+=RoadStep;
                          // if (RoadPosition>=levelImage.getHeight())RoadPosition-=levelImage.getHeight();
			   Slalom_GSR loc=(Slalom_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Slalom_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink = false;
			   }

			   repaint();
			  } break;
           case playGame :{
	                   ticks=0;
			   client_sb.nextGameStep();
			   Slalom_GSR loc=(Slalom_GSR)client_sb.getGameStateRecord();
//			   if (loc.getGameState()==Slalom_GSR.GAMESTATE_OVER) {
//	                       gameStatus=gameOver;
//                               playerScore=loc.getPlayerScores();
//			   }
			   switch (loc.getPlayerState()) {
			     case Slalom_GSR.PLAYER_COLLIDED: gameStatus=crashCar; break;
//			     case Slalom_GSR.PLAYER_OUTFIELD: gameStatus=crashCar; break;
			     case Slalom_GSR.PLAYER_FINISHED: gameStatus=fAnimation;
			                                      finPosition=scrheight;
                                                              playerScore=loc.getPlayerScores();
							      break;

			   }
			   repaint();
			  } break;
	    case crashCar:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
			     if (((Slalom_GSR)client_sb.getGameStateRecord()).getGameState()==Slalom_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
	                            client_sb.resumeGame();
		                    direct=Slalom_PMR.DIRECT_NONE;
			            gameStatus=playGame;
			      }
			   }
			   repaint();
			   break;
	    case fAnimation:
			      Slalom_GSR loc=(Slalom_GSR)client_sb.getGameStateRecord();
	                      if(finPosition>(client_sb.ROAD_HEIGHT>>1)){

			         RoadPosition+=client_sb.PLAYER_SPEED;
                                 if (RoadPosition<-levelImage.getHeight())RoadPosition+=levelImage.getHeight();

				 int ofsX=(client_sb.ROAD_WIDTH>>1)-4-loc.getPlayerX();
				 if (Math.abs(ofsX)<(client_sb.HORIZ_PLAYER_SPEED*5))
	                              finPosition+=client_sb.PLAYER_SPEED;
				 if(Math.abs(ofsX)>client_sb.HORIZ_PLAYER_SPEED)
				    if(ofsX<0){
				      loc.setPlayerX(loc.getPlayerX()-client_sb.HORIZ_PLAYER_SPEED);
				      direct = Slalom_PMR.DIRECT_LEFT;
				    } else {
				        loc.setPlayerX(loc.getPlayerX()+client_sb.HORIZ_PLAYER_SPEED);
					direct = Slalom_PMR.DIRECT_RIGHT;
				    }
				  else direct = Slalom_PMR.DIRECT_NONE;
	                     } else {
	                             if (loc.getPlayerY()<Slalom_SB.ROAD_HEIGHT)
				         loc.setPlayerY(loc.getPlayerY()-Slalom_SB.PLAYER_SPEED);
	                             else {
					   gameStatus=Finished;
					   //ticks = 0;
		                        }
	                     }
		             repaint();
			     break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			      midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
			   repaint();
			   break;
 	 }

        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
          else
          if(workDelay>0)
             Thread.sleep(workDelay);

	} catch (Exception e) {}

	workDelay = System.currentTimeMillis();

      }
    }

    protected void DoubleBuffer(Graphics gBuffer){
//      if (bufferChanged) {

	// prepare screen

        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth,scrheight);

        gBackScreen.drawImage(levelImage,0,RoadPosition,0);
        gBackScreen.drawImage(levelImage,0,RoadPosition+levelImage.getHeight(),0);

        Slalom_GSR loc=(Slalom_GSR)client_sb.getGameStateRecord();


	// Opponents cars

	Obstacle [] o = loc.getObstacleArray();
	Image img=null;

   /// Player
	switch (direct) {
	  case Slalom_PMR.DIRECT_LEFT:img=Elements[IMG_PL_LEFT];
				      break;
	  case Slalom_PMR.DIRECT_RIGHT:img=Elements[IMG_PL_RIGHT];
				      break;
	  default:img=Elements[IMG_PL_CNTR];
	}
        gBackScreen.drawImage(img,loc.getPlayerX(),loc.getPlayerY()/*+RoadTopMargin*/,0);

   /// Obstacles
	for (int i=0;i<o.length;i++){
	    switch (o[i]._type) {

	      case Obstacle.FLAG : img=Elements[IMG_TREE];
					  break;
	      case Obstacle.DROPPED_SKIER : img=Elements[IMG_OPPODROP];
					  break;
	      case Obstacle.NORMAL_SKIER : img=Elements[IMG_OPPO];
					  break;
	      case Obstacle.SKIER_ON_FLAG : img=Elements[IMG_OPPOTREE];
					  break;
			  default: continue;
	    }
            gBackScreen.drawImage(img,o[i]._x,o[i]._y/*+RoadTopMargin*/,0);
	}

	//  Player

        if (gameStatus==fAnimation)
	       gBackScreen.drawImage(Fin,((client_sb.ROAD_WIDTH-Fin.getWidth())>>1),finPosition,0);

	   gBuffer.drawImage(BackScreen,baseX,baseY,0);

        if(!(gameStatus == crashCar && blink))
	 for (int i=0;i<loc.getAttemptNumber();i++){
           gBuffer.drawImage(Heart,baseX+VisWidth-(i+1)*(Heart.getWidth()+2)-10,baseY+VisHeight -Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
	if (gameStatus == demoPlay && blink && blinkImage!=null) {
	   gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
//        gBuffer.drawRect(baseX,baseY,levelImage.getWidth(),levelImage.getHeight());

	bufferChanged=false;
    }
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
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
			  /*
	      case newStage:  {

	                   g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			   g.setColor(InkCOLOR);    // drawin' flyin' text
			   Font f = Font.getDefaultFont();
			   g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                   g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			   g.setFont(f);
	                  } break;
*/
	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
	                    g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			    g.setColor(InkCOLOR);    // drawin' flyin' text
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WON),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :   if (this.isDoubleBuffered())
			      {

		                DoubleBuffer(g);
			      }
			      else
			      {
			        if(bufferChanged)
		                   DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                             	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
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


    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus==crashCar);
    }
    public void gameLoaded(){
       playerScore=0;
       blink=false;
       RoadPosition=0;
//       RoadStep=client_sb.ROAD_SCROLL_SPEED;
       direct=Slalom_PMR.DIRECT_NONE;
       //client_sb.newGame(CurrentLevel);
       gameStatus=playGame;
       ticks = 0;
       bufferChanged=true;
       gameStatus=playGame;
       client_sb.nextGameStep();
       Runtime.getRuntime().gc();
       repaint();
    }

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_FINISH = 3;
private static final int IMG_BCKG = 4;
private static final int IMG_OPPOTREE = 5;
private static final int IMG_TREE = 6;
private static final int IMG_OPPODROP = 7;
private static final int IMG_DEMO = 8;
private static final int IMG_PL_CNTR = 9;
private static final int IMG_PL_LEFT = 10;
private static final int IMG_PL_RIGHT = 11;
private static final int IMG_OPPO = 12;
private static final int IMG_HEART = 13;
private static final int TOTAL_IMAGES_NUMBER = 14;

}

/*
.
DHRACE,0
LOST,1
WON,2
BCKG,3

DEMO,4
HEART,5

PL_CNTR,6
PL_LEFT,7
PL_RIGHT,8
OPPO,9
OPPODROP,10
OPPOTREE,11
TREE,12
FINISH,13

.
.
*/
