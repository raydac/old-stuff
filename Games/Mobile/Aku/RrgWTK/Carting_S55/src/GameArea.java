import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Rally.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, PlayerBlock,LoadListener {

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 80;


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

  /**
   *  технологические переменные
   */
  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  private boolean isShown=false;
  private Thread thread=null;

  private Image AttemptsImage=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Image roadImage=null;
  private Image Fin=null;
  private Graphics gBuffer=null;
  private Graphics roadGraphics=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image levelImage=null;
  private Image []Elements;
  private byte [][] ByteArray;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

  private int CurrentLevel=3;
  protected Rally_PMR client_pmr = null;
  protected int direct=Rally_PMR.DIRECT_NONE;
  protected Rally_SB client_sb = new Rally_SB(/*VisWidth, VisHeight, */null);;
//  protected int Amount=3;
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int RoadPosition=0, RoadTopMargin=-0;
  private final static int RoadStep=4;
  int ticks=0, RoadBorder = 10;

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
			    fAnimation = 4,
			    crashCar = 5,
			    Finished = 6,
			    gameOver = 7;

  private int [] stageSleepTime = {100,100,100,90,100,200,100,100}; // in ms

  private int playerScore = 0;
  private int carPosition = 0;
  private int finPosition = 0;
  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private int crashDelay=6;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  private final static int GC_DELAY = 50;

//******* import section *************

  private Image MenuIcon = null;
  public String loadString  = "Loading...";
  public String YourScore  = "Your score:";
  public String NewStage  = null;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

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

           DemoTitle= Elements[IMG_DEMO];
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];

           levelImage = Elements[IMG_ROAD];
	   Fin = Elements[IMG_FINISH];

           AttemptsImage=Elements[IMG_HEART]; //Image.createImage(DemoTitleImage);
           setBlinkImage(DemoTitle);

	   roadImage = Image.createImage(levelImage.getWidth(),levelImage.getHeight());
	   roadGraphics = roadImage.getGraphics();
	   ib = null;

           Runtime.getRuntime().gc();

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


    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
       repaint();
    }

/* ***************************************************** */



  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    client_sb.setPlayerBlock(this);

    VisWidth = Math.min(scrwidth,WIDTH_LIMIT);
    VisHeight = Math.min(scrheight,HEIGHT_LIMIT);

//    client_sb = new Rally_SB(/*VisWidth, VisHeight, */null);
    client_pmr = new Rally_PMR(Rally_PMR.DIRECT_NONE);

    if (!this.isDoubleBuffered())
    {
//      System.out.println("not dbufferred");
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

       Rally_GSR Rally = (Rally_GSR)gameStateRecord;
       client_pmr.setDirect(direct);

       RoadPosition+=RoadStep;
       if (RoadPosition>=roadImage.getHeight())RoadPosition-=roadImage.getHeight();
       return client_pmr;
    }

    public void showNotify() {
      isShown=true;
    }
    public void hideNotify() {
      isShown=false;
      direct = 0;
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Rally_PMR(Rally_PMR.DIRECT_NONE);

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
//       stageSleepTime[gameStatus]=100;
       //RoadStep=3;//client_sb.ROAD_SCROLL_SPEED;
       direct=Rally_PMR.DIRECT_NONE;
/*
        if (CurrentLevel!=level) {
	   CurrentLevel=level;
	   try {
	     levelImage=Image.createImage(Picture);
	   } catch(Exception e) {
	       System.out.println("Can't read image");
	       gameStatus = titleStatus;
	       return;
	   }
        }
*/
	if (gameStatus==titleStatus) {
	    client_sb.setPlayerBlock(new DemoPlayer());
	    client_sb.newGame(Rally_SB.LEVEL_DEMO);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.setPlayerBlock(this);
	   client_sb.newGame(level);
	   gameStatus=playGame;
	}
	bufferChanged=true;
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
/*
	      case Finished:
	      case gameOver: if (ticks<35) {if (ticks>3)ticks=35; break;}
	      case demoPlay:
              case titleStatus : {
		                    switch (keyCode) {
				      case -4:
				                midlet.ShowMainMenu();
				                break;
				      case -12:  midlet.ShowQuitMenu(false);
				                break;
				      default:
	                                       gameStatus=titleStatus;
			                       ticks=0;
			                       blink=false;
					       repaint();
		                    }
				 } break;
*/
              case playGame :  {
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true); else
	                       switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=(direct|Rally_PMR.DIRECT_RIGHT)&(~Rally_PMR.DIRECT_LEFT); break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=(direct|Rally_PMR.DIRECT_LEFT)&(~Rally_PMR.DIRECT_RIGHT); break;
				      case Canvas.KEY_NUM2/*UP*/: direct=(direct|Rally_PMR.DIRECT_UP)&(~Rally_PMR.DIRECT_DOWN); break;
	                              case Canvas.KEY_NUM8/*DOWN*/:direct=(direct|Rally_PMR.DIRECT_DOWN)&(~Rally_PMR.DIRECT_UP); break;
				         default:
					         switch(getGameAction(keyCode)){
						    case Canvas.RIGHT/*RIGHT*/: direct=(direct|Rally_PMR.DIRECT_RIGHT)&(~Rally_PMR.DIRECT_LEFT); break;
	                                            case Canvas.LEFT/*LEFT*/: direct=(direct|Rally_PMR.DIRECT_LEFT)&(~Rally_PMR.DIRECT_RIGHT); break;
				                    case Canvas.UP/*UP*/: direct=(direct|Rally_PMR.DIRECT_UP)&(~Rally_PMR.DIRECT_DOWN); break;
	                                            case Canvas.DOWN/*DOWN*/:direct=(direct|Rally_PMR.DIRECT_DOWN)&(~Rally_PMR.DIRECT_UP); break;
						      default:direct=Rally_PMR.DIRECT_NONE;
					         }
	                       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
              switch (/*getGameAction(*/keyCode/*)*/) {
  	        case Canvas.KEY_NUM6/*RIGHT*/: direct&=(~Rally_PMR.DIRECT_RIGHT);break;
	        case Canvas.KEY_NUM4/*LEFT*/: direct&=(~Rally_PMR.DIRECT_LEFT); break;
		case Canvas.KEY_NUM2/*UP*/: direct&=(~Rally_PMR.DIRECT_UP); break;
	        case Canvas.KEY_NUM8/*DOWN*/:direct&=(~Rally_PMR.DIRECT_DOWN); break;
		  default:
		    switch (getGameAction(keyCode)) {
		            case Canvas.RIGHT: direct&=(~Rally_PMR.DIRECT_RIGHT);break;
	                    case Canvas.LEFT: direct&=(~Rally_PMR.DIRECT_LEFT); break;
		            case Canvas.UP: direct&=(~Rally_PMR.DIRECT_UP); break;
	                    case Canvas.DOWN:direct&=(~Rally_PMR.DIRECT_DOWN); break;
		    }
              }
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

        if(isShown)
 	 switch (gameStatus) {
           case titleStatus:
	                if (ticks<2) {
			   //LostWon = null;
			   System.gc();
	                }
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   blink=(ticks++&16)!=0;
		           client_sb.nextGameStep();
                           RoadPosition+=RoadStep;
                           if (RoadPosition>=levelImage.getHeight())RoadPosition-=levelImage.getHeight();
			   Rally_GSR loc=(Rally_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Rally_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               playerScore=loc.getPlayerScores();
			       ticks=0;
			   }

			   repaint();
			  } break;
           case playGame :{
			   client_sb.nextGameStep(); ticks=0;
			   Rally_GSR loc=(Rally_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Rally_GSR.GAMESTATE_OVER) {
	                       gameStatus=gameOver;
			       //removeCommand(midlet.backCommand);
                               playerScore=loc.getPlayerScores();
			   }
			   switch (loc.getPlayerState()) {
			     case Rally_GSR.PLAYER_COLLIDED: gameStatus=crashCar; break;
			     case Rally_GSR.PLAYER_OUTFIELD: gameStatus=crashCar; break;
			     case Rally_GSR.PLAYER_FINISHED: gameStatus=fAnimation;
			                                     finPosition=-Fin.getHeight();
				                             carPosition=loc.getPlayerY();
                                                             playerScore=loc.getPlayerScores();
							     break;
			   }
			   repaint();
			  } break;
	    case crashCar:
			   if (ticks++>=crashDelay) {
			     client_sb.resumeGame();
		             direct=Rally_PMR.DIRECT_NONE;
			     gameStatus=playGame;
			     ticks =0;
			   } else
			       blink^=true;
			   repaint();
			   break;
	    case fAnimation: if(finPosition<(client_sb.ROAD_HEIGHT>>1)){

			         RoadPosition+=RoadStep;
			         if (RoadPosition>=roadImage.getHeight())RoadPosition-=roadImage.getHeight();

			         Rally_GSR loc=(Rally_GSR)client_sb.getGameStateRecord();

				 int ofsX=(client_sb.ROAD_WIDTH>>1)-4-loc.getPlayerX();

				 if (Math.abs(ofsX)<(client_sb.HORIZ_PLAYER_SPEED*5))
	                              finPosition+=RoadStep;
				 if(Math.abs(ofsX)>client_sb.HORIZ_PLAYER_SPEED)
				    if(ofsX<0)
				      loc.setPlayerX(loc.getPlayerX()-client_sb.HORIZ_PLAYER_SPEED);
				      else
				        loc.setPlayerX(loc.getPlayerX()+client_sb.HORIZ_PLAYER_SPEED);

	                     } else {
	                             if (Rally_SB.PLAYER_CELL_HEIGHT+carPosition>=0) carPosition-=4;
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
			      midlet.newScore(((Rally_GSR)client_sb.getGameStateRecord()).getPlayerScores());
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
      if(baseX >0 || baseY >0){
        gBuffer.setColor(0x0);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,VisWidth,VisHeight);
      }
////////////////////////////////////////////////


//      if (bufferChanged) {

	// prepare screen

        roadGraphics.drawImage(levelImage,0,RoadPosition-levelImage.getHeight(),Graphics.TOP|Graphics.LEFT);
        roadGraphics.drawImage(levelImage,0,RoadPosition,Graphics.TOP|Graphics.LEFT);

        Rally_GSR loc=(Rally_GSR)client_sb.getGameStateRecord();


	// Opponents cars

	Obstacle [] o = loc.getObstacleArray();
	Image img=null;
//CAR,4
//CAR_DST,5
//OP,6
//OP_DST,7

	for (int i=0;i<o.length;i++){

	    switch (o[i]._type) {
	      case Obstacle.NORMAL_CAR : img=Elements[IMG_OP];
					  break;
	      case Obstacle.FIRING_CAR : img=Elements[IMG_OP_DST];
					  break;
	      case Obstacle.MOVING_CAR : img=Elements[IMG_OP2];
					  break;
	      case Obstacle.FIRING_MOVED_CAR : img=Elements[IMG_OP2_DST];
					  break;

			  default: continue;
	    }
  	    roadGraphics.drawImage(img,RoadBorder+o[i]._x,o[i]._y+RoadTopMargin,Graphics.TOP|Graphics.LEFT);
	}
	//  Players car
	int offset=carPosition;
	if (gameStatus!=fAnimation) offset=loc.getPlayerY()+RoadTopMargin;
	if (gameStatus==crashCar){
	  if (!blink) roadGraphics.drawImage(Elements[IMG_CAR_DST],RoadBorder+loc.getPlayerX(),offset,Graphics.TOP|Graphics.LEFT);
	} else roadGraphics.drawImage(Elements[IMG_CAR],RoadBorder+loc.getPlayerX(),offset,Graphics.TOP|Graphics.LEFT);

        if (gameStatus==fAnimation)
	       roadGraphics.drawImage(Fin,RoadBorder+((client_sb.ROAD_WIDTH-Fin.getWidth())>>1),finPosition,Graphics.LEFT|Graphics.TOP);

        gBuffer.drawImage(roadImage,0,0,Graphics.TOP|Graphics.LEFT);


	if (gameStatus==demoPlay) {
	  if (blink && blinkImage!=null)  gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	} else
	// drawin' the left attempts
	 for (int i=0;i<loc.getAttemptNumber();i++){
           gBuffer.drawImage(AttemptsImage,VisWidth-(i+1)*(AttemptsImage.getWidth()+2)-10,VisHeight-AttemptsImage.getHeight()-2,Graphics.TOP|Graphics.LEFT);
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
	                  g.drawImage(GetImage(IMG_CARTING),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
//				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
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
      return (gameStatus == playGame || gameStatus == crashCar);
    }
    public void gameLoaded(){
       playerScore=0;
       blink=false;
       RoadPosition=0;
       direct=Rally_PMR.DIRECT_NONE;
       client_sb.setPlayerBlock(this);
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }

private static final int IMG_CARTING = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_ROAD = 4;
private static final int IMG_CAR = 5;
private static final int IMG_CAR_DST = 6;
private static final int IMG_OP = 7;
private static final int IMG_OP_DST = 8;
private static final int IMG_OP2 = 9;
private static final int IMG_OP2_DST = 10;
private static final int IMG_FINISH = 11;
private static final int IMG_HEART = 12;
private static final int TOTAL_IMAGES_NUMBER = 13;

}

/*
.
CARTING,0
LOST,1
WON,2
ROAD,3
CAR,4
CAR_DST,5
OP,6
OP_DST,7
FINISH,8
DEMO,9
HEART,10
.
.
*/
