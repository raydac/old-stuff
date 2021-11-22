
import javax.microedition.lcdui.*;
import com.itx.mbgame.GameObject;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;


public class GameArea extends FullCanvas implements Runnable, LoadListener, GameActionListener {
//long accuracy=0;

  private final static int WIDTH_LIMIT = 96;
  private final static int HEIGHT_LIMIT = 65;

/*=============================*/

    private static final int KEY_MENU = -7;
    private static final int KEY_CANCEL = -6;
    private static final int KEY_ACCEPT = -7;
    private static final int KEY_BACK = -10;

    public void setScreenLight(boolean state)
    {
      DeviceControl.setLights(0, state?100:0);
    }
    public void activateVibrator(int n){}

protected Sound [] sounds;
    public void stopAllMelodies(){
      if(sounds!=null && sounds.length>0)
//       synchronized(sounds){
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               sounds[i].stop();
//       }
//      isFirst = false;
    }
int SoundPlayTimeGap = 250;
long sound_timemark = 0;
    public void playSound(int n){
      if(sounds!=null && sounds.length>0 && midlet._Sound){
//	stopAllMelodies();
//       synchronized(sounds){
        if(soundIsPlaying() && System.currentTimeMillis()-sound_timemark<SoundPlayTimeGap) return;
	sound_timemark = System.currentTimeMillis();
	stopAllMelodies();
	sounds[n].play(1);
       }
//      }
    }
    public boolean soundIsPlaying(){
      if(sounds!=null)
        for(int i = 0;i<sounds.length; i++)
           if(sounds[i].getState()==Sound.SOUND_PLAYING)
               return true;
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

  private Image MenuIcon;
  private Image Buffer=null;
  private Graphics gBuffer=null;
  private Image BackScreen=null;
  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String YourScore  = null;
  public String NewStage  = null;
  public String unpacking = null;
  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected int direct;
  protected Bulgar client_sb;
  protected game_PMR client_pmr;
  protected int baseX=0, baseY=0;
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
			         // ni  ts  dp  ns  pg  cr  fi  go
  private int [] stageSleepTime = {100,100,100,100,100,200,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 300; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;

//  private final static int BkgCOLOR = 0x000061; //for Nokia
  private final static int BACKGROUND_COLOR = 0x0D004C;
  private int BkgCOLOR, InkCOLOR, LoadingBar, LoadingColor;
  private int VisWidth, VisHeight;
  boolean isFirst;
  private int scr2, center_x, center_y , start_x, start_y;

  private int bsWIDTH, bsHEIGHT, bs_X_FRAMES, bs_X_LAST_FRAME, bs_X_POS, LOC_X, OFS_X
		               , bs_Y_FRAMES, bs_Y_LAST_FRAME, bs_Y_POS, LOC_Y, OFS_Y
			       , CELL_SIZE;

  private int FLOOR_COLOR;// = 0x737373;
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

    client_sb = new Bulgar(VisWidth, VisHeight, this);
    client_pmr = new game_PMR();

    if (!this.isDoubleBuffered())
    {
       Buffer=Image.createImage(scrwidth,scrheight);
       gBuffer=Buffer.getGraphics();
    }

    gameStatus=notInitialized;
    baseX = (scrwidth - VisWidth+1)>>1;
    baseY = (scrheight - VisHeight+1)>>1;

    CELL_SIZE = client_sb.quadWH;
    bs_X_FRAMES = (VisWidth + CELL_SIZE-1)/ CELL_SIZE +3;
    bs_Y_FRAMES = (VisHeight + CELL_SIZE-1)/ CELL_SIZE +3;
    bsWIDTH = bs_X_FRAMES * CELL_SIZE;
    bsHEIGHT = bs_Y_FRAMES * CELL_SIZE;
    OFS_X = ((VisWidth + CELL_SIZE-1) / CELL_SIZE)>>1;
    OFS_Y = ((VisHeight + CELL_SIZE-1) / CELL_SIZE)>>1;

    center_x = (VisWidth - CELL_SIZE)>>1;
    center_y = (VisHeight - CELL_SIZE)>>1;

    start_x = center_x - (OFS_X+1) * CELL_SIZE;
    start_y = center_y - (OFS_Y+1) * CELL_SIZE;

    if(colorness){
        BkgCOLOR = 0x000061; //for Nokia
	InkCOLOR = 0xffff00;
	LoadingBar = 0x00ff00;
	LoadingColor = 0x000080;
	FLOOR_COLOR = 0x737373;
    } else {
        BkgCOLOR = 0xffffff;
	InkCOLOR = 0x000000;
	LoadingBar = 0x000000;
	LoadingColor = 0x000000;
	FLOOR_COLOR = 0xffffff;
    }
    scr2 = VisWidth>>1;
    isFirst = true;
  }

///////////////////////////////////////////////////////////////////////////////////

    long loadtime=0;

    public void nextItemLoaded(int li){
       LoadingNow=Math.min(LoadingNow+1,LoadingTotal);
       if(loadtime < System.currentTimeMillis() || LoadingNow >= LoadingTotal) {
	 loadtime = System.currentTimeMillis()+100; // 0,1sec slice
         repaint();
       }
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
	   ib = null;

           Runtime.getRuntime().gc();

           BackScreen=Image.createImage(bsWIDTH ,bsHEIGHT);
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
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    client_sb.initStage(2);
	    client_pmr.i_Value = direct;
            client_sb.nextGameStep(client_pmr);
	    repaintBackScreen();
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
//           stageSleepTime[playGame] = client_sb.i_TimeDelay;
	   stage = 0;
//           client_sb.nextGameStep(client_pmr);
	   gameStatus=newStage;
	}
	bufferChanged=true;
//	DoubleBuffer(gBuffer);
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
       stopAllMelodies();
       direct = 0;
       CleanSpace();
    }

    protected void keyRepeated(int keyCode) {keyPressed(keyCode);}

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
		                    stopAllMelodies();
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
			             case MENU_KEY:  midlet.ShowGameMenu(); break;
				     case END_KEY:  midlet.ShowQuitMenu(true); break;
				     case Canvas.KEY_NUM2 : direct = game_PMR.BUTTON_UP; break;
				     case Canvas.KEY_NUM4 : direct = game_PMR.BUTTON_LEFT; break;
				     case Canvas.KEY_NUM6 : direct = game_PMR.BUTTON_RIGHT; break;
				     case Canvas.KEY_NUM8 : direct = game_PMR.BUTTON_DOWN; break;
				         default:
				            switch(getGameAction(keyCode)) {
					       case Canvas.LEFT : direct = game_PMR.BUTTON_LEFT; break;
					       case Canvas.RIGHT: direct = game_PMR.BUTTON_RIGHT; break;
					       case Canvas.UP   : direct = game_PMR.BUTTON_UP; break;
					       case Canvas.DOWN : direct = game_PMR.BUTTON_DOWN; break;
					           default: direct=game_PMR.BUTTON_NONE;
				            }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	    direct=game_PMR.BUTTON_NONE;
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
	                if(isFirst){
			  isFirst = false;
			  //PlayMelody(SND_JBELLS_OTT);
			  ticks = 0;
	                }
			if (ticks<80) ticks++;
			 else
			  if(soundIsPlaying())ticks-=10;
			  else
			  {
			   Runtime.getRuntime().gc();
			   ticks=0;
			   stopAllMelodies();
			   init(demoLevel,null/*restxt.LevelImages[demoLevel]*/);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
			   if (client_sb.bulgar.getDirection()==GameObject.DIR_STOP && (ticks%3 == 0))
			     client_pmr.i_Value = client_sb.getRandomInt(game_PMR.BUTTON_DOWN/*4*/);
		           client_sb.nextGameStep(client_pmr);
			   updateBackScreen();

			   if (client_sb.i_PlayerState!=Gamelet.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
                               //playerScore=loc.getPlayerScores();
			       ticks=0;
			       blink=false;
			       isFirst = true;
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
			  } break;
	   case newStage: {
	                      if(ticks++==0) {
		                direct=game_PMR.BUTTON_NONE;
				CleanSpace();
			        client_sb.initStage(stage);
	                        client_pmr.i_Value = direct;
                                client_sb.nextGameStep(client_pmr);
				repaintBackScreen();
				Runtime.getRuntime().gc();
	                      }
			      else
	                      if (ticks++>25){
			        gameStatus=playGame;
			        repaint();
	                      }
			    } break;
           case playGame :{
			   ticks++;
	                   client_pmr.i_Value = direct;
                           client_sb.nextGameStep(client_pmr);
//			   if(!is_painting)
			     updateBackScreen();
			         switch (client_sb.i_PlayerState) {
					case Gamelet.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Gamelet.PLAYERSTATE_WON: ticks = 0;
					                         //PlayMelody(SND_APPEARANCE_OTT);

								       stage++;
					                               if (stage>Stages.TOTAL_STAGES)
								         gameStatus=Finished;
								       else
					                                 gameStatus=newStage;
								      break;
			         }
			   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
//			   serviceRepaints();
			  } break;
	    case Crashed:
			   stopAllMelodies();
			   ticks++; blink=(ticks&2)==0;
			   if(ticks>20){
			     ticks = 0;
			     blink = false;
		             direct=game_PMR.BUTTON_NONE;
			     if (client_sb.i_GameState==Gamelet.GAMESTATE_OVER){
			            gameStatus=gameOver;
			            // PlayMelody(SND_LOST_OTT);
			     } else {
			           client_sb.resumeGameAfterPlayerLost();
		                   direct=game_PMR.BUTTON_NONE;
	                           client_pmr.i_Value = direct;
                                   client_sb.nextGameStep(client_pmr);
				   repaintBackScreen();
			           gameStatus=playGame;
				   Runtime.getRuntime().gc();
	                           // PlayMelody(SND_APPEARANCE_OTT);
			      }
			   }
	                   bufferChanged=true;
//	                   DoubleBuffer(gBuffer);
			   repaint();
			   break;
	    case gameOver:
	    case Finished:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME)
			     if(soundIsPlaying())ticks=FINAL_PICTURE_OBSERVING_TIME-10;
			      else
			        midlet.newScore(client_sb.getPlayerScore());
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

//	serviceRepaints();
	workDelay = System.currentTimeMillis();
//        if(is_painting) serviceRepaints();

      }
    }

    public void gameAction(int actionID){}
    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID,int x,int y){
//	 switch(actionID) {
//	 case RescueHelicopter.GAMEACTION_FIELDCHANGED:
		  {
//		       UpdateScreen(x,y,((Assault_GSB)client_sb.getGameStateBlock()).getElement(x,y));
                     int item = client_sb.level[y][x];
//		     if(actionID==Bulgar.ACTION_BUTTONPUSH){x++;y--;}
                     int dx = x - bs_X_LAST_FRAME,
                         dy = y - bs_Y_LAST_FRAME;
                     if(Math.abs(dx)<=OFS_X+1 && Math.abs(dy)<=OFS_Y+1) {
//		       System.out.println(x+","+y+",aID="+actionID);
		       dx+=bs_X_POS+OFS_X+1;
		       dy+=bs_Y_POS+OFS_Y+1;
		       if(dx>=bs_X_FRAMES)dx-=bs_X_FRAMES;
//		        else if(dx<0){dx+=bs_X_FRAMES;System.out.println("Catcha!!!! X");}
		       if(dy>=bs_Y_FRAMES)dy-=bs_Y_FRAMES;
//		         else if(dy<0){dy+=bs_Y_FRAMES;System.out.println("Catcha!!!! Y");}
		       dx *= CELL_SIZE;
		       dy *= CELL_SIZE;
	               gBackScreen.setColor(FLOOR_COLOR);
	               gBackScreen.fillRect(dx,dy,CELL_SIZE,CELL_SIZE);
		       switch(actionID){
		           case Bulgar.ACTION_BUTTONPUSH:      item = IMG_LEVER01;
//				                               gBackScreen.drawImage(GetImage(IMG_LEVER01),dx,dy,0);
			                                break;
		           case Bulgar.ACTION_DOOR_KEY_OPENEED:
		           case Bulgar.ACTION_DOOR_BUTTON_OPEN:
				  		        if (item == 91) item = IMG_DOOR_V02; // Vertical
	                                                   else /*if (item == 92)*/ item = IMG_DOOR_H02; // Horizontal
//							gBackScreen.drawImage(GetImage(item),dx,dy,0);
							break;
		         //case Bulgar.ACTION_KEYFOUNDED:
		         //case Bulgar.ACTION_TREASUREFOUNDED:
				default: return;
		       }
	               Image img = GetImage(item);
	               gBackScreen.drawImage(img,
	                        dx+((CELL_SIZE - img.getWidth())>>1),
				dy+((CELL_SIZE - img.getHeight())>>1),0);
                     }
		  }
//	 }
    }

/*
    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(~color);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }
*/

    private void drawCell(int x, int y ,int _x,int _y){
           int qd = IMG_DEMO;//IMG_BACK_TILE;
	   int [][] map = client_sb.level;

           if( x >= 0 && y >= 0 && y < map.length && x < map[0].length) {
	       qd = map[y][x];
	       if(qd == 0) return;
               // Walls, 6 of
               if (qd < 7 )  {

		                  qd = IMG_WALL01+qd-1;
			if(!colorness) //create custom border
			{
	                          Image img = GetImage(qd);
	                          gBackScreen.drawImage(img,
				        _x+((CELL_SIZE - img.getWidth())>>1),
				        _y+((CELL_SIZE - img.getHeight())>>1),0);

				  x++; _x += CELL_SIZE-1; _y += CELL_SIZE-1;
				  gBackScreen.setColor(0x0);
				  if(x>=map[0].length || (qd = map[y][x])>=7 || qd<=0) gBackScreen.drawLine(_x,_y-CELL_SIZE+1,_x,_y);
				  x--;y++;
				  if(y>=map.length || (qd = map[y][x])>=7 || qd<=0) gBackScreen.drawLine(_x-CELL_SIZE+1,_y,_x,_y);
				  return;
			}
               }
               else
               // Floor buttons
               if (qd >= 70 && qd <= 89) qd = IMG_LEVER02;
	       else
               // Pushed floor button
               if (qd == 90) qd = IMG_LEVER01;
	       else
               // Doors
               if (qd >= 10 && qd <= 29)
               {
                     if (qd < 20)
		             qd = IMG_DOOR_V01; // Vertical
                          else
			     qd = IMG_DOOR_H01; // Horizontal
               }
	       else
               // Opened doors
               if (qd == 91) qd = IMG_DOOR_V02; // Vertical
	       else
	       if (qd == 92) qd = IMG_DOOR_H02; // Horizontal
	       else
               // Electroshok
               if(qd == 150 || qd == 151) qd = IMG_SIGN_H_OFF;
	       else
               // Keys
               if (qd >= 50 && qd <= 69) qd = IMG_KEY+qd%4;
	       else
	       // Treasure
               if (qd >= 110 && qd <= 127)
	       {
		 switch(qd%5){
		   case 1://ring
		          qd = IMG_RING;
			  break;
		   case 2://gem01,gem02
			  qd = IMG_GEM01;
		          break;
		   case 3://crown,68
			  qd = IMG_CROWN;
		          break;
		   case 4://emerald,emerald02,
			  qd = IMG_EMERALD;
		          break;
		   default: qd = IMG_MONEY;
		 }
	       }
	       else
               // Exit
               if (qd == 193) qd = IMG_EXIT02;
	       else return;
           } else return;
	  Image img = GetImage(qd);
	  gBackScreen.drawImage(img,
	                        _x+((CELL_SIZE - img.getWidth())>>1),
				_y+((CELL_SIZE - img.getHeight())>>1),0);
    }
    private void repaintBackScreen(){
      gBackScreen.setColor(FLOOR_COLOR);
      gBackScreen.fillRect(0,0,bsWIDTH,bsHEIGHT);

      bs_X_POS = bs_X_FRAMES-1;           /// WARNING: now  OFS_X  and  OFS_Y  actually longer by 1 !!!
      bs_Y_POS = bs_Y_FRAMES-1;           ///          (in oth.w. we should _decrease_ some values on 1)
      bs_X_LAST_FRAME = client_sb.bulgar.X();
      bs_Y_LAST_FRAME = client_sb.bulgar.Y();
      LOC_X = center_x - OFS_X*CELL_SIZE;//-CELL_SIZE>>2;
      LOC_Y = center_y - OFS_Y*CELL_SIZE;//-CELL_SIZE>>2;


      int from_x = bs_X_LAST_FRAME-OFS_X, from_y = bs_Y_LAST_FRAME-OFS_Y;
      int to_x=from_x+bs_X_FRAMES, to_y=from_y+bs_Y_FRAMES;
      int _y = -CELL_SIZE,  _x;


            for (int y = from_y; y < to_y; y++)
	    {
	        _x = -CELL_SIZE;
		_y+=CELL_SIZE;
                for (int x = from_x; x < to_x; x++)
                {
		        _x+=CELL_SIZE;
			drawCell((x==to_x-1?from_x-1:x),(y==to_y-1?from_y-1:y),_x,_y);
//			font.drawDigits(gBackScreen,_x,_y,2,_y/*y*10+x*/);
                }
	    }
    }

    private void repaintHorizontalLine(int index,int bs_position){
       int bsx = bs_X_POS, px = bs_X_LAST_FRAME-OFS_X-1,
           dx = bsx * CELL_SIZE,dy = (bs_position) * CELL_SIZE;
	   gBackScreen.setColor(FLOOR_COLOR);
	   gBackScreen.fillRect(0,dy,bsWIDTH,CELL_SIZE);

	   for (int i = 0 ; i <bs_X_FRAMES ; i++){
	     drawCell(px,index,dx,dy);
	     bsx++; dx+=CELL_SIZE; px++;
	     if(bsx>=bs_X_FRAMES){
	       bsx-=bs_X_FRAMES;
	       dx-=bsWIDTH;
	     }
	   }
    }

    private void repaintVerticalLine(int index,int bs_position){
       int bsy = bs_Y_POS, py = bs_Y_LAST_FRAME-OFS_Y-1,
           dy = bsy * CELL_SIZE,dx = (bs_position) * CELL_SIZE;
	   gBackScreen.setColor(FLOOR_COLOR);
	   gBackScreen.fillRect(dx,0,CELL_SIZE,bsHEIGHT);

	   for (int i = 0 ; i <bs_Y_FRAMES ; i++){
	     drawCell(index,py,dx,dy);
	     bsy++; dy+=CELL_SIZE; py++;
	     if(bsy>=bs_Y_FRAMES){
	       bsy-=bs_Y_FRAMES;
	       dy-=bsHEIGHT;
	     }
	   }
    }

    private void updateBackScreen(){

        int bx = client_sb.bulgar.X();
        int by = client_sb.bulgar.Y();

	if(by!=bs_Y_LAST_FRAME){
	  int dy = by-bs_Y_LAST_FRAME;
	  if(dy>0){
	   for (int i = 0 ; i <dy ; i++) {
	     repaintHorizontalLine(bs_Y_LAST_FRAME - OFS_Y + bs_Y_FRAMES-1,bs_Y_POS);
	     if(++bs_Y_POS >= bs_Y_FRAMES) bs_Y_POS -= bs_Y_FRAMES;
	     bs_Y_LAST_FRAME++;
	   }
	  } else {
	   for (int i = 0 ; i >dy ; i--) {
	     if(--bs_Y_POS < 0) bs_Y_POS += bs_Y_FRAMES;
	     bs_Y_LAST_FRAME--;
	     repaintHorizontalLine(bs_Y_LAST_FRAME - OFS_Y -1 ,bs_Y_POS);
	   }
	  }
	  LOC_Y -= dy*CELL_SIZE;
	  if(LOC_Y>=bsHEIGHT)LOC_Y-=bsHEIGHT;
	    else
	      if(LOC_Y<0)LOC_Y+=bsHEIGHT;
	} else {
	  if(LOC_Y-client_sb.tmpY>=bsHEIGHT)LOC_Y-=bsHEIGHT;
	    else
	      if(LOC_Y-client_sb.tmpY<0)LOC_Y+=bsHEIGHT;
	}

	if(bx!=bs_X_LAST_FRAME){
	  int dx = bx-bs_X_LAST_FRAME;
	  if(dx>0){
	   for (int i = 0 ; i <dx ; i++) {
	     repaintVerticalLine(bs_X_LAST_FRAME - OFS_X + bs_X_FRAMES-1,bs_X_POS);
	     if(++bs_X_POS >= bs_X_FRAMES) bs_X_POS -= bs_X_FRAMES;
	     bs_X_LAST_FRAME++;
	   }
	  } else {
	   for (int i = 0 ; i >dx ; i--) {
	     if(--bs_X_POS < 0) bs_X_POS += bs_X_FRAMES;
	     bs_X_LAST_FRAME--;
	     repaintVerticalLine(bs_X_LAST_FRAME - OFS_X -1 ,bs_X_POS);
	   }
	  }
	  LOC_X -= dx*CELL_SIZE;
	  if(LOC_X>=bsWIDTH)LOC_X-=bsWIDTH;
	    else
	      if(LOC_X<0)LOC_X+=bsWIDTH;
	} else {
	  if(LOC_X-client_sb.tmpX>=bsWIDTH)LOC_X-=bsWIDTH;
	    else
	      if(LOC_X-client_sb.tmpX<0)LOC_X+=bsWIDTH;
	}
    }

    protected void DoubleBuffer(Graphics gBuffer){

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

        Image img = null;
/**********************/
//        gBuffer.setColor(0x737373);gBuffer.fillRect(0,0,VisWidth,VisHeight);
	gBuffer.setColor(0x0);
	Bulgar.Guard guards;
	GameObject bulgar = client_sb.bulgar;
	int tx = client_sb.tmpX, ty = client_sb.tmpY;
	int i = LOC_X-tx, j = LOC_Y-ty;
        int bx = bulgar.X();
        int by = bulgar.Y();
	int cadr;

        // Draw dungeon

	  gBuffer.drawImage(BackScreen,i,j,0);
	  gBuffer.drawImage(BackScreen,i,j-bsHEIGHT,0);
	  gBuffer.drawImage(BackScreen,i-bsWIDTH,j,0);
	  gBuffer.drawImage(BackScreen,i-bsWIDTH,j-bsHEIGHT,0);

          // Draw gems and shockers
          int from_x = bx-OFS_X, from_y = by-OFS_Y;
          int to_x=bx+OFS_X+2, to_y=by+OFS_Y+2;
	  int basex = start_x - tx, _x;
          int    _y = start_y - ty;
	  int []lvl_x = client_sb.level[0];

	  if(to_y>client_sb.level.length) to_y=client_sb.level.length-1;
	  if(from_y<0) {_y+=(-from_y)*CELL_SIZE;from_y=0;}
	  if(to_x>lvl_x.length) to_x=lvl_x.length-1;
	  if(from_x<0) {basex+=(-from_x)*CELL_SIZE;from_x=0;}

            for (int y = from_y; y < to_y; y++)
	    {
	        _x = basex;
		_y+=CELL_SIZE;
		lvl_x = client_sb.level[y];
                for (int x = from_x; x < to_x; x++)
                {
		        _x+=CELL_SIZE;
//			if(y>=0 && y<client_sb.level.length && x>=0 && x<lvl_x.length)
			{
			        i = lvl_x[x];
                                if(i == 150) i = IMG_SIGN_H01+((ticks>>1)&1);
                                else
				  if (i >= 110 && i <= 127 && (ticks&2)==0) {
				    i=i%5;
				    if(i==2) //gem01,gem02
				        i = IMG_GEM02;
				    else
				    if(i==4) //emerald,emerald02,
					i = IMG_EMERALD02;
				    else continue;
                                } else continue;

			        img = GetImage(i);
	                        gBuffer.drawImage(img,
					_x+((CELL_SIZE - img.getWidth())>>1),
				        _y+((CELL_SIZE - img.getHeight())>>1),0);
			}
                }
	    }

//System.out.println(client_sb.tmpX+","+client_sb.tmpY);

        // Guardians

        for (i = 0; i < client_sb.guards.length; i++)
        {
            guards = client_sb.guards[i];
            int gx = guards.X() - bx;
            int gy = guards.Y() - by;

	    if(Math.abs(gx)>OFS_X+1 || Math.abs(gy)>OFS_Y+1) continue;
//	   if(!(gx>OFS_X || gx<-OFS_X || gy>OFS_Y || gy<-OFS_Y)){

            cadr = 0;
            int visx = guards.aX;
            int visy = guards.aY;
            if (visx != 0) cadr = (visx/*>>0*/) & 1; // 2 frames (only for MONOCHROME version)
            if (visy != 0) cadr = (visy/*>>0*/) & 1;


            visx += center_x + gx * CELL_SIZE - tx;
            visy += center_y + gy * CELL_SIZE - ty;


	    int image = guards.getDirection();
	    if(image == guards.DIR_STOP)
	        image = guards.vDir;

            image = ((image-1)&3)<<1;
	    image += (guards.ID%3)<<3; // 2 frame per every of 4 directions
	    image += IMG_GUARD_L01;

//            if (visx >= -CELL_SIZE && visx <= VisWidth+CELL_SIZE && visy >= -CELL_SIZE && visy < VisHeight+CELL_SIZE)
//            {
		gBuffer.drawImage(GetImage(image + cadr), visx, visy ,0);
//            }
//	   }
        }


        // Key rect
	cadr = 3;
	//optimized for 3 keys
	// >>>>
	    i = CELL_SIZE+CELL_SIZE+CELL_SIZE;
            gBuffer.setColor(0x9494c4);  gBuffer.fillRect(3, cadr, CELL_SIZE, i);
            gBuffer.setColor(0x3b3b7d);  gBuffer.drawRect(2, cadr, CELL_SIZE, i);
					 gBuffer.drawRect(2, cadr+CELL_SIZE, CELL_SIZE, CELL_SIZE);
        // <<<<
        for (int y = 0; y < client_sb.keys.length; y++)
        {
//            gBuffer.setColor(0x9494c4);  gBuffer.fillRect(3, cadr, CELL_SIZE, CELL_SIZE);
//            gBuffer.setColor(0x3b3b7d);  gBuffer.drawRect(2, cadr, CELL_SIZE, CELL_SIZE);
            if (client_sb.keys[y] != 0)
            {
	        img = GetImage(IMG_KEY + client_sb.keys[y]%4);
		gBuffer.drawImage(img, 3+((CELL_SIZE-img.getWidth())>>1), cadr + ((CELL_SIZE-img.getHeight())>>1),0);
            }
	   cadr += CELL_SIZE;
        }

        // Gold
        img = GetImage(IMG_BAG);
        gBuffer.drawImage(img,2,VisHeight - 2 -img.getHeight(),0);
	font.drawDigits(gBuffer,3+img.getWidth(), VisHeight - 2 -((img.getHeight() + font.fontHeight)>>1),3,client_sb.gold);

      if(gameStatus!=Crashed || blink){
        // Lives
        img = GetImage(IMG_HEART);
	   for (i=0;i<client_sb.lives-1;i++)
              gBuffer.drawImage(img,/*baseX+*/VisWidth-(i+1)*(img.getWidth()+2)-10,VisHeight-img.getHeight(),0);


        // Draw bulgar
        cadr = 0; // 4 animation cadres
        if (tx != 0) cadr = (tx>>1) &3; //4 frames
        if (ty != 0) cadr = (ty>>1) &3;
	int image = bulgar.getDirection();
	if(image == bulgar.DIR_STOP)
	      image = bulgar.vDir;
	image = (((image-1)&3)<<2) + IMG_PL_L01;
	gBuffer.drawImage(GetImage(image +cadr), center_x, center_y ,0);
      }


/*********************/

	if (gameStatus == demoPlay && blink) {
	   img = GetImage(IMG_DEMO);
	   gBuffer.drawImage(img, scr2,VisHeight-VisHeight/3/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

////////////////////////////////////////////////
      if(baseX !=0 || baseY !=0){
        gBuffer.translate(-baseX,-baseY);
        gBuffer.setClip(0,0,scrwidth,scrheight);
      }

	bufferChanged=false;
    }

boolean is_painting = false;
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */
    protected void paint(Graphics g) {
      is_painting = true;
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
				g.drawString(YourScore+client_sb.getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
//		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage(GetImage(gameStatus==gameOver?IMG_LOST:IMG_WIN),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			    }
			    g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    break;

	      //case demoPlay:
              default :   if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			      }
			      else
			      {
			        //if(bufferChanged)
		                DoubleBuffer(gBuffer);
		                g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
                              g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
	  is_painting = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 1;
       direct=game_PMR.BUTTON_NONE;
       CurrentLevel=client_sb.i_GameLevel;
//       stageSleepTime[playGame] = client_sb.i_TimeDelay;
       stage = client_sb.i_GameStage;
       client_pmr.i_Value = direct;
//       client_sb.nextGameStep(client_pmr);
       repaintBackScreen();
       gameStatus=newStage;
       bufferChanged=true;
       repaint();
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

private static final byte IMG_FP = 0;
private static final byte IMG_LOST = 1;
private static final byte IMG_WIN = 2;
private static final byte IMG_DEMO = 3;
private static final byte IMG_PL_L01 = 4;
private static final byte IMG_PL_L02 = 5;
private static final byte IMG_PL_L03 = 6;
private static final byte IMG_PL_L04 = 7;
private static final byte IMG_PL_R01 = 8;
private static final byte IMG_PL_R02 = 9;
private static final byte IMG_PL_R03 = 10;
private static final byte IMG_PL_R04 = 11;
private static final byte IMG_PL_D01 = 16;
private static final byte IMG_PL_D02 = 17;
private static final byte IMG_PL_D03 = 18;
private static final byte IMG_PL_D04 = 19;
private static final byte IMG_GUARD_L01 = 20;
private static final byte IMG_GUARD_L02 = 21;
private static final byte IMG_GUARD_R01 = 22;
private static final byte IMG_GUARD_R02 = 23;
private static final byte IMG_GUARD_U01 = 24;
private static final byte IMG_GUARD_U02 = 25;
private static final byte IMG_GUARD_D01 = 26;
private static final byte IMG_GUARD_D02 = 27;
private static final byte IMG_GUARD02_L01 = 28;
private static final byte IMG_GUARD02_L02 = 29;
private static final byte IMG_GUARD02_R01 = 30;
private static final byte IMG_GUARD02_R02 = 31;
private static final byte IMG_GUARD02_U01 = 32;
private static final byte IMG_GUARD02_U02 = 33;
private static final byte IMG_GUARD02_D01 = 34;
private static final byte IMG_GUARD02_D02 = 35;
private static final byte IMG_GUARD04_01 = 36;
private static final byte IMG_GUARD04_02 = 37;
private static final byte IMG_WALL01 = 44;
private static final byte IMG_WALL02 = 45;
private static final byte IMG_WALL03 = 46;
private static final byte IMG_WALL04 = 47;
private static final byte IMG_WALL05 = 48;
private static final byte IMG_WALL06 = 49;
private static final byte IMG_DOOR_V01 = 50;
private static final byte IMG_DOOR_V02 = 51;
private static final byte IMG_DOOR_H01 = 52;
private static final byte IMG_DOOR_H02 = 53;
private static final byte IMG_SIGN_H_OFF = 54;
private static final byte IMG_SIGN_H01 = 55;
private static final byte IMG_SIGN_H02 = 56;
private static final byte IMG_KEY = 57;
private static final byte IMG_KEY2 = 58;
private static final byte IMG_KEY3 = 59;
private static final byte IMG_CARD = 60;
private static final byte IMG_HEART = 61;
private static final byte IMG_LEVER01 = 62;
private static final byte IMG_LEVER02 = 63;
private static final byte IMG_MONEY = 64;
private static final byte IMG_RING = 65;
private static final byte IMG_GEM01 = 66;
private static final byte IMG_GEM02 = 67;
private static final byte IMG_CROWN = 68;
private static final byte IMG_EMERALD = 69;
private static final byte IMG_EMERALD02 = 70;
private static final byte IMG_BAG = 71;
private static final byte IMG_EXIT02 = 72;
private static final byte IMG_NFONT = 73;
private static final byte IMG_PL_U01 = 12;
private static final byte IMG_PL_U02 = 13;
private static final byte IMG_PL_U03 = 14;
private static final byte IMG_PL_U04 = 15;
private static final byte IMG_GUARD04_1 = 38;
private static final byte IMG_GUARD04_2 = 39;
private static final byte IMG_GUARD04_3 = 40;
private static final byte IMG_GUARD04_4 = 41;
private static final byte IMG_GUARD04_5 = 42;
private static final byte IMG_GUARD04_6 = 43;
private static final byte TOTAL_IMAGES_NUMBER = 74;
}

