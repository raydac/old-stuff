
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Wall.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, PlayerBlock, GameActionListener, LoadListener {

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 65;


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

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

  private Image [] Elements;
  private byte [][] ByteArray;

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
  private Image BackScreen=null;
  private Graphics gBuffer=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image iBall = null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

//  private Image []racket = new Image[2];
//  private Image []element = new Image[6];

  private int CurrentLevel=3;
  protected Wall_PMR client_pmr = null;
  protected int direct=Wall_PMR.BUTTON_NONE;
  protected Wall_SB client_sb = new Wall_SB(this);
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private int stage = 0;
//  private int lastPlayerScores =0;


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

    if (!this.isDoubleBuffered())
    {
//      System.out.println("not dbufferred");
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    client_sb.setPlayerBlock(this);
    BackScreen=Image.createImage(Math.max(VisWidth,Labyrinths.FIELD_WIDTH*Wall_SB.CELL_WIDTH),
                                 Math.max(VisHeight,Labyrinths.FIELD_HEIGHT*Wall_SB.CELL_HEIGHT));
    gBackScreen=BackScreen.getGraphics();

    VisWidth = BackScreen.getWidth();
    VisHeight = BackScreen.getHeight();

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

     if(gameStatus!=demoPlay)
       client_pmr.setButtonValue(direct);
     else{
        int d = Wall_PMR.BUTTON_NONE;
        Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();
        Ball [] _balls = loc.getBallsArray();
	if (_balls.length>0){
	  Ball _aball = _balls[0];
	  if (loc.getRacketY()-_aball._y<5) d = Wall_PMR.BUTTON_UPTHROW;
	  else
	   if (loc.getRacketX()-_aball._x<-10) d = Wall_PMR.BUTTON_RIGHT;
	   else
	     if (loc.getRacketX()-_aball._x>-4) d = Wall_PMR.BUTTON_LEFT;
	}
       client_pmr.setButtonValue(d);
     }
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
//	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;

	   iBall = Elements[IMG_BALL];
/*
	   racket[0]=Elements[IMG_ROCKETSHRT];
	   racket[1]=Elements[IMG_ROCKETLNG];

	   element[0]=Elements[IMG_ELE1];
	   element[1]=Elements[IMG_ELE2];
	   element[2]=Elements[IMG_ELE3];
	   element[3]=Elements[IMG_ELEWALL];
	   element[4]=Elements[IMG_ELE1];/*IMG_ELES* /
	   element[5]=Elements[IMG_SURP];
*/

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
     stage = Labyrinths.indexes[1];
     Runtime.getRuntime().gc();
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Wall_PMR(Wall_PMR.BUTTON_NONE);

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
       playerScore=0;
       blink=false;
       ticks = 0;
       direct=Wall_PMR.BUTTON_NONE;
       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Wall_SB.LEVEL2);
	    client_sb.nextStage(2);
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
//	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
           stageSleepTime[playGame] = ((Wall_GSR)client_sb.getGameStateRecord()).getLevelDelay();
//	   lastPlayerScores = 0;
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
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Wall_PMR.BUTTON_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=Wall_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=Wall_PMR.BUTTON_UPTHROW; break;
				      default:
	                                switch (getGameAction(keyCode)) {
				            case Canvas.RIGHT: direct=Wall_PMR.BUTTON_RIGHT; break;
	                                    case Canvas.LEFT: direct=Wall_PMR.BUTTON_LEFT; break;
				            case Canvas.UP: direct=Wall_PMR.BUTTON_UPTHROW; break;
						 default:direct=Wall_PMR.BUTTON_NONE;
	                             }
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Wall_PMR.BUTTON_NONE;
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
			   System.gc();
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep();
			   //UpdateBackScreen();
			   Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();
			   if (loc.getPlayerState()!=Wall_GSR.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=Wall_PMR.BUTTON_NONE;
			        client_sb.nextStage(stage);
				client_sb.nextGameStep();
				DrawBackScreen();
				System.gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
				System.gc();
			        repaint();
	                      }
			    } break;
           case playGame :{
	                   if(ticks++>100){System.gc();ticks=0;}
			   client_sb.nextGameStep();
			   //UpdateBackScreen();
			   Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Wall_GSR.GAMESTATE_OVER) {

	                       gameStatus=gameOver;
                               playerScore=loc.getPlayerScores();
			       switch (loc.getPlayerState()) {
				      case Wall_GSR.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Wall_GSR.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else
			         switch (loc.getPlayerState()) {
					case Wall_GSR.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Wall_GSR.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Labyrinths.LAB_NUMBER)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
								      break;
			         }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=Wall_PMR.BUTTON_NONE;
			     if (((Wall_GSR)client_sb.getGameStateRecord()).getGameState()==Wall_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGame();
			           gameStatus=playGame;
				   System.gc();
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

    public void actionEvent(int event_id){
         Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();
	 switch(event_id) {
	 case Wall_SB.BRICK_REMOVED:
		  {
		    int lx = client_sb.getLastElementX();
	            int ly = client_sb.getLastElementY();
                    int ldy = ly*Wall_SB.CELL_HEIGHT;
	            int ldx = lx*Wall_SB.CELL_WIDTH;
	            Image img = img=getImageElement(loc.getElementAt(lx,ly));
	            if (img!=null)
	              gBackScreen.drawImage(img,ldx,ldy,0);
		       else
		          gBackScreen.fillRect(ldx,ldy,Wall_SB.CELL_WIDTH,Wall_SB.CELL_HEIGHT);
		  }
	 }
    }

    private Image getImageElement(int bb){
                switch(bb)
                {
                   case Labyrinths.ELE_ELE1 : return Elements[IMG_ELE1];
                   case Labyrinths.ELE_ELE2  : return Elements[IMG_ELE2];
                   case Labyrinths.ELE_ELE3  : return Elements[IMG_ELE3];
                   case Labyrinths.ELE_WALL : return Elements[IMG_ELEWALL];
                   case Labyrinths.ELE_SURP_RACKET:
                   case Labyrinths.ELE_SURP_GLUE  :
                   case Labyrinths.ELE_SURP_BALLS  :
                   case Labyrinths.ELE_SURP_WIDTH : return Elements[IMG_ELE1];
		   default: return null;
                }


    }
    private void DrawBackScreen(){

        gBackScreen.setColor(0x1111ff);
        gBackScreen.fillRect(0, 0, BackScreen.getWidth(), BackScreen.getHeight());

        Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();

        Image img = null;
	int ldx = 0,ldy = 0;
        for(int lx=0;lx<Labyrinths.FIELD_WIDTH;lx++){
	    ldy = 0;
            for(int ly=0;ly<Labyrinths.FIELD_HEIGHT;ly++)
            {
		if ((img=getImageElement(loc.getElementAt(lx,ly)))!=null)
                        gBackScreen.drawImage(img,ldx,ldy,0);
                ldy += Wall_SB.CELL_HEIGHT;
            }
	    ldx += Wall_SB.CELL_WIDTH;

        }
	System.gc();

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

//long time = System.currentTimeMillis();

//      if (bufferChanged) {

	// prepare screen
        Wall_GSR loc=(Wall_GSR)client_sb.getGameStateRecord();
        Image img = null;

	gBuffer.drawImage(BackScreen,0,0,0);


        // Drawing of surprise
        Surprise [] _sur = loc.getSurpriseArray();
        Surprise _surp = null;
        for(int li=0;li<_sur.length;li++)
        {
            _surp = _sur[li];
            if (_surp._active)
            {
                gBuffer.drawImage(Elements[IMG_SURP], _surp._x,_surp._y,Graphics.TOP|Graphics.LEFT);
            }
        }
	_surp = null;

        // Drawing of the racket

	if (loc.getRacketState() == Wall_SB.RACKETSTATE_LONG) img = Elements[IMG_ROCKETLNG];
	  else img = Elements[IMG_ROCKETSHRT];
        if (gameStatus == Crashed)
 	   gBuffer.drawImage(img,loc.getRacketX(), loc.getRacketY()+ticks, Graphics.TOP|Graphics.LEFT);
	    else
 	      gBuffer.drawImage(img,loc.getRacketX(), loc.getRacketY(), Graphics.TOP|Graphics.LEFT);


        // Drawing of balls
        Ball [] _balls = loc.getBallsArray();
        Ball _aball = null;
        for(int li=0;li<_balls.length;li++)
        {
            _aball = _balls[li];
            if (_aball._active)
            {
                gBuffer.drawImage(iBall, _aball._x,_aball._y,Graphics.TOP|Graphics.LEFT);
            }
        } _aball = null;

        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<loc.getAttemptions()-1;i++){
           gBuffer.drawImage(iBall,VisWidth-(i+1)*(iBall.getWidth()+2)-10,VisHeight-iBall.getHeight()-2,Graphics.TOP|Graphics.LEFT);
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
	                  g.drawImage(GetImage(IMG_RB),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=Wall_PMR.BUTTON_NONE;
       //CurrentLevel=level;
       Wall_GSR loc =(Wall_GSR)client_sb.getGameStateRecord();
       stageSleepTime[playGame] = loc.getLevelDelay();
//       lastPlayerScores = 0;
       stage = loc.getStage();
       client_sb.nextGameStep();
       DrawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final int IMG_RB = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_SURP = 3;
private static final int IMG_DEMO = 4;
private static final int IMG_ELE3 = 5;
private static final int IMG_ROCKETLNG = 6;
private static final int IMG_ROCKETSHRT = 7;
private static final int IMG_ELE2 = 8;
private static final int IMG_ELEWALL = 9;
private static final int IMG_ELE1 = 10;
private static final int IMG_BALL = 11;
private static final int TOTAL_IMAGES_NUMBER = 12;
}

/*
.
RB,0
LOST,1
WON,2
DEMO,3

ROCKETSHRT,4
ROCKETLNG,5

ELE1,6
ELE2,7
ELE3,8
ELEWALL,9
BALL,10
SURP,11
.
.
*/
