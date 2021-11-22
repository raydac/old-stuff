
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Asteroids.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock,LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private int BACKGROUND_STEP_X = 3;
  private int BACKGROUND_STEP_Y = 3;
  private int hitX = 0;
  private int hitY = 0;
  private int hitTickCounter = 0;
  private final static int BROKEN_GLASS_DELAY = 25;
  private boolean isShown = false;

  private Image Title=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Image MenuIcon = null;
  private Image Lost = null;
  private Image Won = null;
  private Graphics gBuffer=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image AttemptsImage=null;

  public String loadString  = "Loading...";
  public String YourScore  = "Your Score: ";

  public final static int Asteroids_quantity = 10;
  public final static int Planet_quantity = 4;
  private Image items[];
/*
  private Image []astro = new Image[Asteroids_quantity];
  private Image []planet = new Image[Planet_quantity];
  private Image []pointer =new Image[4/*PointerImages.length* /];
*/
  private Image bkgImage = null;
  private Image iGlass = null;
  private int bkgX = 0, bkgY = 0;

  private int CurrentLevel=3;
  protected Asteroids_PMR client_pmr = null;
  protected int direct=Asteroids_PMR.MOVE_NONE;
  protected Asteroids_SB client_sb = new Asteroids_SB(null);
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean keyReverse = true;
  public boolean LanguageCallBack = false;


  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
                            playGame = 3,
			    Finished = 4,
			    gameOver = 5;
  private int [] stageSleepTime = {100,100,170,140,250,250}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;


  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    client_sb.setPlayerBlock(this);
    Buffer=Image.createImage(101/*scrwidth*/,64/*scrheight*/);
    gBuffer=Buffer.getGraphics();
    baseX = (scrwidth - 101)>>1;
    baseY = (scrheight - 64)>>1;
    gameStatus=notInitialized;
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

       Asteroids_GSR Rally = (Asteroids_GSR)gameStateRecord;
       client_pmr.setDirect(direct);
   if ((direct&Asteroids_PMR.MOVE_LEFT)>0) {
            bkgX -= BACKGROUND_STEP_X;
	    if (bkgX < 0) bkgX += bkgImage.getWidth();
   }
   if ((direct&Asteroids_PMR.MOVE_RIGHT)>0) {
            bkgX += BACKGROUND_STEP_X;
	    if (bkgX > bkgImage.getWidth()) bkgX -= bkgImage.getWidth();
   }
   if ((direct&Asteroids_PMR.MOVE_UP)>0) {
            bkgY -= BACKGROUND_STEP_Y;
	    if (bkgY < 0) bkgY += bkgImage.getHeight();
   }
   if ((direct&Asteroids_PMR.MOVE_DOWN)>0) {
            bkgY += BACKGROUND_STEP_Y;
	    if (bkgY > bkgImage.getHeight()) bkgY -= bkgImage.getHeight();
   }

//       RoadPosition+=RoadStep;
//       if (RoadPosition>=levelImage.getHeight())RoadPosition-=levelImage.getHeight();
       return client_pmr;
    }

    public void start() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
    }

    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void start(int kkk) {
     start();
     loading(TOTAL_IMAGES_NUMBER+67);


     try {
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
/*
ASTEROIDS,0
LOST,1
WIN,2
STARFIELD,3
GLASS,4
DEMO,5
HEART,6

ASTER0,7
ASTER1,8
ASTER2,9
ASTER3,10
ASTER4,11
ASTER5,12
ASTER6,13
ASTER7,14
ASTER8,15
ASTER9,16

PLANET0,17
PLANET1,18
PLANET2,19
PLANET3,20

PNTR_LEFT,21
PNTR_RIGHT,22
PNTR_UP,23
PNTR_DOWN,24
*/
           items = (new ImageBlock(this))._image_array;
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];

           Title=items[IMG_ASTEROIDS];
           Lost=items[IMG_LOST];
           Won=items[IMG_WIN];

           bkgImage=items[IMG_STARFIELD];
           AttemptsImage=items[IMG_HEART];
           iGlass = items[IMG_GLASS];

/*
           astro[9]=items[IMG_ASTER9];
	   planet[3] = items[IMG_PLANET3];


            astro[0]=items[IMG_ASTER0];

            astro[1]=items[IMG_ASTER1];
            astro[2]=items[IMG_ASTER2];
            astro[3]=items[IMG_ASTER3];
            astro[4]=items[IMG_ASTER4];

            astro[5]=items[IMG_ASTER5];
            astro[6]=items[IMG_ASTER6];
            astro[7]=items[IMG_ASTER7];
            astro[8]=items[IMG_ASTER8];

	    planet[0] = items[IMG_PLANET0];
	    planet[1] = items[IMG_PLANET1];
	    planet[2] = items[IMG_PLANET2];

	    pointer[0] = items[IMG_PNTR_LEFT];
	    pointer[1] = items[IMG_PNTR_RIGHT];
	    pointer[2] = items[IMG_PNTR_UP];
	    pointer[3] = items[IMG_PNTR_DOWN];
*/
           DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage);

           setBlinkImage(DemoTitle);
     } catch(Exception e) {
//       System.out.println("Can't read images");
     }

     Runtime.getRuntime().gc();

    }





    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Asteroids_PMR();

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
       playerScore=0;
       blink=false;
       bkgX=0;
       bkgY=0;
       hitY = 0xffff;
       hitTickCounter =0;
       direct=Asteroids_PMR.MOVE_NONE;

        if (CurrentLevel!=level) {
	   CurrentLevel=level;
        }
	if (gameStatus==titleStatus) {
//	    client_sb.setPlayerBlock(new DemoPlayer());
	    client_sb.newGame(Asteroids_SB.LEVEL0);
            client_sb.nextGameStep();
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//	   client_sb.setPlayerBlock(this);
	   client_sb.newGame(CurrentLevel);
           client_sb.nextGameStep();
	   gameStatus=playGame;
	}
	bufferChanged=true;
	repaint();
    }

    public void loading(){
       gameStatus=notInitialized;
       repaint();
    }


    public void endGame() {
        ticks =0;
	gameStatus=titleStatus;
	repaint();
    }


    public void showNotify(){
       isShown = true;
    }

    public void hideNotify(){
       isShown = false;;
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
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
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
			       int action = getGameAction(keyCode);
			       if (keyReverse)
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=(direct|Asteroids_PMR.MOVE_RIGHT)&(~Asteroids_PMR.MOVE_LEFT); break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=(direct|Asteroids_PMR.MOVE_LEFT)&(~Asteroids_PMR.MOVE_RIGHT); break;
				      case Canvas.KEY_NUM2/*UP*/: direct=(direct|Asteroids_PMR.MOVE_UP)&(~Asteroids_PMR.MOVE_DOWN); break;
	                              case Canvas.KEY_NUM8/*DOWN*/:direct=(direct|Asteroids_PMR.MOVE_DOWN)&(~Asteroids_PMR.MOVE_UP); break;

				   default:direct=Asteroids_PMR.MOVE_NONE;
	                          }
				else
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM4/*LEFT*/: direct=(direct|Asteroids_PMR.MOVE_RIGHT)&(~Asteroids_PMR.MOVE_LEFT); break;
	                              case Canvas.KEY_NUM6/*RIGHT*/: direct=(direct|Asteroids_PMR.MOVE_LEFT)&(~Asteroids_PMR.MOVE_RIGHT); break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=(direct|Asteroids_PMR.MOVE_UP)&(~Asteroids_PMR.MOVE_DOWN); break;
	                              case Canvas.KEY_NUM2/*UP*/:direct=(direct|Asteroids_PMR.MOVE_DOWN)&(~Asteroids_PMR.MOVE_UP); break;

				   default:direct=Asteroids_PMR.MOVE_NONE;
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	   if (keyReverse)
              switch (getGameAction(keyCode)) {
  	        case RIGHT: direct&=(~Asteroids_PMR.MOVE_RIGHT);break;
	        case LEFT: direct&=(~Asteroids_PMR.MOVE_LEFT); break;
		case UP: direct&=(~Asteroids_PMR.MOVE_UP); break;
	        case DOWN:direct&=(~Asteroids_PMR.MOVE_DOWN); break;
              }
	     else
              switch (getGameAction(keyCode)) {
  	        case LEFT: direct&=(~Asteroids_PMR.MOVE_RIGHT);break;
	        case RIGHT: direct&=(~Asteroids_PMR.MOVE_LEFT); break;
		case DOWN: direct&=(~Asteroids_PMR.MOVE_UP); break;
	        case UP:direct&=(~Asteroids_PMR.MOVE_DOWN); break;
              }
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      while(animation) {
  //       blinks++;
        if (hitTickCounter>0)hitTickCounter--;
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
/*
	if (gcActivator++>10){
	   gcActivator = 0;
           System.gc();
           Thread.yield();
	   System.out.println("Runned");
	}
*/
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   Asteroids_GSR loc=(Asteroids_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Asteroids_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
           case playGame :{
			   client_sb.nextGameStep();
			   Asteroids_GSR loc=(Asteroids_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Asteroids_GSR.GAMESTATE_OVER) {
	                       gameStatus=gameOver;
                               playerScore=loc.getPlayerScores();
			   }
			   switch (loc.getPlayerState()) {
			     case Asteroids_GSR.PLAYERSTATE_KILLED: gameStatus=gameOver; break;
			     case Asteroids_GSR.PLAYERSTATE_HIT: {
			              hitX = loc.getLastHitX();
			              hitY = loc.getLastHitY();
				      hitTickCounter = BROKEN_GLASS_DELAY;
			     }
			     break;
			     case Asteroids_GSR.PLAYERSTATE_FINISHED: gameStatus=Finished; break;
			   }
			   repaint();
			  } break;
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

//        gBuffer.setColor(0xffffff);
//        gBuffer.fillRect(0, 0, 101/*scrwidth*/,64/*scrheight*/);


        Asteroids_GSR loc=(Asteroids_GSR)client_sb.getGameStateRecord();
/// night sky
	int dx=0/*baseX*/+bkgX;
	int dy=0/*baseY*/+bkgY;
        try {
             gBuffer.drawImage(bkgImage,dx,dy,Graphics.BOTTOM|Graphics.LEFT);
             gBuffer.drawImage(bkgImage,dx,dy,Graphics.BOTTOM|Graphics.RIGHT);
             gBuffer.drawImage(bkgImage,dx,dy,Graphics.TOP|Graphics.LEFT);
             gBuffer.drawImage(bkgImage,dx,dy,Graphics.TOP|Graphics.RIGHT);
        } catch(Exception e)
	      {}



	int n=Planet_quantity-(loc.getPlanetDistance()*Planet_quantity/client_sb.PLANET_INIT_Z)-1;
	if (n<0) n=0;
	int ddx=0/*baseX*/+loc.getPlanetRX()+(client_sb.ASTEROID_WIDTH>>1);
	int ddy=0/*baseY*/+loc.getPlanetRY()+(client_sb.ASTEROID_HEIGHT>>1);

        try {
             gBuffer.drawImage(items[IMG_PLANET0+n],ddx,ddy,Graphics.HCENTER|Graphics.VCENTER);
        } catch(Exception e)
	      {}

	Asteroid [] o = loc.getVisibleArray();

//	Image img=null;
	dx=0/*baseX*/+client_sb.PLAYER_CENTER_HORIZ+(client_sb.ASTEROID_WIDTH>>1);
	dy=0/*baseY*/+client_sb.PLAYER_CENTER_VERT+(client_sb.ASTEROID_HEIGHT>>1);

	for (int i=0;i<loc.getVisibleCounter();i++){
	   n=Asteroids_quantity-((o[i]._z-1)*Asteroids_quantity/client_sb.ASTEROID_INIT_Z)-1;
	  // if (n<0)n=0;
	 //  if (n>9)n=9;
           try {
             gBuffer.drawImage(items[IMG_ASTER0+n],
	                     o[i]._rx+dx,
			     o[i]._ry+dy,
			     Graphics.HCENTER|Graphics.VCENTER);
           } catch(Exception e)
	      {}

            gBuffer.setColor(0x0);
//	    gBuffer.drawRect(0/*baseX*/+o[i]._rx+client_sb.PLAYER_CENTER_HORIZ, 0/*baseY*/+o[i]._ry+client_sb.PLAYER_CENTER_VERT,
//	                     client_sb.ASTEROID_WIDTH,client_sb.ASTEROID_HEIGHT);


	}

	   if (ddx<0)
	       gBuffer.drawImage(items[IMG_PNTR_LEFT],-baseX,0/*baseY*/+(scrheight>>1),Graphics.VCENTER|Graphics.LEFT);
	   if (ddx>scrwidth)
	       gBuffer.drawImage(items[IMG_PNTR_RIGHT],0/*baseX*/+scrwidth,0/*baseY*/+(scrheight>>1),Graphics.VCENTER|Graphics.RIGHT);
	   if (ddy<0)
	       gBuffer.drawImage(items[IMG_PNTR_UP],0/*baseX*/+(scrwidth>>1),0/*baseY*/,Graphics.TOP|Graphics.HCENTER);
	   if (ddy>scrheight)
	       gBuffer.drawImage(items[IMG_PNTR_DOWN],0/*baseX*/+(scrwidth>>1),0/*baseY*/+scrheight-10,Graphics.BOTTOM|Graphics.HCENTER);

	 for (int i=0;i<loc.getPlayerAttemption();i++){
           gBuffer.drawImage(AttemptsImage,0/*baseX*/+scrwidth-(i+1)*(AttemptsImage.getWidth()+2)-10,0/*baseY*/+scrheight-AttemptsImage.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }

	if (hitTickCounter>0)
	   gBuffer.drawImage(iGlass,dx+hitX, dy+hitY,Graphics.HCENTER|Graphics.VCENTER);




	if (blink && blinkImage!=null) {
	   gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
        gBuffer.setColor(0x0);
//        gBuffer.drawRect(0/*baseX*/,0/*baseY*/,levelImage.getWidth(),levelImage.getHeight());
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
	                  gBuffer.setColor(0xffffff);gBuffer.fillRect(0, 0, scrwidth,scrheight);
		          gBuffer.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  gBuffer.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString("Loading ...["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1)-14,Graphics.TOP|Graphics.HCENTER);
	                  gBuffer.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  gBuffer.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  gBuffer.setFont(f);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);

	                  } break;
              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(Title,0,scrheight,Graphics.LEFT|Graphics.BOTTOM);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;
	      //case demoPlay:
              default :  // if (this.isDoubleBuffered())
			 //     {
		         //       DoubleBuffer(g);
			 //     }
			 //     else {
		                DoubleBuffer(gBuffer);
                                g.setColor(0xffffff);
                                g.fillRect(0, 0, scrwidth,scrheight);
		                g.drawImage(Buffer,baseX,baseY,Graphics.TOP|Graphics.LEFT);
			 //     }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }

    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded() {
       playerScore=0;
       blink=false;
       bkgX=0;
       bkgY=0;
       hitY = 0xffff;
       hitTickCounter = 0;
       direct=Asteroids_PMR.MOVE_NONE;
       //CurrentLevel=level;
       client_sb.nextGameStep();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final byte IMG_WIN = (byte)2;
private static final byte IMG_ASTEROIDS = (byte)0;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_PLANET3 = (byte)20;
private static final byte IMG_ASTER9 = (byte)16;
private static final byte IMG_GLASS = (byte)4;
private static final byte IMG_ASTER8 = (byte)15;
private static final byte IMG_PLANET2 = (byte)19;
private static final byte IMG_STARFIELD = (byte)3;
private static final byte IMG_ASTER7 = (byte)14;
private static final byte IMG_ASTER6 = (byte)13;
private static final byte IMG_ASTER5 = (byte)12;
private static final byte IMG_PLANET1 = (byte)18;
private static final byte IMG_ASTER4 = (byte)11;
private static final byte IMG_DEMO = (byte)5;
private static final byte IMG_ASTER3 = (byte)10;
private static final byte IMG_ASTER2 = (byte)9;
private static final byte IMG_PLANET0 = (byte)17;
private static final byte IMG_ASTER1 = (byte)8;
private static final byte IMG_HEART = (byte)6;
private static final byte IMG_ASTER0 = (byte)7;
private static final byte IMG_PNTR_LEFT = (byte)21;
private static final byte IMG_PNTR_RIGHT = (byte)22;
private static final byte IMG_PNTR_UP = (byte)23;
private static final byte IMG_PNTR_DOWN = (byte)24;
private static final byte TOTAL_IMAGES_NUMBER = (byte)25;
}

