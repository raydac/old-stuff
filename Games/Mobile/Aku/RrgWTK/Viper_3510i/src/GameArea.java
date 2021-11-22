
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Snake.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock, LoadListener {

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
  private boolean isShown = false;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image Heart = null;
  private Image blinkImage=null;
  private final static int BORDER = 10,
                           SMOOTHNESS = 2,
                           CELL_WIDTH = 5, CELL_HEIGHT = 5,
			   bsWIDTH = CELL_WIDTH*Labyrinths.FIELD_WIDTH,
			   bsHEIGHT = CELL_WIDTH*Labyrinths.FIELD_HEIGHT;
/*
                           iMOUSE =0,
                           iWALL =1,
			   iHEAD =2,
			   iEATING = iHEAD +4,
			   iBODY_NORMAL = iEATING+4,
			   iBODY_SCREWED = iBODY_NORMAL+4,
			   iTAIL = iBODY_SCREWED +4,
			   iBG = iTAIL +4;
*/
  private Image items[];// = new Image[iBG+1];
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private int scrX =0, scrY =0;
  private boolean widthIsAligned = false;
  private boolean heightIsAligned = false;
  private boolean keyIsPressed = false;


  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected Snake_PMR client_pmr = null;
  protected int direct=Snake_PMR.DIRECT_NONE;
  protected Snake_SB client_sb = new Snake_SB();
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
  private int [] stageSleepTime = {100,100,100,200,80,200,200,200}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 500; // ticks count
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
    client_sb.setPlayerBlock(this);
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
    gBackScreen=BackScreen.getGraphics();
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

       client_pmr.setDirect(direct);
      if(!keyIsPressed) direct=Snake_PMR.DIRECT_NONE;

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
     loading(kkk);

     try {
           items = (new ImageBlock(this))._image_array;

	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title=items[IMG_VIPER];//Image.createImage(TitleImage];
           Lost=items[IMG_LOST];//Image.createImage(TitleImage];
           Won=items[IMG_WON];//Image.createImage(TitleImage];
           DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage];
           Heart=items[IMG_HEART]; //Image.createImage(DemoTitleImage];
           setBlinkImage(DemoTitle);

/*
	   for (int i=0; i<items.length;i++){
	    items[i] = Image.createImage(CELL_WIDTH,CELL_HEIGHT);
	    Graphics g = items[i].getGraphics();
	    g.setColor(0xffffff);
	    g.drawString(""+i,1,1,Graphics.TOP|Graphics.LEFT);
	    g.fillRect(0,0,CELL_WIDTH-1,CELL_HEIGHT-1);
	   }
*/
/*

           items[iMOUSE] = items[IMG_MOUSE];
           items[iWALL] = items[IMG_WALL];
           items[iBG] = items[IMG_LEVEL];

           items[iHEAD] = items[IMG_H_UP];
           items[iHEAD+1] = items[IMG_H_LFT];
           items[iHEAD+2] = items[IMG_H_RGHT];
           items[iHEAD+3] = items[IMG_H_DWN];

           items[iTAIL] = items[IMG_T_U];
           items[iTAIL+1] = items[IMG_T_L];
           items[iTAIL+2] = items[IMG_T_R];
           items[iTAIL+3] = items[IMG_T_D];

           items[iBODY_NORMAL] = items[IMG_B_L];
           items[iBODY_NORMAL+1] = items[IMG_B_R];
           items[iBODY_NORMAL+2] = items[IMG_B_D];
           items[iBODY_NORMAL+3] = items[IMG_B_U];

           items[iBODY_SCREWED] = items[IMG_B_UL];
           items[iBODY_SCREWED+1] = items[IMG_B_DL];
           items[iBODY_SCREWED+2] = items[IMG_B_UR];
           items[iBODY_SCREWED+3] = items[IMG_B_DR];
*/
	   // iEATING = iHEAD +4,


     } catch(Exception e) {
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }
     Runtime.getRuntime().gc();
     stage = Labyrinths.indexes[1];
    }


    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Snake_PMR(Snake_PMR.DIRECT_NONE);

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
       direct=Snake_PMR.DIRECT_NONE;

       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Snake_SB.LEVEL0);
	    client_sb.initStage(demoLevel);
	    if (demoLevel++>4)demoLevel=0;
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
           stageSleepTime[playGame] = ((Snake_GSR)client_sb.getGameStateRecord()).getLevelDelay();
	   lastPlayerScores = 0;
	   stage = 0;
	   gameStatus=newStage;
	}
	keyIsPressed = false;
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
/*
    protected void keyRepeated(int keyCode) {
       keyPressed(keyCode);
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
*/    //}

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
              case playGame :  { keyIsPressed = true;
			       int action = getGameAction(keyCode);
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Snake_PMR.DIRECT_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=Snake_PMR.DIRECT_LEFT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=Snake_PMR.DIRECT_UP; break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=Snake_PMR.DIRECT_DOWN; break;
				   default:direct=Snake_PMR.DIRECT_NONE;
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
//	  direct=Snake_PMR.DIRECT_NONE;
	  keyIsPressed = false;
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
			   Runtime.getRuntime().gc();
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   Snake_GSR loc=(Snake_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Snake_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
				client_sb.initStage(stage);
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
				client_sb.nextGameStep();
		                direct=Snake_PMR.DIRECT_NONE;
				DrawBackScreen();
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   client_sb.nextGameStep();
			   Snake_GSR loc=(Snake_GSR)client_sb.getGameStateRecord();
			         switch (loc.getPlayerState()) {
					case Snake_GSR.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Snake_GSR.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Labyrinths.LAB_NUMBER)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;  break;
			         }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=Snake_PMR.DIRECT_NONE;
			     if (((Snake_GSR)client_sb.getGameStateRecord()).getGameState()==Snake_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGame();
				   DrawBackScreen();
				   Runtime.getRuntime().gc();
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

    private Image getElementImage(int bb){
        switch ((bb>>4)){
	  case 0: switch(bb){
	             case Labyrinths.ELE_WALL : return items[IMG_WALL];
	             case Labyrinths.ELE_MOUSE : return items[IMG_MOUSE];
		     //case Labyrinths.ELE_GROUND: return items[IMG_LEVEL];
	          } break;
	  case 1: return items[IMG_H_UP+(bb&3)];
	  case 2: return items[IMG_T_U+(bb&3)];
	  case 3: return items[IMG_B_L+(bb&7)];
        }
          return null;
    }

    private void updateBackScreen(Image img, int lx, int ly){
       lx = lx*CELL_WIDTH;
       ly = ly*CELL_HEIGHT;
	if (img!=null){
	       gBackScreen.fillRect(lx,ly,CELL_WIDTH,CELL_HEIGHT);
               gBackScreen.drawImage(img, lx,ly,0);
	}
	    else
	       gBackScreen.fillRect(lx,ly,CELL_WIDTH,CELL_HEIGHT);
    }

    private void DrawBackScreen(){

        gBackScreen.setColor(0xffffff);
        gBackScreen.fillRect(0, 0, bsWIDTH, bsHEIGHT);

        Snake_GSR loc=(Snake_GSR)client_sb.getGameStateRecord();

        AlignPlayer();

        Image img = null;
	int ldx = 0,ldy = 0;
        for(int lx=0;lx<Labyrinths.FIELD_WIDTH;lx++){
	    ldy = 0;
            for(int ly=0;ly<Labyrinths.FIELD_HEIGHT;ly++)
            {
		if ((img=getElementImage(loc.getElementAt(lx,ly)))!=null)
                        gBackScreen.drawImage(img, ldx,ldy,0);
                ldy += CELL_HEIGHT;
            }
	    ldx += CELL_WIDTH;

        }
	Runtime.getRuntime().gc();
    }


    private void AlignPlayer(){

        Snake_GSR loc=(Snake_GSR)client_sb.getGameStateRecord();

	int hx = loc.getHeadX()*CELL_WIDTH;
	int hy = loc.getHeadY()*CELL_HEIGHT;

	if(bsWIDTH < scrwidth){
	   widthIsAligned = true;
	   scrX = (scrwidth - bsWIDTH)>>1;
	  } else {
	     widthIsAligned = false;
	     if (hx<BORDER) scrX=0;
	     else
	       if((bsWIDTH - hx)<=BORDER) scrX=scrwidth-bsWIDTH;
		  else
		    scrX=(scrwidth>>1)-hx;
	    }

	if(bsHEIGHT < scrheight){
	   heightIsAligned = true;
	   scrY = (scrheight - bsHEIGHT)>>1;
	  } else {
	     heightIsAligned = false;
	     if (hy<BORDER) scrY=0;
	     else
	       if((bsHEIGHT - hy)<=BORDER) scrY=scrheight-bsHEIGHT;
		  else
		    scrY=(scrheight>>1)-hy;
	    }
    }


    protected void DoubleBuffer(Graphics gBuffer){

//long time = System.currentTimeMillis();
        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth, scrheight);

//      if (bufferChanged) {


        Snake_GSR loc=(Snake_GSR)client_sb.getGameStateRecord();
        Image img = null;

	int hx= loc.getHeadX();
	int hy= loc.getHeadY();

        updateBackScreen(getElementImage(loc.getElementAt(hx,hy)),hx,hy);
        updateBackScreen(getElementImage(loc.getElementAt(loc.getPrevHeadX(),loc.getPrevHeadY())),loc.getPrevHeadX(),loc.getPrevHeadY());
        updateBackScreen(getElementImage(loc.getElementAt(loc.getTailX(),loc.getTailY())),loc.getTailX(),loc.getTailY());
        updateBackScreen(getElementImage(loc.getElementAt(loc.getTailPrevX(),loc.getTailPrevY())),loc.getTailPrevX(),loc.getTailPrevY());

	//System.out.println("["+hx+","+hy+"]X:"+scrX+", Y:"+scrY);
	hx= loc.getHeadX();
	hy= loc.getHeadY();
        hx*=CELL_WIDTH;
        hy*=CELL_HEIGHT;

	if (hx+scrX>scrwidth-BORDER ){
	  if (bsWIDTH+scrX-CELL_WIDTH>scrwidth) scrX-=CELL_WIDTH;
	    else scrX = scrwidth - bsWIDTH;
	} else
	  if (hx+scrX<BORDER){
	     if (scrX+CELL_WIDTH<0) scrX+=CELL_WIDTH;
	         else scrX = 0;

	  }
	  if (hy+scrY>scrheight-BORDER){
	    if (bsHEIGHT+scrY-CELL_HEIGHT>scrheight) scrY-=CELL_HEIGHT;
	       else scrY = scrheight - bsHEIGHT;
	  } else
	      if (hy+scrY<BORDER){
	        if (scrY+CELL_HEIGHT<0) scrY+=CELL_HEIGHT;
	           else scrY = 0;
	      }


	gBuffer.drawImage(BackScreen,scrX,scrY,0);



        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<loc.getAttemptions();i++){
           gBuffer.drawImage(Heart,baseX+scrwidth-(i+1)*(Heart.getWidth()+2)-10,baseY+scrheight-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
	if (gameStatus == demoPlay && blink && blinkImage!=null) {
	   gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
        gBuffer.setColor(0x0);
//        gBuffer.drawRect(baseX,baseY,levelImage.getWidth(),levelImage.getHeight());
	bufferChanged=false;
//System.out.println(System.currentTimeMillis() - time);

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
	      case newStage:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  gBuffer.setFont(f);
	                  } break;
//	      case Finished: if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_TH_WON);
//	      case gameOver: if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_TH_LOST);

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
      return (gameStatus == playGame || gameStatus == Crashed || gameStatus == newStage);
    }
    public void gameLoaded(){
       Snake_GSR loc = (Snake_GSR)client_sb.getGameStateRecord();
       stage = loc.getStage();
       playerScore=0;
       blink=false;
       ticks = 1;
       direct=Snake_PMR.DIRECT_NONE;
//       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = ((Snake_GSR)client_sb.getGameStateRecord()).getLevelDelay();
       lastPlayerScores = 0;
       gameStatus=newStage;
       keyIsPressed = false;
       bufferChanged=true;
       repaint();
    }
private static final byte IMG_WON = (byte)2;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_VIPER = (byte)0;
private static final byte IMG_DEMO = (byte)3;
private static final byte IMG_HEART = (byte)4;
private static final byte IMG_H_UP = (byte)7;
private static final byte IMG_H_LFT = (byte)8;
private static final byte IMG_H_RGHT = (byte)9;
private static final byte IMG_H_DWN = (byte)10;
private static final byte IMG_WALL = (byte)6;
private static final byte IMG_MOUSE = (byte)5;
private static final byte IMG_B_D = (byte)13;
private static final byte IMG_B_U = (byte)14;
private static final byte IMG_B_UL = (byte)15;
private static final byte IMG_B_DL = (byte)16;
private static final byte IMG_B_DR = (byte)18;
private static final byte IMG_T_U = (byte)19;
private static final byte IMG_T_D = (byte)22;
private static final byte IMG_B_UR = (byte)17;
private static final byte IMG_T_L = (byte)20;
private static final byte IMG_T_R = (byte)21;
private static final byte IMG_LEVEL = (byte)23;
private static final byte IMG_B_L = (byte)11;
private static final byte IMG_B_R = (byte)12;
private static final byte TOTAL_IMAGES_NUMBER = (byte)24;

}

/*
.
VIPER,0
LOST,1
WON,2
DEMO,3
HEART,4

MOUSE,5
WALL,6

H_UP,7
H_LFT,8
H_RGHT,9
H_DWN,10

B_L,11
B_R,12
B_D,13
B_U,14

B_UL,15
B_DL,16
B_UR,17
B_DR,18

T_U,19
T_L,20
T_R,21
T_D,22

LEVEL,23


.
.
*/
