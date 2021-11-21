
import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.game_kit_out_6BE902.DriveOff.*;
import com.igormaznitsa.midp.*;
//import com.igormaznitsa.midp.Utils.drawDigits;
import com.igormaznitsa.midp.Melody.*;

public class VCanvas extends com.igormaznitsa.midp.PhoneModels.GameCanvas implements GamePlayerBlock, GameActionListener{


    private static final int DAMAGE_BAR_WIDTH = 15;
    private static final int DAMAGE_BAR_HEIGHT = 4;

  private Image []items;
  private Image iOffRoad,iBorder,iTool,iReady,iHeart,iRoad;
  protected DriveOff_PMR client_pmr = null;
  protected DriveOff_SB client_sb;
  private Image BackScreen;
  private Graphics gBackScreen;
  private int bsCELLs;

  private GameSoundBlock gsb;
  boolean isFirst;
  private int CrashDelay = 0;

  MoveObject [] sortedObjects;
  private int sortHely;
  private int sortAuto;



  /**Construct the displayable*/
  public VCanvas(com.igormaznitsa.gameapi.ControlMenuBlock m) {
    super(m);
    AMOUNT_OF_LOADED_RESOURCES = 67/*text strings*/+44/*images*/+5/*sound*/;
    client_sb = new DriveOff_SB(scrwidth,scrheight,this,null,this);
    client_sb.setPlayerBlock(this);
    BackScreen = Image.createImage(client_sb.VIRTUALCELL_WIDTH*32,(client_sb.SCREEN_LINES+1)*client_sb.VIRTUALCELL_HEIGHT);
    gBackScreen = BackScreen.getGraphics();
    bsCELLs = client_sb.SCREEN_LINES+2;
       try{
         gsb = new GameSoundBlock(this,"/res/sound.bin",this);
       }catch (Exception e){ gsb = null;}
    isFirst = true;
  }

    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateBlock gameStateBlock){
         client_pmr.setValue(direct);

       return client_pmr;
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new DriveOff_PMR();

    }

    public void prepareImages(ImageBlock pnl) throws NullPointerException {
           items = pnl._image_array;
           Title = items[IMG_FP];
           Lost = items[IMG_LOST];
           DemoTitle = items[IMG_DEMO];
           Won = items[IMG_WIN];

           iOffRoad = Image.createImage(DriveOff_SB.VIRTUALCELL_WIDTH<<5,DriveOff_SB.VIRTUALCELL_HEIGHT);
	   for(int i = 0;i<32; i++)
	      iOffRoad.getGraphics().drawImage(items[IMG_OFFROAD],i*DriveOff_SB.VIRTUALCELL_WIDTH,0,0);
//           iOffRoad = items[IMG_OFFROAD];
           items[IMG_OFFROAD] = iOffRoad;
           iBorder = items[IMG_BORDER];
//	   iRoad = Image.createImage(DriveOff_SB.VIRTUALCELL_WIDTH,DriveOff_SB.VIRTUALCELL_HEIGHT);
	   iTool = items[IMG_DAMAGEICO];
	   iReady = items[IMG_ROCKETICO];
	   iHeart = items[IMG_ATTEMPT];
    }



///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       ticks = 0;
       direct=DriveOff_PMR.BUTTON_NONE;
       CurrentLevel=level;
       stage = 0;
       sortedObjects = new MoveObject[DriveOff_SB.ANIMATION_OBJECTS];
//       baseX=((scrwidth-levelImage.getWidth())>>1);
//       baseY=((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGameSession(0/*DriveOff_SB.LEVEL_DEMO*/);
	    client_sb.initStage(demoLevel/*DriveOff_SB.LEVEL_DEMO*/);
	    drawBackScreen();
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	    drawBackScreen();
	} else {
	   client_sb.newGameSession(CurrentLevel);
	   gameStatus=newStage;
	   stageSleepTime[playGame]=client_sb.getTimeDelay();
	}
	bufferChanged=true;
	repaint(0,0,scrwidth,scrheight);
    }

    public int ParsePressedKey(int keyCode){
	if(!((startup)midlet)._Light)setScreenLight(false);
             switch (keyCode) {
	        case Canvas.KEY_NUM4: return DriveOff_PMR.BUTTON_LEFT;
		case Canvas.KEY_NUM6: return DriveOff_PMR.BUTTON_RIGHT;
		case Canvas.KEY_NUM2: return DriveOff_PMR.BUTTON_FIREUP;
		case Canvas.KEY_NUM8: return DriveOff_PMR.BUTTON_FIREDOWN;
             }
        return DriveOff_PMR.BUTTON_NONE;
    }

    protected void keyReleased(int keyCode) {
       if(gameStatus == titleStatus){
	   isFirst=false;
	   this.stopAllMelodies();
       }
        if (gameStatus==playGame) {
	  direct = DriveOff_PMR.BUTTON_NONE;
        }
	if(!((startup)midlet)._Light)setScreenLight(false);
    }


    public void showTitle(){
      if(isFirst && gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_MAIN_OTT, false);
      isFirst = false;
    }

    public void prepareNextStage(){
         direct=0;
	 client_sb.initStage(stage);
         client_sb.nextGameStep();
	 drawBackScreen();
	 Runtime.getRuntime().gc();
    }

    public int demoPlayNextStep(){
		           client_sb.nextGameStep();
			   return ((DriveOff_GSB)client_sb.getGameStateBlock()).getGameState();
    }

    public void playGameNextStep(){
			   client_sb.nextGameStep();
			   DriveOff_GSB loc=(DriveOff_GSB)client_sb.getGameStateBlock();
			   switch (loc.getPlayerState()){
			     case DriveOff_GSB.PLAYERSTATE_LOST:
			              ticks = 0;  blink = true;  gameStatus = Crashed;
				      _score = loc.getPlayerScore();
				      this.stopAllMelodies();
				      if(gsb!=null && ((startup)midlet)._Sound)
				            gsb.playSound(SND_POHORONY_OTT, false);
				      CrashDelay = 20;
			              break;
			     case DriveOff_GSB.PLAYERSTATE_WON:
				      ticks = 0;  blink = true;
				      if(++stage<Stages.TOTAL_STAGES) gameStatus = newStage;
				       else {
				         gameStatus = Finished;
			                 _score = loc.getPlayerScore();
				       }
			              break;
			   }
    }

    public void Crashed(){
			   ticks++; blink=(ticks&2)==0;
			   if (ticks>=CrashDelay){
			         ticks = 0;
				 blink = true;
				 direct = 0;
				 stopAllMelodies();
				 if(((DriveOff_GSB)client_sb.getGameStateBlock()).getAttemptions()<0) {
				    gameStatus = gameOver;
				 } else {
				    gameStatus = playGame;
				    client_sb.resumeGameAfterPlayerLost();
				 }
			   }
    }




//    private int [] fastoff = {IMG_PLAYERCAR,IMG_PLAYERCAR_D,IMG_BUS,IMG_BUS_D,IMG_BENZOVOZ,IMG_BENZOVOZ_D,IMG_CAR,IMG_CAR_D,
//                              IMG_MOTO,IMG_MOTO_D,IMG_HEL0,IMG_HEL_DESTR,IMG_POLICECAR0,IMG_POLICECAR_D,IMG_MAN1_0,IMG_MAN1_D0,
//			      IMG_};

    private Image getObjImage(MoveObject p_obj){
         try{
                switch((p_obj.getType()<<1)+p_obj.getState())
                {
    		   case 0 /*MoveObject.TYPE_PLAYERCAR*/ :   return items[IMG_PLAYERCAR+p_obj.getCurrentFrame()];
    		   case 1 /*MoveObject.TYPE_PLAYERCAR*/ :   return items[IMG_PLAYERCAR_D+p_obj.getCurrentFrame()];
    		   case 2 /*MoveObject.TYPE_CAR0*/ :        return items[IMG_BUS+p_obj.getCurrentFrame()];
    		   case 3 /* MoveObject.TYPE_CAR0*/ :       return items[IMG_BUS_D+p_obj.getCurrentFrame()];
    		   case 4 /* MoveObject.TYPE_CAR1*/ :       return items[IMG_BENZOVOZ+p_obj.getCurrentFrame()];
    		   case 5 /* MoveObject.TYPE_CAR1*/ :       return items[IMG_BENZOVOZ_D+p_obj.getCurrentFrame()];
    		   case 6 /* MoveObject.TYPE_CAR2*/ :       return items[IMG_CAR+p_obj.getCurrentFrame()];
    		   case 7 /* MoveObject.TYPE_CAR2*/ :       return items[IMG_CAR_D+p_obj.getCurrentFrame()];
    		   case 8 /* MoveObject.TYPE_MOTO*/ :       return items[IMG_MOTO+p_obj.getCurrentFrame()];
    		   case 9 /* MoveObject.TYPE_MOTO*/ :       return items[IMG_MOTO_D+p_obj.getCurrentFrame()];
    		   case 10/* MoveObject.TYPE_HELYCOPTER*/ : return items[IMG_HEL0+p_obj.getCurrentFrame()];
    		   case 11/* MoveObject.TYPE_HELYCOPTER*/ : return items[IMG_HEL_DESTR+p_obj.getCurrentFrame()];
    		   case 12/* MoveObject.TYPE_POLICECAR*/ :  return items[IMG_POLICECAR0+p_obj.getCurrentFrame()];
    		   case 13/* MoveObject.TYPE_POLICECAR*/ :  return items[IMG_POLICECAR_D+p_obj.getCurrentFrame()];
    		   case 14/* MoveObject.TYPE_TRAVELLER0*/ : return items[IMG_MAN1_0+p_obj.getCurrentFrame()];
    		   case 15/* MoveObject.TYPE_TRAVELLER0*/ : return items[IMG_MAN1_D0+p_obj.getCurrentFrame()];
    		   case 16/* MoveObject.TYPE_TRAVELLER1*/ : return items[IMG_MAN0_0+p_obj.getCurrentFrame()];
    		   case 17/* MoveObject.TYPE_TRAVELLER1*/ : return items[IMG_MAN0_D0+p_obj.getCurrentFrame()];
    		   case 18/* MoveObject.TYPE_GRENADE*/ :
    		   case 19/* MoveObject.TYPE_GRENADE*/ :    return items[IMG_GRENADE+p_obj.getCurrentFrame()];
    		   case 20/* MoveObject.TYPE_ROCKET*/ :
    		   case 21/* MoveObject.TYPE_ROCKET*/ :     return items[IMG_ROCKET+p_obj.getCurrentFrame()];
    		   case 22/* MoveObject.TYPE_EXPLODING */ :
    		   case 23/* MoveObject.TYPE_EXPLODING */ : return items[IMG_EXPL0+p_obj.getCurrentFrame()];
                }
         }catch (ArrayIndexOutOfBoundsException e){e.printStackTrace();}
	return null;
    }
/*
    private void DrawScanLine(int e, int pos){
      int offs = 0;
            for(int j=0;j<32;j++){
               if ((e&(1<<j))!=0) gBackScreen.drawImage(iRoad,offs,pos,0);
	         else
	             if(j>0 && j<31 && ((e>>>(j-1))&1)!=((e>>>(j+1))&1)) gBackScreen.drawImage(iBorder,offs,pos,0);
		     else gBackScreen.drawImage(iOffRoad,offs,pos,0);
	       offs += DriveOff_SB.VIRTUALCELL_WIDTH;
            }
    }
*/

/*
    private void DrawScanLine(int e, int pos){
      int offs = 0,mark=0, n =0;
      gBackScreen.drawImage(items[IMG_OFFROAD],0,pos,0);
            for(int j=0;j<33;j++){
	      if((e&1)==0 || j==32){
	          if(mark!=offs){
		    try{
		      gBackScreen.fillRect(mark,pos,offs-mark,4);
		      n=0;
		    } catch (Exception ex){
//		        System.out.println("+"+n);
		    }
	          }
		offs+=4;
		mark=offs;
	      } else {
		offs+=4;
	      }
	      e>>>=1;
            }
    }
*/

    private void DrawScanLine(int e, int pos){
      int offs = 0, mark = 0;
      gBackScreen.drawImage(items[IMG_OFFROAD],0,pos,0);
            for(int j=0;j<32;j++){
	       if((e&1)==0){
	          if(mark!=offs){
		         gBackScreen.fillRect(mark,pos,offs-mark,4);
			 gBackScreen.drawImage(iBorder,offs,pos,0);
	             } else
		          if((e&2)>0)gBackScreen.drawImage(iBorder,offs,pos,0);
		  offs+=4; mark=offs;
	        } else offs+=4;
//	       offs += DriveOff_SB.VIRTUALCELL_WIDTH;
	      e>>>=1;
            }
    }





private int pos = 0;
private int bsLAST_FRAME;
private int _player_offset;
private final static int _v_off = DriveOff_SB.VIRTUALCELL_HEIGHT*2;

    private void drawBackScreen(){
        gBackScreen.setColor(0xffffff);

        DriveOff_GSB loc=(DriveOff_GSB)client_sb.getGameStateBlock();
	MoveObject p_player = client_sb.getPlayer();
        _player_offset = DriveOff_SB.PLAYER_CELLLINES+client_sb.HIGH_BORDER+1; // ScrCells - bsCells = 2

        pos = 0;
        bsLAST_FRAME = p_player.getCurrentLine();
        for(int i=0;i<bsCELLs;i++)
	    DrawScanLine(loc.getLineElement(bsLAST_FRAME+_player_offset-i),(i)*DriveOff_SB.VIRTUALCELL_HEIGHT);
    }



    public void DoubleBuffer(Graphics gBuffer){
	int i,j,k,e,li;

        DriveOff_GSB loc=(DriveOff_GSB)client_sb.getGameStateBlock();
	Image img=null;

        MoveObject p_player = client_sb.getPlayer();

        int i_startline = p_player.getCurrentLine();
        int i_yoffst = p_player.getY()%DriveOff_SB.VIRTUALCELL_HEIGHT;

	int offset = 0;
	if(p_player.getX()>(scrwidth>>1))
	   if(p_player.getX()<(DriveOff_SB.VIRTUALCELL_WIDTH<<5)-(scrwidth>>1))
	   offset =  -p_player.getX()+(scrwidth>>1);
	   else offset = -(DriveOff_SB.VIRTUALCELL_WIDTH<<5)+scrwidth;


        if (i_startline!=bsLAST_FRAME){
	  bsLAST_FRAME = i_startline;
	  if(pos>BackScreen.getHeight())pos-=BackScreen.getHeight();
	    DrawScanLine(loc.getLineElement(bsLAST_FRAME+_player_offset),BackScreen.getHeight()-pos);
	  pos+=DriveOff_SB.VIRTUALCELL_HEIGHT;
        }
	gBuffer.drawImage(BackScreen,offset,i_yoffst-_v_off+pos,0);
	gBuffer.drawImage(BackScreen,offset,i_yoffst-_v_off+pos-BackScreen.getHeight(),0);


	i_startline+=_player_offset-1;
        if (i_startline >= loc.getWayLength()) i_startline -= loc.getWayLength();

        i_startline = i_startline * DriveOff_SB.VIRTUALCELL_HEIGHT + i_yoffst;

	 sortHely = 0;
	 sortAuto = 0;

        MoveObject [] ap_mobj = loc.getMoveObjectArray();

        for(li=0;li<ap_mobj.length;li++)
        {
            MoveObject p_obj = ap_mobj[li];
	    if(p_obj.isActive())
	    {
	       if(p_obj.getState() == MoveObject.STATE_KILLED ||
	          p_obj.getType() == MoveObject.TYPE_TRAVELLER0 ||
	          p_obj.getType() == MoveObject.TYPE_TRAVELLER1 )
		  {
		   if(gameStatus!=Crashed || p_player!=p_obj || blink)
	              if((img=getObjImage(p_obj))!=null){
                          i = p_obj.getX()+offset;
                          if (i_startline>p_obj.getY()) j = i_startline - p_obj.getY() - p_obj.getHeight();
                            else j = i_startline - (p_obj.getY()-loc.getWayLength() * DriveOff_SB.VIRTUALCELL_HEIGHT) - p_obj.getHeight();
	              gBuffer.drawImage(img,i,j,0);
	              }
		  } else
	              if(p_obj.getType() == MoveObject.TYPE_HELYCOPTER || p_obj.getType() == MoveObject.TYPE_ROCKET)
		      		    sortedObjects[sortedObjects.length-(++sortHely)]=p_obj;
		         else
	                       sortedObjects[sortAuto++]=p_obj;
	    }
        }
        for(li=0;li<sortAuto;li++)
        {
              MoveObject p_obj = sortedObjects[li];
	      if((img=getObjImage(p_obj))!=null){
                i = p_obj.getX()+offset;
                if (i_startline>p_obj.getY()) j = i_startline - p_obj.getY() - p_obj.getHeight();
                   else j = i_startline - (p_obj.getY()-loc.getWayLength() * DriveOff_SB.VIRTUALCELL_HEIGHT) - p_obj.getHeight();
	        gBuffer.drawImage(img,i,j,0);
	      }
        }
        for(li=1;li<=sortHely;li++)
        {
              MoveObject p_obj = sortedObjects[sortedObjects.length-li];
	      if((img=getObjImage(p_obj))!=null){
                i = p_obj.getX()+offset;
                if (i_startline>p_obj.getY()) j = i_startline - p_obj.getY() - p_obj.getHeight();
                   else j = i_startline - (p_obj.getY()-loc.getWayLength() * DriveOff_SB.VIRTUALCELL_HEIGHT) - p_obj.getHeight();
	        gBuffer.drawImage(img,i,j,0);
	      }
        }



	    k = Math.max(iReady.getHeight(),iTool.getHeight());
	    i = baseY+scrheight -k-1;
	    j = baseX+2;
	    if(client_sb.isGrenadeReady())
	       gBuffer.drawImage(iReady,j,i+((k-iReady.getHeight())>>1),0);
            if(gameStatus==playGame) e = p_player.getCurrentDamage(); else e = 100;
	    if(e<90 || blink){
	      j+=iReady.getWidth()+2;
	      gBuffer.drawImage(iTool,j,i,0);
	      j+=iTool.getWidth()+2;
	      i = i+ ((iTool.getHeight()-DAMAGE_BAR_HEIGHT)>>1);
	      gBuffer.setColor(0x00000);
	      gBuffer.fillRect(j,i,DAMAGE_BAR_WIDTH+2,DAMAGE_BAR_HEIGHT);
	      gBuffer.setColor(0xffffff);
	      gBuffer.drawRect(j-1,i-1,DAMAGE_BAR_WIDTH+3,DAMAGE_BAR_HEIGHT+1);
	      if(e <= 100){
	        e = (DAMAGE_BAR_WIDTH*e/100);
	        gBuffer.fillRect(j+1+DAMAGE_BAR_WIDTH-e,i+1,e,DAMAGE_BAR_HEIGHT-2);
	      }
	    }

        if(gameStatus != Crashed || blink)
	 for (i=0;i<loc.getAttemptions();i++){
           gBuffer.drawImage(iHeart,baseX+scrwidth-(i+1)*(iHeart.getWidth()+2)-10,baseY+scrheight-iHeart.getHeight()-2,0);
	 }

	if (gameStatus == demoPlay && blink) {
	       gBuffer.drawImage(DemoTitle,scrwidth>>1,scrheight-DemoTitle.getHeight()-5,Graphics.HCENTER|Graphics.VCENTER);
	}
    }
    /**
     * Paint the contents of the Canvas.
     * The clip rectangle(not supported here :-( of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     * @param g Graphics context to paint to.
     */

    public boolean autosaveCheck(){
      return (gameStatus == playGame);
    }
    public void gameLoaded(){
       blink=false;
       direct=DriveOff_PMR.BUTTON_NONE;
       sortedObjects = new MoveObject[DriveOff_SB.ANIMATION_OBJECTS];
       stage = client_sb.getGameStateBlock().getStage();
       stageSleepTime[playGame]=client_sb.getTimeDelay();
       //CurrentLevel=level;
       //baseX=((scrwidth-levelImage.getWidth())>>1);
       //baseY=((scrheight-levelImage.getHeight())>>1);
       gameStatus=newStage;
       ticks=1;
       client_sb.nextGameStep();
       bufferChanged=true;
       drawBackScreen();
       repaint(0,0,scrwidth,scrheight);
    }

    public void OutOfResources(){}

    public boolean isLoadSaveSupporting(){
      return true;
    }

    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID){
      if(DriveOff_SB.GAMEACTION_PLAYEROFFROAD==actionID && ((startup)midlet)._Vibra) activateVibrator(50);
      else
      if(gsb!=null && ((startup)midlet)._Sound)
       switch (actionID){
	  case DriveOff_SB.GAMEACTION_EXPLODING:       gsb.playSound(SND_VZRYV_OTT, false); break;
	  case DriveOff_SB.GAMEACTION_PLAYERCOLLIDED:  gsb.playSound(SND_COLLIDE_OTT, false);break;
	  case DriveOff_SB.GAMEACTION_PLAYERKILLED:    break;
	  case DriveOff_SB.GAMEACTION_POLICE:          gsb.playSound(SND_SIRENA_OTT, false);break;
      }
    }


private static final byte IMG_ROCKETICO = (byte)5;
private static final byte IMG_LOST = (byte)2;
private static final byte IMG_WIN = (byte)1;
private static final byte IMG_FP = (byte)0;
private static final byte IMG_BENZOVOZ_D = (byte)14;
private static final byte IMG_BENZOVOZ = (byte)13;
private static final byte IMG_HEL_DESTR = (byte)22;
private static final byte IMG_BUS_D = (byte)12;
private static final byte IMG_BUS = (byte)11;
private static final byte IMG_HEL1 = (byte)20;
private static final byte IMG_HEL2 = (byte)21;
private static final byte IMG_HEL0 = (byte)19;
private static final byte IMG_DEMO = (byte)3;
private static final byte IMG_MOTO_D = (byte)18;
private static final byte IMG_PLAYERCAR_D = (byte)10;
private static final byte IMG_PLAYERCAR = (byte)9;
private static final byte IMG_CAR_D = (byte)16;
private static final byte IMG_POLICECAR1 = (byte)24;
private static final byte IMG_POLICECAR2 = (byte)25;
private static final byte IMG_POLICECAR0 = (byte)23;
private static final byte IMG_ROCKET = (byte)41;
private static final byte IMG_DAMAGEICO = (byte)4;
private static final byte IMG_EXPL1 = (byte)43;
private static final byte IMG_POLICECAR_D = (byte)26;
private static final byte IMG_MOTO = (byte)17;
private static final byte IMG_EXPL2 = (byte)44;
private static final byte IMG_CAR = (byte)15;
private static final byte IMG_EXPL0 = (byte)42;
private static final byte IMG_ATTEMPT = (byte)6;
private static final byte IMG_EXPL3 = (byte)45;
private static final byte IMG_MAN0_1 = (byte)28;
private static final byte IMG_MAN0_D0 = (byte)30;
private static final byte IMG_MAN0_D2 = (byte)32;
private static final byte IMG_MAN1_D0 = (byte)37;
private static final byte IMG_MAN1_D1 = (byte)38;
private static final byte IMG_MAN1_D2 = (byte)39;
private static final byte IMG_MAN0_0 = (byte)27;
private static final byte IMG_MAN0_2 = (byte)29;
private static final byte IMG_MAN0_D1 = (byte)31;
private static final byte IMG_MAN1_0 = (byte)33;
private static final byte IMG_MAN1_2 = (byte)35;
private static final byte IMG_MAN1_1 = (byte)34;
private static final byte IMG_MAN1_3 = (byte)36;
private static final byte IMG_OFFROAD = (byte)7;
private static final byte IMG_BORDER = (byte)8;
private static final byte IMG_GRENADE = (byte)40;
private static final byte TOTAL_IMAGES_NUMBER = (byte)46;

private static final byte SND_COLLIDE_OTT = (byte)0;
private static final byte SND_MAIN_OTT = (byte)1;
private static final byte SND_POHORONY_OTT = (byte)2;
private static final byte SND_SIRENA_OTT = (byte)3;
private static final byte SND_VZRYV_OTT = (byte)4;

}

