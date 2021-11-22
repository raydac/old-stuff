package com;

import javax.microedition.lcdui.*;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable {//, GameActionListener {
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
  protected KillerTank client_sb;
//  protected KT_PMR client_pmr;
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
  private int [] stageSleepTime = {100,100,100,100,130,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private final static int BkgCOLOR = 0xffffff;//0x000061; for Nokia
  private int VisWidth, VisHeight;
  boolean isFirst;
  private static final int GROUND_OFFSET = 22;     // Vertical resize
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

    client_sb = new KillerTank(VisWidth, VisHeight, null);
//    client_pmr = new KT_PMR();

    if (!this.isDoubleBuffered())
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
	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;
           Runtime.getRuntime().gc();
	   Elements[IMG_BACK] = GetImage(IMG_BACK);
	   ByteArray[IMG_BACK] = null;
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
       direct=KillerTank.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
            client_sb.nextGameStep(direct);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
           client_sb.nextGameStep(direct);
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
				     case Canvas.KEY_NUM2 : direct = KillerTank.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = KillerTank.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = KillerTank.BUTTON_FIRE; break;
				     case Canvas.KEY_NUM6 : direct = KillerTank.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = KillerTank.BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = KillerTank.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = KillerTank.BUTTON_RIGHT; break;
					       case Canvas.FIRE : direct = KillerTank.BUTTON_FIRE; break;
					       case Canvas.UP   : direct = KillerTank.BUTTON_UP; break;
					       case Canvas.DOWN : direct = KillerTank.BUTTON_DOWN; break;
					           default: direct=KillerTank.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=KillerTank.BUTTON_NONE;
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
		           client_sb.nextGameStep(client_sb.getRandomInt(KillerTank.BUTTON_FIRE));
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
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=KillerTank.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_sb.nextGameStep(direct);
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
	                   client_sb.nextGameStep(direct);
			   if(direct==KillerTank.BUTTON_FIRE) direct=KillerTank.BUTTON_NONE;
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
			   if(ticks>20){
			     ticks = 0;
			     blink = false;
		             direct=KillerTank.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=KillerTank.BUTTON_NONE;
	                           client_sb.nextGameStep(direct);
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


/*

    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case RescueHelicopter.GAMEACTION_FIELDCHANGED:
		  {
		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
		  }
	 }
    }

*/
    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
//      if(VisHeight<=80)
     {
//        gBuffer.setColor(~color);
        gBuffer.setColor(0xffffff);
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
      Image img = GetImage(image);
      int y = go.Y() + (go.HEIGHT()>>1);
      g.drawImage(img,tX(go.X() + (go.WIDTH()>>1),y) - (img.getWidth()>>1), tY(y) - (img.getHeight()>>1),0);
   }

    protected void DoubleBuffer(Graphics gBuffer){

      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.setClip(0,0,VisWidth,VisHeight);
        gBuffer.translate(baseX,baseY);
      }

////////////////////////////////////////////////


        Image img = null;

	// prepare screen
//	gBuffer.setColor(0xffffff);gBuffer.fillRect(0,0,VisWidth,VisHeight);
//      gBuffer.setColor(0x0);
        img = GetImage(IMG_BACK);
        gBuffer.drawImage(img,0,0,0);
	int h = img.getHeight();
//	if(h<VisHeight) {gBuffer.setColor(0xffffff);gBuffer.fillRect(0,h,VisWidth,VisHeight-h);}

	gBuffer.setColor(0x0);

        // Hobot
        int angle = (client_sb.hobotAngle + 2) & 63;
        int x2 = client_sb.hobotX + Gamelet.xCoSine(client_sb.hobotRadius, angle);
        int y2 = client_sb.hobotY - Gamelet.xSine(client_sb.hobotRadius, angle);

        // My Killer Tank
	if(gameStatus != Crashed || blink){
          gBuffer.drawLine(client_sb.hobotX, client_sb.hobotY, x2, y2);
	  img = GetImage(IMG_PLAYER01);
	  gBuffer.drawImage(img,scr2-(img.getWidth()>>1),VisHeight - img.getHeight(),0);
	}



        if(client_sb.shield)
        {
	  x2 = scr2 - (client_sb.tank.WIDTH()>>1);
	  y2 = VisHeight -8;
	  int sc = client_sb.MAX_SCOUNTER - client_sb.shieldCounter;
	  int x = VisWidth - font.fontWidth*3;
	  img = GetImage(IMG_DEFENCE01);

          if(sc > 20 || (ticks&1) == 0)
          {
            gBuffer.drawArc(x2-16,y2-16,client_sb.tank.WIDTH()+(16<<1),client_sb.tank.HEIGHT()+(16<<1)+8,0,180);
	    font.drawDigits(gBuffer,x,1,3,sc);
          }
	  gBuffer.drawImage(img,x-img.getWidth(),0,0);

          h = ticks&15;
	  int h2 = h<<1;
          gBuffer.drawArc(x2-h,y2-h,client_sb.tank.WIDTH()+h2,client_sb.tank.HEIGHT()+h2+8,0,180);

          h = ((ticks+7)&15);
	  h2 = h<<1;
          gBuffer.drawArc(x2-h,y2-h,client_sb.tank.WIDTH()+h2,client_sb.tank.HEIGHT()+h2+8,0,180);
        }

        // fire
        for (int i = 0; i < client_sb.fp.length; i++)
        {
            if (client_sb.fp[i].fire)
            {
                angle = (client_sb.fp[i].fireAngle + 2) & 63;
                x2 = client_sb.hobotX + Gamelet.xCoSine(client_sb.fp[i].fireRadiusCounter, angle);
                y2 = client_sb.hobotY - Gamelet.xSine(client_sb.fp[i].fireRadiusCounter, angle);

                if (!client_sb.fp[i].explosion.isActive())
                {
		    x2 = tX(x2,y2);
		    y2 = tY(y2);
                    gBuffer.fillRect(x2, y2, 2, 2);
                }
                else
                {
                    // !!! Required for collision detection !!!
                    // Sandy blya - nu nahren mne ne nado v gameStep schitat' eto!!!

                    // client_sb.fp[i].explosion.setCoord((byte) (x2 - client_sb.fp[i].explosion.WIDTH() / 2), (byte) (y2 - client_sb.fp[i].explosion.HEIGHT() / 2));

                    // >8E

                   client_sb.fp[i].explosion.setCoord((byte) (x2 - (client_sb.fp[i].explosion.WIDTH()>>1)), (byte) (y2 - (client_sb.fp[i].explosion.HEIGHT()>>1)));
                   int idx = (5*client_sb.fp[i].explosionCounter/client_sb.MAXEXPLOSION_COUNTER);
		   if(idx>4)idx=4;
		   DrawImage(gBuffer,client_sb.fp[i].explosion, IMG_EXPL01+idx);
                }

            }
        }

        // Heal present
        if (client_sb.healPresent.isActive())
        {
	      DrawImage(gBuffer,client_sb.healPresent,IMG_HEALTH01 + (ticks&1));

        }

        // Shield present
        if (client_sb.shieldPresent.isActive())
        {
	      DrawImage(gBuffer,client_sb.shieldPresent,IMG_DEFENCE01 + (ticks&1));
        }

        // Enemies
        for (int i = 0; i < client_sb.enemy.length; i++)
        {

            if(client_sb.enemy[i].isActive())
            {

                y2 = client_sb.enemy[i].Y() + (client_sb.enemy[i].HEIGHT()>>1);
//		x2 = client_sb.enemy[i].X() + (client_sb.enemy[i].WIDTH()>>1);
		int distance = dist(y2);
		if(distance == q) distance+=(ticks&1);
                switch(client_sb.enemy[i].type)
                {
                    case 0:
                           distance = IMG_ENEMY01_04 -distance;
                        break;
                    case 1:
                           distance = IMG_TECH02_04 -distance;
                        break;
                    case 2:
                           distance = IMG_TECH02_04 -distance;
                        break;
                    default:
                }
//                gBuffer.fillRect(client_sb.enemy[i].X(), client_sb.enemy[i].Y(), client_sb.enemy[i].WIDTH(), client_sb.enemy[i].HEIGHT());
//		x2 = tX(x2,y2)-(img.getWidth()>>1);
//		y2 = tY(y2)-(img.getHeight()>>1);
//                gBuffer.drawImage(img,x2,y2,0);
                DrawImage(gBuffer,client_sb.enemy[i],distance);

            }
            // Enemy fire;
            if(client_sb.enemy[i].fire)
            {
                gBuffer.fillRect(tX(client_sb.enemy[i].aX, client_sb.enemy[i].aY), tY(client_sb.enemy[i].aY),2, 2);
            }
        }


        // target
        angle = (client_sb.hobotAngle + 2) & 63;
        x2 = client_sb.hobotX + Gamelet.xCoSine(client_sb.targetRadius, angle);
        y2 = client_sb.hobotY - Gamelet.xSine(client_sb.targetRadius, angle);
	x2 = tX(x2,y2);
	y2 = tY(y2);
/*
        gBuffer.drawLine(x2, y2, x2 + 2, y2);
        gBuffer.drawLine(x2 + 1, y2 - 1, x2 + 1, y2 + 1);
*/
	img = GetImage(IMG_SIGHTICO);
        gBuffer.drawImage(img,x2-(img.getWidth()>>1),y2-(img.getHeight()>>1),0);

        // Aircraft
        if(client_sb.aircraftObj.isActive())
        {
              y2 = client_sb.aircraftObj.Y() + (client_sb.aircraftObj.HEIGHT()>>1);
              DrawImage(gBuffer,client_sb.aircraftObj,IMG_PLANE04-dist(y2));
        }

        BAR(gBuffer,0,0,client_sb.MAXLIVES,client_sb.lives,0xff0000);
	font.drawDigits(gBuffer,2,VisHeight - font.fontHeight-2,5,client_sb.i_PlayerScore);

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight - VisHeight/4/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
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
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=KillerTank.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_sb.nextGameStep(direct);
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

private static final byte IMG_FP = 0;
private static final byte IMG_LOST = 1;
private static final byte IMG_WON = 2;
private static final byte IMG_DEMO = 3;
private static final byte IMG_BACK = 4;
private static final byte IMG_PLAYER01 = 5;
private static final byte IMG_PLANE00 = 7;
private static final byte IMG_PLANE01 = 8;
private static final byte IMG_PLANE02 = 9;
private static final byte IMG_PLANE04 = 11;
private static final byte IMG_ENEMY01_00 = 12;
private static final byte IMG_ENEMY02_00 = 13;
private static final byte IMG_ENEMY01_01 = 14;
private static final byte IMG_ENEMY01_02 = 15;
private static final byte IMG_ENEMY01_03 = 16;
private static final byte IMG_ENEMY01_04 = 17;
private static final byte IMG_TECH01 = 18;
private static final byte IMG_TECH02 = 19;
private static final byte IMG_TECH02_01 = 20;
private static final byte IMG_TECH02_02 = 21;
private static final byte IMG_TECH02_03 = 22;
private static final byte IMG_TECH02_04 = 23;
private static final byte IMG_EXPL01 = 24;
private static final byte IMG_EXPL02 = 25;
private static final byte IMG_EXPL03 = 26;
private static final byte IMG_EXPL04 = 27;
private static final byte IMG_EXPL05 = 28;
private static final byte IMG_DEFENCE01 = 29;
private static final byte IMG_HEALTH01 = 31;
private static final byte IMG_SIGHTICO = 33;
private static final byte IMG_NFONT = 34;
private static final byte IMG_PLAYER02 = 6;
private static final byte IMG_PLANE03 = 10;
private static final byte IMG_DEFENCE02 = 30;
private static final byte IMG_HEALTH02 = 32;
private static final byte TOTAL_IMAGES_NUMBER = 35;
}

