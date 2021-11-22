
import javax.microedition.lcdui.*;
//import com.GameKit_3.SantaClaus.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.igormaznitsa.game_kit_E5D3F2.RiverRescue.*;


public class GameArea extends Canvas implements Runnable, LoadListener {//, GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 120;
  private final static int HEIGHT_LIMIT = 160;

/*=============================*/

    private static final int KEY_MENU = -21;
    private static final int KEY_CANCEL = -20;
    private static final int KEY_ACCEPT = -21;
    private static final int KEY_BACK = -20;

    public void setScreenLight(boolean state)
    {
//     DeviceControl.setLights(0, state?100:0);
//     GameScreen.

    }

    public int getBottomInset() { return 0; }
    public int getTopInset()    { return 0; }

    public void activateVibrator(int n){}

//protected SoundEffect [] sounds;
    public void stopAllMelodies(){
/*
      if(sounds!=null && sounds.length>0)
//       synchronized(sounds){
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               sounds[i].stop();
//       }
//      isFirst = false;
*/
    }
int SoundPlayTimeGap = 250;
long sound_timemark = 0;
    public void playSound(int n){
/*
      if(sounds!=null && sounds.length>0 && midlet._Sound){
//	stopAllMelodies();
//       synchronized(sounds){
        if(soundIsPlaying() && System.currentTimeMillis()-sound_timemark<SoundPlayTimeGap) return;
	sound_timemark = System.currentTimeMillis();
	stopAllMelodies();
	sounds[n].play(1);
       }
//      }
*/
    }
    public boolean soundIsPlaying(){
/*
      if(sounds!=null)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               return true;
*/
      return false;
    }
/*=============================*/

  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

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

  private Image [] Elements;
  private byte [][] ByteArray;
/*
  private int SlideNo=0;
  private int slide_delay = 1;
  private int slide_counter = 1;
*/
//  private Image iBlank = Image.createImage(1,1);
//  private int MaxWaterLength = 10;
//  private Image []Water;
  private Image MenuIcon;

  private Image Buffer;
  private Graphics gBuffer;
  private Image BackScreen;
  private Graphics gBackScreen;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

//  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected RiverRescue_PMR client_pmr;
  protected int direct=RiverRescue_PMR.BUTTON_NONE;
  protected RiverRescue_SB client_sb;
  protected int baseX=0, baseY=0, cellW=0,cellH=0, bsCELLs, bsOFFS=1;
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
  private int [] stageSleepTime = {100,100,100,200,100,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    if(scrheight == 144) scrheight = 160;
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

    VisWidth = Math.min(scrwidth,WIDTH_LIMIT);
    VisHeight = Math.min(scrheight,HEIGHT_LIMIT);

    client_sb = new RiverRescue_SB(VisWidth, VisHeight, null);
    client_pmr = new RiverRescue_PMR();

    if (!this.isDoubleBuffered())
    {
//      System.out.println("not dbufferred");
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    bsCELLs = (VisHeight+RiverRescue_SB.VIRTUALCELL_HEIGHT-1)/RiverRescue_SB.VIRTUALCELL_HEIGHT +bsOFFS;
//    BackScreen=Image.createImage(RiverRescue_SB.VIRTUALCELL_WIDTH*64,RiverRescue_SB.VIRTUALCELL_HEIGHT*bsCELLs);
//    BackScreen = Image.createImage(client_sb.VIRTUALCELL_WIDTH*32,(client_sb.SCREEN_LINES+1)*client_sb.VIRTUALCELL_HEIGHT);
    BackScreen=Image.createImage(RiverRescue_SB.VIRTUALCELL_WIDTH*64,RiverRescue_SB.VIRTUALCELL_HEIGHT*bsCELLs);
    gBackScreen = BackScreen.getGraphics();
    gBackScreen.setClip(0,0,BackScreen.getWidth(),BackScreen.getHeight());
//    bsCELLs = client_sb.SCREEN_LINES+2;

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

    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void startIt() {
     loading(67+TOTAL_IMAGES_NUMBER);

     try {

	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock(/*"/res/images.bin",*/this);
	   Elements = ib._image_array;
	   ByteArray = ib._byte_array;
	   MenuIcon = Elements[TOTAL_IMAGES_NUMBER];
//	   font = new drawDigits(GetImage(IMG_NFONT));
	   ib = null;

	   Image img = Image.createImage(64*RiverRescue_SB.VIRTUALCELL_WIDTH,RiverRescue_SB.VIRTUALCELL_HEIGHT);
/*
	   Water = new Image[MaxWaterLength];
	   for(int i=MaxWaterLength-1;i>=0;i--){
	     Water[i]=Image.createImage((i+1)*RiverRescue_SB.VIRTUALCELL_WIDTH,RiverRescue_SB.VIRTUALCELL_HEIGHT);
	     for (int j=i;j<=MaxWaterLength-1;j++)
	       Water[j].getGraphics().drawImage(Elements[IMG_PEXPL6],i*RiverRescue_SB.VIRTUALCELL_WIDTH,0,0);
	   }
*/
	   for(int i=0;i<64;i++)
	      img.getGraphics().drawImage(Elements[IMG_GROUND],i*RiverRescue_SB.VIRTUALCELL_WIDTH,0,0);
	   Elements[IMG_GROUND] = img;

//	   font = new drawDigits(Elements[IMG_NFONT]);
//	   Elements[IMG_NFONT]=null;
           Runtime.getRuntime().gc();


       if (thread==null){
          thread=new Thread(this);
	  thread.start();
//	  thread.setPriority(Thread.MIN_PRIORITY);
       }
     } catch(Exception e) {
        System.out.println("Can't read images");
	midlet.killApp();
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
       direct=RiverRescue_PMR.BUTTON_NONE;
       client_pmr.i_Value = direct;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    //client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
//	    drawBackScreen();
	} else {
           stageSleepTime[playGame] = ((RiverRescue_GSB)client_sb.getGameStateBlock()).i_TimeDelay;
//	   lastPlayerScores = 0;
	   stage = 0;
	   gameStatus=playGame;
	}
	bufferChanged=true;
	drawBackScreen();
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
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME){
		                       if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
			               if(keyCode == MENU_KEY || keyCode == END_KEY)
				            ticks -= 5+1;
	                     }
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
	                          switch (keyCode) {
			              case MENU_KEY: midlet.ShowGameMenu(); break;
				      case END_KEY: midlet.ShowQuitMenu(true); break;
				      case Canvas.KEY_NUM4: direct=RiverRescue_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM6: direct=RiverRescue_PMR.BUTTON_RIGHT; break;
				      default:
				          switch(action){
					    case Canvas.LEFT: direct=RiverRescue_PMR.BUTTON_LEFT; break;
					    case Canvas.RIGHT: direct=RiverRescue_PMR.BUTTON_RIGHT; break;
					    default:
					             direct=RiverRescue_PMR.BUTTON_NONE;
				          }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=RiverRescue_PMR.BUTTON_NONE;
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
			if (ticks<80) ticks++;
			 else
			  {
			   Runtime.getRuntime().gc();
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);

			   //slide_counter = 0;

			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;

		           client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();
			   RiverRescue_GSB loc=(RiverRescue_GSB)client_sb.getGameStateBlock();
			   if (loc.getPlayerState()!=RiverRescue_GSB.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage:
/*                          {
	                      if(ticks++==0) {
		                direct=RiverRescue_PMR.BUTTON_NONE;
			        client_pmr.i_Value = direct;
			        client_sb.initStage(stage);
				client_sb.nextGameStep(client_pmr);
				drawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
*/
           case playGame :{
	                   //if(ticks++>100){Runtime.getRuntime().gc();ticks=0;}
			   ticks++;
			   client_pmr.i_Value = direct;
			   client_sb.nextGameStep(client_pmr);
/*
			   if(--slide_counter<=0){
			     slide_counter = slide_delay;
			     SlideNo=++SlideNo&7;
			   }
*/
			   //UpdateBackScreen();
			   RiverRescue_GSB loc=(RiverRescue_GSB)client_sb.getGameStateBlock();

			   /*
			   if (loc.getGameState()==RiverRescue_GSB.GAMESTATE_OVER) {
			       switch (loc.getPlayerState()) {
				      case RiverRescue_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case RiverRescue_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case RiverRescue_GSB.PLAYERSTATE_LOST: ticks =0; gameStatus=gameOver; break;
			                case RiverRescue_GSB.PLAYERSTATE_WON: ticks = 0;
//								       stage++;
//					                               if (stage>=Stages.TOTAL_STAGES)
								         gameStatus=Finished;
//								       else
//					                                 gameStatus=newStage;
								      break;

			         }
			   repaint();
			  } break;
	    case Crashed:

			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=RiverRescue_PMR.BUTTON_NONE;
			     if (((RiverRescue_GSB)client_sb.getGameStateBlock()).getGameState()==RiverRescue_GSB.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGameAfterPlayerLost();
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
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
			      midlet.newScore(((GameStateBlock)client_sb.getGameStateBlock()).getPlayerScore());
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

	workDelay = System.currentTimeMillis();

      }
    }

/*
    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){

	 switch(actionID) {
	 case RiverRescue_SB.GAMEACTION_CELLCHANGED:
		  {
		       gBackScreen.drawImage(Elements[IMG_SAFE_EMPTY],x*cellW,y*cellH,0);
		  }
	 }
    }
*/

/*
    private void DrawScanLine(long e, int pos){
      int offs = 0;
            for(int j=0;j<64;j++){
	      switch((int)(e&1l)){
	       case 0: gBackScreen.drawImage(Elements[IMG_SB0],offs,pos,0);break;
	       case 1: gBackScreen.drawImage(Elements[IMG_ST0],offs,pos,0);break;
	      }
	      offs += RiverRescue_SB.VIRTUALCELL_WIDTH;
	      e>>=1;
            }
    }
*/

private int pos;
private int bsLAST_FRAME;
private int _left_b;
//private int _v_off = 8*2;

    private void DrawScanLine(long e, int pos){
      int offs = 0,mark=0, n =0;
      gBackScreen.setColor(0x6194EA);
      gBackScreen.drawImage(Elements[IMG_GROUND],0,pos,0);
            for(int j=0;j<65;j++){
	      if((e&1l)==0 || j==64){
	          if(mark!=offs){
		    try{
		      gBackScreen.fillRect(mark,pos,offs-mark,RiverRescue_SB.VIRTUALCELL_HEIGHT);
		      //gBackScreen.drawImage(Water[n],mark,pos,0);
		      n=0;
		    } catch (Exception ex){
//		        System.out.println("+"+n);
		    }
	          }
		offs+=RiverRescue_SB.VIRTUALCELL_WIDTH;
		mark=offs;
	      } else {
		offs+=RiverRescue_SB.VIRTUALCELL_WIDTH;
/*
		if(++n>=MaxWaterLength-1){
		    try{
		  gBackScreen.drawImage(Water[n],mark,pos,0);
		  n=0;
		    } catch (Exception ex){
		        System.out.println(n);
		    }
		  mark=offs;
		}
*/
	      }
	      e>>>=1;
            }
    }

    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);

        gBuffer.setColor(0xffffff);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);

        gBuffer.setColor(0xbb2211);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }

    private void drawBackScreen(){
        RiverRescue_GSB loc=(RiverRescue_GSB)client_sb.getGameStateBlock();
        Player p_player = loc.p_Player;

	bsLAST_FRAME = p_player.i_cy - client_sb.I_GENERATEBORDEROFFSET;
        bsLAST_FRAME = (bsLAST_FRAME>>>3) -bsOFFS;

        if(bsLAST_FRAME<0) bsLAST_FRAME=0;
	if(bsLAST_FRAME+bsCELLs>loc.i_FullWayLength)bsLAST_FRAME=loc.i_FullWayLength-bsCELLs;
	pos = 0;

	int t = bsLAST_FRAME;
	int g = pos;
        for (int i=0;i<bsCELLs;i++){
	  if(t >= loc.i_FullWayLength)
	        t-=loc.i_FullWayLength;
	  DrawScanLine(loc.getLineElement(t),i*RiverRescue_SB.VIRTUALCELL_HEIGHT);
	  t++;
        }
	_left_b = (RiverRescue_SB.VIRTUALCELL_WIDTH<<6)-(VisWidth>>1);
    }

/*
    protected void SortObjects(MovingObject[] ap_mobjs){
	int low = 0;
	int high = ap_mobjs.length-1;
	int medium = ap_mobjs.length>>1;

        for (int li = low; li < high;)
        {
            MovingObject p_obj = ap_mobjs[li];
            if (p_obj.lg_Active)
            {
                switch (p_obj.i_Type)
                {
                    case MovingObject.OBJECT_DROWNINGMAN:
                    case MovingObject.OBJECT_FUEL:
                    case MovingObject.OBJECT_MINE:
		                                  if(low==li)li++;
						  else {
						    ap_mobjs[li]=ap_mobjs[low];
						    ap_mobjs[low]=p_obj;
						  } low++;
						  break;

                    case MovingObject.OBJECT_EXPLOSION:
                    case MovingObject.OBJECT_HELICOPTERHORZ:
                    case MovingObject.OBJECT_AIRPLANEVERT:
						    ap_mobjs[li]=ap_mobjs[high];
						    ap_mobjs[high]=p_obj;
						    high--;
						    break;
		    default:li++;

                }
            }else li++;
        }

    }

*/
int _abs_y,_y_off,stline,offset;
MovingObject p_obj;

    protected void DoubleBuffer(Graphics gBuffer) throws ArrayIndexOutOfBoundsException{
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

	int type, li;
        Image img = null;

//      if (bufferChanged) {
/*
        gBuffer.setColor(0x0);
        gBuffer.fillRect(0, 0, VisWidth, VisHeight);
*/
	// prepare screen
        RiverRescue_GSB loc=(RiverRescue_GSB)client_sb.getGameStateBlock();

        Player p_player = loc.p_Player;
	_abs_y = p_player.i_cy - client_sb.I_GENERATEBORDEROFFSET;

	if(_abs_y>(bsLAST_FRAME<<3) || _abs_y<0)
	   _abs_y=(bsLAST_FRAME<<3);

	_y_off = ~_abs_y&7;  //  7-(abs_y&7)
	stline = (_abs_y+7)>>>3;

	while(stline<bsLAST_FRAME){
	  if((pos-=8)<0)pos += BackScreen.getHeight();
	  bsLAST_FRAME--;
	  if(bsLAST_FRAME-bsOFFS>=0) DrawScanLine(loc.getLineElement(bsLAST_FRAME-bsOFFS),pos);
	}

	offset = 0;
	if(p_player.i_cx>(VisWidth>>1))
	   if(p_player.i_cx<_left_b) offset =  -p_player.i_cx+(VisWidth>>1);
	      else offset = -(RiverRescue_SB.VIRTUALCELL_WIDTH<<6)+VisWidth;
//System.out.println(BackScreen.getWidth());
//	gBuffer.drawImage(BackScreen,0,0,0);

	gBuffer.drawImage(BackScreen,offset,_y_off-pos-8,0);
	gBuffer.drawImage(BackScreen,offset,_y_off-pos-8+BackScreen.getHeight(),0);
//System.out.println(BackScreen.getWidth()+"x"+BackScreen.getHeight()+","+offset);
/*
        int i_block = bsLAST_FRAME / RiverRescue_GSB.BLOCKLENGTH;
        int i_offs = bsLAST_FRAME % RiverRescue_GSB.BLOCKLENGTH;
	try{
	  gBuffer.setColor(0x0);
	  gBuffer.drawString("#"+Stages.ai_wayarray[i_block]+"("+i_block+":"+i_offs+")",0,0,Graphics.TOP|Graphics.LEFT);
	} catch (Exception e){System.out.println(i_block);}
*/
        // Drawing of player's splashes
        Splash[] ap_splashes = p_player.ap_Splashes;

        for (li = 0; li < ap_splashes.length; li++)
        {
            Splash p_splash = ap_splashes[li];
            if (p_splash.lg_Active)
            {
	        gBuffer.drawImage(Elements[IMG_SPLASH0+p_splash.i_Frame],p_splash.i_X+offset, p_splash.i_Y - _abs_y, 0);
            }
        }

        // Drawing of the player object
	if (p_player.lg_Destroyed) type = IMG_PEXPL0; else type = IMG_PLAYER0;
	gBuffer.drawImage(Elements[type+p_player.i_Frame],p_player.getScrX()+offset, p_player.getScrY() - _abs_y, 0);

        // Drawing of the moving objects
        MovingObject[] ap_mobjs = loc.ap_MovingObjects;
	int high = ap_mobjs.length-1;
        for (li = 0; li < ap_mobjs.length; li++)
        {
	    type=-1;
            MovingObject p_obj = ap_mobjs[li];
            if (p_obj.lg_Active)
            {
                switch (p_obj.i_Type)
                {
                    case MovingObject.OBJECT_DROWNINGMAN:
                        {
                            type = IMG_MAN0+p_obj.i_Frame;
                        }
                        ;
                        break;
                    case MovingObject.OBJECT_EXPLOSION:
                        {
                            type = IMG_EXPL0+p_obj.i_Frame;
                        }
                        ;
                        break;
                    case MovingObject.OBJECT_FUEL:
                        {
                            type = IMG_FUEL0+p_obj.i_Frame;
                        }
                        ;
                        break;
                    case MovingObject.OBJECT_HELICOPTERHORZ:
		        if(li>=high) type = IMG_COPTER0+p_obj.i_Frame;
                          else{
			    ap_mobjs[li]=ap_mobjs[high];
			    ap_mobjs[high--]=p_obj;
			    li--;
                          }
                        break;
                    case MovingObject.OBJECT_AIRPLANEVERT:
		        if(li>=high) type = IMG_AIR0+p_obj.i_Frame;
                          else{
			    ap_mobjs[li]=ap_mobjs[high];
			    ap_mobjs[high--]=p_obj;
			    li--;
                          }
                        break;
                    case MovingObject.OBJECT_MINE:
                        {
                            type = IMG_MINE0+p_obj.i_Frame;
                        }
                        ;
                        break;
                }

	        if(type>=0)
		   if(type>Elements.length) System.out.println("Type:"+type);
		   else
		      gBuffer.drawImage(Elements[type],p_obj.i_x+offset, p_obj.i_y - _abs_y, 0);
            }
        }
        // Output of the fuel level
//        g.setColor(Color.black);
//        g.drawString(""+(p_gsb.i8_fuelvalue>>8),0,20);

        BAR(gBuffer, 0,VisHeight-BAR_HEIGHT-2,RiverRescue_SB.I8_MAX_FUEL_VALUE, loc.i8_fuelvalue);

	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(Elements[IMG_DEMO], VisWidth>>1,VisHeight-VisHeight/3,Graphics.HCENTER|Graphics.VCENTER);
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
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    protected void paint(Graphics g) {
            g.setClip(0,0,scrwidth,scrheight);
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
//				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
				g.drawString(YourScore+client_sb.getGameStateBlock().getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
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

    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=RiverRescue_PMR.BUTTON_NONE;
       RiverRescue_GSB loc =(RiverRescue_GSB)client_sb.getGameStateBlock();
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.i_TimeDelay;
       stage = loc.getStage();
       client_pmr.i_Value = direct;
       client_sb.nextGameStep(client_pmr);
//       drawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       drawBackScreen();
       repaint();
    }

private static final int IMG_FP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_PLAYER0 = 4;
private static final int IMG_PLAYER1 = 5;
private static final int IMG_PLAYER2 = 6;
private static final int IMG_PLAYER3 = 7;
private static final int IMG_PLAYER4 = 8;
private static final int IMG_PLAYER5 = 9;
private static final int IMG_PLAYER6 = 10;
private static final int IMG_SPLASH0 = 11;
private static final int IMG_SPLASH1 = 12;
private static final int IMG_SPLASH2 = 13;
private static final int IMG_SPLASH3 = 14;
private static final int IMG_SPLASH4 = 15;
private static final int IMG_SPLASH5 = 16;
private static final int IMG_PEXPL0 = 17;
private static final int IMG_PEXPL1 = 18;
private static final int IMG_PEXPL2 = 19;
private static final int IMG_PEXPL3 = 20;
private static final int IMG_PEXPL4 = 21;
private static final int IMG_PEXPL5 = 22;
private static final int IMG_PEXPL6 = 23;
private static final int IMG_FUEL0 = 24;
private static final int IMG_FUEL1 = 25;
private static final int IMG_FUEL2 = 26;
private static final int IMG_MAN0 = 27;
private static final int IMG_MAN1 = 28;
private static final int IMG_MAN2 = 29;
private static final int IMG_COPTER0 = 30;
private static final int IMG_COPTER1 = 31;
private static final int IMG_COPTER2 = 32;
private static final int IMG_COPTER3 = 33;
private static final int IMG_MINE0 = 34;
private static final int IMG_MINE1 = 35;
private static final int IMG_MINE2 = 36;
private static final int IMG_MINE3 = 37;
private static final int IMG_AIR0 = 38;
private static final int IMG_AIR1 = 39;
private static final int IMG_AIR2 = 40;
private static final int IMG_AIR3 = 41;
private static final int IMG_EXPL0 = 42;
private static final int IMG_EXPL1 = 43;
private static final int IMG_EXPL2 = 44;
private static final int IMG_EXPL3 = 45;
private static final int IMG_EXPL4 = 46;
private static final int IMG_EXPL5 = 47;
private static final int IMG_GROUND = 48;
private static final int TOTAL_IMAGES_NUMBER = 49;

}

