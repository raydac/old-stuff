package com.igormaznitsa.midp.PhoneModels;
import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.ImageBlock;

public abstract class GameCanvas extends PhoneStubImpl implements Runnable,  LoadListener {

  public final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  public final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  public final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time
  public int AMOUNT_OF_LOADED_RESOURCES = 0;

  public com.igormaznitsa.gameapi.ControlMenuBlock midlet;
  public Display display;
  public int scrwidth,scrheight,scrcolors;
  public boolean colorness;
  public Thread thread=null;

  public Image Title=null;
  public Image Lost=null;
  public Image Won=null;
  public Image DemoTitle=null;
//  public Image Buffer=null;
//  public Graphics gBuffer=null;
  public Image levelImage=null;
  private Image MenuIcon = null;
  private Image LoadImg = null;

  public int CurrentLevel=0;
  public int direct=0; //_PMR.DIRECT_NONE;
  public int baseX=0, baseY=0;

  public boolean animation=true;
  public boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    demoPlay = 2,
			    newStage = 3,
                            playGame = 4,
			    Crashed = 5,
			    Finished = 6,
			    outOfResources = 7,
			    gameOver = 8;

  public int [] stageSleepTime = {100,100,100,100,250,250,250,250,250}; // in ms

  public int gameStatus=notInitialized;
  public int demoLevel = 1;
  public int demoPosition = 0;
  public int demoPreviewDelay = 1000; // ticks count
  public int demoLimit = 0;

  public boolean bufferChanged=true;
  public int LoadingTotal =0, LoadingNow = 0, ticks=0;
  public boolean LanguageCallBack = false, keyReverse = false;

  public String loadString  = "LOADING";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;

  public int _score;
  public int stage;

     public static final int IMG_INFON_NOKIA = 209; //Infon_Siemens.png
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


    private long _loading_time_range = 0;

    public void nextItemLoaded(int nnn){
       gameStatus=notInitialized;
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       if (System.currentTimeMillis()-_loading_time_range>25){
         _loading_time_range = System.currentTimeMillis();
         repaint(0,0,scrwidth,scrheight);//custom_paint();
       }
    }

    public void startIt() {
     loading(AMOUNT_OF_LOADED_RESOURCES);
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }

     try {

	   ImageBlock pnl = new ImageBlock("/res/images.bin",this);
            prepareImages(pnl);
           pnl = null;
     } catch(Exception e) {
       System.out.println("Can't read images");
       e.printStackTrace();
     }
     Runtime.getRuntime().gc();
    }

    public void loading(int howmuch){
       gameStatus=notInitialized;
       LoadingTotal = howmuch;
       LoadingNow = 0;
       repaint(0,0,scrwidth,scrheight);//custom_paint();
    }

    public void loading(){
       gameStatus=notInitialized;
       ticks = 0;
       repaint(0,0,scrwidth,scrheight);
    }

    public void endGame() {
	gameStatus=titleStatus;
        ticks =0;
	repaint(0,0,scrwidth,scrheight);
    }


  /**Construct the displayable*/
  public GameCanvas(com.igormaznitsa.gameapi.ControlMenuBlock m) {
    super(null);
    midlet = m;
    display = Display.getDisplay((javax.microedition.midlet.MIDlet)m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();

//    initDoubleBuffer((scrwidth+8)&0xf8,scrheight,null);

//    Buffer=Image.createImage(scrwidth,scrheight);
//    gBuffer=Buffer.getGraphics();

    LoadImg = getImage(IMG_INFON_NOKIA);
    MenuIcon = getImage(IMG_MENUICO);
    storage = null;
    gameStatus=notInitialized;
  }

///////////////////////////////////////////////////////////////////////////////////



    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


    public abstract void init(int level, String Picture);


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
	      case notInitialized: if (keyCode==KEY_BACK) midlet.killApp();
	                           break;
	      case Finished:
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
	      case demoPlay:
              case titleStatus : {
		                    switch (keyCode) {
				      case KEY_MENU:
				                midlet.ShowMainMenu();
				                break;
				      case KEY_BACK:  midlet.ShowQuitMenu(false);
				                break;
				      default:
				              if (gameStatus == demoPlay || ticks>=TOTAL_OBSERVING_TIME){
	                                        gameStatus=titleStatus;
			                        ticks=0;
			                        blink=false;
					        repaint(0,0,scrwidth,scrheight);
				              }
		                    }
				    //midlet.commandAction(midlet.newGameCommand,this);
                               } break;

              case Crashed:
	      case outOfResources:
              case playGame :  {
			         //int action = getGameAction(keyCode);
			         if (keyCode == KEY_MENU) midlet.ShowGameMenu(); else
			         if (keyCode == KEY_BACK) midlet.ShowQuitMenu(true); else
			         direct = ParsePressedKey(keyCode);

	                       }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct = 0;
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
        if (isShowEnabled())
 	 switch (gameStatus) {
           case titleStatus:
	                if(ticks==0) showTitle();
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   System.gc();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   blink=(ticks&8)==0;
			   if (demoPlayNextStep()==GameStateBlock.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       System.gc();
			   }

			   repaint(0,0,scrwidth,scrheight);//custom_paint();
			  } break;
	   case newStage: {
	                      if(ticks==0) {
				prepareNextStage();
	                      }
			      else
	                      if (ticks>20){
				ticks = 0;
			        gameStatus=playGame;
	                      }
			    ticks++;
			    repaint(0,0,scrwidth,scrheight);//custom_paint();
			    } break;
           case playGame :{
			   blink=(++ticks&4)==0;
			   playGameNextStep();
			   repaint(0,0,scrwidth,scrheight);//custom_paint();
			  } break;

	    case outOfResources: OutOfResources();repaint(0,0,scrwidth,scrheight); break;
	    case Crashed: Crashed();repaint(0,0,scrwidth,scrheight);break;

            case Finished:
	    case gameOver:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME){
			      midlet.newScore(_score);
			    }
			   repaint(0,0,scrwidth,scrheight);
			   break;

 	 }
        sleepy = System.currentTimeMillis();
	workDelay += stageSleepTime[gameStatus]-System.currentTimeMillis();
        try {
//          thread.sleep(stageSleepTime[gameStatus]);

//          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
//	    while(System.currentTimeMillis()-sleepy<workDelay-10)thread.yield();
//          else
//	System.out.println(Thread.activeCount()+","+stageSleepTime[gameStatus]+","+workDelay);

          if(workDelay<=0)workDelay=10;
             thread.sleep(workDelay);

	} catch (Exception e) {}

	workDelay = System.currentTimeMillis();
      }

    }


    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    public void paint(Graphics g) {
            switch (gameStatus) {
	      case notInitialized:  drawLoading(g, LoadingTotal, LoadingNow, loadString); break;
              case titleStatus : g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  g.drawImage(Title,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
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
				g.drawString(YourScore+_score ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :
	                   //if (this.isDoubleBuffered())
			   //   {
		               DoubleBuffer(g);
			       g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			   //   }
			   //   else {
//		                DoubleBuffer(gBuffer);
//				gBuffer.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
//		                g.drawImage(Buffer,0,0,0);
			   //   }

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public abstract void showTitle();
    public void prepareNextStage() {}
    public abstract void gameLoaded();
    public abstract void prepareImages(ImageBlock pnl);
    public abstract int ParsePressedKey(int KeyCode);
    public abstract int demoPlayNextStep();
    public abstract void playGameNextStep();
    public abstract void OutOfResources();
    public abstract void Crashed();
    public abstract void DoubleBuffer(Graphics g);

    private final static int i_BAR_HEIGHT = 6;
    public void drawLoading(Graphics g, int total, int current, String text){
			  int l = (scrwidth-LoadImg.getWidth())>>1;
			  int h = (scrheight-LoadImg.getHeight()-i_BAR_HEIGHT)>>1;
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
	                  g.drawImage(LoadImg,l++,h,0);
	                  g.drawRect(l,h+LoadImg.getHeight(), LoadImg.getWidth()-3,i_BAR_HEIGHT);
	                  g.fillRect(l,h+LoadImg.getHeight(), (LoadImg.getWidth()-3)*current/Math.max(1,total),i_BAR_HEIGHT);

/*
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  g.drawString(text/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"* / ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
	                  g.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  g.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*current/Math.max(1,total),6);
			  g.setFont(f);
*/

    }

}

