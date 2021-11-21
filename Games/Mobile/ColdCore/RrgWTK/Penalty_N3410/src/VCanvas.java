
import javax.microedition.lcdui.*;
import com.igormaznitsa.gameapi.*;
import com.igormaznitsa.game_kit_out_6BE902.Penalty.*;
import com.igormaznitsa.midp.*;
import com.igormaznitsa.midp.Utils.drawDigits;
import com.igormaznitsa.midp.Melody.*;

public class VCanvas extends com.igormaznitsa.midp.PhoneModels.Nokia.GameCanvas implements GamePlayerBlock, GameActionListener{


  Image []iStadium = new Image[2];
  Image []iGate = new Image[2];
  Image []iBall = new Image[3];
  Image []iSide = new Image[6];
  Image []iGoalSight = new Image[4];
  Image []GoalKeeper = new Image[12];
  Image []Player  = new Image[4];
  Image PlayerIco;
  Image PhoneIco;
  Image BallIco;


  protected Penalty_PMR client_pmr = null;
  protected Penalty_SB client_sb;
  private Image BackScreen;
  private Graphics gBackScreen;
  private drawDigits digits;
  private int Attemptions = 0;


  private GameSoundBlock gsb;
  private boolean isFirst = true;
  private int CrashDelay;


  /**Construct the displayable*/
  public VCanvas(com.igormaznitsa.gameapi.ControlMenuBlock m) {
    super(m);
    AMOUNT_OF_LOADED_RESOURCES = 67+40+3;
    client_sb = new Penalty_SB(scrwidth,scrheight,this,null,null/*this*/);
    client_sb.setPlayerBlock(this);
       try{
         gsb = new GameSoundBlock(this,"/res/sound.bin",this);
       }catch (Exception e){ gsb = null;}

  }

    /**
     * Get next player's move record for the strategic block
     * @param strategicBlock
     * @return
    */
    public PlayerMoveRecord getPlayerMoveRecord(GameStateBlock gameStateBlock){
      if(gameStatus==demoPlay)direct = Penalty_SB.p_rnd.getInt(6);
         client_pmr.setValue(direct);
	 direct = Penalty_PMR.BUTTON_NONE;

       return client_pmr;
    }

    /**
     * Initing of the player before starting of a new game session
     */
    public void initPlayer(){
        client_pmr = new Penalty_PMR();

    }

    public void prepareImages(ImageBlock pnl) throws NullPointerException {
           Title = pnl.getImageForID(IMG_FP,false);
//           levelImage=pnl.getImageForID(IMG_3,false);
           Lost = pnl.getImageForID(IMG_LOST,false);
           DemoTitle = pnl.getImageForID(IMG_DEMO,false);
           Won = pnl.getImageForID(IMG_WON, false);

	   iStadium  [0] = pnl.getImageForID(IMG_STADIUM0, false);
	   iStadium  [1] = pnl.getImageForID(IMG_STADIUM1, false);

	   iGate  [0] = pnl.getImageForID(IMG_GATENEAR, false);
	   iGate  [1] = pnl.getImageForID(IMG_GATEFAR, false);

	   iBall  [0] = pnl.getImageForID(IMG_BALL0, false);
	   iBall  [1] = pnl.getImageForID(IMG_BALL1, false);
	   iBall  [2] = pnl.getImageForID(IMG_BALL2, false);

	   iGoalSight [0] = pnl.getImageForID(IMG_NEARSIGHT, false);
	   iGoalSight [1] = pnl.getImageForID(IMG_NEARSIGHTSEL, false);
	   iGoalSight [2] = pnl.getImageForID(IMG_FARSIGHT, false);
	   iGoalSight [3] = pnl.getImageForID(IMG_FARSIGHTSEL, false);

	   iSide  [0] = pnl.getImageForID(IMG_SIDELEFT, false);
	   iSide  [1] = pnl.getImageForID(IMG_SIDELEFTSEL, false);
	   iSide  [2] = pnl.getImageForID(IMG_SIDECENTER, false);
	   iSide  [3] = pnl.getImageForID(IMG_SIDECENTERSEL, false);
	   iSide  [4] = pnl.getImageForID(IMG_SIDERIGHT, false);
	   iSide  [5] = pnl.getImageForID(IMG_SIDERIGHTSEL, false);

	   GoalKeeper[0] = pnl.getImageForID(IMG_GK_NEAR_STAND, false);
	   GoalKeeper[1] = pnl.getImageForID(IMG_GK_NEAR_TOP, false);
	   GoalKeeper[2] = pnl.getImageForID(IMG_GK_NEAR_LEFT, false);
	   GoalKeeper[3] = pnl.getImageForID(IMG_GK_NEAR_RIGHT, false);
	   GoalKeeper[4] = pnl.getImageForID(IMG_GK_NEAR_TOP_LEFT, false);
	   GoalKeeper[5] = pnl.getImageForID(IMG_GK_NEAR_TOP_RIGHT, false);

	   GoalKeeper[6] = pnl.getImageForID(IMG_GK_FAR_STAND, false);
	   GoalKeeper[7] = pnl.getImageForID(IMG_GK_FAR_TOP, false);
	   GoalKeeper[8] = pnl.getImageForID(IMG_GK_FAR_LEFT, false);
	   GoalKeeper[9] = pnl.getImageForID(IMG_GK_FAR_RIGHT, false);
	   GoalKeeper[10] = pnl.getImageForID(IMG_GK_FAR_LEFT_TOP, false);
	   GoalKeeper[11] = pnl.getImageForID(IMG_GK_FAR_RIGHT_TOP, false);

	   Player[0] = pnl.getImageForID(IMG_PLAYER0, false);
	   Player[1] = pnl.getImageForID(IMG_PLAYER1, false);
	   Player[2] = pnl.getImageForID(IMG_PLAYER2, false);
	   Player[3] = pnl.getImageForID(IMG_PLAYER3, false);

	   PlayerIco = pnl.getImageForID(IMG_PLAYERICO, false);;
	   PhoneIco = pnl.getImageForID(IMG_PHONEICO, false);
	   BallIco = pnl.getImageForID(IMG_BALLICO, false);
	   digits = new drawDigits(pnl.getImageForID(IMG_NFONT,false));
    }



///////////////////////////////////////////////////////////////////////////////////


    public void init(int level, String Picture) {
       blink=false;
       ticks = 0;
       direct=Penalty_PMR.BUTTON_NONE;
       CurrentLevel=level;
 //      stage = 0;
//       baseX=((scrwidth-levelImage.getWidth())>>1);
//       baseY=((scrheight-levelImage.getHeight())>>1);
	if (gameStatus==titleStatus) {
	    client_sb.newGameSession(0/*Penalty_SB.LEVEL_DEMO*/);
	    demoPosition=0;
	    demoLimit=demoPreviewDelay;
	    gameStatus=demoPlay;
	} else {
	   client_sb.newGameSession(CurrentLevel);
	   gameStatus=playGame;
	   stageSleepTime[playGame]=70;//client_sb.getTimeDelay();
	}
	bufferChanged=true;
	client_sb.nextGameStep();
	repaint(0,0,scrwidth, scrheight);
    }

    public int ParsePressedKey(int keyCode){
             switch (keyCode) {
	        case Canvas.KEY_NUM4: return Penalty_PMR.BUTTON_LEFT;
		case Canvas.KEY_NUM6: return Penalty_PMR.BUTTON_RIGHT;
		case Canvas.KEY_NUM2: return Penalty_PMR.BUTTON_UP;
		case Canvas.KEY_NUM8: return Penalty_PMR.BUTTON_DOWN;
		case Canvas.KEY_NUM5: return Penalty_PMR.BUTTON_FIRE;
             }
        return Penalty_PMR.BUTTON_NONE;
    }

    protected void keyReleased(int keyCode) {
       if(gameStatus == titleStatus){
	   isFirst=false;
	   this.stopAllMelodies();
       }
        if (gameStatus==playGame) {
	  direct = Penalty_PMR.BUTTON_NONE;
        }
    }

    public void prepareNextStage(){
/*
         direct=0;
	 client_sb.initStage(stage);
         client_sb.nextGameStep();
//	 DrawBackScreen();
	 Runtime.getRuntime().gc();
*/
    }

    public void showTitle(){
      if(isFirst && gsb!=null && ((startup)midlet)._Sound) gsb.playSound(SND_MAIN_OTT, false);
      isFirst = false;
    }
    private int loc_ticks = 0;
    public int demoPlayNextStep(){
			   Penalty_GSB loc=(Penalty_GSB)client_sb.getGameStateBlock();

			   if(loc.getPlayerState()!=Penalty_GSB.PLAYERSTATE_NORMAL){
			     if (loc_ticks<40)loc_ticks++;
			     else{
			       client_sb.resumeSession();
			       loc_ticks = 0;
			     }
			   }
			   else client_sb.nextGameStep();
			   return loc.getGameState();
    }

    public void playGameNextStep(){

			   client_sb.nextGameStep();

			   Penalty_GSB loc=(Penalty_GSB)client_sb.getGameStateBlock();
			   if(loc.getPlayerState() != Penalty_GSB.PLAYERSTATE_NORMAL){
			              ticks = 0;  blink = true;  gameStatus = Crashed;
			              _score = loc.getPlayerScore();
				      CrashDelay = 10;
				      if(gsb!=null && ((startup)midlet)._Sound)
				       if(client_sb.getBeatResult()== Penalty_SB.RESULT_GOAL)
				         if (client_sb.getMode() == Penalty_SB.MODE_GOALKEEPER){
				            CrashDelay = 10;
					    gsb.playSound(SND_MIMO_OTT, false);
				         } else {
				            CrashDelay = 10;
					    gsb.playSound(SND_GOAL_OTT, false);
				         }

			   }
    }

    public void Crashed(){
			   ticks++; blink=(ticks&1)==0;
			   if (ticks>10){
			         direct = 0;
			         ticks = 0;
				 blink = true;
				 stopAllMelodies();
				 Penalty_GSB loc=(Penalty_GSB)client_sb.getGameStateBlock();
				 if(loc.getGameState()==GameStateBlock.GAMESTATE_OVER){
			            if(loc.getPlayerState() == Penalty_GSB.PLAYERSTATE_WON)
				     gameStatus = Finished;
				    else
				    gameStatus = gameOver;
				 } else {
				    gameStatus = playGame;
				    client_sb.resumeSession();
				 }
			   }
    }






    public void DoubleBuffer(Graphics gBuffer){
	int i,j,k,e;

        gBuffer.setColor(0xffffff);
        gBuffer.fillRect(0, 0, scrwidth,scrheight);

        Penalty_GSB loc=(Penalty_GSB)client_sb.getGameStateBlock();
	Image img=null;




        // Draw gate
        Gate p_gate = client_sb.getGate();
        Ball p_ball = client_sb.getBall();
        GoalkeeperObj p_gk = client_sb.getGoalkeeperObj();
        GuideSign[] ap_guide = client_sb.getGuideArray();
	int result = client_sb.getBeatResult();

//        gBuffer.setColor(Color.blue);


        if(gameStatus == Crashed && result == Penalty_SB.RESULT_GOAL)
           gBuffer.drawImage(iStadium[blink?1:0],(scrwidth>>1),0,Graphics.HCENTER|Graphics.TOP);
	  else
             gBuffer.drawImage(iStadium[0],scrwidth>>1,0,Graphics.HCENTER|Graphics.TOP);
        if (client_sb.getMode() == Penalty_SB.MODE_GOALKEEPER)
        {
            PlayerObj p_pl = client_sb.getPlayerObj();
            gBuffer.drawImage(Player[p_pl.getFrame()],p_pl.getScrX()-1,p_pl.getScrY()-1,0);

	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_NOHIT)
              gBuffer.drawImage(iBall[p_ball.getFrame()],p_ball.getScrX()-1,p_ball.getScrY()-1,0);

	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_GOALKEEPER)
              gBuffer.drawImage(GoalKeeper[p_gk.getState()],p_gk.getScrX()-1,p_gk.getScrY()-1,0);

	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_GATE)
              gBuffer.drawImage(iGate[0],  p_gate.getScrX()-1, p_gate.getScrY()-1,0);

        }
        else
        {
	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_GATE)
              gBuffer.drawImage(iGate[1],  p_gate.getScrX()-1, p_gate.getScrY()-1,0);
	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_GOALKEEPER)
              gBuffer.drawImage(GoalKeeper[p_gk.getState()+6],p_gk.getScrX()-1,p_gk.getScrY()-1,0);
	    if(gameStatus!=Crashed || !blink || result!=Penalty_SB.RESULT_NOHIT)
              gBuffer.drawImage(iBall[p_ball.getFrame()],p_ball.getScrX()-1,p_ball.getScrY()-1,0);
        }


        for (int li = 0; li < ap_guide.length; li++)
        {
            GuideSign p_obj = ap_guide[li];
            if (!p_obj.isActive()) continue;

	    i = (p_obj.isSelected()?blink?1:0:0);

            switch (p_obj.getType())
            {
                case GuideSign.TYPE_GATESIGHT:
                    {
		       gBuffer.drawImage(iGoalSight[(client_sb.getMode() == Penalty_SB.MODE_GOALKEEPER?0:2)+i],p_obj.getScrX(), p_obj.getScrY(),0);
                    }
                    ;
                    break;
                case GuideSign.TYPE_SIDEARROWCENTER:
                case GuideSign.TYPE_SIDEARROWLEFT:
                case GuideSign.TYPE_SIDEARROWRIGHT:
		         gBuffer.drawImage(iSide[(p_obj.getType()<<1)+i],p_obj.getScrX(), p_obj.getScrY(),0); break;


            }
        }

        if(loc.getGameState()!=Penalty_GSB.GAMESTATE_OVER)
	{
	  j = 5-client_sb.getAttemptions();
	  i = baseX+((scrwidth-(BallIco.getWidth()+1)*j)>>1);
	  for (k=0;k<j;k++){
               gBuffer.drawImage(BallIco,i,baseY,0);
	       i+=BallIco.getWidth()+1;
	  }
        }

        i = scrwidth-PhoneIco.getWidth()-1;
	gBuffer.drawImage(PhoneIco,i,0,0);
        digits.drawDigits(gBuffer,i-digits.fontWidth-digits.fontWidth-1,(PhoneIco.getHeight()-digits.fontWidth)>>1,2,loc.getAIScore());

	gBuffer.drawImage(PlayerIco,1,0,0);
        digits.drawDigits(gBuffer,PlayerIco.getWidth()+2,(PlayerIco.getHeight()-digits.fontWidth)>>1,2,client_sb.getPlayerScore());


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
       direct=Penalty_PMR.BUTTON_NONE;
//       stage = client_sb.getGameStateBlock().getStage();
//       stageSleepTime[playGame]=client_sb.getTimeDelay();
       //CurrentLevel=level;
//       baseX=((scrwidth-levelImage.getWidth())>>1);
//       baseY=((scrheight-levelImage.getHeight())>>1);
       gameStatus=playGame;
       bufferChanged=true;
       stageSleepTime[playGame]=70;
       repaint(0,0,scrwidth, scrheight);
    }

    public void OutOfResources(){}

    public boolean isLoadSaveSupporting(){
      return true;
    }


    public void gameAction(int actionID,int param0){}
    public void gameAction(int actionID,int param0,int param1){}
    public void gameAction(int actionID,int param0,int param1,int param2){}
    public void gameAction(int actionID){
      if(gsb!=null && ((startup)midlet)._Sound){
/*
       switch (actionID){
	  case Penalty_SB.GAMEACTION_SOUND_HIT:        gsb.playSound(SND_HIT_OTT, false); break;
	  case Penalty_SB.GAMEACTION_SOUND_KRYA_KRYA:  gsb.playSound(SND_KRYAKRYA_OTT, false);break;
	  case Penalty_SB.GAMEACTION_SOUND_NOHIT:      gsb.playSound(SND_NOHIT_OTT, false);break;
	  case Penalty_SB.GAMEACTION_SOUND_SHOOT:      gsb.playSound(SND_SHOT_OTT, false);
							if(((startup)midlet)._Vibra){activateVibrator(300); System.out.println("VV");}
							break;
       }
*/
      }
    }

private static final byte IMG_BALL0 = (byte)0;
private static final byte IMG_BALL1 = (byte)1;
private static final byte IMG_BALL2 = (byte)2;
private static final byte IMG_BALLICO = (byte)3;
private static final byte IMG_DEMO = (byte)4;
private static final byte IMG_FARSIGHT = (byte)5;
private static final byte IMG_FARSIGHTSEL = (byte)6;
private static final byte IMG_FP = (byte)7;
private static final byte IMG_GATEFAR = (byte)8;
private static final byte IMG_GATENEAR = (byte)9;
private static final byte IMG_GK_FAR_LEFT = (byte)10;
private static final byte IMG_GK_FAR_LEFT_TOP = (byte)11;
private static final byte IMG_GK_FAR_RIGHT = (byte)12;
private static final byte IMG_GK_FAR_RIGHT_TOP = (byte)13;
private static final byte IMG_GK_FAR_STAND = (byte)14;
private static final byte IMG_GK_FAR_TOP = (byte)15;
private static final byte IMG_GK_NEAR_LEFT = (byte)16;
private static final byte IMG_GK_NEAR_RIGHT = (byte)17;
private static final byte IMG_GK_NEAR_STAND = (byte)18;
private static final byte IMG_GK_NEAR_TOP = (byte)19;
private static final byte IMG_GK_NEAR_TOP_LEFT = (byte)20;
private static final byte IMG_GK_NEAR_TOP_RIGHT = (byte)21;
private static final byte IMG_LOST = (byte)22;
private static final byte IMG_NEARSIGHT = (byte)23;
private static final byte IMG_NEARSIGHTSEL = (byte)24;
private static final byte IMG_NFONT = (byte)25;
private static final byte IMG_PHONEICO = (byte)26;
private static final byte IMG_PLAYER0 = (byte)27;
private static final byte IMG_PLAYER1 = (byte)28;
private static final byte IMG_PLAYER2 = (byte)29;
private static final byte IMG_PLAYER3 = (byte)30;
private static final byte IMG_PLAYERICO = (byte)31;
private static final byte IMG_SIDECENTER = (byte)32;
private static final byte IMG_SIDECENTERSEL = (byte)33;
private static final byte IMG_SIDELEFT = (byte)34;
private static final byte IMG_SIDELEFTSEL = (byte)35;
private static final byte IMG_SIDERIGHT = (byte)36;
private static final byte IMG_SIDERIGHTSEL = (byte)37;
private static final byte IMG_STADIUM0 = (byte)38;
private static final byte IMG_STADIUM1 = (byte)39;
private static final byte IMG_WON = (byte)40;

private static final byte SND_GOAL_OTT = (byte)0;
private static final byte SND_MAIN_OTT = (byte)1;
private static final byte SND_MIMO_OTT = (byte)2;

}

