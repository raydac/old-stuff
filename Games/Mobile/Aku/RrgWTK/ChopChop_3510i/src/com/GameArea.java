package com;
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Karate.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

//import java.io.*;
//import com.siemens.mp.game.Melody;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private final static int BAR_WIDTH = 20;
  private final static int BAR_HEIGHT = 5;
  /**
   *  технологические переменные
   */
  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  private Thread thread=null;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
//  private Image Stages[]=new Image[3];
  private Image DemoTitle=null;
//  private Image Buffer=null;
//  private Graphics gBuffer=null;
  private Image Ico=null;

  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime

  private final int [][][]_o_stay = {{{9,0},{9,0},{9,0}},{{14,0},{12,0},{11,0}}};
  private final int [][][]_o_move = {{{9,0},{9,0},{9,0},{9,0},{9,0}},{{14,0},{15,0},{14,0},{15,0},{14,0}}};
  private final int [][][]_o_hb = {{{9,0},{9,0},{9,0},{9,0},{9,0}},{{14,0},{12,0},{11,0},{13,0},{12,0}}};
  private final int [][][]_o_mb = {{{9,0},{9,0},{9,0},{9,0},{9,0},{9,0}},{{14,0},{12,0},{15,0},{15,0},{12,0},{12,0}}};
  private final int [][][]_o_lb = {{{9,0},{9,0},{9,0},{9,0},{9,0},{9,0}},{{14,0},{12,0},{15,0},{15,0},{12,0},{12,0}}};

  private final int [][][]_o_hp = {{{9,0},{0,0},{0,0},{0,0},{0,0}},{{14,0},{26,0},{18,0},{17,0},{14,0}}};
  private final int [][][]_o_mp = {{{9,0},{0,0},{0,0},{0,0},{0,0}},{{14,0},{26,0},{18,0},{17,0},{12,0}}};
  private final int [][][]_o_lp = {{{9,0},{0,0},{0,0},{0,0}},{{14,0},{26,0},{18,0},{13,0}}};

  private final int [][][]_o_die = {{{0,0},{0,6},{0,21}},{{20,0},{12,8},{2,21}}};

  private final static int LP = 0, RP = 1;
/*
    Image[][] _stand = new Image[2][Karateker.ANIMATION_STAND_FRAMES];
    Image[][] _moving = new Image[2][Karateker.ANIMATION_MOVELEFT_FRAMES];
    Image[][] _hiblock = new Image[2][Karateker.ANIMATION_BLOCKTOP_FRAMES];
    Image[][] _midblock = new Image[2][Karateker.ANIMATION_BLOCKMID_FRAMES];
    Image[][] _lowblock = new Image[2][Karateker.ANIMATION_BLOCKDOWN_FRAMES];
    Image[][] _hipunch = new Image[2][Karateker.ANIMATION_PUNCHTOP_FRAMES];
    Image[][] _midpunch = new Image[2][Karateker.ANIMATION_PUNCHMID_FRAMES];
    Image[][] _lowpunch = new Image[2][Karateker.ANIMATION_PUNCHDOWN_FRAMES];
    Image[][] _died = new Image[2][Karateker.ANIMATION_DIED_FRAMES];
*/
   Image []items;

  private int CurrentLevel=0;
  protected Karate_PMR client_pmr = null;
  protected int direct=0;//Karate_PMR.BUTTON_NONE;
  protected Karate_SB client_sb = null;//new Karate_SB();
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
			    newStage = 3,
                            playGame = 4,
			    Finished = 5,
			    gameOver = 6;
  private int [] stageSleepTime = {100,100,100,200,60,500,500}; // in ms

  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  private boolean playMelody = true;
  public boolean MelodyStateOn = true;
  /*
  private static final byte []notesIntro = new byte[] {18,4,58,5,
                                                       18,4,58,5,
						       18,4,58,5,
						       14,5,58,6,
						       14,5,58,6,
						       14,5,58,6,
						       61,2};
*/
  private boolean isShown = false;
  private Image MenuIcon = null;
  private int LoadingTotal =0, LoadingNow = 0, ticks=0;
  public boolean LanguageCallBack = false, keyReverse = false;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private long ldistance = 0;
  private int stage = 0;


    public void nextItemLoaded(int nnn){
//      System.out.println("total free:"+Runtime.getRuntime().freeMemory()+", n="+(LoadingNow+nnn));

       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       if((System.currentTimeMillis()-ldistance)>50)  {
	 repaint();
	 ldistance=System.currentTimeMillis();
       }
    }

    public void start(int kkk) {
     if (thread==null){
         thread=new Thread(this);
	 thread.start();
     }
     loading(TOTAL_IMAGES_NUMBER+67);

     try {

	    Runtime.getRuntime().gc();
	    items = (new ImageBlock(this))._image_array;

            MenuIcon = items[TOTAL_IMAGES_NUMBER];
            Title = items[IMG_CC];
	    Ico = items[IMG_ICO];
            Lost=items[IMG_LOST];//Image.createImage(TitleImage];
            Won=items[IMG_WON];//Image.createImage(TitleImage];
            DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage];
/*
// left personage

	   _stand[LP][0] = a[IMG_L_CPBSM1];
	   _stand[LP][1] = a[IMG_L_ST2];
	   _stand[LP][2] = a[IMG_L_ST3];

	   _moving[LP][0] = _stand[LP][0];
	   _moving[LP][1] = a[IMG_L_MM2];
	   _moving[LP][2] = a[IMG_L_MM3];
	   _moving[LP][3] = a[IMG_L_MM4];
	   _moving[LP][4] = a[IMG_L_MM5];

	   _died[LP][0] = a[IMG_L_DD1];
	   _died[LP][1] = a[IMG_L_DD2];
	   _died[LP][2] = a[IMG_L_DD3];


// blocks

	   _hiblock[LP][0] = _stand[LP][0];
	   _hiblock[LP][1] = a[IMG_L_CB2];
	   _hiblock[LP][2] = a[IMG_L_HB3];
	   _hiblock[LP][3] = a[IMG_L_HB4];
	   _hiblock[LP][4] = a[IMG_L_HB5];

	   _midblock[LP][0] = _hiblock[LP][0];
	   _midblock[LP][1] = _hiblock[LP][1];
	   _midblock[LP][2] = a[IMG_L_LB_MB3];
	   _midblock[LP][3] = a[IMG_L_LB_MB4];
	   _midblock[LP][4] = a[IMG_L_LB_MB5];
	   _midblock[LP][5] = a[IMG_L_MB6];

	   _lowblock[LP][0] = _hiblock[LP][0];
	   _lowblock[LP][1] = _hiblock[LP][1];
	   _lowblock[LP][2] = a[IMG_L_LB_MB3];
	   _lowblock[LP][3] = a[IMG_L_LB_MB4];
	   _lowblock[LP][4] = a[IMG_L_LB_MB5];
	   _lowblock[LP][5] = a[IMG_L_LB6];
// punches

	   _hipunch[LP][0] = _stand[LP][0];
	   _hipunch[LP][1] = a[IMG_L_CP2];
	   _hipunch[LP][2] = a[IMG_L_CP3];
	   _hipunch[LP][3] = a[IMG_L_HP4];
	   _hipunch[LP][4] = a[IMG_L_HP5];

	   _midpunch[LP][0] = _hipunch[LP][0];
	   _midpunch[LP][1] = _hipunch[LP][1];
	   _midpunch[LP][2] = _hipunch[LP][2];
	   _midpunch[LP][3] = a[IMG_L_MP4];
	   _midpunch[LP][4] = a[IMG_L_MP5];

	   _lowpunch[LP][0] = _hipunch[LP][0];
	   _lowpunch[LP][1] = _hipunch[LP][1];
	   _lowpunch[LP][2] = _hipunch[LP][2];
	   _lowpunch[LP][3] = a[IMG_L_LP4];

// opponent

	   _stand[RP][0] = a[IMG_R_CPBMS1];
	   _stand[RP][1] = a[IMG_R_ST2];
	   _stand[RP][2] = a[IMG_R_ST3];

	   _moving[RP][0] = _stand[RP][0];
	   _moving[RP][1] = a[IMG_R_MM2];
	   _moving[RP][2] = a[IMG_R_MM3];
	   _moving[RP][3] = a[IMG_R_MM4];
	   _moving[RP][4] = a[IMG_R_MM5];

	   _died[RP][0] = a[IMG_R_DD1];
	   _died[RP][1] = a[IMG_R_DD2];
	   _died[RP][2] = a[IMG_R_DD3];

// blocks

	   _hiblock[RP][0] = _stand[RP][0];
	   _hiblock[RP][1] = a[IMG_R_CB2];
	   _hiblock[RP][2] = a[IMG_R_HB3];
	   _hiblock[RP][3] = a[IMG_R_HB4];
	   _hiblock[RP][4] = a[IMG_R_HB5];

	   _midblock[RP][0] = _stand[RP][0];
	   _midblock[RP][1] = a[IMG_R_CB2];
	   _midblock[RP][2] = a[IMG_R_LB_MB3];
	   _midblock[RP][3] = a[IMG_R_LB_MB4];
	   _midblock[RP][4] = a[IMG_R_LB_MB5];
	   _midblock[RP][5] = a[IMG_R_MB6];

	   _lowblock[RP][0] = _stand[RP][0];
	   _lowblock[RP][1] = a[IMG_R_CB2];
	   _lowblock[RP][2] = a[IMG_R_LB_MB3];
	   _lowblock[RP][3] = a[IMG_R_LB_MB4];
	   _lowblock[RP][4] = a[IMG_R_LB_MB5];
	   _lowblock[RP][5] = a[IMG_R_LB6];

// punches

	   _hipunch[RP][0] = _stand[RP][0];
	   _hipunch[RP][1] = a[IMG_R_CP2];
	   _hipunch[RP][2] = a[IMG_R_HP3];
	   _hipunch[RP][3] = a[IMG_R_HP4];
	   _hipunch[RP][4] = a[IMG_R_HP5];

	   _midpunch[RP][0] = _stand[RP][0];
	   _midpunch[RP][1] = _hipunch[RP][1];
	   _midpunch[RP][2] = a[IMG_R_MP3];
	   _midpunch[RP][3] = a[IMG_R_MP4];
	   _midpunch[RP][4] = a[IMG_R_MP5];

	   _lowpunch[RP][0] = _stand[RP][0];
	   _lowpunch[RP][1] = _hipunch[RP][1];
	   _lowpunch[RP][2] = a[IMG_R_LP3];
	   _lowpunch[RP][3] = a[IMG_R_LP4];

           Stages[0]=a[IMG_STG1];
           Stages[1]=a[IMG_STG2];
           Stages[2]=a[IMG_STG3];
*/
	   Runtime.getRuntime().gc();


     } catch(Exception e) {
       //ErrMSG = ""+e.getMessage();
//       e.printStackTrace();
     }


           client_sb = new Karate_SB();
	   client_sb.setPlayerBlock(this);

//          Buffer=Image.createImage(scrwidth,scrheight);
//           gBuffer=Buffer.getGraphics();

     Runtime.getRuntime().gc();

    }

    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
       ldistance = System.currentTimeMillis();
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
    gameStatus=notInitialized;
  }

///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

//     if (gameStatus != demoPlay)
         client_pmr.setButton(direct);
	 return  client_pmr;
    }

/*
    public void playMelody(byte[] m, int bpm){
      if (m!=null && m.length>0 && (m.length&1)==0)    // not null, not empty , and had parity
        try
        {
/*
            MelodyComposer P = new MelodyComposer();
            P.setBPM(bpm);
	    for(int i=0;i<m.length;i++)
                 P.appendNote(m[i++],m[i]);
            Melody melody = P.getMelody();
            melody.play();
* /
        }
        catch(Exception exception)
        {
            //System.out.println("Melody stuff is broke");
        }
    }
*/

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Karate_PMR();

    }

    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       direct=Karate_PMR.BUTTON_NONE;
       CurrentLevel=level;
       baseX=0;//((scrwidth-levelImage.getWidth())>>1);
       baseY=0;//((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGame(0);
	    client_sb.initStage(1);
            stage=1;
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
           stage=0;
	   client_sb.newGame(CurrentLevel);
	   gameStatus=newStage;
	}
	bufferChanged=true;
	repaint();
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
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true); else
	                       switch (keyCode) {
				   // atack
				      case Canvas.KEY_NUM3: direct=Karate_PMR.BUTTON_PUNCHTOP; break;
				      case Canvas.KEY_NUM6: direct=Karate_PMR.BUTTON_PUNCHMID; break;
				      case Canvas.KEY_NUM9: direct=Karate_PMR.BUTTON_PUNCHDOWN; break;
				   // defence
				      case Canvas.KEY_NUM1: direct=Karate_PMR.BUTTON_BLOCKTOP; break;
				      case Canvas.KEY_NUM4: direct=Karate_PMR.BUTTON_BLOCKMID; break;
				      case Canvas.KEY_NUM7: direct=Karate_PMR.BUTTON_BLOCKDOWN; break;
				   // move
				      case Canvas.KEY_POUND: direct=Karate_PMR.BUTTON_RIGHT; break;
				      case Canvas.KEY_STAR: direct=Karate_PMR.BUTTON_LEFT; break;
				default:direct=Karate_PMR.BUTTON_NONE;
	                       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=Karate_PMR.BUTTON_NONE;
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
//	                if (playMelody){
//			   playMelody(notesIntro,35);
//			   playMelody = false;
//	                }
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   System.gc();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   blink=(ticks&8)==0;
			   Karate_GSR loc=(Karate_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Karate_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       System.gc();
			   }

			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=Karate_PMR.BUTTON_NONE;
			        client_sb.initStage(stage);
				//client_sb.nextGameStep();
				//DrawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        direct=Karate_PMR.BUTTON_NONE;
			        gameStatus=playGame;
				//System.gc();
			        repaint();
	                      }
			    } break;
           case playGame :{
			   client_sb.nextGameStep();
			   Karate_GSR loc=(Karate_GSR)client_sb.getGameStateRecord();
			   //if (loc.getGameState()==Karate_GSR.GAMESTATE_OVER) {
			   //    ticks = 0;
			   //    blink = true;
			     switch (loc.getPlayerState()) {
			        case Karate_GSR.PLAYERSTATE_WON: if(stage>=2)gameStatus=Finished;
				                                 else {
								   stage++;
								   ticks=0;
								   gameStatus=newStage;
				                                 }break;
			        case Karate_GSR.PLAYERSTATE_LOST: gameStatus=gameOver; break;
			     }
			   repaint();
			  } break;
/*
	    case outOfAmmo:
	    case crashCar:
			   ticks++; blink=(ticks&2)==0;
			   if (ticks >= 10) {
			      client_sb.resumeGame();
		              direct=Karate_PMR.BUTTON_NONE;
			      gameStatus=playGame;
			      ticks = 0;
			      blink = false;
			   }
			   repaint();
			   break;
			   */
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


    private void drawCharacter(Graphics gBuffer,Karateker k, int n){

        int frame = k.getFrame(), x=0,y=0;
	Image img = null;
	int base = (n==0)?0:IMG_R_CPBMS1-IMG_L_CPBSM1;
     try {
        switch (k.getState()) {
            case Karateker.STATE_STAND:    img =items[base+ IMG_L_CPBSM1 +frame];
	                                   x=_o_stay[n][frame][0];
					   y=_o_stay[n][frame][1];
					   break;
            case Karateker.STATE_PUNCHTOP: img =items[base+ IMG__L5 +frame];
	                                   x=_o_hp[n][frame][0];
					   y=_o_hp[n][frame][1];
					   break;
            case Karateker.STATE_PUNCHMID: img =items[base+ IMG__L6 +frame];
	                                   x=_o_mp[n][frame][0];
					   y=_o_mp[n][frame][1];
					   break;
            case Karateker.STATE_PUNCHDOWN:img =items[base+ IMG__L7 +frame];
	                                   x=_o_lp[n][frame][0];
					   y=_o_lp[n][frame][1];
					   break;
            case Karateker.STATE_MOVERIGHT:
            case Karateker.STATE_MOVELEFT: img =items[base+ IMG__L1 +frame];
	                                   x=_o_move[n][frame][0];
					   y=_o_move[n][frame][1];
					   break;
            case Karateker.STATE_BLOCKTOP: img =items[base+ IMG__L2 +frame];
	                                   x=_o_hb[n][frame][0];
					   y=_o_hb[n][frame][1];
					   break;
            case Karateker.STATE_BLOCKMID: img =items[base+ IMG__L3 +frame];
	                                   x=_o_mb[n][frame][0];
					   y=_o_mb[n][frame][1];
					   break;
            case Karateker.STATE_BLOCKDOWN:img =items[base+ IMG__L4 +frame];
	                                   x=_o_lb[n][frame][0];
					   y=_o_lb[n][frame][1];
					   break;
            case Karateker.STATE_DIED:     img =items[base+ IMG_L_DD1 +frame];
	                                   x=_o_die[n][frame][0];
					   y=_o_die[n][frame][1];
					   break;
	    default: return;
        }
     } catch (Exception e){
         e.printStackTrace();
     }
	gBuffer.drawImage(img,k.getX()+x,k.getY()+y,0);

    }


    protected void DoubleBuffer(Graphics gBuffer){
//      if (bufferChanged) {

	gBuffer.drawImage(items[IMG_STG1+stage],baseX,baseY,0);

        Karate_GSR loc=(Karate_GSR)client_sb.getGameStateRecord();
        Karateker p = loc.getPlayerPerson();
        Karateker o = loc.getAIPerson();
	drawCharacter(gBuffer,p,LP);
	drawCharacter(gBuffer,o,RP);

	int x1 = ((scrwidth-Ico.getWidth())>>1)-3-BAR_WIDTH;
	int x2 = ((scrwidth+Ico.getWidth())>>1)+3;
	int dx1 = Math.max(p.getLifeStatus()*(BAR_WIDTH-3)/p.INIT_LIFE_VALUE,0);
	int dx2 = Math.max(o.getLifeStatus()*(BAR_WIDTH-3)/o.INIT_LIFE_VALUE,0);

        gBuffer.setColor(-1);
	gBuffer.fillRect(x1,0,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x2,0,BAR_WIDTH,BAR_HEIGHT);

        gBuffer.setColor(0x0);
	gBuffer.drawRect(x1,0,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.drawRect(x2,0,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x1+2,2,dx1,BAR_HEIGHT-3);
	gBuffer.fillRect(x2+BAR_WIDTH-dx2-1,2,dx2,BAR_HEIGHT-3);

	gBuffer.drawImage(Ico,(scrwidth-Ico.getWidth())>>1,0,0);

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

		          Graphics gBuffer = g;
	                  gBuffer.setColor(-1);gBuffer.fillRect(0, 0, scrwidth,scrheight);
		          gBuffer.setColor(0);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  gBuffer.drawString(loadString,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString("Loading ...["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1)-14,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString(ErrMSG ,scrwidth>>1, (scrheight>>1)+10,Graphics.TOP|Graphics.HCENTER);
	                  gBuffer.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  gBuffer.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  gBuffer.setFont(f);
		                //g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);

	                  } break;

              case titleStatus : g.setColor(-1);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                  g.setColor(-1);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
	                  } break;
	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(-1);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(-1);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default ://   if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			      }
//			      else {
//		                DoubleBuffer(gBuffer);
//		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT); break;
//			      }
			   g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       direct=Karate_PMR.BUTTON_NONE;
       //CurrentLevel=level;
       baseX=0;//((scrwidth-levelImage.getWidth())>>1);
       baseY=0;//((scrheight-levelImage.getHeight())>>1);
       ticks=1;
       stage = client_sb.getGameStateRecord().getStage();
       gameStatus=newStage;
       bufferChanged=true;
       repaint();
    }

private final byte IMG_CC = (byte)0;
private final byte IMG_LOST = (byte)1;
private final byte IMG_WON = (byte)2;
private final byte IMG_DEMO = (byte)3;
private final byte IMG_STG1 = (byte)4;
private final byte IMG_STG2 = (byte)5;
private final byte IMG_STG3 = (byte)6;
private final byte IMG_ICO = (byte)7;
private final byte IMG_L_CPBSM1 = (byte)8;
private final byte IMG_L_ST2 = (byte)9;
private final byte IMG_L_ST3 = (byte)10;
private final byte IMG_L_MM2 = (byte)12;
private final byte IMG_L_MM3 = (byte)13;
private final byte IMG_L_MM4 = (byte)14;
private final byte IMG_L_MM5 = (byte)15;
private final byte IMG_L_DD1 = (byte)16;
private final byte IMG_L_DD2 = (byte)17;
private final byte IMG_L_DD3 = (byte)18;
private final byte IMG_L_CB2 = (byte)20;
private final byte IMG_L_HB3 = (byte)21;
private final byte IMG_L_HB4 = (byte)22;
private final byte IMG_L_HB5 = (byte)23;
private final byte IMG_L_LB_MB3 = (byte)26;
private final byte IMG_L_LB_MB4 = (byte)27;
private final byte IMG_L_LB_MB5 = (byte)28;
private final byte IMG_L_MB6 = (byte)29;
private final byte IMG_L_LB6 = (byte)35;
private final byte IMG_L_CP2 = (byte)37;
private final byte IMG_L_CP3 = (byte)38;
private final byte IMG_L_HP4 = (byte)39;
private final byte IMG_L_HP5 = (byte)40;
private final byte IMG_L_MP4 = (byte)44;
private final byte IMG_L_MP5 = (byte)45;
private final byte IMG_L_LP4 = (byte)49;
private final byte IMG_R_CPBMS1 = (byte)50;
private final byte IMG_R_ST2 = (byte)51;
private final byte IMG_R_ST3 = (byte)52;
private final byte IMG_R_MM2 = (byte)54;
private final byte IMG_R_MM3 = (byte)55;
private final byte IMG_R_MM4 = (byte)56;
private final byte IMG_R_MM5 = (byte)57;
private final byte IMG_R_DD1 = (byte)58;
private final byte IMG_R_DD2 = (byte)59;
private final byte IMG_R_DD3 = (byte)60;
private final byte IMG_R_CB2 = (byte)62;
private final byte IMG_R_HB3 = (byte)63;
private final byte IMG_R_HB4 = (byte)64;
private final byte IMG_R_HB5 = (byte)65;
private final byte IMG_R_LB_MB3 = (byte)68;
private final byte IMG_R_LB_MB4 = (byte)69;
private final byte IMG_R_LB_MB5 = (byte)70;
private final byte IMG_R_MB6 = (byte)71;
private final byte IMG_R_LB6 = (byte)77;
private final byte IMG_R_CP2 = (byte)79;
private final byte IMG_R_HP3 = (byte)80;
private final byte IMG_R_HP4 = (byte)81;
private final byte IMG_R_HP5 = (byte)82;
private final byte IMG_R_MP3 = (byte)85;
private final byte IMG_R_MP4 = (byte)86;
private final byte IMG_R_MP5 = (byte)87;
private final byte IMG_R_LP3 = (byte)90;
private final byte IMG_R_LP4 = (byte)91;
private final byte IMG__L1 = (byte)11;
private final byte IMG__L2 = (byte)19;
private final byte IMG__L3 = (byte)24;
private final byte IMG__LB1 = (byte)25;
private final byte IMG__L4 = (byte)30;
private final byte IMG__LB2 = (byte)31;
private final byte IMG__LMB1 = (byte)32;
private final byte IMG__LMB2 = (byte)33;
private final byte IMG__LMB3 = (byte)34;
private final byte IMG__L5 = (byte)36;
private final byte IMG__L6 = (byte)41;
private final byte IMG__LP1 = (byte)42;
private final byte IMG__LP2 = (byte)43;
private final byte IMG__L7 = (byte)46;
private final byte IMG__LP3 = (byte)47;
private final byte IMG__LP4 = (byte)48;
private final byte IMG__R1 = (byte)53;
private final byte IMG__R2 = (byte)61;
private final byte IMG__R3 = (byte)66;
private final byte IMG__RB1 = (byte)67;
private final byte IMG__R4 = (byte)72;
private final byte IMG__RB2 = (byte)73;
private final byte IMG__RLB1 = (byte)74;
private final byte IMG__RLB2 = (byte)75;
private final byte IMG__RLB3 = (byte)76;
private final byte IMG__R5 = (byte)78;
private final byte IMG__R6 = (byte)83;
private final byte IMG__RP1 = (byte)84;
private final byte IMG__R7 = (byte)88;
private final byte IMG__RP2 = (byte)89;
private final byte TOTAL_IMAGES_NUMBER = (byte)92;

}
/*
CC,0
LOST,1
WON,2
DEMO,3
STG1,4
STG2,5
STG3,6
ICO,7

L_CPBSM1,8
L_ST2,9
L_ST3,10

_L1,11, ,8
L_MM2,12
L_MM3,13
L_MM4,14
L_MM5,15

L_DD1,16
L_DD2,17
L_DD3,18

_L2,19, ,8
L_CB2,20
L_HB3,21
L_HB4,22
L_HB5,23

_L3,24, ,8
_LB1,25, ,20
L_LB_MB3,26
L_LB_MB4,27
L_LB_MB5,28
L_MB6,29

_L4,30, ,8
_LB2,31, ,20
_LMB1,32, ,26
_LMB2,33, ,27
_LMB3,34, ,28
L_LB6,35


_L5,36, ,8
L_CP2,37
L_CP3,38
L_HP4,39
L_HP5,40

_L6,41, ,8
_LP1,42, ,37
_LP2,43, ,38
L_MP4,44
L_MP5,45

_L7,46, ,8
_LP3,47, ,37
_LP4,48, ,38
L_LP4,49





R_CPBMS1,50
R_ST2,51
R_ST3,52

_R1,53, ,50
R_MM2,54
R_MM3,55
R_MM4,56
R_MM5,57

R_DD1,58
R_DD2,59
R_DD3,60


_R2,61, ,50
R_CB2,62
R_HB3,63
R_HB4,64
R_HB5,65

_R3,66, ,50
_RB1,67, ,62
R_LB_MB3,68
R_LB_MB4,69
R_LB_MB5,70
R_MB6,71

_R4,72, ,50
_RB2,73, ,62
_RLB1,74, ,68
_RLB2,75, ,69
_RLB3,76, ,70
R_LB6,77

_R5,78, ,50
R_CP2,79
R_HP3,80
R_HP4,81
R_HP5,82

_R6,83, ,50
_RP1,84, ,79
R_MP3,85
R_MP4,86
R_MP5,87

_R7,88, ,50
_RP2,89, ,79
R_LP3,90
R_LP4,91
*/
