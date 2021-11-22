
import javax.microedition.lcdui.*;
import com.itx.mbgame.GameObject;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;

public class GameArea extends FullCanvas implements Runnable, LoadListener {//, GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 128;
  private final static int HEIGHT_LIMIT = 128;

/*=============================*/

    private static final int KEY_MENU = -7;
    private static final int KEY_CANCEL = -6;
    private static final int KEY_ACCEPT = -7;
    private static final int KEY_BACK = -10;

    public void setScreenLight(boolean state)
    {
      DeviceControl.setLights(0, state?100:0);
    }
    public void activateVibrator(int n){}

protected Sound [] sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
//       synchronized(sounds){
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               sounds[i].stop();
//       }
//      isFirst = false;
    }
int SoundPlayTimeGap = 250;
long sound_timemark = 0;
    public void playSound(int n){
      if(sounds!=null && sounds.length>0 && midlet._Sound){
//	stopAllMelodies();
//       synchronized(sounds){
        if(soundIsPlaying() && System.currentTimeMillis()-sound_timemark<SoundPlayTimeGap) return;
	sound_timemark = System.currentTimeMillis();
	stopAllMelodies();
	sounds[n].play(1);
       }
//      }
    }
    public boolean soundIsPlaying(){
      if(sounds!=null)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               return true;
      return false;
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

  boolean isDoubleBuffered;

  private Image []Elements;
  private byte [][] ByteArray;

  private Image MenuIcon;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected Tetris client_sb;
  protected game_PMR client_pmr;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

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
			         // ni  ts  dp  ns  pg  cr  fi  go
  private int [] stageSleepTime = {100,100,100,100,150,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private int BACKGROUND_COLOR;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private static final int GROUND_OFFSET = 8;     // Vertical resize
  private static final int FAR_WIDTH     = 0x80;  //8bf, multiplyer
  private static final int NEAR_WIDTH   = 0x133;  //8bf, multiplyer -20% ~33h
  private int scr2, xY,xX;
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

    client_sb = new Tetris(VisWidth, VisHeight, null);
    client_pmr = new game_PMR();

    isDoubleBuffered = false;//this.isDoubleBuffered();

    if (!isDoubleBuffered)
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    scr2 = VisWidth>>1;
    xY = ((VisHeight - GROUND_OFFSET)<<8)/VisHeight;
    xX = (NEAR_WIDTH - FAR_WIDTH)/VisHeight;
    isFirst = true;
    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
        BACKGROUND_COLOR = 0x5555ff;

    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
        BACKGROUND_COLOR = 0xffffff; 
    }

  }

///////////////////////////////////////////////////////////////////////////////////

    long loadtime=0;

    public void nextItemLoaded(int li){
       LoadingNow=Math.min(LoadingNow+1,LoadingTotal);
       if(loadtime < System.currentTimeMillis() || LoadingNow >= LoadingTotal) {
	 loadtime = System.currentTimeMillis()+100; // 0,1sec slice
         repaint();
       }
    }

    public void startIt() {
     loading(67+TOTAL_IMAGES_NUMBER);

     try {

	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock(/*"/res/images.bin",*/this);
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];
	   font = new drawDigits(Elements[IMG_NFONT]);
	   ib = null;

           Runtime.getRuntime().gc();

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
//	  thread.setPriority(Thread.MIN_PRIORITY);
       }
       ClearGame();
     } catch(Exception e) {
        System.out.println("Can't read images");
	System.exit(-1);
//       e.printStackTrace();
     }
     Runtime.getRuntime().gc();
    }

    public void preDrawGame()
    {
      int gridGW;
      int gridGH;
      // Игровое поле
      gBuffer.setColor(0x000000);
      //gBuffer.drawRect(0,0,WIDTH_LIMIT-1,HEIGHT_LIMIT-2);
      int poleW = client_sb.gridW*7;
      int poleH = VisHeight;
      gridGW = poleW/client_sb.gridW;
      gridGH = poleH/client_sb.gridH;
      gBuffer.setColor(0x000000);
      gBuffer.drawImage(GetImage((IMG_SCORE)),VisWidth-38,0,0);
      gBuffer.drawImage(GetImage((IMG_LEVEL)),VisWidth-38,25,0);
      // Сетка
      gBuffer.setColor(0x000000);
      for(int y=0;y<client_sb.gridH;y++)
          for(int x=0;x<client_sb.gridW;x++)
                  gBuffer.drawRect(x*gridGW,y*gridGH,gridGW,gridGH);
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
       ClearGame();
       stage = 0;
       blink=false;
       ticks = 0;
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
       if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
	    client_pmr.i_Value = direct;
//            client_sb.nextGameStep(client_pmr);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
           BackupFigure();
           client_sb.nextGameStep(client_pmr);
	   gameStatus=playGame;
	}
	bufferChanged=true;
//	DoubleBuffer(gBuffer);
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
       stopAllMelodies();
       direct = 0;
       CleanSpace();
    }

    protected void keyRepeated(int keyCode) {keyPressed(keyCode);}

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
		                    stopAllMelodies();
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
	                          switch (keyCode) {
			             case MENU_KEY:  midlet.ShowGameMenu(); break;
				     case END_KEY:  midlet.ShowQuitMenu(true); break;
				     case Canvas.KEY_NUM2 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = game_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM6 : direct = game_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = game_PMR.BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = game_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = game_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = game_PMR.BUTTON_UP; break;
					       case Canvas.DOWN : direct = game_PMR.BUTTON_DOWN; break;
					           default: direct=game_PMR.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=game_PMR.BUTTON_NONE;
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
	                if(isFirst){
			  isFirst = false;
			  //PlayMelody(SND_JBELLS_OTT);
			  ticks = 0;
	                }
			if (ticks<80) ticks++;
			 else
			  if(soundIsPlaying())ticks-=10;
			  else
			  {
			   Runtime.getRuntime().gc();
			   ticks=0;
			   stopAllMelodies();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
             /*
			   ticks++; blink=(ticks&8)==0;
			   client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_FIRE);
		           client_sb.nextGameStep(client_pmr);
//			   UpdateBackScreen();

			   if (client_sb.i_PlayerState!=Gamelet.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			       isFirst = true;
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
             */
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=game_PMR.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_pmr.i_Value = direct;
                                BackupFigure();
                                client_sb.nextGameStep(client_pmr);
//				repaintBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
	                   client_pmr.i_Value = direct;
			   //if(direct==game_PMR.BUTTON_FIRE) direct=game_PMR.BUTTON_NONE;
                           BackupFigure();
                           client_sb.nextGameStep(client_pmr);
//			   UpdateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

//								       stage++;
//					                               if (stage>=Stages.NUMBER_OF_LEVELS)
								         gameStatus=Finished;
//								       else
//					                                 gameStatus=newStage;
								      break;
			         }
			   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
//			   serviceRepaints();
			  } break;
	    case Crashed:
			   stopAllMelodies();
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>0){
			     ticks = 0;
			     blink = false;
		             direct=game_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=game_PMR.BUTTON_NONE;
	                           client_pmr.i_Value = direct;
                                   BackupFigure();
                                   client_sb.nextGameStep(client_pmr);
//				   repaintBackScreen();
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           // PlayMelody(SND_APPEARANCE_OTT);
			      }
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
			   break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			     if(soundIsPlaying())ticks=FINAL_PICTURE_OBSERVING_TIME-10;
			      else
			        midlet.newScore(client_sb.getPlayerScore());
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

//	serviceRepaints();
	workDelay = System.currentTimeMillis();

      }
    }


    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y)
    {
      /*
	 switch(actionID) {
	 case RescueHelicopter.GAMEACTION_FIELDCHANGED:
		  {
		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
		  }
	 }
      */
    }

    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(~color);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }


   int tY(int y)
   {
     return  y*(VisHeight - GROUND_OFFSET)/VisHeight + GROUND_OFFSET;
   }
   int tX(int x, int y)
   {
     return (((x-scr2)*(FAR_WIDTH + y*(NEAR_WIDTH - FAR_WIDTH)/VisHeight))>>8) +scr2;
   }

   int q = 4;
   int dist(int y)
   {
      y = y*(/*amount*/4  + /*offset*/ 7)  /VisHeight;

      if( y < 0 )
                 return 0;
      if( y > q )
                 return q;
      return y;
   }

   void DrawImage(Graphics g, GameObject go, int image){
      Image img = Elements[image];
      int y = go.Y() + (go.HEIGHT()>>1);
      g.drawImage(img,tX(go.X() + (go.WIDTH()>>1),y) - (img.getWidth()>>1), tY(y) - (img.getHeight()>>1),0);
   }

   public void ClearGame()
   {
     if(baseX !=0 || baseY !=0){
       gBuffer.setColor(BkgCOLOR);
       if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                    gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
       if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
                    gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

       gBuffer.translate(baseX,baseY);
       gBuffer.setClip(0,0,VisWidth,VisHeight);
     }
     gBuffer.setColor(BACKGROUND_COLOR);
     gBuffer.fillRect(0,0,VisWidth,VisHeight);
     tmp_score = -1;
     tmp_cursepeed = -1;
     preDrawGame();
     gBuffer.translate(-baseX,-baseY);
   }

   public void BackupFigure()
   {
     tmp_figX = client_sb.figX;
     tmp_figY = client_sb.figY;
     for(int y=0;y<3;y++)
         for(int x=0;x<3;x++)
             tmp_figure[x][y] = client_sb.figure[x][y];
   }

    // ----------- tmp variables ---------------
    int tmp_score;
    int tmp_cursepeed;
    int tmp_figure[][] = new int[3][3];
    int tmp_figX;
    int tmp_figY;
    // -----------------------------------------
    protected void DoubleBuffer(Graphics gBuffer)
    {
      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
                     gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX,baseY);
        gBuffer.setClip(0,0,VisWidth,VisHeight);
      }

      int gridGW;
      int gridGH;

      // Игровое поле
      int poleW = client_sb.gridW*7;
      int poleH = VisHeight;
      gridGW = poleW/client_sb.gridW;
      gridGH = poleH/client_sb.gridH;
      if(client_sb.redrawPole)
      {
       gBuffer.setColor(BACKGROUND_COLOR);
       gBuffer.fillRect(0,0,poleW,poleH);
       // Сетка
       gBuffer.setColor(0x000000);
       for(int y=0;y<client_sb.gridH;y++)
           for(int x=0;x<client_sb.gridW;x++)
                   gBuffer.drawRect(x*gridGW,y*gridGH,gridGW,gridGH);
       for(int y=0;y<client_sb.gridH;y++)
          for(int x=0;x<client_sb.gridW;x++)
              if(client_sb.pole[x][y] != 0)
                  gBuffer.drawImage(GetImage((IMG_01+client_sb.pole[x][y]-1)),x*gridGW,y*gridGH,0);
       client_sb.redrawPole = false;
      }

      // Падающая фигурка
      if(!client_sb.figurefixed)
      //if(client_sb.figY != 0)
      {
        for(int y=0;y<3;y++)
            for(int x=0;x<3;x++)
                if(tmp_figure[x][y] != 0)
                {
                  gBuffer.setColor(BACKGROUND_COLOR);
                  gBuffer.fillRect(tmp_figX*gridGW+x*gridGW,tmp_figY*gridGH+y*gridGH,gridGW,gridGH);
                  gBuffer.setColor(0x000000);
                  gBuffer.drawRect(tmp_figX*gridGW+x*gridGW,tmp_figY*gridGH+y*gridGH,gridGW,gridGH);
                }
      }
      else
        client_sb.figurefixed = false;

      for(int y=0;y<3;y++)
          for(int x=0;x<3;x++)
              if(client_sb.figure[x][y] != 0)
                  gBuffer.drawImage(GetImage((IMG_01+client_sb.figure[x][y]-1)),client_sb.figX*gridGW+x*gridGW,client_sb.figY*gridGH+y*gridGH,0);

      // Текущий уровень игры и очки
      gBuffer.setColor(BACKGROUND_COLOR);
      if(tmp_score != client_sb.score)
      {
       tmp_score = client_sb.score;
       gBuffer.fillRect(VisWidth-37,7,6*5,9);
       font.drawDigits(gBuffer,VisWidth-37,7,5,client_sb.score);
      }
      if(tmp_cursepeed != client_sb.cursepeed)
      {
       tmp_cursepeed = client_sb.cursepeed;
       gBuffer.fillRect(VisWidth-29,32,2*5,9);
       font.drawDigits(gBuffer,VisWidth-29,32,2,Math.abs((client_sb.cursepeed-client_sb.DEFSPEED)));
      }
      gBuffer.translate(-gBuffer.getTranslateX(),-gBuffer.getTranslateY());
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
	                  g.drawImage(GetImage(IMG_FP),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
//				g.setColor(0xFFFF00);    // drawin' flyin' text , nokia color
				g.setColor(0x000000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	//g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WON),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
                                g.drawImage(GetImage(IMG_WON),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :   if (isDoubleBuffered)
			      {
		                DoubleBuffer(g);
			      }
			      else
			      {
			        //if(bufferChanged)
		                   DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                             	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       ClearGame();
       blink=false;
       ticks = 0;
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
       BackupFigure();
       client_sb.nextGameStep(client_pmr);
//       repaintBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
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
private static final int IMG_FP = 0;
private static final int IMG_01 = 1;
private static final int IMG_02 = 2;
private static final int IMG_03 = 3;
private static final int IMG_04 = 4;
private static final int IMG_05 = 5;
private static final int IMG_NFONT = 6;
private static final int IMG_WON = 7;
private static final int IMG_LEVEL = 8;
private static final int IMG_SCORE = 9;
private static final int TOTAL_IMAGES_NUMBER = 10;
}

