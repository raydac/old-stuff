
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Socoban.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock,LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time
  private final static int KEY_CLICK_DELAY = 3;
  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false, KeyVisible=false, DoorVisible=false;

  private Image [] items;

  private Image Title;
  private Image Won;
  private Image DemoTitle;
  private Image Eye;
  private Image MenuIcon;

/*
  private Image iMOVE_UP;
  private Image iMOVE_DOWN;
  private Image iMOVE_LEFT;
  private Image iMOVE_RIGHT;
  private Image iBOX;
  private Image iBOX_PLACE;
  private Image iBOX_ON_PLACE;
  private Image iWALL;
*/
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;


  private boolean widthIsAligned = false;
  private boolean heightIsAligned = false;
  private int scrX =0, scrY =0;
  private int vieX =0, vieY =0, view = 0;
  private int bsWIDTH = 0, bsHEIGHT = 0, FIELD_WIDTH = 0, FIELD_HEIGHT = 0;
  private final static int BORDER = 20, // in cells
                           CELL_WIDTH = 10,CELL_HEIGHT = 10, MOVE_SPEED = CELL_WIDTH;
			   // vis stage elements



  private int CurrentLevel=3;
  protected Socoban_PMR client_pmr = null;
  protected int direct=Socoban_PMR.DIRECT_NONE;
  protected Socoban_SB client_sb = new Socoban_SB();
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private int stage = 0;

  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
			    newStage = 3,
                            playGame = 4,
			    Finished = 5;
  private int [] stageSleepTime = {100,100,200,100,50,200}; // in ms
  private int demoStep = 0;
  public int gameStatus=notInitialized;
  private boolean bufferChanged=true, justPressed = false;
  int blinks=0, ticks=0;
  private int lastDirection = -1;

//  private static final int IMG_MENUICO = 147; //menuico.png

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
///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

     if(demoStep>=Labyrinths.DEMO_STEP.length)demoStep=0;
     if(gameStatus == demoPlay) direct=Labyrinths.DEMO_STEP[demoStep++];

       client_pmr.setDirect(direct);
       if (direct!= Socoban_PMR.DIRECT_BACK && direct!= Socoban_PMR.DIRECT_NONE) lastDirection = direct;
       //direct
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
	   items = new ImageBlock(this)._image_array;
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];

           Title=items[IMG_AM];
           Won=items[IMG_WIN];
           DemoTitle=items[IMG_DEMO];
           Eye=items[IMG_EYE3];

     } catch(Exception e) {
     }
     stage = Labyrinths.FIELD_HEIGHT[1];
     Runtime.getRuntime().gc();
    }


    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Socoban_PMR(Socoban_PMR.DIRECT_NONE);

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
       lastDirection = -1;
       blink=false;
       ticks = 0;
	if (gameStatus==titleStatus) {
	    demoStep = 0;
	    client_sb.newGame(10/*demolevel*/);
	    preparestage(10);
//	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
	} else {
	   client_sb.newGame(0);
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
	      case Finished: if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
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
				              if (ticks>=TOTAL_OBSERVING_TIME){
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
			       if(direct!=-1)
			       switch(keyCode){
				case Canvas.KEY_POUND:
				case Canvas.KEY_STAR: direct=Socoban_PMR.DIRECT_BACK; break;
				case Canvas.KEY_NUM6/*RIGHT*/: justPressed = true; direct=Socoban_PMR.DIRECT_RIGHT; break;
				case Canvas.KEY_NUM4/*LEFT*/: justPressed = true;direct=Socoban_PMR.DIRECT_LEFT; break;
				case Canvas.KEY_NUM2/*UP*/: justPressed = true;direct=Socoban_PMR.DIRECT_UP; break;
				case Canvas.KEY_NUM8/*DOWN*/: justPressed = true;direct=Socoban_PMR.DIRECT_DOWN; break;
				case Canvas.KEY_NUM5/*DOWN*/: vieX=scrX;vieY=scrY;direct=-1; break;
				default:justPressed = false;direct=Socoban_PMR.DIRECT_NONE;
			       } else
			        switch(keyCode){
			         case Canvas.KEY_NUM6/*RIGHT*/: view = 1;break;
				 case Canvas.KEY_NUM4/*LEFT*/: view = 2; break;
				 case Canvas.KEY_NUM2/*UP*/:  view = 3; break;
				 case Canvas.KEY_NUM8/*DOWN*/: view = 4;break;
				 case Canvas.KEY_NUM5/*DOWN*/: direct=Socoban_PMR.DIRECT_NONE; break;
				 default:view = 0;
			        }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu();
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  if(direct!=-1)
	    direct=Socoban_PMR.DIRECT_NONE;
	   else view = 0;
	  justPressed = false;
        }
    }

    private void preparestage(int n){
       FIELD_WIDTH = Labyrinths.FIELD_WIDTH[stage];
       FIELD_HEIGHT = Labyrinths.FIELD_HEIGHT[stage];
       bsWIDTH = FIELD_WIDTH*CELL_WIDTH;
       bsHEIGHT = FIELD_HEIGHT*CELL_HEIGHT;

       BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
       gBackScreen=BackScreen.getGraphics();

       int score = 0;
       if ((Socoban_GSR)client_sb.getGameStateRecord()!=null){
           score = ((Socoban_GSR)client_sb.getGameStateRecord())._counter_move;
       }
       client_sb.newGame(stage);
       ((Socoban_GSR)client_sb.getGameStateRecord())._counter_move = score;
       direct=Socoban_PMR.DIRECT_NONE;
       client_sb.nextGameStep();
       Runtime.getRuntime().gc();
    }

    public void run() {

      while(animation) {
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
	                if (ticks<2) {
			   Runtime.getRuntime().gc();
	                }
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(0,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   Socoban_GSR loc=(Socoban_GSR)client_sb.getGameStateRecord();
			   if (loc.getPlayerState()==Socoban_GSR.PLAYSTATE_WON || ++ticks>300) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks==0) {
				preparestage(stage);
	                      }
			      else
	                      if (ticks==1){
				DrawBackScreen();
				System.gc();
	                      }
			      else
	                      if (ticks>5){
				ticks = 0;
			        gameStatus=playGame;
	                      }
			    ticks++;
			    repaint();
			    } break;
           case playGame :{
	                   ticks++;
			   if(direct!=-1){
	                     if(justPressed){
			       justPressed = false;
			       client_sb.nextGameStep();
			       Socoban_GSR loc=(Socoban_GSR)client_sb.getGameStateRecord();
			       if (direct==Socoban_PMR.DIRECT_BACK){
//				DrawBackScreen();
	                        int a = loc.getPosition_X();
	                        int b = loc.getPosition_Y();
                                for(int i = Math.max(0,a-2);i<Math.min(a+3,FIELD_WIDTH);i++)
                                  for(int j = Math.max(0,b-2);j<Math.min(b+3,FIELD_HEIGHT);j++)
	                            updateBackScreen(i,j);
			       }
                               ticks=0;
			       if((ticks&0xff)==50) Runtime.getRuntime().gc();
			       if(loc.getPlayerState()==Socoban_GSR.PLAYSTATE_WON){
			          ticks = 0;
				  stage++;
				  if (stage>=Labyrinths.LAB_NUMBER)
				    gameStatus=Finished;
				  else
				    gameStatus=newStage;
			       }
	                     }
			    repaint();
			   } else{
			       switch(view){
			         case 1: if(vieX+bsWIDTH+2>scrwidth)vieX-=2; break;
				 case 2: if(vieX<-1)vieX+=2;else vieX = 0; break;
				 case 3: if(vieY<-1)vieY+=2;else vieY=0; break;
				 case 4: if(vieY+bsHEIGHT+2>scrheight)vieY-=2; break;
			       }
			    repaint();
	                   }
			   if(ticks>KEY_CLICK_DELAY && direct!=Socoban_PMR.DIRECT_NONE /*&& direct!=Socoban_PMR.DIRECT_BACK*/){
			      justPressed = true;
			   }
			  } break;
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

        try {
          thread.sleep(stageSleepTime[gameStatus]);
	} catch (Exception e) {}

      }
    }

    private void AlignPlayer(){

        Socoban_GSR loc=(Socoban_GSR)client_sb.getGameStateRecord();

        int hx = loc.getPosition_X()*CELL_WIDTH;
	int hy = loc.getPosition_Y()*CELL_HEIGHT;

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
    //     System.out.println("BSH:"+bsHEIGHT+", hy:"+hy+", SCRH:"+scrheight+", Y:"+scrY);
    }

    private void updateBackScreen(int x, int y){
        Socoban_GSR loc=(Socoban_GSR)client_sb.getGameStateRecord();
	int item = loc.getElementAt(x,y);
	if (item>1) gBackScreen.drawImage(items[item+IMG_BULD_U/*IMG_GARBAGE-1*/],x*CELL_WIDTH ,y*CELL_HEIGHT ,0);
	  else gBackScreen.fillRect(x*CELL_WIDTH ,y*CELL_HEIGHT ,CELL_WIDTH,CELL_HEIGHT);
    }

    private void DrawBackScreen(){
        gBackScreen.setColor(0xffffff);
        gBackScreen.fillRect(0, 0, bsWIDTH, bsHEIGHT);

        AlignPlayer();

        for(int lx=0;lx<FIELD_WIDTH;lx++)
            for(int ly=0;ly<FIELD_HEIGHT;ly++)
	      updateBackScreen(lx,ly);
    }


    protected void DoubleBuffer(Graphics gBuffer){

//long time = System.currentTimeMillis();
        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth, scrheight);

//      if (bufferChanged) {


        Socoban_GSR loc=(Socoban_GSR)client_sb.getGameStateRecord();
        Image img = null;

        int hx = loc.getPosition_X()*CELL_WIDTH;
	int hy = loc.getPosition_Y()*CELL_HEIGHT;

//BULD_L,4
//BULD_R,5
//BULD_U,6
//BULD_D,7
	int pl = 0;
	switch (lastDirection){
	  case Socoban_PMR.DIRECT_DOWN:pl++;
	  case Socoban_PMR.DIRECT_UP: pl++;
	  case Socoban_PMR.DIRECT_RIGHT: pl++;
	   default: pl+=IMG_BULD_L;
	}

if(direct!=-1){
	if ((hx+scrX+CELL_WIDTH)>(scrwidth-BORDER) ){
	  if ((bsWIDTH+scrX-MOVE_SPEED)>scrwidth) scrX-=MOVE_SPEED;//CELL_WIDTH;
	    else scrX = scrwidth - bsWIDTH;
	} else
	  if ((hx+scrX)<BORDER){
	     if (scrX+MOVE_SPEED<0) scrX+=MOVE_SPEED;//CELL_WIDTH;
	         else scrX = 0;

	  }
	if ((hy+scrY+CELL_HEIGHT)>(scrheight-BORDER)){
	    if ((bsHEIGHT+scrY-MOVE_SPEED)>scrheight) scrY-=MOVE_SPEED;//CELL_HEIGHT;
	       else scrY = scrheight - bsHEIGHT;
	} else
	      if ((hy+scrY)<BORDER){
	        if ((scrY+MOVE_SPEED)/*CELL_HEIGHT*/<0) scrY+=MOVE_SPEED;//CELL_HEIGHT;
	           else scrY = 0;
	      }

	int a = loc.getPosition_X();
	int b = loc.getPosition_Y();

	if(a-loc.getOldPosition_X()!=0 || b-loc.getOldPosition_Y()!=0)
        {
	      updateBackScreen(a,b);
	      updateBackScreen(a+1,b);
	      updateBackScreen(a-1,b);
	      updateBackScreen(a,b+1);
	      updateBackScreen(a,b-1);
	}

	gBuffer.drawImage(BackScreen,scrX,scrY,0);
	gBuffer.drawImage(items[pl],scrX+hx,scrY+hy,0);

} else {
	gBuffer.drawImage(BackScreen,vieX,vieY,0);
	if((ticks&4)==0)gBuffer.drawImage(Eye,0,0,0);
	gBuffer.drawImage(items[pl],vieX+hx,vieY+hy,0);
}


	if (gameStatus == demoPlay && blink) {
	       gBuffer.drawImage(DemoTitle,scrwidth>>1,scrheight-DemoTitle.getHeight()-5,Graphics.HCENTER|Graphics.VCENTER);
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
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(Won,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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

    public void restart(){
       gameStatus=newStage;
       ((Socoban_GSR)client_sb.getGameStateRecord())._counter_move = ((Socoban_GSR)client_sb.getGameStateRecord())._prestage_counter_move;
       ticks = 0;
       repaint();
    }


    public void gameLoaded(){
       lastDirection = -1;
       Socoban_GSR loc = (Socoban_GSR)client_sb.getGameStateRecord();
       blink=false;
       ticks = 1;
       direct=Socoban_PMR.DIRECT_NONE;
       CurrentLevel=loc.getLevel();
       //stageSleepTime[playGame] = loc.getGameTimedelay();
       stage = loc.getStage();

       FIELD_WIDTH = Labyrinths.FIELD_WIDTH[stage];
       FIELD_HEIGHT = Labyrinths.FIELD_HEIGHT[stage];
       bsWIDTH = FIELD_WIDTH*CELL_WIDTH;
       bsHEIGHT = FIELD_HEIGHT*CELL_HEIGHT;

       BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
       gBackScreen=BackScreen.getGraphics();

       direct=Socoban_PMR.DIRECT_NONE;
       client_sb.nextGameStep();

       gameStatus=newStage;

       bufferChanged=true;
       repaint();
    }
private static final byte IMG_AM = (byte)0;
private static final byte IMG_WIN = (byte)1;
private static final byte IMG_DEMO = (byte)2;
private static final byte IMG_EYE3 = (byte)3;
private static final byte IMG_PLACE = (byte)9;
private static final byte IMG_BULD_L = (byte)4;
private static final byte IMG_GARBPLACE = (byte)10;
private static final byte IMG_BULD_R = (byte)5;
private static final byte IMG_BULD_U = (byte)6;
private static final byte IMG_BULD_D = (byte)7;
private static final byte IMG_GARBAGE = (byte)8;
private static final byte IMG_WALL = (byte)11;
private static final byte TOTAL_IMAGES_NUMBER = (byte)12;

}

/*
.
AM,0
WIN,1
DEMO,2
EYE3,3
BULD_L,4
BULD_R,5
BULD_U,6
BULD_D,7
GARBAGE,8
PLACE,9
GARBPLACE,10
WALL,11
.
.
*/
