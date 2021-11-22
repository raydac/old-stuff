
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Tennis.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false, KeyVisible=false, DoorVisible=false;;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
                            playGame = 3,
			    Crashed = 4,
			    Finished = 5,
			    gameOver = 6;
  private int [] stageSleepTime = {100,100,100,80,200,200,200}; // in ms

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image MenuIcon = null;
//  private Image Ball[] = new Image[3];
  private Image Table = null;
  private Image Grid = null;

  private final static int iDOWN = 0,
                           iUP = 1,
			   iLEFT = 2,
			   iRIGHT = 3,
			   iICO = 4;

  private Image items[];// = new Image[iOPPONENT + 5];

  private int CurrentLevel=3;
  protected Tennis_PMR client_pmr = null;
  protected int direct=Tennis_PMR.BUTTON_NONE;
  protected Tennis_SB client_sb = new Tennis_SB();
  protected int baseX=0, baseY=0;

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  int blinks=0, ticks=0;

//=====================font===================================

    private Image []numbers=new Image[10];
    public int fontHeight=0,fontWidth=0, TitleN = 0;

    public void drawDigits(Graphics gBuffer, int x, int y, int vacant, int num)
    {
        x = x + fontWidth * (--vacant);
        if (num >= 0)
            for (; vacant >= 0; vacant--)
            {
                gBuffer.drawImage(numbers[num % 10], x, y, 0/* Graphics.LEFT|Graphics.TOP*/);
                num = num / 10;
                x -= fontWidth;
            }
    }

/*
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
*/
//============================================================



  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    client_sb.setPlayerBlock(this);
    //client_sb.setAIBlock(new Tennis_AI());
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    gameStatus=notInitialized;
    baseX = (scrwidth-100)>>1;
    baseY = (scrheight-64)>>1;
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
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
	   items = (new ImageBlock(this))._image_array;
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];

           Title=items[IMG_PP];
           Lost=items[IMG_LOST];
           Won=items[IMG_WON];
           DemoTitle=items[IMG_DEMO];
           Table=items[IMG_TBL];
           Grid=items[IMG_GRID];
/*
           Ball[0]=items[IMG_BALL1]; //Image.createImage(DemoTitleImage];
           Ball[1]=items[IMG_BALL2]; //Image.createImage(DemoTitleImage];
           Ball[2]=items[IMG_BALL3]; //Image.createImage(DemoTitleImage];

           items[iPLAYER+iDOWN]=items[IMG_ME_D];
           items[iPLAYER+iUP]=items[IMG_ME_U];
           items[iPLAYER+iLEFT]=items[IMG_ME_R];
           items[iPLAYER+iRIGHT]=items[IMG_ME_L];
           items[iPLAYER+iICO]=items[IMG_PLICO];

           items[iOPPONENT+iDOWN]=items[IMG_OPP_D];
           items[iOPPONENT+iUP]=items[IMG_OPP_T];
           items[iOPPONENT+iLEFT]=items[IMG_OPP_L];
           items[iOPPONENT+iRIGHT]=items[IMG_OPP_R];
           items[iOPPONENT+iICO]=items[IMG_OPICO];
*/
          Image tmp=items[IMG_FONT];//Image.createImage(FontImage];
          fontHeight = tmp.getHeight();
          fontWidth = tmp.getWidth()/10;
          for (int i = 1;i<10; i++){
	      numbers[i] = Image.createImage(fontWidth,fontHeight);
	      numbers[i].getGraphics().drawImage(tmp,-(i-1)*fontWidth,0,Graphics.TOP|Graphics.LEFT);
          }
	  numbers[0] = Image.createImage(fontWidth,fontHeight);
	  numbers[0].getGraphics().drawImage(tmp,-9*fontWidth,0,Graphics.TOP|Graphics.LEFT);
 	       nextItemLoaded(11);
	  tmp = null;
	  items[IMG_FONT] = null;

     } catch(Exception e) {
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }

     Runtime.getRuntime().gc();
    }


    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Tennis_PMR();

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
       direct=Tennis_PMR.BUTTON_NONE;

       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Tennis_SB.LEVEL0);
            client_sb.nextGameStep();
	    direct = Tennis_PMR.BUTTON_BEAT;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGame(CurrentLevel);
           //stageSleepTime[playGame] = ((Tennis_GSR)client_sb.getGameStateRecord()).getTimeDelay();
	   gameStatus=playGame;
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
			       switch (keyCode){
				case Canvas.KEY_NUM1: direct=Tennis_PMR.BUTTON_BEATLEFT; break;
				case Canvas.KEY_NUM3: direct=Tennis_PMR.BUTTON_BEATRIGHT; break;
				case MENU_KEY: midlet.ShowGameMenu(); break;
				case END_KEY: midlet.ShowQuitMenu(true); break;
				default:
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM4/*LEFT*/: direct=Tennis_PMR.BUTTON_LEFT; break;
	                              case Canvas.KEY_NUM6/*RIGHT*/: direct=Tennis_PMR.BUTTON_RIGHT; break;
				      case Canvas.KEY_NUM5/*FIRE*/: direct=Tennis_PMR.BUTTON_BEAT; break;
				   default:direct=Tennis_PMR.BUTTON_NONE;
	                          }
			       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Tennis_PMR.BUTTON_NONE;
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
			   Tennis_GSR loc=(Tennis_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Tennis_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
           case playGame :{
			   client_sb.nextGameStep();
			   Tennis_GSR loc=(Tennis_GSR)client_sb.getGameStateRecord();
                           ticks++;
			   if (loc.isBallLost()){
			     ticks =0; gameStatus=Crashed;
			   }
			   switch (loc.getPlayerState()) {
			      case Tennis_GSR.PLAYERSTATE_LOST: ticks =0; gameStatus=gameOver; break;
			      case Tennis_GSR.PLAYERSTATE_WON:  ticks = 0; gameStatus=Finished; break;
			         }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=Tennis_PMR.BUTTON_NONE;
			     Tennis_GSR  loc = (Tennis_GSR)client_sb.getGameStateRecord();
			      switch (loc.getPlayerState()) {
			          case Tennis_GSR.PLAYERSTATE_LOST:  gameStatus=gameOver; break;
			          case Tennis_GSR.PLAYERSTATE_WON:   gameStatus=Finished; break;
				       default: gameStatus=playGame;
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
			      ;//midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
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


private final static int _l1 = 100, _l2 = 60, _l0 = 50, _center = _l1>>1,
                         _h0 = 90, _h1=0, _h2 = 46,
			 _z0 = Tennis_SB.BALL_MAX_Z>>1, _zr = _z0+(Tennis_SB.RACKET_WIDTH>>2), _z1=_zr, _z2 = ((_z1<<4)/7*5)>>4,//_z1*_l2/_l1,
			 dX = 0, dY = 0, dZ =0;

    protected int transX(int x,int y, int z){
        int max_h = (_l1<<8) - (((_l1-_l2)*y)<<8)/_h0;
	x=(max_h*x/_l0)>>8;
	return dX+_center+x-(max_h>>9);
    }

    protected int transY(int x,int y, int z){
      z=(dZ<<8) + (
                   (
		    (_z1<<8)-(((_z1-_z2)*y)<<8)/_h0)*z/_z0);
      return 64-(((dY<<8)+((y*_h2)<<8)/_h0 + z)>>8);
    }

    protected int translBall(int y){

      return Math.min(Math.max(y*3/(_h0+10),0),2);

    }


    protected void DoubleBuffer(Graphics gBuffer){

//      if (bufferChanged) {
        gBuffer.setColor(0xffffff);
	gBuffer.fillRect(0,0,Buffer.getWidth(),Buffer.getHeight());

	gBuffer.drawImage(Table,baseX,baseY,0);

        Tennis_GSR loc=(Tennis_GSR)client_sb.getGameStateRecord();

	Racket p = loc.getPlayerRacket();
	Racket o = loc.getAIRacket();
	Ball b = loc.getBall();

	gBuffer.drawImage(items[IMG_ME_D+iICO], 0,0,0);
	gBuffer.drawImage(items[IMG_OPP_D+iICO], scrwidth - items[IMG_OPP_D+iICO].getWidth(),0,0);
	if(gameStatus!=Crashed || blink || !loc.isPlayerScoresChanged())
	   drawDigits(gBuffer,items[IMG_OPP_D+iICO].getWidth()+2,0,2,loc.getPlayerScores());
	if(gameStatus!=Crashed || blink || loc.isPlayerScoresChanged())
 	   drawDigits(gBuffer,scrwidth - items[IMG_OPP_D+iICO].getWidth()-fontWidth*2-2 ,0,2,loc.getAIScores());

	gBuffer.drawImage(items[IMG_OPP_D+o.getState()],baseX+transX(o.getY(),o.getX(),_zr),baseY+transY(o.getY(),o.getX(),_zr),0);//Graphics.TOP|Graphics.RIGHT);
	if(b.getX()>45)gBuffer.drawImage(Grid,baseX+12,baseY+30,0);
	if(gameStatus!=Crashed || blink)
           gBuffer.drawImage(items[IMG_BALL1+ translBall(b.getX())],
	        baseX+transX(b.getY(), /**/   (b._state==b.BALL_SPEEDLOW&&b.getX()<20)?20:b.getX() /**/, b.getZ()>>1),
		baseY+transY(b.getY(),/**/   (b._state==b.BALL_SPEEDLOW&&b.getX()<20)?20:b.getX() /**/,(b._state==b.BALL_SPEEDLOW?0:(b.getZ()>>1))),
		Graphics.VCENTER|Graphics.LEFT);
//		Graphics.HCENTER|Graphics.VCENTER);
	if(b.getX()<=45)gBuffer.drawImage(Grid,baseX+12,baseY+30,0);

	gBuffer.drawImage(items[IMG_ME_D+p.getState()],baseX+transX(p.getY(),p.getX(),_zr),baseY+transY(p.getY(),p.getX(),_zr),0);//Graphics.TOP|Graphics.RIGHT);

/*
        if(!(gameStatus == Crashed && blink)){
	  gBuffer.drawImage(Ball,loc.
        }
*/
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
              default :   if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			      }
			      else {
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed);
    }


    public void gameLoaded(){
       Tennis_GSR loc = (Tennis_GSR)client_sb.getGameStateRecord();
       blink=false;
       ticks = 1;
       direct=Tennis_PMR.BUTTON_NONE;
       CurrentLevel=loc.getLevel();
//       stageSleepTime[playGame] = loc.getTimeDelay();
       gameStatus=playGame;
//       client_sb.nextGameStep();
//       DrawBackScreen();
       bufferChanged=true;
       repaint();
    }
private static final byte IMG_PP = (byte)0;
private static final byte IMG_WON = (byte)2;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_TBL = (byte)3;
private static final byte IMG_FONT = (byte)19;
private static final byte IMG_DEMO = (byte)5;
private static final byte IMG_GRID = (byte)4;
private static final byte IMG_ME_D = (byte)9;
private static final byte IMG_ME_U = (byte)10;
private static final byte IMG_ME_R = (byte)11;
private static final byte IMG_ME_L = (byte)12;
private static final byte IMG_PLICO = (byte)13;
private static final byte IMG_OPP_D = (byte)14;
private static final byte IMG_OPP_L = (byte)16;
private static final byte IMG_OPICO = (byte)18;
private static final byte IMG_OPP_T = (byte)15;
private static final byte IMG_OPP_R = (byte)17;
private static final byte IMG_BALL1 = (byte)6;
private static final byte IMG_BALL2 = (byte)7;
private static final byte IMG_BALL3 = (byte)8;
private static final byte TOTAL_IMAGES_NUMBER = (byte)20;

}

/*
.
PP,0
LOST,1
WON,2
TBL,3
GRID,4
DEMO,5

BALL1,6
BALL2,7
BALL3,8

ME_D,9
ME_U,10
ME_R,11
ME_L,12
PLICO,13

OPP_D,14
OPP_T,15
OPP_L,16
OPP_R,17
OPICO,18

FONT,19
.
.
*/
