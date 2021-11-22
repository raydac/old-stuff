
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Treasure.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;


  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time


  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false, KeyVisible=false, DoorVisible=false;;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image DemoTitle=null;
  private Image iKeyIco=null;
  private Image iDoorIco=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
  private Image Heart = null;
  private final static int BORDER = 25, // in cells
                           CELL_WIDTH = Treasure_SB.VIRTUAL_CELL_WIDTH, CELL_HEIGHT = Treasure_SB.VIRTUAL_CELL_HEIGHT,
			   bsWIDTH = CELL_WIDTH*Labyrinths.FIELD_WIDTH,
			   bsHEIGHT = CELL_HEIGHT*Labyrinths.FIELD_HEIGHT,
			   MOVE_SPEED = Treasure_SB.PLAYER_SPEED>>8;
/*
			   // vis stage elements
                           iKEY =0,
                           iDOOR =1,
			   iWALL =2,
			   iTREASURE =3,
			   iWATER =4,
			   iSTAIRS =5,
			   //characters
			   iMAN_APPERANCE =6,
			   iMAN_MOVE_LEFT = iMAN_APPERANCE + Man.APPEARANCE_FRAMES,
			   iMAN_MOVE_RIGHT = iMAN_MOVE_LEFT + Man.MOVE_FRAMES,
			   iMAN_MOVE_UPDOWN = iMAN_MOVE_RIGHT + Man.MOVE_FRAMES,

			   iENEMY_MOVE_LEFT = iMAN_MOVE_UPDOWN + Man.MOVE_FRAMES,
			   iENEMY_MOVE_RIGHT = iENEMY_MOVE_LEFT + Man.MOVE_FRAMES,
			   iENEMY_MOVE_UPDOWN = iENEMY_MOVE_RIGHT + Man.MOVE_FRAMES;
*/
  private Image items[];// = new Image[iENEMY_MOVE_UPDOWN + Man.MOVE_FRAMES];
  private int scrX =0, scrY =0;
  private boolean widthIsAligned = false;
  private boolean heightIsAligned = false;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private int CurrentLevel=3;
  protected int direct=Treasure_SB.DIRECT_NONE;
  protected Treasure_SB client_sb = new Treasure_SB();
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
  private int [] stageSleepTime = {100,100,100,100,80,200,200,200}; // in ms

  private int playerScore=0;
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
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    BackScreen=Image.createImage(bsWIDTH, bsHEIGHT);
    gBackScreen=BackScreen.getGraphics();
    gameStatus=notInitialized;
  }

///////////////////////////////////////////////////////////////////////////////////
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
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
	   items = (new ImageBlock(this))._image_array;

	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title=items[IMG_TH_FP];//Image.createImage(TitleImage];
           Lost=items[IMG_TH_LOST];//Image.createImage(TitleImage];
           Won=items[IMG_TH_WON];//Image.createImage(TitleImage];
           DemoTitle=items[IMG_DEMO]; //Image.createImage(DemoTitleImage];
           Heart=items[IMG_HEART]; //Image.createImage(DemoTitleImage];

           iKeyIco=items[IMG_KEYICO]; //Image.createImage(DemoTitleImage];
           iDoorIco=items[IMG_DOORICO]; //Image.createImage(DemoTitleImage];
/*
           items[iKEY]=items[IMG_KEY];
           items[iWALL]=items[IMG_BRICK];
           items[iWATER]=items[IMG_WATER];
           items[iTREASURE]=items[IMG_TREAS];
           items[iDOOR]=items[IMG_DOOR];
           items[iSTAIRS]=items[IMG_STAIR];

           items[iMAN_APPERANCE]=items[IMG_APP0];
           items[iMAN_APPERANCE+1]=items[IMG_APP1];
           items[iMAN_APPERANCE+2]=items[IMG_APP2];
           items[iMAN_APPERANCE+3]=items[IMG_APP3];
//           items[iMAN_APPERANCE+2]=items[iMAN_APPERANCE];
//           items[iMAN_APPERANCE+3]=items[iMAN_APPERANCE+1];
           items[iMAN_APPERANCE+4]=items[IMG_APP4];
           items[iMAN_APPERANCE+5]=items[IMG_APP5];

           items[iMAN_MOVE_LEFT]=items[IMG_PLL0];
           items[iMAN_MOVE_LEFT+1]=items[IMG_PLL1];
           items[iMAN_MOVE_LEFT+2]=items[IMG_PLL2];
           items[iMAN_MOVE_LEFT+3]=items[IMG_PLL3];

           items[iMAN_MOVE_RIGHT]=items[IMG_PLR0];
           items[iMAN_MOVE_RIGHT+1]=items[IMG_PLR1];
           items[iMAN_MOVE_RIGHT+2]=items[IMG_PLR2];
           items[iMAN_MOVE_RIGHT+3]=items[IMG_PLR3];

           items[iMAN_MOVE_UPDOWN]=items[IMG_PLU0];
           items[iMAN_MOVE_UPDOWN+1]=items[IMG_PLU1];
           items[iMAN_MOVE_UPDOWN+2]=items[IMG_PLU2];
           items[iMAN_MOVE_UPDOWN+3]=items[IMG_PLU3];

           items[iENEMY_MOVE_LEFT]=items[IMG_MML0];
           items[iENEMY_MOVE_LEFT+1]=items[IMG_MML1];
           items[iENEMY_MOVE_LEFT+2]=items[IMG_MML2];
           items[iENEMY_MOVE_LEFT+3]=items[IMG_MML3];

           items[iENEMY_MOVE_RIGHT]=items[IMG_MMR0];
           items[iENEMY_MOVE_RIGHT+1]=items[IMG_MMR1];
           items[iENEMY_MOVE_RIGHT+2]=items[IMG_MMR2];
           items[iENEMY_MOVE_RIGHT+3]=items[IMG_MMR3];

           items[iENEMY_MOVE_UPDOWN]=items[IMG_MMU0];
           items[iENEMY_MOVE_UPDOWN+1]=items[IMG_MMU1];
           items[iENEMY_MOVE_UPDOWN+2]=items[IMG_MMU2];
           items[iENEMY_MOVE_UPDOWN+3]=items[IMG_MMU3];
*/

     } catch(Exception e) {
       //System.out.println("Can't read images");
       //e.printStackTrace();
     }

// System.out.println("png1:"+Runtime.getRuntime().freeMemory());
     Runtime.getRuntime().gc();
// System.out.println("png2:"+Runtime.getRuntime().freeMemory());
     stage = Labyrinths.indexes[1];
// System.out.println("lab:"+Runtime.getRuntime().freeMemory());
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
       direct=Treasure_SB.DIRECT_NONE;
       Runtime.getRuntime().gc();

       CurrentLevel=level;
	if (gameStatus==titleStatus) {
	    client_sb.newGame(Treasure_SB.LEVEL0);
	    client_sb.initStage(0);
            client_sb.nextGameStep();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    DrawBackScreen();
	    lastPlayerScores = 0;
	} else {
	   client_sb.newGame(CurrentLevel);
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
       isShown = false;
       direct = Treasure_SB.DIRECT_NONE;
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
	                          switch (keyCode/*action*/) {
				      case Canvas.KEY_NUM6/*RIGHT*/: direct=Treasure_SB.DIRECT_RIGHT; break;
	                              case Canvas.KEY_NUM4/*LEFT*/: direct=Treasure_SB.DIRECT_LEFT; break;
				      case Canvas.KEY_NUM2/*UP*/: direct=Treasure_SB.DIRECT_UP; break;
				      case Canvas.KEY_NUM8/*DOWN*/: direct=Treasure_SB.DIRECT_DOWN; break;
				   default:direct=Treasure_SB.DIRECT_NONE;
	                          }
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Treasure_SB.DIRECT_NONE;
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
	                if (ticks<2) {
			   // LostWon = null;
			   Runtime.getRuntime().gc();
	                }
			if (ticks<80) ticks++;
			 else
			  {
			   ticks=0;
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
		           client_sb.nextGameStep();
			   ticks++; blink=(ticks&8)==0;
			   Treasure_GSR loc=(Treasure_GSR)client_sb.getGameStateRecord();
			   if (loc.getGameState()==Treasure_GSR.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks==0) {
				client_sb.initStage(stage);
                                client_sb.nextGameStep();
                                stageSleepTime[playGame] = ((Treasure_GSR)client_sb.getGameStateRecord()).getGameTimedelay();
		                direct=Treasure_SB.DIRECT_NONE;
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks==1){
				DrawBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks>5){
				ticks = 0;
			        gameStatus=playGame;
	                      }
			    ticks++;
			    repaint();
			    } break;
           case playGame :{
	                   client_sb.i_Button = direct;
			   client_sb.nextGameStep();
			   Treasure_GSR loc=(Treasure_GSR)client_sb.getGameStateRecord();
                           ticks++;
			   //if((ticks&0xff)==50) Runtime.getRuntime().gc();
/*
			   if (loc.getGameState()==Treasure_GSR.GAMESTATE_OVER) {

	                       gameStatus=gameOver;
                               playerScore=loc.getPlayerScores();
			       switch (loc.getPlayerState()) {
				      case Treasure_GSR.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Treasure_GSR.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case Treasure_GSR.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Treasure_GSR.PLAYERSTATE_WON: ticks = 0;
								       stage++;
					                               if (stage>=Labyrinths.LAB_NUMBER)
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
		             direct=Treasure_SB.DIRECT_NONE;
			     if (((Treasure_GSR)client_sb.getGameStateRecord()).getGameState()==Treasure_GSR.GAMESTATE_OVER)
			            gameStatus=gameOver;
			      else {
				   Runtime.getRuntime().gc();
			           client_sb.resumeGame();
				   AlignPlayer();
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

    private Image getElementImage(int bb){
       switch(bb)
        {
            case Labyrinths.ELE_KEY   : KeyVisible = true; return items[IMG_KEY];
            case Labyrinths.ELE_DOOR  : KeyVisible = true; DoorVisible = true; return items[IMG_DOOR];
            case Labyrinths.ELE_WALL  : return items[IMG_BRICK];
//            case Labyrinths.ELE_EMPTY :
            case Labyrinths.ELE_TREASURE : return items[IMG_TREAS];
            case Labyrinths.ELE_WATER : return items[IMG_WATER];
            case Labyrinths.ELE_STAIRS : return items[IMG_STAIR];
        }
	return null;
    }


   private Image getCreature(Man man, int base){
       switch(man._cur_state & 0xf0)
        {
            case Man.MOVE_UPDOWN_STATE : base+=Man.MOVE_FRAMES;
            case Man.MOVE_RIGHT_STATE  : base+=Man.MOVE_FRAMES;
            case Man.MOVE_LEFT_STATE   : return items[base+(man._cur_state & 0xf)];
            case Man.APPEARANCE_STATE  : return items[IMG_APP0 + (man._cur_state & 0xf)];
        }
//	System.out.println("!!!");
	return null;
    }

/*

    private void updateBackScreen(Image img, int lx, int ly){
       lx = lx*CELL_WIDTH;
       ly = ly*CELL_HEIGHT;
	if (img!=null)
               gBackScreen.drawImage(img, lx,ly,0);
	    else
	       gBackScreen.fillRect(lx,ly,CELL_WIDTH,CELL_HEIGHT);
    }
*/

    private void AlignPlayer(){

        Treasure_GSR loc=(Treasure_GSR)client_sb.getGameStateRecord();

        int hx = loc._player.getX();
	int hy = loc._player.getY();

	if(bsWIDTH < scrwidth){
	   widthIsAligned = true;
	   scrX = (scrwidth - bsWIDTH)>>1;
	  } else {
	     widthIsAligned = false;
	     if (hx<BORDER) scrX=0;
	     else
	       if((bsWIDTH - hx)<=BORDER) scrX=scrwidth-bsWIDTH;
		  else
		    scrX=(scrwidth>>1)-hx;
	    }

	if(bsHEIGHT < scrheight){
	   heightIsAligned = true;
	   scrY = (scrheight - bsHEIGHT)>>1;
	  } else {
	     heightIsAligned = false;
	     if (hy<BORDER) scrY=0;
	     else
	       if((bsHEIGHT - hy)<=BORDER) scrY=scrheight-bsHEIGHT;
		  else
		    scrY=(scrheight>>1)-hy;
	    }
    //     System.out.println("BSH:"+bsHEIGHT+", hy:"+hy+", SCRH:"+scrheight+", Y:"+scrY);
    }

    private void DrawBackScreen(){

        gBackScreen.setColor(0xffffff);
        gBackScreen.fillRect(0, 0, bsWIDTH, bsHEIGHT);
        KeyVisible=false; DoorVisible=false;

        Treasure_GSR loc=(Treasure_GSR)client_sb.getGameStateRecord();

//	scrX = (scrwidth - bsWIDTH+((bsWIDTH>>1)-loc.getHeadX()*CELL_WIDTH))>>1;
//	scrY = (scrheight - bsHEIGHT+((bsHEIGHT>>1)-loc.getHeadY()*CELL_HEIGHT))>>1;
//        scrX=0; scrY=0;

        AlignPlayer();

        Image img = null;
	int ldx = 0,ldy = 0;
        for(int lx=0;lx<Labyrinths.FIELD_WIDTH;lx++){
	    ldy = 0;
            for(int ly=0;ly<Labyrinths.FIELD_HEIGHT;ly++)
            {
		if ((img=getElementImage(loc.getElementAt(lx,ly)))!=null)
                        gBackScreen.drawImage(img, ldx,ldy,0);
                ldy += CELL_HEIGHT;
            }
	    ldx += CELL_WIDTH;

        }
	Runtime.getRuntime().gc();
    }


    protected void DoubleBuffer(Graphics gBuffer){

//long time = System.currentTimeMillis();

        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth, scrheight);

//      if (bufferChanged) {


        Treasure_GSR loc=(Treasure_GSR)client_sb.getGameStateRecord();
        Image img = null;

        int hx = loc._player.getX();
	int hy = loc._player.getY();
	if ((hx+scrX+CELL_WIDTH)>(scrwidth-BORDER) ){
	  if ((bsWIDTH+scrX-MOVE_SPEED)>scrwidth) scrX-=MOVE_SPEED;//CELL_WIDTH;
	    else scrX = scrwidth - bsWIDTH;
	} else
	  if ((hx+scrX)<BORDER){
	     if (scrX+MOVE_SPEED<0) scrX+=MOVE_SPEED;//CELL_WIDTH;
	         else scrX = 0;

	  }
	  if ((hy+scrY+CELL_HEIGHT)>(scrheight-BORDER)){
	    if ((bsHEIGHT+scrY-MOVE_SPEED)>scrheight) scrY-=MOVE_SPEED;//CELL_HEIGHT;
	       else scrY = scrheight - bsHEIGHT;
	  } else
	      if ((hy+scrY)<BORDER){
	        if ((scrY+MOVE_SPEED)/*CELL_HEIGHT*/<0) scrY+=MOVE_SPEED;//CELL_HEIGHT;
	           else scrY = 0;
	      }

	gBuffer.drawImage(BackScreen,scrX,scrY,0);

        Man [] _k = loc.getOpponentArray();
        int _kl = loc.getOpponentArraySize();
        for(int li=0;li<_kl;li++)
	    gBuffer.drawImage(getCreature(_k[li],IMG_MML0/*ENEMY_MOVE_LEFT*/),_k[li].getX()+scrX,_k[li].getY()+scrY,0);

        // Drawing of the player
        if(!(gameStatus == Crashed && blink))
	gBuffer.drawImage(getCreature(loc._player,IMG_PLL0/*iMAN_MOVE_LEFT*/),hx+scrX,hy+scrY,0);

	if (client_sb.isChanged()){
	  if (client_sb.isKeyChanged()) //{
	    if (!client_sb.isDoorChanged()) {
	         gBackScreen.drawImage(items[IMG_KEY],loc.getKeyX()*CELL_WIDTH,loc.getKeyY()*CELL_HEIGHT,0);
		 KeyVisible = true;
	      }
	        else {
		  DoorVisible = true;
	          gBackScreen.drawImage(items[IMG_DOOR],loc.getDoorX()*CELL_WIDTH,loc.getDoorY()*CELL_HEIGHT,0);
	        }
	  //} else
	     gBackScreen.fillRect(client_sb.getChangedX()*CELL_WIDTH,client_sb.getChangedY()*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT);

	}




	//System.out.println("["+hx+","+hy+"]X:"+scrX+", Y:"+scrY);
        if(gameStatus == playGame && (ticks&4)==0)
	   if(KeyVisible)
             gBuffer.drawImage(items[(DoorVisible?IMG_DOORICO:IMG_KEYICO)],baseX+scrwidth-8,baseY+0,Graphics.TOP|Graphics.LEFT);



        if(!(gameStatus == Crashed && blink))
	 for (int i=0;i<loc.getAttemptions()-1;i++){
           gBuffer.drawImage(Heart,baseX+scrwidth-(i+1)*(Heart.getWidth()+2)-10,baseY+scrheight-Heart.getHeight()-2,Graphics.TOP|Graphics.LEFT);
	 }
	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(items[IMG_DEMO],scrwidth>>1,scrheight-items[IMG_DEMO].getHeight()-5,Graphics.HCENTER|Graphics.VCENTER);
	}
        gBuffer.setColor(0x0);
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
	                  g.drawImage(Title,0,0,Graphics.TOP|Graphics.LEFT);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
	      case newStage:  {
	                  g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0x00000);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  gBuffer.setFont(f);
	                  } break;
//	      case Finished: if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_TH_WON);
//	      case gameOver: if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_TH_LOST);

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
              default :  // if (this.isDoubleBuffered())
			      {
		         //       DoubleBuffer(g);
			 //     }
			 //     else {
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed || gameStatus == newStage);
    }



    public void gameLoaded(){
       Treasure_GSR loc = (Treasure_GSR)client_sb.getGameStateRecord();
       blink=false;
       ticks = 1;
       direct=Treasure_SB.DIRECT_NONE;
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.getGameTimedelay();
       stage = loc.getStage();
       gameStatus=newStage;

//       	  if (client_sb.isKeyChanged()) //{
//	    if (!client_sb.isDoorChanged()) {

//       KeyVisible = true;
//       DoorVisible = true;

//       client_sb.nextGameStep();
//       DrawBackScreen();
       bufferChanged=true;
       repaint();
    }
private static final int IMG_TH_FP = 0;
private static final int IMG_TH_LOST = 1;
private static final int IMG_TH_WON = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_MMU1 = 40;
private static final int IMG_MMU2 = 41;
private static final int IMG_MMU3 = 42;
private static final int IMG_MMU0 = 39;
private static final int IMG_PLL0 = 19;
private static final int IMG_PLL3 = 22;
private static final int IMG_PLU0 = 27;
private static final int IMG_PLU1 = 28;
private static final int IMG_PLU2 = 29;
private static final int IMG_PLU3 = 30;
private static final int IMG_MMR0 = 35;
private static final int IMG_MMR3 = 38;
private static final int IMG_DOOR = 8;
private static final int IMG_WATER = 11;
private static final int IMG_APP4 = 17;
private static final int IMG_PLL2 = 21;
private static final int IMG_PLR0 = 23;
private static final int IMG_PLR1 = 24;
private static final int IMG_PLR2 = 25;
private static final int IMG_PLR3 = 26;
private static final int IMG_MML0 = 31;
private static final int IMG_MML2 = 33;
private static final int IMG_MML3 = 34;
private static final int IMG_MMR1 = 36;
private static final int IMG_TREAS = 10;
private static final int IMG_STAIR = 12;
private static final int IMG_APP3 = 16;
private static final int IMG_APP5 = 18;
private static final int IMG_PLL1 = 20;
private static final int IMG_MML1 = 32;
private static final int IMG_MMR2 = 37;
private static final int IMG_KEY = 7;
private static final int IMG_APP2 = 15;
private static final int IMG_DOORICO = 6;
private static final int IMG_BRICK = 9;
private static final int IMG_KEYICO = 5;
private static final int IMG_APP1 = 14;
private static final int IMG_HEART = 4;
private static final int IMG_APP0 = 13;
private static final int TOTAL_IMAGES_NUMBER = 43;

}

