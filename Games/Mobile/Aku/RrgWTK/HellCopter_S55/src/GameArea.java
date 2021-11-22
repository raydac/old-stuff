import com.igormaznitsa.game_kit_E5D3F2.Bomber.*;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Melody;
//import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

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
/*
    public void activateVibrator(long duration){
        Vibrator.triggerVibrator((int) duration);
    }
*/

  private Melody []sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i]!=null)
	    try{
               sounds[i].stop();
	    }catch(Exception e){}
      isFirst = false;
    }

    public void PlayMelody(int n){
//      stopAllMelodies();
      if(midlet._Sound && sounds!=null && sounds[n]!=null){
          sounds[n].play();
      }
    }



    public boolean soundIsPlaying(){
//      if(sounds!=null)
//        for(int i = 0;i<sounds.length; i++)
//           if(sounds[i].getState()==Sound.SOUND_PLAYING)
//               return true;
      return false;
    }

/*=============================*/

  private final static int BACKGROUND_COLOR = 0x212190;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

  private Image [] Elements;
  private byte [][] ByteArray;

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
  private Image BackScreen=null;
  private Graphics gBuffer=null;
  private Graphics gBackScreen=null;

  private Image MenuIcon = null;
  private drawDigits font;


  private final static int STAGE_WIDTH = Stages.STAGE_WIDTH * Bomber_SB.VIRTUAL_CELL_WIDTH;
  private final static int STAGE_HEIGHT = Stages.STAGE_HEIGHT * Bomber_SB.VIRTUAL_CELL_HEIGHT;
/*
  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image iBomb = null;
  private Image iBombIcon = null;
  private Image iBombReady = null;
  private Image iHeart = null;
  private Image []iPlayer = new Image[5/*PLAYER STATES* /*Bomber_SB.PLAYER_ANIMATION_FRAMES];
  private Image []iElements = new Image[1];
  private Image []iExplosion = new Image[1/*Exploded Object* /*AnimationObject.ANIMATION_FRAMES];
*/

  public int TitleN = 0;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected Bomber_PMR client_pmr = null;
  protected int direct=Bomber_PMR.BUTTON_NONE;
  protected Bomber_SB client_sb = null;
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

    client_sb = new Bomber_SB(VisWidth, VisHeight, this);
    client_pmr = new Bomber_PMR();

    if (!this.isDoubleBuffered())
    {
//      System.out.println("not dbufferred");
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }
    BackScreen=Image.createImage(Math.max(VisWidth,STAGE_WIDTH),
                                 Math.max(VisHeight,STAGE_HEIGHT));
    gBackScreen=BackScreen.getGraphics();

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
	   font = new drawDigits(GetImage(IMG_NFONT));
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
        client_pmr = new Bomber_PMR();

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
       direct=Bomber_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_pmr.i_value=direct;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
//	    lastPlayerScores = 0;
	} else {
           stageSleepTime[playGame] = client_sb.getTimeDelay();
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
/*
			       int action = getGameAction(keyCode);
	                          switch (keyCode) {
				      case Canvas.KEY_NUM6: direct=Bomber_PMR.BUTTON_RIGHT; break;
	                              case Canvas.KEY_NUM4: direct=Bomber_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM5: direct=Bomber_PMR.BUTTON_UPTHROW; break;
				   default:direct=Bomber_PMR.BUTTON_NONE;
	                          }
*/
			       switch(keyCode) {
			           case MENU_KEY: midlet.ShowGameMenu(); break;
				   case END_KEY: midlet.ShowQuitMenu(true); break;
				   default: direct=Bomber_PMR.BUTTON_DROP_BOMB;
			       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Bomber_PMR.BUTTON_NONE;
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
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();
			   Bomber_GSB loc=(Bomber_GSB)client_sb.getGameStateBlock();
			   if (loc.getPlayerState()!=Bomber_GSB.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=Bomber_PMR.BUTTON_NONE;
			        client_pmr.i_value=direct;
			        client_sb.initStage(stage);
				client_sb.nextGameStep(client_pmr);
				DrawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++; blink=(ticks&4)==0;
			   client_pmr.i_value=direct;
	                   //if(ticks++>100){Runtime.getRuntime().gc();ticks=0;}
			   client_sb.nextGameStep(client_pmr);

			   //UpdateBackScreen();
			   Bomber_GSB loc=(Bomber_GSB)client_sb.getGameStateBlock();
/*
			   if (loc.getGameState()==Bomber_GSB.GAMESTATE_OVER) {
			       switch (loc.getPlayerState()) {
				      case Bomber_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Bomber_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case Bomber_GSB.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Bomber_GSB.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Stages.TOTAL_STAGES)
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
		             direct=Bomber_PMR.BUTTON_NONE;
			     if (((Bomber_GSB)client_sb.getGameStateBlock()).getGameState()==Bomber_GSB.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGameAfterPlayerLost();
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
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
			      midlet.newScore(((GameStateBlock)client_sb.getGameStateBlock()).getPlayerScore());
			   repaint();
			   break;
 	 }

        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

//          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
//	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
//          else
          if(workDelay<=0) workDelay=10;
             Thread.sleep(workDelay);

	} catch (Exception e) {}

	workDelay = System.currentTimeMillis();

      }
    }


    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case Bomber_SB.GAMEACTION_ERASEBLOCK:
		  {
		       gBackScreen.fillRect(x*Bomber_SB.VIRTUAL_CELL_WIDTH,y*Bomber_SB.VIRTUAL_CELL_HEIGHT,Bomber_SB.VIRTUAL_CELL_WIDTH,Bomber_SB.VIRTUAL_CELL_HEIGHT);
		  }
	 }
    }

    private Image getImageElement(int bb){
                switch(bb)
                {
                   case Stages.ELE_HOUSE : return Elements[IMG_HOUSE];
		   default: return null;
                }


    }

    private void DrawBackScreen(){

        gBackScreen.setColor(BACKGROUND_COLOR);
        gBackScreen.fillRect(0,0,BackScreen.getWidth(),BackScreen.getHeight());

        Bomber_GSB loc=(Bomber_GSB)client_sb.getGameStateBlock();

        Image img = null;
	int ldx = 0,ldy = 0;
        for(int lx=0;lx<Stages.STAGE_WIDTH;lx++){
	    ldy = 0;
            for(int ly=0;ly<Stages.STAGE_HEIGHT;ly++)
            {
		if ((img=getImageElement(loc.getElementAt(lx,ly)))!=null)
                        gBackScreen.drawImage(img, ldx,ldy,0);
                ldy += Bomber_SB.VIRTUAL_CELL_HEIGHT;
            }
	    ldx += Bomber_SB.VIRTUAL_CELL_WIDTH;

        }
//	System.gc();

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
        Image img = null;
	int li=0;
        AnimationObject ao;
	BombObject bo;
//      if (bufferChanged) {

	// prepare screen
        Bomber_GSB loc=(Bomber_GSB)client_sb.getGameStateBlock();

	int anchor = Math.max(0,VisHeight-STAGE_HEIGHT);
	if(anchor>0){
            gBuffer.setColor(BACKGROUND_COLOR);
            gBuffer.fillRect(0,0,VisWidth,anchor);
	}

	gBuffer.drawImage(BackScreen,0,anchor,0);

        for(li = 0;li<=client_sb.getLastAnimationObjectIndex();li++)
        {
            ao = client_sb.getExplodingAray()[li];
            if (ao.isActive())
            {
                gBuffer.drawImage(Elements[IMG_HOUSE_E0+ao.getFrameNumber()], ao.getScreenX(),ao.getScreenY()+anchor,0/*Graphics.TOP|Graphics.LEFT*/);
            }
        }

        // drawing bomb objects
        for(li = 0;li<=client_sb.getLastBombIndex();li++)
        {
            bo = client_sb.getBombArray()[li];
            if (bo.isActive())
            {
                gBuffer.drawImage(Elements[IMG_BOMB], bo.getScrX(),bo.getScrY()+anchor,0/*Graphics.TOP|Graphics.LEFT*/);
            }
        }


        if (!(((client_sb.getBombCounter()<5 && gameStatus == playGame) || (gameStatus == Crashed && client_sb.getBombCounter()<=0))&&blink)){
            gBuffer.drawImage(Elements[IMG_BOMBICO],0,2,0/*Graphics.TOP|Graphics.LEFT*/);
	    font.drawDigits(gBuffer,Elements[IMG_BOMBICO].getWidth()+1,((Elements[IMG_BOMBICO].getHeight()-font.fontHeight)>>1)+2,2,client_sb.getBombCounter());
        }
	if(client_sb.isBombPresent())
            gBuffer.drawImage(Elements[IMG_BOMB3],VisWidth>>1,2,Graphics.TOP|Graphics.HCENTER);


        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<client_sb.getAttemptions()-1;i++){
           gBuffer.drawImage(Elements[IMG_HEART],VisWidth-(i+1)*(Elements[IMG_HEART].getWidth()+2),2,0/*Graphics.TOP|Graphics.LEFT*/);
	 }


       li = IMG_LEFT0+client_sb.getPlayerAnimationState()*client_sb.PLAYER_ANIMATION_FRAMES+client_sb.getPlayerAnimationFrame();
       if(gameStatus != Crashed /*&& li<iPlayer.length*/){
	  gBuffer.drawImage(Elements[li],client_sb.getPlayerX(), client_sb.getPlayerY()+anchor, 0/*Graphics.TOP|Graphics.LEFT*/);
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
	                  g.drawImage(GetImage(IMG_HC),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
				g.drawString(YourScore+client_sb.getGameStateBlock().getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_HC_LOST:IMG_WON),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
       direct=Bomber_PMR.BUTTON_NONE;
       Bomber_GSB loc =(Bomber_GSB)client_sb.getGameStateBlock();
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = client_sb.getTimeDelay();
       stage = loc.getStage();
       client_pmr.i_value=direct;
       client_sb.nextGameStep(client_pmr);
       DrawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }

private static final int IMG_HC = 0;
private static final int IMG_HC_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_HEART = 4;
private static final int IMG_BOMB3 = 5;
private static final int IMG_BOMBICO = 6;
private static final int IMG_NFONT = 7;
private static final int IMG_BOMB = 8;
private static final int IMG_HOUSE = 9;
private static final int IMG_LEFT0 = 10;
private static final int IMG_LEFT1 = 11;
private static final int IMG_LEFT2 = 12;
private static final int IMG_LEFT3 = 13;
private static final int IMG_RIGHT0 = 14;
private static final int IMG_RIGHT1 = 15;
private static final int IMG_RIGHT2 = 16;
private static final int IMG_RIGHT3 = 17;
private static final int IMG_ROUND_L0 = 18;
private static final int IMG_ROUND_L1 = 19;
private static final int IMG_ROUND_L2 = 20;
private static final int IMG_ROUND_L3 = 21;
private static final int IMG_ROUND_R0 = 22;
private static final int IMG_ROUND_R1 = 23;
private static final int IMG_ROUND_R2 = 24;
private static final int IMG_ROUND_R3 = 25;
private static final int IMG_H_EXPL0 = 26;
private static final int IMG_H_EXPL1 = 27;
private static final int IMG_H_EXPL2 = 28;
private static final int IMG_H_EXPL3 = 29;
private static final int IMG_HOUSE_E0 = 30;
private static final int IMG_HOUSE_E1 = 31;
private static final int IMG_HOUSE_E2 = 32;
private static final int IMG_HOUSE_E3 = 33;
private static final int TOTAL_IMAGES_NUMBER = 34;

}

