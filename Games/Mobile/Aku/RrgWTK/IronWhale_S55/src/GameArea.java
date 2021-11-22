import com.igormaznitsa.GameKit_C6B333.DeepDiving.*;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
//import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Melody;
//import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 101;
  private final static int HEIGHT_LIMIT = (GameletImpl.I8_SEASURFACE_OFFSET>>8) + 64 - GameletImpl.I_GROUND_OFFSET;

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
//  private drawDigits font;

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

  private final static int SKY_COLOR = 0x80FFFF;
  private final static int SEA_COLOR = 0x0000FF;

  private int PLAYER_COLOR;
  private int ENEMY_COLOR;
  private int OXYGEN_COLOR;
  private int FLOOR_COLOR;// = 0x737373;
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
	FLOOR_COLOR = 0x737373;
	PLAYER_COLOR = 0xFF0000;
	ENEMY_COLOR = 0x1111AA;
	OXYGEN_COLOR = 0x00EEEE;

    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
	PLAYER_COLOR = 0x000000;
	ENEMY_COLOR = 0x000000;
	OXYGEN_COLOR = 0x000000;
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
     int i = Stage.ai_Stage[0];
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
	   client_sb.initStage(stage);
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
				     case Canvas.KEY_NUM2 : direct = GameletImpl.PLAYER_BUTTON_TOP; break;
				     case Canvas.KEY_NUM3 : direct = GameletImpl.PLAYER_BUTTON_ROCKETFIRE; break;
				     case Canvas.KEY_NUM4 : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
				     case Canvas.KEY_NUM5 : direct = GameletImpl.PLAYER_BUTTON_TORPEDOFIRE; break;
				     case Canvas.KEY_NUM6 : direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = GameletImpl.PLAYER_BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = GameletImpl.PLAYER_BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = GameletImpl.PLAYER_BUTTON_TOP; break;
					       case Canvas.DOWN : direct = GameletImpl.PLAYER_BUTTON_DOWN; break;
					       case Canvas.FIRE : direct = GameletImpl.PLAYER_BUTTON_TORPEDOFIRE; break;
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

								       //stage++;
					                               //if (stage>Stages.TOTAL_STAGES)
								         gameStatus=Finished;
								       // else
					                               //  gameStatus=newStage;
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
      if(scrheight<80){
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

  // view correction

        int playerY = client_sb.p_PlayerSprite.i8_ScreenY >> 8;
        int playerD = playerY - VisHeight + (client_sb.p_PlayerSprite.i8_height>>8) + 25;

	int i_doublecell = Stage.BLOCKLENGTH<<1;
        int i_startCell = client_sb.i8_startViewScreen >>> 8;
        int i_bottomOffset = (GameletImpl.I8_SEASURFACE_OFFSET >>> 8)+64 -GameletImpl.I_GROUND_OFFSET;

if(playerD >0){
        if (i_bottomOffset>VisHeight){
	  int i = VisHeight + playerD - i_bottomOffset;
	  if (i>0) playerD -= i;
          gBuffer.translate(0,-playerD);
        } else playerD = 0;
} else playerD = 0;

        Image img = null;

	if(colorness){
	  gBuffer.setColor(SKY_COLOR);gBuffer.fillRect(0,0,VisWidth,(GameletImpl.I8_SEASURFACE_OFFSET >>> 8));
	  gBuffer.setColor(SEA_COLOR);gBuffer.fillRect(0,(GameletImpl.I8_SEASURFACE_OFFSET >>> 8),VisWidth,VisHeight-(GameletImpl.I8_SEASURFACE_OFFSET >>> 8)+playerD);
	} else {
        // Drawing the sea line
//        gBuffer.setColor(Color.blue);
	  gBuffer.setColor(0xFFFFFF); gBuffer.fillRect(0,0,VisWidth,VisHeight+playerD);
          gBuffer.setColor(0x000000); gBuffer.drawLine(0, GameletImpl.I8_SEASURFACE_OFFSET >>> 8, client_sb.i_ScreenWidth, GameletImpl.I8_SEASURFACE_OFFSET >>> 8);
	}


        // Drawing the bottom
        gBuffer.setColor(0x0);

//if((ticks&1)==0)
	{
//          i_startCell >>= 1;                                  // because map of ground is doubled
          int i_block_start = i_startCell / i_doublecell;
	  int i_block_offset = - (i_startCell % i_doublecell);
	  int i_block_end = i_block_start + (VisWidth + i_doublecell -1) / i_doublecell +1;

	  int cacheidx = Stage.ai_Stage[i_block_start>0?i_block_start-1:0];

          for (int lx = i_block_start; lx <= i_block_end && i_block_offset<VisWidth; lx++)
          {
	    int i = Stage.ai_Stage[lx];
	    if (i == cacheidx) cacheidx=-1;
	    if(i<Stage.BLOCK_NUMBER){
	      img = GetImage(IMG_GROUND01+i);
	      Elements[IMG_GROUND01+i] = img;
	      gBuffer.drawImage(img,i_block_offset, i_bottomOffset-img.getHeight(),0);
	    }
	    i_block_offset += i_doublecell;
          }
	  if(ByteArray!=null && cacheidx>=0 && ByteArray[IMG_GROUND01+cacheidx]!=null) Elements[IMG_GROUND01+cacheidx]=null;
	}

/*
else {
        int prev = 0;
        for (int lx = 0; lx <= client_sb.i_ScreenWidth; lx++)
        {
            int h = client_sb.getDepthAtWayPoint(lx + (i_startCell<<1));
	    int x = lx<<1;
	    gBuffer.drawLine(lx-1, prev,lx,i_bottomOffset-h +GameletImpl.I_GROUND_OFFSET);
	    prev = i_bottomOffset-h +GameletImpl.I_GROUND_OFFSET;
        }
}
*/


        int i8_screenOffset = client_sb.i8_startViewScreen;
	int i_img;

        // Drawing background objects
        for (int li = 0; li < GameletImpl.SPRITES_NUMBER; li++)
        {
            Sprite p_sprite = client_sb.ap_Sprites[li];
	    i_img = 3;

            int i_x = (p_sprite.i8_ScreenX - i8_screenOffset) >> 8;
            int i_y = p_sprite.i8_ScreenY >> 8;

            if (p_sprite.lg_SpriteActive)
            {
                switch (p_sprite.i_objectType)
                {
                    case Sprite.SPRITE_AIRCRAFTCARRIER:     if(client_sb.lg_BossKilled) continue;
		                                            i_img = IMG_SHIP02_01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_BLEBS:               i_img = IMG_BUBBLES01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_CHOPPERLEFT:         i_img = IMG_HELY_L0+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_CHOPPERRIGHT:        i_img = IMG_HELY_R0+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_EXPLOSIONAIR:        i_img = IMG_PEXPL0+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_EXPLOSIONSURFACE:    i_img = IMG_EXP_W_01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_EXPLOSIONUNDERWATER: i_img = IMG_BURST_W01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_ICEBERG:             i_img = IMG_ICEBERG;  break;
                    case Sprite.SPRITE_MINE:                i_img = IMG_MINE;
							    gBuffer.setStrokeStyle(Graphics.DOTTED);
							    int i = i_x+(p_sprite.i8_width>>9);
							    gBuffer.drawLine(i,i_y,i,HEIGHT_LIMIT);
		                                            gBuffer.setStrokeStyle(Graphics.SOLID);
							    break;
                    case Sprite.SPRITE_SUFRACESHIP:         i_img = IMG_SHIP01_01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_SUNKSHIP1:           i_img = IMG_LAMIN01+p_sprite.i_Frame;  break;
                    case Sprite.SPRITE_SUNKSHIP2:           i_img = IMG_SINK01;  break;
                    case Sprite.SPRITE_SUNKSHIP3:           i_img = IMG_SINK02;  break;
                    case Sprite.SPRITE_SUNKSHIP4:           i_img = IMG_SINK03;  break;
                    case Sprite.SPRITE_SURFACEBOMB:         i_img = IMG_AVIABOMB01;  break;
                    case Sprite.SPRITE_UNDERWATERBOMB:      i_img = IMG_BOMB01;  break;
		       default: continue;
                }

		img = GetImage(i_img);

//                gBuffer.drawRect(i_x, i_y, p_sprite.i8_width >> 8, p_sprite.i8_height >> 8);

                switch (p_sprite.i_Align & 0x0F) {
		    case Sprite.SPRITE_ALIGN_CENTER: i_x -= (img.getWidth() - (p_sprite.i8_width >> 8))>>1; break;
                    case Sprite.SPRITE_ALIGN_RIGHT:  i_x -= img.getWidth()-(p_sprite.i8_width >> 8); break;
//                    case SPRITE_ALIGN_LEFT:
                }
                switch (p_sprite.i_Align & 0xF0) {
		    case Sprite.SPRITE_ALIGN_CENTER: i_y -= (img.getHeight() - (p_sprite.i8_height >> 8))>>1; break;
//                    case SPRITE_ALIGN_TOP:    break;
                    case Sprite.SPRITE_ALIGN_DOWN:   i_y -= img.getHeight() - (p_sprite.i8_height >> 8); break;
                }

		gBuffer.drawImage(img,i_x,i_y,0);

                //gBuffer.setColor(p_colorFill);

                //gBuffer.setColor(p_colorBorder);
                //gBuffer.drawRect(i_x, i_y, p_sprite.i8_width >> 8, p_sprite.i8_height >> 8);
            }
        }

        // Drawing the player object
//        gBuffer.setColor(Color.pink);
       if(!client_sb.lg_PlayerKilled)// && gameStatus!=Crashed) || blink)
        gBuffer.drawImage(GetImage(IMG_SUBM01+(ticks&1)/*client_sb.p_PlayerSprite.i_Frame*/),(client_sb.p_PlayerSprite.i8_ScreenX - i8_screenOffset) >> 8, playerY,0);
//        gBuffer.drawRect((client_sb.p_PlayerSprite.i8_ScreenX - i8_screenOffset) >> 8, client_sb.p_PlayerSprite.i8_ScreenY >> 8, client_sb.p_PlayerSprite.i8_width >> 8, client_sb.p_PlayerSprite.i8_height >> 8);

        // Drawing the rocket object
        if (client_sb.p_RocketSprite.lg_SpriteActive)
        {
//            gBuffer.setColor(0x0/*Color.lightGray*/);
              gBuffer.drawImage(GetImage(IMG_ROCKET01),(client_sb.p_RocketSprite.i8_ScreenX - i8_screenOffset) >> 8, client_sb.p_RocketSprite.i8_ScreenY >> 8,0);
//            gBuffer.drawRect((client_sb.p_RocketSprite.i8_ScreenX - i8_screenOffset) >> 8, client_sb.p_RocketSprite.i8_ScreenY >> 8, client_sb.p_RocketSprite.i8_width >> 8, client_sb.p_RocketSprite.i8_height >> 8);
        }

        // Drawing the torpedo object
        if (client_sb.p_TorpedoSprite.lg_SpriteActive)
        {
//            gBuffer.setColor(0x0/*Color.lightGray*/);
              gBuffer.drawImage(GetImage(IMG_TORPED_H_01),(client_sb.p_TorpedoSprite.i8_ScreenX - i8_screenOffset) >> 8, client_sb.p_TorpedoSprite.i8_ScreenY >> 8,0);
//            gBuffer.drawRect((client_sb.p_TorpedoSprite.i8_ScreenX - i8_screenOffset) >> 8, client_sb.p_TorpedoSprite.i8_ScreenY >> 8, client_sb.p_TorpedoSprite.i8_width >> 8, client_sb.p_TorpedoSprite.i8_height >> 8);
        }


if(playerD >0) gBuffer.translate(0,playerD);

          if(!client_sb.lg_PlayerKilled){
	    img = GetImage(IMG_HEART);
	    int h = Math.max(BAR_HEIGHT,img.getHeight());
	    gBuffer.drawImage(img,0,(h - img.getHeight())>>1,0);
	    BAR(gBuffer,img.getWidth(),(h - BAR_HEIGHT)>>1,GameletImpl.INIT_PLAYER_HEALTH,client_sb.i_playerHealth,PLAYER_COLOR);
	    BAR(gBuffer,0,VisHeight-BAR_HEIGHT-2,GameletImpl.I8_MAX_OXYGEN_VALUE,client_sb.i8_player_OxygenValue,OXYGEN_COLOR);
          }
	  if(client_sb.lg_bossDrivingMode){
	    img = GetImage(IMG_HEART_BLUE);
	    int h = Math.max(BAR_HEIGHT,img.getHeight());
	    gBuffer.drawImage(img,VisWidth - img.getWidth(),(h - img.getHeight())>>1,0);
	    BAR(gBuffer,VisWidth - BAR_WIDTH-img.getWidth()-2,(h - BAR_HEIGHT)>>1,GameletImpl.INIT_BOSS_HEALTH,client_sb.i_BossHealth,ENEMY_COLOR);
	  }

//      if(gameStatus!=Crashed || blink){
        if(!client_sb.lg_PlayerKilled || (ticks&2)==0){
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

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_GROUND01 = 4;
private static final int IMG_GROUND02 = 5;
private static final int IMG_GROUND03 = 6;
private static final int IMG_GROUND04 = 7;
private static final int IMG_GROUND05 = 8;
private static final int IMG_GROUND06 = 9;
private static final int IMG_GROUND07 = 10;
private static final int IMG_GROUND08 = 11;
private static final int IMG_GROUND09 = 12;
private static final int IMG_GROUND10 = 13;
private static final int IMG_SUBM01 = 14;
private static final int IMG_SUBM02 = 15;
private static final int IMG_SHIP01_01 = 16;
private static final int IMG_SHIP01_02 = 17;
private static final int IMG_ICEBERG = 18;
private static final int IMG_MINE = 19;
private static final int IMG_AVIABOMB01 = 20;
private static final int IMG_BOMB01 = 21;
private static final int IMG_TORPED_H_01 = 22;
private static final int IMG_ROCKET01 = 23;
private static final int IMG_SHIP02_01 = 24;
private static final int IMG_SHIP02_02 = 25;
private static final int IMG_PEXPL0 = 26;
private static final int IMG_PEXPL1 = 27;
private static final int IMG_PEXPL2 = 28;
private static final int IMG_PEXPL3 = 29;
private static final int IMG_PEXPL4 = 30;
private static final int IMG_EXP_W_01 = 31;
private static final int IMG_EXP_W_02 = 32;
private static final int IMG_EXP_W_03 = 33;
private static final int IMG_EXP_W_04 = 34;
private static final int IMG_EXP_W_05 = 35;
private static final int IMG_EXP_W_06 = 36;
private static final int IMG_BURST_W01 = 37;
private static final int IMG_BURST_W02 = 38;
private static final int IMG_BURST_W03 = 39;
private static final int IMG_BURST_W04 = 40;
private static final int IMG_BURST_W05 = 41;
private static final int IMG_BURST_W06 = 42;
private static final int IMG_BURST_W07 = 43;
private static final int IMG_BUBBLES01 = 44;
private static final int IMG_BUBBLES02 = 45;
private static final int IMG_BUBBLES03 = 46;
private static final int IMG_BUBBLES04 = 47;
private static final int IMG_BUBBLES05 = 48;
private static final int IMG_HELY_L0 = 49;
private static final int IMG_HELY_L1 = 50;
private static final int IMG_HELY_L2 = 51;
private static final int IMG_HELY_L3 = 52;
private static final int IMG_HELY_R0 = 53;
private static final int IMG_HELY_R1 = 54;
private static final int IMG_HELY_R2 = 55;
private static final int IMG_HELY_R3 = 56;
private static final int IMG_LAMIN01 = 57;
private static final int IMG_LAMIN02 = 58;
private static final int IMG_LAMIN03 = 59;
private static final int IMG_LAMIN04 = 60;
private static final int IMG_SINK01 = 61;
private static final int IMG_SINK02 = 62;
private static final int IMG_SINK03 = 63;
private static final int IMG_HEART = 64;
private static final int IMG_HEART_BLUE = 65;
private static final int TOTAL_IMAGES_NUMBER = 66;
}

