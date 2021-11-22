import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Frog.*;
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
  public Thread thread=null;
  private boolean isShown = false;

  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image imgBackGround = null;//new Image[Frog_SB.SUBLEVELS_FLOWMAP.length];
//  private Image [] imgCar = new Image[Car.MAX_TYPE*2+2];     // max types * 2 directions (forw./back)
//  private Image [] imgFrog = new Image[4+2*Frog_GSR.FROG_MOVE_FRAMES+2*Frog_GSR.FROG_JUMP_FRAMES];
  private Image []items;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image Heart = null;
  private Image RevercedGrass;
  private Image blinkImage=null;
  private int bsWIDTH, bsHEIGHT, bsSTEP, bsFRAMES, bsLAST_FRAME=0, scrX=0;
/*
  private static final int iLOOK_LEFT = 0,
                           iLOOK_RIGHT = 1,
                           iLOOK_UP = 2,
                           iLOOK_DOWN = 3,
                           iMOVE_LEFT = 4,
                           iMOVE_RIGHT = iMOVE_LEFT+Frog_GSR.FROG_MOVE_FRAMES,
                           iJUMP_UP = iMOVE_RIGHT+Frog_GSR.FROG_MOVE_FRAMES,
			   iJUMP_DOWN = iJUMP_UP+Frog_GSR.FROG_JUMP_FRAMES;
*/
  private int blinkX=0,blinkY=0;//in ticks defined by singleSleepTime

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected Frog_PMR client_pmr = null;
  protected int direct=Frog_PMR.BUTTON_NONE;
  protected Frog_SB client_sb = new Frog_SB();
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false,keyReverse = false;
  private int stage = 0;
  private int lastPlayerScores =0;


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
  private int [] stageSleepTime = {100,100,100,200,100,200,200,200}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x005e20;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

  private Image [] Elements;
  private byte [][] ByteArray;

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

//    client_sb = new GameletImpl(VisWidth, VisHeight, null);
//    client_pmr = new PlayerMoveObject();
    client_sb.setPlayerBlock(this);

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

///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

       client_pmr.setButton(direct);

       return client_pmr;
    }

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
           Heart=Elements[IMG_HEART];
           imgBackGround=Elements[IMG_ROAD]; //Image.createImage(DemoTitleImage];



	   Image img = GetImage(IMG_GRASS);
	   RevercedGrass = Image.createImage(img.getWidth(),img.getHeight());
	   Graphics g = RevercedGrass.getGraphics();
	   g.setColor(BACKGROUND_COLOR);
	   g.fillRect(0,0,img.getWidth(),img.getHeight());
	   g.drawImage(img,0,0,0);
	   com.siemens.mp.ui.Image.mirrorImageHorizontally(RevercedGrass);

//	   font = new drawDigits(GetImage(IMG_NFONT));
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



    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Frog_PMR();

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
       ticks = 0;
       direct=Frog_PMR.BUTTON_NONE;
       bsSTEP = 0;
       bsLAST_FRAME = 0;
       scrX=0;
       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Frog_SB.LEVEL0);
	    client_sb.nextStage(0);
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
//	    DrawBackScreen();
	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
//           client_sb.nextGameStep();
           stageSleepTime[playGame] = ((Frog_GSR)client_sb.getGameStateRecord()).getTimeDelay();
	   lastPlayerScores = 0;
	   stage = 0;
	   gameStatus=newStage;
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
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
                                 else
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM4/*LEFT*/: direct=Frog_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Frog_PMR.BUTTON_RIGHT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=Frog_PMR.BUTTON_UP; break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=Frog_PMR.BUTTON_DOWN; break;
					default:
					  switch (getGameAction(keyCode)) {
						case Canvas.LEFT/*LEFT*/: direct=Frog_PMR.BUTTON_LEFT; break;
				                case Canvas.RIGHT/*RIGHT*/: direct=Frog_PMR.BUTTON_RIGHT; break;
				                case Canvas.UP/*UP*/: direct=Frog_PMR.BUTTON_UP; break;
				                case Canvas.DOWN/*DOWN*/: direct=Frog_PMR.BUTTON_DOWN; break;
						   default:direct=Frog_PMR.BUTTON_NONE;
					  }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Frog_PMR.BUTTON_NONE;
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
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			   //repaintBackScreen();
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   Frog_GSR loc=(Frog_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Frog_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
				client_sb.nextStage(stage);
		                direct=Frog_PMR.BUTTON_NONE;
				client_sb.nextGameStep();

				//repaintBackScreen();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   client_sb.nextGameStep();
			   Frog_GSR loc=(Frog_GSR)client_sb.getGameStateRecord();
			   /*
			   if (loc.getGameState()==Frog_GSR.GAMESTATE_OVER) {
                               playerScore=loc.getPlayerScores();
			       switch (loc.getPlayerState()) {
				      case Frog_GSR.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
				      // case Frog_GSR.PLAYERSTATE_OUTOFAMMO: ticks = 0;gameStatus=Crashed; break;
			              case Frog_GSR.PLAYERSTATE_WON: ticks = 0;
//				              if (++stage<Frog_SB.SUBLEVELS_FLOWMAP.length)
//						   gameStatus=newStage;
//						 else
						    gameStatus=Finished;
//					      break;
			       }
			   } else   */
			         switch (loc.getPlayerState()) {
					case Frog_GSR.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Frog_GSR.PLAYERSTATE_WON: ticks = 0;
				              if (++stage<Frog_SB.SUBLEVELS_FLOWMAP.length)
						    gameStatus=newStage;
						 else
						      gameStatus=Finished;
					break;
			         }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=Frog_PMR.BUTTON_NONE;
			     if (((Frog_GSR)client_sb.getGameStateRecord()).getGameState()==Frog_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGame();
			           gameStatus=playGame;
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

//        gBuffer.setColor(0xffffff);
//        gBuffer.fillRect(0, VisHeight-10, VisWidth,10);

//      if (bufferChanged) {

        Frog_GSR loc=(Frog_GSR)client_sb.getGameStateRecord();
        Image img = null;

	int playzone_layout_offset = (VisHeight - imgBackGround.getHeight())>>1;
	int gap = VisHeight - playzone_layout_offset - imgBackGround.getHeight();
	gBuffer.setColor(BACKGROUND_COLOR);
	if(playzone_layout_offset>0) gBuffer.fillRect(0, 0, VisWidth, playzone_layout_offset);
	if(gap>0) gBuffer.fillRect(0, VisHeight-gap, VisWidth, gap);

	if(RevercedGrass!=null)
	 gBuffer.drawImage(RevercedGrass,0,playzone_layout_offset-RevercedGrass.getHeight(),0);

	gBuffer.drawImage(imgBackGround,0,playzone_layout_offset,0);


	playzone_layout_offset = (VisHeight - (Car.CAR_HEIGHT<<2) - (Frog_SB.PLAYER_HEIGHT<<1))>>1;


gBuffer.translate(0,playzone_layout_offset);


        // Drawing of the frog
if(!(gameStatus == Crashed && blink))
  try {
	switch (loc.getPlayerViewState()) {
	   case Frog_GSR.PLAYER_VIEW_LEFT    : img = Elements[IMG_F_L0+loc.getPlayerFrame()]; break;
	   case Frog_GSR.PLAYER_VIEW_RIGHT   : img = Elements[IMG_F_R0 +loc.getPlayerFrame()]; break;
	   case Frog_GSR.PLAYER_VIEW_UP      : img = Elements[IMG_F_U0]; break;
	   case Frog_GSR.PLAYER_VIEW_DOWN    : img = Elements[IMG_F_D0]; break;
	   case Frog_GSR.PLAYER_VIEW_JUMPDOWN: img = Elements[IMG_F_D0+loc.getPlayerFrame()]; break;
	   case Frog_GSR.PLAYER_VIEW_JUMPUP  : img = Elements[IMG_F_U0+loc.getPlayerFrame()]; break;

	}
          gBuffer.drawImage(img,loc.getPlayerX(),loc.getPlayerY(),0);
	} catch (Exception e){
//	   System.out.println("State:"+loc.getPlayerViewState()+", Frame:"+loc.getPlayerFrame()+
//	   ", Offset:"+off+", imgFrog.length="+imgFrog.length);
	}

        Car[] _viewarr = loc.getViewCarArray();
	Car _c = null;
        int _num = loc.getViewCarNumber();

        for (int li = 0; li < _num; li++)
        {
            _c = _viewarr[li];
	    if (_c.getState()==Car.STATE_ACTIVE)
                 gBuffer.drawImage(Elements[IMG_LCAR0+(_c.getType()<<1)+_c.getDirection()],_c.getX() ,_c.getY(),0);
        }

gBuffer.translate(0,-playzone_layout_offset);

	img = GetImage(IMG_GRASS);
	gBuffer.drawImage(img, 0 ,VisHeight-img.getHeight(),0);

        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<loc.getAttemptionNumber()-1;i++){
           gBuffer.drawImage(Heart,VisWidth-(i+1)*(Heart.getWidth()+2)-10,VisHeight-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, VisWidth>>1 ,VisHeight-VisHeight/4/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
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
	                  g.drawImage(GetImage(IMG_LF),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
      return (gameStatus == playGame || gameStatus == Crashed || gameStatus == newStage);
    }
    public void gameLoaded(){
       Frog_GSR loc = (Frog_GSR)client_sb.getGameStateRecord();
       blink=false;
       ticks = 0;
       direct=Frog_PMR.BUTTON_NONE;
       bsSTEP = 0;
       bsLAST_FRAME = 0;
       scrX=0;
       stageSleepTime[playGame] = loc.getTimeDelay();
	   lastPlayerScores = 0;
	   stage = loc.getStage();
	   gameStatus=newStage;
	   ticks = 1;
	bufferChanged=true;
	repaint();
    }

private static final int IMG_LF = 0;
private static final int IMG_WON = 1;
private static final int IMG_LOST = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_ROAD = 4;
private static final int IMG_LCAR0 = 5;
private static final int IMG_RCAR0 = 6;
private static final int IMG_LCAR1 = 7;
private static final int IMG_RCAR1 = 8;
private static final int IMG_LCAR2 = 9;
private static final int IMG_RCAR2 = 10;
private static final int IMG_LCAR3 = 11;
private static final int IMG_RCAR3 = 12;
private static final int IMG_LCAR4 = 13;
private static final int IMG_RCAR4 = 14;
private static final int IMG_RCAR5 = 15;
private static final int IMG_LCAR5 = 16;
private static final int IMG_F_L0 = 17;
private static final int IMG_F_L1 = 18;
private static final int IMG_F_L2 = 19;
private static final int IMG_F_R0 = 20;
private static final int IMG_F_R1 = 21;
private static final int IMG_F_R2 = 22;
private static final int IMG_F_U0 = 23;
private static final int IMG_F_U1 = 24;
private static final int IMG_F_D0 = 28;
private static final int IMG_F_D1 = 29;
private static final int IMG_HEART = 33;
private static final int IMG_GRASS = 34;
private static final int IMG_F_U2 = 25;
private static final int IMG_F_U3 = 26;
private static final int IMG_F_U4 = 27;
private static final int IMG_F_D2 = 30;
private static final int IMG_F_D3 = 31;
private static final int IMG_F_D4 = 32;
private static final int TOTAL_IMAGES_NUMBER = 35;

}

/*
.
LF,0
LOST,1
WON,2
ROAD,3

LCAR0,4
RCAR0,5
LCAR1,6
RCAR1,7
LCAR2,8
RCAR2,9
LCAR3,10
RCAR3,11
LCAR4,12
RCAR4,13
LCAR5,14
RCAR5,15

F_L0,16
F_L1,17
F_L2,18

F_R0,19
F_R1,20
F_R2,21

F_U0,22
F_U1,23
DEMO,24

F_D0,25
F_D1,26
HEART,27

.
.
*/
