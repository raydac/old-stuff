
import javax.microedition.lcdui.*;
//import com.GameKit_3.SantaClaus.*;
//import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable {//, GameActionListener {
//String Error="no error";
/*=============================*/

    private static final int KEY_MENU = -7;
    private static final int KEY_CANCEL = -6;
    private static final int KEY_ACCEPT = -7;
    private static final int KEY_BACK = -10;

    public void setScreenLight(boolean state)
    {
      DeviceControl.setLights(0, state?100:0);
    }

    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               sounds[i].stop();
      is_first_show = false;
    }
    public void PlayMelody(int n){
      stopAllMelodies();
      if(midlet._Sound && sounds!=null)
          sounds[n].play(1);
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
  private byte[][] ByteArray;
  private Sound []sounds;

  private Image MenuIcon;
//  private Image Buffer=null;
//  private Graphics gBuffer=null;
//  private Image BackScreen=null;
//  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
//  public String unpacking = null;
//  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected GameletImpl client_sb;
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
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  int visHeight;
  int V_QUEUE;
  private final static int MAX_HEIGHT = 128;
  private final static int BOTTOM_GAP = 15;
  private final static int BACKGROUND_COLOR = 0x0D004C;
  private final static int BkgCOLOR = 0x000061;
  private int PIPE_WIDTH;
  private int I8_PIPE_X;
  private int PIPE_Y;

  boolean fire_pressed;
  boolean is_first_show;

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    visHeight = Math.min(scrheight,80);
//    client_sb = new GameletImpl(scrwidth,visHeight);

//    Buffer=Image.createImage(scrwidth,scrheight);
//    gBuffer=Buffer.getGraphics();

//    BackScreen=Image.createImage(scrwidth,scrheight);
//    gBackScreen=BackScreen.getGraphics();
    gameStatus=notInitialized;
//    baseX = ((scrwidth - GameletImpl.FIELD_WIDTH*cellW)>>1)+1;
    baseY = (scrheight - visHeight)>>1;
    is_first_show = true;
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
//      int prior = Thread.currentThread().getPriority();
     loading(67+TOTAL_IMAGES_NUMBER);

     try {
//           Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock(/*"/res/images.bin",*/this);
//if(ib==null || ib._image_array==null)Error ="Images wasn't loaded";
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;

	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];

//	   font = new drawDigits(Elements[IMG_NFONT]);
//	   Elements[IMG_NFONT] = Elements[IMG_PL_T2];

           sounds = ib.LoadSound();
//if(sounds==null)Error ="Sounds wasn't loaded";
	   long [][]a = ib.LoadMask();
//if(client_sb.alal_masks==null)Error ="Mask wasn't loaded";
	   AnimeObject.ai_AnimationValues = ib.LoadObjects();
//if(AnimeObject.ai_AnimationValues==null)Error ="Mask wasn't loaded";

	   ib = null;
           Runtime.getRuntime().gc();

	   client_sb = new GameletImpl(scrwidth,visHeight);
	   client_sb.alal_masks = a;
	   a=null;

           V_QUEUE = (client_sb.i_ScreenWidth>>1) / GameletImpl.BLOCKLENGTH +1;
	   Image img = GetImage(IMG_PIPE);
	   PIPE_WIDTH = img.getWidth();
	   I8_PIPE_X = GameletImpl.I8_MAINPIPE_CENTER_X-(PIPE_WIDTH<<7);
	   PIPE_Y = (GameletImpl.I8_MAINPIPE_CENTER_Y-(img.getHeight()<<7))>>8;

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     } catch(Exception e) {
        System.exit(-1);
//      Error = "e:"+e.getMessage();
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }

//     Thread.currentThread().setPriority(prior);
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
 //      accuracy =0;
       CleanSpace();
       stage = 0;
       blink=false;
       ticks = 0;
       direct=GameletImpl.BUTTON_NONE;
       fire_pressed = false;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
//	    client_sb.initStage(2);
            client_sb.nextGameStep(direct);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
//	    drawBackScreen();
	} else {
           stageSleepTime[playGame] = client_sb.i_TimeDelay;
//	   lastPlayerScores = 0;
	   stage = 0;
//	   drawBackScreen();

           client_sb.nextGameStep(GameletImpl.BUTTON_NONE);
	   PlayMelody(SND_APPEARANCE_OTT);
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
       isShown = false;
       CleanSpace();
       stopAllMelodies();
    }


    /**
     * Handle a single key event.
     * The LEFT, RIGHT, UP, and DOWN keys are used to
     * move the scroller within the Board.
     * Other keys are ignored and have no effect.
     * Repaint the screen on every action key.
     */
    protected void keyPressed(int keyCode) {
     // System.out.println(keyCode);
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
		                    CleanSpace();
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

	                       fire_pressed = false;
	                          switch (keyCode) {
			              case MENU_KEY: CleanSpace(); midlet.ShowGameMenu(); break;
				      case END_KEY: CleanSpace(); midlet.ShowQuitMenu(true); break;

				      case Canvas.KEY_NUM6:direct=GameletImpl.BUTTON_RIGHT; break;
				      case Canvas.KEY_NUM4:direct=GameletImpl.BUTTON_LEFT; break;
	                              case Canvas.KEY_NUM2:direct=GameletImpl.BUTTON_JUMPUPDIR; break;
	                              case Canvas.KEY_NUM8:direct=GameletImpl.BUTTON_COWER; break;
	                              case Canvas.KEY_NUM5:fire_pressed = true;
//							   if(client_sb.i_snowballForce==0)
//							        client_sb.increaseSnowBallForce();
							   break;
				   default:
				      if( client_sb.p_Player.i_State==AnimeObject.STATE_LEFT ||
				          client_sb.p_Player.i_State==AnimeObject.STATE_RIGHT){
					    switch (keyCode){
	                                      case Canvas.KEY_NUM1:  direct=GameletImpl.BUTTON_JUMPUPDIR;
							             client_sb.p_Player.i_ObjectDirection=AnimeObject.STATE_LEFT;
							             break;
					      case Canvas.KEY_NUM3:  direct=GameletImpl.BUTTON_JUMPUPDIR;
							             client_sb.p_Player.i_ObjectDirection=AnimeObject.STATE_RIGHT;
							             break;
	                                      case Canvas.KEY_NUM7:  direct=GameletImpl.BUTTON_COWER;
							             client_sb.p_Player.i_ObjectDirection=AnimeObject.STATE_LEFT;
							             break;
					      case Canvas.KEY_NUM9:  direct=GameletImpl.BUTTON_COWER;
							             client_sb.p_Player.i_ObjectDirection=AnimeObject.STATE_RIGHT;
							             break;
					           default:
						         switch(getGameAction(keyCode)) {
							         case Canvas.LEFT:direct=GameletImpl.BUTTON_LEFT; break;
				                                 case Canvas.RIGHT:direct=GameletImpl.BUTTON_RIGHT; break;
								 case Canvas.UP:direct=GameletImpl.BUTTON_JUMPUPDIR; break;
	                                                         case Canvas.DOWN:direct=GameletImpl.BUTTON_COWER; break;
	                                                         case Canvas.FIRE:fire_pressed = true;
//								                      if(client_sb.i_snowballForce==0)
//							                                 client_sb.increaseSnowBallForce();
							                              break;
								     default: direct=GameletImpl.BUTTON_NONE;
						         }
					    }
				      }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=GameletImpl.BUTTON_NONE;
	    fire_pressed = false;
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
	                if(is_first_show){
			  is_first_show = false;
			  PlayMelody(SND_JBELLS_OTT);
			  ticks = 0;
	                }
			if (ticks<80) ticks++;
			 else
			  if(sounds!=null && sounds[SND_JBELLS_OTT].getState()==Sound.SOUND_PLAYING)ticks=0;
			  else
			  {
			   CleanSpace();
			   Runtime.getRuntime().gc();
			   ticks=0;
			   stopAllMelodies();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep(client_sb.getRandomInt(GameletImpl.BUTTON_COWER));
			   //UpdateBackScreen();

			   if (client_sb.i_PlayerState!=GameletImpl.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			       CleanSpace();
			       is_first_show = true;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      ticks++;
			      repaint();
/*
	                      if(ticks++==0) {
		                direct=GameletImpl.BUTTON_NONE;

//			        client_sb.initStage(stage);
				client_sb.nextGameStep(direct);
//				drawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
*/
			    } break;
           case playGame :{
	                   //if(ticks++>100){Runtime.getRuntime().gc();ticks=0;}
//			   if(direct==GameletImpl.BUTTON_INCREASE_FORCE) {
//			     client_sb.increaseSnowBallForce();
//			   }
			   ticks++;
                           if(fire_pressed) {client_sb.increaseSnowBallForce();client_sb.nextGameStep(GameletImpl.BUTTON_NONE);}
			     else if (client_sb.i_snowballForce>0) client_sb.nextGameStep(GameletImpl.BUTTON_FIRE);
			       else client_sb.nextGameStep(direct);


			   //UpdateBackScreen();
/*
			   if (loc.i_GameState==Assault_GSB.GAMESTATE_OVER) {
			       switch (loc.i_PlayerState) {
				      case Assault_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Assault_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (client_sb.i_PlayerState) {
					case GameletImpl.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case GameletImpl.PLAYERSTATE_WON: ticks = 0; gameStatus=Finished;CleanSpace();
					                         PlayMelody(SND_APPEARANCE_OTT);
					                       /*
								       stage++;
					                               if (stage>=Stages.TOTAL_STAGES)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
									 */
								      break;
			         }
			   repaint();
			   //serviceRepaints();
			  } break;
	    case Crashed:
			   stopAllMelodies();
	                   fire_pressed = false;
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=GameletImpl.BUTTON_NONE;
			     if (client_sb.i_GameState==GameletImpl.GAMESTATE_OVER){
			            CleanSpace();
			            gameStatus=gameOver;
			            PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
                                   client_sb.nextGameStep(GameletImpl.BUTTON_NONE);
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           PlayMelody(SND_APPEARANCE_OTT);
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
			     if(soundIsPlaying())ticks=FINAL_PICTURE_OBSERVING_TIME-10;
			      else{
				int n = client_sb.getPlayerScore();
			        if(midlet.GDS.getMinScoreInTopList()<n)ticks=FINAL_PICTURE_OBSERVING_TIME-10;
			        midlet.newScore(n);
			      }
			   repaint();
			   break;
 	 }

        sleepy = System.currentTimeMillis();
        serviceRepaints();
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


/*

    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case GameletImpl.GAMEACTION_FIELDCHANGED:
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
        gBuffer.setColor(0x0);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }

    protected void DoubleBuffer(Graphics gBuffer){
//String s="";
          gBuffer.setColor(0x00000); gBuffer.fillRect(0, 0, scrwidth,scrheight);
/*
      if(scrheight>visHeight){
          gBuffer.setColor(0x00000);
	  gBuffer.fillRect(0, 0, scrwidth,baseY);
          //gBuffer.drawImage(GetImage(IMG_TITLE),0,0,0);
//          gBuffer.drawImage(GetImage(IMG_TITLE),0,0,0);
          //gBuffer.drawImage(Low,0,scrheight-24,0);
          gBuffer.fillRect(0, scrheight-baseY, scrwidth,baseY);
      }

        gBuffer.setColor(0x0/*BACKGROUND_COLOR* /);
	gBuffer.fillRect(0, GetImage(IMG_BACK).getHeight(), scrwidth,visHeight-GetImage(IMG_BACK).getHeight());
*/
	Image img;
	int idx, i_x, i_y, li;
        int i8_absxoffset = client_sb.i8_offsetOfViewX;
	int iiy = (client_sb.p_Player.i8_ScreenY >>> 8)+10;

	int yOffs = 0;
	if(visHeight>MAX_HEIGHT)yOffs = (visHeight-MAX_HEIGHT)>>1;
	 else if(iiy<=((visHeight-client_sb.p_Player.i_Height)>>1))yOffs = 0;
	      else if(iiy>=(MAX_HEIGHT - ((visHeight+client_sb.p_Player.i_Height)>>1)))yOffs = visHeight-MAX_HEIGHT;
	          else yOffs = ((visHeight-client_sb.p_Player.i_Height)>>1) - iiy;


	gBuffer.translate(0,baseY);
	gBuffer.setClip(0,0,scrwidth,visHeight);


        // drawing background
        gBuffer.drawImage(GetImage(IMG_BACK), 0, 0, 0);

        // Drawing the moon
        i_x = (client_sb.p_Moon.i8_ScreenX - i8_absxoffset) >> 8;
        i_y = (client_sb.p_Moon.i8_ScreenY >> 8)+yOffs;

	switch (client_sb.p_Moon.i_State){
	  case AnimeObject.STATE_APPEARANCE: idx = IMG_MOON01;
	                                     break;
	  case AnimeObject.STATE_LEFT:       idx = IMG_MOON02;
	                                     break;
	  case AnimeObject.STATE_RIGHT:      idx = IMG_MOON03;
	                                     break;
	                      default:       /*AnimeObject.STATE_FIRE*/
			                     idx = IMG_MOON_ATTACK01+client_sb.p_Moon.i_Frame;
					     break;
	}
        gBuffer.drawImage(GetImage(idx), i_x, i_y, 0);
/*
        // Drawing background objects
        if (client_sb.p_Ufo.lg_Active)
        {
	      switch (client_sb.p_Ufo.i_State){
	             case AnimeObject.STATE_LEFT:
	             case AnimeObject.STATE_RIGHT:      idx = IMG_UFO01 + client_sb.p_Ufo.i_Frame; break;
	                                 default:       idx = IMG_UFO_FALL;
	      }
              gBuffer.drawImage(GetImage(idx), client_sb.p_Ufo.i8_ScreenX >> 8, (client_sb.p_Ufo.i8_ScreenY >> 8)+yOffs, 0);
        }

        if (client_sb.p_Witch.lg_Active)
        {
	      switch (client_sb.p_Witch.i_State){
	             case AnimeObject.STATE_LEFT:       idx = IMG_WITCH_L_01 + client_sb.p_Witch.i_Frame; break;
	             case AnimeObject.STATE_RIGHT:      idx = IMG_WITCH_R_01 + client_sb.p_Witch.i_Frame; break;
	                      default:       idx = IMG_WITCH_FALL_01;
	      }
              gBuffer.drawImage(GetImage(idx), client_sb.p_Witch.i8_ScreenX >> 8, (client_sb.p_Witch.i8_ScreenY >> 8)+yOffs, 0);
        }
*/
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////
///////////
//////
///
//

//        int i_endX = (client_sb.i8_offsetOfViewX >>> 8) + client_sb.i_ScreenWidth;
        int i_startX = client_sb.i8_offsetOfViewX >> 8;
	int block_width = GameletImpl.BLOCKLENGTH<<1;
	int _house_block_start = i_startX / block_width;
	int _house_offset = i_startX % block_width;
	int _house_block_end = _house_block_start + client_sb.i_ScreenWidth / block_width +1;

        i_startX= -_house_offset+GameletImpl.BLOCKLENGTH;

	int align;
	int prev = -1;
        if(_house_block_start>0 && _house_block_start<client_sb.ai_wayarray.length)
	{
	   prev=client_sb.ai_wayarray[_house_block_start-1];
	   if(Elements[IMG_HOUSE1+prev]==null) prev = -1;
	}

//	s="#"+_house_block_start+":"+((client_sb.i_ScreenWidth>>1) / GameletImpl.BLOCKLENGTH +1)+"[";

        for (li = _house_block_start; li <= _house_block_end; li++)
	{
	    idx = client_sb.ai_wayarray[li];
	    img = GetImage(IMG_HOUSE1+idx);//Elements[IMG_HOUSE1+align];
	    Elements[IMG_HOUSE1+idx]=img;
	    if(idx==prev)prev = -1;
	    switch(GameletImpl.ai_AlignArray[idx])
	    {
	      case GameletImpl.ALIGN_CENTER:   align = -(img.getWidth()>>1);break;
	      case GameletImpl.ALIGN_LEFT: align = -40;break;
	        default: align = 40-img.getWidth();break;
	    }
	    gBuffer.drawImage(img,i_startX + align, MAX_HEIGHT-img.getHeight() + yOffs, 0);
	    i_startX+= block_width;
//	    s+=idx+" ";
        }
	if(prev!=-1)Elements[IMG_HOUSE1+prev]=null;
	img = null;
//s+="]";


        // Drawing of the PIPE

	i_x = (I8_PIPE_X-i8_absxoffset)>>8;
        if(i_x<scrwidth && i_x>-PIPE_WIDTH)
	{
	    gBuffer.drawImage(GetImage(IMG_PIPE),i_x,PIPE_Y+yOffs,0);
        }


        // Drawing of the player
        gBuffer.setColor(0x00000);
        i_x = (client_sb.p_Player.i8_ScreenX - i8_absxoffset) >>> 8;
        i_y = (client_sb.p_Player.i8_ScreenY >>> 8)+yOffs;

	idx = client_sb.p_Player.i_ObjectDirection == AnimeObject.STATE_LEFT
	                                ?
			0 : IMG_SWALK_RIGHT02 - IMG_SWALK_LEFT02 ;

	switch (client_sb.p_Player.i_State) {
	  case AnimeObject.STATE_APPEARANCE: idx = IMG_SWALK_RIGHT02;	                                     break;
	  case AnimeObject.STATE_LEFT:
	  case AnimeObject.STATE_RIGHT:      idx += IMG_SWALK_LEFT01+client_sb.p_Player.i_Frame;             break;
	  case AnimeObject.STATE_JUMP:       idx += IMG_SJUMP_LEFT01+client_sb.p_Player.i_Frame;             break;
	  case AnimeObject.STATE_COWER:      idx += IMG_SDOWN_LEFT01+client_sb.p_Player.i_Frame;             break;
	  case AnimeObject.STATE_DEATH:      idx = IMG_SDEAD01+client_sb.p_Player.i_Frame;                   break;
	  case AnimeObject.STATE_FIRE:       idx += IMG_SDROP_LEFT01+client_sb.p_Player.i_Frame;             break;
	}
        gBuffer.drawImage(GetImage(idx), i_x, i_y, 0);

/// draw borderline
//        gBuffer.setColor(0xffffff);
//        gBuffer.drawRect(i_x, i_y, client_sb.p_Player.i_Width, client_sb.p_Player.i_Height);

        // Drawing the animation objects
        for (li = 0; li < client_sb.ap_MoveObjects.length; li++)
        {
            AnimeObject p_obj = client_sb.ap_MoveObjects[li];
            if (p_obj.lg_Active)
            {
                switch (p_obj.i_Type)
                {
                    case AnimeObject.OBJECT_CAT:
						  switch (p_obj.i_State){
							 case AnimeObject.STATE_LEFT:       idx = IMG_CAT_WALK_LEFT01 + p_obj.i_Frame; break;
	                                                 case AnimeObject.STATE_RIGHT:      idx = IMG_CAT_WALK_RIGHT01 + p_obj.i_Frame; break;
	                                                 case AnimeObject.STATE_JUMP:       idx = IMG_CAT_UP01 + p_obj.i_Frame; break;
	                                                 case AnimeObject.STATE_COWER:      idx = IMG_CAT_DOWN01 + p_obj.i_Frame; break;
	                                                 case AnimeObject.STATE_DEATH:      idx = IMG_CAT_FALL01 + p_obj.i_Frame; break;
	                                                 case AnimeObject.STATE_FIRE:       idx = ((p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
							                                                                     ?
												       IMG_CAT_ATTACK_LEFT01 : IMG_CAT_ATTACK_RIGHT01)
							                                                            + p_obj.i_Frame; break;
	                                                     default:       idx = IMG_CAT_UP01; break;
						  }
						  break;
                    case AnimeObject.OBJECT_FALLEDSTAR:      idx = IMG_STAR_DOWN; break;
                    case AnimeObject.OBJECT_FALLINGSTAR:     idx = IMG_STAR01 + p_obj.i_Frame; break;
                    case AnimeObject.OBJECT_FIRE:
//		                                  idx = IMG_STAR_DOWN; break;/*
						  switch (p_obj.i_State){
	                                                 case AnimeObject.STATE_APPEARANCE: idx = IMG_MOON_STRIKE01 + p_obj.i_Frame; break;
	                                                     default:
							                                    idx = IMG_MOON_STRIKE_FALL01 + p_obj.i_Frame; break;
						  }
						  break; //*/
                    case AnimeObject.OBJECT_GHOST:
						  switch (p_obj.i_State){
	                                                 case AnimeObject.STATE_APPEARANCE: idx = IMG_GHOST_R_IN01 + p_obj.i_Frame; break;
							 case AnimeObject.STATE_LEFT:
	                                                 case AnimeObject.STATE_RIGHT:      idx = IMG_GHOST_AT01 + p_obj.i_Frame; break;
	                                                     default: /*AnimeObject.STATE_DEATH*/
							                                    idx = IMG_GHOST_BIRST01 + p_obj.i_Frame; break;
						  }
						  break;
                    case AnimeObject.OBJECT_ICECREAM:        idx = IMG_ICECREAM; break;
                    case AnimeObject.OBJECT_SMOKE:
						  switch (p_obj.i_State){
	                                                 case AnimeObject.STATE_APPEARANCE: idx = IMG_SMOKE_AP0 + p_obj.i_Frame; break;
							 case AnimeObject.STATE_LEFT:
	                                                 case AnimeObject.STATE_RIGHT:      idx = IMG_SMOKE00 + p_obj.i_Frame; break;
	                                                     default: /*AnimeObject.STATE_DEATH*/
							                                    idx = IMG_SMOKE_D_00 + p_obj.i_Frame; break;
						  }
						  break;

                    default :
                        {
                            System.out.println("Unknown an object's type " + p_obj.i_Type);
//                            System.exit(1);
                        }
                }
                i_x = (p_obj.i8_ScreenX - i8_absxoffset) >>> 8;
                i_y = (p_obj.i8_ScreenY >>> 8)+yOffs;
                gBuffer.drawImage(GetImage(idx), i_x, i_y, 0);
///// draw borderline
///                gBuffer.drawRect(i_x, i_y, p_obj.i_Width, p_obj.i_Height);
            }
        }


        // Drawing of the snowball if it is presented
        if (client_sb.p_Snowball.lg_Active)
        {
            i_x = (client_sb.p_Snowball.i8_ScreenX - i8_absxoffset) >>> 8;
            i_y = (client_sb.p_Snowball.i8_ScreenY >> 8) + yOffs;

            if(client_sb.p_Snowball.i_State == AnimeObject.STATE_APPEARANCE)
	      gBuffer.drawImage(GetImage(IMG_SNOWBALL), i_x,i_y, 0);
             else gBuffer.drawImage(GetImage(IMG_SNOWBALL_FALL), i_x,i_y, 0);
        }

	if (gameStatus == demoPlay && blink) {
	   img  = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scrwidth>>1,visHeight-img.getHeight()-5/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

//} catch (OutOfMemoryError e)
//    {
//      CleanSpace();
//    }

        gBuffer.translate(0,-baseY);
	gBuffer.setClip(0,0,scrwidth,scrheight);


        // Drawing of the snowball force
	int baseline = 4;
        int i_pos = client_sb.i_snowballForce / GameletImpl.SNOWBALL_FORCE_STEP;
	int i_max = (GameletImpl.MAX_SNOWBALL_FORCE/GameletImpl.SNOWBALL_FORCE_STEP)>>1;
        img = GetImage(IMG_HEART);

        if (client_sb.i_snowballForce != 0)
        {
	    gBuffer.setColor(0xffffff);
	    i_y = baseY+visHeight+i_max+baseline;
	    if(baseY >= i_max+2)
	    {
	     i_x = (scrwidth-4*i_max)>>1;
	     //if((ticks&1)==0)
	     gBuffer.drawImage(GetImage(IMG_SNOWBALL),i_x-5,i_y-(i_max),0);
             for (li = 0; li < i_pos; li++)
             {
		int v = baseline+(li>>1);
                gBuffer.fillRect(i_x,i_y/*baseY*/-v,3,v);
		i_x += 4;
             }
	    } else {
	      i_x = 0;
              //if((ticks&1)==0)
	      gBuffer.drawImage(GetImage(IMG_SNOWBALL),i_x,baseY+baseline,0);
               for (li = 0; li < i_pos; li++)
               {
                 gBuffer.fillRect(i_x, baseY, 3, baseline+li);
                 i_x += 4;
               }
	    }
        }



        if(!(gameStatus == Crashed && blink)) {
//           gBuffer.drawImage(iBomb[0],/*baseX+*/0,/*baseY+*/scrheight-iBomb[0].getHeight()-2+BarOffset,0);
//	   font.drawDigits(gBuffer,iBomb[0].getWidth(),scrheight-((iBomb[0].getHeight()+font.fontHeight)>>1)-2+BarOffset,2,loc.i_bombs);

           i_y = scrheight-img.getHeight()/*-baseY*/;
           gBuffer.drawImage(img,2,i_y,0);
           BAR(gBuffer,img.getWidth()+2, i_y+((img.getHeight()-BAR_HEIGHT)>>1), client_sb.MAX_PLAYER_HEALTH, client_sb.i_PlayerHealth,colorness?0x7cfaff:0xffffff);
	   for (int i=0;i<client_sb.i_Attemptions-1;i++)
              gBuffer.drawImage(img,/*baseX+*/scrwidth-(i+1)*(img.getWidth()+2)-10,i_y,0);
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

/*
       if(Error!=null){
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  g.drawString(Error ,scrwidth>>1, (scrheight>>1)-23,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("N "+LoadingNow+"/"+LoadingTotal,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1),Graphics.TOP|Graphics.HCENTER);
//	                  g.drawString("li "+NNN1+" : "+NNN2,scrwidth>>1, (scrheight>>1)+13,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
			  return;
       }
*/
            switch (gameStatus) {
	      case notInitialized:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x0);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  g.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  g.drawString("N "+LoadingNow+"/"+LoadingTotal,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  g.drawString("["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1),Graphics.TOP|Graphics.HCENTER);
//	                  g.drawString("li "+NNN1+" : "+NNN2,scrwidth>>1, (scrheight>>1)+13,Graphics.TOP|Graphics.HCENTER);
//		          g.setColor(0xffff00);    // drawin' flyin' text
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
/*
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
*/
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
              default :  // if (this.isDoubleBuffered())
/*
Runtime.getRuntime().gc();
long m=Runtime.getRuntime().freeMemory();
accuracy-=m;
if(m<20000)CleanSpace();
*/
			      {

		                DoubleBuffer(g);
			 //     }
			 //     else {
//		                DoubleBuffer(gBuffer);
//		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
/*
g.setColor(0x00ffff);
Runtime.getRuntime().gc();
m = Runtime.getRuntime().freeMemory();
accuracy+=m;
g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL));
g.drawString(m+":"+accuracy,scrwidth>>1,2,Graphics.TOP|Graphics.HCENTER);
*/
            }
/*
                          int xc = scrwidth>>1;
			  int yc = scrheight>>1;

	  g.setColor(0x000000); g.drawString("N "+LoadingNow+"/"+LoadingTotal,xc, yc-23,Graphics.TOP|Graphics.HCENTER);
	                        g.drawString(Long.toString(Runtime.getRuntime().freeMemory()) ,xc,yc+13,Graphics.TOP|Graphics.HCENTER);
	  xc--;yc--;
	  g.setColor(0xffffff); g.drawString("N "+LoadingNow+"/"+LoadingTotal,xc, yc-23,Graphics.TOP|Graphics.HCENTER);
	                        g.drawString(Long.toString(Runtime.getRuntime().freeMemory()) ,xc,yc+13,Graphics.TOP|Graphics.HCENTER);
*/
/*
//       if(Error!=null){

//	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
                          int xc = scrwidth>>1;
			  int yc = scrheight>>1;
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
		          g.setColor(0x00000);    // drawin' flyin' text
	                  g.drawString(Error ,xc, yc-23,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("N "+LoadingNow+"/"+LoadingTotal,xc, yc-13,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,xc,yc,Graphics.TOP|Graphics.HCENTER);

			  xc--;yc--;
		          g.setColor(0xffffff);    // drawin' flyin' text

	                  g.drawString(Error ,xc, yc-23,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("N "+LoadingNow+"/"+LoadingTotal,xc, yc-13,Graphics.TOP|Graphics.HCENTER);
	                  g.drawString("["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,xc,yc,Graphics.TOP|Graphics.HCENTER);

//	                  g.drawString("li "+NNN1+" : "+NNN2,scrwidth>>1, (scrheight>>1)+13,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
//			  return;
//       }
*/
/*
		          g.setColor(0xffffff);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL));
	                  g.drawString("["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,0, (scrheight>>1)-23,Graphics.TOP|Graphics.LEFT);
			  g.setFont(f);
*/
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=GameletImpl.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_sb.nextGameStep(direct);
       fire_pressed = false;
//       drawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
    private Image GetImage(int n){
       if(Elements[n]==null){
//	if(n<3 && Runtime.getRuntime().freeMemory()<33000)CleanSpace();
//	else
	if(Runtime.getRuntime().freeMemory()<4000){
	  Runtime.getRuntime().gc();
	  if(Runtime.getRuntime().freeMemory()<4000)CleanSpace();
//	  if(ByteArray==null){Error="BA_NULL";return Elements[TOTAL_IMAGES_NUMBER];}
//	  if(ByteArray.length<n){Error="BA_L<"+n;return Elements[TOTAL_IMAGES_NUMBER];}
//	  if(ByteArray[n]==null){Error="BA_"+n+"_NULL";return Elements[TOTAL_IMAGES_NUMBER];}
	}
        return Image.createImage(ByteArray[n], 0, ByteArray[n].length);
       } else return Elements[n];
    }

public void CleanSpace(){
  for(int i=IMG_HOUSE1;i<=IMG_HOUSE13;i++) if(ByteArray[i]!=null) Elements[i]=null;
  Runtime.getRuntime().gc();
}


private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_HOUSE1 = 3;
private static final int IMG_HOUSE2 = 4;
private static final int IMG_HOUSE3 = 5;
private static final int IMG_HOUSE4 = 6;
private static final int IMG_HOUSE5 = 7;
private static final int IMG_HOUSE6 = 8;
private static final int IMG_HOUSE7 = 9;
private static final int IMG_HOUSE8 = 10;
private static final int IMG_HOUSE9 = 11;
private static final int IMG_HOUSE10 = 12;
private static final int IMG_HOUSE11 = 13;
private static final int IMG_HOUSE12 = 14;
private static final int IMG_HOUSE13 = 15;
private static final int IMG_BACK = 16;
private static final int IMG_DEMO = 17;
private static final int IMG_SWALK_LEFT01 = 18;
private static final int IMG_SWALK_LEFT02 = 19;
private static final int IMG_SWALK_LEFT03 = 20;
private static final int IMG_SWALK_LEFT04 = 21;
private static final int IMG_SWALK_LEFT05 = 22;
private static final int IMG_SWALK_LEFT06 = 23;
private static final int IMG_SJUMP_LEFT01 = 26;
private static final int IMG_SJUMP_LEFT02 = 27;
private static final int IMG_SJUMP_LEFT03 = 28;
private static final int IMG_SDROP_LEFT01 = 29;
private static final int IMG_SDROP_LEFT02 = 30;
private static final int IMG_SDROP_LEFT03 = 31;
private static final int IMG_SWALK_RIGHT01 = 32;
private static final int IMG_SWALK_RIGHT02 = 33;
private static final int IMG_SWALK_RIGHT03 = 34;
private static final int IMG_SWALK_RIGHT04 = 35;
private static final int IMG_SWALK_RIGHT05 = 36;
private static final int IMG_SWALK_RIGHT06 = 37;
private static final int IMG_SJUMP_RIGHT01 = 40;
private static final int IMG_SJUMP_RIGHT02 = 41;
private static final int IMG_SJUMP_RIGHT03 = 42;
private static final int IMG_SDROP_RIGHT01 = 43;
private static final int IMG_SDROP_RIGHT02 = 44;
private static final int IMG_SDROP_RIGHT03 = 45;
private static final int IMG_SDEAD01 = 46;
private static final int IMG_SDEAD02 = 47;
private static final int IMG_SDEAD03 = 48;
private static final int IMG_CAT_WALK_LEFT01 = 49;
private static final int IMG_CAT_WALK_LEFT02 = 50;
private static final int IMG_CAT_WALK_LEFT03 = 51;
private static final int IMG_CAT_ATTACK_LEFT01 = 52;
private static final int IMG_CAT_ATTACK_LEFT02 = 53;
private static final int IMG_CAT_WALK_RIGHT01 = 54;
private static final int IMG_CAT_WALK_RIGHT02 = 55;
private static final int IMG_CAT_WALK_RIGHT03 = 56;
private static final int IMG_CAT_ATTACK_RIGHT01 = 57;
private static final int IMG_CAT_ATTACK_RIGHT02 = 58;
private static final int IMG_CAT_UP01 = 59;
private static final int IMG_CAT_UP02 = 60;
private static final int IMG_CAT_DOWN01 = 61;
private static final int IMG_CAT_DOWN02 = 62;
private static final int IMG_CAT_FALL01 = 63;
private static final int IMG_CAT_FALL02 = 64;
private static final int IMG_GHOST_R_IN01 = 65;
private static final int IMG_GHOST_R_IN02 = 66;
private static final int IMG_GHOST_R_IN03 = 67;
private static final int IMG_GHOST_R_IN04 = 68;
private static final int IMG_GHOST_R_IN05 = 69;
private static final int IMG_GHOST_AT01 = 70;
private static final int IMG_GHOST_AT02 = 71;
private static final int IMG_GHOST_BIRST01 = 72;
private static final int IMG_GHOST_BIRST02 = 73;
private static final int IMG_GHOST_BIRST03 = 74;
private static final int IMG_GHOST_BIRST04 = 75;
private static final int IMG_GHOST_BIRST05 = 76;
private static final int IMG_SMOKE_AP0 = 77;
private static final int IMG_SMOKE_AP1 = 78;
private static final int IMG_SMOKE00 = 79;
private static final int IMG_SMOKE01 = 80;
private static final int IMG_SMOKE02 = 81;
private static final int IMG_SMOKE_D_00 = 82;
private static final int IMG_SMOKE_D_01 = 83;
private static final int IMG_SMOKE_D_02 = 84;
private static final int IMG_MOON01 = 85;
private static final int IMG_MOON02 = 86;
private static final int IMG_MOON03 = 87;
private static final int IMG_MOON_ATTACK01 = 88;
private static final int IMG_MOON_ATTACK02 = 89;
private static final int IMG_MOON_STRIKE01 = 90;
private static final int IMG_MOON_STRIKE02 = 91;
private static final int IMG_MOON_STRIKE_FALL01 = 92;
private static final int IMG_MOON_STRIKE_FALL02 = 93;
private static final int IMG_PIPE = 94;
private static final int IMG_ICECREAM = 95;
private static final int IMG_SNOWBALL = 96;
private static final int IMG_SNOWBALL_FALL = 97;
private static final int IMG_STAR01 = 98;
private static final int IMG_STAR02 = 99;
private static final int IMG_STAR_DOWN = 100;
private static final int IMG_HEART = 101;
private static final int IMG_SDOWN_LEFT01 = 24;
private static final int IMG_SDOWN_LEFT02 = 25;
private static final int IMG_SDOWN_RIGHT01 = 38;
private static final int IMG_SDOWN_RIGHT02 = 39;
private static final int TOTAL_IMAGES_NUMBER = 102;

private static final byte SND_JBELLS_OTT = 0;
private static final byte SND_APPEARANCE_OTT = 1;
private static final byte SND_WON_OTT = 2;
private static final byte SND_LOST_OTT = 3;
}

