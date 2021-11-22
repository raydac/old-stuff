
import com.itx.mbgame.GameObject;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Melody;
//import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener {// , GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = 100;

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
  protected RescueHelicopter client_sb;
  protected hel_PMR client_pmr;
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
  private int [] stageSleepTime = {100,100,100,100,100,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private int demoStage = 2;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private final static int BkgCOLOR = 0x000061;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int bsWIDTH, bsHEIGHT, bsFRAMES, bsLAST_FRAME, scrX, bsPOS;
  private static final int GROUND_HEIGHT = 2;
  private static final int SCENE_HEIGHT = 100;
  private static final int PEAK_WIDTH = 2;

  private Image Sky,Ground;

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

    client_sb = new RescueHelicopter((byte)VisWidth, (byte)VisHeight, null);
    client_pmr = new hel_PMR();

    if (!this.isDoubleBuffered())
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    bsFRAMES = VisWidth/ /*RescueHelicopter.*/PEAK_WIDTH +3;
    bsWIDTH =  bsFRAMES* /*RescueHelicopter.*/PEAK_WIDTH;
    bsHEIGHT = /*RescueHelicopter.*/SCENE_HEIGHT;

    BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
    gBackScreen=BackScreen.getGraphics();

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth +1)>>1;
    baseY = (scrheight - VisHeight +1)>>1;

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
	   Elements[IMG_NFONT] = null;
	   ib = null;
	   if(colorness){
	     Image img = Elements[IMG_MAP];
//	     Sky = CreateGradientImage(2,100,0xA0410D,0xFF8000);
	     Sky = CreateGradientImage(2,100,0x461062,0xf19178);
//	     Sky.getGraphics().drawImage(img,0,img.getHeight()-Sky.getHeight(),0);
	     Ground = CreateGradientImage(2,100,0x804000,0x000000);
	     Ground.getGraphics().drawImage(img,0,img.getHeight()-Ground.getHeight(),0);
             Elements[IMG_MAP] = null;
	   } else {
	     Sky = null;
	     Ground = Elements[IMG_MAP];
	   }


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
       direct=hel_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    if (demoStage >= Stages.NUMBER_OF_LEVELS || demoStage >= 5) demoStage=2;
	    client_sb.initStage(demoStage);
	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(client_pmr);
	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
//           client_sb.nextGameStep(hel_PMR.BUTTON_NONE);
	   gameStatus=newStage;
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
				     case Canvas.KEY_NUM1 : direct = hel_PMR.BUTTON_UPLEFT; break;
				     case Canvas.KEY_NUM2 : direct = hel_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM3 : direct = hel_PMR.BUTTON_UPRIGHT; break;
				     case Canvas.KEY_NUM4 : direct = hel_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM6 : direct = hel_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM7 : direct = hel_PMR.BUTTON_DOWNLEFT; break;
				     case Canvas.KEY_NUM8 : direct = hel_PMR.BUTTON_DOWN; break;
				     case Canvas.KEY_NUM9 : direct = hel_PMR.BUTTON_DOWNRIGHT; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT:direct=hel_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT:direct=hel_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP:direct=hel_PMR.BUTTON_UP; break;
					       case Canvas.DOWN:direct=hel_PMR.BUTTON_DOWN; break;
					           default: direct=hel_PMR.BUTTON_ANY;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=hel_PMR.BUTTON_NONE;
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
			   client_pmr.i_Value = client_sb.getRandomInt(hel_PMR.BUTTON_ANY);
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
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=hel_PMR.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_pmr.i_Value = direct;
                                client_sb.nextGameStep(client_pmr);
				repaintBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>25){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
	                   client_pmr.i_Value = direct;
                           client_sb.nextGameStep(client_pmr);
//			   UpdateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>=Stages.NUMBER_OF_LEVELS)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
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
//			   if(ticks>20)
			   {
			     ticks = 0;
			     blink = false;
		             direct=hel_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=hel_PMR.BUTTON_NONE;
	                           client_pmr.i_Value = direct;
                                   client_sb.nextGameStep(client_pmr);
				   repaintBackScreen();
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


    private Image CreateGradientImage(int x,int y,int startc,int endc){
       Image img = Image.createImage(x,y);
       Graphics g = img.getGraphics();

       int rs =(startc>>>16)&0xff,rd=((endc>>>16)&0xff)-rs;
       int gs =(startc>>>8)&0xff,gd=((endc>>>8)&0xff) - gs;
       int bs =(startc)&0xff,bd=((endc)&0xff)-bs;

       if(y>0)
        for (int i = 0; i<y; i++){
	  g.setColor(rs+rd*i/y,
	             gs+gd*i/y,
		     bs+bd*i/y);
	  g.drawLine(0,i,x,i);
        }
       return img;
    }

    private int getAttitude(int index){
      return (index < client_sb.level.length && index > 0)
                                ?
         client_sb.level[index] : GROUND_HEIGHT;
    }

    private void DrawVerticalCell(int index,int bs_position){
	 int _y = getAttitude(index);
	 int h = client_sb.H - _y;
//	 bs_position = (bs_position%bsFRAMES)*RescueHelicopter.PEAK_WIDTH;
	 bs_position = bs_position<<1;

	 if(Sky==null){
	   gBackScreen.setColor(0xffffff);                 //sky
           gBackScreen.fillRect(bs_position, 0 , 2, client_sb.H);
	 } else
	   gBackScreen.drawImage(Sky, bs_position, 0,0 );

	 gBackScreen.setColor(0);                        // ground
	 if(Ground==null){
         gBackScreen.fillRect(bs_position, h , 2, _y);
	 } else {

	     gBackScreen.setClip(bs_position, h , 2, _y);
	     gBackScreen.drawImage(Ground, bs_position-(bs_position%Ground.getWidth()), client_sb.H - Ground.getHeight() +(_y>>1),0 );
             gBackScreen.setClip(0,0,bsWIDTH , bsHEIGHT );
	 }

	 int _yc = client_sb.H - getAttitude(index-1);   // left contour
	 gBackScreen.setColor(0);
	 gBackScreen.drawLine(bs_position, _yc, bs_position++, h);
	 gBackScreen.drawLine(bs_position, h, bs_position, h);
    }

    private void repaintBackScreen(){
       bsLAST_FRAME = client_sb.screenX;
       for(int i=0;i<bsFRAMES;i++)
		 DrawVerticalCell(bsLAST_FRAME+i,i);
       scrX = -2;
       bsPOS = 0;
    }


int base_dy = 0;
    protected void DoubleBuffer(Graphics gBuffer){

//      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(0x0);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }


        gBuffer.setClip(0,0,VisWidth,VisHeight); // Siemens is using an absolute screen's clip region,
						 // but the Nokia is using relative region and for preventing
						 // incompatibility of these devices we should set regions
						 // before translating the coordinates

        if(HEIGHT_LIMIT>VisHeight){
	  base_dy = client_sb.hel.Y()-(VisHeight>>1);
	  if(base_dy<0)base_dy=0;
	   else
	     if(base_dy+VisHeight>HEIGHT_LIMIT)base_dy=HEIGHT_LIMIT - VisHeight;
	  base_dy = -base_dy;
	  gBuffer.translate(baseX , base_dy);
        } else
        gBuffer.translate(baseX,baseY);
//      }

////////////////////////////////////////////////


        Image img = null;

	// prepare screen
//	gBuffer.setColor(0xffffff);gBuffer.fillRect(0,0,VisWidth,HEIGHT_LIMIT);


	if(client_sb.screenX!=bsLAST_FRAME) {
	   int dx = client_sb.screenX - bsLAST_FRAME;
	   int step = dx>0?1:-1;

	   for (int i = 0 ; i != dx ; i+=step){
	     DrawVerticalCell(bsLAST_FRAME + (step>0?(bsFRAMES-1):0),bsPOS);
	     bsPOS += step;
	     if(bsPOS<0)bsPOS+=bsFRAMES;
	      else if(bsPOS>=bsFRAMES)bsPOS-=bsFRAMES;
	     bsLAST_FRAME+=step;
	   }
//	   bsLAST_FRAME = client_sb.screenX;
	   scrX -= (dx<<1);
	   if(scrX>=bsWIDTH)scrX-=bsWIDTH;
	     else
	       if(scrX<0)scrX+=bsWIDTH;

	}
	  gBuffer.drawImage(BackScreen,scrX,0,0);
	  gBuffer.drawImage(BackScreen,scrX-bsWIDTH,0,0);

        // Peoples
        for (int i = 0; i < client_sb.peoples.length; i++)
        {
            if (client_sb.peoples[i] != null && client_sb.peoples[i].isActive())
            {
                int lx = client_sb.peoples[i].aX;
                gBuffer.drawImage(Elements[IMG_MAN01+((ticks>>1)&1)], client_sb.getScreenX(lx), client_sb.peoples[i].aY, 0);
            }
        }

        // Helicopter
        if ((gameStatus != Crashed && client_sb.hel.isActive() && !client_sb.helExplosion) || blink)
        {

	  if(client_sb.hel.getDirection() == GameObject.DIR_LEFT){
	    img = Elements[IMG_HEL_RIGHT01 + (ticks&1)];
            gBuffer.drawImage(img, client_sb.hel.X() + 1 +client_sb.hel.WIDTH() -img.getWidth(), client_sb.hel.Y() + client_sb.hel.HEIGHT() - img.getHeight(),0);
	  } else {
	    img = Elements[IMG_HEL_LEFT01 + (ticks&1)];
            gBuffer.drawImage(img, client_sb.hel.X() + 1, client_sb.hel.Y() + client_sb.hel.HEIGHT() - img.getHeight(),0);
	  }

	  /*
            gBuffer.drawImage(
	                      Elements[
			               (
				           client_sb.hel.getDirection() == GameObject.DIR_LEFT
					                             ?
						      IMG_HEL_RIGHT01 : IMG_HEL_LEFT01
					) + (ticks&1)
				      ], client_sb.hel.X() + 1, client_sb.hel.Y() + 1,0);
	  */
        }

        gBuffer.setColor(0x0);
	if(colorness) gBuffer.setColor(200,0,0);

        // Vulcan fire and stones
        for (int v = 0; v < client_sb.vulcan.length; v++)
        {
	    RescueHelicopter.VulcanObject vulcan = client_sb.vulcan[v];

            if (vulcan != null && vulcan.isActive())
            {

	       if(vulcan.vtype == 2){

		img = Elements[(vulcan.phase>>1) + IMG_SMOKE01];
                gBuffer.drawImage(
		                   img,
		                   vulcan.X() - ((img.getWidth() - vulcan.aW)>>1),
				   vulcan.Y() - ((img.getHeight() - vulcan.aH)>>1), 0
				 );

	       }
                   else
		   {
//                     if (client_sb.vulcan[v].traceX[0] != -1 && client_sb.vulcan[v].exploionAction){
		     if ((vulcan.explosive && vulcan.isExplosiveEnd())){
//		       System.out.println("Ph:"+client_sb.vulcan[v].phase+", vcounter:"+client_sb.vulcan[v].vcounter+",x="+client_sb.vulcan[v].X());
//                       int idx = client_sb.vulcan[v].VCOUNTERMAX>>1;
//		       idx = (client_sb.vulcan[v].vcounter - idx)*6/*seq.len.* //idx;
//		       if(idx>5)idx = 5;
		       gBuffer.drawImage( Elements[IMG_BURST01 + (ticks&3)], vulcan.X(), vulcan.Y(),0);

		     } else {
                       for (int i = 0; i < vulcan.traceX.length; i++)
                       {
                        int tx = vulcan.traceX[i];
                        int ty = vulcan.traceY[i];
                        if (tx != -1)
                            gBuffer.fillRect(tx, ty, 2, 2);
                       }
		       gBuffer.drawImage( Elements[IMG_STONE], vulcan.X(), vulcan.Y(),0);
                     }
		   }
            }
        }


        // Helicopter base
        if (client_sb.screenX < client_sb.level.length - client_sb.HELBASE_WIDTH)
        {
            int lx = client_sb.level.length - client_sb.HELBASE_WIDTH;
//            gBuffer.setColor(Color.yellow);
//            gBuffer.fillRect(client_sb.getScreenX(lx), client_sb.H - 4, client_sb.HELBASE_WIDTH * 2, 4);
            gBuffer.drawImage(Elements[IMG_HEL_STAGE], client_sb.getScreenX(lx), client_sb.H - 4,0);
        }

        // Helicopter explosion
        if (client_sb.helExplosion && gameStatus != Crashed)
        {
            int zx = client_sb.hel.X() + client_sb.hel.WIDTH() / 2;
            int zy = client_sb.hel.Y() + client_sb.hel.HEIGHT() / 2;
	    img = Elements[IMG_BURST_HEL01+client_sb.helExplosionCounter % 6];

            gBuffer.drawImage(img,zx - (img.getWidth()>>1), zy - (img.getHeight()>>1),0);
        }


////////////////////////////////////////////////
//      if(baseX !=0 || baseY !=0){
/*
        if(HEIGHT_LIMIT>VisHeight){
	  gBuffer.translate(-baseX , -base_dy);
        } else
        gBuffer.translate(-baseX,-baseY);
*/
        gBuffer.translate(-gBuffer.getTranslateX(),-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,scrwidth,scrheight);
//      }

        // Peoples left / score

        gBuffer.setColor(0x0);
        // Flying people ;-)
	int saved_ppl = 0;
        for (int i = 0; i < client_sb.flypp.length; i++)
        {
            gBuffer.drawRect(client_sb.W - 10 +baseX, 2 + i * 8 +baseY, 8, 8);
            if (client_sb.flypp[i] != null){
             gBuffer.drawImage(Elements[IMG_MAN01],client_sb.W - 8 +baseX, 4 + i * 8+baseY,0);
	     saved_ppl++;
            }
        }

	// Peoples left
//        gBuffer.drawString("Peoples left:" + client_sb.peoples_left, 2,f.getHeight(), (Graphics.LEFT|Graphics.TOP));
	img = Elements[IMG_MAN01];
	Image img2 = Elements[IMG_HEL_ICO];
	int y=/*baseY+VisHeight - */(Math.max(img.getHeight(),img2.getHeight())>>1)+baseY;
	gBuffer.drawImage(img,baseX,y - (img.getHeight()>>1),0);
	font.drawDigits(gBuffer,baseX+img.getWidth(),y - (font.fontHeight>>1),2,client_sb.peoples_left - saved_ppl);

//	font.drawDigits(gBuffer,baseX+((VisWidth - font.fontWidth*5)>>1),baseY +VisHeight - font.fontHeight -1,5,client_sb.score);

	// life
	y -= (img2.getHeight()>>1);
	int dx = img2.getWidth()+2, x = baseX + ((VisWidth - (client_sb.lives+1)*dx)>>1);
	if(gameStatus != Crashed || blink)
	  for (int i=0 ; i < client_sb.lives-1; i++,x+=dx)
	   gBuffer.drawImage(img2,x,y,0);

	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(Elements[IMG_DEMO], scr2,VisHeight-VisHeight/4,Graphics.HCENTER|Graphics.VCENTER);
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
	                  if (colorness){
	                      g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
			      g.setColor(0xFFFF00);    // drawin' flyin' text colour
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
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       direct=hel_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
       client_sb.nextGameStep(client_pmr);
       repaintBackScreen();
       gameStatus=newStage;
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
private static final int IMG_BURST_HEL01 = 4;
private static final int IMG_BURST_HEL02 = 5;
private static final int IMG_BURST_HEL03 = 6;
private static final int IMG_BURST_HEL04 = 7;
private static final int IMG_BURST_HEL05 = 8;
private static final int IMG_BURST_HEL06 = 9;
private static final int IMG_HEL_LEFT01 = 10;
private static final int IMG_HEL_LEFT02 = 11;
private static final int IMG_HEL_RIGHT01 = 12;
private static final int IMG_HEL_RIGHT02 = 13;
private static final int IMG_MAN01 = 14;
private static final int IMG_MAN02 = 15;
private static final int IMG_STONE = 16;
private static final int IMG_HEL_ICO = 17;
private static final int IMG_HEL_STAGE = 18;
private static final int IMG_BURST01 = 19;
private static final int IMG_BURST02 = 20;
private static final int IMG_BURST03 = 21;
private static final int IMG_BURST04 = 22;
private static final int IMG_SMOKE01 = 23;
private static final int IMG_SMOKE02 = 24;
private static final int IMG_SMOKE03 = 25;
private static final int IMG_SMOKE04 = 26;
private static final int IMG_SMOKE05 = 27;
private static final int IMG_NFONT = 28;
private static final int IMG_MAP = 29;
private static final int TOTAL_IMAGES_NUMBER = 30;
}

