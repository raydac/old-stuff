
import javax.microedition.lcdui.*;
import com.igormaznitsa.game_kit_out_6BE902.Snowboard.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;

public class GameArea extends com.igormaznitsa.midp.PhoneModels.PhoneStubImpl implements Runnable, GameActionListener,LoadListener {

  private final static int MENU_KEY = KEY_MENU;
  private final static int END_KEY = KEY_BACK;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 37; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image MenuIcon = null;
  private Image LoadImg = null;
  private Image iHeart = null;
  private Image iCloud = null;


  private Image [] Hill;
  private Image [] Fir;
  private Image [] Stone;
  private Image [] iPlayerRoad;
  private Image [] iPlayerFall;
  private Image [] iPlayerJump;

  private Image TrysIco;
  private Image ScoreIco;

  private drawDigits smallFont;
  private drawDigits bigFont;

//  private Image Buffer=null;
//  private Graphics gBuffer=null;
  private int LabyrinthHeight = 0, LabyrinthHeightAbs = 0, VisibleArea = 0;


  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

//  private final static int iSTAIRS = 0,

  private int CurrentLevel=3;
  private int stage = 0;
  protected Snowboard_PMR client_pmr = null;
  protected int direct=Snowboard_PMR.BUTTON_NONE;
  protected Snowboard_SB client_sb = null;
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  private GameSoundBlock gsb;
  private boolean isFirst;

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
  private int [] stageSleepTime = {100,100,100,200,60,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

     public static final int IMG_INFON_SIEMENS = 209; //Infon_Siemens.png
     public static final int IMG_MENUICO = 1769569; //menuico.png


 public static long [] storage = {
5927942488114331648l, 1945555040534003712l, -6496490840993562623l, 4702964862425235558l, -4463716146635573164l, -8276159864300564432l, -2115389633125000631l, -1915432779544587726l, -8643232179708091708l, -4904268192639252717l, 4531018965983428055l, 4089220766095415561l, 2886726237415350330l, -8348864285874133981l, 1283266176360859003l, 1228226418263785907l, 4117892338884029116l, 6932218993778526213l, 8325230085525390910l, -7562525395094173520l, -3238674416029381878l, -5387716821936493512l, 4606031933696670478l, -5835568900809439309l, 925309889345l, 6936297984498157824l, -126l,
5927942488114331648l, 360287970307080192l, 1050556976355869704l, 4702964381388898501l, 2675705529530452l, -2261394425466714048l, 2345879546012841255l, -5865370525394499904l, -4270371276757873702l, 2971562147760700094l, 98183410043l, 6936297984498157824l, -126l,
};

	 public final static Image getImage(int id)
	 {
	   try {
 	    byte[] img = new byte[(id&0xffff)+8];
	    id>>>=16;
	    int pos = 0;
	    long n=0;
	    while (pos < img.length) {
	      if ((pos&7)==0)
	      if (pos == 0) n = 727905341920923785l;
 	         else                 n=storage[id+(pos>>3)-1];
	      img[pos++]=(byte)n;
	      n>>>=8;
	    }
               Image ret = Image.createImage(img,0,img.length);
	       img = null;
	       System.gc();
	       return ret;
	   } catch (Exception e) {return null;}
	 }


  /**Construct the displayable*/
  public GameArea(startup m) {
    super(null);
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    client_sb = new Snowboard_SB(scrwidth,scrheight,this);
    client_pmr = new Snowboard_PMR();

//    Buffer=Image.createImage(scrwidth,scrheight);
//    gBuffer=Buffer.getGraphics();

    gameStatus=notInitialized;
    baseX = 0;//(scrwidth - SlideFrame.getWidth())>>1;
    baseY = 0;//(scrheight - SlideFrame.getHeight())>>1;
       try{
         gsb = new GameSoundBlock(this,"/res/sound.bin",this);
       }catch (Exception e){ gsb = null;}
    LoadImg = getImage(IMG_INFON_SIEMENS);
    MenuIcon = getImage(IMG_MENUICO);
    isFirst = true;
  }

///////////////////////////////////////////////////////////////////////////////////



    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void startIt() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     loading(67+58);

     try {
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);

	   Runtime.getRuntime().gc();
	   ImageBlock pnl = new ImageBlock("/res/images.bin",this);

           Title=pnl.getImageForID(IMG_FP);
           Lost=pnl.getImageForID(IMG_LOST);
           Won=pnl.getImageForID(IMG_WIN);
           DemoTitle = pnl.getImageForID(IMG_DEMO);
	   iCloud = pnl.getImageForID(IMG_OBLAKA);
	   TrysIco = pnl.getImageForID(IMG_BOARD);

	   smallFont = new drawDigits(pnl.getImageForID(IMG_NFONT));
	   bigFont = new drawDigits(pnl.getImageForID(IMG_BIGFONT));

	   Hill = new Image[Obstacle.MAXFRAMES];
	   Hill[0] = pnl.getImageForID(IMG_HILL0);
	   Hill[1] = pnl.getImageForID(IMG_HILL1);
	   Hill[2] = pnl.getImageForID(IMG_HILL2);
	   Hill[3] = pnl.getImageForID(IMG_HILL3);
	   Hill[4] = pnl.getImageForID(IMG_HILL4);
	   Hill[5] = pnl.getImageForID(IMG_HILL5);
	   Hill[6] = pnl.getImageForID(IMG_HILL6);
	   Hill[7] = pnl.getImageForID(IMG_HILL7);
	   Hill[8] = pnl.getImageForID(IMG_HILL8);
	   Hill[9] = pnl.getImageForID(IMG_HILL9);
	   Hill[10] = pnl.getImageForID(IMG_HILL10);
	   Hill[11] = pnl.getImageForID(IMG_HILL11);
	   Hill[12] = pnl.getImageForID(IMG_HILL12);
	   Hill[13] = pnl.getImageForID(IMG_HILL13);

	   Fir = new Image[Obstacle.MAXFRAMES];
	   Fir[0] = pnl.getImageForID(IMG_FIR0);
	   Fir[1] = pnl.getImageForID(IMG_FIR1);
	   Fir[2] = pnl.getImageForID(IMG_FIR2);
	   Fir[3] = pnl.getImageForID(IMG_FIR3);
	   Fir[4] = pnl.getImageForID(IMG_FIR4);
	   Fir[5] = pnl.getImageForID(IMG_FIR5);
	   Fir[6] = pnl.getImageForID(IMG_FIR6);
	   Fir[7] = pnl.getImageForID(IMG_FIR7);
	   Fir[8] = pnl.getImageForID(IMG_FIR8);
	   Fir[9] = pnl.getImageForID(IMG_FIR9);
	   Fir[10] = pnl.getImageForID(IMG_FIR10);
	   Fir[11] = pnl.getImageForID(IMG_FIR11);
	   Fir[12] = pnl.getImageForID(IMG_FIR12);
	   Fir[13] = pnl.getImageForID(IMG_FIR13);

	   Stone = new Image[Obstacle.MAXFRAMES];
	   Stone[0] = pnl.getImageForID(IMG_STONE0);
	   Stone[1] = pnl.getImageForID(IMG_STONE1);
	   Stone[2] = pnl.getImageForID(IMG_STONE2);
	   Stone[3] = pnl.getImageForID(IMG_STONE3);
	   Stone[4] = pnl.getImageForID(IMG_STONE4);
	   Stone[5] = pnl.getImageForID(IMG_STONE5);
	   Stone[6] = pnl.getImageForID(IMG_STONE6);
	   Stone[7] = pnl.getImageForID(IMG_STONE7);
	   Stone[8] = pnl.getImageForID(IMG_STONE8);
	   Stone[9] = pnl.getImageForID(IMG_STONE9);
	   Stone[10] = pnl.getImageForID(IMG_STONE10);
	   Stone[11] = pnl.getImageForID(IMG_STONE11);
	   Stone[12] = pnl.getImageForID(IMG_STONE12);
	   Stone[13] = pnl.getImageForID(IMG_STONE13);

	   iPlayerFall = new Image[Player.MAXFRAMES_FALLENSTATE];
	   iPlayerFall[0] = pnl.getImageForID(IMG_DROP0);
	   iPlayerFall[1] = pnl.getImageForID(IMG_DROP1);
	   iPlayerFall[2] = pnl.getImageForID(IMG_DROP2);

	   iPlayerJump = new Image[Player.MAXFRAMES_JUMPSTATE];
	   iPlayerJump[0] = pnl.getImageForID(IMG_JUMP0);
	   iPlayerJump[1] = pnl.getImageForID(IMG_JUMP1);
	   iPlayerJump[2] = pnl.getImageForID(IMG_JUMP2);

	   iPlayerRoad = new Image[Player.MAXFRAMES_ROADSTATE];
	   iPlayerRoad[0] = pnl.getImageForID(IMG_PLAYER0);
	   iPlayerRoad[1] = pnl.getImageForID(IMG_PLAYER1);
	   iPlayerRoad[2] = pnl.getImageForID(IMG_PLAYER2);
	   iPlayerRoad[3] = pnl.getImageForID(IMG_PLAYER3);
	   iPlayerRoad[4] = pnl.getImageForID(IMG_PLAYER4);
	   iPlayerRoad[5] = pnl.getImageForID(IMG_PLAYER5);
	   iPlayerRoad[6] = pnl.getImageForID(IMG_PLAYER6);

	   pnl = null;
     } catch(Exception e) {
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }

     Runtime.getRuntime().gc();
    }


    /**
     * Initing of the player before starting of a new game session
     */

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
       direct=Snowboard_PMR.BUTTON_NONE;
       client_pmr.i_value=direct;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	   // client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.getTimeDelay();
//	   lastPlayerScores = 0;
	   stage = 0;
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
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    { stopAllMelodies(); ticks=FINAL_PICTURE_OBSERVING_TIME+1; }
	      case demoPlay:
              case titleStatus : {
				    isFirst=false;
	                            this.stopAllMelodies();

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
				      case Canvas.KEY_NUM4: direct=Snowboard_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM6: direct=Snowboard_PMR.BUTTON_RIGHT; break;
				   default:direct=Snowboard_PMR.BUTTON_NONE;
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Snowboard_PMR.BUTTON_NONE;
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
                        if(isFirst && gsb!=null && ((startup)midlet)._Sound){isFirst = false; gsb.playSound(SND_MAINTHEME_OTT, false);}
			if (ticks<80) ticks++;
			 else
			  {
			   System.gc();
			   ticks=(1<<4)+15;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
			   client_pmr.i_value=(ticks>>4)&0x3;
		           client_sb.nextGameStep(client_pmr);
			   //UpdateSlideFrame();
			   Snowboard_GSB loc=(Snowboard_GSB)client_sb.getGameStateBlock();
			   if (loc.getPlayerState()!=Snowboard_GSB.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=Snowboard_PMR.BUTTON_NONE;
				client_pmr.i_value=direct;
				client_sb.nextGameStep(client_pmr);

				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
	                   //if(ticks++>100){Runtime.getRuntime().gc();ticks=0;}
			   blink = (ticks++&2)==0;
			   client_pmr.i_value=direct;

//			   client_pmr.i_value=direct;
			   client_sb.nextGameStep(client_pmr);
			   //UpdateSlideFrame();
			   Snowboard_GSB loc=(Snowboard_GSB)client_sb.getGameStateBlock();
/*
			   if (loc.getGameState()==Snowboard_GSB.GAMESTATE_OVER) {
			       switch (loc.getPlayerState()) {
				      case Snowboard_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Snowboard_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case Snowboard_GSB.PLAYERSTATE_LOST: ticks =0;
					                                     gameStatus=Crashed;
									     if(gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_DROP_OTT, false);
									     break;
			                case Snowboard_GSB.PLAYERSTATE_WON: ticks = 0;
//								       stage++;
//					                               if (stage>=Stages.STAGES_NUMBER)
								         gameStatus=Finished;
									 if(gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_WONTHEME_OTT, false);
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
		             direct=Snowboard_PMR.BUTTON_NONE;
			     if (((Snowboard_GSB)client_sb.getGameStateBlock()).getGameState()==Snowboard_GSB.GAMESTATE_OVER){
			            gameStatus=gameOver;
				    if(gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_LOSTTHEME_OTT, false);
			     } else {
			           client_sb.resumeGame();
			           gameStatus=playGame;
				   System.gc();
			      }
			   }
			   repaint();
			   break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
			     isFirst = true;
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


    public void gameAction(int actionID){
         if(gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_JUMP_OTT, false);
    }
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){}
    public void gameAction(int x,int y){
//	 switch(actionID) {
//	 case Snowboard_SB.GAMEACTION_ERASEBLOCK:
		  {
//		       gSlideFrame.fillRect(x*Snowboard_SB.VIRTUAL_CELL_WIDTH,(y%FrameHeight)*Snowboard_SB.VIRTUAL_CELL_HEIGHT,Snowboard_SB.VIRTUAL_CELL_WIDTH,Snowboard_SB.VIRTUAL_CELL_HEIGHT);
		  }
//	 }
    }
/*
    private Image getImageElement(int bb){
                switch(bb)
                {
                    case Stages.ELE_STAIRS : return iElements[iSTAIRS];
                    case Stages.ELE_STOPCOCK : return iElements[iSTOPCOCK];
                    case Stages.ELE_WALL0 : return iElements[iWALL0];
                    case Stages.ELE_WALL1 : return iElements[iWALL1];
                    case Stages.ELE_WALL2 : return iElements[iWALL2];
                    case Stages.ELE_WALL3 : return iElements[iWALL3];
                    case Stages.ELE_WALL4 : return iElements[iWALL4];
                    case Stages.ELE_HEART : return iElements[iHeartEle];
                }
	      return null;
    }


*/

static int []scores = {100,150,200,250,300};

    protected void DoubleBuffer(Graphics gBuffer){

//      if (bufferChanged) {

        Image img = null;
	// prepare screen
        Snowboard_GSB loc=(Snowboard_GSB)client_sb.getGameStateBlock();

        gBuffer.setColor(0xffffff);
        gBuffer.drawImage(iCloud,0,0,0);
        gBuffer.fillRect(0,client_sb.I_OBSTACLE_STARTY,scrwidth,scrheight);

        // Draw horizon
        gBuffer.setColor(0x0);
//        gBuffer.drawLine(0,client_sb.I_OBSTACLE_STARTY,scrwidth,20);

        // Draw objects

        // Obstacles
	int pos = loc.i_BorderPosition;
        for (int li = 0;li < Snowboard_SB.MAX_OBSTACLES;li++)
        {
	    if(--pos<0)pos =loc.ap_obstacles.length-1;
            Obstacle p_obj = loc.ap_obstacles[pos
	    ];
            if (!p_obj.lg_isactive) continue;

	 try {
            switch (p_obj.b_type)
            {
                case Obstacle.OBSTACLE_FIR:  img = Fir[p_obj.i_frame]; break;
                case Obstacle.OBSTACLE_HILL:  img = Hill[p_obj.i_frame]; break;
                case Obstacle.OBSTACLE_STONE:  img = Stone[p_obj.i_frame]; break;
		default: img = null;
            }
         } catch (Exception e){System.out.println("Obstacle, type:"+p_obj.b_type+", frame:"+p_obj.i_frame); img = null;}

	    if(img!=null)
            gBuffer.drawImage(img,p_obj.i_scrX,p_obj.i_scrY - p_obj.ab_cursizearray[(p_obj.i_frame << 1) + 1],0); //p_obj.ab_cursizearray[p_obj.i_frame << 1],p_obj.ab_cursizearray[(p_obj.i_frame << 1) + 1]
        }

        // Player
//        gBuffer.setColor(Color.orange);

         try{
            switch (loc.p_player.i_state)
            {
                case Player.STATE_FALLEN:  img = iPlayerFall[loc.p_player.i_frame]; break;
                case Player.STATE_JUMP:  img = iPlayerJump[loc.p_player.i_frame]; break;
                case Player.STATE_ROAD:  img = iPlayerRoad[loc.p_player.i_frame]; break;
		default: img = null;
            }
         } catch (Exception e){System.out.println("Player, state:"+loc.p_player.i_state+", frame:"+loc.p_player.i_frame); img = null;}

        if(img!=null)
          gBuffer.drawImage(img,loc.p_player.i_scrx,loc.p_player.i_scry,0); //Snowboard_SB.PLAYER_FRAMEWIDTH,Snowboard_SB.PLAYER_FRAMEHEIGHT

        // Scores

        if (client_sb.getScoreObject().isActive()) {
            Score p_sc = client_sb.getScoreObject();
            smallFont.drawDigits(gBuffer,p_sc.getScrX(),p_sc.getScrY(),3,scores[p_sc.getType()]);
        }

	bigFont.drawDigits(gBuffer,/*baseX*/ 0,/*baseY+*/0,5,Math.max(0,loc.getPlayerScore()));


        if(!(gameStatus == Crashed && blink)){
//	   gBuffer.drawImage(iHeart,/*baseX+*/scrwidth-(fontWidth<<1)-10-iHeart.getWidth(),/*baseY+*/scrheight-iHeart.getHeight()-2,0);
	   for (int i=0;i<loc.i_Attemptions-1;i++)
             gBuffer.drawImage(TrysIco,/*baseX+*/scrwidth-(i+1)*(TrysIco.getWidth()+2)-10,/*baseY+*/ scrheight-2-TrysIco.getHeight() ,0);
        }
	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(DemoTitle, scrwidth>>1,scrheight-DemoTitle.getHeight()-5/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}
//        gBuffer.setColor(0x0);
//        gBuffer.drawRect(baseX,baseY,levelImage.getWidth(),levelImage.getHeight());
	bufferChanged=false;
//System.out.println(System.currentTimeMillis() - time);

    }
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    private final static int i_BAR_HEIGHT = 6;
    protected void paint(Graphics g) {

            switch (gameStatus) {
	      case notInitialized:  {
              		  int l = (scrwidth-LoadImg.getWidth())>>1;
			  int h = (scrheight-LoadImg.getHeight()-i_BAR_HEIGHT)>>1;
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
	                  g.drawImage(LoadImg,l++,h,0);
	                  g.drawRect(l,h+LoadImg.getHeight(), LoadImg.getWidth()-3,i_BAR_HEIGHT);
	                  g.fillRect(l,h+LoadImg.getHeight(), (LoadImg.getWidth()-3)*LoadingNow/Math.max(1,LoadingTotal),i_BAR_HEIGHT);
	                  } break;
              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
		          g.setColor(0x00000);    // drawin' flyin' text
	                      g.fillRect(0, 0, scrwidth,scrheight);
			  g.setColor(0xffffff);
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
	                  } break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateBlock().getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :  // if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			 //     }
			 //     else {
//		                DoubleBuffer(gBuffer);
//		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
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
       direct=Snowboard_PMR.BUTTON_NONE;
       Snowboard_GSB loc =(Snowboard_GSB)client_sb.getGameStateBlock();
       CurrentLevel=loc.getLevel();
//       stageSleepTime[playGame] = client_sb.getTimeDelay();
       stage = loc.getStage();
       client_pmr.i_value=direct;
       client_sb.nextGameStep(client_pmr);
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }

private static final byte IMG_FP = (byte)0;
private static final byte IMG_WIN = (byte)1;
private static final byte IMG_LOST = (byte)2;
private static final byte IMG_FIR0 = (byte)3;
private static final byte IMG_FIR1 = (byte)4;
private static final byte IMG_DROP0 = (byte)5;
private static final byte IMG_FIR2 = (byte)6;
private static final byte IMG_FIR3 = (byte)7;
private static final byte IMG_DROP1 = (byte)8;
private static final byte IMG_DROP2 = (byte)9;
private static final byte IMG_BIGFONT = (byte)10;
private static final byte IMG_OBLAKA = (byte)11;
private static final byte IMG_PLAYER0 = (byte)12;
private static final byte IMG_PLAYER1 = (byte)13;
private static final byte IMG_FIR4 = (byte)14;
private static final byte IMG_JUMP2 = (byte)15;
private static final byte IMG_PLAYER2 = (byte)16;
private static final byte IMG_JUMP1 = (byte)17;
private static final byte IMG_PLAYER3 = (byte)18;
private static final byte IMG_PLAYER4 = (byte)19;
private static final byte IMG_JUMP0 = (byte)20;
private static final byte IMG_FIR5 = (byte)21;
private static final byte IMG_PLAYER6 = (byte)22;
private static final byte IMG_PLAYER5 = (byte)23;
private static final byte IMG_FIR6 = (byte)24;
private static final byte IMG_FIR7 = (byte)25;
private static final byte IMG_STONE2 = (byte)26;
private static final byte IMG_STONE0 = (byte)27;
private static final byte IMG_STONE1 = (byte)28;
private static final byte IMG_STONE3 = (byte)29;
private static final byte IMG_FIR8 = (byte)30;
private static final byte IMG_STONE4 = (byte)31;
private static final byte IMG_FIR9 = (byte)32;
private static final byte IMG_STONE5 = (byte)33;
private static final byte IMG_NFONT = (byte)34;
private static final byte IMG_HILL0 = (byte)35;
private static final byte IMG_STONE6 = (byte)36;
private static final byte IMG_HILL1 = (byte)37;
private static final byte IMG_DEMO = (byte)38;
private static final byte IMG_HILL2 = (byte)39;
private static final byte IMG_BOARD = (byte)40;
private static final byte IMG_FIR10 = (byte)41;
private static final byte IMG_STONE8 = (byte)42;
private static final byte IMG_STONE7 = (byte)43;
private static final byte IMG_HILL3 = (byte)44;
private static final byte IMG_HILL4 = (byte)45;
private static final byte IMG_STONE9 = (byte)46;
private static final byte IMG_HILL5 = (byte)47;
private static final byte IMG_STONE10 = (byte)48;
private static final byte IMG_FIR11 = (byte)49;
private static final byte IMG_STONE11 = (byte)50;
private static final byte IMG_HILL6 = (byte)51;
private static final byte IMG_HILL7 = (byte)52;
private static final byte IMG_FIR12 = (byte)53;
private static final byte IMG_HILL8 = (byte)54;
private static final byte IMG_HILL9 = (byte)55;
private static final byte IMG_STONE12 = (byte)56;
private static final byte IMG_HILL10 = (byte)57;
private static final byte IMG_HILL11 = (byte)58;
private static final byte IMG_STONE13 = (byte)59;
private static final byte IMG_HILL13 = (byte)60;
private static final byte IMG_HILL12 = (byte)61;
private static final byte IMG_FIR13 = (byte)62;

public static final byte SND_MAINTHEME_OTT = (byte)0;
public static final byte SND_LOSTTHEME_OTT = (byte)1;
public static final byte SND_WONTHEME_OTT = (byte)2;
public static final byte SND_JUMP_OTT = (byte)3;
public static final byte SND_DROP_OTT = (byte)4;

}

