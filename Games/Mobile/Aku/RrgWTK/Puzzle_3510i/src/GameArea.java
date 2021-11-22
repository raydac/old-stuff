
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Mosaic.*;
import com.igormaznitsa.GameAPI.*;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

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
  private Thread thread=null;

  private Image Title=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image []iLevels=new Image[3];
  private Image levelImage = null;
  private int CurrentLevel=-1;
  protected Mosaic_PMR mosaic_pmr = null;
  protected int direct=Mosaic_PMR.DIRECT_NONE;
  protected Mosaic_SB client_sb = new Mosaic_SB(null);
  protected Image [] pieces = null;
  protected int Amount=3;
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
			    showImage = 3,
                            playGame = 4,
			    pieceMoving = 5,
			    gameOver = 6;
  private int [] stageSleepTime = {1,50,5,3,3,1,80}; // in ticks, defined by singleSleepTime
  private int singleSleepTime=60, ticks=0;
//  private int ticksPerStage = {0,80,3,0,2,1,0};
//  private int sleepTime=60

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private byte [] demoScene=null;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 50; // ticks count
  private int demoLimit = 0;
  private int AmountSteps=5;
  private int demoAmountSteps=5;
  private int cMoveStep = 0;
  private int fromX=0,fromY=0,toX=0,toY=0, targetElement=0;
  private int stepX=0, stepY=0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  public int prevGameStatus = titleStatus;

  private boolean isShown = false;
  private Image MenuIcon = null;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  public String loadString  = "Loading...";
  public String YourScore  = "Your score:";

     private int IMG_DEMO = 209; //demo.png
     private int IMG_MENUICO = 1769611; //menuico.png

 private long [] storage = {
5927942488114331648l, 864691129361104896l, 8349393333679816705l, 5566843881284174011l, 5907452288523305025l, 5929097898523361427l, -9223371875894165427l, -9223370937460588413l, -1585266860713049880l, 1657325312385482848l, 913111095151l, 7105686615351380324l, -6313483176152727452l, -4501345627476455425l, 62044951256954265l, -1483657695497501695l, 3731375648703265050l, -8357620141352025086l, 5819992127052581901l, 4078044681319997952l, -8789260627274039232l, 1153422697292693759l, 220680794794053696l, 1998257930281156628l, 742576142955l, 6936297984498157824l, -126l,
5927942488114331648l, 360287970307080192l, 1050556976355869704l, 6433515251655770309l, 792633611911168115l, 277621817606418l, 8017379684120791296l, -8574853190445077139l, 5273715163651280170l, -9112403153787797180l, -1603179204034166775l, 4396817202161377437l, 330255705690873998l, -7497651356403323238l, 2019729493505000636l, 6434547960588605l, -5889496353209319424l, -8232894l,
};

	 public Image getImage(int id)
	 {
	   try {
 	    byte[] img = new byte[(id&0xffff)+8];
	    id>>>=16;
	    int pos = 0;
	    long n=0;
	    while (pos < img.length) {
	      if ((pos&7)==0)
	      if (pos == 0) n = 727905341920923785l;
 	         else                 n=storage[id+(pos>>3)-1];
	      img[pos++]=(byte)n;
	      n>>>=8;
	    }
               Image ret = Image.createImage(img,0,img.length);
	       img = null;
	       System.gc();
	       return ret;
	   } catch (Exception e) {return null;}
	 }


    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void start(int kkk) {
     start();
     loading(kkk);

     try {
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
	   MenuIcon = getImage(IMG_MENUICO);
           DemoTitle = getImage(IMG_DEMO);
           setBlinkImage(DemoTitle);
	     nextItemLoaded(2);

           Title = Image.createImage("/res/title.png");
	     nextItemLoaded(1);
           iLevels[0] = Image.createImage("/res/level1.png");
	     nextItemLoaded(1);
           iLevels[1] = Image.createImage("/res/level2.png");
	     nextItemLoaded(1);
           iLevels[2] = Image.createImage("/res/level3.png");
	     nextItemLoaded(1);

     } catch(Exception e) {}

     storage = null;
     System.gc();

    }

    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
       repaint();
    }


    public void endGame() {
	gameStatus=titleStatus;
	ticks = 0;
	repaint();
    }


    public void showNotify(){
       isShown = true;
    }

    public void hideNotify(){
       isShown = false;
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

       mosaic_pmr.setDirect(direct);
       return mosaic_pmr;
    }

    public void start() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        mosaic_pmr = new Mosaic_PMR(Mosaic_PMR.DIRECT_NONE);

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
       if (CurrentLevel!=level) {
	  CurrentLevel=level;
	  Amount=CurrentLevel+3;
	  levelImage = iLevels[CurrentLevel];
	  cellW=(int)levelImage.getWidth()/Amount;
	  cellH=(int)levelImage.getHeight()/Amount;
	  baseX=((scrwidth-levelImage.getWidth())>>1);
	  baseY=((scrheight-levelImage.getHeight())>>1);

	  pieces = new Image[Amount*Amount];
	  for (int i=0;i<Amount;i++)
	    for (int j=0;j<Amount;j++) {
	       pieces[i*Amount+j]=Image.createImage(cellW,cellH);
	       Graphics g1 = pieces[i*Amount+j].getGraphics();
	       g1.drawImage(levelImage,-j*cellW,-i*cellH,Graphics.TOP|Graphics.LEFT);
	       g1.setColor(0x00000);
	       //g1.setStrokeStyle(Graphics.DOTTED);
	       g1.drawLine(0,0,cellW-1,0);
	       g1.drawLine(0,0,0,cellH-1);
	       //g1.setStrokeStyle(Graphics.SOLID);
	    }
	 }
	if (gameStatus==titleStatus) {
	    demoScene=client_sb.newDemoGame(demoLevel++);
	    if(demoLevel>2)demoLevel=0;
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    if(demoScene.length<demoPreviewDelay)demoLimit=demoScene.length;
	    gameStatus=demoPlay;
	} else {
  	   client_sb.newGame(CurrentLevel);
	   gameStatus=showImage;
	}
	bufferChanged=true;
	System.gc();
	repaint();
    }

    public void loading(){
       gameStatus=notInitialized;
       ticks = 0;
       repaint();
    }

    /**
     * Handle a repeated arrow keys as though it were another press.
     * @param keyCode the key pressed.
     */
    protected void keyRepeated(int keyCode) {

        int action = getGameAction(keyCode);
        switch (action) {
        case Canvas.LEFT:
        case Canvas.RIGHT:
        case Canvas.UP:
        case Canvas.DOWN:
            keyPressed(keyCode);
	    break;
        default:
            break;
        }
    }

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
		                    }//default:
				              if (gameStatus == demoPlay || ticks>=TOTAL_OBSERVING_TIME){
	                                        gameStatus=titleStatus;
			                        ticks=0;
			                        blink=false;
					        repaint();
				              }
		                    //}
				    //midlet.commandAction(midlet.newGameCommand,this);
                               } break;

	      case showImage:
			         if (keyCode == MENU_KEY) {midlet.ShowGameMenu(); break;}
			         if (keyCode == END_KEY) {midlet.ShowQuitMenu(true);break;}

	                         gameStatus=playGame;
			         repaint();
	                      	 break;
              case playGame :
			       if (keyCode == MENU_KEY) {midlet.ShowGameMenu(); break;}
			       if (keyCode == END_KEY) {midlet.ShowQuitMenu(true); break;}

	                       switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Mosaic_PMR.DIRECT_LEFT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/:  direct=Mosaic_PMR.DIRECT_RIGHT; break;
	                              case Canvas.KEY_NUM8/*DOWN*/:  direct=Mosaic_PMR.DIRECT_TOP; break;
	                              case Canvas.KEY_NUM2/*UP*/:    direct=Mosaic_PMR.DIRECT_DOWN; break;
				      case Canvas.KEY_NUM5/*FIRE*/:  gameStatus=showImage;
				                                     repaint();
				                                     return;
					default:
					   switch(getGameAction(keyCode)){
					      case Canvas.RIGHT: direct=Mosaic_PMR.DIRECT_LEFT; break;
	                                      case Canvas.LEFT:  direct=Mosaic_PMR.DIRECT_RIGHT; break;
	                                      case Canvas.DOWN:  direct=Mosaic_PMR.DIRECT_TOP; break;
	                                      case Canvas.UP:    direct=Mosaic_PMR.DIRECT_DOWN; break;
				              case Canvas.FIRE:  gameStatus=showImage;
				                                 repaint();
				                                 return;
					   }
	                       }
			       NextStep();
			       break;

	      case pieceMoving:
			      if (prevGameStatus==demoPlay){
				gameStatus=titleStatus;
				ticks=0;
				blink=false;
			        if (keyCode == MENU_KEY) {midlet.ShowMainMenu(); break;}
			        if (keyCode == END_KEY) {midlet.ShowQuitMenu(false); break;}
				repaint();
			      }
			      if (keyCode == MENU_KEY) {midlet.ShowGameMenu(); break;}
			      if (keyCode == END_KEY) {midlet.ShowQuitMenu(true); break;}
            }
    }

    protected void keyReleased(int keyCode) {
          if (getGameAction(keyCode)==this.FIRE && gameStatus==showImage) {
	     gameStatus=playGame;
	     repaint();
          }
    }


    public void NextStep() {
        client_sb.nextGameStep();
	Mosaic_GSR loc=(Mosaic_GSR)client_sb.getGameStateRecord();
	direct=Mosaic_PMR.DIRECT_NONE;
	toX=loc.getOldEmptyX(); fromX=loc.getEmptyCellX();
	toY=loc.getOldEmptyY(); fromY=loc.getEmptyCellY();
	targetElement=loc.getElementAt(toX,toY);
	if (fromX!=toX || fromY!=toY) {
	    stepX=(toX-fromX)*cellW/AmountSteps;
	    stepY=(toY-fromY)*cellH/AmountSteps;
 	    bufferChanged=true;
	    prevGameStatus=gameStatus;
            gameStatus=pieceMoving;
	    cMoveStep=0;
	    repaint();
	}
    }

    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      int blinks=0;
      boolean borderReached=false;
      while(animation) {
//	synchronized (this){
         if (ticks++>stageSleepTime[gameStatus]) {
	    borderReached=true;
	    ticks=0;
         } blinks++;
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
			if (borderReached) init(demoLevel,null);
			break;
	   case demoPlay:
			if (borderReached)
			  if (demoPosition<demoLimit){
			    direct=(int)demoScene[demoPosition++];
			    NextStep();
			  } else {
			     gameStatus=titleStatus;
			     blink=false;
			     repaint();
			  }

			break;
	   case showImage : /*if (borderReached) repaint();*/break;
           case playGame : /*if (borderReached) repaint();*/ break;
	   case pieceMoving :
                             if (prevGameStatus==demoPlay) blink=true;
		             if (cMoveStep>=AmountSteps) {
			       blink=false;
			       //System.gc();
			       gameStatus=prevGameStatus;
	                       Mosaic_GSR loc=(Mosaic_GSR)client_sb.getGameStateRecord();
	                       if (gameStatus==playGame)
	                         if (loc.getGameState()==Mosaic_GSR.GAME_OVER) {
	                           gameStatus=gameOver;
	                           playerScore=loc.getPlayerScores();
	                         }
		             }
			      else cMoveStep++;
			     bufferChanged=true;
		             repaint();
			     break;
	    case gameOver:
	                   if(borderReached){
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			      midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
			   repaint();
			   break;

 	 }



	  borderReached=false;

        sleepy = System.currentTimeMillis();
	workDelay += singleSleepTime-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

          if(gameStatus==pieceMoving /*&& Thread.activeCount()>1*/)
	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
          else
          if(workDelay>0)
             Thread.sleep(workDelay);

	} catch (Exception e) {}

	workDelay = System.currentTimeMillis();
/*
	  try {
	     thread.sleep(singleSleepTime);
	  } catch (Exception e) {}
*/
      }
    }

    protected void DoubleBuffer(){
      if (bufferChanged) {
	 if (gameStatus==pieceMoving) {
	      if (cMoveStep==0)
	         gBuffer.fillRect(baseX+cellW*toX,baseY+cellH*toY,cellW,cellH);
	      else
	       gBuffer.fillRect(baseX+cellW*fromX+stepX*(cMoveStep-1),baseY+cellH*fromY+stepY*(cMoveStep-1),cellW,cellH);
	       gBuffer.drawImage(pieces[targetElement],
	                    baseX+cellW*fromX+stepX*cMoveStep,
			    baseY+cellH*fromY+stepY*cMoveStep,
			    Graphics.TOP|Graphics.LEFT);
	 } else {
           gBuffer.setColor(0xffffff);
           gBuffer.fillRect(0, 0, scrwidth,scrheight);
           Mosaic_GSR loc=(Mosaic_GSR) client_sb.getGameStateRecord();
           gBuffer.setColor(0x0);

           for (int i=0;i<Amount;i++)
	     for (int j=0;j<Amount;j++) {
	       int n=loc.getElementAt(j,i);
	       //if (n<(Amount*Amount))
	        if (n==loc.EMPTY_CELL)gBuffer.fillRect(baseX+cellW*j,baseY+cellH*i, cellW,cellH);
		 else
	         gBuffer.drawImage(pieces[n],baseX+cellW*j,baseY+cellH*i,Graphics.TOP|Graphics.LEFT);
	     }
	 }
        gBuffer.drawRect(baseX,baseY,cellW*Amount-1,cellH*Amount-1);
	if (blink && blinkImage!=null) {
	   gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}

	bufferChanged=false;
      }
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
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;

              case showImage : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.drawImage(levelImage,baseX,baseY,Graphics.TOP|Graphics.LEFT); break;

	      case gameOver: {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
			    } break;

	      //case demoPlay:
	      case demoPlay:
	      case pieceMoving:
              case playGame : DoubleBuffer();
		              g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      break;
            }
	    if (gameStatus != notInitialized) g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || (gameStatus == pieceMoving && prevGameStatus == playGame));
    }
    public void gameLoaded(){
       ticks = 0;
       playerScore=0;
       blink=false;
       int level = client_sb._game_level;
       if (CurrentLevel!=level) {
	  CurrentLevel=level;
	  Amount=CurrentLevel+3;
	  levelImage = iLevels[CurrentLevel];
	  cellW=(int)levelImage.getWidth()/Amount;
	  cellH=(int)levelImage.getHeight()/Amount;
	  baseX=((scrwidth-levelImage.getWidth())>>1);
	  baseY=((scrheight-levelImage.getHeight())>>1);

	  pieces = new Image[Amount*Amount];
	  for (int i=0;i<Amount;i++)
	    for (int j=0;j<Amount;j++) {
	       pieces[i*Amount+j]=Image.createImage(cellW,cellH);
	       Graphics g1 = pieces[i*Amount+j].getGraphics();
	       g1.drawImage(levelImage,-j*cellW,-i*cellH,Graphics.TOP|Graphics.LEFT);
	       g1.setColor(0x00000);
	       //g1.setStrokeStyle(Graphics.DOTTED);
	       g1.drawLine(0,0,cellW-1,0);
	       g1.drawLine(0,0,0,cellH-1);
	       //g1.setStrokeStyle(Graphics.SOLID);
	    }
       }
  	//client_sb.newGame(CurrentLevel);
	bufferChanged=true;
	gameStatus=playGame;
	System.gc();
	repaint();
    }

}

