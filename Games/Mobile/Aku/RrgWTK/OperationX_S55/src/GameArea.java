import com.igormaznitsa.GameKit_3.OperationX.*;

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
//  private Image BackScreen=null;
//  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected GameletImpl client_sb;
  protected PlayerMoveObject client_pmr;
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
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;


  /**Fast access tables*/
                              //  Appearance,     Left,                Right,             Death,              Fight,             Hide
  short [] pilotgunner = { IMG_HEL_MAN_SHOOT_L, IMG_HEL_MAN_SHOOT_L, IMG_HEL_MAN_SHOOT, IMG_HEL_MAN_DEAD01, IMG_HEL_MAN_SHOOT, IMG_HEL_MAN_FALL};
  short [] en1_sm =      { IMG_EN_SM1_L01,      IMG_EN_SM1_L01,      IMG_EN_SM1_R01,    IMG_EN_SM1_DEAD01,  IMG_EN_SM1_SHOOT};
  short [] en2_sm =      { IMG_EN_SM2_L01,      IMG_EN_SM2_L01,      IMG_EN_SM2_R01,    IMG_EN_SM2_DEAD01,  IMG_EN_SM2_SHOOT};
  short [] en1 =         { IMG_ENEMY1_L01,      IMG_ENEMY1_L01,      IMG_ENEMY1_R01,    IMG_ENEMY1_DEAD01,  IMG_ENEMY1_SHOOT};
  short [] en2 =         { IMG_ENEMY2_L01,      IMG_ENEMY2_L01,      IMG_ENEMY2_R01,    IMG_ENEMY2_DEAD01,  IMG_ENEMY2_SHOOT};
  short [] hel_h =       { IMG_HEL_L01,         IMG_HEL_L01,         IMG_HEL_R01,       IMG_BURST01,        IMG_BURST01};


  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

    VisWidth = Math.min(scrwidth,WIDTH_LIMIT);
    VisHeight = Math.min(scrheight,HEIGHT_LIMIT);

    client_sb = new GameletImpl(VisWidth, VisHeight, null);
    client_pmr = new PlayerMoveObject();

    if (!this.isDoubleBuffered())
    {
      System.out.println("not dbufferred");
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

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
       direct=PlayerMoveObject.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(0);
	    client_pmr.i_Button = direct;
            client_sb.nextGameStep(client_pmr);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
//           client_sb.nextGameStep(client_pmr);
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
				     case Canvas.KEY_NUM0 :
				     case Canvas.KEY_NUM1 : direct = PlayerMoveObject.BUTTON_RECHARGE; break;
				     case Canvas.KEY_NUM2 : direct = PlayerMoveObject.BUTTON_UP; break;
				     case Canvas.KEY_NUM3 : direct = PlayerMoveObject.BUTTON_RECHARGE; break;
				     case Canvas.KEY_NUM4 : direct = PlayerMoveObject.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = PlayerMoveObject.BUTTON_FIRE; break;
				     case Canvas.KEY_NUM6 : direct = PlayerMoveObject.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = PlayerMoveObject.BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = PlayerMoveObject.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = PlayerMoveObject.BUTTON_RIGHT; break;
					       case Canvas.FIRE : direct = PlayerMoveObject.BUTTON_FIRE; break;
					       case Canvas.UP   : direct = PlayerMoveObject.BUTTON_UP; break;
					       case Canvas.DOWN : direct = PlayerMoveObject.BUTTON_DOWN; break;
					           default: direct=PlayerMoveObject.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=PlayerMoveObject.BUTTON_NONE;
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
			   client_pmr.i_Button = direct;
		           client_sb.nextGameStep(client_pmr);
//			   UpdateBackScreen();

			   if (client_sb.i_PlayerState!=Gamelet.PLAYERSTATE_NORMAL || ticks>demoLimit) {
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
		                direct=PlayerMoveObject.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_pmr.i_Button = direct;
                                client_sb.nextGameStep(client_pmr);
//				repaintBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>30){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
	                   client_pmr.i_Button = direct;
			   if(direct==PlayerMoveObject.BUTTON_FIRE) direct=PlayerMoveObject.BUTTON_NONE;
                           client_sb.nextGameStep(client_pmr);
//			   UpdateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);
			                                               CleanSpace();
								       stage++;
					                               if (stage>=Stages.TOTAL_STAGES)
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
			   if(ticks>0){
			     ticks = 0;
			     blink = false;
		             direct=PlayerMoveObject.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            CleanSpace();
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=PlayerMoveObject.BUTTON_NONE;
	                           client_pmr.i_Button = direct;
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
      if(scrheight<=80){
        gBuffer.setColor(~color);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }

/*
   void DrawImage(Graphics g, GameObject go, int image){
      Image img = Elements[image];
      int y = go.Y() + (go.HEIGHT()>>1);
      g.drawImage(img,tX(go.X() + (go.WIDTH()>>1),y) - (img.getWidth()>>1), tY(y) - (img.getHeight()>>1),0);
   }
*/

    protected void DoubleBuffer(Graphics gBuffer){

      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
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
//	gBuffer.setColor(0xffffff);gBuffer.fillRect(0,0,VisWidth,VisHeight);
        int ln = (client_sb.I8_GROUND_Y_OFFSET >> 8)-3;
        img = GetImage(IMG_BACK);

	if(colorness){  gBuffer.setColor(0x80ffff);   gBuffer.fillRect(0,0,VisWidth,ln-img.getHeight());
	                gBuffer.setColor(0xffff80);   gBuffer.fillRect(0,ln,VisWidth,VisHeight-ln); }
	     else    {  gBuffer.setColor(0xffffff);   gBuffer.fillRect(0,ln-img.getHeight(),VisWidth,ln-img.getHeight());
	                                              gBuffer.fillRect(0,ln,VisWidth,VisHeight-ln); }

	for(int i =0;i<VisWidth;i+=img.getWidth())
           gBuffer.drawImage(img,i,ln-img.getHeight(),0);

	gBuffer.setColor(0x0);

	// drawing houses
	int scrx = client_sb.i8_startViewX>>8;
	int houses_x = -scrx % Stages.BLOCK_WIDTH, houses_y = client_sb.I8_GROUND_Y_OFFSET >> 8;
	int amount = (VisWidth+Stages.BLOCK_WIDTH-1-houses_x)/Stages.BLOCK_WIDTH;
//	int start_n = client_sb.getBlockNumberForXPosition();
        for(int i=0;i<amount;i++){
          int n = IMG_HOUSE01+client_sb.getBlockNumberForXPosition(scrx);
	  if(n<=IMG_HOUSE05){
	    img = GetImage(n);
	    Elements[n] = img;
	    gBuffer.drawImage(img, houses_x,houses_y-img.getHeight(),0);
	  }
	  scrx+=Stages.BLOCK_WIDTH;
	  houses_x+=Stages.BLOCK_WIDTH;
        }
	for(int i=IMG_HOUSE01;i<=IMG_HOUSE05;i++)if(ByteArray[i]!=null)Elements[i]=null;

        // Drawing enemy objects
	AnimeObject p_obj;
        int sort_pos = 0, sort_dist = -1;
        for (int li = 0; li < client_sb.ap_AnimationObjects.length; li++)
        {
            p_obj = client_sb.ap_AnimationObjects[li];
	    int type = IMG_DEMO;
            if (p_obj.lg_Active)
            {
                if (p_obj.lg_LinkedObject) continue;
		if(sort_pos<=li)
		{
		   sort_dist++;
		   sort_pos = client_sb.ap_AnimationObjects.length-1;
		}
		if(p_obj.i_line_index>sort_dist)
		{
		  client_sb.ap_AnimationObjects[li--] = client_sb.ap_AnimationObjects[sort_pos];
		  client_sb.ap_AnimationObjects[sort_pos--] = p_obj;
		  continue;

		}


                while (true)
                {
/*
		 // diag
		  if(!(p_obj.i_Type ==AnimeObject.OBJECT_PILOTGUNNER && p_obj.i_State==AnimeObject.STATE_HIDE) && p_obj.i_State>=AnimeObject.STATE_HIDE)
		     { System.out.println("Invalid State:"+p_obj.i_State+", type:"+p_obj.i_Type+",Frame:"+p_obj.i_Frame); break; }
*/

                    switch (p_obj.i_Type)
                    {
                        case AnimeObject.OBJECT_PILOTGUNNER:    type = pilotgunner[p_obj.i_State]+p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_TROOPER0:       type = (p_obj.i_line_index>=2?en1:en1_sm)[p_obj.i_State]+p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_TROOPER1:       type = (p_obj.i_line_index>=2?en1:en1_sm)[p_obj.i_State]+p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_TROOPER2:       type = (p_obj.i_line_index>=2?en2:en2_sm)[p_obj.i_State]+p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_HELYCOPTERHORZ: type = hel_h[p_obj.i_ObjectDirection]+p_obj.i_Frame; break;
                        case AnimeObject.OBJECT_HELYCOPTERVERT: type = IMG_HEL_FR01 + p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_TANK:           type = IMG_TANK01 + p_obj.i_Frame;  break;
                        case AnimeObject.OBJECT_AMMO:           type = IMG_BULLET;  break;
                        case AnimeObject.OBJECT_EXPLOSION:      type = IMG_BURST01 + p_obj.i_Frame; break;
                        case AnimeObject.OBJECT_FOUNTAIN:
                        case AnimeObject.OBJECT_SHOOTFIRE:
                        case AnimeObject.OBJECT_PLAYERSHOOT:    type = IMG_BOOM01 + p_obj.i_Frame; break;
                    }

                    int i_x = (p_obj.i8_ScreenX - client_sb.i8_startViewX) >> 8;
                    int i_y = p_obj.i8_ScreenY >> 8;

                    //gBuffer.fillRect(i_x, i_y, p_obj.i_Width, p_obj.i_Height);

		    if(type<TOTAL_IMAGES_NUMBER){
		      img = GetImage(type);
		      gBuffer.drawImage(img, i_x+((p_obj.i_Width-img.getWidth())>>1), i_y+((p_obj.i_Height-img.getHeight())>>1),0);
		    }

//                    g.setColor(Color.PINK);

                    for (int lf = 0; lf < 2; lf++)
                    {
                        AnimeObject p_flush = p_obj.ap_shotFireArray[lf];

                        if (p_flush != null && p_flush.lg_Active)
                        {

                            i_x = (p_flush.i8_ScreenX - client_sb.i8_startViewX) >> 8;
                            i_y = p_flush.i8_ScreenY >> 8;

                            //gBuffer.fillRect(i_x, i_y, p_flush.i_Width, p_flush.i_Height);
			    gBuffer.drawImage(GetImage(IMG_SHOOT01+(ticks&1)),i_x, i_y,0);
                        }
                    }

                    if (p_obj.p_linkObject != null)
                    {
                        p_obj = p_obj.p_linkObject;
                    }
                    else
                        break;
                }
            }
        }

        // Drawing player's flashes
//        g.setColor(Color.red);
        for (int li = 0; li < client_sb.ap_PlayersFlashes.length; li++)
        {
            p_obj = client_sb.ap_PlayersFlashes[li];
            if (!p_obj.lg_Active) continue;
            int i_x = (p_obj.i8_ScreenX - client_sb.i8_startViewX) >> 8;
            int i_y = p_obj.i8_ScreenY >> 8;
//            gBuffer.fillRect(i_x, i_y, p_obj.i_Width, p_obj.i_Height);
	    gBuffer.drawImage(GetImage(IMG_BOOM01+p_obj.i_Frame),i_x, i_y,0);
        }

        // Drawing the sight

        if (client_sb.p_PlayerGun.i_state != PlayerGun.STATE_CHARGING)
        {
//            g.setColor(Color.red);
//            gBuffer.drawRect(client_sb.p_PlayerGun.getSightX(), client_sb.p_PlayerGun.getSightY(), PlayerGun.SIGHT_FRAME_WIDTH, PlayerGun.SIGHT_FRAME_HEIGHT);
            gBuffer.drawImage(GetImage(IMG_SIGHTICO),client_sb.p_PlayerGun.getSightX(), client_sb.p_PlayerGun.getSightY(), 0);
        }

        // Drawing the gun
//        g.setColor(Color.yellow);
//        gBuffer.fillRect(client_sb.p_PlayerGun.getGunX(), client_sb.p_PlayerGun.getGunY(), 20, 20);
        PlayerGun p_gun = client_sb.p_PlayerGun;
	int type = ((p_gun.getState()==PlayerGun.STATE_CHARGING)
			               ?
                          IMG_RELOAD01 : IMG_GUN01)
			     + p_gun.getFrame();
	img = GetImage(type);
	if(Elements[type]==null){
	  for(int i=IMG_GUN01;i<=IMG_RELOAD03;i++) if(ByteArray[i]!=null) Elements[i]=null;
	  Elements[type]=img;
	}
        gBuffer.drawImage(img, p_gun.getGunX(),p_gun.getGunY(),0);

        // Drawing bullets
//        g.setColor(Color.black);
//        gBuffer.drawString("" + client_sb.p_PlayerGun.i_bulletsingun+","+client_sb.i_currentPlayerHealth, 10, 10);

        // Drawing Hit
        if (client_sb.i_lastHit_x>=0)
        {
//            g.setColor(Color.red);
	    img = GetImage(IMG_HOLLOW);
	    gBuffer.drawImage(img,client_sb.i_lastHit_x-(img.getWidth()>>1),client_sb.i_lastHit_y-(img.getHeight()>>1),0);

        }

          img = GetImage(IMG_BULLET);
          gBuffer.drawImage(img,2,VisHeight - 2 -img.getHeight(),0);
	  font.drawDigits(gBuffer,3+img.getWidth(), VisHeight - 2 -((img.getHeight() + font.fontHeight)>>1),3,p_gun.i_bulletsingun);

	  BAR(gBuffer,VisWidth - 10 - BAR_WIDTH - 2,VisHeight-2-BAR_HEIGHT,client_sb.I_PLAYER_MAX_HEALT,client_sb.i_currentPlayerHealth,0xff0000);


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
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WIN),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
       direct=PlayerMoveObject.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Button = direct;
       client_sb.nextGameStep(client_pmr);
//       repaintBackScreen();
       gameStatus=newStage;
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
private static final byte IMG_WIN = 2;
private static final byte IMG_DEMO = 3;
private static final byte IMG_EN_SM1_L01 = 4;
private static final byte IMG_EN_SM1_L02 = 5;
private static final byte IMG_EN_SM1_R01 = 8;
private static final byte IMG_EN_SM1_R02 = 9;
private static final byte IMG_EN_SM1_DEAD01 = 12;
private static final byte IMG_EN_SM1_DEAD02 = 13;
private static final byte IMG_EN_SM1_DEAD03 = 14;
private static final byte IMG_EN_SM1_SHOOT = 16;
private static final byte IMG_EN_SM2_L01 = 17;
private static final byte IMG_EN_SM2_L02 = 18;
private static final byte IMG_EN_SM2_R01 = 21;
private static final byte IMG_EN_SM2_R02 = 22;
private static final byte IMG_EN_SM2_DEAD01 = 25;
private static final byte IMG_EN_SM2_DEAD02 = 26;
private static final byte IMG_EN_SM2_DEAD03 = 27;
private static final byte IMG_EN_SM2_SHOOT = 29;
private static final byte IMG_ENEMY1_L01 = 30;
private static final byte IMG_ENEMY1_L02 = 31;
private static final byte IMG_ENEMY1_L03 = 32;
private static final byte IMG_ENEMY1_L04 = 33;
private static final byte IMG_ENEMY1_R01 = 34;
private static final byte IMG_ENEMY1_R02 = 35;
private static final byte IMG_ENEMY1_R03 = 36;
private static final byte IMG_ENEMY1_R04 = 37;
private static final byte IMG_ENEMY1_DEAD01 = 38;
private static final byte IMG_ENEMY1_DEAD02 = 39;
private static final byte IMG_ENEMY1_DEAD03 = 40;
private static final byte IMG_ENEMY1_SHOOT = 42;
private static final byte IMG_ENEMY2_L01 = 43;
private static final byte IMG_ENEMY2_L02 = 44;
private static final byte IMG_ENEMY2_L03 = 45;
private static final byte IMG_ENEMY2_L04 = 46;
private static final byte IMG_ENEMY2_R01 = 47;
private static final byte IMG_ENEMY2_R02 = 48;
private static final byte IMG_ENEMY2_R03 = 49;
private static final byte IMG_ENEMY2_R04 = 50;
private static final byte IMG_ENEMY2_DEAD01 = 51;
private static final byte IMG_ENEMY2_DEAD02 = 52;
private static final byte IMG_ENEMY2_DEAD03 = 53;
private static final byte IMG_ENEMY2_SHOOT = 55;
private static final byte IMG_HEL_L01 = 56;
private static final byte IMG_HEL_L02 = 57;
private static final byte IMG_HEL_R01 = 58;
private static final byte IMG_HEL_R02 = 59;
private static final byte IMG_HEL_FR01 = 60;
private static final byte IMG_HEL_FR02 = 61;
private static final byte IMG_GUN01 = 62;
private static final byte IMG_GUN02 = 63;
private static final byte IMG_GUN03 = 64;
private static final byte IMG_GUN04 = 65;
private static final byte IMG_GUN05 = 66;
private static final byte IMG_RELOAD01 = 67;
private static final byte IMG_RELOAD02 = 68;
private static final byte IMG_RELOAD03 = 69;
private static final byte IMG_HOUSE01 = 70;
private static final byte IMG_HOUSE02 = 71;
private static final byte IMG_HOUSE03 = 72;
private static final byte IMG_HOUSE04 = 73;
private static final byte IMG_HOUSE05 = 74;
private static final byte IMG_TANK01 = 75;
private static final byte IMG_TANK02 = 76;
private static final byte IMG_HEL_MAN_SHOOT = 77;
private static final byte IMG_HEL_MAN_SHOOT_L = 78;
private static final byte IMG_HEL_MAN_DEAD01 = 79;
private static final byte IMG_HEL_MAN_DEAD02 = 80;
private static final byte IMG_HEL_MAN_DEAD03 = 81;
private static final byte IMG_HEL_MAN_FALL = 82;
private static final byte IMG_BURST01 = 83;
private static final byte IMG_BURST02 = 84;
private static final byte IMG_BURST03 = 85;
private static final byte IMG_BURST04 = 86;
private static final byte IMG_BURST05 = 87;
private static final byte IMG_BOOM01 = 88;
private static final byte IMG_BOOM02 = 89;
private static final byte IMG_BOOM03 = 90;
private static final byte IMG_SHOOT01 = 91;
private static final byte IMG_SHOOT02 = 92;
private static final byte IMG_SIGHTICO = 93;
private static final byte IMG_BULLET = 94;
private static final byte IMG_HOLLOW = 95;
private static final byte IMG_BACK = 96;
private static final byte IMG_NFONT = 97;
private static final byte IMG___TMP1_L03 = 6;
private static final byte IMG___TMP1_L04 = 7;
private static final byte IMG___TMP1_R03 = 10;
private static final byte IMG___TMP1_R04 = 11;
private static final byte IMG___TMP1_DEAD04 = 15;
private static final byte IMG___TMP2_L03 = 19;
private static final byte IMG___TMP2_L04 = 20;
private static final byte IMG___TMP2_R03 = 23;
private static final byte IMG___TMP2_R04 = 24;
private static final byte IMG___TMP2_DEAD04 = 28;
private static final byte IMG___TMP3_DEAD04 = 41;
private static final byte IMG___TMP4_DEAD04 = 54;
private static final byte TOTAL_IMAGES_NUMBER = 98;
}

