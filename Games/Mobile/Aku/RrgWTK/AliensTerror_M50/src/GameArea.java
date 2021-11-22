import fire.*;

import javax.microedition.lcdui.*;
import com.itx.mbgame.GameObject;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.*;


public class GameArea extends Canvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = Game.LEVEL_QWH*Stage.LEVEL_HEIGHT;

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
  protected Game client_sb;
  protected game_PMR client_pmr;
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
  private int [] stageSleepTime = {100,100,100,100,80,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private int demoStage = 2;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int FLOOR_COLOR;// = 0x737373;
  private int PLAYER_COLOR;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int bsWIDTH, bsHEIGHT, bsFRAMES, bsLAST_FRAME, scrX, bsPOS;
  private static final int CELL_SIZE = Game.LEVEL_QWH;
  private static final int STAGE_HEIGHT = Stage.LEVEL_HEIGHT;
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

    client_sb = new Game(VisWidth, VisHeight, this);
    client_pmr = new game_PMR();

    if (useDblBuffer)
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    bsFRAMES = (VisWidth + CELL_SIZE-1)/CELL_SIZE +3;
    bsWIDTH =  bsFRAMES*CELL_SIZE;
    bsHEIGHT = STAGE_HEIGHT*CELL_SIZE;


    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth +1)>>1;
    baseY = (scrheight - VisHeight +1)>>1;

    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
	FLOOR_COLOR = 0x1180ff;
	PLAYER_COLOR = 0xff0000;
    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
	PLAYER_COLOR = 0x000000;
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
//	   font = new drawDigits(Elements[IMG_NFONT]);
	   ib = null;

           Runtime.getRuntime().gc();

           BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
           gBackScreen=BackScreen.getGraphics();

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
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    //if (demoStage >= Stage.TOTAL_STAGES || demoStage >= 5)
	        demoStage=2;
	    client_sb.initStage(demoStage);
	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(client_pmr);
	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
//           client_sb.nextGameStep(game_PMR.BUTTON_NONE);
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
				     case Canvas.KEY_NUM1 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM2 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM3 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = game_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = game_PMR.BUTTON_FIRE; break;
				     case Canvas.KEY_NUM6 : direct = game_PMR.BUTTON_RIGHT; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = game_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = game_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = game_PMR.BUTTON_UP; break;
				               case Canvas.FIRE : direct = game_PMR.BUTTON_FIRE; break;
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
			   ticks++; blink=(ticks&8)==0;
//			   client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_ANY);
			   client_pmr.i_Value = game_PMR.BUTTON_RIGHT;
		           client_sb.nextGameStep(client_pmr);
			   UpdateBackScreen();

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
		                direct=game_PMR.BUTTON_NONE;
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
			   if(direct==game_PMR.BUTTON_FIRE)direct=game_PMR.BUTTON_NONE;
			   UpdateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>=Stage.TOTAL_STAGES)
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
		             direct=game_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=game_PMR.BUTTON_NONE;
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



    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case Game.ACTION_HEALTHUPDATE:
		  {
		       int n = x-bsLAST_FRAME;
		       if(n<0 || n>=bsFRAMES) return;
		       DrawVerticalCell(x,(bsPOS+n+1)%bsFRAMES);
		  }
	 }
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



    private void DrawVerticalCell(int index,int bs_position){

	 int h = 0;
	 int [][] map = client_sb.cur_stage;
	 int item;
	 Image img;
	 int 	hh = gBackScreen.getClipHeight(), ww = gBackScreen.getClipWidth(),
	        xx = gBackScreen.getClipX(),      yy = gBackScreen.getClipY();
	 bs_position = (bs_position%bsFRAMES)*CELL_SIZE;

	 gBackScreen.setClip(bs_position,0,CELL_SIZE,bsHEIGHT);

	 gBackScreen.setColor(FLOOR_COLOR);
	 gBackScreen.fillRect(bs_position,0,CELL_SIZE,bsHEIGHT);

	 if(!(map == null || index < 0 || index >= map[0].length)) {
          for(int y = 0;y<map.length; y++){
	    item = map[y][index];
	    if(item>Stage.EMPTY && item<=Stage.PIPE_MIDL2) {
	      item += IMG_GROUND-1;
	      img = GetImage(item);
	      gBackScreen.drawImage(img,
	                                bs_position+((CELL_SIZE - img.getWidth())>>1),
				                  h+((CELL_SIZE - img.getHeight())>>1),0);
	    }
	    h+=CELL_SIZE;
          }
	 }
        gBackScreen.setClip(xx,yy,ww,hh);
    }

int stage_lenght;
int scrOffset;

    private int getScreenX(){
       int scr_offset = client_sb.player.X()*CELL_SIZE+client_sb.player.aX - (VisWidth>>1);
       if(scr_offset<0)scr_offset=0;
         else if(scr_offset+VisWidth>=stage_lenght)scr_offset = stage_lenght - VisWidth;
       return scr_offset;
    }

    private void repaintBackScreen(){
       stage_lenght = client_sb.cur_stage[0].length*CELL_SIZE;
       scrX = getScreenX();
       bsLAST_FRAME = scrX/CELL_SIZE;
       for(int i=0;i<bsFRAMES;i++)
		 DrawVerticalCell(bsLAST_FRAME+i,i);
       bsPOS = bsFRAMES-1;
       scrOffset = -(((bsPOS+1)%bsFRAMES)*CELL_SIZE + scrX%CELL_SIZE);
    }

    private void UpdateBackScreen(){
       scrX = getScreenX();
       int scrpos = scrX/CELL_SIZE;

	if(scrpos!=bsLAST_FRAME) {
	   int dx = scrpos - bsLAST_FRAME;
	   int step = dx>0?1:-1;

	   for (int i = 0 ; i != dx ; i+=step){
	     DrawVerticalCell(bsLAST_FRAME + (step>0?(bsFRAMES-1):-1),bsPOS);
	      bsPOS += step;
	      if(bsPOS<0)bsPOS+=bsFRAMES;
	        else if(bsPOS>=bsFRAMES)bsPOS-=bsFRAMES;
	     bsLAST_FRAME+=step;
	   }
//	   bsLAST_FRAME = scrpos;
           scrOffset = -(((bsPOS+1)%bsFRAMES)*CELL_SIZE/* + scrX%CELL_SIZE*/);
	}
//        scrOffset = -(((bsPOS+1)%bsFRAMES)*CELL_SIZE/* + scrX%CELL_SIZE*/);

//	   scrX = scr_offset;
    }

//int base_dy;
    protected void DoubleBuffer(Graphics gBuffer){

//      if(baseX !=0 || baseY !=0){
        gBuffer.setColor(BkgCOLOR);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,VisWidth,VisHeight); //see Nokia's defect list
//      }

////////////////////////////////////////////////


        Image img = null;

	// prepare screen
	gBuffer.setColor(FLOOR_COLOR);gBuffer.fillRect(0,0,VisWidth,HEIGHT_LIMIT);
//        gBuffer.translate(0,Game.LEVEL_QWH);

        GameObject player = client_sb.player;
        GameObject obj;
	int dx = scrX%CELL_SIZE;

	gBuffer.drawImage(BackScreen,scrOffset-dx,0,0);
	gBuffer.drawImage(BackScreen,scrOffset+bsWIDTH-dx,0,0);
	dx= scrX;

        // Draw player
	int dir = (player.getDirection() == com.itx.mbgame.GameObject.DIR_RIGHT) ? 4:0;
	int i = dir;
	switch (client_sb.player_state){
	  case Game.STEP_PHASE1:  i += IMG_PL_RUN_L01; break;
	  case Game.STEP_PHASE2:  i += IMG_PL_RUN_L02; break;
	  case Game.STEP_PHASE3:  i += IMG_PL_RUN_L03; break;
	  case Game.STEP_PHASE4:  i += IMG_PL_RUN_L04; break;
	  case Game.STEP_JUMP:    i += IMG_PL_JUMP_L02; break;
	     default: 	i += IMG_PL_JUMP_L01;
	}
	if(client_sb.PLAYER_DEAD_ANIM > 0)i = IMG_PL_DEAD_L01+dir+client_sb.PLAYER_DEAD_ANIM-1;
	img = GetImage(i);
	gBuffer.drawImage(img,player.X()*CELL_SIZE+player.aX-dx,
	                      player.Y()*CELL_SIZE+player.aY,0);
        // Player Shot
        for(int j=0;j<client_sb.shot.length;j++)
        {
	  obj = client_sb.shot[j];
         if(obj.isActive())
         {
	   gBuffer.setColor(0x0);
           gBuffer.fillRect(obj.X() * CELL_SIZE +obj.aX -dx,
	                    obj.Y() * CELL_SIZE +obj.aY,
			    client_sb.SHOT_WH,client_sb.SHOT_WH);
         }
        }

        // Player Shot
	  obj = client_sb.enemyshot;
         if(obj.isActive())
         {
	   gBuffer.setColor(0x0);
           gBuffer.fillRect(obj.X() * CELL_SIZE +obj.aX -dx,
	                    obj.Y() * CELL_SIZE +obj.aY,
			    client_sb.SHOT_WH,client_sb.SHOT_WH);
         }


        // Enemy
        for(int j=0;j<client_sb.enemy.length;j++)
        {
          // Фазы движения  obj.getState() : STEP_STOP,STEP_PHASE1,STEP_PHASE2
          // Картинка врага obj.getType()  : ENEMY_1,ENEMY_2,ENEMY_3,ENEMY_4
	  obj = client_sb.enemy[j];
	  if(obj.vDir>0){
	    i = IMG_BURST01 + (client_sb.ENEMY_BURN-obj.vDir)*6/*BURSTING*//client_sb.ENEMY_BURN;
	  } else
            if(obj.isActive())
             {
	       dir = (obj.getDirection() == com.itx.mbgame.GameObject.DIR_RIGHT) ? 2:0;
	       i = IMG_ENEMY1_L01 + obj.getType()*(IMG_ENEMY2_L01-IMG_ENEMY1_L01)+dir+obj.getState()-1;
             } else continue;
	    img = GetImage(i);
	    gBuffer.drawImage(img,obj.X()*CELL_SIZE +obj.aX -dx,
	                          obj.Y()*CELL_SIZE +obj.aY,
	 			  0);

        }

        if(client_sb.aircraft.isActive())
        {
	     gBuffer.drawImage(GetImage(IMG_UFO3),
	                           client_sb.aircraft.X()*CELL_SIZE +client_sb.aircraft.aX -dx,
	                           client_sb.aircraft.Y()*CELL_SIZE +client_sb.aircraft.aY,
				   0);

        }
//	gBuffer.translate(0,-Game.LEVEL_QWH);

          if(gameStatus != Crashed || blink){
	    img = GetImage(IMG_HEART);
	    int h = Math.max(BAR_HEIGHT,img.getHeight());
	    gBuffer.drawImage(img,0,(h - img.getHeight())>>1,0);
	    BAR(gBuffer,img.getWidth(),(h - BAR_HEIGHT)>>1,client_sb.MAXLIVE,client_sb.live,PLAYER_COLOR);
          }
//!
//gBuffer.drawString(player.X()+":"+player.Y(),VisWidth>>1,0,Graphics.HCENTER|Graphics.TOP);
	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
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
//      is_painting = true;
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
//	  is_painting = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       direct=game_PMR.BUTTON_NONE;
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
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_GROUND = 4;
private static final int IMG_GRASS01 = 5;
private static final int IMG_GRASS02 = 6;
private static final int IMG_PIPE_END_LEFT = 7;
private static final int IMG_PIPE_END_RIGHT = 8;
private static final int IMG_PIPE1 = 9;
private static final int IMG_BLOCK01 = 10;
private static final int IMG_TREE01 = 11;
private static final int IMG_TREE02 = 12;
private static final int IMG_TREE03 = 13;
private static final int IMG_HOUSE_BLOCK01 = 14;
private static final int IMG_HOUSE_BLOCK02 = 15;
private static final int IMG_HOUSE_BLOCK03 = 16;
private static final int IMG_HOUSE_BLOCK04 = 17;
private static final int IMG_HOUSE_BLOCK05 = 18;
private static final int IMG_HOUSE_BLOCK06 = 19;
private static final int IMG_HOUSE_BLOCK07 = 20;
private static final int IMG_HOUSE_ROOF = 21;
private static final int IMG_HEART = 22;
private static final int IMG_PIPE2 = 23;
private static final int IMG_PL_RUN_L01 = 24;
private static final int IMG_PL_RUN_L02 = 25;
private static final int IMG_PL_RUN_L03 = 26;
private static final int IMG_PL_RUN_L04 = 27;
private static final int IMG_PL_RUN01 = 28;
private static final int IMG_PL_RUN02 = 29;
private static final int IMG_PL_RUN03 = 30;
private static final int IMG_PL_RUN04 = 31;
private static final int IMG_PL_JUMP_L01 = 32;
private static final int IMG_PL_JUMP_L02 = 33;
private static final int IMG_PL_JUMP_L03 = 34;
private static final int IMG_PL_JUMP_L04 = 35;
private static final int IMG_PL_JUMP01 = 36;
private static final int IMG_PL_JUMP02 = 37;
private static final int IMG_PL_JUMP03 = 38;
private static final int IMG_PL_JUMP04 = 39;
private static final int IMG_PL_DEAD01 = 44;
private static final int IMG_PL_DEAD02 = 45;
private static final int IMG_PL_DEAD03 = 46;
private static final int IMG_PL_DEAD04 = 47;
private static final int IMG_PL_SHOOT_L01 = 48;
private static final int IMG_PL_SHOOT_L02 = 49;
private static final int IMG_PL_SHOOT01 = 52;
private static final int IMG_PL_SHOOT02 = 53;
private static final int IMG_ENEMY1_L01 = 54;
private static final int IMG_ENEMY1_L02 = 55;
private static final int IMG_ENEMY1_R01 = 56;
private static final int IMG_ENEMY1_R02 = 57;
private static final int IMG_ENEMY2_L01 = 58;
private static final int IMG_ENEMY2_L02 = 59;
private static final int IMG_ENEMY2_R01 = 60;
private static final int IMG_ENEMY2_R02 = 61;
private static final int IMG_ENEMY3_L01 = 62;
private static final int IMG_ENEMY3_L02 = 63;
private static final int IMG_ENEMY3_R01 = 64;
private static final int IMG_ENEMY3_R02 = 65;
private static final int IMG_ENEMY4_L01 = 66;
private static final int IMG_ENEMY4_L02 = 67;
private static final int IMG_ENEMY4_R01 = 68;
private static final int IMG_ENEMY4_R02 = 69;
private static final int IMG_BURST01 = 70;
private static final int IMG_BURST02 = 71;
private static final int IMG_BURST03 = 72;
private static final int IMG_BURST04 = 73;
private static final int IMG_BURST05 = 74;
private static final int IMG_BURST06 = 75;
private static final int IMG_UFO3 = 76;
private static final int IMG_PL_DEAD_L01 = 40;
private static final int IMG_PL_DEAD_L02 = 41;
private static final int IMG_PL_DEAD_L03 = 42;
private static final int IMG_PL_DEAD_L04 = 43;
private static final int IMG___DUMMY_01 = 50;
private static final int IMG___DUMMY_02 = 51;
private static final int TOTAL_IMAGES_NUMBER = 77;
}
