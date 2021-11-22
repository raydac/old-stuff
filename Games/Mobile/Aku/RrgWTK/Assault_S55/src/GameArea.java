import com.igormaznitsa.game_kit_E5D3F2.Assault.*;

import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Melody;
//import com.siemens.mp.game.Light;

public class GameArea extends Canvas implements Runnable, LoadListener , GameActionListener {
//long accuracy=0;

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
/*
    public void activateVibrator(long duration){
        Vibrator.triggerVibrator((int) duration);
    }
*/

  private Melody []sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i]!=null)
	    try{
               sounds[i].stop();
	    }catch(Exception e){}
      isFirst = false;
    }

    public void PlayMelody(int n){
//      stopAllMelodies();
      if(midlet._Sound && sounds!=null && sounds[n]!=null){
          sounds[n].play();
      }
    }



    public boolean soundIsPlaying(){
//      if(sounds!=null)
//        for(int i = 0;i<sounds.length; i++)
//           if(sounds[i].getState()==Sound.SOUND_PLAYING)
//               return true;
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
  private byte [][] ByteArray;
  private Image iBlank;

  private Image MenuIcon = null;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;

  private drawDigits font;

  private final static int MAX_MOVES = 4;

  private int CurrentLevel=0;
  private int stage = 0;
  protected Assault_PMR client_pmr = null;
  protected int direct=Assault_PMR.BUTTON_NONE;
  protected Assault_SB client_sb = null;
  protected int baseX=0, baseY=0, cellW=Assault_SB.VIRTUALCELL_WIDTH,cellH=Assault_SB.VIRTUALCELL_HEIGHT;
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

  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;

  private int back_x,back_y;

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

    client_sb = new Assault_SB(VisWidth, VisHeight, this);
    client_pmr = new Assault_PMR();

    if (!this.isDoubleBuffered())
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    BackScreen=Image.createImage(VisWidth,VisHeight);
    gBackScreen=BackScreen.getGraphics();

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

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

    back_x=(VisWidth-WIDTH_LIMIT)>>1; back_y=(VisHeight-HEIGHT_LIMIT)>>1;
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

	   font = new drawDigits(GetImage(IMG_NFONT));

	   iBlank = Image.createImage(cellW,cellH);
	   Elements[IMG_NFONT] = Elements[IMG_PL_T2];

	   ib = null;

           Runtime.getRuntime().gc();

       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     } catch(Exception e) {
        System.out.println("Can't read images");
	System.exit(-1);
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
       direct=Assault_PMR.BUTTON_NONE;
       client_pmr.i_Value = direct;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
//	    client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    drawBackScreen();
	} else {
           stageSleepTime[playGame] = ((Assault_GSB)client_sb.getGameStateBlock()).i_TimeDelay;
//	   lastPlayerScores = 0;
	   stage = 0;
	   drawBackScreen();
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

	                          switch (keyCode) {
			              case MENU_KEY: midlet.ShowGameMenu(); break;
				      case END_KEY: midlet.ShowQuitMenu(true); break;
				      case Canvas.KEY_NUM1:
				      case Canvas.KEY_NUM4:
				      case Canvas.KEY_NUM7:
				                           direct=Assault_PMR.BUTTON_LEFT; break;
	                              case Canvas.KEY_NUM8:
	                              case Canvas.KEY_NUM5:
	                              case Canvas.KEY_NUM2:
				                           direct=Assault_PMR.BUTTON_FIRE; break;
				      case Canvas.KEY_NUM9:
				      case Canvas.KEY_NUM6:
				      case Canvas.KEY_NUM3:
				                           direct=Assault_PMR.BUTTON_RIGHT; break;
				      default:
					switch (getGameAction(keyCode)) {
				               case Canvas.LEFT: direct=Assault_PMR.BUTTON_LEFT; break;
				               case Canvas.RIGHT: direct=Assault_PMR.BUTTON_RIGHT; break;
				               case Canvas.DOWN:
				               case Canvas.FIRE: direct=Assault_PMR.BUTTON_FIRE; break;
						    default:
						        direct=Assault_PMR.BUTTON_NONE;
					}
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Assault_PMR.BUTTON_NONE;
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
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();
			   Assault_GSB loc=(Assault_GSB)client_sb.getGameStateBlock();
			   if (loc.getPlayerState()!=Assault_GSB.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=Assault_PMR.BUTTON_NONE;
			        client_pmr.i_Value = direct;
//			        client_sb.initStage(stage);
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
           case playGame :{
	                   //if(ticks++>100){Runtime.getRuntime().gc();ticks=0;}
			   client_pmr.i_Value = direct;
			   client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();
			   Assault_GSB loc=(Assault_GSB)client_sb.getGameStateBlock();
/*
			   if (loc.getGameState()==Assault_GSB.GAMESTATE_OVER) {
			       switch (loc.getPlayerState()) {
				      case Assault_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Assault_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case Assault_GSB.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Assault_GSB.PLAYERSTATE_WON: ticks = 0; gameStatus=Finished;
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
			  } break;
	    case Crashed:
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>10){
			     ticks = 0;
			     blink = false;
		             direct=Assault_PMR.BUTTON_NONE;
			     if (((Assault_GSB)client_sb.getGameStateBlock()).getGameState()==Assault_GSB.GAMESTATE_OVER)
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



    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
	 switch(actionID) {
	 case Assault_SB.GAMEACTION_FIELDCHANGED:
		  {
		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
		  }
	 }
    }

    private boolean flagFlood = true;
    private void UpdateScreen(int x,int y, int bb){
          if(!flagFlood) return;
          Image i;
	  x = x*cellW+back_x; y = y*cellH+back_y;
                switch(bb)
                {
                    case Assault_GSB.CELL_DAM: i = GetImage(IMG_DAM); break;
                    case Assault_GSB.CELL_LADDER: i = GetImage(IMG_STAIRWAY); break;
		    default:
		          i = iBlank;
			  i.getGraphics().drawImage(GetImage(IMG_WALL),-x,-y+(((Assault_GSB)client_sb.getGameStateBlock()).i_playeralt+1)*cellH,0);
                }
	 gBackScreen.drawImage(i,x,y,0);
    }

    private void drawBackScreen(){
      gBackScreen.setColor(0xffffff);
      gBackScreen.fillRect(0,0,BackScreen.getWidth(),BackScreen.getHeight());

      Assault_GSB loc=(Assault_GSB)client_sb.getGameStateBlock();

      flagFlood = false;

      gBackScreen.drawImage(Elements[IMG_WALL],0,(loc.i_playeralt+1)*cellH,0);
      for(int i = 0;i<Assault_SB.FIELD_WIDTH;i++)
        for(int j = 0;j<Assault_SB.FIELD_HEIGHT;j++)
	   UpdateScreen(i,j,loc.getElement(i,j));
      flagFlood = true;
    }





    protected void DoubleBuffer(Graphics gBuffer){

//      if (bufferChanged) {
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

        Image img = null;
	// prepare screen
        Assault_GSB loc=(Assault_GSB)client_sb.getGameStateBlock();

	int linkage = VisHeight-65;
	gBuffer.setColor(0xffffff);
	if(linkage>0)                                   gBuffer.fillRect(0,0,VisWidth,linkage);
	if(linkage + BackScreen.getHeight()<VisHeight)  gBuffer.fillRect(0,linkage + BackScreen.getHeight(),VisWidth,VisHeight - linkage - BackScreen.getHeight());

	gBuffer.drawImage(BackScreen,0,linkage,0);


        if(!(gameStatus == Crashed && blink)) {
           gBuffer.drawImage(Elements[IMG_ENEMY_ICO],0,/*VisHeight-iBomb[0].getHeight()-2+BarOffset*/0,0);
	   font.drawDigits(gBuffer,Elements[IMG_ENEMY_ICO].getWidth(),/*VisHeight-(*/(Elements[IMG_ENEMY_ICO].getHeight()-font.fontHeight)>>1/*)-2+BarOffset*/,3,loc.i_curassaulternumber+loc.i_assaulternumber);
	   for (int i=0;i<loc.i_Attemptions-1;i++)
              gBuffer.drawImage(Elements[IMG_HEART],VisWidth-(i+1)*(Elements[IMG_HEART].getWidth()+2),/*VisHeight-iHeart.getHeight()-2*/0,0);
        }

gBuffer.translate(0,linkage);

        // Drawing of the player
        MovingObject p_obj = loc.p_Player;
	switch (p_obj.i_State){
	  case MovingObject.STATE_LEFT:  img = Elements[IMG_PL_L0+p_obj.i_Frame]; break;
	  case MovingObject.STATE_RIGHT:  img = Elements[IMG_PL_R0+p_obj.i_Frame]; break;
	  case MovingObject.STATE_EXPLODE:  img = Elements[IMG_PL_D0+p_obj.i_Frame]; break;
	  default: img = Elements[IMG_PL_T2+p_obj.i_Frame];
	}
	gBuffer.drawImage(img,p_obj.getScrX(), p_obj.getScrY(),0);
        gBuffer.drawImage(Elements[IMG_WALLPART],0,(loc.i_playeralt+1)*cellH-1,0);



        // Drawing of moving objects
        for (int li = 0; li < loc.ap_MovingObjects.length; li++)
        {
            p_obj = loc.ap_MovingObjects[li];
            if (!p_obj.lg_Active) continue;
            switch (p_obj.i_Type)
            {
                case MovingObject.TYPE_ARROW:
		         img = Elements[IMG_ARROW];
			 break;
                case MovingObject.TYPE_ASSAULTER:
			switch (p_obj.i_State){
			       case MovingObject.STATE_LEFT:  img = Elements[IMG_ENEM_L0+p_obj.i_Frame]; break;
			       case MovingObject.STATE_RIGHT:  img = Elements[IMG_ENEM_R0+p_obj.i_Frame]; break;
			       case MovingObject.STATE_EXPLODE:  img = Elements[IMG_ENEM_D0+p_obj.i_Frame]; break;
	                         default: img = Elements[IMG_ENEM_U0+p_obj.i_Frame];
			} break;
/*
                case MovingObject.TYPE_STONE:
			switch (p_obj.i_State){
			       case MovingObject.STATE_EXPLODE:  img = Elements[IMG_STONE_E0+p_obj.i_Frame]; break;
	                         default: img = Elements[IMG_STONE];
			}
*/
            }
	    if(img!=null)
	    gBuffer.drawImage(img,p_obj.getScrX(), p_obj.getScrY(),0);
        }

        // Drawing of the stone
	p_obj = loc.p_Stone;
        if (p_obj.lg_Active)
        {
			switch (p_obj.i_State){
			       case MovingObject.STATE_EXPLODE:  img = Elements[IMG_STONE_E0+p_obj.i_Frame]; break;
	                         default: img = Elements[IMG_STONE];
			}
  	    gBuffer.drawImage(img,p_obj.getScrX(), p_obj.getScrY(),0);
        }

gBuffer.translate(0,-linkage);


//        if(!(gameStatus == Crashed && blink)) {
//           gBuffer.drawImage(iBomb[0],/*baseX+*/0,/*baseY+*/VisHeight-iBomb[0].getHeight()-2+BarOffset,0);
//	   font.drawDigits(gBuffer,iBomb[0].getWidth(),VisHeight-((iBomb[0].getHeight()+font.fontHeight)>>1)-2+BarOffset,2,loc.i_bombs);
//	   for (int i=0;i<loc.i_Attemptions-1;i++)
//              gBuffer.drawImage(iHeart,/*baseX+*/VisWidth-(i+1)*(iHeart.getWidth()+2)-10,/*baseY+*//*VisHeight-iHeart.getHeight()-2*/0,0);
//        }

	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(Elements[IMG_DEMO], VisWidth>>1,VisHeight-Elements[IMG_DEMO].getHeight()-5/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
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
				g.drawString(YourScore+client_sb.getGameStateBlock().getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOSS:IMG_WIN),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
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
       direct=Assault_PMR.BUTTON_NONE;
       Assault_GSB loc =(Assault_GSB)client_sb.getGameStateBlock();
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.i_TimeDelay;
       stage = loc.getStage();
       client_pmr.i_Value = direct;
       client_sb.nextGameStep(client_pmr);
       drawBackScreen();
       gameStatus=playGame;
       bufferChanged=true;
       repaint();
    }

private static final int IMG_FP = 0;
private static final int IMG_LOSS = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_HEART = 4;
private static final int IMG_DAM = 5;
private static final int IMG_WALL = 6;
private static final int IMG_STAIRWAY = 7;
private static final int IMG_WALLPART = 8;
private static final int IMG_ARROW = 9;
private static final int IMG_ENEMY_ICO = 10;
private static final int IMG_ENEM_L0 = 11;
private static final int IMG_ENEM_L1 = 12;
private static final int IMG_ENEM_L2 = 13;
private static final int IMG_ENEM_L3 = 14;
private static final int IMG_ENEM_R0 = 15;
private static final int IMG_ENEM_R1 = 16;
private static final int IMG_ENEM_R2 = 17;
private static final int IMG_ENEM_R3 = 18;
private static final int IMG_ENEM_U0 = 19;
private static final int IMG_ENEM_U1 = 20;
private static final int IMG_ENEM_U2 = 21;
private static final int IMG_ENEM_U3 = 22;
private static final int IMG_ENEM_D0 = 23;
private static final int IMG_ENEM_D1 = 24;
private static final int IMG_ENEM_D2 = 25;
private static final int IMG_ENEM_D3 = 26;
private static final int IMG_STONE = 27;
private static final int IMG_STONE_E0 = 28;
private static final int IMG_STONE_E1 = 29;
private static final int IMG_STONE_E2 = 30;
private static final int IMG_STONE_E3 = 31;
private static final int IMG_PL_L0 = 32;
private static final int IMG_PL_L1 = 33;
private static final int IMG_PL_L2 = 34;
private static final int IMG_PL_L3 = 35;
private static final int IMG_PL_R0 = 36;
private static final int IMG_PL_R1 = 37;
private static final int IMG_PL_R2 = 38;
private static final int IMG_PL_R3 = 39;
private static final int IMG_PL_D0 = 40;
private static final int IMG_PL_D1 = 41;
private static final int IMG_PL_D2 = 42;
private static final int IMG_PL_D3 = 43;
private static final int IMG_PL_T2 = 44;
private static final int IMG_PL_T1 = 45;
private static final int IMG_PL_T0 = 46;
private static final int IMG_NFONT = 47;
private static final int TOTAL_IMAGES_NUMBER = 48;

}

