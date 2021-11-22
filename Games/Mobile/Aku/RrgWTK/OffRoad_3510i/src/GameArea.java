
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Offroad.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.GameAPI.Utils.*;
import com.igormaznitsa.midp.ImageBlock;


//import com.igormaznitsa.GameAPI.Utils.Sine;
public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int BROKEN_GLASS_DELAY = 25;
  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  private Thread thread=null;

  private Image Title=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image Heart = null;
  private Image Glass = null;
  private Image Lost = null;
  private Image Won = null;
  private Image Sky = null;
  private Image Ground = null;

  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
                            playGame = 3,
			    Finished = 4,
			    gameOver = 5;
  private int [] stageSleepTime = {100,100,200,150,250,250}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;

//  public final static String []PointerImages = new String[]{"/res/pntr_left.png","/res/pntr_right.png"};
  public final static int Obstacles_quantity = 15;
  public final static int Targets_quantity = 4;

  private Image [] items;
/*
  private Image []obst = new Image[Obstacles_quantity];
  private Image []target = new Image[Targets_quantity];
  private Image []pointer =new Image[2];
*/
//  private int CurrentLevel=3;
  protected int direct=Offroad_SB.MOVE_NONE;
  protected Offroad_SB client_sb = new Offroad_SB(null);
  private int baseX=0, baseY=0, cellW=0,cellH=0, /*temp_counter=0, */ticks = 0 , skyPosition =0, groundPosition = 0;

  private int hitX = 0;
  private int hitY = 0;
  private int hitTickCounter = 0;
  private int prevLives = 0;

  /// точечки

  private boolean isShown = false;
  private Image MenuIcon = null;
  public String loadString  = "Loading...";
  public String YourScore  = "Your Score:";

  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;


    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void start(int kkk) {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     loading(TOTAL_IMAGES_NUMBER+67);

     try {
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
	   items = (new ImageBlock(this))._image_array;
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];

           Title = items[IMG_OFFROAD];
	   Sky = items[IMG_CLOUDS];
           Lost = items[IMG_OFF_LOST];
           Won = items[IMG_OFF_WIN];
/*
           obst[0]=items[IMG_TREE0];
           obst[1]=items[IMG_TREE1];
           obst[2]=items[IMG_TREE2];
           obst[3]=items[IMG_TREE3];
           obst[4]=items[IMG_TREE4];
           obst[5]=items[IMG_TREE5];
           obst[6]=items[IMG_TREE6];
           obst[7]=items[IMG_TREE7];
           obst[8]=items[IMG_TREE8];
           obst[9]=items[IMG_TREE9];
           obst[10]=items[IMG_TREE10];
           obst[11]=items[IMG_TREE11];
           obst[12]=items[IMG_TREE12];
           obst[13]=items[IMG_TREE13];
           obst[14]=items[IMG_TREE14];

           target[0]=items[IMG_TOWER0];
           target[1]=items[IMG_TOWER1];
           target[2]=items[IMG_TOWER2];
           target[3]=items[IMG_TOWER3];

           pointer[0]=items[IMG_PNTR_LEFT];
           pointer[1]=items[IMG_PNTR_RIGHT];
*/

           DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage];
           Heart=items[IMG_HEART]; //Image.createImage(DemoTitleImage];
           Glass=items[IMG_GLASS]; //Image.createImage(DemoTitleImage];
           Ground=items[IMG_GROUND]; //Image.createImage(DemoTitleImage];
           setBlinkImage(DemoTitle);

     } catch(Exception e) {
//       System.out.println("Can't read images");
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
       isShown = false;
       direct = Offroad_SB.MOVE_NONE;
    }

  /**
   *  константы обозначающие различные состояния игры,
   *  флаг состояния
   */

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    baseX = (scrwidth - 100)>>1;
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
    private void setDirection(){

//       Offroad_GSR Rally = (Offroad_GSR)gameStateRecord;
       client_sb.i_Button = direct;

       switch(direct){
         case Offroad_SB.MOVE_LEFT:
	                              groundPosition-=(client_sb.PLAYER_HORIZ_STEP);
	                              skyPosition-=(client_sb.PLAYER_HORIZ_STEP>>1);
				      break;
         case Offroad_SB.MOVE_RIGHT:
				      groundPosition+=(client_sb.PLAYER_HORIZ_STEP);
				      skyPosition+=(client_sb.PLAYER_HORIZ_STEP>>1);
       }
       if (groundPosition>0)groundPosition-=Ground.getWidth();
       else
         if (groundPosition<=-Ground.getWidth())groundPosition+=Ground.getWidth();

       if (skyPosition>0)skyPosition-=Sky.getWidth();
       else
         if (skyPosition<-Sky.getWidth())skyPosition+=Sky.getWidth();
    }


    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       skyPosition =0;
       groundPosition = 0;
       playerScore=0;
       blink=false;
       hitY = 0xffff;

       direct=Offroad_SB.MOVE_NONE;

//        if (CurrentLevel!=level) CurrentLevel=level;
	if (gameStatus==titleStatus) {
//	    client_sb.setPlayerBlock(new DemoPlayer());
	    client_sb.newGame(Offroad_SB.LEVEL0);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGame(level);
//	   client_sb.setPlayerBlock(this);
	   gameStatus=playGame;
	}
        prevLives = ((Offroad_GSR)client_sb.getGameStateRecord()).getPlayerAttemption();
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
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
			       else
	                       switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM4/*LEFT*/: direct=(direct|Offroad_SB.MOVE_RIGHT)&(~Offroad_SB.MOVE_LEFT); break;
	                              case Canvas.KEY_NUM6/*RIGHT*/: direct=(direct|Offroad_SB.MOVE_LEFT)&(~Offroad_SB.MOVE_RIGHT); break;
//				      case UP: direct=(direct|Offroad_SB.MOVE_UP)&(~Offroad_SB.MOVE_DOWN); break;
//	                              case DOWN:direct=(direct|Offroad_SB.MOVE_DOWN)&(~Offroad_SB.MOVE_UP); break;

				default:direct=Offroad_SB.MOVE_NONE;
	                       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
              switch (/*getGameAction(*/keyCode/*)*/) {
  	        case Canvas.KEY_NUM4/*LEFT*/: direct&=(~Offroad_SB.MOVE_RIGHT);break;
	        case Canvas.KEY_NUM6/*RIGHT*/: direct&=(~Offroad_SB.MOVE_LEFT); break;
//		case UP: direct&=(~Offroad_SB.MOVE_UP); break;
//	        case DOWN:direct&=(~Offroad_SB.MOVE_DOWN); break;
              }
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      int blinks=0;
      while(animation) {
  //       blinks++;
//  temp_counter+=client_sb.OBSTACLE_SPEED;
//  temp_counter&=0x255;
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}

        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
			if (ticks++<2){System.gc();}
			else
			 if (ticks++>80)
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   blink = (ticks & 4) ==0;
			   Offroad_GSR loc=(Offroad_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Offroad_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               playerScore=loc.getPlayerScores();
			       ticks=0;
			   }
			   repaint();
			  } break;
           case playGame :{
		           if (hitTickCounter>0)hitTickCounter--;
			   Offroad_GSR loc=(Offroad_GSR)client_sb.getGameStateRecord();

			   if (loc.getPlayerState()!=Offroad_GSR.PLAYERSTATE_KILLED){
			      setDirection();
			      client_sb.nextGameStep();
			   }
			   //if (loc.getGameState()==Offroad_GSR.GAMESTATE_OVER) {
	                   //    gameStatus=gameOver;
                           //    playerScore=loc.getPlayerScores();
			   //}
			   ticks = 0;
			   if (prevLives> loc.getPlayerAttemption()){
			       prevLives = loc.getPlayerAttemption();
			       hitX = client_sb.getLastTreeX();
			       hitY = client_sb.getLastTreeY();
			       hitTickCounter = BROKEN_GLASS_DELAY;
			   }
			   switch (loc.getPlayerState()) {
			     case Offroad_GSR.PLAYERSTATE_KILLED: if (--hitTickCounter<=0)gameStatus=gameOver; break;
//			     case Offroad_GSR.PLAYER_OUTFIELD: gameStatus=crashCar; break;
			     case Offroad_GSR.PLAYERSTATE_FINISHED: gameStatus=Finished; break;
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
//        gBuffer.fillRect(0, 0, scrwidth,scrheight);


        Offroad_GSR loc=(Offroad_GSR)client_sb.getGameStateRecord();

	int n=Targets_quantity-(loc.getCityDistance()*Targets_quantity/client_sb.CITY_INIT_Z)-1;
	if (n<0) n=0;
	int ddx=0/*baseX*/+loc.getCityRX()+(client_sb.CITY_WIDTH>>1);
	int ddy=0/*baseY*/+loc.getCityRY()+(client_sb.CITY_HEIGHT);

        gBuffer.drawImage(Ground,0/*baseX*/+groundPosition,ddy,0);
        gBuffer.drawImage(Ground,0/*baseX*/+groundPosition+Ground.getWidth()-1,ddy,0);
        gBuffer.drawImage(Sky,0/*baseX*/+skyPosition,0/*baseY*/,0);
        gBuffer.drawImage(Sky,0/*baseX*/+skyPosition+Sky.getWidth(),0/*baseY*/,0);

// drawing the horizont
//        gBuffer.setColor(0x0);
//        gBuffer.drawLine(0,ddy,scrwidth,ddy);
/*
	int cnt=temp_counter;
	for (int i=ddy;i<scrheight;i+=(cnt>>4)){
	  cnt+=31;
	  gBuffer.drawLine(0,i,scrwidth,i);
	}
*/

        try {
             gBuffer.drawImage(items[IMG_TOWER0+n],ddx,ddy,Graphics.BOTTOM|Graphics.HCENTER);
        } catch(Exception e)
	      {}

	Obstacle [] o = loc.getVisibleArray();

	int dx=0/*baseX*/+client_sb.PLAYER_CENTER_HORIZ+(client_sb.OBSTACLE_WIDTH>>1);
	int dy=0/*baseY*/+client_sb.PLAYER_CENTER_VERT+(client_sb.OBSTACLE_HEIGHT);

	for (int i=0;i<loc.getVisibleCounter();i++){
	   n=Obstacles_quantity-((o[i]._z-1)*Obstacles_quantity/client_sb.OBSTACLE_INIT_Z)-1;
           try {
             gBuffer.drawImage(items[IMG_TREE0+n],
	                     o[i]._rx+dx,
			     o[i]._ry+dy,
			     Graphics.HCENTER|Graphics.BOTTOM);
           } catch(Exception e)
	      {}
	}

	   if (ddx<0)
	       gBuffer.drawImage(items[IMG_PNTR_LEFT],0/*baseX*/,0/*baseY*/+ddy-items[IMG_PNTR_LEFT].getHeight()-1,Graphics.TOP|Graphics.LEFT);
	   if (ddx>101/*scrwidth*/)
	       gBuffer.drawImage(items[IMG_PNTR_RIGHT],0/*baseX*/+scrwidth/*scrwidth*/,0/*baseY*/+ddy-items[IMG_PNTR_RIGHT].getHeight()-1,Graphics.TOP|Graphics.RIGHT);

	if (hitTickCounter>0)
	   gBuffer.drawImage(Glass,dx+hitX, 64/*scrheight*/>>1/*dy+hitY*/,Graphics.HCENTER|Graphics.VCENTER);

	 for (int i=0;i<loc.getPlayerAttemption();i++){
           gBuffer.drawImage(Heart,0/*baseX*/+101/*scrwidth*/-(i+1)*(Heart.getWidth()+2)-10,0/*baseY*/+64/*scrheight*/-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }

	if (gameStatus == demoPlay && blink && blinkImage != null) {
	   gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
/*
        gBuffer.setColor(0x0);
	gBuffer.drawString("Dist:"+loc.getCityDistance(),scrwidth,5,Graphics.RIGHT|Graphics.TOP);
*/
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
	                  g.drawImage(Title,0,0,Graphics.TOP|Graphics.LEFT);
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
              default :   //if (this.isDoubleBuffered())
			  //    {
		          //      DoubleBuffer(g);
			  //    }
			  //    else {
		                DoubleBuffer(gBuffer);
                                //g.setColor(0xffffff);
                                //g.fillRect(0, 0, scrwidth,scrheight);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT); //break;
			  //    }
	                  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded(){
       skyPosition =0;
       groundPosition = 0;
       playerScore=0;
       blink=false;
       direct=Offroad_SB.MOVE_NONE;
       bufferChanged=true;
       prevLives = ((Offroad_GSR)client_sb.getGameStateRecord()).getPlayerAttemption();
       hitY = 0xffff;
       gameStatus=playGame;
       System.gc();
       repaint();
    }

private static final int IMG_OFFROAD = 0;
private static final int IMG_OFF_WIN = 2;
private static final int IMG_OFF_LOST = 1;
private static final int IMG_TREE14 = 22;
private static final int IMG_TREE13 = 21;
private static final int IMG_CLOUDS = 3;
private static final int IMG_TREE12 = 20;
private static final int IMG_GROUND = 5;
private static final int IMG_TREE11 = 19;
private static final int IMG_TOWER3 = 26;
private static final int IMG_GLASS = 4;
private static final int IMG_TREE10 = 18;
private static final int IMG_TREE9 = 17;
private static final int IMG_TREE8 = 16;
private static final int IMG_TREE7 = 15;
private static final int IMG_TREE6 = 14;
private static final int IMG_TOWER2 = 25;
private static final int IMG_TREE5 = 13;
private static final int IMG_DEMO = 6;
private static final int IMG_TREE4 = 12;
private static final int IMG_TREE3 = 11;
private static final int IMG_TREE2 = 10;
private static final int IMG_TOWER1 = 24;
private static final int IMG_TREE1 = 9;
private static final int IMG_HEART = 7;
private static final int IMG_TREE0 = 8;
private static final int IMG_TOWER0 = 23;
private static final int IMG_PNTR_LEFT = 27;
private static final int IMG_PNTR_RIGHT = 28;
private static final int TOTAL_IMAGES_NUMBER = 29;

}

