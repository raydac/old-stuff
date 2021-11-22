
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.ShipsDuel.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private final static int BAR_WIDTH = 20;
  private final static int BAR_HEIGHT = 5;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  private Thread thread=null;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image blinkImage=null;
  private int blinkX=0,blinkY=0, blinkDelay=2;//in ticks defined by singleSleepTime
  private Image levelImage=null;

  public int fontHeight=0,fontWidth=0, TitleN = 0;
/*
  public final static int iPlayerShip = 0,
                          iEnemyShip = 1,
                          iCloud = 2,
                          iIsland = 3,
		          iBombIco = 4,
		          iCrewIco = 5,
			  iBomb = 6,
			  iExpl = 7,
			  iWater = iExpl+ShipsDuel_SB.EXPLODE_STAGES;
*/
  private Image []items;//=new Image[iWater+ShipsDuel_SB.WATER_EXPLODE_STAGES+1];
  private Image []numbers=new Image[10];

  private int CurrentLevel=0;
  protected ShipsDuel_PMR client_pmr = null;
  protected int direct=ShipsDuel_PMR.DIRECT_NONE;
  protected ShipsDuel_SB client_sb = new ShipsDuel_SB();
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private final static int RocketDeviation = 0x100;
  private int RoadStep=4,TankPos=0, TankMoveDelay =0;

  /**
   *  константы обозначающие различные состояния игры,
   *  флаг состояния
   */
  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
                            playGame = 3,
			    Finished = 4,
			    gameOver = 5;
  private int [] stageSleepTime = {100,100,100,100,500,500}; // in ms

  public int gameStatus=notInitialized;
  private int demoLevel = ShipsDuel_SB.DEMO_LEVEL;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  private boolean playMelody = true;
  public boolean MelodyStateOn = true;
  private static final byte []notesIntro = new byte[] {18,4,58,5,
                                                       18,4,58,5,
						       18,4,58,5,
						       14,5,58,6,
						       14,5,58,6,
						       14,5,58,6,
						       61,2};

  private boolean isShown = false;
  private Image MenuIcon = null;
  private int LoadingTotal =0, LoadingNow = 0, ticks=0;
  public boolean LanguageCallBack = false, keyReverse = false;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;


    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void start(int kkk) {
     start();
     loading(kkk);

     try {
           items = (new ImageBlock(this))._image_array;

	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title = items[IMG_SHIPDUEL];
           levelImage=items[IMG_BACK];
           Lost=items[IMG_LOST];
           Won=items[IMG_WON];
           DemoTitle=items[IMG_DEMO];
             setBlinkImage(DemoTitle);
/*
		items[iPlayerShip]=items[IMG_SHIP];
		items[iEnemyShip]=items[IMG_OPPONENT];
		items[iCloud]=items[IMG_CLOUD];
		items[iIsland]=items[IMG_ISLAND];
		items[iBombIco]=items[IMG_SHELLICO];
		items[iCrewIco]=items[IMG_CREWICO];
		items[iBomb]=items[IMG_SHELL];
		items[iExpl]=items[IMG_EXPL0];
		items[iExpl+1]=items[IMG_EXPL1];
		items[iExpl+2]=items[IMG_EXPL2];
		items[iExpl+3]=items[IMG_EXPL3];
		items[iWater]=items[IMG_WEXPL0];
		items[iWater+1]=items[IMG_WEXPL1];
		items[iWater+2]=items[IMG_WEXPL2];
		items[iWater+3]=items[IMG_WEXP3];
		items[iWater+4]=items[iWater+3];
		items[iWater+5]=items[iWater+2];
		items[iWater+6]=items[iWater+1];
		items[iWater+7]=items[iWater];
*/
          Image tmp=items[IMG_NFONT];//Image.createImage(FontImage];
          fontHeight = tmp.getHeight();
          fontWidth = tmp.getWidth()/10;
          for (int i = 1;i<10; i++){
	      numbers[i] = Image.createImage(fontWidth,fontHeight);
	      numbers[i].getGraphics().drawImage(tmp,-(i-1)*fontWidth,0,Graphics.TOP|Graphics.LEFT);
          }
	  numbers[0] = Image.createImage(fontWidth,fontHeight);
	  numbers[0].getGraphics().drawImage(tmp,-9*fontWidth,0,Graphics.TOP|Graphics.LEFT);
 	       nextItemLoaded(11);
	  tmp = null;
//	  pnl = null;

     } catch(Exception e) {
//       System.out.println("Can't read images");
//       e.printStackTrace();
     }

     System.gc();

    }

    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
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

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

    client_sb.setPlayerBlock(this);
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    gameStatus=notInitialized;
  }

   void setBlinkImage(Image img){
      blinkImage=img;
      blinkX=scrwidth>>1;
      blinkY=scrheight-blinkImage.getHeight()-5/*bottomMargin*/;
   }
///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

//     if (gameStatus != demoPlay)
         client_pmr.setDirect(direct);
	 return  client_pmr;
    }

    public void start() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
    }

    public void playMelody(byte[] m, int bpm){
      if (m!=null && m.length>0 && (m.length&1)==0)    // not null, not empty , and had parity
        try
        {
/*
            MelodyComposer P = new MelodyComposer();
            P.setBPM(bpm);
	    for(int i=0;i<m.length;i++)
                 P.appendNote(m[i++],m[i]);
            Melody melody = P.getMelody();
            melody.play();
*/
        }
        catch(Exception exception)
        {
            //System.out.println("Melody stuff is broke");
        }
    }


    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new ShipsDuel_PMR(ShipsDuel_PMR.DIRECT_NONE);

    }

    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       direct=ShipsDuel_PMR.DIRECT_NONE;
       CurrentLevel=level;
       baseX=((scrwidth-levelImage.getWidth())>>1);
       baseY=((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGame(demoLevel);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGame(CurrentLevel);
	   gameStatus=playGame;
	}
	bufferChanged=true;
	repaint();
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
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
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
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true); else
	                       switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM2/*UP*/: direct=ShipsDuel_PMR.DIRECT_POWER_MORE; break;
	                              case Canvas.KEY_NUM8/*DOWN*/: direct=ShipsDuel_PMR.DIRECT_POWER_LESS; break;
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=ShipsDuel_PMR.DIRECT_ANGLE_LEFT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=ShipsDuel_PMR.DIRECT_ANGLE_RIGHT; break;
				      case Canvas.KEY_NUM5/*FIRE*/: direct=ShipsDuel_PMR.DIRECT_FIRE; break;

				default:direct=ShipsDuel_PMR.DIRECT_NONE;
	                       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=ShipsDuel_PMR.DIRECT_NONE;
        }
    }


    public void run() {
     long workDelay = System.currentTimeMillis();
     long sleepy = 0;

      int blinks=0;
      while(animation) {
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus:
//	                if (playMelody){
//			   playMelody(notesIntro,35);
//			   playMelody = false;
//	                }
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   System.gc();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   blink=(ticks&8)==0;
			   ShipsDuel_GSR loc=(ShipsDuel_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==ShipsDuel_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       System.gc();
			   }

			   repaint();
			  } break;
           case playGame :{
			   client_sb.nextGameStep();
			   ShipsDuel_GSR loc=(ShipsDuel_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==ShipsDuel_GSR.GAMESTATE_OVER) {
			       ticks = 0;
			       blink = true;
			     switch (loc.getPlayerState()) {
			        case ShipsDuel_GSR.PLAYER_OUT_OF_AMMO: gameStatus=gameOver; break;
			        case ShipsDuel_GSR.PLAYER_FINISHED: gameStatus=Finished; break;
			        case ShipsDuel_GSR.PLAYER_KILLED: gameStatus=gameOver; break;
			     }
			   }
			   repaint();
			  } break;
/*
	    case outOfAmmo:
	    case crashCar:
			   ticks++; blink=(ticks&2)==0;
			   if (ticks >= 10) {
			      client_sb.resumeGame();
		              direct=ShipsDuel_PMR.DIRECT_NONE;
			      gameStatus=playGame;
			      ticks = 0;
			      blink = false;
			   }
			   repaint();
			   break;
			   */
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			      midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
			   repaint();
			   break;

 	 }

        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
          else
          if(workDelay>0)
             Thread.sleep(workDelay);

	} catch (Exception e) {}

	workDelay = System.currentTimeMillis();


      }
    }

    public void drawDigits(Graphics gBuffer, int x, int y, int vacant, int num)
    {
        x = x + fontWidth * (--vacant);
        if (num >= 0)
            for (; vacant >= 0; vacant--)
            {
                gBuffer.drawImage(numbers[num % 10], x, y, 0/* Graphics.LEFT|Graphics.TOP*/);
                num = num / 10;
                x -= fontWidth;
            }
    }
/*
    private void drawDigits(Graphics gBuffer,int x,int y,int vacant,int num){
      String a = new String("000000"+num);
      if (vacant>=a.length()) return;
      int b = a.length()-vacant;
      int _x=x;
      Image d;
      for (int i = 0; i < vacant; i++){
	d = numbers[(byte)a.charAt(b+i)-(byte)'0'];
         gBuffer.drawImage(d,_x, y, Graphics.LEFT|Graphics.TOP);
	 _x+=d.getWidth();
      }
//	  System.out.println(", "+a);
    }
*/

    protected void DoubleBuffer(Graphics gBuffer){
//      if (bufferChanged) {

	gBuffer.drawImage(levelImage,baseX,baseY,Graphics.TOP|Graphics.LEFT);

        ShipsDuel_GSR loc=(ShipsDuel_GSR)client_sb.getGameStateRecord();

	Ship pl = loc.getPlayer();
	Ship en = loc.getEnemy();

	gBuffer.drawImage(items[IMG_SHIP],baseX+pl._x,baseY+pl._y,0);
	gBuffer.drawImage(items[IMG_OPPONENT],baseX+en._x,baseY+en._y,0);

	int o = baseX+items[IMG_SHELLICO].getWidth()+3;
	gBuffer.drawImage(items[IMG_SHELLICO],baseX+3,baseY,0);
	drawDigits(gBuffer,++o,baseY,2,pl._rockets);
	gBuffer.drawImage(items[IMG_CREWICO],(o=o+3+fontWidth*2),baseY,0);
	drawDigits(gBuffer,o+items[IMG_CREWICO].getWidth()+1,baseY,3,pl._men_left);

	o = baseX+levelImage.getWidth()-items[IMG_SHELLICO].getWidth()-2-fontWidth*3;
	gBuffer.drawImage(items[IMG_CREWICO],o,baseY,0);
	drawDigits(gBuffer,o+items[IMG_SHELLICO].getWidth()+1,baseY,3,en._men_left);




//	gBuffer.drawRect(pl._x,pl._y,ShipsDuel_SB.SHIP_WIDTH,ShipsDuel_SB.SHIP_HEIGHT);
//	gBuffer.drawRect(en._x,en._y,ShipsDuel_SB.SHIP_WIDTH,ShipsDuel_SB.SHIP_HEIGHT);



	gBuffer.drawImage(items[IMG_ISLAND],baseX+ShipsDuel_SB.ISLAND_START_X,baseY+ShipsDuel_SB.GROUND_POSITION-ShipsDuel_SB.ISLAND_HEIGHT,0);

	int [] r = loc.getRocket();
	if (r!=null){
	  if(r[4]==0)
	     gBuffer.drawImage(items[IMG_SHELL],baseX+(r[0]>>8),baseY+(r[1]>>8),0);
	   else
	     if(!loc.isWaterExpl)
	        gBuffer.drawImage(items[IMG_EXPL0+r[4]-1],baseX+(r[0]>>8),baseY+(r[1]>>8),Graphics.BOTTOM|Graphics.HCENTER);
	      else
	        gBuffer.drawImage(items[IMG_WEXPL0+r[4]-1],baseX+(r[0]>>8),baseY+(r[1]>>8),Graphics.BOTTOM|Graphics.HCENTER);
	} else
	  if(loc._player_turn){
	    int y = baseY+levelImage.getHeight()-BAR_HEIGHT-1;
	    int dxpl = Math.max((pl._power-pl.MIN_POWER)*(BAR_WIDTH-3)/(pl.MAX_POWER-pl.MIN_POWER),0);

	    gBuffer.setColor(0xffffff);
	    gBuffer.fillRect(baseX+10,y,BAR_WIDTH,BAR_HEIGHT);

            gBuffer.setColor(0x0);
            gBuffer.drawLine(baseX+pl._gun_x1,baseY+pl._gun_y1,baseX+pl._gun_x2,baseY+pl._gun_y2);
	    gBuffer.drawRect(baseX+10,y,BAR_WIDTH,BAR_HEIGHT);
	    gBuffer.fillRect(baseX+10+2,y+2,dxpl,BAR_HEIGHT-3);

	  } else {

	    int xe = baseX+levelImage.getWidth()-10-BAR_WIDTH;
	    int y = baseY+levelImage.getHeight()-BAR_HEIGHT-1;
	    int dxen = Math.max((en._power-en.MIN_POWER)*(BAR_WIDTH-3)/(en.MAX_POWER-en.MIN_POWER),0);

            gBuffer.setColor(0xffffff);
	    gBuffer.fillRect(xe,y,BAR_WIDTH,BAR_HEIGHT);

            gBuffer.setColor(0x0);
            gBuffer.drawLine(baseX+en._gun_x1,baseY+en._gun_y1,baseX+en._gun_x2,baseY+en._gun_y2);
	    gBuffer.drawRect(xe,y,BAR_WIDTH,BAR_HEIGHT);
	    gBuffer.fillRect(xe+2,y+2,dxen,BAR_HEIGHT-3);

	  }

	int [] c = loc.getFlowsX();
	for(int i = 0;i<c.length;i++)
	   gBuffer.drawImage(items[IMG_CLOUD],baseX+c[i],baseY+ShipsDuel_SB.FLOW_TOP+loc._flows_step*i,0);






/*
   int offs=2+items[iRocketUpLeft].getWidth()+1;
   if (gameStatus != outOfAmmo || (gameStatus == outOfAmmo && blink)){
        gBuffer.drawImage(items[iRocketUpLeft],2,1,Graphics.TOP|Graphics.LEFT);
	drawDigits(gBuffer,offs,0,3,loc._rockets_left);
   }
   offs+=fontWidth*3+4;
   gBuffer.drawImage(items[iUfoIcon],offs,1,Graphics.TOP|Graphics.LEFT);
   drawDigits(gBuffer,offs+items[iUfoIcon].getWidth()+1,0,3,(loc._ufo_total-loc._ufo_fired+1));
*/


        gBuffer.setColor(0x0);
	if (gameStatus == demoPlay && blink && blinkImage!=null) {
	       gBuffer.drawImage(blinkImage,blinkX,blinkY,Graphics.HCENTER|Graphics.VCENTER);
	}
//        gBuffer.drawRect(baseX,baseY,levelImage.getWidth(),levelImage.getHeight());

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
	                  gBuffer.setColor(0xffffff);gBuffer.fillRect(0, 0, scrwidth,scrheight);
		          gBuffer.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  gBuffer.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString("Loading ...["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1)-14,Graphics.TOP|Graphics.HCENTER);
	                  gBuffer.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  gBuffer.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  gBuffer.setFont(f);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);

	                  } break;

              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(Title,scrwidth,0,Graphics.RIGHT|Graphics.TOP);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :   if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			      }
			      else {
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT); break;
			      }
			   g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded(){
       blink=false;
       direct=ShipsDuel_PMR.DIRECT_NONE;
       //CurrentLevel=level;
       baseX=((scrwidth-levelImage.getWidth())>>1);
       baseY=((scrheight-levelImage.getHeight())>>1);
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final byte IMG_SHIPDUEL = (byte)0;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_WON = (byte)2;
private static final byte IMG_BACK = (byte)3;
private static final byte IMG_DEMO = (byte)4;
private static final byte IMG_SHIP = (byte)5;
private static final byte IMG_OPPONENT = (byte)6;
private static final byte IMG_CLOUD = (byte)7;
private static final byte IMG_ISLAND = (byte)8;
private static final byte IMG_SHELLICO = (byte)9;
private static final byte IMG_CREWICO = (byte)10;
private static final byte IMG_SHELL = (byte)11;
private static final byte IMG_EXPL0 = (byte)12;
private static final byte IMG_EXPL1 = (byte)13;
private static final byte IMG_EXPL2 = (byte)14;
private static final byte IMG_EXPL3 = (byte)15;
private static final byte IMG_WEXPL0 = (byte)16;
private static final byte IMG_WEXPL1 = (byte)17;
private static final byte IMG_WEXPL2 = (byte)18;
private static final byte IMG_WEXP3 = (byte)19;
private static final byte IMG_NFONT = (byte)24;
private static final byte IMG__WE1 = (byte)20;
private static final byte IMG__WE2 = (byte)21;
private static final byte IMG__WE3 = (byte)22;
private static final byte IMG__WE4 = (byte)23;
private static final byte TOTAL_IMAGES_NUMBER = (byte)25;

}

/*
.
SHIPDUEL,0
LOST,1
WON,2
BACK,3
DEMO,4

SHIP,5
OPPONENT,6
CLOUD,7
ISLAND,8
SHELLICO,9
CREWICO,10
SHELL,11
EXPL0,12
EXPL1,13
EXPL2,14
EXPL3,15
WEXPL0,16
WEXPL1,17
WEXPL2,18
WEXP3,19

NFONT,20
.
.
*/
