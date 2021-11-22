import com.itx.mbgame.GameObject;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Melody;
//import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener {//, GameActionListener {
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
  protected Hockey client_sb;
  private hok_PMR client_pmr;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  private boolean still_painting = false;
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
			      //                    pg
  private int [] stageSleepTime = {100,100,100,100,100,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private final static int BkgCOLOR = 0x000061;
  private int VisWidth, VisHeight;
  boolean isFirst;

    // precalculated values
  int scr2;            // VisWidth/2;

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

    client_sb = new Hockey(VisWidth, VisHeight, null);
    client_pmr = new hok_PMR();

    if(!this.isDoubleBuffered()){
      Buffer=Image.createImage(scrwidth,scrheight);
      gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    scr2 = VisWidth>>1;
    isFirst = true;
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
	   client_sb.vorotaY += (VisHeight-GetImage(IMG_BACK).getHeight())>>1;
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
       blink=false;
       ticks = 0;
       direct=hok_PMR.BUTTON_NONE;
       client_pmr.i_Value = direct;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
//	    client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
           client_sb.nextGameStep(client_pmr);
	   stage = 0;
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
       if(gameStatus == playGame) client_sb.resumeGame();
    }

    public void hideNotify(){
       isShown = false;
       stopAllMelodies();
       direct = 0;
       CleanSpace();
       if(gameStatus == playGame) client_sb.pauseGame();
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
				     case Canvas.KEY_NUM1 : direct = hok_PMR.BUTTON_UPLEFT; break;
				     case Canvas.KEY_NUM2 : direct = hok_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM3 : direct = hok_PMR.BUTTON_UPRIGHT; break;
				     case Canvas.KEY_NUM4 : direct = hok_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = hok_PMR.BUTTON_PASS; break;
				     case Canvas.KEY_NUM6 : direct = hok_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM7 : direct = hok_PMR.BUTTON_DOWNLEFT; break;
				     case Canvas.KEY_NUM8 : direct = hok_PMR.BUTTON_DOWN; break;
				     case Canvas.KEY_NUM9 : direct = hok_PMR.BUTTON_DOWNRIGHT; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT:direct=hok_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT:direct=hok_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP:direct=hok_PMR.BUTTON_UP; break;
					       case Canvas.DOWN:direct=hok_PMR.BUTTON_DOWN; break;
					       case Canvas.FIRE:direct=hok_PMR.BUTTON_PASS; break;
					           default: direct=hok_PMR.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=hok_PMR.BUTTON_NONE;
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
			   ticks++; blink=(ticks&8)==0;
			   client_pmr.i_Value = client_sb.getRandomInt(hok_PMR.BUTTON_PASS);
		           client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();

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
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=hok_PMR.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
				client_pmr.i_Value = direct;
				client_sb.nextGameStep(client_pmr);
//				drawBackScreen();
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
			   client_sb.nextGameStep(client_pmr);
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
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=hok_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
				   client_pmr.i_Value = direct;
				   client_sb.nextGameStep(client_pmr);
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           // PlayMelody(SND_APPEARANCE_OTT);
			      }
			   }
			//!!!!!! ONLY FOR HOCKEY
			   gameStatus=gameOver;
			//!!!!!!!!!!!!!!!!!
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
        if(still_painting) serviceRepaints();
	workDelay = System.currentTimeMillis();

      }
    }


/*

    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case Hockey.GAMEACTION_FIELDCHANGED:
		  {
		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
		  }
	 }
    }


    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(0x0);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }
*/
    Font f = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_SMALL);

    private void DrawRoundedText(Graphics gBuffer, String whatprint, int x, int y, int limit, boolean from_right){
        Font cur = gBuffer.getFont();
	gBuffer.setFont(f);

	if(whatprint.length()>limit)limit = whatprint.length();

        int w = f.charWidth('0')*limit+4 +1,
	    h = f.getHeight()+4;

	if(from_right) x-=w;

        gBuffer.setColor(0xffffff);
        gBuffer.fillRoundRect(x,y,w,h,4,4);
        gBuffer.setColor(0x0);
        gBuffer.drawRoundRect(x,y,w,h,4,4);

	x+=w>>1;
	gBuffer.drawString(whatprint,x,y+3, Graphics.HCENTER|Graphics.TOP);

	gBuffer.setFont(cur);
    }


    protected void DoubleBuffer(Graphics gBuffer){

      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(0x0);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }


        gBuffer.translate(baseX,baseY);
        gBuffer.setClip(0,0,VisWidth,VisHeight);
      }

////////////////////////////////////////////////

        Image img = null;

	// prepare screen
	gBuffer.setColor(0xffffff);gBuffer.fillRect(0,0,VisWidth,VisHeight);
	gBuffer.setColor(0x0);

	img = Elements[IMG_BACK];
        gBuffer.drawImage(img,(VisWidth-img.getWidth())>>1,(VisHeight-img.getHeight())>>1,0);

        // Gate
//        gBuffer.drawImage(Elements[IMG_GATE], client_sb.vorotaX, client_sb.vorotaY, 0);

        // throw pass
        int addX = 0,addY = 0;
        if(client_sb.shajbaPlayer == 0)
         addX = -5;
        else
         addX = 5;
	int i = IMG_KEEP_CEN_DOWN;
        if(client_sb.missHitAnim)
        {
            int dy,dx;
            addX = 0;
            addY = 0;
            if(client_sb.missHit)
            {
                dy = 5;
                dx = 10;
            }
            else
            {
                dy = 3;
                dx = 5;
            }
	    switch(client_sb.shajba.getDirection()){
	      case GameObject.DIR_UP:       i = IMG_KEEP_CEN_UP; addY -= dy; break;

              case GameObject.DIR_LEFT:     i = IMG_KEEP_L_MID; addX-=dx; break;
	      case GameObject.DIR_UPLEFT:   i = IMG_KEEP_L_UP; addX-=dx; addY -= dy;break;
	      case GameObject.DIR_DOWNLEFT: i = IMG_KEEP_L_DOWN; addX-=dx; break;

	      case GameObject.DIR_RIGHT:    i = IMG_KEEP_R_MID; addX+=dx; break;
	      case GameObject.DIR_UPRIGHT:  i = IMG_KEEP_R_UP; addX+=dx; addY -= dy;break;
	      case GameObject.DIR_DOWNRIGHT:i = IMG_KEEP_R_DOWN; addX+=dx; break;
	    }
        }
	gBuffer.drawImage(Elements[i], client_sb.goaile.X()+addX, client_sb.goaile.Y()+addY, 0);

        // Man 1 & 2
	int p1_frame = IMG_PLAYER01, p2_frame = IMG_PLAYER01;
	if(/*client_sb.standbycounter<2 && */(client_sb.shajbaMove || client_sb.shajbaMoveToGoaile))
	  if (client_sb.shajbaPlayer==0) p1_frame++;
	     else p2_frame++;
        gBuffer.drawImage(Elements[p1_frame], client_sb.man1.X(), client_sb.man1.Y(), 0);
        gBuffer.drawImage(Elements[p2_frame], client_sb.man2.X(), client_sb.man2.Y(), 0);


        // Shajba
        img = Elements[IMG_SHAIBA];
        if(client_sb.missHit && client_sb.missHitAnim /*&& (ticks&1) == 0*/) // Shajba otbita ----->
	{
	  Image star = Elements[IMG_STAR];
          gBuffer.drawImage(star, client_sb.shajba.X()+((img.getWidth() - star.getWidth())>>1)
	                        , client_sb.shajba.Y()+((img.getHeight() - star.getHeight())>>1), 0);
	} else
           gBuffer.drawImage(img, client_sb.shajba.X(), client_sb.shajba.Y(), 0);


	    int x = 2, y=2, w = (font.fontWidth<<2)+3,h = font.fontHeight+4;
	    // Score
            gBuffer.setColor(0xffffff);
            gBuffer.fillRect(x,y,w,h); //            gBuffer.fillRoundRect(x,y,w,h,4,4);
            gBuffer.setColor(0x0);
            gBuffer.drawRect(x,y,w,h); //            gBuffer.fillRoundRect(x,y,w,h,4,4);

            x+=2;
            if (client_sb.gametype != 2)
	         font.drawDigits(gBuffer,x+font.fontWidth/*(w-f.Width*2)/2*/,y+2,2,client_sb.score);
            else {
	         font.drawDigits(gBuffer,x,y+2,2,client_sb.score);
		 x+=font.fontWidth<<1;
	         font.drawSymbol(gBuffer,x,y+2,font.HYPHEN);
		 x+=font.fontWidth;
	         font.drawDigits(gBuffer,x,y+2,1,(client_sb.arcadeLevelBase + client_sb.arcadelevel));
            }

            // Time
	    x = VisWidth-w-2;
            gBuffer.setColor(0xffffff);
            gBuffer.fillRect(x,y,w,h); //            gBuffer.fillRoundRect(x,y,w,h,4,4);
            gBuffer.setColor(0x0);
            gBuffer.drawRect(x,y,w,h); //            gBuffer.fillRoundRect(x,y,w,h,4,4);

	    x+=2;
            if (client_sb.gametype != 0)
	         font.drawDigits(gBuffer,x+font.fontWidth/*(w-f.Width*2)/2*/,y+2,2,(int)client_sb.tm);
            else {
	         font.drawDigits(gBuffer,x,y+2,1,client_sb.x3time);
		 x+=font.fontWidth;
	         font.drawSymbol(gBuffer,x,y+2,font.MULTIPLY);
		 x+=font.fontWidth;
	         font.drawDigits(gBuffer,x,y+2,2,(int)client_sb.tm);
            }
// demo
	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, VisWidth>>1 ,VisHeight-VisHeight/4/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
      if(baseX !=0 || baseY !=0){
        gBuffer.translate(-baseX,-baseY);
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
      still_painting = true;
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
	                  g.drawImage(GetImage(IMG_FP),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                  if (colorness){
	                      g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0xFF0000);    // drawin' flyin' text colour
	                  } else {
	                      g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0x0);    // drawin' flyin' monochrome
	                  }
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
	                  } break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0xFFFF00);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
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
      still_painting = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=hok_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
       client_sb.nextGameStep(client_pmr);
//       drawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }


private Image GetImage(int n){
       if(Elements[n]!=null) return Elements[n];
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
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_BACK = 4;
private static final int IMG_GATE = 5;
private static final int IMG_KEEP_CEN_DOWN = 6;
private static final int IMG_KEEP_CEN_UP = 7;
private static final int IMG_KEEP_L_DOWN = 8;
private static final int IMG_KEEP_L_MID = 9;
private static final int IMG_KEEP_L_UP = 10;
private static final int IMG_KEEP_R_DOWN = 11;
private static final int IMG_KEEP_R_MID = 12;
private static final int IMG_KEEP_R_UP = 13;
private static final int IMG_KEEP01 = 14;
private static final int IMG_PLAYER01 = 15;
private static final int IMG_PLAYER02 = 16;
private static final int IMG_SHAIBA = 17;
private static final int IMG_STAR = 18;
private static final int IMG_NFONT = 19;
private static final int TOTAL_IMAGES_NUMBER = 20;
}

