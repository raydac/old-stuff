
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.LongFlight.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;
import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener {

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

/*=============================*/

  private final static int MENU_KEY = KEY_MENU;
  private final static int END_KEY = KEY_BACK;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 25; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 40; //x200ms = 7 seconds , game over / finished sleep time

private final static int MapCorrection = -1;


  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;

  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image BombIcon = null;
  private Image Heart = null;
  private int bsWIDTH, bsHEIGHT, bsSTEP, bsFRAMES, bsLAST_FRAME=0, scrX=0;
/*
  private static final int iGROUND = 0,
                           iTREE  = 1,
			   iLAUNCER = 2,
			   iLEFTSLANT = 3,
			   iRIGHTSLANT = 4,
			   iHOUSE = 5,
			   iFACTORY = 6,
			   iANTIAIRGUN = 7,
			   iCRATER = 8,
			   iPLAYER = 9,
			   iBOMB = 10,
			   iBOMBEXPLODED = 11,
			   iSHELL = iBOMBEXPLODED+ExplObj.EXPLODE_FRAMES,
			   iROCKET = iSHELL+1;
*/
  private Image []numbers=new Image[10];
  public int fontHeight=0,fontWidth=0, TitleN = 0;

  private int blinkX=0,blinkY=0;//in ticks defined by singleSleepTime

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected int direct=LongFlight_SB.BUTTON_NONE;
  protected LongFlight_SB client_sb = new LongFlight_SB();
  protected int baseX=0, baseY=0, cellW=0,cellH=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;
  private int stage = 0;
  private int lastPlayerScores =0;


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
  private int [] stageSleepTime = {100,100,100,200,70,200,200,200}; // in ms

  private int playerScore=0;
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

  private Image [] Elements;
  private byte [][] ByteArray;

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

    client_sb = new LongFlight_SB(/*VisWidth, VisHeight, null*/);
//    client_pmr = new longPlayerMoveObject();

    if (!this.isDoubleBuffered())
    {
//      System.out.println("not dbufferred");
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    bsFRAMES = LongFlight_SB.OBJECTS_ON_SCREEN+1;
    bsWIDTH =  bsFRAMES*LongFlight_SB.SPRITE_WIDTH;
    bsHEIGHT = LongFlight_SB.SCREEN_HEIGHT;

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

           Heart=Elements[IMG_HEART]; //Image.createImage(DemoTitleImage];
	   BombIcon = Elements[IMG_BOMBICO];

           Image tmp=Elements[IMG_NFONT];//Image.createImage(FontImage];
           fontHeight = tmp.getHeight();
           fontWidth = tmp.getWidth()/10;
           for (int i = 1;i<10; i++){
	      numbers[i] = Image.createImage(fontWidth,fontHeight);
	      numbers[i].getGraphics().drawImage(tmp,-(i-1)*fontWidth,0,Graphics.TOP|Graphics.LEFT);
           }
	   numbers[0] = Image.createImage(fontWidth,fontHeight);
	   numbers[0].getGraphics().drawImage(tmp,-9*fontWidth,0,Graphics.TOP|Graphics.LEFT);
	   tmp = null;
	   Elements[IMG_NFONT] = null;

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
     stage = Way.indexes[1];
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
       blink=false;
       ticks = 0;
       direct=LongFlight_SB.BUTTON_NONE;
       bsSTEP = 0;
       bsLAST_FRAME = 0;
       scrX=0;
       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(LongFlight_SB.LEVEL0);
	    client_sb.initStage(0);
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
//	    DrawBackScreen();
	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
           client_sb.nextGameStep();
           /* stageSleepTime[playGame] = ((LongFlight_GSR)client_sb.getGameStateRecord()).getLevelDelay();*/
	   lastPlayerScores = 0;
	   stage = 0;
	   gameStatus=newStage;
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
			       //int action = getGameAction(keyCode);
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
                                 else
	                          switch (keyCode) {
				      case KEY_NUM1:
				      case KEY_NUM2:
				      case KEY_NUM3:
				                     direct=LongFlight_SB.BUTTON_UP; break;
				      case KEY_NUM7:
				      case KEY_NUM8:
				      case KEY_NUM9:
				                     direct=LongFlight_SB.BUTTON_DOWN; break;
				   default:
					  switch (getGameAction(keyCode)) {
						 case UP: direct=LongFlight_SB.BUTTON_UP; break;
						 case DOWN: direct=LongFlight_SB.BUTTON_DOWN; break;
						      default:direct=LongFlight_SB.BUTTON_BOMB;
					  }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=LongFlight_SB.BUTTON_NONE;
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
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			   bsLAST_FRAME=0;
			   repaintBackScreen();
			   System.gc();
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   LongFlight_GSR loc=(LongFlight_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==LongFlight_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=LongFlight_SB.BUTTON_NONE;
				client_sb.initStage(stage);
				client_sb.nextGameStep();
				bsLAST_FRAME = 0;
				repaintBackScreen();
	                      }
			      else
	                      if (ticks++>10){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
	                   client_sb.i_Button = direct;
			   client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   LongFlight_GSR loc=(LongFlight_GSR)client_sb.getGameStateRecord();
/*
			   if (loc.getGameState()==LongFlight_GSR.GAMESTATE_OVER) {
	                       gameStatus=gameOver;
                               playerScore=loc.getPlayerScores();
			       switch (loc.getPlayerState()) {
				      case LongFlight_GSR.PLAYERSTATE_KILLED: ticks = 0;gameStatus=Crashed; break;
				      case LongFlight_GSR.PLAYERSTATE_OUTOFAMMO: ticks = 0;gameStatus=Crashed; break;
			              case LongFlight_GSR.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case LongFlight_GSR.PLAYERSTATE_KILLED: ticks =0; gameStatus=Crashed; break;
					case LongFlight_GSR.PLAYERSTATE_OUTOFAMMO: ticks =0; gameStatus=Crashed; break;
			                case LongFlight_GSR.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Way.WAY_NUMBER)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;  break;
			         }
			   repaint();
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=LongFlight_SB.BUTTON_NONE;
			     if (((LongFlight_GSR)client_sb.getGameStateRecord()).getGameState()==LongFlight_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
			           client_sb.resumeGame();
			           gameStatus=playGame;
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

    private void DrawVerticalCell(int ele,int bs_position){
         int hght = ele & 0x0F;
         int obj = (ele & 0xF0)>>4;
	 int j=bsHEIGHT-LongFlight_SB.SPRITE_HEIGHT;
	 bs_position = (bs_position%bsFRAMES)*LongFlight_SB.SPRITE_WIDTH;
         gBackScreen.setColor(0xffffff);
         gBackScreen.fillRect(bs_position,0,LongFlight_SB.SPRITE_WIDTH,bsHEIGHT);
/*
	  for(int i = 0;i<hght;i++){
	    gBackScreen.drawImage(Elements[iGROUND],bs_position,j,0);
	    j-=LongFlight_SB.SPRITE_HEIGHT;
	  }
*/
	    j-=LongFlight_SB.SPRITE_HEIGHT*(hght-1);
	    gBackScreen.drawImage(Elements[IMG_GROUND],bs_position,j,0);
	    j-=LongFlight_SB.SPRITE_HEIGHT;


/*
	  Image img = null;
            switch (obj)
            {
                case Way.OBJ_ANTIAIRGUN : img = Elements[iANTIAIRGUN];break;
                case Way.OBJ_CRATER     : img = Elements[iCRATER];break;
                case Way.OBJ_FACTORY    : img = Elements[iFACTORY];break;
                case Way.OBJ_HOUSE      : img = Elements[iHOUSE];break;
                case Way.OBJ_LAUNCER    : img = Elements[iLAUNCER];break;
                case Way.OBJ_LEFTSLANT  : img = Elements[iLEFTSLANT];break;
                case Way.OBJ_RIGHTSLANT : img = Elements[iRIGHTSLANT];break;
                case Way.OBJ_TREE       : img = Elements[iTREE];break;
            }
*/

	  if (obj>0)
	    gBackScreen.drawImage(Elements[obj+IMG_GROUND],bs_position,j,0);
    }

    private void repaintBackScreen(){
        LongFlight_GSR loc=(LongFlight_GSR)client_sb.getGameStateRecord();
	for(int i=0;i<bsFRAMES;i++)
		 DrawVerticalCell(loc.getElementAtPos(bsLAST_FRAME+i-LongFlight_SB.PLAYER_X_POSITION + MapCorrection),i);
//	bsLAST_FRAME=0;//-LongFlight_SB.PLAYER_X_POSITION;
	scrX = 0;
	System.gc();
    }


    protected void DoubleBuffer(Graphics gBuffer){
      if(baseX >0 || baseY >0){
        gBuffer.setColor(0x0);
        if(baseX>0){ gBuffer.fillRect(0,0,baseX,scrheight);
                     gBuffer.fillRect(scrwidth-baseX,0,baseX,scrheight); }
        if(baseY>0){ gBuffer.fillRect(baseX,0,scrwidth-baseX,baseY);
	             gBuffer.fillRect(baseX,scrheight-baseY,scrwidth-baseX,baseY); }

        gBuffer.translate(baseX-gBuffer.getTranslateX(),baseY-gBuffer.getTranslateY());
        gBuffer.setClip(0,0,VisWidth,VisHeight);
      }
////////////////////////////////////////////////


        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, VisWidth,VisHeight);

if(VisHeight>64) gBuffer.translate(0,VisHeight-64);

//      if (bufferChanged) {

        LongFlight_GSR loc=(LongFlight_GSR)client_sb.getGameStateRecord();
        Image img = null;

        int foff = loc.getPlayerAbsX() % LongFlight_SB.SPRITE_WIDTH;
        int lli = loc.getPlayerAbsX() / LongFlight_SB.SPRITE_WIDTH;// - LongFlight_SB.PLAYER_X_POSITION;

	if(lli!=bsLAST_FRAME) {
	    bsLAST_FRAME = lli;
	    scrX+=LongFlight_SB.SPRITE_WIDTH;
	    if(scrX>bsWIDTH)scrX-=bsWIDTH;
	    //(scrX/LongFlight_SB.SPRITE_WIDTH)+bsFRAMES-1;
	    //scrX=(lli%bsFRAMES)*LongFlight_SB.SPRITE_WIDTH;
            DrawVerticalCell(loc.getElementAtPos(lli-LongFlight_SB.PLAYER_X_POSITION+bsFRAMES-1+MapCorrection),(scrX/LongFlight_SB.SPRITE_WIDTH)+bsFRAMES-1/*lli+bsFRAMES-1*/);
	}


	gBuffer.drawImage(BackScreen,-scrX-foff,0,0);
	gBuffer.drawImage(BackScreen,-scrX+bsWIDTH-foff,0,0);

        // Drawing bomb
        ExplObj [] _expl = loc.getBombArray();
        for(int li=0;li<_expl.length;li++) {
            ExplObj _b = _expl[li];
            if (_b._active) {
	       int landType = loc.getElementAtPos(client_sb.getXAtScrCoord(_b._x))& 0xF0;
	       //client_sb.getElementAtScrXY(_b._x,_b.getY())
                if (_b._explode ){
		  if (_b._tick<2 && landType!=Way.OBJ_LEFTSLANT && landType!=Way.OBJ_RIGHTSLANT){
		    gBackScreen.setColor(0xffffff);
		    int ofs = _b._x+scrX+foff;
		    if (ofs>=bsWIDTH) ofs-=bsWIDTH;
		    gBackScreen.fillRect(ofs,_b.getY(),LongFlight_SB.SPRITE_WIDTH,LongFlight_SB.SPRITE_HEIGHT);
                    gBackScreen.drawImage(Elements[IMG_CRATER],ofs,_b.getY(),0);
//		    System.out.println("x:"+_b._x+", y:"+_b.getY()+", scr:"+scrX+", tot:"+(_b._x+scrX+foff));
		  }
                    gBuffer.drawImage(Elements[IMG_EXPL0+_b._frame],_b._x,_b.getY(),0);
//		    DrawVerticalCell(int ele,int bs_position){
                }
                else
                    gBuffer.drawImage(Elements[IMG_BOMB],_b._x,_b.getY(),0);
            }
        }

        // Drawing shells
        _expl = loc.getShellArray();
        for(int li=0;li<_expl.length;li++) {
            ExplObj _b = _expl[li];
            if (_b._active){
                 gBuffer.drawImage(Elements[IMG_SHELL],_b._x,_b.getY(),0);
            }
        }

        // Drawing rockets
        _expl = loc.getRocketArray();
        for(int li=0;li<_expl.length;li++) {
            ExplObj _b = _expl[li];
            if (_b._active)
                 gBuffer.drawImage(Elements[IMG_ROCKET],_b._x,_b.getY(),0);
        }

        if(!(gameStatus == Crashed && blink))
          /* Player */  gBuffer.drawImage(Elements[IMG_BOMBER],LongFlight_SB.PLAYER_X_OFFSET, loc.getPlayerY(), 0);

if(VisHeight>64) gBuffer.translate(0,64-VisHeight);

        int offs=BombIcon.getWidth()+1;
        if (!(((loc.getBombsNumber()<5 && gameStatus == playGame) || (gameStatus == Crashed && loc.getBombsNumber()<=0))&&blink)){
            gBuffer.drawImage(BombIcon,0,0,Graphics.TOP|Graphics.LEFT);
	    drawDigits(gBuffer,offs,0/*VisHeight - 7*/,2,loc.getBombsNumber());
        }

        if(!(gameStatus == Crashed && blink)) {
//          /* Player */  gBuffer.drawImage(Elements[IMG_BOMBER],LongFlight_SB.PLAYER_X_OFFSET, loc.getPlayerY(), 0);
          /* lives */
	    int y = VisHeight-Heart.getHeight()-2;
	    if(VisHeight>64) y = 1;
	    for (int i=0;i<loc.getAttemptions();i++)
                gBuffer.drawImage(Heart,VisWidth-(i+1)*(Heart.getWidth()+2)-10,y,0);
        }

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, VisWidth>>1 ,VisHeight-VisHeight/4/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}
////////////////////////////////////////////////
      if(baseX >0 || baseY >0){
        gBuffer.translate(-gBuffer.getTranslateX(),-gBuffer.getTranslateY());
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
	                  g.drawImage(GetImage(IMG_DZ),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
				g.drawString(YourScore+((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
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
      return (gameStatus == playGame || gameStatus == Crashed || gameStatus == newStage);
    }
    public void gameLoaded(){
       playerScore=0;
       blink=false;
       ticks = 0;
       direct=LongFlight_SB.BUTTON_NONE;
       bsSTEP = 0;
       bsLAST_FRAME = 0;
       scrX=0;
//       CurrentLevel=level;
       client_sb.nextGameStep();
       lastPlayerScores = 0;
       LongFlight_GSR loc=(LongFlight_GSR)client_sb.getGameStateRecord();
       stage = loc.getStage();
       bsLAST_FRAME=loc.getPlayerAbsX() / LongFlight_SB.SPRITE_WIDTH - LongFlight_SB.PLAYER_X_POSITION+1;
       repaintBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }
private static final int IMG_DZ = 0;
private static final int IMG_LOST = 1;
private static final int IMG_WIN = 2;
private static final int IMG_GROUND = 3;
private static final int IMG_TREE1 = 4;
private static final int IMG_ROCKET1 = 5;
private static final int IMG_LEFTMSIDE = 6;
private static final int IMG_RIGHTMSIDE = 7;
private static final int IMG_HOUSE = 8;
private static final int IMG_FACTORY = 9;
private static final int IMG_ZENITH1 = 10;
private static final int IMG_CRATER = 11;
private static final int IMG_EXPL0 = 12;
private static final int IMG_EXPL1 = 13;
private static final int IMG_EXPL2 = 14;
private static final int IMG_EXPL3 = 15;
private static final int IMG_BOMBER = 16;
private static final int IMG_HEART = 17;
private static final int IMG_DEMO = 18;
private static final int IMG_BOMBICO = 19;
private static final int IMG_ROCKET = 20;
private static final int IMG_BOMB = 21;
private static final int IMG_SHELL = 22;
private static final int IMG_NFONT = 23;
private static final int TOTAL_IMAGES_NUMBER = 24;
}