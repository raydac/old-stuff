
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.Seabattle.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;

public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, PlayerBlock, LoadListener {

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;

  private final static int ANTI_CLICK_DELAY = 10; //x100ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 40; //x100ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 70; //x100ms = 7 seconds , game over / finished sleep time
  private final static int PRE_SHOT_DELAY = 5;
  private final static int VIEW_ENEMY_SHOT_DELAY = 15;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;

  private Image Title=null;
  private Image Lost=null;
  private Image Won=null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image MenuIcon = null;
  private Image MenuIconShip = null;
  private Image [] items = new Image[6];
  private Image [] Ships = null;
  private Image iSight = null;
  private static final int iEmpty = 0, iShip = 1, iHit = 2, iMiss = 3, iDestroy = 4, iOurShip = 5;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;


  protected Seabattle_PMR client_pmr = null;
  protected Seabattle_SB client_sb = new Seabattle_SB();
  protected int cellW=8,cellH=6;
  protected int baseX=0, baseY=0;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false,keyReverse = false;
  private int MoveX=0,MoveY=0;

  private boolean animation=true;
  private boolean blink=false;
  private boolean _viewenemy=true;

  private Image []Desk=new Image[2];
  private Image []Info=new Image[2];
  private Graphics []gDesk=new Graphics[2];
  private byte []MyArray=new byte[100];
  private byte []OppArray=new byte[100];
  private static final int MyDesk = 0, OpponentDesk = 1;
  private int installShip = -1;
  private byte direction = SBLogic.DIRECTION_NORTHWARD;
  private int [] _ships = new int[4];
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    setupScreen = 2,
                            playGame = 3,
			    Finished = 4,
			    gameOver = 5;
  private int stageSleepTime = 100;

  private int playerScore=0;
  private int lastPlayerScores =0;

  public int gameStatus=notInitialized;
  private boolean bufferChanged=true;
  int blinks=0, ticks=0;

  public int fontHeight=0,fontWidth=0, TitleN = 0, ShipNumberBase = 0, ShipHeight = 0;
  private Image []numbers=new Image[10];
  private boolean OpponentView = false;


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
    for(int i=0;i<2;i++){
      Desk[i]=Image.createImage(cellW*10+1,cellH*10+1);
      gDesk[i]=Desk[i].getGraphics();
    }
    baseX = (scrwidth-cellW*10)>>1;
    baseY = (scrheight-cellH*10)>>1;
    gameStatus=notInitialized;
  }

///////////////////////////////////////////////////////////////////////////////////


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord){

       client_pmr.setXY(MoveX,MoveY);

       return client_pmr;
    }

    public void start() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
    }

    public void nextItemLoaded(int nnn){
       LoadingNow=Math.min(LoadingNow+nnn,LoadingTotal);
       repaint();
    }

    public void start(int kkk) {
     start();
     loading(kkk);

     try {
	//	items[0]=pngresource.getImage(pngresource.IMG_BOMB3);
	   Image[]a = (new ImageBlock(this))._image_array;

	   MenuIcon = a[TOTAL_IMAGES_NUMBER];
           Title=a[IMG_SB];//Image.createImage(TitleImage);
           Lost=a[IMG_LOST];
           Won=a[IMG_WON];
	   iSight=a[IMG_SIGHT];
	   MenuIconShip=a[IMG_MENUICO];

          Image tmp=a[IMG_NFONT];//Image.createImage(FontImage);
          fontHeight = tmp.getHeight();
          fontWidth = tmp.getWidth()/10;
          for (int i = 1;i<10; i++){
	      numbers[i] = Image.createImage(fontWidth,fontHeight);
	      numbers[i].getGraphics().drawImage(tmp,-(i-1)*fontWidth,0,Graphics.TOP|Graphics.LEFT);
          }
	  numbers[0] = Image.createImage(fontWidth,fontHeight);
	  numbers[0].getGraphics().drawImage(tmp,-9*fontWidth,0,Graphics.TOP|Graphics.LEFT);

	  tmp = null;

           Info[MyDesk]=Image.createImage(20,scrheight);
           Info[OpponentDesk]=Image.createImage(20,scrheight);

           tmp=a[IMG_PLAYERICO];
	   Info[OpponentDesk].getGraphics().drawImage(tmp,2,1,0);

	  ShipNumberBase=tmp.getHeight()+2;
	  tmp = null;

           tmp=a[IMG_OPPICO];
	   Info[MyDesk].getGraphics().drawImage(tmp,2,1,0);

	  tmp = null;

	  Ships = new Image[4];

	   byte [] ab = {IMG_SHIP4,IMG_SHIP3,IMG_SHIP2,IMG_SHIP1};
	   for(int i = 0;i<4;i++){
              Ships[i]=a[ab[i]];
	      Info[OpponentDesk].getGraphics().drawImage(Ships[i],2,ShipNumberBase+i*(Ships[i].getHeight()+fontHeight),0);
	      Info[MyDesk].getGraphics().drawImage(Ships[i],2,ShipNumberBase+i*(Ships[i].getHeight()+fontHeight),0);
	   }
	   ShipHeight = Ships[3].getHeight();

           for (int i = 0; i<items.length;i++)
	   {
	     items[i] = Image.createImage(cellW,cellH);
	     Graphics g = items[i].getGraphics();
	     g.setColor(0);
	     g.drawRect(0,0,10,10);
	     g.setColor(0xffffff);
	     for (int j=0;j<10;j++)
	       g.drawRect(-1,-1,j+2,++j+1);

	     g.setColor(0);
	     switch (i){
	       //case 0: g.drawRect(0,0,10,10); break;
	       case 1: g.fillRect(0,0,10,10); break;
	       case 2: g.drawLine(0,0,cellW,cellH); g.drawLine(0,cellH,cellW,0); break;
	       case 3: g.fillRect(cellW/2-1,cellH/2-1,2,2); break;
	       case 4: g.fillRect(0,0,10,10); break;
	       case 5: for (int j=0;j<10;j++) {
		           g.drawLine(-1+j,-1,-1+j+10,-1+10);
		           g.drawLine(-1,-1+j,-1+10,-1+j+10);
			   j++;
	               }
	     }
	   }
/*
	   Ships = new Image[4];
	   for (int i=4;i>0;i--){
	    Ships[4-i] = Image.createImage(cellW*i,cellH);
	    for(int j=0;j<i;j++)
	      Ships[4-i].getGraphics().drawImage(items[iShip],j*cellW,0,0);
	   }
*/
     } catch(Exception e) {
     }

     System.gc();

    }


    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Seabattle_PMR(0,0);

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
       installShip = -1;
       playerScore=0;
       blink=false;
       ticks = 0;
       MoveX = 5;
       MoveY = 4;
       client_sb.newGame(0);
       lastPlayerScores = 0;

       switch(level) {
	 case 0: {
	            gameStatus=setupScreen;
		    ClearDesk();
	         } break;
	 case 1: {
	           ((Seabattle_GSR)client_sb.getGameStateRecord()).getPlayerLogic().autoPlacingOurShips();
		   startPlaying();
/*
	           bufferChanged=true;
		   gameStatus = playGame;
		   PrepareOpponentDesk();
		   PreparePlayerDesk();
                   repaint();
*/		 }
	}
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
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
//	      case demoPlay:
              case titleStatus : {
		                    switch (keyCode) {
				      case MENU_KEY:
				                midlet.ShowMainMenu();
				                break;
				      case END_KEY:  midlet.ShowQuitMenu(false);
				                break;
				      default:
				              if (/*gameStatus == demoPlay ||*/ ticks>=TOTAL_OBSERVING_TIME){
	                                        gameStatus=titleStatus;
			                        ticks=0;
			                        blink=false;
					        repaint();
				              }
		                    }
				    //midlet.commandAction(midlet.newGameCommand,this);
                               } break;
	      case setupScreen:
			       if (keyCode == MENU_KEY) {callMenu(); break;}
			       if (keyCode == END_KEY) {
				    if (installShip>-1) {
				       installShip=-1;
				       repaint();
				    } else midlet.ShowQuitMenu(true);
				    break;
	                        }
			        if (keyCode==KEY_NUM0){direction=(byte)(++direction&3); repaint(); break;}

              case playGame :  {
			       int action = getGameAction(keyCode);
			       if (keyCode==KEY_NUM0)_viewenemy=true; else
			       if (keyCode == MENU_KEY) midlet.ShowGameMenu(); else
			       if (keyCode == END_KEY) midlet.ShowQuitMenu(true); else
	                          switch (keyCode/*action*/) {
				   case Canvas.KEY_NUM4/*LEFT*/     : if (MoveX>0)MoveX--;  break;
				   case Canvas.KEY_NUM6/*RIGHT*/    : if (MoveX<9)MoveX++;  break;
				   case Canvas.KEY_NUM2/*UP*/       : if (MoveY>0)MoveY--;  break;
				   case Canvas.KEY_NUM8/*DOWN*/     : if (MoveY<9)MoveY++;  break;
				   case Canvas.KEY_NUM5/*FIRE*/     :
				                   Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
				                   if(gameStatus==playGame){
						    if (loc.isPlayerMoving() && !(OpponentView || _viewenemy)){
						      if(loc.getPlayerLogic().getOpponentElement(MoveX,MoveY)==SBLogic.BS_FIELD_EMPTY){
				                        client_sb.nextGameStep();
							ticks=0;
							if(!loc.isPlayerMoving())OpponentView=true;
						      }
				                      UpdateOpponent();
						    }
	                                           } else {
						     if(installShip>-1)
						        if(loc.getPlayerLogic().placingShip(installShip+1,MoveX,MoveY,direction)){
							  _ships[installShip]--;
							  installShip=-1;
							  PreparePlayerDesk();
							  callMenu();
						        };
	                                           }
						   break;
	                          }
				repaint();
                               }
			      // System.out.println(keyCode);
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	 // direct=Seabattle_PMR.BUTTON_NONE;
	  _viewenemy = false;
	  repaint();
        }
    }


    public void run() {

      while(animation) {
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
	    case setupScreen: blink=(ticks&4)==0;repaint(); ticks++;break;
	    case playGame:    Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
	                      if (OpponentView && ticks++==PRE_SHOT_DELAY) {
	                        if(!loc.isPlayerMoving()){
                                    client_sb.nextGameStep();
				    UpdateMyDesk();
				    if (!loc.isPlayerMoving()) ticks = 0;
	                        }
	                      }
	                      if (OpponentView && ticks>=VIEW_ENEMY_SHOT_DELAY) {
				 OpponentView = !loc.isPlayerMoving();
				 ticks = 0;
	                      }

                                 if (loc.getGameState() == Seabattle_GSR.GAMESTATE_OVER){
				    ticks = 0;
                                    if (loc.getPlayerState()==Seabattle_GSR.PLAYERSTATE_LOST)
				          gameStatus = gameOver;
                                        else
				           gameStatus = Finished;
                                 }
			      repaint();
			      break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>TOTAL_OBSERVING_TIME){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME);
//			      midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
			   repaint();
			   break;


 	 }

        try {
          thread.sleep(stageSleepTime);
	} catch (Exception e) {}

      }
    }

    protected void PrepareOpponentDesk(){
        Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
	SBLogic op = loc.getPlayerLogic();
	System.arraycopy(op.getOpponentArray(),0,OppArray,0,OppArray.length);

	int k =0, l = 0;
        for(int i=0;i<10;i++){
          for(int j=0;j<10;j++) {
	     gDesk[OpponentDesk].drawImage(items[op.getOpponentElement(i,j)],i*cellW,j*cellH,0);
          }
	  gDesk[OpponentDesk].drawImage(items[0],i*cellW,10*cellH,0);
	  gDesk[OpponentDesk].drawImage(items[0],10*cellW,i*cellH,0);
        }
	gDesk[OpponentDesk].drawImage(items[0],10*cellW,10*cellH,0);

    }
    protected void PreparePlayerDesk(){
        Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
	SBLogic pl = loc.getAILogic();
	SBLogic op = loc.getPlayerLogic();
	System.arraycopy(pl.getOpponentArray(),0,MyArray,0,MyArray.length);

	int k =0, l = 0;
        for(int i=0;i<10;i++){
          for(int j=0;j<10;j++) {
	     k = op.getOurElement(i,j);
	     l = pl.getOpponentElement(i,j);
	     if (l==SBLogic.BS_FIELD_EMPTY && k!=SBLogic.BS_FIELD_EMPTY)
	            l = iOurShip;
	     gDesk[MyDesk].drawImage(items[l],i*cellW,j*cellH,0);
          }
	  gDesk[MyDesk].drawImage(items[0],i*cellW,10*cellH,0);
	  gDesk[MyDesk].drawImage(items[0],10*cellW,i*cellH,0);
        }
	gDesk[MyDesk].drawImage(items[0],10*cellW,10*cellH,0);
    }

    protected void UpdateOpponent(){
//      boolean t = false;
        Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
	byte []arr = loc.getPlayerLogic().getOpponentArray();
	for (int i=0;i<100;i++)
	  if(OppArray[i]!=arr[i]){
	    OppArray[i]=arr[i]; //t=true;
	    gDesk[OpponentDesk].drawImage(items[arr[i]],(i%10)*cellW,(i/10)*cellH,0);
	  }
	arr=null;
//     System.out.println("Update View, "+(t?"Someting were changed":"Nothing changed"));
    }

    protected void UpdateMyDesk(){
        Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
	byte []arr = loc.getAILogic().getOpponentArray();
	for (int i=0;i<100;i++)
	  if(MyArray[i]!=arr[i]){
	    MyArray[i]=arr[i];
	    gDesk[MyDesk].drawImage(items[arr[i]],(i%10)*cellW,(i/10)*cellH,0);
	  }
	arr=null;
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


    protected void DoubleBuffer(Graphics gBuffer){

//      if (bufferChanged) {

        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0,0,scrwidth,scrheight);
        gBuffer.setColor(0x0);

	// prepare screen
//	  gBuffer.drawImage(Desk[OpponentDesk],0,0,0);
	if(gameStatus==setupScreen) {
	  gBuffer.drawImage(Desk[MyDesk],0,0,0);
	   if(installShip>-1){
	    int dx = 0, dy = -1;
            switch (direction) {
	      case SBLogic.DIRECTION_EASTWARD  : dx = 1; dy = 0; break;
//            case SBLogic.DIRECTION_NORTHWARD : dx = 0; dy = -1; break;
              case SBLogic.DIRECTION_SOUTHWARD : dx = 0; dy = 1; break;
              case SBLogic.DIRECTION_WESTWARD  : dx = -1; dy = 0; break;
            }
	     for (int i=0;i<=installShip;i++)
	      gBuffer.drawImage(items[iShip],(MoveX+dx*i)*cellW,(MoveY+dy*i)*cellH,0);
	  }
	  if(blink)gBuffer.drawImage(MenuIconShip,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
	}
	else
	{
           Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();

	   SBLogic pl = loc.getAILogic();
	   int idx = OpponentDesk;
	   int tx = MoveX;
	   int ty = MoveY;
	   int ofs = ShipNumberBase+ShipHeight;

	   if(OpponentView || _viewenemy){
              idx=MyDesk;
	      tx = loc.getLastOpponentX();
	      ty = loc.getLastOpponentY();
	      pl = loc.getPlayerLogic();
	   }

           gBuffer.drawImage(Desk[idx],0,0,0);
           gBuffer.drawImage(Info[idx],81,0,0);

	   drawDigits(gBuffer,91-(fontHeight>>1),ofs,1,pl.getShip4Counter());
	   ofs+=ShipHeight+fontHeight;
	   drawDigits(gBuffer,91-(fontHeight>>1),ofs,1,pl.getShip3Counter());
	   ofs+=ShipHeight+fontHeight;
	   drawDigits(gBuffer,91-(fontHeight>>1),ofs,1,pl.getShip2Counter());
	   ofs+=ShipHeight+fontHeight;
	   drawDigits(gBuffer,91-(fontHeight>>1),ofs,1,pl.getShip1Counter());
	   ofs+=ShipHeight+fontHeight;


	   gBuffer.drawImage(iSight,tx*cellW+(cellW>>1),ty*cellH+(cellH>>1),Graphics.HCENTER|Graphics.VCENTER);
	}

//        gBuffer.setColor(0x0);
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
/*			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else */{
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
                              	if (gameStatus != setupScreen)  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }

    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded(){
       installShip =  -1;
       playerScore=0;
       blink=false;
       ticks = 0;
       MoveX = 0;
       MoveY = 0;
       startPlaying();
    }



   public void callMenu(){
       Seabattle_GSR loc=(Seabattle_GSR)client_sb.getGameStateRecord();
       midlet.ShowShipMenu(_ships,Ships);
   }
   public void InstallShip(int n){
      installShip =  n;
//      System.out.println(n);
      repaint();
   }
   public void ClearDesk(){
      installShip =  -1;
      _ships = new int[]{SBLogic.SHIPS_COUNT_1,SBLogic.SHIPS_COUNT_2,SBLogic.SHIPS_COUNT_3,SBLogic.SHIPS_COUNT_4};
      ((Seabattle_GSR)client_sb.getGameStateRecord()).getPlayerLogic().clearOurField();
      PreparePlayerDesk();
//      PrepareOpponentDesk();
      repaint();
      callMenu();
   }

   public void startPlaying(){
      installShip =  -1;
      bufferChanged=true;
      ticks = 7;
      OpponentView = !((Seabattle_GSR)client_sb.getGameStateRecord()).isPlayerMoving();
      _viewenemy = false;
      PrepareOpponentDesk();
      PreparePlayerDesk();
      gameStatus = playGame;
      repaint();
   }
private static final byte IMG_SB = (byte)0;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_WON = (byte)2;
private static final byte IMG_NFONT = (byte)11;
private static final byte IMG_OPPICO = (byte)5;
private static final byte IMG_PLAYERICO = (byte)6;
private static final byte IMG_SIGHT = (byte)3;
private static final byte IMG_MENUICO = (byte)4;
private static final byte IMG_SHIP2 = (byte)9;
private static final byte IMG_SHIP4 = (byte)7;
private static final byte IMG_SHIP3 = (byte)8;
private static final byte IMG_SHIP1 = (byte)10;
private static final byte TOTAL_IMAGES_NUMBER = (byte)12;

}

/*
.
SB,0
LOST,1
WON,2
SIGHT,3
MENUICO,4
OPPICO,5
PLAYERICO,6

SHIP4,7
SHIP3,8
SHIP2,9
SHIP1,10

NFONT,11
.
.
*/
