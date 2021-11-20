package com;

import javax.microedition.lcdui.*;
import com.igormaznitsa.game_kit_E5D3F2.Zynaps.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.midp.*;
//import com.igormaznitsa.midp.Utils.drawDigits;
import com.igormaznitsa.Utils.WDecoder;
import com.nokia.mid.ui.*;


public class GameArea extends com.nokia.mid.ui.FullCanvas implements Runnable{

  private final static int MENU_KEY = -7;//KEY_MENU;
  private final static int END_KEY = -10;//KEY_BACK;
    public void setScreenLight(boolean state)
    {
      com.nokia.mid.ui.DeviceControl.setLights (0, state?100:0);
    }

  private final static int ANTI_CLICK_DELAY = 5; //x200ms = 1 seconds , game over / finished sleep time
  private final static int FINAL_PICTURE_OBSERVING_TIME = 50; //x200ms = 10 seconds , game over / finished sleep time
  private final static int TOTAL_OBSERVING_TIME = 75; //x200ms = 15 seconds , game over / finished sleep time
  private final static int DEMO_APPEARING_TIME = 60*10;//x100ms = 120 sec

  private int V_WIDTH = 128;
  private int V_QUEUE = 3;

  private startup midlet;
  private Display display;
  private int scrwidth,scrheight,scrcolors;
  private boolean colorness;
  public Thread thread=null;
  private boolean isShown = false;


  private byte [][] Elements;
  private Image []Ele;
  private final static int bk1_V = 0x200;
  private int bk1_offset;
  private final static  int bk2_V = 0x080;
  private int bk2_offset;

//  private Image iBlank = Image.createImage(1,1);
//  private Image MenuIcon = null;

//  private Image Buffer=null;
//  private Graphics gBuffer=null;
//  private Image BackScreen=null;
//  private Graphics gBackScreen=null;

  public String loadString  = "Loading...";
  public String unpacking  = "unpacking..";
  public String YourScore  = null;
  public String NewStage  = null;

//  private drawDigits font;

  private int CurrentLevel=0;
  private int stage = 0;
  protected Zynaps_PMR client_pmr = null;
  protected int direct=Zynaps_PMR.BUTTON_NONE;
  protected Zynaps_SB client_sb = null;
  protected int baseX=0, baseY=0, cellW=0,cellH=0, bsCELLs, bsOFFS=1;
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
  private int [] stageSleepTime = {100,100,100,200,100,100,200,200}; // in ms

  public int gameStatus=notInitialized;
  private int demoPreviewDelay = 1000; // ticks count
  private int demoLevel = 0;
  private int demoLimit = 0;
  private boolean bufferChanged=true;
  private boolean justStarted=true;
  int blinks=0, ticks=0;


int BkgCOLOR = 0x000061;
boolean warn = false;

  /**Construct the displayable*/
  public GameArea(startup m) {
    midlet = m;
    display = Display.getDisplay(m);
    scrheight = this.getHeight();
    scrwidth = this.getWidth();
    scrcolors = display.numColors();
    colorness = display.isColor();
    V_WIDTH = Math.min(128,scrwidth);
    V_QUEUE = (V_WIDTH+Stages.BLOCKLENGTH*2-1)/(Stages.BLOCKLENGTH*2)+1;
//    client_sb = new Zynaps_SB(101,64,null);
//    client_pmr = new Zynaps_PMR();

//    Buffer=Image.createImage(scrwidth,scrheight);
//    gBuffer=Buffer.getGraphics();

    gameStatus=notInitialized;
    baseX = (scrwidth - V_WIDTH)>>1;
    baseY = (scrheight - 64)>>1;
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
     loading(67+TOTAL_IMAGES_NUMBER);

     try {

	   Runtime.getRuntime().gc();
	   ImageBlock ib = new ImageBlock("/res/images.bin",this);
 	   Ele = ib._image_array;
	   Elements = ib._byte_array;
//	   MenuIcon = getMenuIcon();
	   ib = null;

    client_sb = new Zynaps_SB(V_WIDTH,64,null);
    client_pmr = new Zynaps_PMR();



     } catch(Exception e) {
//       System.out.println("Can't read images");
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
       CleanSpace();
       stage = 0;
       blink=false;
       ticks = 0;
       direct=Zynaps_PMR.BUTTON_NONE;
       client_pmr.i_Value = direct;
       CurrentLevel=level;
       client_sb.newGameSession(level);
	if (gameStatus==titleStatus) {
	    //client_sb.initStage(2);
            client_sb.nextGameStep(client_pmr);
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
//	    drawBackScreen();
	} else {
           stageSleepTime[playGame] = ((Zynaps_GSB)client_sb.getGameStateBlock()).i_TimeDelay;
//	   lastPlayerScores = 0;
	   stage = 0;
	   gameStatus=playGame;
	}
	bufferChanged=true;

//	bk1_offset=(scrheight-Ele[IMG_BCKG0].getHeight())<<8;
//	bk2_offset=(scrheight-Ele[IMG_BCKG1].getHeight())<<8;
	bk1_offset=(scrwidth-Ele[IMG_BCKG0].getWidth())<<8;
	bk2_offset=(scrwidth-Ele[IMG_BCKG1].getWidth())<<8;

//((Zynaps_GSB)client_sb.getGameStateBlock()).i8_backgroundway = ((Stages.ai_wayarray.length-2)*Stages.BLOCKLENGTH*2)<<8;

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
       if(gameStatus==titleStatus){
		      ticks = 0;
		      warn = false;
       }
    }

    public void hideNotify(){
       isShown = false;
    }

    protected void keyPressed(int keyCode) {
            switch (gameStatus) {
	      case notInitialized: if (keyCode==END_KEY) midlet.killApp();
	                           break;
	      case Finished:
	      case gameOver: if(warn)return;
	                     if (ticks<TOTAL_OBSERVING_TIME) if (ticks>ANTI_CLICK_DELAY && ticks<FINAL_PICTURE_OBSERVING_TIME)
	                                    ticks=FINAL_PICTURE_OBSERVING_TIME+1;

	      case demoPlay:
              case titleStatus :if (ticks>ANTI_CLICK_DELAY)
	                        {
		                    switch (keyCode) {
				      case MENU_KEY:
				                midlet.ShowMainMenu();
				                break;
				      case END_KEY:
				                midlet.ShowQuitMenu(false);
				                break;
				      default:
				              if (gameStatus == demoPlay/* || ticks>=TOTAL_OBSERVING_TIME*/){
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
			              case MENU_KEY: CleanSpace(); midlet.ShowGameMenu(); break;
				      case END_KEY: midlet.ShowQuitMenu(true); break;
				      case Canvas.KEY_NUM2: direct=Zynaps_PMR.BUTTON_UP; break;
				      case Canvas.KEY_NUM8: direct=Zynaps_PMR.BUTTON_DOWN; break;
				      case Canvas.KEY_NUM4: direct=Zynaps_PMR.BUTTON_LEFT; break;
				      case Canvas.KEY_NUM6: direct=Zynaps_PMR.BUTTON_RIGHT; break;
				      case Canvas.KEY_NUM5: direct=Zynaps_PMR.BUTTON_FIRE; break;
				      default:
				          switch(action){
					    case Canvas.UP: direct=Zynaps_PMR.BUTTON_UP; break;
					    case Canvas.DOWN: direct=Zynaps_PMR.BUTTON_DOWN; break;
					    case Canvas.LEFT: direct=Zynaps_PMR.BUTTON_LEFT; break;
					    case Canvas.RIGHT: direct=Zynaps_PMR.BUTTON_RIGHT; break;
					    case Canvas.FIRE: direct=Zynaps_PMR.BUTTON_FIRE; break;
					    default:
					             direct=Zynaps_PMR.BUTTON_NONE;
				          }
	                          }
              }
            }
    }

    protected void keyReleased(int keyCode) {
        if (gameStatus==playGame) {
	  direct=Zynaps_PMR.BUTTON_NONE;
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
	                if(ticks==0)serviceRepaints();
	                if(ticks==1){
			  CleanSpace();
			  warn = true;
			  repaint();
			  serviceRepaints();
	                }/* else
			   if(ticks==1){warn=false; repaint();}
			   */
			if (ticks<DEMO_APPEARING_TIME) ticks++;
			 else
			  {
			   Runtime.getRuntime().gc();
			   ticks=0;
			   init(demoLevel,null);
			  }
			break;
	   case demoPlay: {
			   ticks++; blink=(ticks&8)==0;
		           client_sb.nextGameStep(client_pmr);
			   Zynaps_GSB loc=(Zynaps_GSB)client_sb.getGameStateBlock();
			   if (loc.getPlayerState()!=Zynaps_GSB.PLAYERSTATE_NORMAL || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       blink=false;
			   }
			   repaint();
			  } break;
	   case newStage:
/*                          {
	                      if(ticks++==0) {
		                direct=Zynaps_PMR.BUTTON_NONE;
			        client_pmr.i_Value = direct;
			        client_sb.initStage(stage);
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
*/
           case playGame :{
			   ticks++;
			   client_pmr.i_Value = direct;
			   client_sb.nextGameStep(client_pmr);
			   //UpdateBackScreen();
			   Zynaps_GSB loc=(Zynaps_GSB)client_sb.getGameStateBlock();

			   /*
			   if (loc.getGameState()==Zynaps_GSB.GAMESTATE_OVER) {
			       switch (loc.getPlayerState()) {
				      case Zynaps_GSB.PLAYERSTATE_LOST: ticks = 0;gameStatus=Crashed; break;
			              case Zynaps_GSB.PLAYERSTATE_WON: ticks = 0;gameStatus=Finished;  break;
			       }
			   } else */
			         switch (loc.getPlayerState()) {
					case Zynaps_GSB.PLAYERSTATE_LOST: ticks =0; gameStatus=Crashed; break;
			                case Zynaps_GSB.PLAYERSTATE_WON: ticks = 0;
//								       stage++;
//					                               if (stage>=Stages.TOTAL_STAGES)
                                                                         CleanSpace();
								         gameStatus=Finished;
									 warn = false;
//								       else
//					                                 gameStatus=newStage;
								      break;

			         }
			   repaint();
			  } break;
	    case Crashed:

			   ticks++; blink=(ticks&2)==0;
			   //if(ticks>2){
			     ticks = 0;
			     blink = false;
		             direct=Zynaps_PMR.BUTTON_NONE;
			     if (((Zynaps_GSB)client_sb.getGameStateBlock()).getGameState()==Zynaps_GSB.GAMESTATE_OVER){
			            CleanSpace();
			            gameStatus=gameOver;
				    warn = false;
			     }
			      else {
				   CleanSpace();
			           client_sb.resumeGameAfterPlayerLost();
			           gameStatus=playGame;
			      }
			   //}
			   repaint();
			   break;
	    case gameOver:
	    case Finished:
//	                if(ticks==0)serviceRepaints();
                       if(!warn){
	                if(ticks==1){
			  warn = true;
			  repaint();
			  serviceRepaints();
			  System.out.println();
//			  ticks++;
	                } /*else
			   if(ticks==1){warn=false; repaint();serviceRepaints();}
			   */
//			  else {

	                   if(ticks++>TOTAL_OBSERVING_TIME){
			     ticks = 0;
			     gameStatus=titleStatus;
			     repaint();
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME){
//			      ticks=TOTAL_OBSERVING_TIME+1;
			      midlet.newScore(((GameStateBlock)client_sb.getGameStateBlock()).getPlayerScore());
			      repaint();
			    }
                       }
//			  }
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


    private final static int BAR_WIDTH = 20;
    private final static int BAR_HEIGHT = 6;

    protected void BAR(Graphics gBuffer, int x, int y, int max, int current, int color){

	int dx = Math.max(current*(BAR_WIDTH-3)/max,0);
      if(scrheight<=80){
        gBuffer.setColor(0x0);
	gBuffer.fillRect(x,y,BAR_WIDTH,BAR_HEIGHT);
      }

        gBuffer.setColor(color);
	gBuffer.drawRect(x,y,BAR_WIDTH,BAR_HEIGHT);
	gBuffer.fillRect(x+2,y+2,dx,BAR_HEIGHT-3);
    }

    protected void DoubleBuffer(Graphics gBuffer){

//      if (bufferChanged) {

        gBuffer.setColor(BkgCOLOR);
        gBuffer.fillRect(0, 0, scrwidth, scrheight);

	gBuffer.translate(baseX,baseY);
	gBuffer.setClip(0,0,V_WIDTH,64);

        Image img = null;
        Zynaps_GSB loc=(Zynaps_GSB)client_sb.getGameStateBlock();



        bk1_offset -= bk1_V;
	bk2_offset -= bk2_V;
	if (bk1_offset<0) bk1_offset+=((Ele[IMG_BCKG0].getWidth())<<8);
	if (bk2_offset<0) bk2_offset+=((Ele[IMG_BCKG1].getWidth())<<8);

	gBuffer.drawImage(Ele[IMG_BCKG0],bk1_offset>>8,0,0);
	gBuffer.drawImage(Ele[IMG_BCKG0],(bk1_offset>>8)-Ele[IMG_BCKG0].getWidth(),0,0);

	gBuffer.drawImage(Ele[IMG_BCKG1],bk2_offset>>8,5,0);
	gBuffer.drawImage(Ele[IMG_BCKG1],(bk2_offset>>8)-Ele[IMG_BCKG1].getWidth(),5,0);


        int block = (loc.i8_backgroundway>>9)/Stages.BLOCKLENGTH;
	int offset = (- (loc.i8_backgroundway>>8)%(Stages.BLOCKLENGTH<<1));
//	int offset = (- (loc.i8_backgroundway>>9)%(Stages.BLOCKLENGTH))<<1;
//	offset += (2-((loc.i8_backgroundway>>8)&1));

//	System.out.println(offset+", "+(loc.i8_backgroundway));
//	offset +=2-((loc.i8_backgroundway>>8)&1);

        int n ;
	int prev = 0;
        n = block;
        if(block>0 && block<Stages.ai_wayarray.length && (prev=Stages.ai_wayarray[block-1])>0 && Ele[IMG_BLOCK0+prev-1]!=null){
	  for (int i = 0;i<=V_QUEUE;i++){
	    if(n<Stages.ai_wayarray.length && Stages.ai_wayarray[n]>0 &&
	       Stages.ai_wayarray[n]==prev){prev=0;break;}
	    n++;
	  }
	 if (prev>0){
	   Ele[IMG_BLOCK0+prev-1]=null;
//	   System.out.println("rm:"+(IMG_BLOCK0+prev-1)+" /"+prev+" "+Ele[IMG_BLOCK0+prev-1]);
	 }
        }


	for (int i = 0;i<V_QUEUE;i++){
	   if(block<Stages.ai_wayarray.length && Stages.ai_wayarray[block]>0)
	   {
	    n = IMG_BLOCK0+Stages.ai_wayarray[block]-1;
	    try{
	       if(Ele[n]==null){
//		 System.out.println("app:"+n);
//		 int kk = 0;
//		 for(int m=40;m<50;m++) if(Ele[m]!=null)kk++;

                 Runtime.getRuntime().gc();
//		 System.out.println("app:"+n+", tot've:"+kk+", Mem:"+Runtime.getRuntime().freeMemory());
//		 if(Runtime.getRuntime().freeMemory()<40000);
                 try{
                    Ele[n] = Image.createImage(Elements[n-37],0,Elements[n-37].length);
                 }catch(OutOfMemoryError e){
//		              e.printStackTrace();
                              CleanSpace();
                              try{
                                  Ele[n] = Image.createImage(Elements[n-37],0,Elements[n-37].length);
                              }catch(OutOfMemoryError ex){}
                 }
	       }if(Ele[n]!=null)gBuffer.drawImage(Ele[n],offset,0,0);
	    }catch(Exception e){}
	   }
	    offset+=(Stages.BLOCKLENGTH<<1);
	    block++;
	}


/*
        int n ;
	int prev = 0;
	for (int i = 0;i<V_QUEUE;i++){
	   if(block<Stages.ai_wayarray.length && Stages.ai_wayarray[block]>0){
	    n = IMG_BLOCK0+Stages.ai_wayarray[block]-1;
	    if(n==prev)prev=0;
	    if(i==0) prev = n;
	    try{
	      if(Ele[n]==null)Ele[n] = Image.createImage(Elements[n-37],0,Elements[n-37].length);
	      gBuffer.drawImage(getImage(IMG_BLOCK0+Stages.ai_wayarray[block]-1),offset,0,0);
	    }catch(Exception e){CleanSpace();}
	   }
	    offset+=(Stages.BLOCKLENGTH<<1);
	    block++;
	}
	if (prev!=0 && Ele[prev]!=null){Ele[prev]=null;Runtime.getRuntime().gc();}
*/

/*
	for (int i = 0;i<3;i++){
	   if(block<Stages.ai_wayarray.length && Stages.ai_wayarray[block]>0)
	      gBuffer.drawImage(getImage(IMG_BLOCK0+Stages.ai_wayarray[block]-1),offset,0,0);
	    offset+=(Stages.BLOCKLENGTH<<1);
	    block++;
	}
*/
	boolean bossIsHere = false;

        // Drawing of the moving objects
        MovingObject [] ap_moa = loc.ap_MovingObjects;
	int subtype;
        for(int li=0;li<ap_moa.length;li++)
        {
	    subtype=-1;
            MovingObject p_obj = ap_moa[li];
            if (p_obj.lg_Active)
            {
                switch(p_obj.i_Type)
                {
                    case MovingObject.TYPE_BOSS :       subtype = IMG_BOSS0+p_obj.i_Frame; bossIsHere = true; break;
                    case MovingObject.TYPE_BOSSBULLET : subtype = IMG_BOSSBULLET0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_ENEMY0 :     subtype = IMG_ENEMY0/*+p_obj.i_Frame*/;break;
                    case MovingObject.TYPE_ENEMY1 :     subtype = IMG_ENEMY1_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_ENEMY2 :     subtype = IMG_ENEMY2/*+p_obj.i_Frame*/;break;
                    case MovingObject.TYPE_ENEMY3 :     subtype = IMG_ENEMY3_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_ENEMY4 :     subtype = IMG_ENEMY4_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_ENEMYBULLET :subtype = IMG_ENEMYBULLET/*+p_obj.i_Frame*/;break;
                    case MovingObject.TYPE_EXPLOSION :  subtype = IMG_EXPL0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_WEAPON0 :    subtype = IMG_WEAPON0_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_WEAPON1 :    subtype = IMG_WEAPON1_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_WEAPON2 :    subtype = IMG_WEAPON2_0+p_obj.i_Frame;break;
                    case MovingObject.TYPE_WEAPON3 :    subtype = IMG_WEAPON3_0+p_obj.i_Frame;break;
                }

                if(subtype>=0)gBuffer.drawImage(Ele[subtype],p_obj.getScrX(),p_obj.getScrY(),0);
            }
        }

        // Drawing of the player
       if(gameStatus!=Crashed){
         Player p_player = loc.p_Player;
         if(loc.i_NonDestroyableDelay<=0 || (ticks&2)==0)
	 gBuffer.drawImage(Ele[(p_player.lg_Destroyed?IMG_PEXPL0+p_player.i_Frame:IMG_PLAYER)],p_player.getScrX(),p_player.getScrY(),0);
       }


        // Drawing of the player's bullets
        ap_moa = loc.ap_PlayerBullets;
        for(int li=0;li<ap_moa.length;li++)
        {
            MovingObject p_obj = ap_moa[li];
            if (p_obj.lg_Active)
            {
	       switch(p_obj.i_Type){
                 case MovingObject.TYPE_BULLET1: subtype = IMG_BULLET1; break;
                 default: subtype = IMG_BULLET2; break;
	       }
	       gBuffer.drawImage(getImage(subtype),p_obj.getScrX(),p_obj.getScrY(),0);
            }
        }

        // Drawing of shields
        if (loc.p_PlayerShield.lg_Active)
	    gBuffer.drawImage(getImage(IMG_SHIELD0 + loc.p_PlayerShield.i_Frame),loc.p_PlayerShield.getScrX(),loc.p_PlayerShield.getScrY(),0);

        if (loc.p_PlayerShortWeapon.lg_Active)
	    gBuffer.drawImage(getImage(IMG_ESHIELD0 + loc.p_PlayerShortWeapon.i_Frame),loc.p_PlayerShortWeapon.getScrX(),loc.p_PlayerShortWeapon.getScrY(),0);



	gBuffer.translate(-baseX,-baseY);
	gBuffer.setClip(0,0,scrwidth,scrheight);

	int w = 3, h = 3;
	int hh =h + ((Ele[IMG_HEART].getHeight()-BAR_HEIGHT)>>1);
	int ww =w + Ele[IMG_HEART].getWidth()+3;

//	if(scrwidth>101)w = Ele[IMG_HEART].getWidth();
//	if(scrwidth>64)h = Ele[IMG_HEART].getHeight();
	  gBuffer.drawImage(Ele[IMG_HEART],w,h,0);
	  BAR(gBuffer,ww,hh, Zynaps_SB.PLAYER_POWER,loc.i_playerpower,0xff0000);
	  if(bossIsHere) {
	     gBuffer.drawImage(Ele[IMG_BOSS_ICO],scrwidth - Ele[IMG_HEART].getWidth() - w,h,0);
	     BAR(gBuffer,scrwidth - BAR_WIDTH-ww,hh,Zynaps_SB.BOSS_POWER,loc.i_bosspower, 0x0cfafa);
	  }
//        g.setColor(Color.black);
//        g.drawString(""+p_gsb.i_waycounter+" "+p_gsb.i_playerpower+" "+p_gsb.i_bosspower,0,20);



//	int BarOffset = p_player.getScrY()+20/*(iPlayer[0].getHeight()<<1)*/-scrheight;
//	if (BarOffset<0)BarOffset=0;// BarOffset = iPlayer[0].getHeight()-2;

        if(!(gameStatus == Crashed && blink)) {
//           gBuffer.drawImage(Elements[IMG_BOMB_0],/*baseX+*/0,/*baseY+*/scrheight-Elements[IMG_BOMB_0].getHeight()-2+BarOffset,0);
//	   font.drawDigits(gBuffer,Elements[IMG_BOMB_0].getWidth(),scrheight-((Elements[IMG_BOMB_0].getHeight()+font.fontHeight)>>1)-2+BarOffset,2,loc.i_bombs);
	   for (int i=0;i<loc.i_Attemptions-1;i++)
//              gBuffer.drawImage(Elements[IMG_HEART],/*baseX+*/scrwidth-(i+1)*(Elements[IMG_HEART].getWidth()+2)-10,/*baseY+*/scrheight-Elements[IMG_HEART].getHeight()-2+BarOffset,0);
              gBuffer.drawImage(Ele[IMG_HEART],/*baseX+*/i*(Ele[IMG_HEART].getWidth()+2)+w,/*baseY+*/scrheight-Ele[IMG_HEART].getHeight()-2,0);
        }

	if (gameStatus == demoPlay && blink) {
	   gBuffer.drawImage(getImage(IMG_DEMO), scrwidth>>1,scrheight-getImage(IMG_DEMO).getHeight()-5/*bottomMargin*/,Graphics.HCENTER|Graphics.VCENTER);
	}

//        gBuffer.setColor(0x0);
//        gBuffer.drawRect(baseX,baseY,levelImage.getWidth(),levelImage.getHeight());


	bufferChanged=false;
//System.out.println(System.currentTimeMillis() - time);

    }
    private void wavelet(Graphics g, int item){
			  if(!warn){
		            g.setColor(BkgCOLOR);    // drawin' flyin' text
			    g.fillRect(0,0,scrwidth,scrheight);
		            g.setColor(0x00ff33);    // drawin' flyin' text
			    Font f = Font.getDefaultFont();
			    g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                    g.drawString(unpacking,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//			    g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			   } else {
                             try{
	                       WDecoder.decodeImageToDirectGraphics(DirectUtils.getDirectGraphics(g),0,0,Elements[item],0);
			       warn=false;
			       Runtime.getRuntime().gc();
                             } catch(Exception e){/*System.out.println(e);*/}
//	                     g.drawImage(getImage(IMG_FP),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
			     g.drawImage(Ele[TOTAL_IMAGES_NUMBER]/*MenuIcon*/,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			   }
		warn = false;
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
		          Graphics gBuffer = g;
	                  gBuffer.setColor(BkgCOLOR);gBuffer.fillRect(0, 0, scrwidth,scrheight);
		          gBuffer.setColor(0xffffff);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  gBuffer.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_SMALL));
	                  gBuffer.drawString(loadString/*"Loading ...["+LoadingNow+"/"+LoadingTotal+"]"*/ ,scrwidth>>1, (scrheight>>1)-13,Graphics.TOP|Graphics.HCENTER);
//	                  gBuffer.drawString("Loading ...["+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()+"]" ,scrwidth>>1, (scrheight>>1)-14,Graphics.TOP|Graphics.HCENTER);
		          gBuffer.setColor(0xffff00);    // drawin' flyin' text
	                  gBuffer.drawRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40,6);
	                  gBuffer.fillRect((scrwidth-40)>>1,((scrheight-15)>>1)+10, 40*LoadingNow/Math.max(1,LoadingTotal),6);
			  gBuffer.setFont(f);
		          //      g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);

	                  } break;
              case titleStatus :
                          {
                             wavelet(g,IMG_FP);
                          }
			  break;
	      case newStage:  {
	                  g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
		          g.setColor(0xffffff);    // drawin' flyin' text
			  Font f = Font.getDefaultFont();
			  g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
	                  g.drawString(NewStage+(stage+1) ,scrwidth>>1, (scrheight - 10)>>1,Graphics.TOP|Graphics.HCENTER);
			  g.setFont(f);
	                  } break;

	      case Finished: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_WON);
	      case gameOver: //if (LostWon == null)  LostWon = pngresource.getImage(pngresource.IMG_LOST);
			    if (ticks>FINAL_PICTURE_OBSERVING_TIME) {
				g.setColor(BkgCOLOR);g.fillRect(0, 0, scrwidth,scrheight);
				g.setColor(0xFFFF00);    // drawin' flyin' text
			        Font f = Font.getDefaultFont();
			        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL , Font.STYLE_BOLD, Font.SIZE_SMALL));
				g.drawString(YourScore+client_sb.getGameStateBlock().getPlayerScore() ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
				g.drawImage(Ele[TOTAL_IMAGES_NUMBER]/*MenuIcon*/,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
                     	    } else {
			         wavelet(g,gameStatus==gameOver?IMG_LOST:IMG_WON);
                     	    }
			    break;

	      //case demoPlay:
              default :  // if (this.isDoubleBuffered())
			      {
		                DoubleBuffer(g);
			 //     }
			 //     else {
		         //       DoubleBuffer(gBuffer);
		         //       g.drawImage(Buffer,0,0,Graphics.TOP|Graphics.LEFT);
			      }
			      g.drawImage(Ele[TOTAL_IMAGES_NUMBER]/*MenuIcon*/,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);

            }
      // Runtime.getRuntime().gc();
      // g.setColor(0xff0000);
      // g.drawString(Long.toString(Runtime.getRuntime().freeMemory()),10,20,Graphics.TOP|Graphics.LEFT);
      warn = false;
    }
    public boolean autosaveCheck(){
      return (gameStatus == playGame || gameStatus == Crashed  || gameStatus == newStage);
    }
    public void gameLoaded(){
       blink=false;
       ticks = 0;
       direct=Zynaps_PMR.BUTTON_NONE;
       Zynaps_GSB loc =(Zynaps_GSB)client_sb.getGameStateBlock();
       CurrentLevel=loc.getLevel();
       stageSleepTime[playGame] = loc.i_TimeDelay;
       stage = loc.getStage();
       client_pmr.i_Value = direct;
       client_sb.nextGameStep(client_pmr);
       gameStatus=playGame;
       bufferChanged=true;
//       drawBackScreen();

	bk1_offset = 0;
	bk2_offset = 0;

       repaint();
    }

private Image getImage(int a){
  if(Ele[a]==null) {
//    if (a>=0 && a<3)
                    a=3;
  }
    return Ele[a];
}
public void CleanSpace(){
  for(int i =40;i<50;i++) Ele[i]=null;
  Runtime.getRuntime().gc();
}


private static final byte IMG_WON = (byte)2;
private static final byte IMG_LOST = (byte)1;
private static final byte IMG_FP = (byte)0;
private static final byte IMG_BOSS0 = (byte)26;
private static final byte IMG_BOSS2 = (byte)28;
private static final byte IMG_BOSS1 = (byte)27;
private static final byte IMG_BOSS3 = (byte)29;
private static final byte IMG_BLOCK6 = (byte)46;
private static final byte IMG_EXPL1 = (byte)57;
private static final byte IMG_EXPL2 = (byte)58;
private static final byte IMG_EXPL0 = (byte)56;
private static final byte IMG_EXPL5 = (byte)61;
private static final byte IMG_EXPL3 = (byte)59;
private static final byte IMG_BLOCK7 = (byte)47;
private static final byte IMG_BLOCK1 = (byte)41;
private static final byte IMG_BLOCK8 = (byte)48;
private static final byte IMG_BLOCK9 = (byte)49;
private static final byte IMG_BLOCK3 = (byte)43;
private static final byte IMG_BLOCK2 = (byte)42;
private static final byte IMG_PEXPL2 = (byte)10;
private static final byte IMG_BLOCK5 = (byte)45;
private static final byte IMG_BCKG0 = (byte)5;
private static final byte IMG_PEXPL1 = (byte)9;
private static final byte IMG_BCKG1 = (byte)6;
private static final byte IMG_BLOCK4 = (byte)44;
private static final byte IMG_DEMO = (byte)3;
private static final byte IMG_PEXPL3 = (byte)11;
private static final byte IMG_ENEMY3_0 = (byte)34;
private static final byte IMG_BLOCK0 = (byte)40;
private static final byte IMG_PLAYER = (byte)7;
private static final byte IMG_ENEMY3_1 = (byte)35;
private static final byte IMG_ENEMY1_0 = (byte)31;
private static final byte IMG_ENEMY2 = (byte)33;
private static final byte IMG_ENEMY4_0 = (byte)37;
private static final byte IMG_ENEMY1_1 = (byte)32;
private static final byte IMG_ENEMY4_2 = (byte)39;
private static final byte IMG_WEAPON2_0 = (byte)17;
private static final byte IMG_PEXPL4 = (byte)12;
private static final byte IMG_ENEMY4_1 = (byte)38;
private static final byte IMG_ENEMY3_2 = (byte)36;
private static final byte IMG_ENEMY0 = (byte)30;
private static final byte IMG_BOSSBULLET2 = (byte)55;
private static final byte IMG_BOSSBULLET0 = (byte)53;
private static final byte IMG_BOSSBULLET1 = (byte)54;
private static final byte IMG_WEAPON3_1 = (byte)20;
private static final byte IMG_BULLET2 = (byte)50;
private static final byte IMG_PEXPL0 = (byte)8;
private static final byte IMG_WEAPON2_1 = (byte)18;
private static final byte IMG_WEAPON3_0 = (byte)19;
private static final byte IMG_WEAPON0_0 = (byte)14;
private static final byte IMG_WEAPON1_0 = (byte)15;
private static final byte IMG_WEAPON0_1 = (byte)13;
private static final byte IMG_WEAPON1_1 = (byte)16;
private static final byte IMG_EXPL4 = (byte)60;
private static final byte IMG_ESHIELD0 = (byte)21;
private static final byte IMG_ESHIELD1 = (byte)22;
private static final byte IMG_ESHIELD2 = (byte)23;
private static final byte IMG_HEART = (byte)4;
private static final byte IMG_BULLET1 = (byte)51;
private static final byte IMG_ENEMYBULLET = (byte)52;
private static final byte IMG_SHIELD0 = (byte)24;
private static final byte IMG_SHIELD1 = (byte)25;
private static final byte IMG_BOSS_ICO = (byte)62;
private static final byte TOTAL_IMAGES_NUMBER = (byte)63;

}
