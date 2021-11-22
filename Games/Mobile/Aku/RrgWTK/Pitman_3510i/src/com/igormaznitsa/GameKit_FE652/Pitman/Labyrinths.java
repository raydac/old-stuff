package com.igormaznitsa.GameKit_FE652.Pitman;

public class Labyrinths
{
    public static final byte ELEMENT_EMPTY = 0;
    public static final byte ELEMENT_TITANIUMWALL = 1;
    public static final byte ELEMENT_DIRT = 2;
    public static final byte ELEMENT_FIREFLY = 3;
    public static final byte ELEMENT_BUTTERFLY = 4;
    public static final byte ELEMENT_STONE = 5;
    public static final byte ELEMENT_DIAMOND = 6;
    public static final byte ELEMENT_PLAYER = 7;
    public static final byte DEMO_LEVEL = 11;
    public static final byte[] DEMO_STEP={Pitman_SB.MOVE_RIGHT,Pitman_SB.MOVE_RIGHT,Pitman_SB.MOVE_RIGHT,
                                          Pitman_SB.MOVE_LEFT,
				          Pitman_SB.MOVE_RIGHT,  Pitman_SB.MOVE_RIGHT,
				          Pitman_SB.MOVE_UP,
				          Pitman_SB.MOVE_RIGHT,Pitman_SB.MOVE_RIGHT,
				          Pitman_SB.MOVE_DOWN,Pitman_SB.MOVE_DOWN,Pitman_SB.MOVE_DOWN};

    //==========
    public static final byte ELEMENT_INVISIBLE = 9;
    //==========

    public static final int LAB_WIDTH = 15;
    public static final int LAB_HEIGHT = 15;
public static final int LAB_NUMBER = 11;
    public static final int [] indexes = {
              225, 983265, 1966305, 2949345, 3932385, 4915425, 5898465, 6881505, 7864545, 8847585, 9830625, 10813665
};

 private static final long [] storage = {
1229782938247303441l, 2383852406294323746l, 2815139140895646242l, 5918035734088262230l, 6147995554491277861l, 2459564708316979750l, 2460410229488509474l, 2460762415527519781l, 2747853426965750354l, 6220071565331145250l, 2747796252645216802l, 2459569175079752226l, 7378622569490026786l, 1229782938247303442l, -15l,
1229782938247303441l, 76561203972931584l, 4791672246509574l, 18313465708225024l, 2459547038264743200l, 2459635077007693090l, 2477580201989657941l, 2459570269977731618l, 2474009060952056354l, 2689249457472676389l, 160559096354l, 2305843018426552866l, 7277817011288281381l, 1229782938247303442l, -15l,
1229782938247303441l, 6994090221306392576l, 2454849238943686752l, 6134204105266438693l, 2474183900769953314l, 7072165534109082146l, 5918383093858849314l, 2690097382621848917l, 5918383179477164578l, 2689249458277983573l, 2459622170629841490l, 70229l, 6944656592449507584l, 1229782938247303440l, -15l,
1229782938247303441l, 5841168739606025557l, 6129680569259677013l, 7300634063971640662l, 6220897151982118229l, 2766706675250713173l, 2460467402492617298l, 2460466646886011426l, 2459565893392623138l, 2478776543637480018l, 5919531057011577381l, 7372993069955749157l, 2689249457490825510l, 1229782938247303445l, -15l,
1229782938247303441l, 8147612205913876002l, 6129722770749284898l, 2477281208646640213l, 6135610381497095509l, 6148012803133625685l, 6148911099789337941l, 7373842163251439206l, 6153696247118177621l, 7301853558982451797l, 2459570548847485477l, 7359482271037986130l, 5918405157106749781l, 1229782938247303445l, -15l,
1229782938247303441l, 6994090221306381568l, 5913620375704856166l, 31830128884798805l, 2459547476852473856l, 2477579106772984354l, 2459565803480425061l, 2305843219971319074l, 144115201250164736l, 9007200078135296l, 562949954551808l, 35184372158464l, 2199023259904l, 1229782938247303440l, -15l,
1229782938247303441l, 2384205642729940583l, 437242056009580544l, 153427318555017216l, 153704395485216768l, 154566137723486208l, 154848695031955456l, 154567562578886656l, 2459565876212400128l, 72567785398818l, 2305843146653773826l, 162129595178487808l, 7071256292972695808l, 1229782938247303442l, -15l,
1229782938247303441l, 76561193665298432l, 4785074604081152l, 2306142086398039381l, 7278680115890181461l, 6205961455861651030l, 7377462511138072166l, 7377494328526136934l, 2819906489810952806l, 2459565867349852754l, 1114146l, 69632l, 2748922152620265728l, 1229782938247303442l, -15l,
1229782938247303441l, 2383905402754782549l, 4822604600975906l, 299067971219456l, 18691697672192l, 1168231104512l, 2305843082228138530l, 2449958201852952615l, 285212674l, 17825792l, 18085042132680704l, 2459565876498731008l, 7072378086953718050l, 1229782938247303446l, -15l,
1229782938247303441l, 2383004682829308514l, 6201775985065861670l, 6147715197081835110l, 5787707310282724693l, 6148857228063408213l, 5918383093858697221l, 5774218496214979925l, 6148013971025843536l, 384213343228011781l, 2747793907022635605l, 2459566210932610594l, 6153418290880647458l, 1229782938247303445l, -15l,
1229782938247303441l, 2382404615199129606l, 148900271269879808l, 2459264464243917376l, 2305862044542304806l, 2666132156800193058l, 5917729983382561319l, 2459528351061135906l, 2305878211050866258l, 3891116821701599778l, 9077705438986244l, 600479377723392l, 37529961304320l, 1229782938247303440l, -15l,
0l, 0l, 0l, 1152921504606846976l, 72057594324259089l, 4503599646580736l, 6949335700010574432l, 2459545938683831590l, 5919173569132107298l, 1229782933666005346l, 17l, 0l, 0l, 0l, -16l,
};

	 public final static byte [] getLabyrinth(int id)
	 {
	   id=indexes[id];
	   try {
 	    byte[] img = new byte[id&0xffff];
	    id>>>=16;
	    int pos = 0;
	    long n=0;
	    while (pos < img.length) {
	      if ((pos&15)==0)
	      /* if (pos == 0) n = storage[0];
	         else */ n=storage[id+(pos>>4)];
	      img[pos++]=(byte)(n&15);
	      n>>>=4;
	    }
               return img;
	   } catch (Exception e) {return null;}
	 }


}