
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.PackRace.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, LoadListener {

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
  private boolean isShown = false, KeyVisible=false, DoorVisible=false;;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image GunIco=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image Heart = null;
  private Image blinkImage=null;
  private final static int BORDER = 30, // in cells
                           X_OVERLAY = 3, Y_OVERLAY = 6,
                           CELL_WIDTH = PackRace_SB.VIRTUAL_CELL_WIDTH-X_OVERLAY, CELL_HEIGHT = PackRace_SB.VIRTUAL_CELL_HEIGHT-Y_OVERLAY,
			   bsWIDTH = CELL_WIDTH*Labyrints.FIELD_WIDTH+X_OVERLAY*Labyrints.FIELD_HEIGHT,
			   bsHEIGHT = (CELL_HEIGHT)*Labyrints.FIELD_HEIGHT+Y_OVERLAY,
			   MOVE_SPEED = 1;//CELL_WIDTH/Creature.FRAME_MOVE,
			   // vis stage elements
/*
			   iWALL = 0,
			   iRICE = 1,
			   iGUN = 2,
			   //characters
			   iAPPEARANCE = 3,
			   iCREATURE_LEFT  = iAPPEARANCE     + Creature.FRAME_APPEARANCE,
			   iCREATURE_RIGHT = iCREATURE_LEFT  + Creature.FRAME_MOVE,
			   iCREATURE_UP    = iCREATURE_RIGHT + Creature.FRAME_MOVE,
			   iCREATURE_DOWN  = iCREATURE_UP    + Creature.FRAME_MOVE,
			   iHUNTER_LEFT    = iCREATURE_DOWN  + Creature.FRAME_MOVE,
			   iHUNTER_RIGHT   = iHUNTER_LEFT    + Creature.FRAME_MOVE,
			   iHUNTER_UP      = iHUNTER_RIGHT   + Creature.FRAME_MOVE,
			   iHUNTER_DOWN    = iHUNTER_UP      + Creature.FRAME_MOVE;
*/

  private Image items[];// = new Image[iHUNTER_DOWN+Creature.FRAME_MOVE];
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private int scrX =0, scrY =0;
  private boolean widthIsAligned = false;
  private boolean heightIsAligned = false;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected int direct=PackRace_SB.DIRECT_NONE;
  protected PackRace_SB client_sb = new PackRace_SB();
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
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
  private int [] stageSleepTime = {100,100,100,100,80,200,200,200}; // in ms

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
           Title = items[IMG_PR];
           Lost = items[IMG_LOST];
           Won = items[IMG_WIN];
           DemoTitle = items[IMG_DEMO];
           Heart = items[IMG_HEART];
	   GunIco = items[IMG_GRENADEICO];
           setBlinkImage(DemoTitle);
/*
           items[iWALL] = items[IMG_WALL];
           items[iRICE] = items[IMG_RICE];
           items[iGUN] = items[IMG_GRENADE];

           items[iAPPEARANCE] = items[IMG_APP0];
           items[iAPPEARANCE+1] = items[IMG_APP1];
           items[iAPPEARANCE+2] = items[IMG_APP2];
           items[iAPPEARANCE+3] = items[IMG_APP3];

           items[iHUNTER_LEFT] = items[IMG_KOLLFT0];
           items[iHUNTER_LEFT+1] = items[IMG_KOLLFT1];

           items[iHUNTER_RIGHT] = items[IMG_KOLRGT0];
           items[iHUNTER_RIGHT+1] = items[IMG_KOLRGT1];

           items[iHUNTER_UP] = items[IMG_KOLUP0];
           items[iHUNTER_UP+1] = items[IMG_KOLUP1];

           items[iHUNTER_DOWN] = items[IMG_KOLDWN0];
           items[iHUNTER_DOWN+1] = items[IMG_KOLDWN1];

           items[iCREATURE_LEFT] = items[IMG_G_L0];
           items[iCREATURE_LEFT+1] = items[IMG_G_L1];

           items[iCREATURE_RIGHT] = items[IMG_G_R0];
           items[iCREATURE_RIGHT+1] = items[IMG_G_R1];

           items[iCREATURE_UP] = items[IMG_G_U0];
           items[iCREATURE_UP+1] = items[IMG_G_U1];

           items[iCREATURE_DOWN] = items[IMG_G_D0];
           items[iCREATURE_DOWN+1] = items[IMG_G_D1];
*/

     } catch(Exception e) {
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }

     Runtime.getRuntime().gc();
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//     stage = Labyrinths.indexes[0];
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
       blink=false;
       ticks = 0;
       direct=PackRace_SB.DIRECT_NONE;

       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(PackRace_SB.LEVEL0);
	    client_sb.initStage(0);
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
           stageSleepTime[playGame] = ((PackRace_GSR)client_sb.getGameStateRecord()).getTimeDelay();
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
       isShown = false;
       direct = PackRace_SB.DIRECT_NONE;
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
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=PackRace_SB.DIRECT_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=PackRace_SB.DIRECT_LEFT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=PackRace_SB.DIRECT_UP; break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=PackRace_SB.DIRECT_DOWN; break;
				   default:direct=PackRace_SB.DIRECT_NONE;
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=PackRace_SB.DIRECT_NONE;
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
	                if (ticks<2) {
			   // LostWon = null;
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
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==PackRace_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks==0) {
				client_sb.initStage(stage);
		                direct=PackRace_SB.DIRECT_NONE;
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks==1){
				client_sb.i_Button = direct;
                                client_sb.nextGameStep();
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
	                   client_sb.i_Button = direct;
			   client_sb.nextGameStep();
			   PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();
                           ticks++;
/*
			   if (loc.getGameState()==PackRace_GSR.GAMESTATE_OVER) {

	                       gameStatus=gameOver;
                               playerScore=loc.getPlayerScores();
			       switch (loc.getPlayerState()) {
				      case PackRace_GSR.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case PackRace_GSR.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case PackRace_GSR.PLAYERSTATE_KILLED:
					                                      ticks =0; gameStatus=Crashed; break;
			                case PackRace_GSR.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Labyrints.LAB_NUMBER)
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
		             direct=PackRace_SB.DIRECT_NONE;
			     if (((PackRace_GSR)client_sb.getGameStateRecord()).getGameState()==PackRace_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGame();
				   AlignScreen();
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
       switch(bb)
        {
            case Labyrints.ELE_RICE   : return items[IMG_RICE];
            case Labyrints.ELE_WALL  : return items[IMG_WALL];
            case Labyrints.ELE_GUN : return items[IMG_GRENADE];
        }
	return null;//iBlank;
    }


   private Image getCreature(Creature creature, int base){
       switch(creature.getState())
        {
            case Creature.STATE_DOWN       : base+=Creature.FRAME_MOVE;
            case Creature.STATE_UP         : base+=Creature.FRAME_MOVE;
            case Creature.STATE_RIGHT      : base+=Creature.FRAME_MOVE;
            case Creature.STATE_LEFT       : return items[base+creature.getFrame()];
            case Creature.STATE_APPEARANCE : return items[IMG_APP0 + creature.getFrame()];
        }
//	System.out.println("!!!");
	return null;
    }

    private void AlignScreen() {
        PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();
	Creature cr = loc.getPlayer();

	int hy = transY(cr.getY());
        int hx = transX(cr.getX())+X_OVERLAY*(Labyrints.FIELD_HEIGHT - hy/CELL_HEIGHT);


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
    private void DrawBackScreen(){

        gBackScreen.setColor(0xffffff);
        gBackScreen.fillRect(0, 0, bsWIDTH, bsHEIGHT);

        PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();

        AlignScreen();
        Image img = null;
	int ldx = 0,ldy = 0;
        for(int ly=0;ly<Labyrints.FIELD_HEIGHT;ly++)
          for(int lx=0;lx<Labyrints.FIELD_WIDTH;lx++){
	      if ((img=getElementImage(loc.getElementAt(lx,ly)))!=null)
                 gBackScreen.drawImage(img, lx*CELL_WIDTH+(Labyrints.FIELD_HEIGHT-ly)*X_OVERLAY,ly*CELL_HEIGHT,0);

        }
	Runtime.getRuntime().gc();
    }

    protected int transX(int x){
      return x*CELL_WIDTH/PackRace_SB.VIRTUAL_CELL_WIDTH;
    }
    protected int transY(int y){
      return y*CELL_HEIGHT/PackRace_SB.VIRTUAL_CELL_HEIGHT;
    }

    protected void DrawPersonage(Graphics g, Image img, int hx, int hy, int z){
/*
        Image im = Image.createImage(img.getWidth(),img.getHeight());
	Graphics gim = im.getGraphics();
*/
	hx = transX(hx);
	hy = transY(hy);
//	int x=hx/CELL_WIDTH;
//	int y=hy/CELL_HEIGHT;
	hx=hx + (X_OVERLAY*(Labyrints.FIELD_HEIGHT*CELL_HEIGHT - hy))/CELL_HEIGHT;
/*
	int yofs=hy%CELL_HEIGHT;
	int xofs=hx%CELL_WIDTH;

	gim.drawImage(BackScreen,-hx,-hy,0);
	gim.drawImage(img,0,0,0);

	PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();
        Image ig = null;
        for(int ly=-1;ly<2;ly++)
          for(int lx=-2;lx<2;lx++){
	     if((y+ly)>0 && (y+ly)<Labyrints.FIELD_HEIGHT && (ly>0 || lx>0))
	      if ((ig=getElementImage(loc.getElementAt(x+lx,y+ly)))!=null)
                 gim.drawImage(ig, lx*CELL_WIDTH+(-ly)*X_OVERLAY+xofs,ly*CELL_HEIGHT+yofs,0);
        }

	g.drawImage(img, hx+scrX,hy+scrY,0);
*/
	g.drawImage(img, hx+scrX,hy+scrY,0);

    }

    protected void ChangeBackScreen(int hx, int hy){
        Image im = Image.createImage(PackRace_SB.VIRTUAL_CELL_WIDTH,PackRace_SB.VIRTUAL_CELL_HEIGHT);
	Graphics gim = im.getGraphics();

//	gim.drawImage(BackScreen,-hx,-hy,0);

        PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();


        Image img = null;
        for(int ly=-2;ly<3;ly++)
          for(int lx=-1;lx<2;lx++){
	     if ((hy+ly)>=0 && (hy+ly)<Labyrints.FIELD_HEIGHT && (hx+lx)>=0 && (hx+lx)<Labyrints.FIELD_WIDTH)
	      if ((img=getElementImage(loc.getElementAt(hx+lx,hy+ly)))!=null)
                 gim.drawImage(img, lx*CELL_WIDTH+(-ly)*X_OVERLAY,ly*CELL_HEIGHT,0);
        }
	gBackScreen.drawImage(im, hx*CELL_WIDTH+(Labyrints.FIELD_HEIGHT-hy)*X_OVERLAY,hy*CELL_HEIGHT,0);

    }


    protected void DoubleBuffer(Graphics gBuffer){

//long time = System.currentTimeMillis();
//        gBuffer.setColor(0xffffff);
//        gBuffer.fillRect(0, 0, scrwidth, scrheight);

//      if (bufferChanged) {


        PackRace_GSR loc=(PackRace_GSR)client_sb.getGameStateRecord();
        Image img = null;

	Creature pl = loc.getPlayer();
	int hy = transY(pl.getY());
        int hx = transX(pl.getX())+X_OVERLAY*(Labyrints.FIELD_HEIGHT - hy/CELL_HEIGHT);

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

	gBuffer.drawImage(BackScreen,scrX,scrY,0);

        Creature [] _k = loc.getGuardianArray();
        for(int li=0;li<_k.length;li++)
	    DrawPersonage(gBuffer, getCreature(_k[li],IMG_G_L0), _k[li].getX(),_k[li].getY(),0);

	if (client_sb.isFieldChanged()){
	      ChangeBackScreen(client_sb.getChangeX(),client_sb.getChangeY());
	}
        if(gameStatus == playGame && client_sb.isGunMode() && ((ticks&2)==0 || (client_sb.GUN_TICKS-loc.getGunTicks())>20))
	    gBuffer.drawImage(GunIco,1,1,0);

        if(!(gameStatus == Crashed && blink)){
	 DrawPersonage(gBuffer, getCreature(pl,IMG_KOLLFT0),pl.getX(),pl.getY(),0);
	 for (int i=0;i<loc.getAttemptions()-1;i++){
           gBuffer.drawImage(Heart,baseX+scrwidth-(i+1)*(Heart.getWidth()+2)-10,baseY+scrheight-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
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
       PackRace_GSR loc = (PackRace_GSR)client_sb.getGameStateRecord();
       blink=false;
       ticks = 1;
       direct=PackRace_SB.DIRECT_NONE;
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.getTimeDelay();
       stage = loc.getStage();
       gameStatus=newStage;
//       client_sb.nextGameStep();
//       DrawBackScreen();
       bufferChanged=true;
       repaint();
    }
private static final int IMG_PR = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_HEART = 4;
private static final int IMG_GRENADEICO = 5;
private static final int IMG_WALL = 6;
private static final int IMG_RICE = 7;
private static final int IMG_GRENADE = 8;
private static final int IMG_APP0 = 9;
private static final int IMG_APP1 = 10;
private static final int IMG_APP2 = 11;
private static final int IMG_APP3 = 12;
private static final int IMG_KOLLFT0 = 13;
private static final int IMG_KOLLFT1 = 14;
private static final int IMG_KOLRGT0 = 15;
private static final int IMG_KOLRGT1 = 16;
private static final int IMG_KOLUP0 = 17;
private static final int IMG_KOLUP1 = 18;
private static final int IMG_KOLDWN0 = 19;
private static final int IMG_KOLDWN1 = 20;
private static final int IMG_G_L0 = 21;
private static final int IMG_G_L1 = 22;
private static final int IMG_G_R0 = 23;
private static final int IMG_G_R1 = 24;
private static final int IMG_G_U0 = 25;
private static final int IMG_G_U1 = 26;
private static final int IMG_G_D0 = 27;
private static final int IMG_G_D1 = 28;
private static final int TOTAL_IMAGES_NUMBER = 29;

}

