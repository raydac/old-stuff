
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Balloon.*;
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

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  private Thread thread=null;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image Flame=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image []items;

  private int CurrentLevel=3;
  protected Balloon_PMR client_pmr = null;
  protected int direct=Balloon_PMR.BURNER_OFF;
  protected Balloon_SB client_sb = new Balloon_SB(null);
//  protected int Amount=3;
  protected int baseX=0, baseY=0, cellW=0,cellH=0;

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
			    Finished = 5,
			    gameOver = 6;

  private int [] stageSleepTime = {100,100,100,150,200,200,200}; // in ms

  private int playerScore = 0;
  private int carPosition = 0;
  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private int crashDelay=6;
  private boolean bufferChanged=true;
  private boolean justStarted=true;

  private boolean isShown = false;
  private Image MenuIcon = null;
  private int LoadingTotal =0, LoadingNow = 0,ticks = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  public String loadString  = "Loading...";
  public String YourScore  = "Your score:";
  public String NewStage  = null;


  public int fontHeight=0,fontWidth=0, TitleN = 0;
  private Image []numbers=new Image[10];
  int BkgCOLOR, InkCOLOR,LoadingBar,LoadingColor;


    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void startIt() {
     loading(TOTAL_IMAGES_NUMBER+67);

     try {
	   items = (new ImageBlock(this))._image_array;

           Title    = items[IMG_BAL_PARK];
           Lost     = items[IMG_BAL_LOST];
           Won      = items[IMG_BAL_WIN];
           DemoTitle= items[IMG_DEMO];
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
	   Flame    = items[IMG_FLAME];

           setBlinkImage(DemoTitle);

           Image tmp=items[IMG_NFONT];//Image.createImage(FontImage);
           fontHeight = tmp.getHeight();
           fontWidth = tmp.getWidth()/10;
           for (int i = 1;i<10; i++){
	      numbers[i] = Image.createImage(fontWidth,fontHeight);
	      numbers[i].getGraphics().drawImage(tmp,-(i-1)*fontWidth,0,Graphics.TOP|Graphics.LEFT);
           }
	   numbers[0] = Image.createImage(fontWidth,fontHeight);
	   numbers[0].getGraphics().drawImage(tmp,-9*fontWidth,0,Graphics.TOP|Graphics.LEFT);

           items[IMG_NFONT] = null;
	   tmp = null;

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     } catch(Exception e) {
       System.out.println("Can't read images");
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
    client_sb.setPlayerBlock(this);
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    gameStatus=notInitialized;
    if(colorness) {
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

       Balloon_GSR Balloon = (Balloon_GSR)gameStateRecord;
       client_pmr.setState(direct);

       return client_pmr;
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Balloon_PMR(Balloon_PMR.BURNER_OFF);

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
       ticks = 0;
       direct=Balloon_PMR.BURNER_OFF;

        if (CurrentLevel!=level) {
	   CurrentLevel=level;
	   baseX=((scrwidth-items[IMG_LVL1+CurrentLevel].getWidth())>>1);
	   baseY=((scrheight-items[IMG_LVL1+CurrentLevel].getHeight())>>1);
        }
	if (gameStatus==titleStatus) {
	    client_sb.setPlayerBlock(new DemoPlayer());
	    client_sb.newGame(0/*Balloon_SBLEVEL_DEMO*/);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.setPlayerBlock(this);
	   client_sb.newGame(CurrentLevel);
	   gameStatus=playGame;
	}
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
			       int action = getGameAction(keyCode);
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
				 else
			            direct=Balloon_PMR.BURNER_ON;
			       }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Balloon_PMR.BURNER_OFF;
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;
      int blinks=0;
      while(animation) {
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
			   init(demoLevel,null);
			  }
			break;
	   case demoPlay: {
			   blink=(ticks++&16)!=0;
		           client_sb.nextGameStep();
			   Balloon_GSR loc=(Balloon_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Balloon_GSR.GAME_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			   }

			   repaint();
			  } break;
           case playGame :{
			   client_sb.nextGameStep(); ticks=0;
			   Balloon_GSR loc=(Balloon_GSR)client_sb.getGameStateRecord();
//			   if (loc.getGameState()==Balloon_GSR.GAME_OVER) {
//	                       gameStatus=gameOver;
//                               playerScore=loc.getPlayerScores();
//			   }
			   switch (loc.getPlayerState()) {
			     case Balloon_GSR.PLAYER_DEAD: gameStatus=crashCar; break;
			     case Balloon_GSR.PLAYER_FINISHED: gameStatus=Finished; ticks = 0;
							     break;
			   }
			   repaint();
			  } break;
	    case crashCar:
			   if (ticks++>=crashDelay) {
			     gameStatus=gameOver;
			     ticks = 0;
			     blink = false;
			   } else
			       blink^=true;
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
    private void drawDigits(Graphics gBuffer,int x,int y,int vacant,int num){
      String a = new String("000000"+num);
      if (vacant>=a.length()) return;
      int b = a.length()-vacant;
      int _x=x;
      Image d;
      for (int i = 0; i < vacant; i++){
	d = numbers[(byte)a.charAt(b+i)-(byte)'0'];
         gBuffer.drawImage(d,_x, y, Graphics.LEFT|Graphics.TOP);
	 _x+=d.getWidth();
      }
//	  System.out.println(", "+a);
    }

    protected void DoubleBuffer(Graphics gBuffer){
//      if (bufferChanged) {

	// prepare screen

//roadGraphics=gBuffer;

        Balloon_GSR loc=(Balloon_GSR)client_sb.getGameStateRecord();

	gBuffer.drawImage(items[IMG_LVL1+CurrentLevel],baseX,baseY,Graphics.TOP|Graphics.LEFT);

	AirFlow [] o = loc.getFlowArray();
	for (int i=0;i<o.length;i++){
  	    gBuffer.drawImage(items[IMG_CLOUD],baseX+o[i]._markerx,baseY+o[i]._markery,Graphics.TOP|Graphics.LEFT);
	}
	//  Players car

	int Item=(loc.getPlayerState()==Balloon_GSR.PLAYER_BURNINGON)?IMG_FON:IMG_FOFF;
//	System.out.println(loc.getPlayerState());

	int offset=baseY+carPosition;

	offset=baseY+loc.getPlayerY();

	if (gameStatus==crashCar){
	  if (!blink) gBuffer.drawImage(items[Item],baseX+loc.getPlayerX(),offset,Graphics.TOP|Graphics.LEFT);
	} else gBuffer.drawImage(items[Item],baseX+loc.getPlayerX(),offset,Graphics.TOP|Graphics.LEFT);


        int off = baseX+scrwidth - fontWidth*4;
        gBuffer.drawImage(Flame,off-2-Flame.getWidth(),baseY+1,Graphics.TOP|Graphics.LEFT);
        drawDigits(gBuffer,off,baseY+1,4,loc.getGasLevel());


	/// target
//	gBuffer.drawImage(items[iTarget],baseX+client_sb.PLACE_X,baseY+client_sb.PLACE_Y,Graphics.TOP|Graphics.LEFT);

/*
	int fuel=loc.getGasLevel()*levelImage[CurrentLevel].getWidth()/client_sb.MAX_GAS_LEVEL;
        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(baseX,baseY+levelImage[CurrentLevel].getHeight()-6,fuel,6);
        gBuffer.setColor(0x0);
        gBuffer.fillRect(baseX,baseY+levelImage[CurrentLevel].getHeight()-5,fuel,4);
*/
      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(0xffffff);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }
      }

	if (gameStatus==demoPlay) {
	  if (blink && blinkImage!=null)  gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
	// drawin' the left attempts

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
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
			      {
		         //       DoubleBuffer(g);
			 //     }
			 //     else {
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded(){
       playerScore=0;
       blink=false;
       ticks = 0;
       direct=Balloon_PMR.BURNER_OFF;
       client_sb.setPlayerBlock(this);
       CurrentLevel=((Balloon_GSR)client_sb.getGameStateRecord()).getLevel();
       baseX=((scrwidth-items[IMG_LVL1+CurrentLevel].getWidth())>>1);
       baseY=((scrheight-items[IMG_LVL1+CurrentLevel].getHeight())>>1);
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final int IMG_BAL_PARK = 0;
private static final int IMG_BAL_LOST = 1;
private static final int IMG_BAL_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_LVL1 = 4;
private static final int IMG_LVL2 = 5;
private static final int IMG_LVL3 = 6;
private static final int IMG_FOFF = 7;
private static final int IMG_FON = 8;
private static final int IMG_FLAME = 9;
private static final int IMG_CLOUD = 10;
private static final int IMG_NFONT = 11;
private static final int TOTAL_IMAGES_NUMBER = 12;

}




/*
.
BAL_PARK,0
BAL_LOST,1
BAL_WIN,2
DEMO,3
LVL1,4
LVL2,5
LVL3,6
FOFF,7
FON,8
FLAME,9
CLOUD,10
NFONT,11
.
.
*/
