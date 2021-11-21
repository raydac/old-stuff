package com.igormaznitsa.midp;

import com.igormaznitsa.midp.Melody.Melody;
//import com.igormaznitsa.midp.Melody.MelodyEventListener;
import com.nokia.mid.sound.Sound;
import com.nokia.mid.sound.SoundListener;
import com.nokia.mid.ui.DeviceControl;
import com.nokia.mid.ui.FullCanvas;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.*;
import com.igormaznitsa.midp.ImageBlock;
import com.igormaznitsa.game_kit_out_6BE902.DuckHunt.*;



public class GameCanvas extends FullCanvas implements SoundListener, Runnable {


// moving objects
    private static final byte iDuck_StartsLeft = 0,
			      iDuck_StartsRight = iDuck_StartsLeft + MoveObject.ANIMATION_FRAMES*MoveObject.DUCK_DISTANCE_STEP,
			      iDuck_FlyLeft = iDuck_StartsRight + MoveObject.ANIMATION_FRAMES*MoveObject.DUCK_DISTANCE_STEP,
			      iDuck_FlyRight = iDuck_FlyLeft+MoveObject.ANIMATION_FRAMES*MoveObject.DUCK_DISTANCE_STEP,
			      iDuck_Falls = iDuck_FlyRight+MoveObject.ANIMATION_FRAMES*MoveObject.DUCK_DISTANCE_STEP,
			      iCane = iDuck_Falls+MoveObject.DUCK_DISTANCE_STEP,
			      iBurst = iCane+MoveObject.CANE_DISTANCE_STEP,
			      iGun_Ready = iBurst+MoveObject.ANIMATION_FRAMES*MoveObject.BURST_DISTANCE_STEP,
			      iGun_Charging = iGun_Ready + Gun.GUN_TURN_FRAMES;

  private Image iAmmo;
  private Image iClock;
  private Image iSight;
  private Image iBog;
  private Image []items=new Image[iGun_Charging+Gun.GUN_CHARGING_FRAMES];
  protected DuckHunt_SB client_sb;

  private MoveObject [] sortbuff=new MoveObject[DuckHunt_SB.MAX_ANIMATION_OBJECTS*(MoveObject.DUCK_DISTANCE_STEP-1)];
  private int []sortptr = new int[MoveObject.DUCK_DISTANCE_STEP-1];
  private static final int Cane_Dist_Discrete = (MoveObject.CANE_DISTANCE_STEP+1)/MoveObject.DUCK_DISTANCE_STEP;

  private GameSoundBlock gsb;
  private boolean isFirst = false;

  private int fontHeight = 0,fontWidth = 0;
  private Image[] numbers = new Image[10];















  public static final int KEY_MENU = -7;
  public static final int KEY_CANCEL = -6;
  public static final int KEY_ACCEPT = -7;
  public static final int KEY_BACK = -10;

//  protected MelodyEventListener p_melodyeventlistener;
  protected Melody p_playedmelody;
  protected boolean lg_blockingmelody;
  private boolean lg_isShown;

  public final static int ANTI_CLICK_DELAY = 4; //x200ms = 1 seconds , game over / finished sleep time
  public final static int FINAL_PICTURE_OBSERVING_TIME = 16; //x200ms = 4 seconds , game over / finished sleep time
  public final static int TOTAL_OBSERVING_TIME = 28; //x200ms = 7 seconds , game over / finished sleep time

  public int AMOUNT_OF_LOADED_RESOURCES = 0;

  public startup midlet;
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

  public int _score;

     public static final int IMG_INFON_NOKIA = 209; //Infon_Siemens.png
     public static final int IMG_MENUICO = 1769569; //menuico.png


 public static long [] storage = {
5927942488114331648l, 1945555040534003712l, -6496490840993562623l, 4702964862425235558l, -4463716146635573164l, -8276159864300564432l, -2115389633125000631l, -1915432779544587726l, -8643232179708091708l, -4904268192639252717l, 4531018965983428055l, 4089220766095415561l, 2886726237415350330l, -8348864285874133981l, 1283266176360859003l, 1228226418263785907l, 4117892338884029116l, 6932218993778526213l, 8325230085525390910l, -7562525395094173520l, -3238674416029381878l, -5387716821936493512l, 4606031933696670478l, -5835568900809439309l, 925309889345l, 6936297984498157824l, -126l,
5927942488114331648l, 360287970307080192l, 1050556976355869704l, 4702964381388898501l, 2675705529530452l, -2261394425466714048l, 2345879546012841255l, -5865370525394499904l, -4270371276757873702l, 2971562147760700094l, 98183410043l, 6936297984498157824l, -126l,
};


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


    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public void getPlayerMoveRecord(){
         client_sb.i_value = direct;
    }

    /**
     * Initing of the player before starting of a new game session
     */

    public void prepareImages(Image [] _image_array) throws NullPointerException {
           Title = _image_array[IMG_FP];
//           levelImage=_image_array[IMG_3,false);
           Lost = _image_array[IMG_LOST];
           DemoTitle = _image_array[IMG_DEMO];
           Won = _image_array[IMG_WON];
	   iAmmo = _image_array[IMG_BULLETSICO];
	   iClock = _image_array[IMG_TIMEICO];
	   iSight = _image_array[IMG_SIGHTICO];
	   iBog = Image.createImage(scrwidth,scrheight);
	   iBog.getGraphics().drawImage(_image_array[IMG_BOLOTO],0,client_sb.i_screenHeight>>1,0);

 //------------------
	Image img = _image_array[IMG_NFONT];
        fontHeight = img.getHeight();
        fontWidth = img.getWidth() / 10;
        for (int i = 1; i < 10; i++)
        {
            numbers[i] = Image.createImage(fontWidth, fontHeight);
            numbers[i].getGraphics().drawImage(img, -(i - 1) * fontWidth, 0, Graphics.TOP | Graphics.LEFT);
        }
        numbers[0] = Image.createImage(fontWidth, fontHeight);
        numbers[0].getGraphics().drawImage(img, -9 * fontWidth, 0, Graphics.TOP | Graphics.LEFT);
 //------------------


	   items[iDuck_FlyLeft+6+0] = _image_array[IMG_DUCK_B_L0];
	   items[iDuck_FlyLeft+6+1] = _image_array[IMG_DUCK_B_L1];
	   items[iDuck_FlyLeft+6+2] = _image_array[IMG_DUCK_B_L2];
	   items[iDuck_FlyLeft+3+0] = _image_array[IMG_DUCK_M_L0];
	   items[iDuck_FlyLeft+3+1] = _image_array[IMG_DUCK_M_L1];
	   items[iDuck_FlyLeft+3+2] = _image_array[IMG_DUCK_M_L2];
	   items[iDuck_FlyLeft+0+0] = _image_array[IMG_DUCK_S_L0];
	   items[iDuck_FlyLeft+0+1] = _image_array[IMG_DUCK_S_L1];
	   items[iDuck_FlyLeft+0+2] = _image_array[IMG_DUCK_S_L2];

	   items[iDuck_FlyRight+6+0] = _image_array[IMG_DUCK_B_R0];
	   items[iDuck_FlyRight+6+1] = _image_array[IMG_DUCK_B_R1];
	   items[iDuck_FlyRight+6+2] = _image_array[IMG_DUCK_B_R2];
	   items[iDuck_FlyRight+3+0] = _image_array[IMG_DUCK_M_R0];
	   items[iDuck_FlyRight+3+1] = _image_array[IMG_DUCK_M_R1];
	   items[iDuck_FlyRight+3+2] = _image_array[IMG_DUCK_M_R2];
	   items[iDuck_FlyRight+0+0] = _image_array[IMG_DUCK_S_R0];
	   items[iDuck_FlyRight+0+1] = _image_array[IMG_DUCK_S_R1];
	   items[iDuck_FlyRight+0+2] = _image_array[IMG_DUCK_S_R2];


	   items[iDuck_StartsRight+6+0] = _image_array[IMG_DUCK_B_UR0];
	   items[iDuck_StartsRight+6+1] = _image_array[IMG_DUCK_B_UR1];
	   items[iDuck_StartsRight+6+2] = _image_array[IMG_DUCK_B_UR2];
	   items[iDuck_StartsRight+3+0] = _image_array[IMG_DUCK_M_UR0];
	   items[iDuck_StartsRight+3+1] = _image_array[IMG_DUCK_M_UR1];
	   items[iDuck_StartsRight+3+2] = _image_array[IMG_DUCK_M_UR2];
	   items[iDuck_StartsRight+0+0] = _image_array[IMG_DUCK_S_UR0];
	   items[iDuck_StartsRight+0+1] = _image_array[IMG_DUCK_S_UR1];
	   items[iDuck_StartsRight+0+2] = _image_array[IMG_DUCK_S_UR2];

	   items[iDuck_StartsLeft+6+0] = _image_array[IMG_DUCK_B_UL0];
	   items[iDuck_StartsLeft+6+1] = _image_array[IMG_DUCK_B_UL1];
	   items[iDuck_StartsLeft+6+2] = _image_array[IMG_DUCK_B_UL2];
	   items[iDuck_StartsLeft+3+0] = _image_array[IMG_DUCK_M_UL0];
	   items[iDuck_StartsLeft+3+1] = _image_array[IMG_DUCK_M_UL1];
	   items[iDuck_StartsLeft+3+2] = _image_array[IMG_DUCK_M_UL2];
	   items[iDuck_StartsLeft+0+0] = _image_array[IMG_DUCK_S_UL0];
	   items[iDuck_StartsLeft+0+1] = _image_array[IMG_DUCK_S_UL1];
	   items[iDuck_StartsLeft+0+2] = _image_array[IMG_DUCK_S_UL2];



	   items[iCane+0] = _image_array[IMG_CANE0];
	   items[iCane+1] = _image_array[IMG_CANE1];
	   items[iCane+2] = _image_array[IMG_CANE2];
	   items[iCane+3] = _image_array[IMG_CANE3];
	   items[iCane+4] = _image_array[IMG_CANE4];
	   items[iCane+5] = _image_array[IMG_CANE5];
	   items[iCane+6] = _image_array[IMG_CANE6];
	   items[iCane+7] = _image_array[IMG_CANE7];
	   items[iCane+8] = _image_array[IMG_CANE8];
	   items[iCane+9] = _image_array[IMG_CANE9];
	   items[iCane+10] = _image_array[IMG_CANE10];

	   items[iBurst+6+0] = _image_array[IMG_BURST_B0];
	   items[iBurst+6+1] = _image_array[IMG_BURST_B1];
	   items[iBurst+6+2] = _image_array[IMG_BURST_B2];
	   items[iBurst+3+0] = _image_array[IMG_BURST_M0];
	   items[iBurst+3+1] = _image_array[IMG_BURST_M1];
	   items[iBurst+3+2] = _image_array[IMG_BURST_M2];
	   items[iBurst+0+0] = _image_array[IMG_BURST_S0];
	   items[iBurst+0+1] = _image_array[IMG_BURST_S1];
	   items[iBurst+0+2] = _image_array[IMG_BURST_S2];

	   items[iDuck_Falls+2] = _image_array[IMG_KILLED_DUCK_BIG];
	   items[iDuck_Falls+1] = _image_array[IMG_KILLED_DUCK_MID];
	   items[iDuck_Falls+0] = _image_array[IMG_KILLED_DUCK_SMALL];
/*
	   items[iGun_Ready+0] = _image_array[IMG_GUN1];
	   items[iGun_Ready+1] = _image_array[IMG_GUN1];
	   items[iGun_Ready+2] = _image_array[IMG_GUN2];
	   items[iGun_Ready+3] = _image_array[IMG_GUN3];
	   items[iGun_Ready+4] = _image_array[IMG_GUN4];
	   items[iGun_Ready+5] = _image_array[IMG_GUN5];
	   items[iGun_Ready+6] = _image_array[IMG_GUN5];
*/
	   items[iGun_Ready+0] = _image_array[IMG_GUN1];
	   items[iGun_Ready+1] = _image_array[IMG_GUN2];
	   items[iGun_Ready+2] = _image_array[IMG_GUN3];
	   items[iGun_Ready+3] = _image_array[IMG_GUN4];
	   items[iGun_Ready+4] = _image_array[IMG_GUN5];

	   items[iGun_Charging+0] = _image_array[IMG_GUN3];
	   items[iGun_Charging+1] = _image_array[IMG_CHARG0];
	   items[iGun_Charging+2] = _image_array[IMG_CHARG1];
	   items[iGun_Charging+3] = _image_array[IMG_CHARG2];
	   items[iGun_Charging+4] = _image_array[IMG_CHARG3];
    }



///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       direct=DuckHunt_SB.BUTTON_NONE;
       CurrentLevel=level;
//       baseX=((scrwidth-levelImage.getWidth())>>1);
//       baseY=((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGameSession(0/*DuckHunt_SB.LEVEL_DEMO*/);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGameSession(CurrentLevel);
	   gameStatus=playGame;
	   stageSleepTime[playGame]=client_sb.getTimeDelay();
	}
	bufferChanged=true;
	repaint(0,0,scrwidth,scrheight); //custom_paint();
    }

    public int ParsePressedKey(int keyCode){
             switch (keyCode) {
	        case Canvas.KEY_NUM4: return DuckHunt_SB.BUTTON_LEFT;
		case Canvas.KEY_NUM6: return DuckHunt_SB.BUTTON_RIGHT;
		case Canvas.KEY_NUM2: return DuckHunt_SB.BUTTON_UP;
		case Canvas.KEY_NUM8: return DuckHunt_SB.BUTTON_DOWN;
		case Canvas.KEY_NUM5: return DuckHunt_SB.BUTTON_FIRE;
             }
        return DuckHunt_SB.BUTTON_NONE;
    }


    public void showTitle(){
      if(isFirst && gsb!=null && ((startup)midlet)._Sound)gsb.playSound(SND_MELODY_OTT, false);
      isFirst = false;
    }
    public int demoPlayNextStep(){
		           client_sb.nextGameStep();
			   return ((DuckHunt_GSB)client_sb.getGameStateBlock()).getGameState();
    }

    public void playGameNextStep(){
			   client_sb.nextGameStep();
			   DuckHunt_GSB loc=(DuckHunt_GSB)client_sb.getGameStateBlock();
			   switch (loc.getPlayerState()){
			     case DuckHunt_GSB.PLAYERSTATE_LOST:
			              ticks = 0;  blink = true;  gameStatus = gameOver;
			              _score = loc.getPlayerScore();
				      isFirst = true;
			              break;
			     case DuckHunt_GSB.PLAYERSTATE_WON:
				      ticks = 0;  blink = true;  gameStatus = Finished;
			              _score = loc.getPlayerScore();
				      isFirst = true;
			              break;
			   }
    }

//public static int cc=0;

    private Image simpleSort(MoveObject p_obj){
      try{
      if (p_obj.isActive()){
              int dist=p_obj.getDistanceFrame();
	      switch (p_obj.getType()){
		case MoveObject.OBJECT_CANE: dist = p_obj.getDistanceFrame()/Cane_Dist_Discrete -1;
		default:  if (dist-->0){
//		                 cc++; if (dist<0 || dist>sortptr.length) System.out.println(sortptr.length+","+dist);
		                 sortbuff[sortptr[dist]++]=p_obj;
//				 System.out.println("d..->"+dist+","+sortptr[dist]);
			         return null;
		          }
		case MoveObject.OBJECT_BURST: return getObjImage(p_obj);
	      }
	}
      }catch (Exception e){}
	 return null;
    }



    private Image getObjImage(MoveObject p_obj){
            try{
	       if(p_obj.getState()==MoveObject.STATE_KILLED)
	         return items[iDuck_Falls+p_obj.getDistanceFrame()];
	       else
                switch(p_obj.getType())
                {
                    case MoveObject.OBJECT_CANE :  return items[iCane+p_obj.getDistanceFrame()];
                    case MoveObject.OBJECT_DUCK_FLYING_RIGHT : return items[iDuck_FlyRight+p_obj.getDistanceFrame()*3+p_obj.getFrame()];
                    case MoveObject.OBJECT_DUCK_FLYING_LEFT :  return items[iDuck_FlyLeft+p_obj.getDistanceFrame()*3+p_obj.getFrame()];
                    case MoveObject.OBJECT_DUCK_TAKEN_OFF_LEFT :return items[iDuck_StartsLeft+p_obj.getDistanceFrame()*3+p_obj.getFrame()];
                    case MoveObject.OBJECT_DUCK_TAKEN_OFF_RIGHT :return items[iDuck_StartsRight+p_obj.getDistanceFrame()*3+p_obj.getFrame()];
                    case MoveObject.OBJECT_BURST :return items[iBurst+p_obj.getDistanceFrame()*3+p_obj.getFrame()];
                }
            } catch(Exception e){}
	 return null;
    }


    public void DoubleBuffer(Graphics gBuffer){
	int i;
	Image img=null;

        gBuffer.drawImage(iBog,0,0,0);


        DuckHunt_GSB loc =(DuckHunt_GSB)client_sb.getGameStateBlock();

        // Draw objects
	for(i=0;i<sortptr.length;i++)sortptr[i]=DuckHunt_SB.MAX_ANIMATION_OBJECTS*i;
//cc = 0;

	MoveObject tmp;
        MoveObject [] ap_moveobjects = loc.getMoveObjects();

        for(i=0;i<ap_moveobjects.length;i++)
        {

	   tmp=ap_moveobjects[i];
	   if((img = simpleSort(tmp))!=null)
	   if(tmp.isActive() && (img = getObjImage(tmp))!=null)
                  gBuffer.drawImage(img,tmp.getScrX(),tmp.getScrY(),0);
        }

	for(i=0;i<sortptr.length;)
	  if(--sortptr[i]>=0 && sortptr[i]<sortbuff.length && sortbuff[sortptr[i]]!=null)
	  {
	       tmp=sortbuff[sortptr[i]];
	       img = getObjImage(tmp);
	       if(img!=null){
                  gBuffer.drawImage(img,tmp.getScrX(),tmp.getScrY(),0);
	       }
	       sortbuff[sortptr[i]]=null;
	  }
	    else i++;

        Score [] ap_score = client_sb.getScoreArray();
        for(i=0;i<ap_score.length;i++)
        {
            Score p_obj = ap_score[i];
            if (p_obj!=null && p_obj.isActive())
	        drawDigits(gBuffer,p_obj.getScrX(),p_obj.getScrY(),3,(p_obj.getType()+1)*100);
        }




        Gun p_gun = loc.getGunObject();
        // Draw sight
        if (p_gun.isSightVisibled())
	   gBuffer.drawImage(iSight,p_gun.getSightX(),p_gun.getSightY(),0);

	try{
        // Draw gun
        if (p_gun.getState()==Gun.STATE_CHARGING)
        gBuffer.drawImage(items[iGun_Charging+p_gun.getFrame()], p_gun.getGunX(),p_gun.getGunY(),0);
        else
        gBuffer.drawImage(items[iGun_Ready+p_gun.getFrame()], p_gun.getGunX(),p_gun.getGunY(),0);
        // Draw score
	}catch (Exception e){}



        i = scrwidth-iAmmo.getWidth();
	gBuffer.drawImage(iAmmo,i,0,0);
        drawDigits(gBuffer,i-fontWidth-fontWidth-1,(iAmmo.getHeight()-fontWidth)>>1,2,loc.getCurrentBullets());

	i = client_sb.getTimeTillEnd();
	if(i>10 || blink){
	   gBuffer.drawImage(iClock,1,0,0);
           drawDigits(gBuffer,iClock.getWidth()+2,(iClock.getHeight()-fontWidth)>>1,3,i);
	}


	if (gameStatus == demoPlay && blink) {
	       gBuffer.drawImage(DemoTitle,scrwidth>>1,scrheight-DemoTitle.getHeight()-5,Graphics.HCENTER|Graphics.VCENTER);
	}
    }

    public void gameLoaded(){
       blink=false;
       direct=DuckHunt_SB.BUTTON_NONE;
       stageSleepTime[playGame]=client_sb.getTimeDelay();
       //CurrentLevel=level;
       //baseX=((scrwidth-levelImage.getWidth())>>1);
       //baseY=((scrheight-levelImage.getHeight())>>1);
       client_sb.nextGameStep();
       gameStatus=playGame;
       bufferChanged=true;
       repaint(0,0,scrwidth,scrheight);//custom_paint();
    }

    public void OutOfResources(){}
    public void Crashed(){}

    public boolean isLoadSaveSupporting(){
      return true;
    }


//    public void gameAction(int actionID,int param0){}
//    public void gameAction(int actionID,int param0,int param1){}
//    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID){
      if(gsb!=null && ((startup)midlet)._Sound){
       switch (actionID){
	  case DuckHunt_SB.GAMEACTION_SOUND_HIT:        gsb.playSound(SND_HIT_OTT, false); break;
	  case DuckHunt_SB.GAMEACTION_SOUND_KRYA_KRYA:  gsb.playSound(SND_KRYAKRYA_OTT, false);break;
	  case DuckHunt_SB.GAMEACTION_SOUND_NOHIT:      /*gsb.playSound(SND_NOHIT_OTT, false);*/break;
	  case DuckHunt_SB.GAMEACTION_SOUND_SHOOT:      gsb.playSound(SND_SHOT_OTT, false);
							//if(((startup)midlet)._Vibra)com.nokia.mid.ui.DeviceControl.stopVibra();
							//activateVibrator(250);
							break;
       }
      }
    }


private static final byte IMG_BOLOTO = (byte)0;
private static final byte IMG_BULLETSICO = (byte)1;
private static final byte IMG_BURST_B0 = (byte)2;
private static final byte IMG_BURST_B1 = (byte)3;
private static final byte IMG_BURST_B2 = (byte)4;
private static final byte IMG_BURST_M0 = (byte)5;
private static final byte IMG_BURST_M1 = (byte)6;
private static final byte IMG_BURST_M2 = (byte)7;
private static final byte IMG_BURST_S0 = (byte)8;
private static final byte IMG_BURST_S1 = (byte)9;
private static final byte IMG_BURST_S2 = (byte)10;
private static final byte IMG_CANE0 = (byte)11;
private static final byte IMG_CANE1 = (byte)12;
private static final byte IMG_CANE10 = (byte)13;
private static final byte IMG_CANE2 = (byte)14;
private static final byte IMG_CANE3 = (byte)15;
private static final byte IMG_CANE4 = (byte)16;
private static final byte IMG_CANE5 = (byte)17;
private static final byte IMG_CANE6 = (byte)18;
private static final byte IMG_CANE7 = (byte)19;
private static final byte IMG_CANE8 = (byte)20;
private static final byte IMG_CANE9 = (byte)21;
private static final byte IMG_CHARG0 = (byte)22;
private static final byte IMG_CHARG1 = (byte)23;
private static final byte IMG_CHARG2 = (byte)24;
private static final byte IMG_CHARG3 = (byte)25;
private static final byte IMG_DEMO = (byte)26;
private static final byte IMG_DUCK_B_L0 = (byte)27;
private static final byte IMG_DUCK_B_L1 = (byte)28;
private static final byte IMG_DUCK_B_L2 = (byte)29;
private static final byte IMG_DUCK_B_R0 = (byte)30;
private static final byte IMG_DUCK_B_R1 = (byte)31;
private static final byte IMG_DUCK_B_R2 = (byte)32;
private static final byte IMG_DUCK_B_UL0 = (byte)33;
private static final byte IMG_DUCK_B_UL1 = (byte)34;
private static final byte IMG_DUCK_B_UL2 = (byte)35;
private static final byte IMG_DUCK_B_UR0 = (byte)36;
private static final byte IMG_DUCK_B_UR1 = (byte)37;
private static final byte IMG_DUCK_B_UR2 = (byte)38;
private static final byte IMG_DUCK_M_L0 = (byte)39;
private static final byte IMG_DUCK_M_L1 = (byte)40;
private static final byte IMG_DUCK_M_L2 = (byte)41;
private static final byte IMG_DUCK_M_R0 = (byte)42;
private static final byte IMG_DUCK_M_R1 = (byte)43;
private static final byte IMG_DUCK_M_R2 = (byte)44;
private static final byte IMG_DUCK_M_UL0 = (byte)45;
private static final byte IMG_DUCK_M_UL1 = (byte)46;
private static final byte IMG_DUCK_M_UL2 = (byte)47;
private static final byte IMG_DUCK_M_UR0 = (byte)48;
private static final byte IMG_DUCK_M_UR1 = (byte)49;
private static final byte IMG_DUCK_M_UR2 = (byte)50;
private static final byte IMG_DUCK_S_L0 = (byte)51;
private static final byte IMG_DUCK_S_L1 = (byte)52;
private static final byte IMG_DUCK_S_L2 = (byte)53;
private static final byte IMG_DUCK_S_R0 = (byte)54;
private static final byte IMG_DUCK_S_R1 = (byte)55;
private static final byte IMG_DUCK_S_R2 = (byte)56;
private static final byte IMG_DUCK_S_UL0 = (byte)57;
private static final byte IMG_DUCK_S_UL1 = (byte)58;
private static final byte IMG_DUCK_S_UL2 = (byte)59;
private static final byte IMG_DUCK_S_UR0 = (byte)60;
private static final byte IMG_DUCK_S_UR1 = (byte)61;
private static final byte IMG_DUCK_S_UR2 = (byte)62;
private static final byte IMG_FP = (byte)63;
private static final byte IMG_GUN1 = (byte)64;
private static final byte IMG_GUN2 = (byte)65;
private static final byte IMG_GUN3 = (byte)66;
private static final byte IMG_GUN4 = (byte)67;
private static final byte IMG_GUN5 = (byte)68;
private static final byte IMG_KILLED_DUCK_BIG = (byte)69;
private static final byte IMG_KILLED_DUCK_MID = (byte)70;
private static final byte IMG_KILLED_DUCK_SMALL = (byte)71;
private static final byte IMG_LOST = (byte)72;
private static final byte IMG_NFONT = (byte)73;
private static final byte IMG_SIGHTICO = (byte)74;
private static final byte IMG_TIMEICO = (byte)75;
private static final byte IMG_WON = (byte)76;

private static final byte SND_HIT_OTT = (byte)0;
private static final byte SND_KRYAKRYA_OTT = (byte)1;
private static final byte SND_MELODY_OTT = (byte)2;
private static final byte SND_SHOT_OTT = (byte)3;


































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
       if (System.currentTimeMillis()-_loading_time_range>6){
         _loading_time_range = System.currentTimeMillis();
         repaint(); //custom_paint();
       }
    }

    public void startIt() {
       if (thread==null){
          thread=new Thread(this);
	  thread.start();
       }
     loading(AMOUNT_OF_LOADED_RESOURCES);
     try {

	    ImageBlock pnl = new ImageBlock("/res/images.bin",this);
            prepareImages(pnl._image_array);
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
       repaint(); //custom_paint();
    }

    public void loading(){
       gameStatus=notInitialized;
       ticks = 0;
       repaint(); //custom_paint();
    }
    public void endGame() {
	gameStatus=titleStatus;
        ticks =0;
	repaint(); //custom_paint();
    }


  /**Construct the displayable*/
  public GameCanvas(startup m) {
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

    AMOUNT_OF_LOADED_RESOURCES = 67/*text strings*/+76/*images*/+5/*sound*/;

    client_sb = new DuckHunt_SB(scrwidth,scrheight,this);
       try{
         gsb = new GameSoundBlock(this,"/res/sound.bin",this);
       }catch (Exception e){ gsb = null;}
    isFirst = true;

  }

///////////////////////////////////////////////////////////////////////////////////

    public void destroy(){
      animation=false;
      gameStatus=notInitialized;
      if (thread!=null) thread=null;
    }

///////////////////////////////////////////////////////////////////////////////////


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
					        repaint(); //custom_paint();
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
       if(gameStatus == titleStatus){
	   stopAllMelodies();
	   isFirst=false;
       }
        if (gameStatus==playGame) {
	  direct = DuckHunt_SB.BUTTON_NONE;
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
        if (lg_isShown)
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
			   if (demoPlayNextStep()==DuckHunt_GSB.GAMESTATE_OVER || ++ticks>demoLimit) {
	                       gameStatus=titleStatus;
			       ticks=0;
			       System.gc();
			   }

			   repaint(); //custom_paint();
			  } break;
           case playGame :{
			   blink=(++ticks&4)==0;
			   playGameNextStep();
			   repaint(); //custom_paint();
			  } break;

	    case outOfResources: OutOfResources(); break;
	    case Crashed: Crashed();break;

	    case Finished:
	    case gameOver:
	                   if(ticks++>40){
			     ticks = 0;
			     gameStatus=titleStatus;
	                   } else
			    if(ticks>=FINAL_PICTURE_OBSERVING_TIME){
			      midlet.newScore(_score);
			    }
			   repaint(); //custom_paint();
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

    public void /*custom_*/paint(Graphics g) {
     //Graphics g = getGraphicsForDoubleBuffer();
            switch (gameStatus) {
	      case notInitialized:  drawLoading(g, LoadingTotal, LoadingNow, loadString); break;
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
				g.drawString(YourScore+_score ,scrwidth>>1, scrheight>>1,Graphics.TOP|Graphics.HCENTER);
                     	    } else {
		          	g.setColor(0xffffff);g.fillRect(0, 0, scrwidth,scrheight);
	                  	g.drawImage((gameStatus==gameOver?Lost:Won),scrwidth>>1,scrheight>>1,Graphics.HCENTER|Graphics.VCENTER);
	                  	g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
			    } break;

	      //case demoPlay:
              default :  // if (this.isDoubleBuffered())
			      {
		               DoubleBuffer(g);
			 //     }
			//      else {
//		                DoubleBuffer(gBuffer);
//		                g.drawImage(Buffer,0,0,0);
			      }
			   g.drawImage(MenuIcon,scrwidth-2,scrheight-1, Graphics.BOTTOM|Graphics.RIGHT);
            }
	//  paintDoubleBuffer(0,0);
    }

    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }

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
    public void setScreenLight(boolean state)
    {
            DeviceControl.setLights(0, state?100:0);
    }


    public void playTone(int freq, int tone_time, boolean blocking)
    {
        new Sound(freq, tone_time);
        if (blocking)
        {
            try
            {
                sleep(tone_time);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public void sleep(int duration) throws InterruptedException
    {
        Thread.sleep(duration);
    }

    public boolean convertMelody(Melody _melody)
    {
        int i_type = 0;
        i_type = Sound.FORMAT_TONE;
        try
        {
            Sound p_sound = new Sound(_melody.getMelodyArray(), i_type);
            p_sound.setSoundListener(this);
            _melody.setNativeObject(p_sound);
        }
        catch (Exception ex)
        {
            return false;
        }
        finally
        {
            Runtime.getRuntime().gc();
        }
        return true;
    }

    public void playMelody(Melody _melody, boolean _blocking)
    {
        if (_blocking)
        {
            stopAllMelodies();
            Sound p_sound = (Sound) _melody.getNativeObject();
            p_playedmelody = _melody;
            lg_blockingmelody = true;
            p_sound.play(1);

            try
            {
                synchronized (_melody)
                {
                    _melody.wait();
                }
            }
            catch (InterruptedException e)
            {
                return;
            }
            p_playedmelody = null;
        }
        else
        {
            if (p_playedmelody != null)
            {
                stopAllMelodies();
            }
            p_playedmelody = _melody;
            lg_blockingmelody = false;
            Sound p_sound = (Sound) _melody.getNativeObject();
            p_sound.play(1);
        }
    }

    public void stopMelody(Melody _melody)
    {
        if (p_playedmelody.equals(_melody)) p_playedmelody = null;
        ((Sound) _melody.getNativeObject()).stop();
    }

    public void stopAllMelodies()
    {
        if (p_playedmelody != null)
        {
            ((com.nokia.mid.sound.Sound) p_playedmelody.getNativeObject()).stop();
            p_playedmelody = null;
        }
    }

    public void soundStateChanged(Sound sound, int event)
    {
        Melody p_melody = p_playedmelody;
        if (p_melody == null) return;

        if (event == com.nokia.mid.sound.Sound.SOUND_STOPPED)
          {
                    if (lg_blockingmelody)
                    {
                        synchronized (p_melody)
                        {
                            p_melody.notify();
                        }
                    }
                   // if (!lg_blockingmelody && p_melodyeventlistener != null) p_melodyeventlistener.melodyEnd(p_melody.getMelodyID());
                    p_melody = null;
          }
    }

    public void showNotify()
    {
        lg_isShown = true;
        if(gameStatus==playGame) client_sb.resumeGame();
    }
    public void hideNotify()
    {
        lg_isShown = false;
        stopAllMelodies();
        if(gameStatus==playGame) client_sb.pauseGame();
    }


}

