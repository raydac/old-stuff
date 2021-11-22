
import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.GameKit_C6B333.Squirrel.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable, LoadListener , GameActionListener {
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
  protected GameletImpl client_sb;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private boolean useDblBuffer = !this.isDoubleBuffered();

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
  private int [] stageSleepTime = {100,100,100,100,120,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

//  private final static int BkgCOLOR = 0x000061; //for Nokia
  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int scr2, center_x, center_y, start_x,start_y;

  private int PLAYER_COLOR;
  private int ENEMY_COLOR;

  private int FLOOR_COLOR;// = 0x737373;


  // fastaccess index
  // GOLEFT   GORIGHT   GOTOP   GODOWN     DEATH     DROP  BEATJUMPLEFT BEATJUMPRIGHT APPEARANCE  UNDERFIRE
//    // squirrell
/*
  short [][] fastaccess = new short[]{
    new short[]{ //squirell
       IMG_S_JUMP_L01,IMG_S_JUMP_R01,IMG_S_UP01,IMG_S_DOWN01,IMG_S_DEAD01,IMG_S_FALL01,IMG_S_DROP_L01,IMG_S_DROP_R01,IMG_APPEAR01,IMG_S_UNDER_ATTACK_LEFT
    }
  };
*/
/*

    public static final int OBJECT_SQUIRREL = 0;
    public static final int OBJECT_LOGGER = 1;
    public static final int OBJECT_OWL = 2;
    public static final int OBJECT_NUT = 3;
    public static final int OBJECT_CATERPILLAR = 4;
    public static final int OBJECT_BEE = 5;
    public static final int OBJECT_BEETLE = 6;
    public static final int OBJECT_HUNTER = 7;
    public static final int OBJECT_BULLET = 8;
*/
                              // GOLEFT       GORIGHT        GOTOP            GODOWN          DEATH            DROP            BEATJUMPLEFT    BEATJUMPRIGHT   APPEARANCE   UNDERFIRE
short [] squirrel = new short[]{ IMG_S_JUMP_L01,IMG_S_JUMP_R01,IMG_S_UP01,    IMG_S_DOWN01,   IMG_S_DEAD01,    IMG_S_FALL01,   IMG_S_JUMP_L01, IMG_S_JUMP_R01, IMG_APPEAR01,IMG_S_UNDER_ATTACK_LEFT};

short [] owl    = new short[]{ IMG_OWL_SIT01, IMG_OWL_SIT01, IMG_OWL_SIT01,   IMG_OWL_SIT01,  IMG_OWL_DEAD01,  IMG_OWL_SIT01,  IMG_OWL_AT_L01, IMG_OWL_AT_R01, IMG_APPEAR01,IMG_OWL_DEAD01};
short [] caterp = new short[]{ IMG_WORM01,    IMG_WORM01,    IMG_WORM01,      IMG_WORM01,     IMG_WORM_DEAD01, IMG_WORM01,     IMG_WORM_AT_L01,IMG_WORM_AT_R01,IMG_APPEAR01,IMG_WORM_DEAD01};
short [] bee    = new short[]{ IMG_BEE_L01,   IMG_BEE_R01,   IMG_BEE_L01,     IMG_BEE_L01 ,   IMG_BEE_DEAD01,  IMG_BEE_L01,    IMG_BEE_AT_L,   IMG_BEE_AT_R ,  IMG_APPEAR01,IMG_BEE_DEAD01};
short [] beetle = new short[]{ IMG_BUG_L01,   IMG_BUG_R01,   IMG_BUG_UP_01,   IMG_BUG_DOWN_01,IMG_BUG_DEAD01,  IMG_BUG_DOWN_01,IMG_BUG_AT_L01, IMG_BUG_AT_R01, IMG_APPEAR01,IMG_BUG_DEAD01};
short [] hunter = new short[]{ IMG_HUNTER_L01,IMG_HUNTER_R01,IMG_HUNTER_AT_L01,IMG_HUNTER_L01,IMG_HUNTER_DEAD01,IMG_HUNTER_AT_L01,IMG_HUNTER_AT_L01,IMG_HUNTER_AT_R01,IMG_APPEAR01,IMG_HUNTER_DEAD01};


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

    client_sb = new GameletImpl(VisWidth, VisHeight, this);
    if (useDblBuffer)
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

//    center_x = (VisWidth - CELL_SIZE)>>1;
//    center_y = (VisHeight - CELL_SIZE)>>1;

    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
	FLOOR_COLOR = 0x008000;
	PLAYER_COLOR = 0xFF0000;
	ENEMY_COLOR = 0x1111AA;

    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
	PLAYER_COLOR = 0x000000;
	ENEMY_COLOR = 0x000000;
    }
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
	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;

           Runtime.getRuntime().gc();

//           BackScreen=Image.createImage(bsWIDTH ,bsHEIGHT);
//           gBackScreen=BackScreen.getGraphics();

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
//     int i = Stages.ab_stages[0];
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
       direct=GameletImpl.PLAYER_BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
//	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(direct);
//	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
           stageSleepTime[playGame] = client_sb.i_GameTimeDelay;
//	   client_sb.initStage(stage);
//           client_sb.nextGameStep(direct);
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
				     case Canvas.KEY_NUM1 : direct = GameletImpl.PLAYER_BUTTON_JUMPLEFT; break;
				     case Canvas.KEY_NUM2 : direct = GameletImpl.PLAYER_BUTTON_UP; break;
				     case Canvas.KEY_NUM3 : direct = GameletImpl.PLAYER_BUTTON_JUMPRIGHT; break;
				     case Canvas.KEY_NUM4 : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = GameletImpl.PLAYER_BUTTON_FIRE; break;
				     case Canvas.KEY_NUM6 : direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = GameletImpl.PLAYER_BUTTON_UP; break;
					       case Canvas.DOWN : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
					       case Canvas.FIRE : direct = GameletImpl.PLAYER_BUTTON_FIRE; break;
					           default: direct=GameletImpl.PLAYER_BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=GameletImpl.PLAYER_BUTTON_NONE;
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
//			   if (client_sb.bulgar.getDirection()==GameObject.DIR_STOP && (ticks%3 == 0))
//			     client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_DOWN/*4*/);
		           client_sb.nextGameStep(GameletImpl.PLAYER_BUTTON_RIGHT);

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
		                direct=GameletImpl.PLAYER_BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
                                client_sb.nextGameStep(direct);
				//repaintBackScreen();
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
			   client_sb.nextGameStep(direct);
//			   if(!is_painting)
//			     updateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

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
			   if(ticks>1){
			     ticks = 0;
			     blink = false;
		             direct=GameletImpl.PLAYER_BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=GameletImpl.PLAYER_BUTTON_NONE;
                                   client_sb.nextGameStep(direct);
				   //repaintBackScreen();
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

	serviceRepaints();
	workDelay = System.currentTimeMillis();
//        if(is_painting) serviceRepaints();

      }
    }


    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
//	 switch(actionID) {
//	 }
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


    protected void DoubleBuffer(Graphics gBuffer){

      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,VisWidth,VisHeight); //see Nokia's defect list
      }

////////////////////////////////////////////////
        gBuffer.setColor(FLOOR_COLOR);gBuffer.fillRect(0,0,VisWidth,VisHeight);

        Image img = null;

  // view correction

        int playerX = client_sb.p_Player.i8_ScreenX >> 8;
        int playerY = client_sb.p_Player.i8_ScreenY >> 8;

	int visX = Math.max(playerX + (((client_sb.p_Player.i8_width>>8) - VisWidth)>>1),0);
	int visY = Math.max(playerY + (((client_sb.p_Player.i8_height>>8) - VisHeight)>>1),0);
	visX = Math.min(visX,client_sb.i_StageWidth*GameletImpl.VIRTUALCELL_WIDTH - VisWidth);
	visY = Math.min(visY,client_sb.i_StageHeigh*GameletImpl.VIRTUALCELL_HEIGHT - VisHeight);

	int visXend = visX + VisWidth;
	int visYend = visY + VisHeight;

        gBuffer.translate(-visX,-visY);
//        gBuffer.setClip(visX,visY,VisWidth,VisHeight); //see Nokia's defect list


	int cellX = Math.max(visX / GameletImpl.VIRTUALCELL_WIDTH,0);
	int _ofsX = 0;//visX % GameletImpl.VIRTUALCELL_WIDTH - GameletImpl.VIRTUALCELL_WIDTH;
	int cellY = Math.max(visY / GameletImpl.VIRTUALCELL_HEIGHT,0);
	int ofsY =  0;//visY % GameletImpl.VIRTUALCELL_HEIGHT - GameletImpl.VIRTUALCELL_HEIGHT;

        int i_wdth = client_sb.i_StageWidth;
        int i_hght = client_sb.i_StageHeigh;
	int cellsH = Math.min((VisWidth + GameletImpl.VIRTUALCELL_WIDTH -1) / GameletImpl.VIRTUALCELL_WIDTH +1, i_wdth);
	int cellsV = Math.min((VisHeight + GameletImpl.VIRTUALCELL_HEIGHT -1) / GameletImpl.VIRTUALCELL_HEIGHT +1,i_hght);

	int ofsX, zone_width = client_sb.i_StageWidth - cellsH;


	int stage_ptr = cellX + cellY * client_sb.i_StageWidth;


        // Drawing the labyrinth
        byte[] ab_stagearray = client_sb.ab_StageArray;
	int idx;

	if(cellY+cellsV > client_sb.i_StageHeigh)cellsV = client_sb.i_StageHeigh - cellY;
	if(cellX+cellsH > client_sb.i_StageWidth)cellsH = client_sb.i_StageWidth - cellX;
gBuffer.setColor(0xff0000);
        for (int ly = cellY; ly < cellY+cellsV; ly++)
        {
            for (int lx = cellX; lx < cellX+cellsH; lx++)
            {
		idx = ab_stagearray[lx + ly * i_wdth];

                switch (idx&0xf)
                {
                    case Stages.OBJECT_NONE: idx = -1;break;//continue;//gBuffer.setColor(0xffffff); break;
                    case Stages.OBJECT_BRANCH: idx = IMG_TREE_SM_BRANCH + ((idx>>4)&0xf);break;
                    case Stages.OBJECT_TRUNK:  idx = IMG_TREE_SM_LOG + ((idx>>4)&0xf);break;
                    case Stages.OBJECT_EARTH:  idx = IMG_GROUND;
		                              //gBuffer.setColor(0x0); break;
                }
		if(idx>0)
	        gBuffer.drawImage(Elements[idx],lx * GameletImpl.VIRTUALCELL_WIDTH + _ofsX, ly * GameletImpl.VIRTUALCELL_HEIGHT +ofsY,0);

//		gBuffer.drawRect(lx * GameletImpl.VIRTUALCELL_WIDTH + _ofsX, ly * GameletImpl.VIRTUALCELL_HEIGHT +ofsY, GameletImpl.VIRTUALCELL_WIDTH, GameletImpl.VIRTUALCELL_HEIGHT);

//                gBuffer.fillRect(lx * GameletImpl.VIRTUALCELL_WIDTH + _ofsX, ly * GameletImpl.VIRTUALCELL_HEIGHT +ofsY, GameletImpl.VIRTUALCELL_WIDTH, GameletImpl.VIRTUALCELL_HEIGHT);
            }
        }

        int i_x,i_y;
        Sprite p_spr;
        // Drawing object sprites
        for (int li = 0; li < client_sb.i_MaxSpriteNum; li++)
        {
            p_spr = client_sb.ap_ObjectSprites[li];

            if (p_spr.lg_SpriteActive)
            {
	      i_x = p_spr.i8_ScreenX >> 8;
	      i_y = p_spr.i8_ScreenY >> 8;
	      if(i_x >= visXend || i_y >= visYend ||
	         i_x < visX-20 || i_y < visY -20) continue;

                switch (p_spr.i_objectType)
                {
                    case Sprite.OBJECT_OWL:    idx = owl[p_spr.i_objectState]+p_spr.i_Frame;break;
                    case Sprite.OBJECT_NUT:    idx = IMG_NUT;break;
                    case Sprite.OBJECT_CATERPILLAR: idx = caterp[p_spr.i_objectState]+p_spr.i_Frame;break;
                    case Sprite.OBJECT_BEE:    idx = bee[p_spr.i_objectState]+p_spr.i_Frame; break;
                    case Sprite.OBJECT_BEETLE: idx = beetle[p_spr.i_objectState]+p_spr.i_Frame;break;
                    case Sprite.OBJECT_HUNTER:
		                               if( p_spr.i_objectState == Sprite.STATE_DROP &&
		                                   p_spr.i_previousState == Sprite.STATE_GORIGHT)
						     idx = IMG_HUNTER_AT_R01 + p_spr.i_Frame;
						  else
		                                      idx = hunter[p_spr.i_objectState]+p_spr.i_Frame;
					       break;
		    default: continue;
                }
		img = GetImage(idx);

		if(p_spr.i_objectState==Sprite.STATE_APPEARANCE){
		  i_x=i_x+(p_spr.i8_width>>9)-(img.getWidth()>>1);
		  i_y=i_y+(p_spr.i8_height>>9)-(img.getHeight()>>1);
		}

		gBuffer.drawImage(GetImage(idx),i_x,i_y,0);
//                gBuffer.drawRect(p_spr.i8_ScreenX >> 8, p_spr.i8_ScreenY >> 8, p_spr.i8_width >> 8, p_spr.i8_height >> 8);
            }
        }

        // Drawing the player object
	p_spr = client_sb.p_Player;
	idx = squirrel[p_spr.i_objectState] + p_spr.i_Frame;
	if(p_spr.i_objectState==Sprite.STATE_APPEARANCE){
		  playerX=playerX+(p_spr.i8_width>>9)-(img.getWidth()>>1);
		  playerY=playerY+(p_spr.i8_height>>9)-(img.getHeight()>>1);
	}
	gBuffer.drawImage(GetImage(idx),playerX,playerY,0);
/*
	gBuffer.setColor(0xffffff);
        gBuffer.drawRect(p_spr.i8_ScreenX >> 8, p_spr.i8_ScreenY >> 8, p_spr.i8_width >> 8, p_spr.i8_height >> 8);

	gBuffer.setColor(0x00ffff);
*/
/*
	int xx = p_spr.i8_mainX>>8;
	int yy = p_spr.i8_mainY>>8;
	gBuffer.drawLine(xx-3, yy, xx+3,yy);
	gBuffer.drawLine(xx, yy-3, xx,yy+3);

	gBuffer.setColor(0xff00ff);
	xx = p_spr.i8_ScreenX>>8;
	yy = p_spr.i8_ScreenY>>8;
	gBuffer.drawLine(xx-3, yy, xx+3,yy);
	gBuffer.drawLine(xx, yy-3, xx,yy+3);
*/
        // Drawing the dropped nut
        if (client_sb.p_DroppedNut.lg_SpriteActive)
        {
            i_x = client_sb.p_DroppedNut.i8_ScreenX >> 8;
            i_y = client_sb.p_DroppedNut.i8_ScreenY >> 8;
//            i_w = client_sb.p_DroppedNut.i8_width >> 8;
//            i_h = client_sb.p_DroppedNut.i8_height >> 8;
	    gBuffer.drawImage(GetImage(IMG_NUT),i_x, i_y,0);
        }

        // Drawing the hunt bullet
        if (client_sb.p_HunterBullet.lg_SpriteActive)
        {
            i_x = client_sb.p_HunterBullet.i8_ScreenX >> 8;
            i_y = client_sb.p_HunterBullet.i8_ScreenY >> 8;
//            i_w = client_sb.p_HunterBullet.i8_width >> 8;
//            i_h = client_sb.p_HunterBullet.i8_height >> 8;
	    gBuffer.drawImage(GetImage(IMG_BULLET),i_x, i_y,0);
        }
/*
        //Drawing info
        gBuffer.setColor(Color.black);
        gBuffer.drawString("   HN:"+client_sb.i_takenNutNumber+" TN:"+client_sb.i_currentNutNumber+" H:"+client_sb.p_Player.i_ObjectHealth,0,20);

	        gBuffer.setColor(Color.red);
		gBuffer.drawRect(fixX,fixY,128,128);
		gBuffer.setColor(Color.blue);
		gBuffer.drawRect(followX,followY,128,128);
*/

   gBuffer.translate(visX,visY);
//gBuffer.setClip(visX,visY,VisWidth,VisHeight); //see Nokia's defect list

//          if(!client_sb.lg_PlayerKilled)
	  {
	    img = GetImage(IMG_HEART);
	    int h = Math.max(BAR_HEIGHT,img.getHeight());
	    gBuffer.drawImage(img,0,(h - img.getHeight())>>1,0);
	    BAR(gBuffer,img.getWidth(),(h - BAR_HEIGHT)>>1,GameletImpl.I_PLAYERMAXHEALTH,client_sb.p_Player.i_ObjectHealth,PLAYER_COLOR);
          }

//	  if(client_sb.lg_bossDrivingMode)
         {
	    img = GetImage(IMG_NUT_ICO);
	    int h = Math.max(font.fontHeight,img.getHeight());
	    int h1 = VisHeight - h;
	    gBuffer.drawImage(img,0, h1 + ((h - img.getHeight())>>1),0);
	    font.drawDigits(gBuffer,img.getWidth(),h1 + ((h - font.fontHeight)>>1),2,client_sb.i_takenNutNumber);
	  }

       if(gameStatus!=Crashed || blink){
//        if(!client_sb.lg_PlayerKilled || (ticks&2)==0){
        // Lives
        img = GetImage(IMG_HEART);
	   for (int i=0;i<client_sb.i_Attemptions;i++)
              gBuffer.drawImage(img,VisWidth-(i+1)*(img.getWidth()+2)-10,VisHeight-img.getHeight(),0);
      }

/*********************/

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
//      if(baseX !=0 || baseY !=0){
        gBuffer.translate(-gBuffer.getTranslateX(),-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,scrwidth,scrheight);
//      }

	bufferChanged=false;
    }

boolean is_painting = false;
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    protected void paint(Graphics g) {
      is_painting = true;
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
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WON),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			    }
			    g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    break;

	      //case demoPlay:
              default :   if (useDblBuffer)
			      {
			        //if(bufferChanged)
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
			     else
			      {
		                DoubleBuffer(g);
			      }
                              g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
	  is_painting = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       direct=GameletImpl.PLAYER_BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
       stageSleepTime[playGame] = client_sb.i_GameTimeDelay;
       stage = client_sb.i_GameStage;
       client_sb.nextGameStep(direct);
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

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_HEART = 4;
private static final int IMG_NUT_ICO = 5;
private static final int IMG_BULLET = 7;
private static final int IMG_GROUND = 8;
private static final int IMG_TREE_SM_BRANCH = 9;
private static final int IMG_TREE_SM_BRANCH1 = 10;
private static final int IMG_TREE_SM_BRANCH2 = 11;
private static final int IMG_TREE_SM_BR_END_LEFT = 12;
private static final int IMG_TREE_SM_BR_END_RIGHT = 13;
private static final int IMG_TREE_BRANCH_DOWN = 14;
private static final int IMG_TREE_BRANCH_UP = 15;
private static final int IMG_TREE_SM_LOG = 16;
private static final int IMG_TREE_SM_TOP = 17;
private static final int IMG_TREE_SM_LOG_LEFT = 18;
private static final int IMG_TREE_SM_LOG_BOTH = 19;
private static final int IMG_TREE_SM_LOG_RIGHT = 20;
private static final int IMG_TREE_SM_ROOT = 21;
private static final int IMG_TREE_TOP_L = 22;
private static final int IMG_TREE_TOP_R = 23;
private static final int IMG_TREE_LOG_L = 24;
private static final int IMG_TREE_LOG_BR_L = 25;
private static final int IMG_TREE_LOG_BR_R = 26;
private static final int IMG_TREE_LOG_R = 27;
private static final int IMG_TREE_ROOT_L = 28;
private static final int IMG_TREE_ROOT_R = 29;
private static final int IMG_APPEAR01 = 30;
private static final int IMG_APPEAR02 = 31;
private static final int IMG_APPEAR03 = 32;
private static final int IMG_APPEAR04 = 33;
private static final int IMG_S_JUMP_L01 = 34;
private static final int IMG_S_JUMP_L02 = 35;
private static final int IMG_S_JUMP_L03 = 36;
private static final int IMG_S_JUMP_L04 = 37;
private static final int IMG_S_JUMP_R01 = 38;
private static final int IMG_S_JUMP_R02 = 39;
private static final int IMG_S_JUMP_R03 = 40;
private static final int IMG_S_JUMP_R04 = 41;
private static final int IMG_S_UP01 = 42;
private static final int IMG_S_UP02 = 43;
private static final int IMG_S_UP03 = 44;
private static final int IMG_S_UP04 = 45;
private static final int IMG_S_DOWN01 = 46;
private static final int IMG_S_DOWN02 = 47;
private static final int IMG_S_DOWN03 = 48;
private static final int IMG_S_DOWN04 = 49;
private static final int IMG_S_DEAD01 = 50;
private static final int IMG_S_DEAD02 = 51;
private static final int IMG_S_DEAD03 = 52;
private static final int IMG_S_DEAD04 = 53;
private static final int IMG_S_FALL01 = 54;
private static final int IMG_S_DROP_L01 = 55;
private static final int IMG_S_DROP_L02 = 56;
private static final int IMG_S_DROP_L03 = 57;
private static final int IMG_S_DROP_R01 = 58;
private static final int IMG_S_DROP_R02 = 59;
private static final int IMG_S_DROP_R03 = 60;
private static final int IMG_S_UNDER_ATTACK_LEFT = 61;
private static final int IMG_S_UNDER_ATTACK_RIGHT = 62;
private static final int IMG_OWL_SIT01 = 63;
private static final int IMG_OWL_DEAD01 = 64;
private static final int IMG_OWL_DEAD02 = 65;
private static final int IMG_OWL_DEAD03 = 66;
private static final int IMG_OWL_AT_L01 = 67;
private static final int IMG_OWL_AT_L02 = 68;
private static final int IMG_OWL_AT_R01 = 69;
private static final int IMG_OWL_AT_R02 = 70;
private static final int IMG_WORM01 = 71;
private static final int IMG_WORM02 = 72;
private static final int IMG_WORM03 = 73;
private static final int IMG_WORM_DEAD02 = 75;
private static final int IMG_WORM_AT_L01 = 78;
private static final int IMG_WORM_AT_L02 = 79;
private static final int IMG_WORM_AT_L03 = 80;
private static final int IMG_WORM_AT_R01 = 81;
private static final int IMG_WORM_AT_R02 = 82;
private static final int IMG_WORM_AT_R03 = 83;
private static final int IMG_BEE_L01 = 84;
private static final int IMG_BEE_L02 = 85;
private static final int IMG_BEE_R01 = 86;
private static final int IMG_BEE_R02 = 87;
private static final int IMG_BEE_DEAD01 = 88;
private static final int IMG_BEE_DEAD02 = 89;
private static final int IMG_BEE_DEAD03 = 90;
private static final int IMG_BEE_AT_L = 91;
private static final int IMG_BEE_AT_R = 92;
private static final int IMG_BUG_L01 = 93;
private static final int IMG_BUG_L02 = 94;
private static final int IMG_BUG_R01 = 95;
private static final int IMG_BUG_R02 = 96;
private static final int IMG_BUG_UP_01 = 97;
private static final int IMG_BUG_UP_02 = 98;
private static final int IMG_BUG_UP_03 = 99;
private static final int IMG_BUG_UP_04 = 100;
private static final int IMG_BUG_DOWN_01 = 101;
private static final int IMG_BUG_DOWN_02 = 102;
private static final int IMG_BUG_DOWN_03 = 103;
private static final int IMG_BUG_DOWN_04 = 104;
private static final int IMG_BUG_DEAD01 = 105;
private static final int IMG_BUG_DEAD02 = 106;
private static final int IMG_BUG_DEAD03 = 107;
private static final int IMG_BUG_AT_L01 = 108;
private static final int IMG_BUG_AT_L02 = 109;
private static final int IMG_BUG_AT_L03 = 110;
private static final int IMG_BUG_AT_R01 = 111;
private static final int IMG_BUG_AT_R02 = 112;
private static final int IMG_BUG_AT_R03 = 113;
private static final int IMG_HUNTER_L01 = 114;
private static final int IMG_HUNTER_L02 = 115;
private static final int IMG_HUNTER_L03 = 116;
private static final int IMG_HUNTER_R01 = 120;
private static final int IMG_HUNTER_R02 = 121;
private static final int IMG_HUNTER_R03 = 122;
private static final int IMG_HUNTER_DEAD01 = 126;
private static final int IMG_HUNTER_DEAD02 = 127;
private static final int IMG_HUNTER_DEAD03 = 128;
private static final int IMG_HUNTER_AT_L01 = 129;
private static final int IMG_HUNTER_AT_L02 = 130;
private static final int IMG_HUNTER_AT_L03 = 131;
private static final int IMG_HUNTER_AT_R01 = 133;
private static final int IMG_HUNTER_AT_R02 = 134;
private static final int IMG_HUNTER_AT_R03 = 135;
private static final int IMG_NFONT = 137;
private static final int IMG_NUT = 6;
private static final int IMG_WORM_DEAD01 = 74;
private static final int IMG_WORM_DEAD03 = 76;
private static final int IMG_WORM_DEAD04 = 77;
private static final int IMG_HUNTER_L04 = 117;
private static final int IMG_HUNTER_L05 = 118;
private static final int IMG_HUNTER_L06 = 119;
private static final int IMG_HUNTER_R04 = 123;
private static final int IMG_HUNTER_R05 = 124;
private static final int IMG_HUNTER_R06 = 125;
private static final int IMG_HUNTER_AT_L04 = 132;
private static final int IMG_HUNTER_AT_R04 = 136;
private static final int TOTAL_IMAGES_NUMBER = 138;

}

