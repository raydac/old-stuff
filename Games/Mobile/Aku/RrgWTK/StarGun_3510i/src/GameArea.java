
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.StarGun.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
//import com.nokia.mid.ui.*;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  /**
   *  технологические переменные
   */
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

  public final static int iBomb = 0,
		          iRocketUpLeft = 1,
		          iRocketUpRight = 2,
		          iRocketUp = 3,
			  iTank = 4,
			  iTankAmount = 4,
			  iUfo = 8,
			  iBombCrash = 9,
			  iBombCrashLength = 3,
			  iGroundCrash = 12,
			  iGroundCrashLength = 3,
			  iUfoCrash = 15,
			  iUfoCrashLength = 6,
			  iHeart = 21,
			  iUfoIcon = 22;

  private Image []numbers=new Image[10];
  private Image [] items;

  private int CurrentLevel=0;
  protected int direct=StarGun_SB.DIRECT_NONE;
  protected StarGun_SB client_sb = new StarGun_SB();
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
			    crashCar = 4,
			    outOfAmmo = 5,
			    Finished = 6,
			    gameOver = 7;
  private int [] stageSleepTime = {100,100,100,100,200,200,500,500}; // in ms

  public int gameStatus=notInitialized;
  private int demoLevel = 1;
  private int demoPosition = 0;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;

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
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     loading(TOTAL_IMAGES_NUMBER+67);

     try {

           items = (new ImageBlock(this))._image_array;

	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title = items[IMG_STARGUNFP];
           levelImage=items[IMG_3];//	  Image.createImage(LevelImage];
           Lost=items[IMG_LOST];//Image.createImage(TitleImage];
           Won=items[IMG_WON];//Image.createImage(TitleImage];
           DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage];
             setBlinkImage(DemoTitle);
/*
		items[IMG_[0]=items[IMG_BOMB3];
		items[IMG_[1]=items[IMG_BULL1];
		items[IMG_[2]=items[IMG_BULL2];
		items[IMG_[3]=items[IMG_BULL3];
		items[IMG_[4]=items[IMG_TANK];
		items[IMG_[5]=items[IMG_TANK1];
		items[IMG_[6]=items[IMG_TANK2];
		items[IMG_[7]=items[IMG_TANK3];
		items[IMG_[8]=items[IMG_UFO3];
		items[IMG_[9]=items[IMG_ER1];
		items[IMG_[10]=items[IMG_ER1];


		items[IMG_[11]=items[IMG_ER2];
		items[IMG_[12]=items[IMG_ERB1];
		items[IMG_[13]=items[IMG_ERB2];
		items[IMG_[14]=items[IMG_ERB3];
		items[IMG_[15]=items[IMG_UFO5_1];
		items[IMG_[16]=items[IMG_UFO5_2];
		items[IMG_[17]=items[IMG_UFO5_3];
		items[IMG_[18]=items[IMG_UFO5_4];
		items[IMG_[19]=items[IMG_UFO5_5];
		items[IMG_[20]=items[IMG_UFO5_6];
		items[IMG_[21]=items[IMG_HEART];
		items[IMG_[22]=items[IMG_UFOICO];
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

	  items[IMG_NFONT]=items[IMG_ER1];

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
//    super();
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

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
    public void setDirection(){

     if (gameStatus != demoPlay)
         client_sb.i_Button = direct;

     TankMoveDelay++;
     StarGun_GSR loc=(StarGun_GSR)client_sb.getGameStateRecord();

     if (TankMoveDelay >= 1 &&
          loc.getPlayerX() < StarGun_SB.PLAYER_RIGHT_BORDER &&
	  loc.getPlayerX() > 0) {
      TankMoveDelay = 0;
      switch (direct) {
	case StarGun_SB.DIRECT_LEFT:
	                         if (TankPos==0)TankPos=iTankAmount-1;
	                            else TankPos--;
				 break;
        case StarGun_SB.DIRECT_RIGHT:
	                         if (TankPos==iTankAmount-1)TankPos=0;
	                            else TankPos++;
				 break;
      }
     }
    }

    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       direct=StarGun_SB.DIRECT_NONE;
       client_sb.i_Button = direct;
       CurrentLevel=level;
       baseX=((scrwidth-levelImage.getWidth())>>1);
       baseY=((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGame(StarGun_SB.LEVEL_DEMO);
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
		                  // System.out.println("kk:"+keyCode/*+", ac:"+getGameAction(keyCode)+", acn:"+getKeyName(keyCode)*/);
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
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=StarGun_SB.DIRECT_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=StarGun_SB.DIRECT_LEFT; break;
				      case Canvas.KEY_NUM3: direct=StarGun_SB.DIRECT_ANGLE_LEFT; break;
				      case Canvas.KEY_NUM1: direct=StarGun_SB.DIRECT_ANGLE_RIGHT; break;
				      case Canvas.KEY_NUM9/*RIGHT*/: direct=StarGun_SB.DIRECT_RIGHT|StarGun_SB.DIRECT_ANGLE_RIGHT; break;
	                              case Canvas.KEY_NUM7/*LEFT*/: direct=StarGun_SB.DIRECT_LEFT|StarGun_SB.DIRECT_ANGLE_LEFT; break;
				      case Canvas.KEY_NUM5/*FIRE*/: direct=StarGun_SB.DIRECT_FIRE; break;

				default:direct=StarGun_SB.DIRECT_NONE;
	                       }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct = StarGun_SB.DIRECT_NONE;
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
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   Runtime.getRuntime().gc();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   blink=(ticks&8)==0;
			   StarGun_GSR loc=(StarGun_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==StarGun_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       System.gc();
			   }

			   repaint();
			  } break;
           case playGame :{
	                   setDirection();
			   client_sb.nextGameStep();
			   StarGun_GSR loc=(StarGun_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==StarGun_GSR.GAMESTATE_OVER) {
			       ticks = 0;
			       blink = true;
			     switch (loc.getPlayerState()) {
			        case StarGun_GSR.PLAYER_BURNED: gameStatus=crashCar; break;
			        case StarGun_GSR.PLAYER_OUT_OF_AMMO: gameStatus=outOfAmmo; break;
			        case StarGun_GSR.PLAYER_FINISHED: gameStatus=Finished; break;
			        case StarGun_GSR.PLAYER_KILLED: gameStatus=gameOver; break;
			     }
			   }
			   repaint();
			  } break;
	    case outOfAmmo:
	    case crashCar:
			   ticks++; blink=(ticks&2)==0;
			   if (ticks >= 10) {
			      client_sb.resumeGame();
		              direct=StarGun_SB.DIRECT_NONE;
			      gameStatus=playGame;
			      ticks = 0;
			      blink = false;
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

	// prepare screen
        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth,scrheight);
	gBuffer.drawImage(levelImage,baseX,baseY,Graphics.TOP|Graphics.LEFT);

        StarGun_GSR loc=(StarGun_GSR)client_sb.getGameStateRecord();

	Image img=null;

   /// Player
   if (gameStatus != crashCar || (gameStatus == crashCar && blink)){
        gBuffer.drawImage(items[IMG_TANK+TankPos],baseX+loc.getPlayerX(),baseY+loc.getPlayerY(),Graphics.BOTTOM|Graphics.LEFT);

        gBuffer.setColor(0x0);
        gBuffer.drawLine(baseX+loc._gun_x1,baseY+loc._gun_y1,baseX+loc._gun_x2,baseY+loc._gun_y2);
	for (int i=0;i<loc._trys_left; i++)
            gBuffer.drawImage(items[IMG_HEART],scrwidth - 10 -(items[IMG_HEART].getWidth()+1)*i,scrheight-7,Graphics.TOP|Graphics.RIGHT);

   }

//        gBuffer.drawRect(loc.getPlayerX(),loc.getPlayerY()-StarGun_SB.PLAYER_CELL_HEIGHT,StarGun_SB.PLAYER_CELL_WIDTH,StarGun_SB.PLAYER_CELL_HEIGHT);
   /// Enemyes

	FlyingObject [] o = loc.getEnemyesArray();
	int ofs = 0;
	for (int i=0;i<loc._enemyes_counter;i++){
	  if (o[i]!=null){
	    switch (o[i]._type) {
	      case FlyingObject.BOMB : img=items[IMG_BOMB3];
					  break;
	      case FlyingObject.UFO : img=items[IMG_UFO3];
					  break;
	      case FlyingObject.UFO_BURNING : img=items[IMG_UFO3];
					  break;
	      case FlyingObject.UFO_EXPLODE : img=items[IMG_UFO5_1+5-o[i].crash_stage];
					  break;
	      case FlyingObject.BOMB_EXPLODE : img=items[IMG_ERB1+2-o[i].crash_stage];
					  break;
	      case FlyingObject.AIR_EXPLODE : img=items[IMG_ER1+2-o[i].crash_stage];
					  break;

			  default: //System.out.println("Err: unk.obj:"+o[i]._type);
			             continue;
	    }
           gBuffer.drawImage(img,baseX+o[i]._x,baseY+o[i]._y,Graphics.BOTTOM|Graphics.LEFT);
  //        gBuffer.drawRect(o[i]._x,o[i]._y-o[i].CELL_HEIGHT,o[i].CELL_WIDTH,o[i].CELL_HEIGHT);
	  }
	}

   /// Defenders

	o = loc.getRocketsArray();
	for (int i=0;i<loc._rockets_counter;i++){
	  if (o[i]!=null){
	    switch (o[i]._type) {
	      case FlyingObject.ROCKET :
				         if (o[i]._Vx<=-RocketDeviation)img=items[IMG_BULL2];
					  else
				            if (o[i]._Vx>=RocketDeviation)img=items[IMG_BULL1];
					      else
					         img=items[IMG_BULL3];
					  break;
			  default: continue;
	    }
           gBuffer.drawImage(img,baseX+o[i]._x,baseY+o[i]._y,Graphics.BOTTOM|Graphics.LEFT);
//           gBuffer.drawRect(o[i]._x,o[i]._y-o[i].CELL_HEIGHT,o[i].CELL_WIDTH,o[i].CELL_HEIGHT);
	  }
	}

   int offs=2+items[IMG_BULL1].getWidth()+1;
   if (gameStatus != outOfAmmo || (gameStatus == outOfAmmo && blink)){
        gBuffer.drawImage(items[IMG_BULL1],2,1/*scrheight - 6*/,Graphics.TOP|Graphics.LEFT);
	drawDigits(gBuffer,offs,0/*scrheight - 7*/,3,loc._rockets_left);
   }
   offs+=fontWidth*3+4;
   gBuffer.drawImage(items[IMG_UFOICO],offs,1/*scrheight - 6*/,Graphics.TOP|Graphics.LEFT);
   drawDigits(gBuffer,offs+items[IMG_UFOICO].getWidth()+1,0/*scrheight - 7*/,3,(loc._ufo_total-loc._ufo_fired+1));



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
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
       direct=StarGun_SB.DIRECT_NONE;
       //CurrentLevel=level;
       baseX=((scrwidth-levelImage.getWidth())>>1);
       baseY=((scrheight-levelImage.getHeight())>>1);
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final int IMG_STARGUNFP = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WON = 2;
private static final int IMG_3 = 3;
private static final int IMG_DEMO = 4;
private static final int IMG_NFONT = 15;
private static final int IMG_UFO5_1 = 20;
private static final int IMG_UFO5_3 = 22;
private static final int IMG_TANK2 = 11;
private static final int IMG_UFO5_2 = 21;
private static final int IMG_TANK = 9;
private static final int IMG_TANK1 = 10;
private static final int IMG_TANK3 = 12;
private static final int IMG_UFO3 = 13;
private static final int IMG_UFO5_4 = 23;
private static final int IMG_UFO5_5 = 24;
private static final int IMG_ER1 = 14;
private static final int IMG_UFO5_6 = 25;
private static final int IMG_ER2 = 16;
private static final int IMG_ERB1 = 17;
private static final int IMG_ERB2 = 18;
private static final int IMG_HEART = 27;
private static final int IMG_ERB3 = 19;
private static final int IMG_BOMB3 = 5;
private static final int IMG_BULL1 = 6;
private static final int IMG_BULL3 = 8;
private static final int IMG_BULL2 = 7;
private static final int IMG_UFOICO = 26;
private static final int TOTAL_IMAGES_NUMBER = 28;

}

