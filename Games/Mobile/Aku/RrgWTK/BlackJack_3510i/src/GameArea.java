
import javax.microedition.lcdui.*;
import com.igormaznitsa.GameKit_FE652.BlackJack.*;
import com.igormaznitsa.GameAPI.*;
import com.igormaznitsa.midp.ImageBlock;


public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable, BlackJackListener,LoadListener{

  private final static int MENU_KEY = -7;
  private final static int END_KEY = -10;
  private final static int DECLINE_KEY = -6;

  private final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  private int CARDZ_W = 20,
              CARDZ_H = 25,
	      CARDZ_OVERLAY = 10;
  private int PACK_CARDZ_X = 0,
              PACK_CARDZ_Y = 0;
  private int DEALER_CARDZ_X = PACK_CARDZ_X+CARDZ_W+3,
              DEALER_CARDZ_Y = PACK_CARDZ_Y;
  private int PLAYER_CARDZ_X = PACK_CARDZ_X ,
              PLAYER_CARDZ_Y = DEALER_CARDZ_Y+CARDZ_H+(CARDZ_H>>1);

  private Font _d_font = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_PLAIN,Font.SIZE_SMALL);
  private int FONT_HEIGHT = _d_font.getHeight()+2;
  private int DIALOG_X = 10,
              DIALOG_Y=7,
	      DIALOG_WIDTH = 80,
	      DIALOG_HEIGHT = 41,
	      DIALOG_SCROLL_STEP = 1,
	      DIALOG_SLEEP_TIME = 10;
  private final static int NONE = 0,
                           UP = 1,
			   DOWN = 2,
			   SHOW_DESK = 3;
  private final static int DIALOG_PRE_DELAY = 500;
  private final static int bsWIDTH = 101, bsHEIGHT = 64, BUTTON_WIDTH = 40;

  private int Reply = -1,
              _player_cardz = 0,
	      _dealer_cardz = 0,
	      direct = 0,
	      _y_position = 0;
  private int _move_card,
              _move_ticks,
	      _move_dx,
	      _move_dy,
	      _dial_base_y = 0;
  private Image iDialogText;
  private Graphics gDialogText;
  private boolean isPlayer = true;
  private String [] sDialogText = null;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;

  public String loadString  = "Loading...";
  public String PlayAgain  = null;
  public String Insurance  = null;
  public String Double  = null;
  public String Payment11  = null;
  public String Bust  = null;
  public String YouLost  = null;
  public String YouWon  = null;
  public String NextCard  = null;
  public String Surrender  = null;
  public String Yes  = null;
  public String No  = null;

  private Image Title=null;
  private Image Lost=null;
//  private Image Won=null;
  private Image Buffer=null;
  private Image Bkg=null;
  private Image Cloud=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;
  private Image MenuIcon = null;
//  private Image Arrows[]=new Image[2];
  private Image iBack=null;
//  private Image cardz[] = new Image[12+4];
  private Image []items;

  protected BlackJack_SB client_sb = new BlackJack_SB();
  protected int baseX=0, baseY=0, deskX = 1;
  private int LoadingTotal =0, LoadingNow = 0;
  public boolean LanguageCallBack = false, keyReverse = false;

  private boolean animation=true;
  private boolean blink=false;
  public final static int   notInitialized = 0,
                            titleStatus = 1,
			    moveCardz = 2,
			    openDealerCard = 3,
			    showDialog = 4,
                            playGame = 5,
			    Finished = 6,
			    gameOver = 7;

  private int stageSleepTime = 100; // in ms
  public int gameStatus=notInitialized;

  private Image []numbers=new Image[10];
  public int fontHeight=0,fontWidth=0, TitleN = 0;
  private boolean bufferChanged=true;
  int blinks=0, ticks=0;
  private boolean showButton = true;
  private boolean terminate = false;
  private int dScore=0,pScore=0;



  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    client_sb.setBJListener(this);
    Buffer=Image.createImage(scrwidth,scrheight);
    gBuffer=Buffer.getGraphics();
    BackScreen=Image.createImage(scrwidth,scrheight);
    gBackScreen=BackScreen.getGraphics();
    iDialogText = Image.createImage(DIALOG_WIDTH,DIALOG_HEIGHT);
    gDialogText = iDialogText.getGraphics();
    gameStatus=notInitialized;
    baseX = (scrwidth-101)>>1;
    baseY = (scrheight-64)>>1;
    deskX = (baseX>=0?baseX:1);
  }

///////////////////////////////////////////////////////////////////////////////////


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
     loading(TOTAL_IMAGES_NUMBER+67);

     try {

	   Runtime.getRuntime().gc();
	   items = (new ImageBlock(this))._image_array;

	//	items[0]=pnl.getImage(pnl.IMG_BOMB3);
	   MenuIcon = items[TOTAL_IMAGES_NUMBER];
           Title=items[IMG_BJ];//Image.createImage(TitleImage);
           Lost=items[IMG_LOST];//Image.createImage(TitleImage);
//           Won=pnl.getImageForID(IMG_WON);//Image.createImage(TitleImage);

	   Bkg = items[IMG_TABLE];
	   iBack = items[IMG_BACK];

/*
	   cardz[0]=pnl.getImageForID(IMG_2);
	   cardz[1]=pnl.getImageForID(IMG_3);
	   cardz[2]=pnl.getImageForID(IMG_4);
	   cardz[3]=pnl.getImageForID(IMG_5);
	   cardz[4]=pnl.getImageForID(IMG_6);
	   cardz[5]=pnl.getImageForID(IMG_7);
	   cardz[6]=pnl.getImageForID(IMG_8);
	   cardz[7]=pnl.getImageForID(IMG_9);
	   cardz[8]=pnl.getImageForID(IMG_10);
	   cardz[9]=pnl.getImageForID(IMG_J);
	   cardz[10]=pnl.getImageForID(IMG_Q);

	   cardz[11]=pnl.getImageForID(IMG_K);

	   cardz[12]=pnl.getImageForID(IMG_A_S);
	   cardz[13]=pnl.getImageForID(IMG_A_C);
	   cardz[14]=pnl.getImageForID(IMG_A_D);
	   cardz[15]=pnl.getImageForID(IMG_A_H);

	   Arrows[0] = pnl.getImageForID(IMG_ARROWUP);
	   Arrows[1] = pnl.getImageForID(IMG_ARROWDWN);
*/
	   Cloud = items[IMG_CLOUD];

          Image tmp=items[IMG_NFONT];//Image.createImage(FontImage);
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
	  items[IMG_NFONT] = null;

     } catch(Exception e) {
//      System.out.println("err:"+e.getMessage());
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



    public void init(int money, String Picture) {
       terminate = false;
       blink=false;
       ticks = 0;
       dScore=0;
       pScore=0;
       System.gc();
       if(money<0){
	  client_sb.newGame(0);
	  midlet.AskBet(((BlackJack_GSR)client_sb.getGameStateRecord()).getPlayerMoney());
       } else {
	 client_sb.initNewGame(money);
         gameStatus=playGame;
         bufferChanged=true;
         _player_cardz = 0;
         _dealer_cardz=((BlackJack_GSR)client_sb.getGameStateRecord()).getDealerCardCounter();

         DrawBackScreen();
         repaint();
       }
    }

    public void loading(){
       gameStatus=notInitialized;
       ticks = 0;
       repaint();
    }


    public void endGame(boolean condition) {
      int prev = gameStatus;
      gameStatus=titleStatus;
      ticks =0;
      Reply =1;
      terminate = true;
      if(condition){
	if(prev==showDialog) try{Thread.sleep(100);}catch(Exception e){}
        BlackJack_GSR loc = null;
        if (client_sb!=null && (loc = (BlackJack_GSR)client_sb.getGameStateRecord())!=null){
	   client_sb.exceptGame();
	  if(loc.getPlayerMoney()>0)
	    midlet.SaveGameMenu();
        }
	repaint();
      }
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
	      case gameOver: if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;
              case titleStatus : {
		                    switch (keyCode) {
				      case MENU_KEY:
				                midlet.ShowMainMenu();
				                break;
				      case END_KEY:  midlet.ShowQuitMenu(false);
				                break;
				      default:
				              if (ticks>=TOTAL_OBSERVING_TIME){
	                                        gameStatus=titleStatus;
			                        ticks=0;
			                        blink=false;
					        repaint();
				              }
		                    }
				    //midlet.commandAction(midlet.newGameCommand,this);
                               } break;
	      case moveCardz:
              case playGame :
				if (keyCode == END_KEY) midlet.ShowQuitMenu(true);
				break;
              case showDialog : {
		                 switch (keyCode) {
				   case Canvas.KEY_NUM5: direct = SHOW_DESK; repaint(); break;
				   case Canvas.KEY_NUM1:
				   case Canvas.KEY_NUM4:
				   case Canvas.KEY_NUM7:
				   case Canvas.KEY_STAR:
				   case DECLINE_KEY: Reply = BlackJackListener.NO;  break;
				   case Canvas.KEY_NUM3:
				   case Canvas.KEY_NUM6:
				   case Canvas.KEY_NUM9:
				   case Canvas.KEY_POUND:
				   case MENU_KEY: Reply = BlackJackListener.YES; break;
				   case END_KEY: midlet.ShowQuitMenu(true); break;
				   default:
	                             switch (/*getGameAction(*/keyCode/*)*/) {
				        case Canvas.KEY_NUM2/*Canvas.UP*/:  direct = UP; break;
				        case Canvas.KEY_NUM8/*Canvas.DOWN*/: direct = DOWN; break;
					default: direct = NONE;
					     break;
	                             }
				    repaint();
		                 }
				}
            }
    }


    protected void keyReleased(int keyCode) {
        if (gameStatus==showDialog) {
	  direct=NONE;
	  repaint();
        }
    }


    private void _process_next_step(BlackJack_GSR loc){
			   if(_player_cardz!=loc.getPlayerCardCounter()) {
			      _player_cardz=loc.getPlayerCardCounter();
			      _move_card = loc.getPlayerCardArray()[loc.getPlayerCardCounter()-1];
			      _move_ticks = loc.getPlayerCardCounter()*2+1;
			      _move_dx = ((PLAYER_CARDZ_X+(loc.getPlayerCardCounter()-1)*CARDZ_OVERLAY - PACK_CARDZ_X)<<4)/_move_ticks;
			      _move_dy = ((PLAYER_CARDZ_Y - PACK_CARDZ_Y)<<4)/_move_ticks;
			      isPlayer = true;
			      gameStatus = moveCardz;
			   }
			   else
			   if(_dealer_cardz!=loc.getDealerCardCounter()) {
			      _dealer_cardz=loc.getDealerCardCounter();
			      _move_card = loc.getDealerCardArray()[loc.getDealerCardCounter()-1];
			      _move_ticks = loc.getDealerCardCounter()*2+1;
			      _move_dx = ((DEALER_CARDZ_X+(loc.getDealerCardCounter()-1)*CARDZ_OVERLAY - PACK_CARDZ_X)<<4)/_move_ticks;
			      _move_dy = ((DEALER_CARDZ_Y - PACK_CARDZ_Y)<<4)/_move_ticks;
			      isPlayer = false;
			      gameStatus = moveCardz;
			   }
			   else
			    switch (loc.getPlayerState()) {
			      case BlackJack_GSR.PLAYER_LOST_BUST: OnceMore(Bust); break;
			      case BlackJack_GSR.PLAYER_LOST_LESS: OnceMore(YouLost); break;
			      case BlackJack_GSR.PLAYER_WON: OnceMore(YouWon+"  $"+loc.getPlayerMoney()); break;
			      //default: System.out.println("state:"+loc.getPlayerState());
			   }
    }

    public void run() {
//     long workDelay = System.currentTimeMillis();
//     long sleepy = 0;

    boolean dealerhidecard = false;
      while(animation) {
	if (LanguageCallBack){
	   LanguageCallBack = false;
           midlet.LoadLanguage();
	}
        if (isShown)
 	 switch (gameStatus) {
           case titleStatus: break;
	   case moveCardz:   ticks++;
	                     if(terminate) gameStatus = titleStatus;
	                     if(ticks>_move_ticks){
			       gameStatus = playGame;
			       DrawBackScreen();
			       BlackJack_GSR loc=(BlackJack_GSR)client_sb.getGameStateRecord();
			       pScore = loc.getPlayerScores();
			       dScore = loc.getAIScores();
	                     }
			     repaint();
	                     break;
	   case openDealerCard:
	                     ticks++;
			     if(terminate) gameStatus = titleStatus;
	                     if (ticks>_move_ticks){
			      gameStatus = playGame;
			      BlackJack_GSR loc=(BlackJack_GSR)client_sb.getGameStateRecord();
			      dScore = loc.getAIScores();
			      gBackScreen.drawImage(getIMG(_move_card), DEALER_CARDZ_X+CARDZ_OVERLAY,DEALER_CARDZ_Y,0);
			      _process_next_step(loc);
			     }
			     repaint();
	                     break;

           case playGame :{
	                   ticks=0;
			   if(terminate) gameStatus = titleStatus;
			   client_sb.nextGameStep();
			   BlackJack_GSR loc = (BlackJack_GSR)client_sb.getGameStateRecord();
			   if (!dealerhidecard && !loc.isPlayerMove()){
			      gameStatus = openDealerCard;
			      DrawBackScreen();
			      _move_card = loc.getDealerCardArray()[1]|Deck.VISIBLE_FLAG;
			      _move_ticks = 4;
			      _move_dx = ((CARDZ_OVERLAY-CARDZ_W-2)<<4)/_move_ticks;
			      _move_dy = 0;
			      isPlayer = false;
			   }
			    else
			     _process_next_step(loc);
			   dealerhidecard=!loc.isPlayerMove();
			   repaint();
			  } break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			      //midlet.newScore(((GameStateRecord)client_sb.getGameStateRecord()).getPlayerScores());
			   repaint();
			   break;

 	 }

//        sleepy = System.currentTimeMillis();
//	workDelay += stageSleepTime-System.currentTimeMillis();
        try {
          thread.sleep(stageSleepTime);

//          if(gameStatus==playGame /*&& Thread.activeCount()>1*/)
//	    while(System.currentTimeMillis()-sleepy<workDelay-10)Thread.yield();
//          else
//          if(workDelay>0)
//             Thread.sleep(workDelay);

	} catch (Exception e) {}

//	workDelay = System.currentTimeMillis();

      }
    }


    private void drawDigits(Graphics gBuffer,int x,int y,int num){
      String a = new String(""+num);
      int _x=x;
      Image d;
      for (int i = 0; i < a.length(); i++){
	d = numbers[(byte)a.charAt(i)-(byte)'0'];
         gBuffer.drawImage(d,_x, y, 0);
	 _x+=d.getWidth();
      }
//	  System.out.println(", "+a);
    }







    private int CreateDialog(String s){
           try {
            thread.sleep(DIALOG_PRE_DELAY);
        } catch (Exception e){}

      direct = NONE;
      Reply = -1;
      _y_position = 0;
      ticks = 0;
      int prevStatus = gameStatus;
      gameStatus = showDialog;

      java.util.Vector v = new java.util.Vector();
      try{
          String l = "";
          int i = 0, j = 0;
          while (j<s.length()){
	     if((j=s.indexOf(" ",i))<0) j=s.length();
	     String k = new String(s.substring(i,j));
	     if (l.length()==0) {l=k; k="";}
	     if(k.length()>0){
	         String h = l+" "+k;
	         if(_d_font.stringWidth(h)>DIALOG_WIDTH) {v.addElement(l);l=k;}
	            else l=h;
	     }
	     i=j+1;
          }
          v.addElement(new String(l));
      } catch (Exception e){}

      sDialogText = new String[v.size()];

      for(int i=0;i<v.size();i++)
	 sDialogText[i]=(String)v.elementAt(i);

      int n = 0;
      _dial_base_y = ((DIALOG_HEIGHT-(n=sDialogText.length*FONT_HEIGHT))>0?(DIALOG_HEIGHT-n)>>1:0);

      repaint();
 //      final String ss="i know, here's a threads bug, but i haven't time to cure it. 'cuse me :(";
	while (Reply == -1 && gameStatus == showDialog){
	   switch (direct){
	       case UP:   if (_y_position>0) {
		            _y_position-=DIALOG_SCROLL_STEP;
			    repaint();
	                  } break;
	       case DOWN  :
	                  if ((FONT_HEIGHT*sDialogText.length-_y_position)>DIALOG_HEIGHT) {
			  _y_position+=DIALOG_SCROLL_STEP;
			  repaint();
	                  }  break;
	   }
           try {
            thread.sleep(DIALOG_SLEEP_TIME);
        } catch (Exception e){}
	}
	if(gameStatus == showDialog) gameStatus = prevStatus;
	return Reply;
    }


    public int requestInsurance() { return CreateDialog(Insurance);}
    public int requestDouble() { return CreateDialog(Double); }
    public int requestPayment11() { return CreateDialog(Payment11);}
    public int requestSurrender() { return 0;/*CreateDialog(Surrender);*/}
    public boolean doYouMove() { return (CreateDialog(NextCard)==YES); }

    public void OnceMore(String msg) {
          showButton = false;
          CreateDialog(msg);
	  showButton = true;
          BlackJack_GSR loc = (BlackJack_GSR)client_sb.getGameStateRecord();
	  if (loc.getPlayerMoney()<=0) {gameStatus = gameOver; return;}
	  else
            if(CreateDialog(PlayAgain)==0)
	          midlet.AskBet(loc.getPlayerMoney());
	    else {
	      endGame(true);
	    }
    }


    private Image getIMG(int x){
      if ((x & Deck.VISIBLE_FLAG)==0) return iBack;
      if((x&0xf)==0x0c) x=0x0c+((x>>4)&0xf);
       else
         x = (x&0xf);
      if(x>16) return null;
      return items[x+IMG_2];
    }


    private void DrawBackScreen(){
        gBackScreen.drawImage(Bkg,0,0,0);

	BlackJack_GSR loc = (BlackJack_GSR)client_sb.getGameStateRecord();

	int[] card = loc.getDealerCardArray();
	int k = loc.getDealerCardCounter();
	Image img = null;

	if (gameStatus==openDealerCard)
	  gBackScreen.drawImage(getIMG(card[0]), DEALER_CARDZ_X,DEALER_CARDZ_Y,0);
	else
	if (loc.isPlayerMove()&& k==2) {
	  gBackScreen.drawImage(getIMG(card[0]), DEALER_CARDZ_X,DEALER_CARDZ_Y,0);
	  gBackScreen.drawImage(getIMG(card[1]), DEALER_CARDZ_X+CARDZ_W+2,DEALER_CARDZ_Y,0);
	} else
	  for(int i = 0; i<k; i++){
	      gBackScreen.drawImage(getIMG(card[i]), DEALER_CARDZ_X+i*CARDZ_OVERLAY,DEALER_CARDZ_Y,0);
	  }

	card = loc.getPlayerCardArray();
	k = loc.getPlayerCardCounter();
	  for(int i = 0; i<k; i++){
	      gBackScreen.drawImage(getIMG(card[i]), PLAYER_CARDZ_X+i*CARDZ_OVERLAY,PLAYER_CARDZ_Y,0);
	  }

	System.gc();
    }



    protected void DoubleBuffer(Graphics gBuffer){

       gBuffer.drawImage(BackScreen,deskX,baseY,0);

       BlackJack_GSR loc = (BlackJack_GSR)client_sb.getGameStateRecord();
       gBuffer.drawImage(iBack, deskX+PACK_CARDZ_X,baseY+PACK_CARDZ_Y,0);
       if(loc.getInsurance()>0)drawDigits(gBuffer,deskX+76,baseY+11,loc.getInsurance());
       drawDigits(gBuffer,deskX+76,baseY+41,loc.getBet());
       if(pScore>0)drawDigits(gBuffer,deskX+PLAYER_CARDZ_X,baseY+PLAYER_CARDZ_Y+CARDZ_H-fontHeight,pScore);
       if(!loc.isPlayerMove() && dScore>0)drawDigits(gBuffer,deskX+DEALER_CARDZ_X,baseY+DEALER_CARDZ_Y,dScore);

       Image img = null;
       switch(gameStatus){
	 case moveCardz:
	                  gBuffer.drawImage(getIMG(_move_card), deskX+PACK_CARDZ_X+(_move_dx*ticks>>4),baseY+PACK_CARDZ_Y+(_move_dy*ticks>>4),0);
	                  break;
	 case openDealerCard:
	                  gBuffer.drawImage(getIMG(_move_card), deskX+DEALER_CARDZ_X+2+CARDZ_W+(_move_dx*ticks>>4),baseY+DEALER_CARDZ_Y,0);
	                  break;
	 case showDialog:
	                 if(direct!=SHOW_DESK){
	                  gDialogText.setColor(0xffffff);
			  gDialogText.fillRect(0,0,DIALOG_WIDTH,DIALOG_HEIGHT);
	                  gDialogText.setColor(0x0);
//	                    gBuffer.drawRect(DIALOG_X-1,DIALOG_Y-1,DIALOG_WIDTH+1,DIALOG_HEIGHT+1);
			  gDialogText.setFont(_d_font);
			  int start = _y_position/FONT_HEIGHT;
			  int ofs = _y_position%FONT_HEIGHT;
			  int end = (DIALOG_HEIGHT/FONT_HEIGHT)+2;
			  for(int i=0;i<end;i++)
			    if(i+start<sDialogText.length)
			      gDialogText.drawString(sDialogText[i+start],(DIALOG_WIDTH>>1),-ofs+i*FONT_HEIGHT+1+_dial_base_y, Graphics.TOP|Graphics.HCENTER);

			  gBuffer.drawImage(Cloud,baseX,baseY,0);
			  gBuffer.drawImage(iDialogText,baseX+DIALOG_X,baseY+DIALOG_Y,0);
			  if ((DIALOG_HEIGHT/FONT_HEIGHT)<sDialogText.length){
			      if(_y_position>0)
			             gBuffer.drawImage(items[IMG_ARROWUP],baseX+DIALOG_X+DIALOG_WIDTH-2/*Arrows[0].getWidth()*/,baseY+DIALOG_Y,0);
			      if ((FONT_HEIGHT*sDialogText.length-_y_position)>DIALOG_HEIGHT)
			             gBuffer.drawImage(items[IMG_ARROWDWN],baseX+DIALOG_X+DIALOG_WIDTH-2/*Arrows[1].getWidth()*/,baseY+DIALOG_Y+DIALOG_HEIGHT-items[IMG_ARROWDWN].getHeight(),0);

			  }
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD|Font.STYLE_ITALIC|Font.STYLE_UNDERLINED,Font.SIZE_SMALL));
			  if(showButton){
			  // draw buttons
			    gBuffer.drawString(No,baseX+(BUTTON_WIDTH>>1),baseY+bsHEIGHT-FONT_HEIGHT-1,Graphics.TOP|Graphics.HCENTER);
			    gBuffer.drawString(Yes,baseX+bsWIDTH-(BUTTON_WIDTH>>1),baseY+bsHEIGHT-FONT_HEIGHT-1,Graphics.TOP|Graphics.HCENTER);
			  } else
			    gBuffer.drawString("Ok",baseX+bsWIDTH-(BUTTON_WIDTH>>1),baseY+bsHEIGHT-FONT_HEIGHT-1,Graphics.TOP|Graphics.HCENTER);
	                 }

       }
        gBuffer.setColor(0x0);
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
	                  g.drawImage(Title,0,0,Graphics.TOP|Graphics.LEFT);
			  g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			  break;
//	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
/*			    if (ticks>FINAL_PICTURE_OBSERVING_TIME)
                            {
				g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0x00000);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateRecord().getPlayerScores() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else */
			    {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(Lost,scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	//g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

              default :   if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			      }
			      else {
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
//                              	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == moveCardz || gameStatus == showDialog);
    }
    public void gameLoaded(){
       terminate = false;
       blink=false;
       ticks = 0;
       dScore=0;
       pScore=0;
       System.gc();
       midlet.AskBet(((BlackJack_GSR)client_sb.getGameStateRecord()).getPlayerMoney());
    }
private static final byte IMG_BJ = (byte)0;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_CLOUD = (byte)22;
private static final byte IMG_BACK = (byte)3;
private static final byte IMG_8 = (byte)10;
private static final byte IMG_7 = (byte)9;
private static final byte IMG_4 = (byte)6;
private static final byte IMG_6 = (byte)8;
private static final byte IMG_3 = (byte)5;
private static final byte IMG_5 = (byte)7;
private static final byte IMG_2 = (byte)4;
private static final byte IMG_TABLE = (byte)2;
private static final byte IMG_9 = (byte)11;
private static final byte IMG_A_D = (byte)18;
private static final byte IMG_A_H = (byte)19;
private static final byte IMG_A_S = (byte)16;
private static final byte IMG_Q = (byte)14;
private static final byte IMG_K = (byte)15;
private static final byte IMG_J = (byte)13;
private static final byte IMG_10 = (byte)12;
private static final byte IMG_NFONT = (byte)23;
private static final byte IMG_A_C = (byte)17;
private static final byte IMG_ARROWUP = (byte)20;
private static final byte IMG_ARROWDWN = (byte)21;
private static final byte TOTAL_IMAGES_NUMBER = (byte)24;

}

/*
.
BJ,0
LOST,1
TABLE,2
BACK,3
2,4
3,5
4,6
5,7
6,8
7,9
8,10
9,11
10,12
J,13
Q,14
K,15
A_S,16
A_C,17
A_D,18
A_H,19
ARROWUP,20
ARROWDWN,21
CLOUD,22
NFONT,23
.
.
*/
