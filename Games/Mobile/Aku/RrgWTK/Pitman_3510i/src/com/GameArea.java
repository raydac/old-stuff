package com;

import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Pitman.*;
//import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

//String WWW = "";

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
  private Image DemoTitle=null;

//  private Image Buffer=null;
//  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image Heart = null;
//  private byte[][] shadowLabyrinth = null;

  private final static int BORDER = 24, // in cells
                           CELL_WIDTH = Pitman_SB.VIRTUAL_CELL_WIDTH, CELL_HEIGHT = Pitman_SB.VIRTUAL_CELL_HEIGHT,
			   bsWIDTH = CELL_WIDTH*Labyrinths.LAB_WIDTH,
			   bsHEIGHT = CELL_HEIGHT*Labyrinths.LAB_HEIGHT,
			   MOVE_SPEED = MovingCreature.SPEED;

			   // vis stage elements

  private Image items[];

  private int scrX =0, scrY =0;
  private boolean widthIsAligned = false;
  private boolean heightIsAligned = false;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected int direct=Pitman_SB.MOVE_NONE;
  protected Pitman_SB client_sb = new Pitman_SB();
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private int stage = 5;
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
  private int [] stageSleepTime = {100,100,150,100,80,200,200,200}; // in ms

  private int playerScore=0;
  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = Labyrinths.DEMO_LEVEL;
  private int demoLimit = 0;
  private int demoPosition = 0;
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
//    Buffer=Image.createImage(scrwidth,scrheight);
//    gBuffer=Buffer.getGraphics();
    BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
    gBackScreen=BackScreen.getGraphics();
    gameStatus=notInitialized;
//    shadowLabyrinth = new byte[Labyrinths.LAB_WIDTH][Labyrinths.LAB_HEIGHT];
  }

///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */

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
	   items = (new ImageBlock(this))._image_array;

//	   DemoTitle = Image.createImage(CELL_WIDTH,CELL_HEIGHT);
//	   DemoTitle.getGraphics().drawImage(items[IMG_DIAMOND],0,0,0);
//	   items[IMG_DIAMOND] = DemoTitle;

	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title    = items[IMG_PITMAN];
           Lost     = items[IMG_LOST];
           Won      = items[IMG_WIN];
           DemoTitle= items[IMG_DEMO];

//	   Lost = DemoTitle;
//	   Won = DemoTitle;
//	   items[IMG_LOST] = null;
//	   items[IMG_WIN] = null;
/*
           items[iDIRT]=pnl.getImageForID(IMG_DIRT);
           items[iTITANIUM_WALL]=pnl.getImageForID(IMG_TITAN);
           items[iDIAMOND]=
	   items[iDIAMOND].getGraphics().drawImage(pnl.getImageForID(IMG_DIAMOND),0,0,0);
           items[iSTONE]=pnl.getImageForID(IMG_STONE);
           items[iEMPTY]=Image.createImage(CELL_WIDTH,CELL_HEIGHT);

           items[iPLAYER_LEFT]=pnl.getImageForID(IMG_PL_LFT0);
           items[iPLAYER_LEFT+1]=pnl.getImageForID(IMG_PL_LFT1);
           items[iPLAYER_LEFT+2]=pnl.getImageForID(IMG_PL_LFT2);

           items[iPLAYER_RIGHT]=pnl.getImageForID(IMG_PL_RGHT0);
           items[iPLAYER_RIGHT+1]=pnl.getImageForID(IMG_PL_RGHT1);
           items[iPLAYER_RIGHT+2]=pnl.getImageForID(IMG_PL_RGHT2);

           items[iPLAYER_UP]=pnl.getImageForID(IMG_PL_UP0);
           items[iPLAYER_UP+1]=pnl.getImageForID(IMG_PL_UP1);
           items[iPLAYER_UP+2]=pnl.getImageForID(IMG_PL_UP2);

           items[iPLAYER_DOWN]=pnl.getImageForID(IMG_PL_DWN0);
           items[iPLAYER_DOWN+1]=pnl.getImageForID(IMG_PL_DWN1);
           items[iPLAYER_DOWN+2]=pnl.getImageForID(IMG_PL_DWN2);

           items[iPLAYER_STAND]=pnl.getImageForID(IMG_PL_STND0);
           items[iPLAYER_STAND+1]=pnl.getImageForID(IMG_PL_STND1);
           items[iPLAYER_STAND+2]=pnl.getImageForID(IMG_PL_STND2);


           items[iBUTTERFLY_LEFT]=pnl.getImageForID(IMG_BAT0);
           items[iBUTTERFLY_LEFT+1]=pnl.getImageForID(IMG_BAT1);
           items[iBUTTERFLY_LEFT+2]=pnl.getImageForID(IMG_BAT2);

           items[iBUTTERFLY_RIGHT]=items[iBUTTERFLY_LEFT];
           items[iBUTTERFLY_RIGHT+1]=items[iBUTTERFLY_LEFT+1];
           items[iBUTTERFLY_RIGHT+2]=items[iBUTTERFLY_LEFT+2];

           items[iBUTTERFLY_UP]=items[iBUTTERFLY_LEFT];
           items[iBUTTERFLY_UP+1]=items[iBUTTERFLY_LEFT+1];
           items[iBUTTERFLY_UP+2]=items[iBUTTERFLY_LEFT+2];

           items[iBUTTERFLY_DOWN]=items[iBUTTERFLY_LEFT];
           items[iBUTTERFLY_DOWN+1]=items[iBUTTERFLY_LEFT+1];
           items[iBUTTERFLY_DOWN+2]=items[iBUTTERFLY_LEFT+2];

           items[iBUTTERFLY_STAND]=items[iBUTTERFLY_LEFT];
           items[iBUTTERFLY_STAND+1]=items[iBUTTERFLY_LEFT+1];
           items[iBUTTERFLY_STAND+2]=items[iBUTTERFLY_LEFT+2];


           items[iFIREFLY_LEFT]=pnl.getImageForID(IMG_BUG_L0);
           items[iFIREFLY_LEFT+1]=pnl.getImageForID(IMG_BUG_L1);
           items[iFIREFLY_LEFT+2]=pnl.getImageForID(IMG_BUG_L2);

           items[iFIREFLY_RIGHT]=pnl.getImageForID(IMG_BUG_R0);
           items[iFIREFLY_RIGHT+1]=pnl.getImageForID(IMG_BUG_R1);
           items[iFIREFLY_RIGHT+2]=pnl.getImageForID(IMG_BUG_R2);

           items[iFIREFLY_UP]=pnl.getImageForID(IMG_BUG_U0);
           items[iFIREFLY_UP+1]=pnl.getImageForID(IMG_BUG_U1);
           items[iFIREFLY_UP+2]=pnl.getImageForID(IMG_BUG_U2);

           items[iFIREFLY_DOWN]=pnl.getImageForID(IMG_BUG_D0);
           items[iFIREFLY_DOWN+1]=pnl.getImageForID(IMG_BUG_D1);
           items[iFIREFLY_DOWN+2]=pnl.getImageForID(IMG_BUG_D2);

           items[iFIREFLY_STAND]=pnl.getImageForID(IMG_BUG_U0);
           items[iFIREFLY_STAND+1]=pnl.getImageForID(IMG_BUG_U1);
           items[iFIREFLY_STAND+2]=pnl.getImageForID(IMG_BUG_U2);

           items[iELEMENT_EXPLOSIVE]=pnl.getImageForID(IMG_EXPL0);
           items[iELEMENT_EXPLOSIVE+1]=pnl.getImageForID(IMG_EXPL1);
           items[iELEMENT_EXPLOSIVE+2]=pnl.getImageForID(IMG_EXPL2);

           items[iELEMENT_GROWCRYSTAL]=pnl.getImageForID(IMG_GROW0);
           items[iELEMENT_GROWCRYSTAL+1]=pnl.getImageForID(IMG_GROW1);
           items[iELEMENT_GROWCRYSTAL+2]=pnl.getImageForID(IMG_GROW2);

	   pnl = null;
*/
     } catch(Exception e) {
     }

     Runtime.getRuntime().gc();
//     stage = Labyrinths.indexes[1];
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
       direct=Pitman_SB.MOVE_NONE;
       Runtime.getRuntime().gc();
//       System.out.println(Runtime.getRuntime().freeMemory());

       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Pitman_SB.LEVEL0);
	    client_sb.initStage(demoLevel);
            client_sb.nextGameStep();
	    DrawBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    demoPosition = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
	   stageSleepTime[playGame] = ((Pitman_GSR)client_sb.getPitman_GSR()).getTimeDelay();
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
       direct = Pitman_SB.MOVE_NONE;
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
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Pitman_SB.MOVE_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=Pitman_SB.MOVE_LEFT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=Pitman_SB.MOVE_UP; break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=Pitman_SB.MOVE_DOWN; break;
				   default:
				      switch(action){
				        case Canvas.RIGHT/*RIGHT*/: direct=Pitman_SB.MOVE_RIGHT; break;
	                                case Canvas.LEFT/*LEFT*/: direct=Pitman_SB.MOVE_LEFT; break;
				        case Canvas.UP/*UP*/: direct=Pitman_SB.MOVE_UP; break;
				        case Canvas.DOWN/*DOWN*/: direct=Pitman_SB.MOVE_DOWN; break;
				           default:direct=Pitman_SB.MOVE_NONE;
				      }
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Pitman_SB.MOVE_NONE;
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
			   //Runtime.getRuntime().gc();
	                }
			if (ticks<80) ticks++;//System.out.println("AAA"+Thread.activeCount());}
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   if (ticks%MovingCreature.ANIMATION_FRAMES==0) {
	                       if (demoPosition >= Labyrinths.DEMO_STEP.length)demoPosition=0;
	                       direct = Labyrinths.DEMO_STEP[demoPosition];
	                       demoPosition++;
			   }
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep();

			   Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();
			   if (loc.getPlayerState()!=Pitman_GSR.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks==0) {
		                direct=Pitman_SB.MOVE_NONE;
				client_sb.initStage(stage);
                                client_sb.nextGameStep();
//                                stageSleepTime[playGame] = ((Pitman_GSR)client_sb.getPitman_GSR()).getTimeDelay();
//				System.gc();
	                      }
			      else
	                      if (ticks==1){
				DrawBackScreen();
				Runtime.getRuntime().gc();
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
			   Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();
                           //ticks++;
			   //if((ticks&0xff)==50) System.gc();
			   if (loc.getPlayerState()!=Pitman_GSR.PLAYERSTATE_NORMAL) {
			     ticks = 0;
		             direct=Pitman_SB.MOVE_NONE;
			     gameStatus = Crashed;
			   }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();
			   if (loc.getPlayerState() != Pitman_GSR.PLAYERSTATE_LOST)
			      client_sb.nextGameStep();

			   if(ticks>MovingCreature.ANIMATION_FRAMES){
			     ticks = 0;
			     blink = false;
		             direct=Pitman_SB.MOVE_NONE;
			         switch (loc.getPlayerState()) {
					case Pitman_GSR.PLAYERSTATE_LOST: gameStatus=gameOver; break;
			                case Pitman_GSR.PLAYERSTATE_WON:
								       stage++;
					                               if (stage>=Labyrinths.LAB_NUMBER)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;  break;
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
			      midlet.newScore(((Pitman_GSR)client_sb.getPitman_GSR()).getPlayerScores());
			   repaint();
			   break;

 	 }

        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {

//           thread.sleep(stageSleepTime[gameStatus]);
/*
          if(gameStatus==playGame )
	    while(System.currentTimeMillis()-sleepy<workDelay-10)
	     try{
	       thread.yield();
	     } catch (Exception e){}
          else*/
//	  if(Thread.currentThread()!=thread) WWW="other threed going sleep";
          if(workDelay>0)
             thread.sleep(workDelay);
          else
             thread.sleep(10);
	} catch (Exception e) {}

//	WWW = ""+workDelay+","+(System.currentTimeMillis()-sleepy);

	workDelay = System.currentTimeMillis();


      }
    }




    private void AlignPlayer(){

        Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();

        int hx = loc.getPlayer().getX();
	int hy = loc.getPlayer().getY();
	scrX = 0;
	scrY = 0;

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

	if(scrwidth-scrX>bsWIDTH)scrX=scrwidth-bsWIDTH;
	if(scrheight-scrY>bsHEIGHT)scrY=scrheight-bsHEIGHT;;
	if(scrX>0)scrX=0;
	if(scrY>0)scrY=0;

    //     System.out.println("BSH:"+bsHEIGHT+", hy:"+hy+", SCRH:"+scrheight+", Y:"+scrY);
    }

    private void DrawBackScreen(){

        gBackScreen.setColor(0xffffff);
        gBackScreen.fillRect(0, 0, bsWIDTH, bsHEIGHT);

        Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();

        AlignPlayer();


        Image img = null;
	int ldx = 0,ldy = 0, ele = 0;
        for(int lx=0;lx<Labyrinths.LAB_WIDTH;lx++){
	    ldy = 0;
            for(int ly=0;ly<Labyrinths.LAB_HEIGHT;ly++)
            {
	      ele = getItemNum(loc.getElementAt(lx,ly));
//	      shadowLabyrinth[lx][ly] = (byte)ele;
		if (ele!=0)
                   gBackScreen.drawImage(items[ele], ldx,ldy,0);
                ldy += CELL_HEIGHT;
            }
	    ldx += CELL_WIDTH;

        }
	System.gc();
    }


    private int getItemNum(int bb){
                switch(bb)
                {
                   case Labyrinths.ELEMENT_TITANIUMWALL: return IMG_TITAN;
                   case Labyrinths.ELEMENT_DIRT : return IMG_DIRT;
                   case Labyrinths.ELEMENT_STONE: return IMG_STONE;
                   case Labyrinths.ELEMENT_DIAMOND : return IMG_DIAMOND;
                }
	    return 0;
    }

   private Image getCreature(MovingCreature creature){
       if(creature.getX()+scrX<scrwidth && creature.getY()+scrY<scrheight &&
          (creature.getX()+CELL_WIDTH)>scrX && (creature.getY()+CELL_HEIGHT)>scrY)
	  {
	    int index = 999;
	    switch (creature.getType()){
	      case MovingCreature.CREATURE_PLAYER    : index = IMG_PL_LFT0+creature.getState()*MovingCreature.ANIMATION_FRAMES+creature.getFrame(); break;
	      case MovingCreature.CREATURE_BUTTERFLY : index = IMG_BAT0+creature.getFrame(); break;
	      case MovingCreature.CREATURE_FIREFLY   : index = IMG_BUG_L0+creature.getState()*MovingCreature.ANIMATION_FRAMES+creature.getFrame(); break;
	      case MovingCreature.ELEMENT_EXPLOSIVE  : index = IMG_EXPL0+creature.getFrame(); break;
	      case MovingCreature.ELEMENT_GROWCRYSTAL: index = IMG_GROW0+creature.getFrame(); break;
	    }
	    if( index < items.length) return items[index];
	  }
	return null;//items[iEMPTY];
    }




    protected void DoubleBuffer(Graphics gBuffer){

//long time = System.currentTimeMillis();
//        gBuffer.setColor(0xffffff);
//        gBuffer.fillRect(0, 0, scrwidth, scrheight);

//      if (bufferChanged) {


        Pitman_GSR loc=(Pitman_GSR)client_sb.getPitman_GSR();
        Image img = null;

	MovingCreature pl = loc.getPlayer();


        int hx = pl.getX();
	int hy = pl.getY();
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

        if((img = getCreature(pl))!=null){
	      gBackScreen.fillRect(pl.getX(),pl.getY(),CELL_WIDTH,CELL_HEIGHT);
//	      gBackScreen.drawImage(img,pl.getX(),pl.getY(),0);
        }

//*
	MovingElement []me = client_sb.getStonesArray();
//	MovingElement tmp;
	int ele = 0, l = client_sb.getMovingStoneNumber();

//	if(l>0)System.out.println("client_sb.getMovingStoneNumber():"+l);
//if(l>0) WWW = ""+l+":"+me[0].getType();

        for(int i = 0;i<l;i++)
        {
	       gBackScreen.fillRect(me[i].getPrevX()*CELL_WIDTH,me[i].getPrevY()*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
	       if((ele = getItemNum(me[i].getType()))!=0)
	          gBackScreen.drawImage(items[ele],me[i].getCurX()*CELL_WIDTH,me[i].getCurY()*CELL_HEIGHT,0);
        }
/*

     int k, n = client_sb.getMovingStoneNumber();
      if(n>0)
        for(int i=0, j=0;i<Labyrinths.LAB_WIDTH; i+=(++j<Labyrinths.LAB_HEIGHT)?0:1+(j=0))
	//   for(int j=0;j<Labyrinths.LAB_HEIGHT; j++)
	{
	    if((k=getItemNum(loc.getElementAt(i,j)))!=shadowLabyrinth[i][j])
	    {
	      shadowLabyrinth[i][j] = (byte)k;
	      if (k==0)gBackScreen.fillRect(i*CELL_WIDTH,j*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
	      else gBackScreen.drawImage(items[k],i*CELL_WIDTH,j*CELL_HEIGHT,0);
	      //if(n--==0) break;
	    }
	   }
//*/

        MovingCreature [] _crt = loc.getCreaturesArray();
/*
        for(int li=0;li<_crt.length;li++)
        {
	   if(_crt[li].getType()!=MovingCreature.CREATURE_INACTIVE)
	    gBackScreen.fillRect(_crt[li].getCellX()*CELL_WIDTH,_crt[li].getCellY()*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);
        }
*/

	gBuffer.drawImage(BackScreen,scrX,scrY,0);

        for(int li=0;li<_crt.length;li++)
        {
	    if((img = getCreature(_crt[li]))!=null)
//	      if(_crt[li].getType()!=MovingCreature.CREATURE_PLAYER)
	        gBuffer.drawImage(img,scrX+_crt[li].getX(),scrY+_crt[li].getY(),0);
        }




/*
        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<loc.getAttemptions()-1;i++){
           gBuffer.drawImage(Heart,baseX+scrwidth-(i+1)*(Heart.getWidth()+2)-10,baseY+scrheight-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
*/
	if (gameStatus == demoPlay && blink) {
	    gBuffer.drawImage(DemoTitle,scrwidth>>1,scrheight-DemoTitle.getHeight()-5,Graphics.HCENTER|Graphics.VCENTER);
	}
//        gBuffer.setColor(0x0);
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
		          Graphics gBuffer = g;
	                  gBuffer.setColor(0xffffff);gBuffer.fillRect(0, 0, scrwidth,scrheight);
		          gBuffer.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  gBuffer.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString("Loading ...["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1)-14,Graphics.TOP|Graphics.HCENTER);
	                  gBuffer.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  gBuffer.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  gBuffer.setFont(f);
//		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);

	                  } break;
              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  int x = scrwidth-Title.getWidth();
	                  int y = (scrheight-Title.getHeight())>>1;
			  if(x>0)x>>=1;
			  g.drawImage(Title,x,y,0);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
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
				g.drawString(YourScore+client_sb.getPitman_GSR().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :  // if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			 //     }
			 //     else {
//		                DoubleBuffer(gBuffer);
//		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
//                              g.drawString(WWW,0,scrheight-1, Graphics.BOTTOM|Graphics.LEFT);

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed || gameStatus == newStage);
    }



    public void gameLoaded(){
       Pitman_GSR loc = (Pitman_GSR)client_sb.getPitman_GSR();
       blink=false;
       ticks = 1;
       direct=Pitman_SB.MOVE_NONE;
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.getTimeDelay();
       stage = loc.getStage();
       gameStatus=newStage;

       bufferChanged=true;
       repaint();
    }
private static final int IMG_WIN = 2;
private static final int IMG_PITMAN = 0;
private static final int IMG_LOST = 1;
private static final int IMG_DEMO = 3;
private static final int IMG_PL_UP1 = 11;
private static final int IMG_PL_LFT0 = 4;
private static final int IMG_PL_UP0 = 10;
private static final int IMG_BUG_R0 = 25;
private static final int IMG_BUG_D0 = 31;
private static final int IMG_PL_LFT2 = 6;
private static final int IMG_PL_RGHT0 = 7;
private static final int IMG_PL_RGHT2 = 9;
private static final int IMG_PL_DWN0 = 13;
private static final int IMG_PL_DWN1 = 14;
private static final int IMG_PL_DWN2 = 15;
private static final int IMG_PL_STND0 = 16;
private static final int IMG_PL_STND1 = 17;
private static final int IMG_PL_STND2 = 18;
private static final int IMG_BUG_U0 = 28;
private static final int IMG_BUG_L0 = 22;
private static final int IMG_BUG_R1 = 26;
private static final int IMG_PL_LFT1 = 5;
private static final int IMG_PL_RGHT1 = 8;
private static final int IMG_PL_UP2 = 12;
private static final int IMG_BUG_L1 = 23;
private static final int IMG_BUG_U1 = 29;
private static final int IMG_BUG_D1 = 32;
private static final int IMG_DIAMOND = 37;
private static final int IMG_BUG_L2 = 24;
private static final int IMG_BUG_R2 = 27;
private static final int IMG_BUG_U2 = 30;
private static final int IMG_BUG_D2 = 33;
private static final int IMG_STONE = 36;
private static final int IMG_BAT0 = 19;
private static final int IMG_BAT1 = 20;
private static final int IMG_BAT2 = 21;
private static final int IMG_TITAN = 34;
private static final int IMG_GROW2 = 43;
private static final int IMG_DIRT = 35;
private static final int IMG_EXPL1 = 39;
private static final int IMG_GROW1 = 42;
private static final int IMG_EXPL0 = 38;
private static final int IMG_EXPL2 = 40;
private static final int IMG_GROW0 = 41;
private static final int TOTAL_IMAGES_NUMBER = 44;

}

/*
.
PITMAN,0
LOST,1
WIN,2
DEMO,3

PL_LFT0,4
PL_LFT1,5
PL_LFT2,6

PL_RGHT0,7
PL_RGHT1,8
PL_RGHT2,9

PL_UP0,10
PL_UP1,11
PL_UP2,12

PL_DWN0,13
PL_DWN1,14
PL_DWN2,15

PL_STND0,16
PL_STND1,17
PL_STND2,18

BAT0,19
BAT1,20
BAT2,21

BUG_L0,22
BUG_L1,23
BUG_L2,24
BUG_R0,25
BUG_R1,26
BUG_R2,27
BUG_U0,28
BUG_U1,29
BUG_U2,30
BUG_D0,31
BUG_D1,32
BUG_D2,33

TITAN,34
DIRT,35
STONE,36
DIAMOND,37

EXPL0,38
EXPL1,39
EXPL2,40

GROW0,41
GROW1,42
GROW2,43


.
.
*/
